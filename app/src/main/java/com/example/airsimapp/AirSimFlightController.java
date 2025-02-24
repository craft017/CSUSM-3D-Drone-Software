package com.example.airsimapp;

import android.util.Log;
import android.widget.TextView;

import okhttp3.*;

public class AirSimFlightController implements flightControllerInterface {
    private static final String TAG = "FlightController";
    private WebSocket webSocket;
    private final OkHttpClient client;
    private final TextView output;

    public AirSimFlightController(TextView output) {
        this.client = new OkHttpClient();
        this.output = output;
    }

    @Override
    public void connect() {
        Request request = new Request.Builder().url("ws://10.0.2.2:8766").build();
        webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }

    @Override
    public void sendToDrone(String jsonCommand) {
        if (webSocket != null) {
            webSocket.send(jsonCommand);
        } else {
            Log.e(TAG, "WebSocket is not connected");
        }
    }

    private class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            appendOutput("Connected to WebSocket");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            appendOutput("Received message: " + text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            appendOutput("WebSocket closing: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            appendOutput("WebSocket error: " + t.getMessage());
        }

        private void appendOutput(String message) {
            output.post(() -> output.append("\n" + message));
        }
    }
}
