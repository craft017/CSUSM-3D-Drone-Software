package com.example.airsimapp;
//Class to store current GPS coordinates
public class GPS {
    private float latitude;
    private float longitude;
    private float altitude;

    public GPS(String lat, String lon, String alt){
        this.latitude = Float.parseFloat(lat);
        this.longitude = Float.parseFloat(lon);
        this.altitude = Float.parseFloat(alt);
    }

    public GPS(float lat, float lon, float alt){
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
