package com.example.airsimapp.Activities;

import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.airsimapp.AirSimFlightController;
import com.example.airsimapp.Fragments.AutopilotFragment;
import com.example.airsimapp.Fragments.ManualFragment;
import com.example.airsimapp.Orchestrator;
import com.example.airsimapp.R;

public class UserActivity extends AppCompatActivity {
    private static Orchestrator orchestrator;
    private static ManualFragment manualFragment;
    private static AutopilotFragment autopilotFragment;
    private FragmentTransaction fragmentTransaction;

    public static Fragment getAutopilotFragment(){
        return autopilotFragment;
    }
    public static Fragment getUserPhoneFragment(){
        return manualFragment;
    }
    public static Orchestrator getOrchestrator() {
        return orchestrator;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        //AirSimFlightController flightController = new AirSimFlightController();
        orchestrator = new Orchestrator(); // Create here to use in manual and autopilot fragments for now, will be on drone phone later
        // Initialize fragments
        manualFragment = new ManualFragment();
        autopilotFragment = new AutopilotFragment();

// Check if the fragments are already added
        if (savedInstanceState == null) {
            // Add the fragments but don't show any initially
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, manualFragment);
            fragmentTransaction.add(R.id.fragment_container, autopilotFragment);
            fragmentTransaction.hide(autopilotFragment); // Hide the autopilot fragment initially
            fragmentTransaction.commit();
        }
    }

    // Function to switch between Autopilot & Manual Control
    public void switchFragment(Fragment newFragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Show the new fragment
        fragmentTransaction.show(newFragment);

        // Hide the other fragment
        if (newFragment instanceof ManualFragment) { // if fragment passed through is userFragment, hide the autopilot
            fragmentTransaction.hide(autopilotFragment);
        } else if (newFragment instanceof AutopilotFragment) { // if fragment passed through is AutopilotFragment, hide the UserPhoneFragment
            fragmentTransaction.hide(manualFragment);
        }

        fragmentTransaction.addToBackStack(null); // Optional if you want the ability to go back
        fragmentTransaction.commit();
    }
}