package net.jajica.libiot;

public enum SCHEDULE_STATE {
    INVALID_PROG(0),
    VALID_PROG(1),
    INH_PROG(2);

    private int estado;

    SCHEDULE_STATE(int estado) {
        this.estado = estado;
    }

    public int getScheduleState() {

        return this.estado;
    }

    public SCHEDULE_STATE fromId(int id) {

        for (SCHEDULE_STATE tipo : values()) {

            if (tipo.getScheduleState() == id) return tipo;
        }
        return null;
    }



    }
