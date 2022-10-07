package net.jajica.libiot;

import java.io.Serializable;

public enum ESTADO_DISPOSITIVO implements Serializable {
    NORMAL_AUTO(0),
    NORMAL_AUTOMAN(1),
    NORMAL_MANUAL(2),
    NORMAL_ARRANCANDO(3),
    NORMAL_SIN_PROGRAMACION(4),
    INDETERMINADO(-1);

    private int estadoDispositivo;

    ESTADO_DISPOSITIVO(int estado) {
        this.estadoDispositivo = estado;
    }

    public int getEstadoDispositivo() {

        return this.estadoDispositivo;
    }

    public ESTADO_DISPOSITIVO fromId(int id) {

        for (ESTADO_DISPOSITIVO tipo : values()) {

            if (tipo.getEstadoDispositivo() == id) return tipo;
        }
        return null;
    }


}
