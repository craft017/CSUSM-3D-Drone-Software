package com.example.airsimapp;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


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

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;


public class UserPhoneFragment extends Fragment {
    private static final String TAG = "UserPhoneFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private TextView output;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private final Set<String> activeActions = new HashSet<>();
    private Runnable commandRunnable;
    private Orchestrator orchestrator;
    private static final long COMMAND_INTERVAL = 100;
    private final Handler commandHandler = new Handler(Looper.getMainLooper());

    public UserPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_phone, container, false);

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
        output = rootView.findViewById(R.id.output);
        output.setMovementMethod(new ScrollingMovementMethod());
        //previewView = rootView.findViewById(R.id.previewView);
        // flightControllerSpinner may need to be in dronePhoneFragment
        Spinner flightControllerSpinner = rootView.findViewById(R.id.flight_controller_spinner);

        // Set up listeners, this is what the buttons do when clicked/held.
        autoPilotButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("button_text", "Manual");  // Set new button text for Autopilot fragment

            AutopilotFragment autopilotFragment = new AutopilotFragment();
            autopilotFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, autopilotFragment);
            transaction.addToBackStack(null);
            transaction.commit();
                });
        start.setOnClickListener(v -> orchestrator.connectToDrone());
        takeoff.setOnClickListener(v -> orchestrator.processCommand("takeoff", this::sendCommand));
        land.setOnClickListener(v -> orchestrator.processCommand("land", this::sendCommand));
        setMovementListener(forward, "forward");
        setMovementListener(backward, "backward");
        setMovementListener(left, "left");
        setMovementListener(right, "right");
        setMovementListener(up, "up");
        setMovementListener(down, "down");
        setMovementListener(rleft, "left_turn");
        setMovementListener(rright, "right_turn");

        // Set up Spinner (dropdown)
        String[] controllers = {"AirSim"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, controllers);
        flightControllerSpinner.setAdapter(adapter);


        // Handle dropdown selection
        flightControllerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedController = (String) parent.getItemAtPosition(position);
                if (selectedController.equals("AirSim")) {
                    AirSimFlightController flightController = new AirSimFlightController(output);
                    orchestrator = new Orchestrator(flightController);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        // Default Spinner selection
        flightControllerSpinner.setSelection(0);
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
            orchestrator.processCommand("stop", UserPhoneFragment.this::sendCommand);
        } else {
            // Define correct order of actions
            String[] correctOrder = {"forward", "backward", "left", "right", "up", "down", "left_turn", "right_turn"};

            // Sort activeActions according to the predefined order
            List<String> sortedActions = new ArrayList<>(activeActions);
            sortedActions.sort(Comparator.comparingInt(action -> Arrays.asList(correctOrder).indexOf(action)));
            // Combine active actions using underscores (e.g., "forward_right")
            String combinedAction = String.join("_", sortedActions);
            orchestrator.processCommand(combinedAction, UserPhoneFragment.this::sendCommand);
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
            orchestrator.processCommand("stop", UserPhoneFragment.this::sendCommand); // Send stop command
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

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
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
                startCamera();
            } else {
                // Handle permission denial
                Log.e(TAG, "Permissions not granted by the user.");
                if (getActivity() != null) {
                    getActivity().finish(); // Close the activity containing the fragment
            }
        }
    }
    }
}
