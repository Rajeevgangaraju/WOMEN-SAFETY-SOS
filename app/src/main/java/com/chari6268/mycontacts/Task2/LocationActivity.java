package com.chari6268.mycontacts.Task2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.chari6268.mycontacts.R;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;
    private static final long UPDATE_INTERVAL = 10000; // 10 seconds
    private static final long FASTEST_INTERVAL = 5000; // 5 seconds

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Button locateButton, saveButton, alarmButton;
    private TextView locationTextView;
    private Location currentLocation;
    private static final int SMS_PERMISSION_CODE = 101;
    String message = "";
    String[] userPhoneNumbers = {
            "9440226858",
            "9391954213",
            "9866684845",
            "8019735081"
    };
// Add more phone numbers as needed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initialization();
        saveButton.setEnabled(false);

        locateButton.setOnClickListener(v -> checkLocationSettingsAndProceed());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(LocationActivity.this, "Could not get location", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationResult.getLastLocation();
                currentLocation = location;
                String locationText = "Latitude: " + location.getLatitude() +
                        "\nLongitude: " + location.getLongitude();
                message = locationText;
                locationTextView.setText(locationText);
                saveButton.setEnabled(true);
                alarmButton.setEnabled(true);
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };

        alarmButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                for(int i =0;i<userPhoneNumbers.length;i++){
                    sendSms(userPhoneNumbers[i], "There is an Emergency \n Iam at This Location \n"+message);  // Replace with the correct number and message
                }
//                sendSms("9440226858", "Hello,Rajeev \n Iam at Location \n"+message);  // Replace with the correct number and message
            } else {
                // Request permission if not granted
                ActivityCompat.requestPermissions(LocationActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            }
        });
    }

//    private void sendSms(String phoneNumber, String message) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();  // Get the default SmsManager instance
//            smsManager.sendTextMessage(phoneNumber, null, message, null, null);  // Send SMS without user intervention
//            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "Error sending SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
private void sendSms(String phoneNumber, String message) {
    try {
        SmsManager smsManager = SmsManager.getDefault();  // Get the default SmsManager instance

        // If the currentLocation is available, include the Google Maps link
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            String mapLink = "https://www.google.com/maps?q=" + latitude + "," + longitude;

            // Append the Google Maps link to the message
            message += "\n" + mapLink;
        }
        // Send the SMS
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
        Toast.makeText(this, "Error sending SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for(int i =0;i<userPhoneNumbers.length;i++){
                    sendSms(userPhoneNumbers[i], "There is an Emergency \n Iam at This Location \n"+message);  // Replace with the correct number and message
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied! Unable to send SMS.", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initialization() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationTextView = findViewById(R.id.location_text);
        locateButton = findViewById(R.id.locate_button);
        saveButton = findViewById(R.id.save_button);
        alarmButton = findViewById(R.id.alertButton);
    }

    private void checkLocationSettingsAndProceed() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(this, locationSettingsResponse -> requestLocationPermissionAndGetLocation())
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Toast.makeText(LocationActivity.this, "Error opening location settings dialog", Toast.LENGTH_SHORT).show();
                        }
                    } else if (statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(LocationActivity.this, "Unable to turn on location. Please enable location in settings.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                requestLocationPermissionAndGetLocation();
            } else {
                Toast.makeText(this, "Location services must be enabled to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Toast.makeText(this, "Getting location...", Toast.LENGTH_SHORT).show();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
