package com.example.airsimapp;

import org.json.JSONObject;

public class Manual {
    public String translateCommand(String userAction) {
        String action = "";
        switch (userAction) {
            case "forward":
                action = "forward";
                break;
            case "backward":
                action = "backward";
                break;
            case "left":
                action = "left";
                break;
            case "right":
                action = "right";
                break;
            case "takeoff":
                action = "takeoff";
                break;
            case "land":
                action = "land";
                break;
            case "stop":
                action = "stop";
                break;
            default:
                action = "unknown";
        }

        try {
            JSONObject command = new JSONObject();
            command.put("action", action);
            return command.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
