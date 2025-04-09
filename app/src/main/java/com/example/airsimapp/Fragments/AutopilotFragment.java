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

import com.example.airsimapp.Activities.UserActivity;
import com.example.airsimapp.GPS;
import com.example.airsimapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;

public class AutopilotFragment extends Fragment {
    private static final String TAG = "AutopilotFragment";
    private TextView speedTextView;
    private double currentSpeed = 0.0;
    private Runnable speedUpdateRunnable;
    public GPS tempGPS;
    private Handler speedUpdateHandler = new Handler(Looper.getMainLooper());
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
        EditText headingSpeed = view.findViewById(R.id.headingSpeed);
        EditText headingTime = view.findViewById(R.id.headingTime);
        EditText patternTime = view.findViewById(R.id.patternTime);
        Button addGPS = view.findViewById(R.id.addGPS);
        Button addHeadingSpeed = view.findViewById(R.id.addHeadingSpeed);
        Button addPattern = view.findViewById(R.id.addPattern);
        addHeadingSpeed.setOnClickListener(v -> {
            UserActivity.getOrchestrator().getAutopilot().addToCommandQueue(headingSpeed.toString(), headingTime.toString());
        });
        addGPS.setOnClickListener(v -> {
            UserActivity.getOrchestrator().getAutopilot().addToCommandQueue(latitude.toString(), longitude.toString(), altitude.toString(), gpsTime.toString());
        });
        addPattern.setOnClickListener(v -> {
            UserActivity.getOrchestrator().getAutopilot().addToCommandQueue();
        });


        // Set the button's click listener to return to the UserPhoneFragment
        manualButton.setOnClickListener(v -> {
            // Ensure the activity is of type UserActivity
            if (getActivity() instanceof UserActivity) {
                // Call switchFragment on the activity
                ((UserActivity) getActivity()).switchFragment(UserActivity.getUserPhoneFragment());
            }
        });

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
        startSpeedUpdates();
    }

    private void updateSpeedTextView() {
        String speedText = getString(R.string.speed_display, currentSpeed);
        speedTextView.setText(speedText);
    }
    private double getCurrentSpeed() {
        return (UserActivity.getOrchestrator().getAutopilot().getCurrentSpeed());
    }
    private void startSpeedUpdates() {
        speedUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                currentSpeed = getCurrentSpeed();
                updateSpeedTextView();
                speedUpdateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        speedUpdateHandler.post(speedUpdateRunnable);
    }
    private void stopSpeedUpdates() {
        if(speedUpdateHandler != null && speedUpdateRunnable != null) {
            speedUpdateHandler.removeCallbacks(speedUpdateRunnable);
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
                    requireActivity().runOnUiThread(this::updateSpeedTextView);
                }
            } catch (NumberFormatException e) {
                Log.e("AutopilotFragment", "Failed to parse speed", e);
            }
        }
    }

}