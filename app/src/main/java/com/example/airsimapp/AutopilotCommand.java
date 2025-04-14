package com.example.airsimapp;

import java.util.ArrayList;

abstract public class AutopilotCommand {
    private String id;
    private int hourEndTime;
    private int minuteEndTime;
    private String commandMessage;
    private float headingTolerance;

    public AutopilotCommand(){
        this.id = "NULL";
        this.hourEndTime = 0;
        this.minuteEndTime = 0;
        this.headingTolerance = 2;
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

    public String getCommandMessage() {
        return commandMessage;
    }

    public void setCommandMessage(String commandMessage) {
        this.commandMessage = commandMessage;
    }

    public float getHeadingTolerance() {
        return headingTolerance;
    }
}
