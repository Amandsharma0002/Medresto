package com.gla.medresto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private static boolean otpRequested = false;
    private static boolean permitAllowed = false;
    Button getOtp;
    Button register;
    SharedPreferences sp;
    String verificationCodeBySystem;
    ImageView signInButton;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    EditText guardian;
    String st;
    private EditText phoneNumber;
    private EditText otp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        runtimePermission();

        sp = getSharedPreferences("login", MODE_PRIVATE);

        phoneNumber = findViewById(R.id.inputNumber);
        otp = findViewById(R.id.inputOTP);
        getOtp = findViewById(R.id.getOTPButton);
        register = findViewById(R.id.registerButton);
        guardian = findViewById(R.id.inputSecoundaryNumber);


        signInButton = (ImageView) findViewById(R.id.googleRegister);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("413645942786-b8caijhi98b0nu2vubbvgcvfru7b2fl0.apps.googleusercontent.com").requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(RegisterActivity.this, googleSignInOptions);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!permitAllowed) {
                    Toast.makeText(RegisterActivity.this, "Please Allow asked Permissions.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (guardian.getText().toString().length() == 0) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Guardian Number first!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (guardian.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Invalid Guardian Number, Please Check your Guardian Number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = googleSignInClient.getSignInIntent();
                startActivityForResult(i, 100);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent i = new Intent(RegisterActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }


        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Invalid Phone Number, Please Check your Phone Number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                otpRequested = true;
                sendOtpToUser(phoneNumber.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!permitAllowed) {
                    Toast.makeText(RegisterActivity.this, "Please Allow asked Permissions.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (guardian.getText().toString().length() == 0) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Guardian Number first!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (guardian.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Invalid Guardian Number, Please Check your Guardian Number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!otpRequested) {
                    Toast.makeText(RegisterActivity.this, "Please request for OTP first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = otp.getText().toString();
                st = guardian.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                    return;
                } else if (code.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Wrong OTP...", Toast.LENGTH_SHORT).show();
                    otp.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    st = guardian.getText().toString();
                                    sp.edit().putBoolean("loggedIn", true).apply();
                                    sp.edit().putString("type", "googleLogin").apply();
                                    sp.edit().putString("gmail", googleSignInAccount.getEmail()).apply();
                                    sp.edit().putString("guardianNumber", guardian.getText().toString()).apply();
                                    Toast.makeText(RegisterActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent j = new Intent(RegisterActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(j);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Database Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendOtpToUser(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            sp.edit().putBoolean("loggedIn", true).apply();
                            sp.edit().putString("type", "phoneLogin").apply();
                            sp.edit().putString("number", phoneNumber.getText().toString()).apply();
                            sp.edit().putString("guardianNumber", guardian.getText().toString()).apply();
                            //Perform Your required action here to either let the user sign In or do something required
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        permitAllowed = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(RegisterActivity.this, "You need to Allow Permission in order to use the app.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


}