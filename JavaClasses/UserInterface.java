package com.example.airsimapp;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class UserInterface extends AppCompatActivity {
    private WebSocket webSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize WebSocket connection
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://localhost:8765").build();
        webSocket = client.newWebSocket(request, new DroneWebSocketListener());
        // Set up buttons
        Button buttonForward = findViewById(R.id.buttonForward);
        Button buttonBackward = findViewById(R.id.buttonBackward);
        Button buttonLeft = findViewById(R.id.buttonLeft);
        Button buttonRight = findViewById(R.id.buttonRight);
        // Set touch listeners for buttons
        buttonForward.setOnTouchListener(createTouchListener("takeoff"));
        buttonBackward.setOnTouchListener(createTouchListener("land"));
        buttonLeft.setOnTouchListener(createTouchListener("moveLeft"));
        buttonRight.setOnTouchListener(createTouchListener("moveRight"));
    }
    private View.OnTouchListener createTouchListener(String action) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        webSocket.send("{\"action\": \"" + action + "\"}");
                        break;
                    case MotionEvent.ACTION_UP:
                        webSocket.send("{\"action\": \"stop\"}");
                        break;
                }
                return true;
            }
        };
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "App closed");
        }
    }
    private class DroneWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            System.out.println("WebSocket Connected");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("Message Received: " + text);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            System.out.println("WebSocket Error: " + t.getMessage());
        }
    }
}