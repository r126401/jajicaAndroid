package com.iotcontrol;

import java.io.Serializable;

public class dispositivoIotDesconocido extends dispositivoIot implements Serializable {


    dispositivoIotDesconocido() {

        super();
    }

    dispositivoIotDesconocido(String nombreDispositivo, String idDispositivo, TIPO_DISPOSITIVO_IOT tipo) {
        super(nombreDispositivo, idDispositivo, tipo);

    }

    dispositivoIotDesconocido(dispositivoIot padre) {


        this.nombreDispositivo = padre.nombreDispositivo;
        this.idDispositivo = padre.idDispositivo;
        this.tipoDispositivo = padre.tipoDispositivo;
        this.topicPublicacion = padre.topicPublicacion;
        this.topicSubscripcion = padre.topicSubscripcion;
        this.versionOta = padre.versionOta;

    }

}
