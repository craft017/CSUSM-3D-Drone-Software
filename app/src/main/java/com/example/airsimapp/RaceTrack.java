package com.example.airsimapp;

public class RaceTrack extends Pattern{
    public RaceTrack(float yawRate, float speed){
        this.setCommandLimit(4);
        this.loadCommands(yawRate, speed);
    }
    public void loadCommands(float yawRate, float speed){
        float radius = this.radiusCalculation(yawRate, speed);
        float rotationTime = this.rotationTime(yawRate);
        float straightTime = this.straightTime(radius, speed);
        this.commands.add("autopilot,forward_left," + yawRate + "," + speed + "," + rotationTime);
        this.commands.add("autopilot,forward," + yawRate + "," + speed + "," + straightTime);
        this.commands.add("autopilot,forward_left," + yawRate + "," + speed + "," + rotationTime);
        this.commands.add("autopilot,forward," + yawRate + "," + speed + "," + straightTime);
    }
    public int currentCommandTime(int index, float yawRate, float speed){
        if(index == 0 || index == 2){
            return (int)this.rotationTime(yawRate);
        }
        else if(index == 1 || index == 3){
            return (int)this.straightTime((this.radiusCalculation(yawRate, speed)), speed);
        }
        else{
            return 0;
        }
    }
    private float straightTime(float radius, float speed){
        return (float) (2*radius)/speed;
    }
}
