package net.jajica.libiot;

public enum ESPONTANEO_IOT {

    ARRANQUE_APLICACION(0),
    ACTUACION_RELE_LOCAL(1),
    ACTUACION_RELE_REMOTO(2),
    UPGRADE_FIRMWARE_FOTA(3),
    CAMBIO_DE_PROGRAMA(4),
    COMANDO_APLICACION(5),
    CAMBIO_TEMPERATURA(6),
    CAMBIO_ESTADO(7),
    RELE_TEMPORIZADO(8),
    INFORME_ALARMA(9),
    CAMBIO_UMBRAL_TEMPERATURA(10),
    CAMBIO_ESTADO_APLICACION(11),
    ERROR(12),

    ESPONTANEO_DESCONOCIDO(-1);


    private int idTipoInforme;

    ESPONTANEO_IOT(int tipoInforme) {
        this.idTipoInforme = tipoInforme;
    }

    public int getIdInforme() {
        return this.idTipoInforme;
    }

    public ESPONTANEO_IOT fromId(int id) {


        for (ESPONTANEO_IOT orden : values()) {

            if (orden.getIdInforme() == id) {

                return orden;
            }

        }
        return null;

    }


}
