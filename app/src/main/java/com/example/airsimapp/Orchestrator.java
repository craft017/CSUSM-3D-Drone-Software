package com.example.airsimapp;

public class Orchestrator {
    //TODO add websocket connection here instead of connecting to drone directly, THIS CODE CAN BE USED ON DRONE PHONE
    // TO CONNECT TO DRONE
    private final Autopilot autopilot;
    private final flightControllerInterface flightController;

    public Orchestrator(flightControllerInterface flightController) {
        this.flightController = flightController; // No need to create flight controller here, will instead be
                                                // passed through websocket.
        this.autopilot = new Autopilot();
    }

    public void connectToDrone() {
        flightController.connect();
    } // This can connect to websockets instead of drone directly

    public void processCommand(String userAction, CommandCallback callback) {
        String command = autopilot.getManual().translateCommand(userAction);
        callback.onCommandReady(command);
        flightController.sendToDrone(command); // Send to websocket -> Drone Phone
    }

    public interface CommandCallback {
        void onCommandReady(String jsonCommand);
    }
}
