package net.jajica.libiot;

public enum IOT_SCHEDULE_CONDITION {
    INVALID_PROG(0),
    VALID_PROG(1),
    INH_PROG(2);

    private int estado;

    IOT_SCHEDULE_CONDITION(int estado) {
        this.estado = estado;
    }

    public int getScheduleState() {

        return this.estado;
    }

    public IOT_SCHEDULE_CONDITION fromId(int id) {

        for (IOT_SCHEDULE_CONDITION tipo : values()) {

            if (tipo.getScheduleState() == id) return tipo;
        }
        return null;
    }



    }
