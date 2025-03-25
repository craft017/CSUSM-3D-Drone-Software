package com.example.airsimapp;

import org.json.JSONObject;

public class Manual {
    public String translateCommand(String userAction) {
        String action = "";
        switch (userAction) {
            case "manual,forward":
                action = "forward";
                break;
            case "manual,backward":
                action = "backward";
                break;
            case "manual,left":
                action = "left";
                break;
            case "manual,right":
                action = "right";
                break;
            case "manual,forward_left":
                action = "forward_left";
                break;
            case "manual,forward_right":
                action = "forward_right";
                break;
            case "manual,backward_left":
                action = "backward_left";
                break;
            case "manual,backward_right":
                action = "backward_right";
                break;
            case "manual,up":
                action = "up";
                break;
            case "manual,down":
                action = "down";
                break;
            case "manual,forward_up":
                action = "forward_up";
                break;
            case "manual,backward_up":
                action = "backward_up";
                break;
            case "manual,left_up":
                action = "left_up";
                break;
            case "manual,right_up":
                action = "right_up";
                break;
            case "manual,forward_left_up":
                action = "forward_left_up";
                break;
            case "manual,forward_right_up":
                action = "forward_right_up";
                break;
            case "manual,backward_left_up":
                action = "backward_left_up";
                break;
            case "manual,backward_right_up":
                action = "backward_right_up";
                break;
            case "manual,forward_down":
                action = "forward_down";
                break;
            case "manual,backward_down":
                action = "backward_down";
                break;
            case "manual,left_down":
                action = "left_down";
                break;
            case "manual,right_down":
                action = "right_down";
                break;
            case "manual,forward_left_down":
                action = "forward_left_down";
                break;
            case "manual,forward_right_down":
                action = "forward_right_down";
                break;
            case "manual,backward_left_down":
                action = "backward_left_down";
                break;
            case "manual,backward_right_down":
                action = "backward_right_down";
                break;
            case "manual,right_turn":
                action = "right_turn";
                break;
            case "manual,left_turn":
                action = "left_turn";
                break;
            case "manual,takeoff":
                action = "takeoff";
                break;
            case "manual,land":
                action = "land";
                break;
            case "manual,stop":
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