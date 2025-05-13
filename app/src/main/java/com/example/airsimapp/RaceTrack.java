package com.example.airsimapp;

import java.util.Calendar;

public class RaceTrack extends Pattern{
    public RaceTrack(float yawRate, float speed){
        this.calculateStraightDistance(yawRate, speed);
        this.degrees = 180;
        this.setAllFlags(false);    //set flags to false by default
    }
    private void calculateStraightDistance(float yawRate, float speed){
        float r = this.calculateRadius(yawRate, speed);
        this.straightDistance = 2*r;
    }

    public String executePattern(float currentHeading, float yawRate, float speed, float commandTime, Calendar startTime, float headingTolerance) {
        if (!this.gotHeading) {
            desiredFirstTurn = (currentHeading + this.getDegrees()) % 360;
            desiredSecondTurn = (desiredFirstTurn + 180) % 360;
            firstLowerHeading = ((desiredFirstTurn - headingTolerance) % 360);
            firstUpperHeading = ((desiredFirstTurn + headingTolerance) % 360);
            this.setGotHeading(true);
        }


        if (!this.firstTurn && (currentHeading >= firstUpperHeading || currentHeading <= firstLowerHeading)) {
            return "autopilot,forward_left," + yawRate + "," + speed + "," + commandTime;
        } else if (!this.firstTurn && (currentHeading <= firstUpperHeading || currentHeading >= firstLowerHeading)) {
            this.setFirstTurn(true);
        } else if (!this.firstStraight && forwardCounter < (this.getRadius(yawRate, speed) / speed) * 10) {
            forwardCounter++;
            return "autopilot,forward," + yawRate + "," + speed + "," + commandTime;
        } else if (!this.firstStraight && (forwardCounter >= (this.getRadius(yawRate, speed) / speed) * 10)) {
            this.setFirstStraight(true);
            forwardCounter = 0;
            secondLowerHeading = ((desiredSecondTurn - headingTolerance) % 360);
            secondUpperHeading = ((desiredSecondTurn + headingTolerance) % 360);
        } else if (!this.secondTurn && (currentHeading >= secondUpperHeading || currentHeading <= secondLowerHeading)) {
            return "autopilot,forward_left," + yawRate + "," + speed + "," + commandTime;
        } else if (!this.secondTurn && (currentHeading <= secondUpperHeading || currentHeading >= secondLowerHeading)) {
            this.setSecondTurn(true);
        } else if (!this.secondStraight && (forwardCounter < (this.getRadius(yawRate, speed) / speed) * 10)) {
            forwardCounter++;
            return ("autopilot,forward," + yawRate + "," + speed + "," + commandTime);
        } else if (forwardCounter >= (this.getRadius(yawRate, speed) / speed) * 10) {
            this.setSecondStraight(true);
            forwardCounter = 0;
        } else {
            this.setAllFlags(false);

        }
        return("autopilot,stop," + yawRate + "," + speed + "," + commandTime);
    }
}
