package com.example.airsimapp;

import android.util.Log;

import java.io.IOException;

import fi.iki.elonen.NanoWSD;
// FOR TESTING!!!!!!
public class DroneWebSocketServer extends NanoWSD {
    private static final String TAG = "DroneWebSocketServer";
    private final Orchestrator orchestrator;

    public DroneWebSocketServer(int port, flightControllerInterface flightController) {
        super(port);
        this.orchestrator = new Orchestrator(flightController);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new DroneWebSocket(this);
    }

    private class DroneWebSocket extends WebSocket {
        public DroneWebSocket(NanoWSD server) {
            super((IHTTPSession) server);
        }

        @Override
        protected void onOpen() {
            Log.d(TAG, "WebSocket connection opened.");
        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            String userAction = message.getTextPayload();
            Log.d(TAG, "Received command from user phone: " + userAction);

            orchestrator.processCommand(userAction, new Orchestrator.CommandCallback() {
                @Override
                public void onCommandReady(String jsonCommand) {
                    Log.d(TAG, "Processed command: " + jsonCommand);
                    // The orchestrator has already sent the command to AirSim, so no extra processing needed.
                }
            });

        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            Log.d(TAG, "WebSocket closed: " + reason);
        }

        @Override
        protected void onPong(WebSocketFrame pong) {}

        @Override
        protected void onException(IOException e) {
            Log.e(TAG, "WebSocket error: " + e.getMessage());
        }
    }
}
