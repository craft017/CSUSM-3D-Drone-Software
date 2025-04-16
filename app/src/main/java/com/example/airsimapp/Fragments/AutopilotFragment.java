package com.example.airsimapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.airsimapp.Activities.UserActivity;
import com.example.airsimapp.GPS;
import com.example.airsimapp.R;

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autopilot, container, false);

        // Get the button from the layout
        Button manualButton = view.findViewById(R.id.manualButton);

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
            } else if (Objects.equals(strBreakup[0], "getGPS")) {
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
        headingTextView = view.findViewById(R.id.headingTextView);
        gpsTextView = view.findViewById(R.id.gpsTextView);
        startSpeedUpdates();
    }

    private void updateUI() {
        String speedText = getString(R.string.speed_display, currentSpeed);
        speedTextView.setText(speedText);
        //update heading
        String headingText = getString(R.string.heading_display, currentHeading);
        headingTextView.setText(headingText);

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