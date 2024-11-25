package com.example.airsimapp;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class UserInterface extends AppCompatActivity {
    private static final String TAG = "WebSocket";
    private Button start, forward, backward, left, right;
    private TextView output;
    private OkHttpClient client;
    private WebSocket webSocket;
    private boolean isMoving = false;
    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response){

            runOnUiThread(() -> output("Connected to server!"));
            Log.d(TAG, "WebSocket opened");

        }

        @Override
        public void onMessage(WebSocket WebSocket, String text) {
            runOnUiThread(() -> output("Received: " + text));
            Log.d(TAG, "Message received: " + text);
        }

        @Override
        public void onClosing(WebSocket WebSocket, int code,  String reason){
            runOnUiThread(() -> output("Connection closed: " + reason));
            Log.d(TAG, "WebSocket closed: " + reason);
        }

        @Override
        public void onFailure(WebSocket WebSocket, Throwable t, okhttp3.Response response){
            output("Error: " + t.getMessage());
//            runOnUiThread(() -> output.setText("Connection failed: " + t.getMessage()));
//            Log.e(TAG, "WebSocket error", t);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        forward = (Button) findViewById(R.id.forward);
        backward = (Button) findViewById(R.id.backward);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        output = (TextView) findViewById(R.id.output);
        client = new OkHttpClient();

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                start();
            }
        });

        Button takeoffButton = findViewById(R.id.takeoff_button);
        takeoffButton.setOnClickListener(v -> sendCommand("takeoff"));

        Button landButton  = findViewById(R.id.land_button);
        landButton .setOnClickListener(v -> sendCommand("land"));

        // Set up continuous movement buttons
        setMovementListener(forward, "forward");
        setMovementListener(backward, "backward");
        setMovementListener(left, "left");
        setMovementListener(right, "right");
    }

    private void start(){ // Connects us to the websocket (localhost)
        if (client == null) {
            client = new OkHttpClient();
        }
        Request request = new Request.Builder().url("ws://10.0.2.2:8765").build(); // 10.0.2.2 is the host machines IP
        webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }

    private void sendCommand(String action) {
        if (webSocket != null) {
            try {
                JSONObject command = new JSONObject();
                command.put("action", action);
                command.put("params", new JSONObject()); // Empty params for now
                webSocket.send(command.toString());
            } catch (Exception e) {
                Log.e(TAG, "Failed to send command", e);
            }
        } else {
            output("No websocket connection");
        }
    }

    private void setMovementListener(Button button, String action) {
        button.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isMoving) {
                        isMoving = true;
                        new Thread(() -> {
                            while (isMoving) {
                                sendCommand(action);
                                try {
                                    Thread.sleep(200); // Adjust interval for smoother movement
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Movement thread interrupted", e);
                                }
                            }
                        }).start();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isMoving = false;
                    break;
            }
            return true;
        });
    }

    private void output(final String txt){
        runOnUiThread(( ) -> {
            output.setText(output.getText().toString() + "\n\n" + txt);
        });
    }
}