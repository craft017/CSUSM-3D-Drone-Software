package com.example.airsimapp.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airsimapp.R;

public class MainActivity extends AppCompatActivity {
    // This is where the user starts in the app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //output = findViewById(R.id.output);

        Button userPhoneButton = findViewById(R.id.selectUserButton);
        Button dronePhoneButton = findViewById(R.id.selectDroneButton);

        // User activity will open if user button is clicked
        userPhoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        });
        // Drone activity will open if drone button is clicked
        dronePhoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DroneActivity.class);
            startActivity(intent);
        });

}}
