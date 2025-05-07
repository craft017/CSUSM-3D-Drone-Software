package com.example.airsimapp;

abstract public class Pattern {
    protected float straightDistance;
    protected float degrees;
    protected boolean firstTurn;
    protected boolean secondTurn;
    protected boolean firstStraight;
    protected boolean secondStraight;

    protected boolean gotHeading;

    public Pattern (){
        this.straightDistance = 0;
        this.degrees = 0;
        setAllFlags(false);
    }

    protected float calculateRadius(float yawRate, float speed){
        return (float) ((180 * speed)/(Math.PI * yawRate));
    }
    public void setAllFlags(boolean value){
        this.firstTurn = value;
        this.secondTurn = value;
        this.firstStraight = value;     //resets booleans
        this.secondStraight = value;
        this.gotHeading = value;
    }
    private void calculateStraightDistance(){
        //Generic function to be overloaded
    }

    public float getStraightDistance() {
        return straightDistance;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setFirstStraight(boolean firstStraight) {
        this.firstStraight = firstStraight;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public void setSecondStraight(boolean secondStraight) {
        this.secondStraight = secondStraight;
    }

    public void setSecondTurn(boolean secondTurn) {
        this.secondTurn = secondTurn;
    }

    public void setGotHeading(boolean gotHeading){ this.gotHeading = gotHeading;}
    public float getRadius(float yaw, float speed){return this.calculateRadius(yaw, speed);}
}
