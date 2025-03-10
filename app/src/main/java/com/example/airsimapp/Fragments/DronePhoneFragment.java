package com.example.airsimapp.Fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;


import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.example.airsimapp.R;


public class DronePhoneFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drone_phone, container, false);

        Button connectButton = rootView.findViewById(R.id.connectButton);
        TextView output = rootView.findViewById(R.id.output);
        //connectButton.setOnClickListener(v -> connectToDrone());

        return rootView;
    }

    private void connectToDrone() {
        // Code to connect to the drone, user phone should send instructions here?
    }
}