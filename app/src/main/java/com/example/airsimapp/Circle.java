package com.example.airsimapp;

import java.util.Calendar;

public class Circle extends Pattern {
    public Circle(float yawRate, float speed){

    }

    public String executePattern(float yawRate, float speed, float commandTime, Calendar startTime){
        return "autopilot,forward_right," + yawRate + "," + speed + "," + commandTime;
    }
}
