package com.example.controliot;

import java.io.Serializable;

public class ProgramaDispositivoIotTermostato extends ProgramaDispositivoIot implements Serializable {


    private ESTADO_RELE estadoRele;
    private double umbralTemperatura;

    ProgramaDispositivoIotTermostato() {
        super();
        estadoRele = ESTADO_RELE.ON;
        umbralTemperatura = -100;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO;

    }

    public ESTADO_RELE getEstadoRele() {
        return estadoRele;
    }

    public void setEstadoRele(ESTADO_RELE estadoRele) {
        this.estadoRele = estadoRele;
    }

    public double getUmbralTemperatura() {
        return umbralTemperatura;
    }

    //-----------------------------------------------------------


    public void setUmbralTemperatura(double umbralTemperatura) {
        this.umbralTemperatura = umbralTemperatura;
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

}
