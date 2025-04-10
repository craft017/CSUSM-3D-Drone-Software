package com.example.airsimapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.airsimapp.Activities.UserActivity;
import com.example.airsimapp.GPS;
import com.example.airsimapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AutopilotFragment extends Fragment {
    private static final String TAG = "AutopilotFragment";
    private TextView speedTextView;
    private TextView headingTextView;
    private TextView gpsTextView;
    private double currentSpeed = 0.0;
    private float currentHeading = 0.0F;
    private Runnable uiUpdateRunnable;
    public GPS tempGPS;
    private Handler uiUpdateHandler = new Handler(Looper.getMainLooper());
    private static final long UPDATE_INTERVAL = 1000;
    public Date date = Calendar.getInstance().getTime();
    public Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autopilot, container, false);

        // Get the button from the layout
        Button manualButton = view.findViewById(R.id.manualButton);
        EditText latitude = view.findViewById(R.id.gpsCord);
        EditText longitude = view.findViewById(R.id.gpsCord2);
        EditText altitude = view.findViewById(R.id.gpsCord3);
        EditText gpsTime = view.findViewById(R.id.gpsTime);
        EditText heading = view.findViewById(R.id.heading);
        EditText speed = view.findViewById(R.id.Speed);
        EditText headingTime = view.findViewById(R.id.headingTime);
        EditText patternTime = view.findViewById(R.id.patternTime);
        Button addGPS = view.findViewById(R.id.addGPS);
        Button addHeadingSpeed = view.findViewById(R.id.addHeadingSpeed);
        Button addPattern = view.findViewById(R.id.addPattern);
        addHeadingSpeed.setOnClickListener(v -> {
            if (heading.getText().toString().isEmpty() || speed.getText().toString().isEmpty() || headingTime.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                UserActivity.getOrchestrator().getAutopilot().addToCommandQueue(heading.getText().toString().trim(), speed.getText().toString().trim(), headingTime.getText().toString().trim());
                heading.setText("");
                speed.setText("");
                headingTime.setText("");
            }

            // For loop is for testing
            for (int i = 0; i < UserActivity.getOrchestrator().getAutopilot().getCommandQueue().size(); i++) {
                //Log.e(TAG, UserActivity.getOrchestrator().getAutopilot().getCommandQueue().get(i).toString());
                Log.e(TAG, UserActivity.getOrchestrator().getAutopilot().getCommandQueue().get(i).getId());
            }
        });
        addGPS.setOnClickListener(v -> {
            if (latitude.getText().toString().isEmpty() || longitude.getText().toString().isEmpty() || altitude.getText().toString().isEmpty() || gpsTime.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                UserActivity.getOrchestrator().getAutopilot().addToCommandQueue(latitude.getText().toString().trim(), longitude.getText().toString().trim(), altitude.getText().toString().trim(), gpsTime.getText().toString().trim());
                latitude.setText("");
                longitude.setText("");
                altitude.setText("");
                gpsTime.setText("");
            }

            for (int i = 0; i < UserActivity.getOrchestrator().getAutopilot().getCommandQueue().size(); i++) {
                //Log.e(TAG, UserActivity.getOrchestrator().getAutopilot().getCommandQueue().get(i).toString());
                Log.e(TAG, UserActivity.getOrchestrator().getAutopilot().getCommandQueue().get(i).getId());
            }
        });
        addPattern.setOnClickListener(v -> {
            if (patternTime.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                UserActivity.getOrchestrator().getAutopilot().addToCommandQueue("Temp pattern", patternTime.getText().toString().trim());
                patternTime.setText("");
            }
            for (int i = 0; i < UserActivity.getOrchestrator().getAutopilot().getCommandQueue().size(); i++) {
                Log.e(TAG, UserActivity.getOrchestrator().getAutopilot().getCommandQueue().get(i).getId());
            }
        });


        // Set the button's click listener to return to the UserPhoneFragment
        manualButton.setOnClickListener(v -> {
            // Ensure the activity is of type UserActivity
            if (getActivity() instanceof UserActivity) {
                // Call switchFragment on the activity
                ((UserActivity) getActivity()).switchFragment(UserActivity.getUserPhoneFragment());
            }
        });
        // This will listen for messages from the drone websocket
        UserActivity.getOrchestrator().webSocket.setWebSocketMessageListener(message -> {
            String[] strBreakup = message.split(",");
            if (Objects.equals(strBreakup[0], "getSpeed")) {
                UserActivity.getOrchestrator().getAutopilot().setCurrentSpeed(Float.parseFloat(strBreakup[1]));
                //Log.e(TAG, "TESTING1: ", UserActivity.getOrchestrator().getAutopilot().setCurrentSpeed(Float.parseFloat(strBreakup[1])));
            } else if (Objects.equals(strBreakup[0], "getHeading")) {
                UserActivity.getOrchestrator().getAutopilot().setCurrentHeading(Float.parseFloat(strBreakup[1]));
            } else if (Objects.equals(strBreakup[0], "getGPS")){
                tempGPS = new GPS(strBreakup[1], strBreakup[2], strBreakup[3]);
                UserActivity.getOrchestrator().getAutopilot().setCurrentGPS(tempGPS);
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get a reference to the TextView
        speedTextView = view.findViewById(R.id.speedTextView);
        headingTextView = view.findViewById(R.id.HeadingViewText);
        gpsTextView = view.findViewById(R.id.gpsTextView);
        startSpeedUpdates();
    }

    private void updateUI() {
        String speedText = getString(R.string.speed_display, currentSpeed);
        speedTextView.setText(speedText);
        //update heading
        String headingText = getString(R.string.heading_display, currentHeading);
        speedTextView.setText(headingText);

        //update GPS
//        if(//currentGPS != null && get altitude from currentGPS)
//        ){
//            String gpsText = getString(R.string.gps_display, tempGPS.getLatitude(), tempGPS.getLongitude(), tempGPS.getAltitude());
//            gpsTextView.setText(gpsText);
//        }
    }
    private double getCurrentSpeed() {
        return (UserActivity.getOrchestrator().getAutopilot().getCurrentSpeed());
    }
    private GPS getCurrentGPS() {
        return (UserActivity.getOrchestrator().getAutopilot().getCurrentGPS());
    }

    private float getCurrentHeading() {
        return (UserActivity.getOrchestrator().getAutopilot().getCurrentHeading());
    }
    private void startSpeedUpdates() {
        uiUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                currentSpeed = getCurrentSpeed();
                currentHeading = getCurrentHeading();
                updateUI();
                uiUpdateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        uiUpdateHandler.post(uiUpdateRunnable);
    }
    private void stopSpeedUpdates() {
        if(uiUpdateHandler != null && uiUpdateRunnable != null) {
            uiUpdateHandler.removeCallbacks(uiUpdateRunnable);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSpeedUpdates();
    }

    public void onFlightControllerMessage(String message) {
        if (message.startsWith("getSpeed,")) {
            try {
                String[] parts = message.split(",");
                if (parts.length == 2) {
                    currentSpeed = Double.parseDouble(parts[1]);
                    requireActivity().runOnUiThread(this::updateUI);
                }
            } catch (NumberFormatException e) {
                Log.e("AutopilotFragment", "Failed to parse speed", e);
            }
        }
    }

}