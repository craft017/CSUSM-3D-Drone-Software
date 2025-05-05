package com.example.airsimapp;

public interface flightControllerInterface {
    void connect();
    void sendToDrone(String jsonCommand);

    void setMessageListener(MessageListener listener);

}