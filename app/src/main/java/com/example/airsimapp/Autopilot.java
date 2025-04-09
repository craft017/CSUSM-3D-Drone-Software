package com.example.airsimapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class  Autopilot  {
    private final Manual manual;
    private float yawRate;
    private float velocity;
    private float commandTime;
    private ArrayList<AutopilotCommand> commandQueue;
    private GPS currentGPS;
    private float currentHeading;
    private float currentSpeed;
    String currentTime = (Calendar.getInstance().getTime().toString());

    public Autopilot(){
        this.manual = new Manual();
        this.commandQueue = new ArrayList<AutopilotCommand>();
        this.currentGPS = new GPS(0, 0, 0);
        this.currentHeading = 0;
        this.currentSpeed = 0;
        this.yawRate = 15;
        this.velocity = 2;
        this.commandTime = 0.2F;
    }

    public Manual getManual() {return manual;}

    public ArrayList<AutopilotCommand> getCommandQueue() {return commandQueue;}

    public void addToCommandQueue(String lat, String lon, String alt, String time){
        float latitude = Float.parseFloat(lat);
        float longitude = Float.parseFloat(lon);
        float altitude = Float.parseFloat(alt);
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2));
        GPSCommand gps = new GPSCommand(latitude, longitude, altitude, hour, minute);
        this.commandQueue.add(gps);
    }
    public void addToCommandQueue(String heading, String speed, String time){
        float desiredHeading = Float.parseFloat(heading);
        float desiredSpeed = Float.parseFloat(speed);
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2));
        HeadingAndSpeed headingAndSpeed = new HeadingAndSpeed(desiredHeading, desiredSpeed, hour, minute);
        this.commandQueue.add(headingAndSpeed);
    }
    public void addToCommandQueue(String loiterPattern, String time){
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2));
        LoiterPattern pattern = new LoiterPattern(loiterPattern, hour, minute);
        this.commandQueue.add(pattern);
    }

    public float getCurrentHeading() {
        return currentHeading;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getYawRate(){
        return yawRate;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getCommandTime() {return commandTime;}

    public GPS getCurrentGPS() {
        return currentGPS;
    }

    public void setCurrentHeading(float currentHeading) {
        this.currentHeading = this.convertHeadingTo360(currentHeading); //AirSim specific function
    }

    //Use below function if heading from drone goes from 0 to 180, then -180 to 0 (AirSim specific feature)
    private float convertHeadingTo360(float currentHeading){
        float newHeading360;
        if(currentHeading > 0){
            return currentHeading;
        }
        else{
            newHeading360 = (180 + currentHeading) + 180;
        }
        return newHeading360;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setCurrentGPS(GPS currentGPS) {
        this.currentGPS = currentGPS;
    }
}
