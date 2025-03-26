package com.example.airsimapp;

import java.util.ArrayList;

public class  Autopilot  {
    private final Manual manual;
    private float yawRate;
    private float velocity;
    private ArrayList<AutopilotCommand> commandQueue;
    private GPS currentGPS;
    private float currentHeading;
    private float currentSpeed;

    public Autopilot(){
        this.manual = new Manual();
        this.commandQueue = new ArrayList<AutopilotCommand>();
        this.currentGPS = new GPS(0, 0, 0);
        this.currentHeading = 0;
        this.currentSpeed = 0;
        this.yawRate = 15;
        this.velocity = 2;
    }

    public Manual getManual() {return manual;}

    public ArrayList<AutopilotCommand> getCommandQueue() {return commandQueue;}

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
