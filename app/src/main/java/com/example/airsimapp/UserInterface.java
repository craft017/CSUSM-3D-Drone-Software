package com.example.airsimapp;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserInterface extends AppCompatActivity {
    private static final String TAG = "UserInterface";
    private Button start, forward, backward, left, right, takeoff, land;
    private TextView output;
    private Orchestrator orchestrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        start = findViewById(R.id.start);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        takeoff = findViewById(R.id.takeoff_button);
        land = findViewById(R.id.land_button);
        output = findViewById(R.id.output);

        // Initialize Orchestrator
        flightController flightController = new flightController(output);
        orchestrator = new Orchestrator(flightController);

        // Set up listeners
        start.setOnClickListener(v -> orchestrator.connectToWebSocket());
        takeoff.setOnClickListener(v -> orchestrator.processCommand("takeoff", this::sendCommand));
        land.setOnClickListener(v -> orchestrator.processCommand("land", this::sendCommand));
        setMovementListener(forward, "forward");
        setMovementListener(backward, "backward");
        setMovementListener(left, "left");
        setMovementListener(right, "right");
    }

    private void setMovementListener(Button button, String action) {
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    orchestrator.processCommand(action, this::sendCommand);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    orchestrator.processCommand("stop", this::sendCommand);
                    break;
            }
            return true;
        });
    }

    private void sendCommand(String command) {
        Log.d(TAG, "Sending command: " + command);
    }
}