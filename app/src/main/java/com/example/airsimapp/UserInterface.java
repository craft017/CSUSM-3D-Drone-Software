package com.example.airsimapp;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.Manifest;


public class UserInterface extends AppCompatActivity {
    private static final String TAG = "UserInterface";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private Button start, forward, backward, left, right, takeoff, land, up, down, Rleft, Rright, switchScreens;
    private TextView output;
    private Orchestrator orchestrator;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private Spinner flightControllerSpinner;
    private final Set<String> activeCommands = new HashSet<>();
    private final Set<String> activeActions = new HashSet<>();
    private final Handler commandHandler = new Handler(Looper.getMainLooper());
    private Runnable commandRunnable;
    private static final long COMMAND_INTERVAL = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Set the ModeSelectionFragment as the initial fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ModeSelectionFragment())
                    .commit();
        }

        // Initialize UI components
        start = findViewById(R.id.start);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        takeoff = findViewById(R.id.takeoff);
        land = findViewById(R.id.land);
        up = findViewById(R.id.go_up);
        down = findViewById(R.id.go_down);
        Rleft = findViewById(R.id.Rleft);
        Rright = findViewById(R.id.Rright);
        switchScreens = findViewById(R.id.switchScreens);
        output = findViewById(R.id.output);
        output.setMovementMethod(new ScrollingMovementMethod());
        previewView = findViewById(R.id.previewView);
        flightControllerSpinner = findViewById(R.id.flight_controller_spinner);

//        switchScreens.setOnClickListener(v -> setContentView(R.layout.activity_mode_selection));
//        if (switchScreens.hasFocus()){
//            setContentView(R.layout.activity_mode_selection);
//        }
        // Set up Spinner (dropdown)
        String[] controllers = {"AirSim"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, controllers);
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

        //End of dropdown & spinner logic

        // Set up listeners, this is what the buttons do when clicked/held.
        start.setOnClickListener(v -> orchestrator.connectToDrone());
        takeoff.setOnClickListener(v -> orchestrator.processCommand("takeoff", this::sendCommand));
        land.setOnClickListener(v -> orchestrator.processCommand("land", this::sendCommand));
        setMovementListener(forward, "forward");
        setMovementListener(backward, "backward");
        setMovementListener(left, "left");
        setMovementListener(right, "right");
        setMovementListener(up, "up");
        setMovementListener(down, "down");
        setMovementListener(Rleft, "left_turn");
        setMovementListener(Rright, "right_turn");

        // Starts Camera
        cameraExecutor = Executors.newSingleThreadExecutor();
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
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
            orchestrator.processCommand("stop", UserInterface.this::sendCommand);
        } else {
            // Define correct order of actions
            String[] correctOrder = {"forward", "backward", "left", "right", "up", "down", "left_turn", "right_turn"};

            // Sort activeActions according to the predefined order
            List<String> sortedActions = new ArrayList<>(activeActions);
            sortedActions.sort(Comparator.comparingInt(action -> Arrays.asList(correctOrder).indexOf(action)));
            // Combine active actions using underscores (e.g., "forward_right")
            String combinedAction = String.join("_", sortedActions);
            orchestrator.processCommand(combinedAction, UserInterface.this::sendCommand);
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
            orchestrator.processCommand("stop", UserInterface.this::sendCommand); // Send stop command
        }
    }
    // Logic for starting the camera
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
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
        }, ContextCompat.getMainExecutor(this));
    }

    // Function to send information to log
    private void sendCommand(String command) {
        Log.d(TAG, "Sending command: " + command);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    // Function to check if camera permissions have been granted by user
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
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
                finish();
            }
        }
    }
}
