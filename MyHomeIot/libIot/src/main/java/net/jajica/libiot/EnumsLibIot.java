package jajica;

import java.io.Serializable;



enum ESTADO_RELE implements Serializable{
    OFF(0),
    ON(1),
    INDETERMINADO(-1);

    private int estadoRele;

    ESTADO_RELE(int estadoRele) {

        this.estadoRele = estadoRele;

    }

    public int getEstadoRele() {

        return this.estadoRele;
    }

    public ESTADO_RELE fromId(int id) {

        for (ESTADO_RELE tipo : values()) {

            if (tipo.getEstadoRele() == id) return tipo;
        }
        return null;
    }



}



enum ESTADO_PROGRAMACION implements Serializable{
    INVALIDO(0),
    VALIDO(1),
    INHIBIDO(2),
    INDETERMINADO(-1);

    private int estadoProgramacion;

    ESTADO_PROGRAMACION(int estado) {
        this.estadoProgramacion = estado;
    }

    public int getEstadoProgramacion() {
        return this.estadoProgramacion;
    }

    public ESTADO_PROGRAMACION fromId(int id) {

        for (ESTADO_PROGRAMACION tipo : values()) {

            if (tipo.getEstadoProgramacion() == id) return tipo;
        }
        return null;
    }


}


enum OPERACION_DISPOSITIVO {

    DISPOSITIVO_OK,
    NO_EXISTE_DISPOSITIVO,
    EXISTE_DISPOSITIVO

}


