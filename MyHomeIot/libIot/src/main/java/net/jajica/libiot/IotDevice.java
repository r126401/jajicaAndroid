package net.jajica.libiot;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Esta es la clase base para la gestion de todos los dispositivos que se comunican con la aplicacion.
 * En ella se gestiona todo lo relativo al dispositivo
 */


public class IotDevice {

    protected final String TAG = "IotDevice";

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
    protected DEVICE_STATE_CONNECTION estadoConexion;

    protected ConexionMqtt cnx;

    public ConexionMqtt getCnx() {
        return cnx;
    }

    public void setCnx(ConexionMqtt cnx) {
        this.cnx = cnx;
    }

    /**
     * Puntero a la estructura de la version reportada por el dispositivo
     */
    protected OtaVersion datosOta;
    protected String currentOtaVersion;
    protected String programaActivo;
    protected int finUpgrade;
    protected JSONObject dispositivoJson;
    protected ArrayList<ProgramaDispositivoIot> programs;
    protected SCHEDULE_STATE ScheduleState;
    protected Timer timerCommand;
    protected OnErrorAnswerDevice onErrorAnswerDevice;

    public OnErrorAnswerDevice getOnErrorAnswerDevice() {
        return onErrorAnswerDevice;
    }

    public void setOnErrorAnswerDevice(OnErrorAnswerDevice onErrorAnswerDevice) {
        this.onErrorAnswerDevice = onErrorAnswerDevice;
    }

    public SCHEDULE_STATE getScheduleState() {
        return ScheduleState;
    }

    public void setScheduleState(SCHEDULE_STATE scheduleState) {
        ScheduleState = scheduleState;
    }

    public ArrayList<ProgramaDispositivoIot> getPrograms() {
        return programs;
    }

    public void setPrograms(ArrayList<ProgramaDispositivoIot> programs) {
        this.programs = programs;
    }

    public String getCurrentOtaVersion() {
        return currentOtaVersion;
    }

    public void setCurrentOtaVersion(String currentOtaVersion) {
        this.currentOtaVersion = currentOtaVersion;
    }

    /**
     *  Definicion de Interfaces
     */



    protected OnReceivedStatus onReceivedStatus;

    public interface  OnReceivedStatus {

        void receivedStatus(RESULT_CODE resultCode);

    }

    public interface OnRececiveInfoDevice {
        void receivedInfoDevice(IotDevice device);
    }

    public interface OnSwitchDevice {
        void receivedSwitchDevice(IotDevice device);
    }

    public interface OnResetDevice {
        void receivedResetDevice(IotDevice device);
    }

    public interface OnFactoryResetDevice {
        void receivedFactoryResetDevice(IotDevice device);
    }

    public interface OnModifyParameterDevice {
        void receivedModifyParameterDevice(IotDevice device);
    }

    public interface OnModifyClockDevice {
        void receivedModifyClockDevice(IotDevice device);
    }

    public interface OnUpgradeFirmwareDevice {
        void receivedUpgradeFirmwareDevice(IotDevice device);
    }

    public interface OnOtaVersionAvailableDevice {
        void receivedOtaVersionAvailableDevice(IotDevice device);
    }

    public interface OnErrorAnswerDevice {
        void receivedErrorAnswerDevice(RESULT_CODE result);
    }

    public OnReceivedStatus getOnReceivedStatus() {
        return onReceivedStatus;
    }

    public void setOnReceivedStatus(OnReceivedStatus onReceivedStatus) {
        this.onReceivedStatus = onReceivedStatus;
    }

