package net.jajica.libiot;

public enum IOT_CLASS_SCHEDULE {
    UNKNOWN_SCHEDULE(-1),
    DIARY_SCHEDULE(0),
    WEEKLY_SCHEDULE(1),
    DATE_SCHEDULE(2);

    private int estadoPrograma;

    IOT_CLASS_SCHEDULE(int tipoPrograma) {
        this.estadoPrograma = tipoPrograma;
    }

    public int getTipoPrograma() {
        return this.estadoPrograma;
    }

    public IOT_CLASS_SCHEDULE fromId(int id) {

        for (IOT_CLASS_SCHEDULE orden : values()) {
            if (orden.getTipoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }

}

