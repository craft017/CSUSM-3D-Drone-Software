package com.example.airsimapp;

import android.util.Log;

import okhttp3.WebSocket;

public class Orchestrator {
    //TODO add websocket connection here instead of connecting to drone directly, THIS CODE CAN BE USED ON DRONE PHONE
    // TO CONNECT TO DRONE
    private  Autopilot autopilot;
    public WebSocketClientTesting webSocket;
    //private final flightControllerInterface flightController;
    private String command;

    public Orchestrator(flightControllerInterface flightController) {
        //this.flightController = flightController;
        this.webSocket = new WebSocketClientTesting();
        this.autopilot = new Autopilot();
    }

    public Orchestrator() {
        //this.flightController = flightController;
        this.webSocket = new WebSocketClientTesting();
        this.autopilot = new Autopilot();
    }


    public void connectToDrone() {
        webSocket.connect("ws://10.0.2.2:8766");
        webSocket.sendMessage("PLEASE WORK!");
       // flightController.connect();
    } // This can connect to websockets instead of drone directly

    public void recieveCommand(){
        webSocket.setWebSocketMessageListener(message -> {
            String[] recievedMessage = message.split(",");
            switch(recievedMessage[0]) {
                case "getGPS":
                    autopilot.getCurrentGPS().setLatitude(Float.parseFloat(recievedMessage[1]));
                    autopilot.getCurrentGPS().setLongitude(Float.parseFloat(recievedMessage[2]));
                    autopilot.getCurrentGPS().setAltitude(Float.parseFloat(recievedMessage[3]));
                    break;

                case "getHeading":
                    autopilot.setCurrentHeading(Float.parseFloat(recievedMessage[1]));
                    break;

                case "getSpeed":
                    autopilot.setCurrentSpeed(Float.parseFloat(recievedMessage[1]));
                    break;
            }

//                requireActivity().runOnUiThread(() -> output.setText(message)); // UI update
//                if (flightController != null) {
//                    flightController.sendToDrone(command);
//                } else {
//                    Log.e("DronePhoneFragment", "flightController is null!");
//                }
        });
    }
    public void processCommand(String userAction, CommandCallback callback) {
//        command = autopilot.getManual().translateCommand(userAction);
//        webSocket.sendMessage(command);
//        callback.onCommandReady(command);
        //flightController.sendToDrone(command); // Send to websocket -> Drone Phone

        String[] message = userAction.split(",");
        switch(message[0]){ //Use action identifier for each type of message
            case "manual":
                command = autopilot.getManual().translateCommand(userAction);
                callback.onCommandReady(command);
                webSocket.sendMessage(command); // Send to websocket -> Drone Phone
                break;
            case "getGPS":
                GPS gps = new GPS(message[1], message[2], message[3]);
                autopilot.setCurrentGPS(gps);
                break;
            case "getSpeed":
                Float speed = Float.parseFloat(message[1]);
                autopilot.setCurrentSpeed(speed);
                break;
            case "getHeading":
                Float heading = Float.parseFloat(message[1]);
                autopilot.setCurrentHeading(heading);
                break;
            default:
                Log.e("Orchestrator", "Unknown Message Received, Cannot Process Command");
        }


    }

    public interface CommandCallback {
        void onCommandReady(String jsonCommand);
    }
}
