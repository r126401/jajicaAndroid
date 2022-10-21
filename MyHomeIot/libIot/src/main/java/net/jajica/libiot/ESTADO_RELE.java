package net.jajica.libiot;

import java.io.Serializable;

public enum ESTADO_RELE implements Serializable {
    OFF(0),
    ON(1),
    INDETERMINADO(-1);

    private int estadoRele;

    ESTADO_RELE(int estadoRele) {

        this.estadoRele = estadoRele;

    }

    public int getEstadoRele() {

        return this.estadoRele;
    }

    public ESTADO_RELE fromId(int id) {

        for (ESTADO_RELE tipo : values()) {

            if (tipo.getEstadoRele() == id) return tipo;
        }
        return null;
    }


}
