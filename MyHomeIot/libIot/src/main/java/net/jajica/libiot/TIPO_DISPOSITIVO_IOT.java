package net.jajica.libiot;

import java.io.Serializable;

public enum TIPO_DISPOSITIVO_IOT implements Serializable {
    DESCONOCIDO(-1),
    INTERRUPTOR(0),
    TERMOMETRO(2),
    CRONOTERMOSTATO(1),
    SERVIDOR_OTA(100);

    private int tipo;

    TIPO_DISPOSITIVO_IOT(int tipoDispositivo) {

        this.tipo = tipoDispositivo;
    }

    public int getValorTipoDispositivo() {

        return this.tipo;
    }


    public TIPO_DISPOSITIVO_IOT fromId(int id) {


        for (TIPO_DISPOSITIVO_IOT orden : values()) {

            if (orden.getValorTipoDispositivo() == id) {

                return orden;
            }

        }
        return null;

    }


}
