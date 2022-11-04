package net.jajica.libiot;

public enum IOT_SPONTANEOUS_TYPE {

    START_DEVICE(0),
    ACTUACION_RELE_LOCAL(1),
    ACTUACION_RELE_REMOTO(2),
    UPGRADE_FIRMWARE_FOTA(3),
    START_SCHEDULE(4),
    COMANDO_APLICACION(5),
    CAMBIO_TEMPERATURA(6),
    CAMBIO_ESTADO(7),
    END_SCHEDULE(8),
    INFORME_ALARMA(9),
    CAMBIO_UMBRAL_TEMPERATURA(10),
    CAMBIO_ESTADO_APLICACION(11),
    ERROR(12),

    ESPONTANEO_DESCONOCIDO(-1);


    private int idTipoInforme;

    IOT_SPONTANEOUS_TYPE(int tipoInforme) {
        this.idTipoInforme = tipoInforme;
    }

    public int getIdInforme() {
        return this.idTipoInforme;
    }

    public IOT_SPONTANEOUS_TYPE fromId(int id) {


        for (IOT_SPONTANEOUS_TYPE orden : values()) {

            if (orden.getIdInforme() == id) {

                return orden;
            }

        }
        return null;

    }


}
