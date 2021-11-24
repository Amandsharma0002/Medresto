package com.gla.medresto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    SharedPreferences sp;
    ImageView signOutButton;

    GoogleSignInClient googleSignInClient;

    FloatingActionButton mCreateRem;
    RecyclerView mRecyclerview;
    ArrayList<Model> dataholder = new ArrayList<Model>();                                               //Array list to add reminders and display in recyclerview
    MyAdapter adapter;

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
                                new DatabaseManager(getApplicationContext()).flushDatabase();
                                sp.edit().putBoolean("loggedIn", false).apply();
                                sp.edit().putString("type", null).apply();
                                sp.edit().putString("gmail", null).apply();
                                sp.edit().putString("guardianNumber", null).apply();
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
                    sp.edit().putString("guardianNumber", null).apply();
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

        mCreateRem = (FloatingActionButton) findViewById(R.id.create_reminder);                     //Floating action button to change activity
        mCreateRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                startActivity(intent);                                                              //Starts the new activity to add Reminders
            }
        });




        // List The medicine Reminders
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        Cursor cursor = new DatabaseManager(this).readAllReminders();                  //Cursor To Load data From the database
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Model model = new Model(cursor.getString(1), cursor.getString(2), cursor.getString(3));
                dataholder.add(model);
            }
        }
        adapter = new MyAdapter(this, dataholder, mRecyclerview);
        mRecyclerview.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        finish();                                                                                   //Makes the user to exit form the app
        super.onBackPressed();
    }
}