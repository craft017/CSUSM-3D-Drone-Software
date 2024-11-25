package com.example.airsimapp;

public class Orchestrator {
    private final Manual manual;
    private final Autopilot autopilot;
    private final flightControllerInterface flightController;

    public Orchestrator(flightControllerInterface flightController) {
        this.flightController = flightController;
        this.manual = new Manual();
        this.autopilot = new Autopilot();
    }

    public void connectToWebSocket() {
        flightController.connect();
    }

    public void processCommand(String userAction, CommandCallback callback) {
        String command = manual.translateCommand(userAction);
        callback.onCommandReady(command);
        flightController.sendToWebSocket(command);
    }

    public interface CommandCallback {
        void onCommandReady(String jsonCommand);
    }
}
