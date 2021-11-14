package com.gla.medresto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button b1;
    FirebaseAuth firebaseAuth;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        b1=(Button) findViewById(R.id.button);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  i =new Intent(HomeActivity.this,RegisterActivity.class);
//                firebaseAuth.signOut();
                sp.edit().putBoolean("loggedIn",false).apply();
                sp.edit().putString("type", null).apply();
                sp.edit().putString("number",null).apply();
                startActivity(i);
                finish();
            }
        });
    }
}