package com.example.airsimapp;

import java.util.ArrayList;

public class  Autopilot  {
    private final Manual manual;
    private ArrayList<AutopilotCommand> commandQueue;

    public Autopilot(){
        this.manual = new Manual();
        this.commandQueue = new ArrayList<AutopilotCommand>();
    }

    public Manual getManual() {return manual;}

    public ArrayList<AutopilotCommand> getCommandQueue() {return commandQueue;}
}
