package com.example.airsimapp.Fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.airsimapp.Activities.UserActivity;
import com.example.airsimapp.AirSimFlightController;
import com.example.airsimapp.R;
import com.example.airsimapp.WebSocketClientTesting;
import com.example.airsimapp.flightControllerInterface;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import okhttp3.WebSocket;
import okhttp3.Response;

public class DronePhoneFragment extends Fragment {

    private WebSocket websocketTest;

    public WebSocketClientTesting webSocket = new WebSocketClientTesting();
    private TextView output;
    private flightControllerInterface flightController;
    private String command;
    private Button connectUserButton;
    private Button connectDroneButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drone_phone, container, false);
        connectDroneButton = rootView.findViewById(R.id.connectDroneButton);
        connectUserButton = rootView.findViewById(R.id.connectUserButton);
        output = rootView.findViewById(R.id.droneActivityTextView);
        Spinner flightControllerSpinner = rootView.findViewById(R.id.flightControllerSpinner);
        connectUserButton.setOnClickListener(v -> connectToUser());
        connectDroneButton.setOnClickListener(v -> connectToDrone());
        webSocket.setWebSocketStateListener(new WebSocketClientTesting.WebSocketStateListener() {
            @Override
            public void onOpen() {
                // already on main thread thanks to your listener
                connectUserButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.status_ok)
                );
                connectUserButton.setText("CONNECTED");
            }

            @Override
            public void onFailure(Throwable t, Response response) {
                connectUserButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.button_primary)
                );
                connectUserButton.setText("Connect To User Phone");
                Toast.makeText(requireContext(),
                        "Failed: " + t.getMessage(), Toast.LENGTH_SHORT
                ).show();
            }
        });
        connectUserButton.setOnClickListener(v -> webSocket.connect("ws://10.0.2.2:8766"));
        // Set up Spinner (dropdown)
        String[] controllers = {"AirSim"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, controllers);
        flightControllerSpinner.setAdapter(adapter);

        flightControllerSpinner.setSelection(0);
        // Handle dropdown selection
        flightControllerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedController = (String) parent.getItemAtPosition(position);
                if (selectedController.equals("AirSim")) {
                    flightController = new AirSimFlightController();
                    flightController.setMessageListener(message -> {
                        if (webSocket != null) {
                            webSocket.sendMessage(message);
                            //requireActivity().runOnUiThread(() -> output.setText("Response from drone: " + message));
                        }
                    });
                } // add more flightControllers here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        // Default Spinner selection

        // Sends messages received from the user phone directly to the drone
        webSocket.setWebSocketMessageListener(message -> {
            if (output != null) {

                command = message;

                requireActivity().runOnUiThread(() -> output.setText(message)); // UI update
                if (flightController != null) {
                    flightController.sendToDrone(command);
                } else {
                    Log.e("DronePhoneFragment", "flightController is null!");
                }
            }
        });
        return rootView;
    }

    private void connectToUser(){
        webSocket.connect("ws://10.0.2.2:8766");
        webSocket.sendMessage("This is from the drone phone!");
    }

    private void connectToDrone(){
        flightController.connect();
    }

    public void sendFrame(Image image) {
        if (webSocket == null) return;

        Bitmap bitmap = imageToBitmap(image);
        if (bitmap != null) {
            byte[] compressedData = compressBitmap(bitmap);
            //webSocket.send(compressedData); // Send raw bytes
        }
    }

    private Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Compress to reduce size
        return baos.toByteArray();
    }

}