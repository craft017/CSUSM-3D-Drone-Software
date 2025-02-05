package com.example.airsimapp;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.Manifest;


public class UserInterface extends AppCompatActivity {
    private static final String TAG = "UserInterface";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private Button start, forward, backward, left, right, takeoff, land, up, down;
    private TextView output;
    private Orchestrator orchestrator;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private Spinner flightControllerSpinner;
    private Runnable commandRunnable;
    private Handler commandHandler = new Handler(Looper.getMainLooper());
    private static final long COMMAND_INTERVAL = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mode = getIntent().getStringExtra("MODE");

        if ("control_board".equals(mode)) {
            findViewById(R.id.button_layout).setVisibility(View.GONE); // Hide controller buttons
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

        output = findViewById(R.id.output);
        previewView = findViewById(R.id.previewView);
        flightControllerSpinner = findViewById(R.id.flight_controller_spinner);

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
//        forward.setOnClickListener(v -> orchestrator.processCommand("forward", this::sendCommand));
//        backward.setOnClickListener(v -> orchestrator.processCommand("backward", this::sendCommand));
//        left.setOnClickListener(v -> orchestrator.processCommand("left", this::sendCommand));
//        right.setOnClickListener(v -> orchestrator.processCommand("right", this::sendCommand));
        setMovementListener(forward, "forward");
        setMovementListener(backward, "backward");
        setMovementListener(left, "left");
        setMovementListener(right, "right");

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
                    // Start sending commands repeatedly
                    commandRunnable = new Runnable() {
                        @Override
                        public void run() {
                            orchestrator.processCommand(action, UserInterface.this::sendCommand);
                            commandHandler.postDelayed(this, COMMAND_INTERVAL);
                        }
                    };
                    commandHandler.post(commandRunnable);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Stop sending commands
                    commandHandler.removeCallbacks(commandRunnable);
                    orchestrator.processCommand("stop", this::sendCommand);
                    break;
            }
            return true;
        });
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

