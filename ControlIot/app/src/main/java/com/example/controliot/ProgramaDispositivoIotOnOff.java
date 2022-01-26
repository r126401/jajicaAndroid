package com.example.controliot;

import java.io.Serializable;

public class ProgramaDispositivoIotOnOff extends ProgramaDispositivoIot implements Serializable {


    private ESTADO_RELE estadoRele;
    private int duracion;

    ProgramaDispositivoIotOnOff() {
        super();
        estadoRele = ESTADO_RELE.INDETERMINADO;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.INTERRUPTOR;
        duracion = 0;

    }

    public ESTADO_RELE getEstadoRele() {
        return this.estadoRele;
    }

    public void setEstadoRele(ESTADO_RELE estadoRele) {
        this.estadoRele = estadoRele;
    }




    protected void actualizarProgramaDiario(String programaDiario) {


        super.actualizarProgramaDiario(programaDiario);
        int estadoRel;
        estadoRel = Integer.valueOf(programaDiario.substring(9));
        setEstadoRele(estadoRele.fromId(estadoRel));

    }


    protected void actualizarProgramaFechado(String programa) {

        ESTADO_PROGRAMA estadoProg;
        ESTADO_RELE estadoRel;
        int intermedia;


        super.actualizarProgramaFechado(programa);
        setEstadoRele(estadoRele.fromId(Integer.valueOf(programa.substring(15))));

    }


    public void setIdProgramacion(String idProgramacion) {
        this.idProgramacion = idProgramacion;

        int tipo;

        tipo = Integer.valueOf(this.idProgramacion.substring(0,2));
        this.tipoPrograma = TIPO_PROGRAMA.PROGRAMA_DESCONOCIDO.fromId(tipo);

        switch (this.tipoPrograma) {
            case PROGRAMA_DIARIO:
                actualizarProgramaDiario(idProgramacion.substring(2));
                break;
            case PROGRAMA_SEMANAL:
                break;
            case PROGRAMA_FECHADO:
                actualizarProgramaFechado(idProgramacion.substring(2));
                break;
            case PROGRAMA_DESCONOCIDO:
                break;

        }

        this.idProgramacion = this.idProgramacion.substring(0,this.idProgramacion.length()-2);


    }


    public  void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getDuracion() {

        return this.duracion;
    }

    public int getDuracion(String texto) {


        return 0;

    }





}
