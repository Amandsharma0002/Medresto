package com.gla.medresto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (sp.getBoolean("loggedIn", false)) {
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, RegisterActivity.class);
                }
                startActivity(intent);
            }
        }, 2000);
    }

}