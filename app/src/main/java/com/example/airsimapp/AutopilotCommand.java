package com.example.airsimapp;

import java.util.ArrayList;

abstract public class AutopilotCommand {
    private String id;
    private ArrayList<String> manualQueue;
    private int hourEndTime;
    private int minuteEndTime;

    public AutopilotCommand(){
        this.manualQueue = new ArrayList<>();
        this.id = "NULL";
        this.hourEndTime = 0;
        this.minuteEndTime = 0;
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

    public void setHourEndTime(int hourEndTime) {
        this.hourEndTime = hourEndTime;
    }

    public void setMinuteEndTime(int minuteEndTime) {
        this.minuteEndTime = minuteEndTime;
    }

    public int getHourEndTime() {
        return hourEndTime;
    }

    public int getMinuteEndTime() {
        return minuteEndTime;
    }
}
