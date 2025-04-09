package com.example.airsimapp;

import android.util.Log;

import com.example.airsimapp.Activities.UserActivity;

import okhttp3.WebSocket;

public class Orchestrator {
    //TODO add websocket connection here instead of connecting to drone directly, THIS CODE CAN BE USED ON DRONE PHONE
    // TO CONNECT TO DRONE
    private Autopilot autopilot;
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

    public Autopilot getAutopilot() {
        return autopilot;
    }

    public void connectToDrone() {
        webSocket.connect("ws://10.0.2.2:8766");
        webSocket.sendMessage("CONNECTION FROM USER PHONE!");
//        webSocket.setWebSocketMessageListener(new WebSocketClientTesting.WebSocketMessageListener() {
//            @Override
//            public void onMessageReceived(String message) {
//                UserActivity.getAutopilotFragment().onFlightControllerMessage(message);
//            }
//        });
       // flightController.connect();
    } // This can connect to websockets instead of drone directly

    public void recieveCommand(){

    }
    public void processCommand(String userAction, CommandCallback callback) {

        command = autopilot.getManual().translateCommand(userAction, autopilot.getYawRate(), autopilot.getVelocity(), autopilot.getCommandTime());
        webSocket.sendMessage(command);
        callback.onCommandReady(command);
        //flightController.sendToDrone(command); // Send to websocket -> Drone Phone

        String[] message = userAction.split(",");
        switch(message[0]){ //Use action identifier for each type of message
            case "manual":
                command = autopilot.getManual().translateCommand(userAction, autopilot.getYawRate(), autopilot.getVelocity(), autopilot.getCommandTime());
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
