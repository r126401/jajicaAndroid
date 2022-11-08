package net.jajica.libiot;

public enum IOT_OTA_VERSION {
    OTA_ERROR(0),
    OTA_DESCARGA_FIRMWARE(1),
    OTA_BORRANDO_SECTORES(2),
    OTA_COPIANDO_SECTORES(3),
    OTA_UPGRADE_FINALIZADO(4),
    OTA_FALLO_CONEXION(5),
    OTA_DATOS_CORRUPTOS(6),
    OTA_PAQUETES_ERRONEOS(7),
    OTA_CRC_ERRONEO(8),
    OTA_ERROR_MEMORIA(9);


    private int idTipoInformeOta;

    IOT_OTA_VERSION(int tipoInforme) {
        this.idTipoInformeOta = tipoInforme;
    }

    public int getIdInforme() {
        return this.idTipoInformeOta;
    }

    public IOT_OTA_VERSION fromId(int id) {


        for (IOT_OTA_VERSION orden : values()) {

            if (orden.getIdInforme() == id) {

                return orden;
            }

        }
        return null;

    }
}
