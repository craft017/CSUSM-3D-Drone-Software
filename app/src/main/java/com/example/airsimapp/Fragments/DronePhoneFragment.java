package com.example.airsimapp.Fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
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
import com.google.common.util.concurrent.ListenableFuture;

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
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drone_phone, container, false);
        previewView = rootView.findViewById(R.id.previewView);
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
//        webSocket.setWebSocketMessageListener(message -> {
//            if (output != null) {
//
//                command = message;
//
//                requireActivity().runOnUiThread(() -> output.setText(message)); // UI update
//                if (flightController != null) {
//                    flightController.sendToDrone(command);
//                } else {
//                    Log.e("DronePhoneFragment", "flightController is null!");
//                }
//            }
//        });

        webSocket.setWebSocketMessageListener(new WebSocketClientTesting.WebSocketMessageListener() {
            @Override
            public void onMessageReceived(String msg) {
                if (output != null) {

                    command = msg;

                    requireActivity().runOnUiThread(() -> output.setText(msg)); // UI update
                    if (flightController != null) {
                        flightController.sendToDrone(command);
                    } else {
                        Log.e("DronePhoneFragment", "flightController is null!");
                    }
                }
            }
            @Override
            public void onByteReceived(Bitmap bitmap) {
                // update the ImageView on the main thread:
//                requireActivity().runOnUiThread(() -> {
//                    previewView.setImageBitmap(bitmap);
//                });
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // after you’ve inflated and found previewView:
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(this::bindCameraUseCases,
                ContextCompat.getMainExecutor(requireContext()));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindCameraUseCases() {
        try {
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

            // 1) Preview (optional—if you just want to send frames, you can omit this)
            Preview preview = new Preview.Builder().build();
//            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            // 2) ImageAnalysis to get frames
            ImageAnalysis analysis = new ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build();

            analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(requireContext()),
                    imageProxy -> {
                        Image mediaImage = imageProxy.getImage();
                        if (mediaImage != null) {
                            // send over your websocket
                            sendFrame(mediaImage);
                            //webSocket.sendMessage("Sending a frame");
                        }
                        imageProxy.close();
                    }
            );

            // Unbind any previous use-cases before rebinding
            cameraProvider.unbindAll();

            // Bind to lifecycle, so it automatically starts/stops with the fragment
            cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
            );

        } catch (Exception e) {
            Log.e("DronePhoneFragment", "camera init failed", e);
        }
    }

    private void connectToUser(){
        webSocket.connect("ws://10.0.2.2:8766");
    }

    private void connectToDrone(){
        flightController.connect();
    }

    public void sendFrame(Image image) {
        if (webSocket == null || image == null) return;

        // Convert YUV_420_888 → JPEG in one shot
        byte[] jpeg = yuvToJpeg(image, 50);
        webSocket.sendByte(jpeg);
    }

//    private Bitmap imageToBitmap(Image image) {
//        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//        byte[] bytes = new byte[buffer.remaining()];
//        buffer.get(bytes);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }

    private byte[] yuvToJpeg(Image image, int quality) {
        int width = image.getWidth();
        int height = image.getHeight();
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[width * height * 3 / 2];

        // Copy Y values
        yBuffer.get(nv21, 0, ySize);

        // Interleave U and V values correctly
        int uvPixelStride = image.getPlanes()[1].getPixelStride();
        int uvRowStride = image.getPlanes()[1].getRowStride();
        byte[] uBytes = new byte[uBuffer.remaining()];
        byte[] vBytes = new byte[vBuffer.remaining()];
        uBuffer.get(uBytes);
        vBuffer.get(vBytes);

        int uvPos = ySize;
        for (int row = 0; row < height / 2; row++) {
            for (int col = 0; col < width / 2; col++) {
                int uvIndex = row * uvRowStride + col * uvPixelStride;
                nv21[uvPos++] = vBytes[uvIndex]; // V first in NV21
                nv21[uvPos++] = uBytes[uvIndex]; // Then U
            }
        }

        // Now compress
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), quality, baos);

        return baos.toByteArray();
    }



}