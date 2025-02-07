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
            case "forward_left":
                action = "forward_left";
                break;
            case "forward_right":
                action = "forward_right";
                break;
            case "back_left":
                action = "back_left";
                break;
            case "back_right":
                action = "back_right";
                break;
            case "up":
                action = "up";
                break;
            case "down":
                action = "down";
                break;
            case "forward_up":
                action = "forward_up";
                break;
            case "backward_up":
                action = "backward_up";
                break;
            case "left_up":
                action = "left_up";
                break;
            case "right_up":
                action = "right_up";
                break;
            case "forward_left_up":
                action = "forward_left_up";
                break;
            case "forward_right_up":
                action = "forward_right_up";
                break;
            case "backwards_left_up":
                action = "backwards_left_up";
                break;
            case "backwards_right_up":
                action = "backwards_right_up";
                break;
            case "forward_down":
                action = "forward_down";
                break;
            case "backward_down":
                action = "backward_down";
                break;
            case "left_down":
                action = "left_down";
                break;
            case "right_down":
                action = "right_down";
                break;
            case "forward_left_down":
                action = "forward_left_down";
                break;
            case "forward_right_down":
                action = "forward_right_down";
                break;
            case "backwards_left_down":
                action = "backwards_left_down";
                break;
            case "backwards_right_down":
                action = "backwards_right_down";
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
