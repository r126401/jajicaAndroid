package com.example.controliot;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


enum TIPO_DISPOSITIVO_IOT implements Serializable{
    DESCONOCIDO(-1),
    INTERRUPTOR(0),
    TERMOMETRO(2),
    CRONOTERMOSTATO(1),
    SERVIDOR_OTA (100);

    private int tipo;

    TIPO_DISPOSITIVO_IOT(int tipoDispositivo) {

        this.tipo = tipoDispositivo;
    }

    public int getValorTipoDispositivo() {

        return this.tipo;
    }


    public TIPO_DISPOSITIVO_IOT fromId(int id) {


        for (TIPO_DISPOSITIVO_IOT orden : values() ) {

            if (orden.getValorTipoDispositivo() == id) {

                return orden;
            }

        }
        return null;

    }



}

/**
 * Representa el estado del rele del dispositivo
 */

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


/**
 * Representa el estado global del dispositivo
 */
enum ESTADO_DISPOSITIVO implements Serializable{
    NORMAL_AUTO(0),
    NORMAL_AUTOMAN(1),
    NORMAL_MANUAL(2),
    NORMAL_ARRANCANDO(3),
    NORMAL_SIN_PROGRAMACION(4),
    INDETERMINADO(-1);

    private int estadoDispositivo;

    ESTADO_DISPOSITIVO(int estado) {
        this.estadoDispositivo = estado;
    }

    public int getEstadoDispositivo() {

        return this.estadoDispositivo;
    }

    public ESTADO_DISPOSITIVO fromId(int id) {

        for (ESTADO_DISPOSITIVO tipo : values()) {

            if (tipo.getEstadoDispositivo() == id) return tipo;
        }
        return null;
    }



}


/**
 * Representa el estado de programacion global del dispositivo
 */
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

/**
 * Representa el estado de conexion del dispositivo
 */
enum ESTADO_CONEXION_IOT implements Serializable{
    INDETERMINADO,
    CONECTADO,
    DESCONECTADO,
    ESPERANDO_RESPUESTA;
}


public abstract class dispositivoIot extends configuracionDispositivos implements Serializable {



    protected String nombreDispositivo;
    protected String idDispositivo;
    protected TIPO_DISPOSITIVO_IOT tipoDispositivo;
    protected String versionOta;
    protected String topicSubscripcion;
    protected String topicPublicacion;
    protected ESTADO_DISPOSITIVO estadoDispositivo;
    protected ESTADO_CONEXION_IOT estadoConexion;
    protected OtaVersion datosOta;
    protected String programaActivo;
    protected ArrayList<ProgramaDispositivoIot> programas;
    protected int finUpgrade;


    dispositivoIot() {
        super();
        nombreDispositivo = "";
        idDispositivo = "";
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        versionOta = "";
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = ESTADO_CONEXION_IOT.INDETERMINADO;
        topicPublicacion = "";
        topicSubscripcion = "";
        datosOta = new OtaVersion();
        programas = null;
        programaActivo = "ninguno";
        finUpgrade = -1000;


    }


    public int getFinUpgrade() {return finUpgrade;}
    public ArrayList<ProgramaDispositivoIot> getProgramas() {
        return programas;
    }

    public String getProgramaActivo() {
        return programaActivo;
    }

    public String getVersionOta() {
        return versionOta;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public OtaVersion getDatosOta() {
        return datosOta;
    }

    public ESTADO_DISPOSITIVO getEstadoDispositivo() {
        return estadoDispositivo;
    }

    public ESTADO_CONEXION_IOT getEstadoConexion() {
        return estadoConexion;
    }

    public String getTopicPublicacion() {

        return this.topicPublicacion;
    }

    public String getTopicSubscripcion() {
        return this.topicSubscripcion;
    }
/*
    public JSONObject getDatosDispositivos() {

        return datosDispositivos;
    }
*/
    public String getOtaVersion() {

        return this.versionOta;
    }

    public TIPO_DISPOSITIVO_IOT getTipoDispositivo() {

        return this.tipoDispositivo;
    }

    public String getIdDispositivo() {

        return this.idDispositivo;
    }

    public void setFinUpgrade(int upgrade) {
        this.finUpgrade = upgrade;
    }
    public void setTipoDispositivo(TIPO_DISPOSITIVO_IOT tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public void setEstadoDispositivo(ESTADO_DISPOSITIVO estadoDispositivo) {
        this.estadoDispositivo = estadoDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public void setDatosOta(OtaVersion datosOta) {
        this.datosOta = datosOta;
    }

    public void setEstadoConexion(ESTADO_CONEXION_IOT estadoConexion) {
        this.estadoConexion = estadoConexion;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public void setTopicPublicacion(String topicPublicacion) {
        this.topicPublicacion = topicPublicacion;
    }

    public void setTopicSubscripcion(String topicSubscripcion) {
        this.topicSubscripcion = topicSubscripcion;
    }

    public void setProgramas(ArrayList<ProgramaDispositivoIot> programas) {
        this.programas = programas;
    }

    public void setVersionOta(String versionOta) {

        this.versionOta = versionOta;
    }



    public void actualizarProgramaActivo(String idPrograma) {

        //Ponemos a false todos los programas como activos y marcamos solo el actual
        int i;
        int tamano = programas.size();

        for (i=0;i< tamano;i++) {
            if(programas.get(i).getIdProgramacion().equals(idPrograma)) {
                programas.get(i).setProgramaEnCurso(true);
            } else {
                programas.get(i).setProgramaEnCurso(false);
            }
        }

    }

    dispositivoIot(String nombreDispositivo, String idDispositivo, TIPO_DISPOSITIVO_IOT tipo) {

        this.nombreDispositivo = nombreDispositivo;
        this.idDispositivo = idDispositivo;
        this.tipoDispositivo = tipo;
        versionOta = "";
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = ESTADO_CONEXION_IOT.INDETERMINADO;
        topicPublicacion = "/sub_" + idDispositivo;
        topicSubscripcion = "/pub_" + idDispositivo;


    }

    public void setProgramaActivo(String programaActivo) {
        this.programaActivo = programaActivo;


    }


    public boolean guardarDispositivo(Context contexto) {
        return guardarDispositivo(this, contexto);
    }



    /**
     * Redondea un valor double a los decimales que se requieran
     * @param valor
     * @param decimales
     * @return el valor ya redondeado.
     */
    protected double redondearDatos(double valor, int decimales) {




        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);

        return bd.doubleValue();

    }



}
