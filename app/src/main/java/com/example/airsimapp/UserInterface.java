package com.example.airsimapp;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
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
    private Button start, forward, backward, left, right, takeoff, land;
    private TextView output;
    private Orchestrator orchestrator;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        start = findViewById(R.id.start);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        takeoff = findViewById(R.id.takeoff_button);
        land = findViewById(R.id.land_button);
        output = findViewById(R.id.output);
        previewView = findViewById(R.id.previewView);

        // Initialize Orchestrator
        flightController flightController = new flightController(output);
        orchestrator = new Orchestrator(flightController);

        // Set up listeners, this is what the buttons do when clicked/held.
        start.setOnClickListener(v -> orchestrator.connectToWebSocket());
        takeoff.setOnClickListener(v -> orchestrator.processCommand("takeoff", this::sendCommand));
        land.setOnClickListener(v -> orchestrator.processCommand("land", this::sendCommand));
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
                    orchestrator.processCommand(action, this::sendCommand);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
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

