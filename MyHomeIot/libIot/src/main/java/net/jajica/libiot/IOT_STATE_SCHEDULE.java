package net.jajica.libiot;

public enum IOT_STATE_SCHEDULE {
    PROGRAMA_DESCONOCIDO(-1),
    PROGRAMA_INACTIVO(0),
    PROGRAMA_ACTIVO(1),
    PROGRAMA_INVALIDO(-2),
    PROGRAMA_VALIDO(2);

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
