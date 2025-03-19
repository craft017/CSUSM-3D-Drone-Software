package com.example.airsimapp;

import android.util.Log;

public class Orchestrator {
    //TODO add websocket connection here instead of connecting to drone directly, THIS CODE CAN BE USED ON DRONE PHONE
    // TO CONNECT TO DRONE
    private final Autopilot autopilot;
    private final flightControllerInterface flightController;

    public Orchestrator(flightControllerInterface flightController) {
        this.flightController = flightController; // No need to create flight controller here, will instead be
                                                // passed through websocket.
        autopilot = new Autopilot();
    }

    public void connectToDrone() {
        flightController.connect();
    } // This can connect to websockets instead of drone directly

    public void processCommand(String userAction, CommandCallback callback) {
        String[] message = userAction.split(",");
        switch(message[0]){ //Use action identifier for each type of message
            case "manual":
                String command = autopilot.getManual().translateCommand(userAction);
                callback.onCommandReady(command);
                flightController.sendToDrone(command); // Send to websocket -> Drone Phone
                break;
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
                Log.e("Orchestrator", "Unknown Message Received, Cannot Process Command");
        }


    }

    public interface CommandCallback {
        void onCommandReady(String jsonCommand);
    }
}