    /**
     *
     * @return
     */
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
        setTopicPublicacion("/sub_" + idDispositivo);
        setTopicSubscripcion("/pub_" + idDispositivo);
    }

    public TIPO_DISPOSITIVO_IOT getTipoDispositivo() {
        return tipoDispositivo;
    }

    public TIPO_DISPOSITIVO_IOT getTipoDispositivo(String texto) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int tipo;
        tipo = api.getJsonInt(texto, TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        if (tipo >= -1) {
            return TIPO_DISPOSITIVO_IOT.DESCONOCIDO.fromId(tipo);
        }
        else {
            return TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        }


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

    public DEVICE_STATE_CONNECTION getEstadoConexion() {
        return estadoConexion;
    }

    public void setEstadoConexion(DEVICE_STATE_CONNECTION estadoConexion) {
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

    public int getFinUpgrade() {
        return finUpgrade;
    }

    public void setFinUpgrade(int finUpgrade) {
        this.finUpgrade = finUpgrade;
    }

    /**
     *
     */
    public IotDevice() {

        nombreDispositivo = null;
        idDispositivo = null;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        versionOntaDisponible = null;
        topicSubscripcion = null;
        topicPublicacion = null;
        datosOta = null;
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();

    }

    public IotDevice(String nombreDispositivo, String idDispositivo, int tipo) {


        this.nombreDispositivo = nombreDispositivo;
        this.idDispositivo = idDispositivo;
        setTipoDispositivo(tipo);
        versionOntaDisponible = null;
        topicSubscripcion = null;
        topicPublicacion = null;
        datosOta = null;
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();

    }

    public IotDevice(String nombreDispositivo, String idDispositivo, int tipo, ConexionMqtt cnx){

        this.nombreDispositivo = nombreDispositivo;
        this.idDispositivo = idDispositivo;
        setTipoDispositivo(tipo);
        versionOntaDisponible = null;
        topicSubscripcion = null;
        topicPublicacion = null;
        datosOta = null;
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);





    }

    public IotDevice(ConexionMqtt cnx){

        versionOntaDisponible = null;
        topicSubscripcion = null;
        topicPublicacion = null;
        datosOta = null;
        estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estadoConexion = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);
        cnx.setListenerMessage(new ConexionMqtt.OnArrivedMessage() {
            @Override
            public void arrivedMessage(String topic, MqttMessage message) {

                if (topic.equals(getTopicSubscripcion())) {
                    setEstadoConexion(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
                    procesarMensaje(topic, message);
                } else {
                    Log.w(TAG, "Este mensajes no es para mi " + topic + "<->" + getTopicSubscripcion());
                }
            }
        });




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
    public Boolean modificarDispositivo(IotDevice dispositivo) {

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

    public DEVICE_STATE_CONNECTION subscribeDevice() {

        if (cnx == null) {
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        cnx.subscribirTopic(this.topicSubscripcion, new ConexionMqtt.OnSubscriptionTopic() {
            @Override
            public void conExito(IMqttToken iMqttToken) {
                Log.i(TAG, "Subscripcion completada");

            }

            @Override
            public void sinExito(IMqttToken iMqttToken, Throwable throwable) {
                Log.i(TAG, "Error en la subscripcion");
            }
        });


        return DEVICE_STATE_CONNECTION.DEVICE_CONNECTED;
    }


    public DEVICE_STATE_CONNECTION subscribeDevice(String topicDevice) {

        if (cnx == null) {
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        cnx.subscribirTopic(topicDevice, new ConexionMqtt.OnSubscriptionTopic() {
            @Override
            public void conExito(IMqttToken iMqttToken) {
                Log.i(TAG, "Subscripcion completada");

            }

            @Override
            public void sinExito(IMqttToken iMqttToken, Throwable throwable) {
                Log.i(TAG, "Error en la subscripcion");
            }
        });


        return DEVICE_STATE_CONNECTION.DEVICE_CONNECTED;
    }

    private void procesarMensaje(String topic, MqttMessage message) {

        TIPO_INFORME tipoInforme = null;
        String mensaje;
        IotDevice dispositivo;
        mensaje = new String (message.getPayload());
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();

        COMANDO_IOT comando;
        comando = api.getCommandId(mensaje);
        if (comando == COMANDO_IOT.ESPONTANEO) {
            tipoInforme = TIPO_INFORME.INFORME_ESPONTANEO;
        } else {
            tipoInforme = TIPO_INFORME.RESULTADO_COMANDO;
        }

        switch (tipoInforme) {

            case RESULTADO_COMANDO:
                procesarComando(topic, message);
                break;
            case INFORME_ESPONTANEO:
                procesarEspontaneo(topic, message);
                break;
        }

    }

    protected void procesarComando(String topic, MqttMessage message) {
        COMANDO_IOT idComando;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        RESULT_CODE res;

        switch (idComando) {

            case CONSULTAR_CONF_APP:
                break;
            case ACTUAR_RELE:
                break;
            case ESTADO:
                res = procesarStatus(mensaje);
                onReceivedStatus.receivedStatus(RESULT_CODE.RESUT_CODE_OK);
                break;
            case CONSULTAR_PROGRAMACION:
                break;
            case NUEVA_PROGRAMACION:
                break;
            case ELIMINAR_PROGRAMACION:
                break;
            case MODIFICAR_PROGRAMACION:
                break;
            case MODIFICAR_APP:
                break;
            case RESET:
                break;
            case FACTORY_RESET:
                break;
            case MODIFY_CLOCK:
                break;
            case UPGRADE_FIRMWARE:
                break;
            case VERSION_OTA:
                break;
            case ERROR_RESPUESTA:
                break;
            default:
                break;
        }




    }

    protected void procesarEspontaneo(String topic, MqttMessage message) {
        IotDevice dispositivo = null;


    }




    protected RESULT_CODE procesarStatus(String respuesta) {

        getCurrentOtaVersion(respuesta);
        setEstadoConexion(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
        getDeviceStatus(respuesta);
        getScheduleState(respuesta);
        getDeviceType(respuesta);
        return RESULT_CODE.RESUT_CODE_OK;
    }


    /**
     * Metodo para obtener la version OTA del dispositivo
     * @param respuesta Es el texto recibido desde el dispositivo
     * @return Se retorna el codigo de respuesta enviado por el dispositivo
     */
    protected RESULT_CODE getCurrentOtaVersion(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        String version;
        version = api.getJsonString(respuesta, TEXTOS_DIALOGO_IOT.VERSION_OTA.getValorTextoJson());
        if (version != null) {
            setCurrentOtaVersion(version);
            return RESULT_CODE.RESUT_CODE_OK;
        } else {
            return RESULT_CODE.RESULT_CODE_ERROR;
        }

    }

    protected RESULT_CODE getDeviceStatus(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int estado;
        ESTADO_DISPOSITIVO estadoDispositivo = ESTADO_DISPOSITIVO.INDETERMINADO;
        estado = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_DISPOSITIVO.getValorTextoJson());
        if (estado > ESTADO_DISPOSITIVO.INDETERMINADO.getEstadoDispositivo()) {
            setEstadoDispositivo(estadoDispositivo.fromId(estado));
            return RESULT_CODE.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return RESULT_CODE.RESULT_CODE_ERROR;
        }

    }

    protected RESULT_CODE getScheduleState(String respuesta) {
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int estado;
        SCHEDULE_STATE schedule = SCHEDULE_STATE.INH_PROG;
        estado = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson());
        if (estado >= 0) {
            setScheduleState(schedule.fromId(estado));
            return RESULT_CODE.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return RESULT_CODE.RESULT_CODE_ERROR;
        }

    }

    protected RESULT_CODE getDeviceType(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int tipo;
        TIPO_DISPOSITIVO_IOT tipoDispositivo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        tipo = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        setTipoDispositivo(tipoDispositivo.fromId(tipo));
        return RESULT_CODE.RESUT_CODE_OK;
    }


    public DEVICE_STATE_CONNECTION getStatusDeviceCommand(COMANDO_IOT comando) {

        String textoComando;
        ApiDispositivoIot api;
        DEVICE_STATE_CONNECTION estado;
        api = new ApiDispositivoIot();
        textoComando = api.createSimpleCommand(COMANDO_IOT.ESTADO);
        if ((estado = sendCommand(textoComando)) != DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
            return estado;
        }

        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
    }

    protected DEVICE_STATE_CONNECTION sendCommand(String textoComando) {

        DEVICE_STATE_CONNECTION estado;
        if ((estado = cnx.publicarTopic(getTopicPublicacion(), textoComando)) != DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
            setEstadoConexion(estado);
            return estado;
        }
        
        setEstadoConexion(DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE);

        if (timerCommand == null) {
            timerCommand = new Timer();
        }
        timerCommand.schedule(new TimerTask() {
            @Override
            public void run() {

                if (getEstadoConexion() != DEVICE_STATE_CONNECTION.DEVICE_CONNECTED) {
                    if (onErrorAnswerDevice != null) {
                        Log.i(TAG, "Enviamos timeout");
                        onErrorAnswerDevice.receivedErrorAnswerDevice(RESULT_CODE.RESULT_CODE_TIMEOUT);
                    }

                }

            }
        }, 10000);

        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;

    }




}