package com.chari6268.mycontacts.Task2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.chari6268.mycontacts.R;

public class MainActivity extends AppCompatActivity {

    Button healthcareBtn, fireSafetyBtn, accidentBtn, policeBtn, sosBtn, locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);

        healthcareBtn = findViewById(R.id.healthcare_button);
        fireSafetyBtn = findViewById(R.id.firesafety_button);
        accidentBtn = findViewById(R.id.accident_button);
        policeBtn = findViewById(R.id.police_button);
        sosBtn = findViewById(R.id.sos_button);
        locationBtn = findViewById(R.id.location_button);

        healthcareBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HealthCareActivity.class)));
        fireSafetyBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FireSafetyActivity.class)));
        accidentBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AccidentActivity.class)));
        policeBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PoliceActivity.class)));
        sosBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SOSActivity.class)));
        locationBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LocationActivity.class)));
    }


}
