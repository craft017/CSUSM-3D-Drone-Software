package com.example.airsimapp;

public class  Autopilot  {
    private final Manual manual;
    private GPS gps;

    public Autopilot(){
        this.manual = new Manual();
        this.gps = new GPS();
    }

    public Manual getManual() {
        return manual;
    }

    public GPS getGps() {
        return gps;
    }
}
