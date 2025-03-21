package com.chari6268.mycontacts.Task2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chari6268.mycontacts.R;

public class MainScreen extends AppCompatActivity {
    Button locationButton,alarmButton;
    private static final int SMS_PERMISSION_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFunction();
    }

    private void initFunction() {
        locationButton = findViewById(R.id.locate_me_button);
        alarmButton = findViewById(R.id.alarm_btn);
        locationButton.setOnClickListener(v -> {
            startActivity(new Intent(MainScreen.this, LocationActivity.class));
        });
        alarmButton.setOnClickListener(v -> {
//            sendSms("9440226858", "Hello, this is a test message!");
            if (ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS automatically
                sendSms("9440226858", "Hello,Rajeev");  // Replace with the correct number and message
            } else {
                // Request permission if not granted
                ActivityCompat.requestPermissions(MainScreen.this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            }
        });
    }
    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();  // Get the default SmsManager instance
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);  // Send SMS without user intervention
            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error sending SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                sendSms("9440226858", "Hello, Rajeev!");
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied! Unable to send SMS.", Toast.LENGTH_LONG).show();
            }
        }
    }
}