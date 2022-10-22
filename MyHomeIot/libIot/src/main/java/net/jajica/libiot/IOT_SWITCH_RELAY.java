package net.jajica.libiot;

import java.io.Serializable;

public enum IOT_SWITCH_RELAY implements Serializable {
    OFF(0),
    ON(1),
    UNKNOWN(-1);

    private int estadoRele;

    IOT_SWITCH_RELAY(int estadoRele) {

        this.estadoRele = estadoRele;

    }

    public int getEstadoRele() {

        return this.estadoRele;
    }

    public IOT_SWITCH_RELAY fromId(int id) {

        for (IOT_SWITCH_RELAY tipo : values()) {

            if (tipo.getEstadoRele() == id) return tipo;
        }
        return null;
    }


}
