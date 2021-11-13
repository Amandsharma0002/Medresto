package com.gla.medresto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splashscreen extends AppCompatActivity {

    private static int DELAY_TIME = 4000;

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView app_name;
    private Handler mHandler = new Handler();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        imageView = findViewById(R.id.imageView2);
        app_name = findViewById(R.id.app_name);

        imageView.setAnimation(topAnim);
        app_name.setAnimation(bottomAnim);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Intent i = new Intent(Splashscreen.this,MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//        },DELAY_TIME);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (sp.getBoolean("loggedIn", false)) {
                    intent = new Intent(Splashscreen.this, HomeActivity.class);
                } else {
                    intent = new Intent(Splashscreen.this, RegisterActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, DELAY_TIME);


    }
}