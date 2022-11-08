package net.jajica.libiot;

import java.io.Serializable;

public enum IOT_DEVICE_TYPE implements Serializable {
    DESCONOCIDO(-1),
    INTERRUPTOR(0),
    TERMOMETRO(2),
    CRONOTERMOSTATO(1),
    SERVIDOR_OTA(100);

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

