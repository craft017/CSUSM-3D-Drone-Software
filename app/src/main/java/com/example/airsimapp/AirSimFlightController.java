package com.example.airsimapp;

import android.util.Log;
import android.widget.TextView;

import okhttp3.*;

public class AirSimFlightController implements flightControllerInterface {
    private static final String TAG = "AirSimFlightController";
    private WebSocket webSocket;
    private final OkHttpClient client;
    private final TextView output;

    public AirSimFlightController(TextView output) {
        this.client = new OkHttpClient();
        this.output = output;
    }

    public AirSimFlightController(){
        this.client = new OkHttpClient();
        output = null;
    }

    @Override
    public void connect() {
        Request request = new Request.Builder().url("ws://10.0.2.2:8765").build();
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
        public void onOpen(WebSocket webSocket, Response response) {
           // appendOutput("Connected to WebSocket");
            Log.d(TAG, "Connected to WebSocket");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
          //  appendOutput("Sending message: " + text);
            Log.d(TAG, "Receiving message: " + text);
            String[] message = text.split(",");
            switch(message[0]){ //Use action identifier for each type of message
                case "getGPS":
                    //TODO display current location
                    break;
                case "getSpeed":
                    //TODO display current speed
                    break;
                case "getHeading":
                    //TODO display current heading
                    break;
                default:
                    Log.e(TAG, "Unknown Message Received");
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
           // appendOutput("WebSocket closing: " + reason);
            Log.d(TAG, "WebSocket closing: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
           // appendOutput("WebSocket error: " + t.getMessage());
            Log.d(TAG, "WebSocket error: " + t.getMessage());
        }

//        private void appendOutput(String message) {
//            output.post(() -> output.append("\n" + message));
//        }
    }
}
