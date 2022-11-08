package net.jajica.libiot;

public enum IOT_COMMANDS {
    INFO_DEVICE(0),
    SET_RELAY(50),
    STATUS_DEVICE(51),
    GET_SCHEDULE(6),
    NEW_SCHEDULE(7),
    REMOVE_SCHEDULE(9),
    MODIFY_SCHEDULE(8),
    MODIFY_PARAMETER_DEVICE(12),
    RESET(10),
    FACTORY_RESET(11),
    MODIFY_CLOCK(15),
    UPGRADE_FIRMWARE(26),
    MODIFY_THRESHOLD_TEMPERATURE(52),
    SELECT_TEMPERATURE_SENSOR(53),
    SPONTANEOUS(-1),
    GET_OTA_VERSION_AVAILABLE(100),
    ERROR_REPORT(-100);

    private int idComando;

    IOT_COMMANDS(int idComando) {
        this.idComando = idComando;
    }

    public int getIdComando() {

        return this.idComando;
    }

    public IOT_COMMANDS fromId(int id) {


        for (IOT_COMMANDS orden : values()) {

            if (orden.getIdComando() == id) {

                return orden;
            }

        }
        return null;

    }
}
