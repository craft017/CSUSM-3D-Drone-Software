package com.example.airsimapp;

import java.util.ArrayList;

abstract public class Pattern {
    private int commandLimit;
    protected ArrayList<String> commands = new ArrayList<String>();

    private float straightTime(float radius, float speed){
        return 0;
    }
    protected float rotationTime(float yawRate){
        float rotationDegrees = 180;    //Make a half circle
        int time = (int) ((rotationDegrees / yawRate)*1000);    //Duration in ms
        return (float) time;
    }
    protected float radiusCalculation(float yawRate, float speed){
        float yawRadians = (float) (yawRate * (Math.PI/180));   //Convert from degrees to radians
        return (float) (speed / (yawRadians * (Math.PI/180)));
    }

    public void loadCommands(){
        //Generic load command function
    }

    public int currentCommandTime(int index, float yawRate, float speed){
        //Generic current command time function
        return 0;
    }

    public int getCommandLimit() {
        return commandLimit;
    }

    public void setCommandLimit(int commandLimit) {
        this.commandLimit = commandLimit;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }
}
