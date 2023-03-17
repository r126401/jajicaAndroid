package net.jajica.libiot;

import java.io.Serializable;

public enum IOT_DEVICE_STATUS implements Serializable {
    NORMAL_AUTO(0),
    NORMAL_AUTOMAN(1),
    NORMAL_MANUAL(2),
    NORMAL_ARRANCANDO(3),
    NORMAL_SIN_PROGRAMACION(4),
    UPGRADE_IN_PROGRESS(5),
    NORMAL_SYNCHRONIZING(6),
    WAITING_END_START(7),
    START_BEFORE_FACTORY(8),
    NORMAL_END_ACTIVE_SCHEDULE(9),
    INDETERMINADO(-1),
    CUTTING_DEVICE(10),
    PASTE_DEVICE(11);

    private int estadoDispositivo;

    IOT_DEVICE_STATUS(int estado) {
        this.estadoDispositivo = estado;
    }

    public int getDeviceState() {

        return this.estadoDispositivo;
    }

    public IOT_DEVICE_STATUS fromId(int id) {

        for (IOT_DEVICE_STATUS tipo : values()) {

            if (tipo.getDeviceState() == id) return tipo;
        }
        return null;
    }


}
