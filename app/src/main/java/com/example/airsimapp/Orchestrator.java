package com.example.airsimapp;

public class Orchestrator {
    private final Autopilot autopilot;
    private final flightControllerInterface flightController;

    public Orchestrator(flightControllerInterface flightController) {
        this.flightController = flightController;
        this.autopilot = new Autopilot();
    }

    public void connectToDrone() {
        flightController.connect();
    }

    public void processCommand(String userAction, CommandCallback callback) {
        String command = autopilot.getManual().translateCommand(userAction);
        callback.onCommandReady(command);
        flightController.sendToDrone(command);
    }

    public interface CommandCallback {
        void onCommandReady(String jsonCommand);
    }
}
