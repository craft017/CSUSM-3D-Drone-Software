package com.example.airsimapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.airsimapp.Activities.UserActivity;
import com.example.airsimapp.R;

public class AutopilotFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autopilot, container, false);

        // Get the button from the layout
        Button manualButton = view.findViewById(R.id.manualButton);
        //TextView debugMessages = view.findViewById(R.id.autopilotDebug); //For testing purposes only

        // Set the button's click listener to return to the UserPhoneFragment
        manualButton.setOnClickListener(v -> {
            // Ensure the activity is of type UserActivity
            if (getActivity() instanceof UserActivity) {
                // Call switchFragment on the activity
                ((UserActivity) getActivity()).switchFragment(UserActivity.getUserPhoneFragment());

            }
        });
        return view;
    }
    //Testing output function
    private void appendOutput(String message, TextView debugMessages) {
              debugMessages.post(() -> debugMessages.append("\n" + message));
          }
}