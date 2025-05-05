package com.example.airsimapp;

public class RaceTrack extends Pattern{
    public RaceTrack(float yawRate, float speed){
        this.calculateStraightDistance(yawRate, speed);
        this.degrees = 180;
        this.setAllFlags(false);
    }
    private void calculateStraightDistance(float yawRate, float speed){
        float r = this.calculateRadius(yawRate, speed);
        this.straightDistance = 2*r;
    }
}
