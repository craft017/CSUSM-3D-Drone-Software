package com.example.airsimapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

// ModeSelectionFragment.java
public class ModeSelectionFragment extends Fragment {

    private Button userPhoneButton;
    private Button dronePhoneButton;

    public ModeSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mode_selection, container, false);

        userPhoneButton = rootView.findViewById(R.id.user_phone_fragment);
        dronePhoneButton = rootView.findViewById(R.id.drone_phone_fragment);

        userPhoneButton.setOnClickListener(v -> switchToUserPhoneMode());
        dronePhoneButton.setOnClickListener(v -> switchToDronePhoneMode());

        return rootView;
    }

    private void switchToUserPhoneMode() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserPhoneFragment());
        transaction.addToBackStack(null);  // To allow the user to go back
        transaction.commit();
    }

    private void switchToDronePhoneMode() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new DronePhoneFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
