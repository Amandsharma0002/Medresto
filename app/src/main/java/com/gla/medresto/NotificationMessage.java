package com.gla.medresto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//this class creates the Reminder Notification Message

public class NotificationMessage extends AppCompatActivity {
    TextView textView;
    Button b1;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);
        textView = findViewById(R.id.tv_message);
        Bundle bundle = getIntent().getExtras();                                                  //call the data which is passed by another intent
        textView.setText(bundle.getString("message"));
        sp = getSharedPreferences("login", MODE_PRIVATE);
        String st = sp.getString("guardianNumber", null);
        b1 = findViewById(R.id.flashButton);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = st;
                String msg = "Medicine Taken";
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, msg, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Some fiels is Empty", Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(NotificationMessage.this, HomeActivity.class));
                finish();
            }
        });
    }

}

