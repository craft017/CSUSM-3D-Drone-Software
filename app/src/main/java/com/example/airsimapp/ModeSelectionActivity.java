package com.example.airsimapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ModeSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);

        Button controllerButton = findViewById(R.id.controller_mode);
        Button controlBoardButton = findViewById(R.id.control_board_mode);

        controllerButton.setOnClickListener(v -> openMainActivity("controller"));
        controlBoardButton.setOnClickListener(v -> openMainActivity("control_board"));
    }

    private void openMainActivity(String mode) {
        Intent intent = new Intent(this, UserInterface.class);
        intent.putExtra("MODE", mode);
        startActivity(intent);
        finish(); // Close mode selection after choice
    }
}

