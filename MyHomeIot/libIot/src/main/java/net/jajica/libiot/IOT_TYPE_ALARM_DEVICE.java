package net.jajica.libiot;

import java.io.Serializable;

public enum IOT_TYPE_ALARM_DEVICE implements Serializable {
    UNKNOWN_ALARM(-1),
    WIFI_ALARM(0),
    MQTT_ALARM(1),
    NTP_ALARM(2),
    NVS_ALARM(3),
    SENSOR_ALARM(4),
    REMOTE_SENSOR_ALARM(5);

    private int alarma;

    IOT_TYPE_ALARM_DEVICE(int estadoPrograma) {
        this.alarma = estadoPrograma;
    }

    public int getEstadoPrograma() {
        return this.alarma;
    }

    public IOT_TYPE_ALARM_DEVICE fromId(int id) {

        for (IOT_TYPE_ALARM_DEVICE orden : values()) {
            if (orden.getEstadoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }
}
