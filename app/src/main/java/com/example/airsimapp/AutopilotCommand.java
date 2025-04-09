package com.example.airsimapp;

import java.util.ArrayList;

abstract public class AutopilotCommand {
    private String id;
    private ArrayList<String> manualQueue;

    public AutopilotCommand(){
        this.manualQueue = new ArrayList<>();
        this.id = "NULL";
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void calculateCommand(){
        //Generic calculateCommand function
    }
    public void addToManualQueue(String command){
        this.manualQueue.add(command);
    }
}
