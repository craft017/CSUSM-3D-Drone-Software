package com.example.airsimapp;

public interface flightControllerInterface {
    void connect();
    void sendToWebSocket(String jsonCommand);
}