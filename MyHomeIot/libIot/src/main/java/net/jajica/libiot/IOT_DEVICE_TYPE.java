package net.jajica.libiot;

import java.io.Serializable;

public enum IOT_DEVICE_TYPE implements Serializable {
    UNKNOWN(-1),
    SWITCH(0),
    THERMOMETER(1),
    CHRONOTHERMOSTAT(2),
    OTA_SERVER(100);

    private int tipo;

    IOT_DEVICE_TYPE(int tipoDispositivo) {

        this.tipo = tipoDispositivo;
    }

    public int getValorTipoDispositivo() {

        return this.tipo;
    }


    public IOT_DEVICE_TYPE fromId(int id) {


        for (IOT_DEVICE_TYPE orden : values()) {

            if (orden.getValorTipoDispositivo() == id) {

                return orden;
            }

        }
        return null;

    }


}

