package com.example.airsimapp.Fragments;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.airsimapp.Activities.UserActivity;
import com.example.airsimapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;


public class ManualFragment extends Fragment  {
    private static final String TAG = "ManualFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    //private TextView output;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private final Set<String> activeActions = new HashSet<>();
    private Runnable commandRunnable;
    //private Orchestrator orchestrator;

// These help us loop the commands being sent
    private static final long COMMAND_INTERVAL = 100;
    private final Handler commandHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manual, container, false);

        Button start = rootView.findViewById(R.id.start);
        Button forward = rootView.findViewById(R.id.forward);
        Button backward = rootView.findViewById(R.id.backward);
        Button left = rootView.findViewById(R.id.left);
        Button right = rootView.findViewById(R.id.right);
        Button takeoff = rootView.findViewById(R.id.takeoff);
        Button land = rootView.findViewById(R.id.land);
        Button up = rootView.findViewById(R.id.go_up);
        Button down = rootView.findViewById(R.id.go_down);
        Button rleft = rootView.findViewById(R.id.Rleft);
        Button rright = rootView.findViewById(R.id.Rright);
        Button autoPilotButton = rootView.findViewById(R.id.autoPilotButton);

        previewView = rootView.findViewById(R.id.previewView);
        // flightControllerSpinner may need to be in dronePhoneFragment

        // Set up listeners, this is what the buttons do when clicked/held.
        autoPilotButton.setOnClickListener(v -> {
            // Ensure the activity is of type UserActivity
            if (getActivity() instanceof UserActivity) {
                // Call switchFragment on the activity
                ((UserActivity) getActivity()).switchFragment(UserActivity.getAutopilotFragment());
            }
        });
        start.setOnClickListener(v -> UserActivity.getOrchestrator().connectToDrone());
        takeoff.setOnClickListener(v -> UserActivity.getOrchestrator().processCommand("takeoff", this::sendCommand));
        land.setOnClickListener(v -> UserActivity.getOrchestrator().processCommand("land", this::sendCommand));
        setMovementListener(forward, "forward");
        setMovementListener(backward, "backward");
        setMovementListener(left, "left");
        setMovementListener(right, "right");
        setMovementListener(up, "up");
        setMovementListener(down, "down");
        setMovementListener(rleft, "left_turn");
        setMovementListener(rright, "right_turn");

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        return rootView;


    }
    private void setMovementListener(Button button, String action) {
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!activeActions.contains(action)) {
                        activeActions.add(action);
                        startCommandLoop(); // Start sending commands continuously

                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    activeActions.remove(action);
                    if (activeActions.isEmpty()) {
                        stopCommandLoop(); // Stop sending commands if no buttons are held
                    }
                    break;
            }
            return true;
        });
    }
    private void updateAndSendCommand() {
        if (activeActions.isEmpty()) {
            UserActivity.getOrchestrator().processCommand("stop", ManualFragment.this::sendCommand);
        } else {
            // Define correct order of actions
            String[] correctOrder = {"forward", "backward", "left", "right", "up", "down", "left_turn", "right_turn"};

            // Sort activeActions according to the predefined order
            List<String> sortedActions = new ArrayList<>(activeActions);
            sortedActions.sort(Comparator.comparingInt(action -> Arrays.asList(correctOrder).indexOf(action)));
            // Combine active actions using underscores (e.g., "forward_right")
            String combinedAction = String.join("_", sortedActions);
            UserActivity.getOrchestrator().processCommand(combinedAction, ManualFragment.this::sendCommand);
            //output.append("hello");
        }
    }
    private void startCommandLoop() {
        if (commandRunnable == null) {
            commandRunnable = new Runnable() {
                @Override
                public void run() {
                    updateAndSendCommand(); // Send the movement command
                    commandHandler.postDelayed(this, COMMAND_INTERVAL); // Repeat after delay
                }
            };
            commandHandler.post(commandRunnable); // Start the loop
        }
    }

    private void stopCommandLoop() {
        if (commandRunnable != null) {
            commandHandler.removeCallbacks(commandRunnable); // Stop sending commands
            commandRunnable = null;
            UserActivity.getOrchestrator().processCommand("stop", ManualFragment.this::sendCommand); // Send stop command
        }
    }
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview);
            } catch (Exception e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
    private void sendCommand(String command) {
        Log.d(TAG, "Sending command: " + command);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCommandLoop();
        cameraExecutor.shutdown();
    }

    // Function to check if camera permissions have been granted by user
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Log.d(TAG, "Starting camera?");
                startCamera();
            } else {
                // Handle permission denial
                Log.e(TAG, "Permissions not granted by the user.");
                if (getActivity() != null) {
                    Log.d(TAG, "Here?");
                    getActivity().finish(); // Close the activity containing the fragment
            }
        }
    }
    }
}
