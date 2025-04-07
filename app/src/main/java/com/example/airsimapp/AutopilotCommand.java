package com.example.airsimapp;

abstract public class AutopilotCommand {
    private String id;

    public String getId() {
        return id;
    }
    public String calculateCommand(){
        return "THIS SHOULD NOT BE SEEN";
    }
}
