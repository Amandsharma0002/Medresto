package com.gla.medresto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {
    Button logoutBtn;
    FirebaseAuth firebaseAuth;
    SharedPreferences sp;
    ImageView signOutButton;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        signOutButton = (ImageView) findViewById(R.id.signOutBtn);
        sp = getSharedPreferences("login", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        googleSignInClient = GoogleSignIn.getClient(HomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getString("type", "").equals("googleLogin")) {
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sp.edit().putBoolean("loggedIn", false).apply();
                                sp.edit().putString("type", null).apply();
                                sp.edit().putString("gmail", null).apply();
                                firebaseAuth.signOut();
                                Toast.makeText(HomeActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(HomeActivity.this, RegisterActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                } else if (sp.getString("type", "").equals("phoneLogin")) {
                    sp.edit().putBoolean("loggedIn", false).apply();
                    sp.edit().putString("type", null).apply();
                    sp.edit().putString("number", null).apply();
                    firebaseAuth.signOut();
                    Toast.makeText(HomeActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(HomeActivity.this, RegisterActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(HomeActivity.this, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}