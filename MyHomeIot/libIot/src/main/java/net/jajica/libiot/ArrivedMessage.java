package net.jajica.libiot;

import java.util.ArrayList;

public class ArrivedMessage {

    public ArrayList<String> listTopics;
    IotMqttConnection.OnArrivedMessage listener;
}
