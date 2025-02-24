package com.example.airsimapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AutopilotFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autopilot, container, false);

        // Get the button from the layout
        Button autoPilotButton = view.findViewById(R.id.autoPilotButton);

        // Retrieve the passed argument (button text) if available
        Bundle args = getArguments();
        if (args != null) {
            String buttonText = args.getString("button_text", "Autopilot");
            autoPilotButton.setText(buttonText);  // Set the button text to "Manual"
        }
        // Set the button's click listener to return to the UserPhoneFragment
        autoPilotButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new UserPhoneFragment());
            transaction.addToBackStack(null);  // Optional, allows you to go back to AutopilotFragment
            transaction.commit();
        });
        return view;
    }
}