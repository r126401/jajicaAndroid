package com.example.controliot;

import java.io.Serializable;

enum ESTADO_PROGRAMA {
    PROGRAMA_DESCONOCIDO(-1),
    PROGRAMA_INACTIVO(0),
    PROGRAMA_ACTIVO(1);

    private int estadoPrograma;

    ESTADO_PROGRAMA (int estadoPrograma) { this.estadoPrograma = estadoPrograma;}

    public int getEstadoPrograma() {
        return this.estadoPrograma;
    }

    public ESTADO_PROGRAMA fromId(int id) {

        for (ESTADO_PROGRAMA orden : values()) {
            if (orden.getEstadoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }

}

enum TIPO_PROGRAMA {
    PROGRAMA_DESCONOCIDO(-1),
    PROGRAMA_DIARIO(0),
    PROGRAMA_SEMANAL(1),
    PROGRAMA_FECHADO(2);

    private int estadoPrograma;

    TIPO_PROGRAMA (int tipoPrograma) { this.estadoPrograma = tipoPrograma;}

    public int getTipoPrograma() {
        return this.estadoPrograma;
    }

    public TIPO_PROGRAMA fromId(int id) {

        for (TIPO_PROGRAMA orden : values()) {
            if (orden.getTipoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }

}


public class ProgramaDispositivoIot implements Serializable {

    protected String idProgramacion;
    protected TIPO_PROGRAMA tipoPrograma;
    protected int ano;
    protected int mes;
    protected int dia;
    protected int hora;
    protected int minuto;
    protected int segundo;
    protected int diaSemana;
    protected ESTADO_PROGRAMA estadoPrograma;
    protected int mascara;
    protected TIPO_DISPOSITIVO_IOT tipoDispositivo;
    protected boolean[] diasActivos;
    protected boolean programaEnCurso;
    protected int duracion;

    ProgramaDispositivoIot() {

        idProgramacion = null;
        tipoPrograma = TIPO_PROGRAMA.PROGRAMA_DESCONOCIDO;
        hora = 0;
        minuto = 0;
        segundo = 0;
        diaSemana = 0;
        estadoPrograma = ESTADO_PROGRAMA.PROGRAMA_DESCONOCIDO;
        mascara = 0;
        ano = 0;
        mes = 0;
        dia = 0;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        diasActivos = new boolean[7];
        programaEnCurso = false;
    }


    public String generarIdPrograma() {


        String idPrograma;

        switch (this.getTipoPrograma()) {

            case PROGRAMA_DIARIO:
                idPrograma = "00" + getHora() + getMinuto() + getSegundo() + getEstadoPrograma();
                break;
            case PROGRAMA_SEMANAL:
                break;
            case PROGRAMA_FECHADO:
                idPrograma = "02";
                break;
            case PROGRAMA_DESCONOCIDO:
                break;
                default:
                    break;


        }

        return null;
    }

    public boolean getProgramaEnCurso() {
        return this.programaEnCurso;
    }

    public String getIdProgramacion() {
        return idProgramacion;
    }

    public TIPO_PROGRAMA getTipoPrograma() {
        return tipoPrograma;
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public int getHora() {
        return this.hora;
    }

    public int getMinuto() {
        return this.minuto;
    }

    public int getSegundo() {
        return segundo;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public ESTADO_PROGRAMA getEstadoPrograma() {
        return estadoPrograma;
    }


    public int getMascara() {
        return mascara;
    }

    public TIPO_DISPOSITIVO_IOT getTipoDispositivo() {
        return tipoDispositivo;
    }

    public boolean[] getDiasActivos() {
        return this.diasActivos;
    }

    public boolean getDiaActivo(int i) {

        return diasActivos[i];

    }

    //-----------------------------------------------------------



    public void setTipoPrograma(TIPO_PROGRAMA tipoPrograma) {
        this.tipoPrograma = tipoPrograma;
    }

    public void setIdProgramacion(String idProgramacion) {
        this.idProgramacion = idProgramacion;
    }

    public void setDiasActivos(boolean[] diasActivos) {
        this.diasActivos = diasActivos;
    }


    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public void setSegundo(int segundo) {
        this.segundo = segundo;
    }

    public void setDiaSemana(int diaSemana) {
        this.diaSemana = diaSemana;
    }

    public void setEstadoPrograma(ESTADO_PROGRAMA estadoPrograma) {
        this.estadoPrograma = estadoPrograma;
    }


    public void setMascara(int mascara) {
        this.mascara = mascara;
    }

    public void setTipoDispositivo(TIPO_DISPOSITIVO_IOT tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public void setProgramaEnCurso(boolean programaEnCurso) {
        this.programaEnCurso = programaEnCurso;
    }

    public void setProgramaEnCurso(String programa) {

        if(programa.equals( this.idProgramacion)) {
            this.programaEnCurso = true;
        } else {
            this.programaEnCurso = false;
        }
    }

    //------------------------------------


    /**
     * Esta funcion extrae los parametros comunes del programa.
     * @param programa
     */
    protected void parteComunPrograma(String programa) {

        setHora(Integer.valueOf(programa.substring(0,2)));
        setMinuto(Integer.valueOf(programa.substring(2,4)));
        setSegundo(Integer.valueOf(programa.substring(4,6)));


    }

    /**
     * Extrae los datos del array y los pasa al objeto para el programa diario
     * offset 0: hora
     * offset 2: minutos
     * offset 4: segundos
     * offset 6 mascara
     * offset 7: Estado del programa
     * offset 8: Estado del rele
     *
     * @param programaDiario
     */
    protected void actualizarProgramaDiario(String programaDiario) {

        String mask;
        int estadoProg;
        int estadoRel;
        ESTADO_PROGRAMA estado = ESTADO_PROGRAMA.PROGRAMA_DESCONOCIDO;
        parteComunPrograma(programaDiario);
        mask = "0x" + programaDiario.substring(6,8);
        setMascara((Integer.decode(mask)).intValue());
        actualizarDiasActivos();
        estadoProg = Integer.valueOf(programaDiario.substring(8,9));
        setEstadoPrograma(estado.fromId(estadoProg));





    }


    /**
     * Esta funcion lee la mascara de programa y coloca true en el dia de la semana en el que
     * hay programa activo.
     */
    protected void actualizarDiasActivos() {

        int i;
        double comparador;
        int valor;
        for(i=0;i<7;i++) {
            comparador = Math.pow(2,i);
            valor = this.mascara & (int) comparador;
            if (valor == comparador) {
                this.diasActivos[i] = true;
            } else {
                this.diasActivos[i] = false;
            }

        }


    }


    /**
     * Extrae los datos del array y los pasa al objeto para el programa diario
     * offset 0: hora
     * offset 2: minutos
     * offset 4: segundos
     * offset 6 Año.
     * offset 10: Mes
     * offset 12: dia
     * offset 14: Estado del programa
     *
     * @param programa
     */
    protected void actualizarProgramaFechado(String programa) {

        ESTADO_PROGRAMA estadoProg;
        estadoProg = ESTADO_PROGRAMA.PROGRAMA_DESCONOCIDO;
        parteComunPrograma(programa);
        setAno(Integer.valueOf(programa.substring(6,10)));
        setMes(Integer.valueOf(programa.substring(10,12)));
        setDia(Integer.valueOf(programa.substring(12,14)));
        setEstadoPrograma(estadoProg.fromId(Integer.valueOf(programa.substring(14,15))));





    }

    public  void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getDuracion() {

        return this.duracion;
    }
}
