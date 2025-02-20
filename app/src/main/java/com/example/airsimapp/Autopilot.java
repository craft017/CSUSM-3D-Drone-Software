package com.example.airsimapp;

public class  Autopilot {
    private final Manual manual;

    public Autopilot(){
        this.manual = new Manual();
    }
    public Manual getManual() {
        return manual;
    }
}
