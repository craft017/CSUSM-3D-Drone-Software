package com.example.airsimapp;

import okhttp3.*;

public class WebSocketClient {
    private OkHttpClient client;
    private WebSocket webSocket;

    public static final String WEBSOCKET_URL = "ws://10.0.2.2:8765";  // Change to your server IP

    public void connectWebSocket() {
        client = new OkHttpClient();

        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("Connected to server");

                // Example: Send a takeoff command
                sendCommand("{\"action\": \"takeoff\"}");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Received response: " + text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.err.println("WebSocket failure: " + t.getMessage());
                t.printStackTrace();
                // Optionally try reconnecting or alert the user
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                System.out.println("Connection closed");
                client.dispatcher().executorService().shutdown();  // Shutdown only after the connection is closed
            }
        });
    }

    public void sendCommand(String command) {
        if (webSocket != null && webSocket.send(command)) {
            System.out.println("Command sent: " + command);
        } else {
            System.err.println("WebSocket is not open. Command failed.");
        }
    }
}