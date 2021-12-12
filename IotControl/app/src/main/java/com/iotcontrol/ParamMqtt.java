package com.iotcontrol;

import java.io.Serializable;

public class ParamMqtt implements Serializable {

    public String broker;
    public String puerto;
    public String user;
    public String password;
    public boolean estado;

    ParamMqtt() {
        super();
        broker = "jajicaiot.ddns.org";
        puerto = "1883";
        user = "";
        password = "";
        estado = false;
    }
}
