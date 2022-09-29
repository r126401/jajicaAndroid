package jajica;


import org.json.JSONObject;

import java.util.ArrayList;


public class DispositivoIot {


    /**
     * Nombre del dispositivo
     */
        protected String nombreDispositivo;
    /**
     * Identidad del dispositivo
     */
    protected String idDispositivo;
    /**
     * Tipo de dispositivo
     */
        protected TIPO_DISPOSITIVO_IOT tipoDispositivo;

    /**
     * Version del dispositivo.
     * La logica de version que se aplica es AAAAMMDDmmss
     */
    protected String versionOntaDisponible;

    /**
     *  Topic usado por la aplicacion para conectarse la topic de suscripcion del dispositivo
     */
    protected String topicSubscripcion;

    /**
     * Topic usado por la aplicacion para conectarse al topic de publicacion del dispositivo
     */
    protected String topicPublicacion;

    /**
     * Estado del dispositivo
     */
    protected ESTADO_DISPOSITIVO estadoDispositivo;

    /**
     * Estado de la conexion al dispositivo
     */
    protected ESTADO_CONEXION_IOT estadoConexion;

    /**
     * Puntero a la estructura de la version reportada por el dispositivo
     */
    protected OtaVersion datosOta;
        protected String programaActivo;
        protected ArrayList<ProgramaDispositivoIot> programas;
        protected int finUpgrade;

        protected JSONObject dispositivoJson;

    public JSONObject getDispositivoJson() {
        return dispositivoJson;
    }

    public void setDispositivoJson(JSONObject dispositivoJson) {
        this.dispositivoJson = dispositivoJson;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public TIPO_DISPOSITIVO_IOT getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(TIPO_DISPOSITIVO_IOT tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public void setTipoDispositivo(int tipo) {

        this.tipoDispositivo = this.tipoDispositivo.fromId(tipo);

    }

    public String getVersionOntaDisponible() {
        return versionOntaDisponible;
    }

    public void setVersionOntaDisponible(String versionOntaDisponible) {
        this.versionOntaDisponible = versionOntaDisponible;
    }

    public String getTopicSubscripcion() {
        return topicSubscripcion;
    }

    public void setTopicSubscripcion(String topicSubscripcion) {
        this.topicSubscripcion = topicSubscripcion;
    }

    public String getTopicPublicacion() {
        return topicPublicacion;
    }

    public void setTopicPublicacion(String topicPublicacion) {
        this.topicPublicacion = topicPublicacion;
    }

    public ESTADO_DISPOSITIVO getEstadoDispositivo() {
        return estadoDispositivo;
    }

    public void setEstadoDispositivo(ESTADO_DISPOSITIVO estadoDispositivo) {
        this.estadoDispositivo = estadoDispositivo;
    }

    public ESTADO_CONEXION_IOT getEstadoConexion() {
        return estadoConexion;
    }

    public void setEstadoConexion(ESTADO_CONEXION_IOT estadoConexion) {
        this.estadoConexion = estadoConexion;
    }

    public OtaVersion getDatosOta() {
        return datosOta;
    }

    public void setDatosOta(OtaVersion datosOta) {
        this.datosOta = datosOta;
    }

    public String getProgramaActivo() {
        return programaActivo;
    }

    public void setProgramaActivo(String programaActivo) {
        this.programaActivo = programaActivo;
    }

    public ArrayList<ProgramaDispositivoIot> getProgramas() {
        return programas;
    }

    public void setProgramas(ArrayList<ProgramaDispositivoIot> programas) {
        this.programas = programas;
    }

    public int getFinUpgrade() {
        return finUpgrade;
    }

    public void setFinUpgrade(int finUpgrade) {
        this.finUpgrade = finUpgrade;
    }

    /**
     *
     */
    DispositivoIot() {

        nombreDispositivo = null;
        idDispositivo = null;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        versionOntaDisponible = null;
        topicSubscripcion = null;
        topicPublicacion = null;
        datosOta = null;
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = ESTADO_CONEXION_IOT.INDETERMINADO;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();

    }

    DispositivoIot(String nombreDispositivo, String idDispositivo, int tipo) {


        this.nombreDispositivo = nombreDispositivo;
        this.idDispositivo = idDispositivo;
        setTipoDispositivo(tipo);
        versionOntaDisponible = null;
        topicSubscripcion = null;
        topicPublicacion = null;
        datosOta = null;
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = ESTADO_CONEXION_IOT.INDETERMINADO;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();

    }

    public OPERACION_JSON json2DispositivoIot(JSONObject dispositivoJson) {

        int tipo;
        nombreDispositivo = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson());
        idDispositivo = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        topicPublicacion = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson());
        topicSubscripcion = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson());
        tipo = dispositivoJson.getInt(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        tipoDispositivo = tipoDispositivo.fromId(tipo);

        return OPERACION_JSON.JSON_OK;
    }

    /**
     * Este método construye un objeto json a partir del dispositivo
     * @return
     */
    public JSONObject dispositivo2Json() {



        if (nombreDispositivo != null) {
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson(), getNombreDispositivo());
        }

        if (idDispositivo != null) {
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), getIdDispositivo());
        }

        if(topicSubscripcion != null) {
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson(), getTopicSubscripcion());
        }


        if (topicPublicacion != null) {
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson(), getTopicPublicacion());
        }
        dispositivoJson.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), getTipoDispositivo().getValorTipoDispositivo());

        return dispositivoJson;


    }




}