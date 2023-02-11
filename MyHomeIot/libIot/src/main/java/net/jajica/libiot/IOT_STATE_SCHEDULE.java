package net.jajica.libiot;

public enum IOT_STATE_SCHEDULE {
    UNKNOWN_SCHEDULE(-1),
    INACTIVE_SCHEDULE(0),
    ACTIVE_SCHEDULE(1),
    INVALID_SCHEDULE(-2),
    VALID_SCHEDULE(2);

    private int estadoPrograma;

    IOT_STATE_SCHEDULE(int estadoPrograma) {
        this.estadoPrograma = estadoPrograma;
    }

    public int getEstadoPrograma() {
        return this.estadoPrograma;
    }

    public IOT_STATE_SCHEDULE fromId(int id) {

        for (IOT_STATE_SCHEDULE orden : values()) {
            if (orden.getEstadoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }

}
