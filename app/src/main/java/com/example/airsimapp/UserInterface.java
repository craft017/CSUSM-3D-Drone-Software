package com.example.airsimapp;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UserInterface extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Set the ModeSelectionFragment as the initial fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ModeSelectionFragment())
                    .commit();
        }

}}
