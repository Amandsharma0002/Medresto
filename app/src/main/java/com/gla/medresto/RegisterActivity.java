package com.gla.medresto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private EditText phoneNumber;
    private EditText otp;
    Button getOtp;
    Button register;
    ImageView googleSignIn;
    SharedPreferences sp;
    String verificationCodeBySystem;
    private static boolean otpRequested = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        phoneNumber = findViewById(R.id.inputNumber);
        otp = findViewById(R.id.inputOTP);
        getOtp = findViewById(R.id.getOTPButton);
        register = findViewById(R.id.registerButton);

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Invalid Phone Number\nPlease Check your Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                otpRequested = true;
                sendOtpToUser(phoneNumber.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!otpRequested) {
                    Toast.makeText(RegisterActivity.this, "Please request for OTP first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = otp.getText().toString();

                if (code.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Otp", Toast.LENGTH_SHORT).show();
                    return;
                } else if (code.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Wrong OTP...", Toast.LENGTH_SHORT).show();
                    otp.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

        googleSignIn = (ImageView) findViewById(R.id.googleRegister);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Method to login by Google.
                Toast.makeText(RegisterActivity.this, "Clicked...", Toast.LENGTH_SHORT).show();
            }
        });
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

                            Toast.makeText(RegisterActivity.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();
                            sp.edit().putBoolean("loggedIn",true).apply();
                            sp.edit().putString("type", "phoneLogin").apply();
                            sp.edit().putString("number", phoneNumber.getText().toString()).apply();
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


}