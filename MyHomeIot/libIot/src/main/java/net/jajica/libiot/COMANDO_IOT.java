package net.jajica.libiot;

public enum COMANDO_IOT {
    CONSULTAR_CONF_APP(0),
    ACTUAR_RELE(50),
    ESTADO(51),
    CONSULTAR_PROGRAMACION(6),
    NUEVA_PROGRAMACION(7),
    ELIMINAR_PROGRAMACION(9),
    MODIFICAR_PROGRAMACION(8),
    MODIFICAR_APP(12),
    RESET(10),
    FACTORY_RESET(11),
    MODIFY_CLOCK(15),
    UPGRADE_FIRMWARE(26),
    MODIFICAR_UMBRAL_TEMPERATURA(52),
    SELECCIONAR_SENSOR_TEMPERATURA(53),
    ESPONTANEO(-1),
    VERSION_OTA(100),
    ERROR_RESPUESTA(-100);

    private int idComando;

    COMANDO_IOT(int idComando) {
        this.idComando = idComando;
    }

    public int getIdComando() {

        return this.idComando;
    }

    public COMANDO_IOT fromId(int id) {


        for (COMANDO_IOT orden : values()) {

            if (orden.getIdComando() == id) {

                return orden;
            }

        }
        return null;

    }
}
