package net.jajica.libiot;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

enum ESTADO_CONEXION_IOT implements Serializable{
    INDETERMINADO,
    CONECTADO,
    DESCONECTADO,
    ESPERANDO_RESPUESTA;
}

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
    public DispositivoIot() {

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
        try {
            nombreDispositivo = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson());
            idDispositivo = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
            topicPublicacion = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson());
            topicSubscripcion = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson());
            tipo = dispositivoJson.getInt(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
            tipoDispositivo = tipoDispositivo.fromId(tipo);
        } catch (JSONException e) {
            return OPERACION_JSON.JSON_CORRUPTO;
        }

        if (!dispositivoValido()) {
            return OPERACION_JSON.JSON_CORRUPTO;
        }
        setDispositivoJson(dispositivoJson);

        return OPERACION_JSON.JSON_OK;
    }

    /**
     * Este método construye un objeto json a partir del dispositivo para la insercion en la estructura de configuracion
     * de la aplicacion.
     * @return
     */
    public JSONObject dispositivo2Json() {



        if (nombreDispositivo != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson(), getNombreDispositivo());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (idDispositivo != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), getIdDispositivo());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        if(topicSubscripcion != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson(), getTopicSubscripcion());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }


        if (topicPublicacion != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson(), getTopicPublicacion());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), getTipoDispositivo().getValorTipoDispositivo());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return dispositivoJson;


    }

    private Boolean chequearNulos(String valor) {

        if ((valor == null) || valor.equals("")) {
            return false;
        } else {
            return true;
        }


    }

    public Boolean dispositivoValido() {

        if(!chequearNulos(nombreDispositivo)) return false;
        if(!chequearNulos(idDispositivo)) return false;
        if(!chequearNulos(topicPublicacion)) return false;
        if(!chequearNulos(topicSubscripcion)) return false;

        return true;

    }

    /**
     * Este metodo cambia los valores mas relevantes de un dispositivo
     * @param dispositivo
     * @return
     */
    public Boolean modificarDispositivo(DispositivoIot dispositivo) {

        if (!dispositivo.dispositivoValido()) {
            return false;
        }
        this.setNombreDispositivo(dispositivo.getNombreDispositivo());
        this.setIdDispositivo(dispositivo.getIdDispositivo());
        this.setTipoDispositivo(dispositivo.getTipoDispositivo());
        this.setTopicPublicacion(dispositivo.getTopicPublicacion());
        this.setTopicSubscripcion(dispositivo.getTopicSubscripcion());
        return true;

    }




}