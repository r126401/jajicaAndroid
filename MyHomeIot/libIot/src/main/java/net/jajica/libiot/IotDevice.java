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
        protected String deviceName;
    /**
     * Identidad del dispositivo
     */
    protected String deviceId;
    /**
     * Tipo de dispositivo
     */
        protected IOT_DEVICE_TYPE deviceType;

    /**
     * Version del dispositivo.
     * La logica de version que se aplica es AAAAMMDDmmss
     */
    protected String versionOtaAvailable;

    /**
     *  Topic usado por la aplicacion para conectarse la topic de suscripcion del dispositivo
     */
    protected String subscriptionTopic;

    /**
     * Topic usado por la aplicacion para conectarse al topic de publicacion del dispositivo
     */
    protected String publishTopic;

    /**
     * Estado del dispositivo
     */
    protected IOT_DEVICE_STATE deviceState;

    /**
     * Estado de la conexion al dispositivo
     */
    protected DEVICE_STATE_CONNECTION connectionState;

    /**
     * identidad de la conexion mqtt
     */
    protected MqttConnection cnx;

    /**
     * Este metodo devuelve la identidad de la conexion mqtt
     * @return Se retorna la identidad de la conexion
     */
    public MqttConnection getCnx() {
        return cnx;
    }

    /**
     * Este metodo introduce una conexion previamente realizada con MqttConnection
     * @param cnx es la conexion MqttConnection
     */
    public void setCnx(MqttConnection cnx) {
        this.cnx = cnx;
    }

    /**
     * Puntero a la estructura de la version reportada por el dispositivo
     */
    protected OtaVersion datosOta;
    /**
     * Version actual del dispositivo
     */
    protected String currentOtaVersion;
    /**
     * indica la identidad del programa activo segun el formate de la clase IotDeviceProgram
     */
    protected String programaActivo;
    /**
     * Es un array a la lista de Programas del dispositivo
     */
    protected ArrayList<IotDeviceProgram> programs;

    protected int finUpgrade;
    /**
     * Mantiene la estructura del dispositivo en formato json
     */
    protected JSONObject dispositivoJson;

    /**
     * Es el estado global de programacion del dispositivo
     */
    protected SCHEDULE_STATE ScheduleState;

    protected Timer timerCommand;
    /**
     * Interfaz que utiliza la aplicacion cuando falla la comunicacion con el dispositivo
     */
    protected OnErrorAnswerDevice onErrorAnswerDevice;


    /**
     * metodo para crear un listener OnErrorAnswerDevice
     * @param onErrorAnswerDevice Es el listener para error en la respuesta
     */

    public void setOnErrorAnswerDevice(OnErrorAnswerDevice onErrorAnswerDevice) {
        this.onErrorAnswerDevice = onErrorAnswerDevice;
    }

    /**
     * Metodo que devuelve el estado global de programacion del dispositivo
     * @return Devuelve el estado global de programacion del dispositivo
     */
    public SCHEDULE_STATE getScheduleState() {
        return ScheduleState;
    }

    /**
     * Pone el estado Global de programacion del dispositivo
     * @param scheduleState Es el estado que quire introducirse
     */
    public void setScheduleState(SCHEDULE_STATE scheduleState) {
        ScheduleState = scheduleState;
    }

    /**
     * Este metodo devuelve la lista de programas del dispositivo
     * @return Devuelve la lista de programas del dispositivo
     */
    public ArrayList<IotDeviceProgram> getPrograms() {
        return programs;
    }

    /**
     * Asigna al dispositivo la lista de programas del dispositivo
     * @param programs
     */
    public void setPrograms(ArrayList<IotDeviceProgram> programs) {
        this.programs = programs;
    }

    /**
     * Obtiene la version actual del dispositivo
     * @return
     */
    public String getCurrentOtaVersion() {
        return currentOtaVersion;
    }

    /**
     * Actualiza en el dispositivo la version actual
     */
    public void setCurrentOtaVersion(String currentOtaVersion) {
        this.currentOtaVersion = currentOtaVersion;
    }

    /**
     *  Definicion de Interfaces
     */


    /**
     * Interface que la aplicacion utiliza para recibir el estado del dispositivo
     */
    protected OnReceivedStatus onReceivedStatus;

    public interface  OnReceivedStatus {

        void onReceivedStatus(RESULT_CODE resultCode);

    }




    protected OnReceivedInfoDevice onReceivedInfoDevice;
    public interface OnReceivedInfoDevice {
        void onReceivedInfoDevice(RESULT_CODE resultCode);
    }

    public void setOnReceivedInfoDevice(OnReceivedInfoDevice onReceivedInfoDevice) {
        this.onReceivedInfoDevice = onReceivedInfoDevice;
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
     *Este metodo diguelve la estructura del dispositivo en formato json
     * @return Devuelve la estructura json del dispositivo
     */
    public JSONObject getDispositivoJson() {
        return dispositivoJson;
    }

    /**
     * Introduce la estructura del dispositivo json de forma manual
     * @param dispositivoJson es la estructura json del dispositivo
     */
    public void setDispositivoJson(JSONObject dispositivoJson) {
        this.dispositivoJson = dispositivoJson;
    }

    /**
     * Obtiene el nombre del dispositivo
     * @return Retorna el nombre del dispositivo
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Introduce el nombre del dispositivo
     * @param deviceName
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Obtiene la identidad del dispositivo. Actualmente la mac del dispositivo
     * @return Se retorna la identidad del dispositivo
     */

    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Este metodo introduce la identidad del dispositivo
     * @param deviceId Es la identidad del dispositivo
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        setPublishTopic("/sub_" + deviceId);
        setSubscriptionTopic("/pub_" + deviceId);
    }


    /**
     * Este metodo devuelve el estado del dispositivo
     * @return
     */
    public IOT_DEVICE_TYPE getDeviceType() {
        return deviceType;
    }

    /**
     * Este metodo pone el tipo de dispositivo
     * @param deviceType es el tipo de dispositivo
     */
    public void setDeviceType(IOT_DEVICE_TYPE deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Pone el deviceType a partir de su valor entero
     * @param tipo
     */
    public void setDeviceType(int tipo) {

        this.deviceType = this.deviceType.fromId(tipo);

    }

    /**
     * Obtiene la version ota disponible
     * @return Retorna la version ota disponible para upgrade
     */
    public String getVersionOtaAvailable() {
        return versionOtaAvailable;
    }

    /**
     * Introduce la version ota disponible
     * @param versionOtaAvailable Es la version ota disponible
     */
    public void setVersionOtaAvailable(String versionOtaAvailable) {
        this.versionOtaAvailable = versionOtaAvailable;
    }

    /**
     * obtiene el topic de subscripcion del dispositivo
     * @return Retorna el topic de susbcripcion del dispositivo
     */
    public String getSubscriptionTopic() {
        return subscriptionTopic;
    }

    /**
     * Introduce el topic de subscripcion del dispositivo
     * @param subscriptionTopic topic de subscripcion del dispositivo
     */
    public void setSubscriptionTopic(String subscriptionTopic) {
        this.subscriptionTopic = subscriptionTopic;
    }

    /**
     * Obtiene el topic de publicacion del dispositivo
     * @return Retorn el topic de publicacion del dispositivo
     */
    public String getPublishTopic() {
        return publishTopic;
    }

    /**
     * Introduce el topic de publicacion del dispositivo
     * @param publishTopic es el topic de publicacion del dispositivo
     */
    public void setPublishTopic(String publishTopic) {
        this.publishTopic = publishTopic;
    }

    /**
     * Obtiene el estado del dispositivo
     * @return Retorna el estado del dispositivo
     */
    public IOT_DEVICE_STATE getDeviceState() {
        return deviceState;
    }

    /**
     * Pone el estado del dispositivo
     * @param deviceState Es el nuevo estado del dispositivo
     */
    public void setDeviceState(IOT_DEVICE_STATE deviceState) {
        this.deviceState = deviceState;
    }

    /**
     * Obtiene el estado de la conexion
     * @return
     */
    public DEVICE_STATE_CONNECTION getConnectionState() {
        return connectionState;
    }

    /**
     * Actualiza el estado de la conexion
     * @param connectionState Es el nuevo estado de la conexion
     */
    public void setConnectionState(DEVICE_STATE_CONNECTION connectionState) {
        this.connectionState = connectionState;
    }

    /**
     * Obtiene la estructura OTA donde viene la informacion par upgrade OTA
     * @return
     */
    public OtaVersion getDatosOta() {
        return datosOta;
    }

    /**
     * Asigna la estructura ota al dispositivo
     * @param datosOta Es la estructura OTA en la que vienen los datos para upgrade
     */
    public void setDatosOta(OtaVersion datosOta) {
        this.datosOta = datosOta;
    }

    /**
     * Obitiene la identidad del programa activo
     * @return Es el programa activo
     */
    public String getProgramaActivo() {
        return programaActivo;
    }

    /**
     * Asigna al dispositivo el programa activo
     * @param programaActivo Es el programa activo
     */
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
     * Constructor solo usado si no se van a introducir comandos
     */
    public IotDevice() {

        deviceName = null;
        deviceId = null;
        deviceType = IOT_DEVICE_TYPE.DESCONOCIDO;
        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        datosOta = null;
        deviceState = IOT_DEVICE_STATE.INDETERMINADO;
        connectionState = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();

    }

    /**
     * Constructor usado si no se van a introducir comandos
     * @param deviceName Es el nombre del dispositivo
     * @param deviceId Es la identidad del dispositivo
     * @param tipo Es el tipo de dispositivo
     */
    public IotDevice(String deviceName, String deviceId, int tipo) {


        this.deviceName = deviceName;
        this.deviceId = deviceId;

        setDeviceType(tipo);
        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        datosOta = null;
        deviceState = IOT_DEVICE_STATE.INDETERMINADO;
        connectionState = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();

    }

    public IotDevice(String deviceName, String deviceId, int tipo, MqttConnection cnx){

        this.deviceName = deviceName;
        this.deviceId = deviceId;
        setDeviceType(tipo);
        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        datosOta = null;
        deviceState = IOT_DEVICE_STATE.INDETERMINADO;
        connectionState = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);





    }

    /**
     * Constructor preparado para el dialogo con el dispositivo
     * @param cnx Es un handle de la clase MqttConnection para la comunicacion del dispositivo
     */

    public IotDevice(MqttConnection cnx){

        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        datosOta = null;
        deviceState = IOT_DEVICE_STATE.INDETERMINADO;
        connectionState = DEVICE_STATE_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        programaActivo = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);
        cnx.setOnListenerArrivedMessaged(new MqttConnection.OnArrivedMessage() {
            @Override
            public void arrivedMessage(String topic, MqttMessage message) {

                if (topic.equals(getSubscriptionTopic())) {
                    setConnectionState(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
                    procesarMensaje(topic, message);
                } else {
                    Log.w(TAG, "Este mensajes no es para mi " + topic + "<->" + getSubscriptionTopic());
                }
            }
        });



    }

    /**
     * Este metodo vuelca la estructura json del dispositivo en la propia estructura de la clase
     * @param dispositivoJson es la estructura json
     * @return Se devuelve el resultado de la operacion
     */
    public OPERACION_JSON json2Device(JSONObject dispositivoJson) {

        int tipo;
        try {
            deviceName = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson());
            deviceId = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
            publishTopic = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson());
            subscriptionTopic = dispositivoJson.getString(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson());
            tipo = dispositivoJson.getInt(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
            deviceType = deviceType.fromId(tipo);
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
     * @return Se retorna el objeto json creado
     */
    public JSONObject device2Json() {



        if (deviceName != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson(), getDeviceName());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (deviceId != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), getDeviceId());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        if(subscriptionTopic != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson(), getSubscriptionTopic());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }


        if (publishTopic != null) {
            try {
                dispositivoJson.put(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson(), getPublishTopic());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), getDeviceType().getValorTipoDispositivo());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return dispositivoJson;


    }

    private Boolean nullCheck(String valor) {

        if ((valor == null) || valor.equals("")) {
            return false;
        } else {
            return true;
        }


    }

    public Boolean dispositivoValido() {

        if(!nullCheck(deviceName)) return false;
        if(!nullCheck(deviceId)) return false;
        if(!nullCheck(publishTopic)) return false;
        if(!nullCheck(subscriptionTopic)) return false;

        return true;

    }

    /**
     * Este metodo cambia los valores mas relevantes de un dispositivo
     * @param dispositivo Es el dispositivo a modificar
     * @return Se retorna si la operacion se ha realizado satisfactoriamente. True con exito
     */
    public Boolean modifyDevice(IotDevice dispositivo) {

        if (!dispositivo.dispositivoValido()) {
            return false;
        }
        this.setDeviceName(dispositivo.getDeviceName());
        this.setDeviceId(dispositivo.getDeviceId());
        this.setDeviceType(dispositivo.getDeviceType());
        this.setPublishTopic(dispositivo.getPublishTopic());
        this.setSubscriptionTopic(dispositivo.getSubscriptionTopic());
        return true;

    }

    /**
     * Metodo para subscribir la aplicacion a un topic
     * @return Se retorna el estado de la operacion
     */
    public DEVICE_STATE_CONNECTION subscribeDevice() {

        if (cnx == null) {
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        cnx.subscribeTopic(this.subscriptionTopic, new MqttConnection.OnSubscriptionTopic() {
            @Override
            public void Unsuccessful(IMqttToken iMqttToken) {
                Log.i(TAG, "Subscripcion completada");

            }

            @Override
            public void Successful(IMqttToken iMqttToken, Throwable throwable) {
                Log.i(TAG, "Error en la subscripcion");
            }
        });


        return DEVICE_STATE_CONNECTION.DEVICE_CONNECTED;
    }


    /**
     * Subscribe a la aplicacion a un topic diferente del propia para el dispositivo
     * @param topicDevice Es el topic
     * @return Se retorna el resultado de la operacion
     */
    public DEVICE_STATE_CONNECTION subscribeDevice(String topicDevice) {

        if (cnx == null) {
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        cnx.subscribeTopic(topicDevice, new MqttConnection.OnSubscriptionTopic() {
            @Override
            public void Unsuccessful(IMqttToken iMqttToken) {
                Log.i(TAG, "Subscripcion completada");

            }

            @Override
            public void Successful(IMqttToken iMqttToken, Throwable throwable) {
                Log.i(TAG, "Error en la subscripcion");
            }
        });


        return DEVICE_STATE_CONNECTION.DEVICE_CONNECTED;
    }

    /**
     * Es el primer metodo al que se llama cuando llega un mensaje para el dispositivo.
     * El metodo determina si se trata de una respuesta a un comando o de un mensaje espontaneo
     * del dispositivo.
     * @param topic Es el topic del mensaje
     * @param message Es el contenido del mensaje
     */
    private void procesarMensaje(String topic, MqttMessage message) {

        IOT_REPORT_TYPE tipoInforme = null;
        String mensaje;
        IotDevice dispositivo;
        mensaje = new String (message.getPayload());
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();

        COMANDO_IOT comando;
        comando = api.getCommandId(mensaje);
        if (comando == COMANDO_IOT.ESPONTANEO) {
            tipoInforme = IOT_REPORT_TYPE.SPONTANEOUS_REPORT;
        } else {
            tipoInforme = IOT_REPORT_TYPE.COMMAND_REPORT;
        }

        switch (tipoInforme) {

            case COMMAND_REPORT:
                procesarComando(topic, message);
                break;
            case SPONTANEOUS_REPORT:
                procesarEspontaneo(topic, message);
                break;
        }

    }

    /**
     * Este metodo distribuye el procesamiento para los diferentes mensajes que el dispositivo es capaz
     * de tratar.
     * @param topic Es el topic de subscripcion del dispositivo
     * @param message Es el contenido del mensaje
     */

    protected void procesarComando(String topic, MqttMessage message) {
        COMANDO_IOT idComando;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        RESULT_CODE res;

        switch (idComando) {

            case CONSULTAR_CONF_APP:
                res = processInfoDevice(mensaje);
                onReceivedInfoDevice.onReceivedInfoDevice(RESULT_CODE.RESUT_CODE_OK);
                break;
            case ACTUAR_RELE:
                break;
            case ESTADO:
                res = processStatus(mensaje);
                onReceivedStatus.onReceivedStatus(RESULT_CODE.RESUT_CODE_OK);
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

    /**
     * Esta funcion procesa los mensajes espontaneos
     * @param topic Es el topic de subscripcion
     * @param message Es el contenido del mensaje espontaneo
     */
    protected void procesarEspontaneo(String topic, MqttMessage message) {
        IotDevice dispositivo = null;


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
        IOT_DEVICE_TYPE tipoDispositivo = IOT_DEVICE_TYPE.DESCONOCIDO;
        tipo = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        setDeviceType(tipoDispositivo.fromId(tipo));
        return RESULT_CODE.RESUT_CODE_OK;
    }


    public DEVICE_STATE_CONNECTION simpleCommand(COMANDO_IOT command) {

        String textoComando;
        ApiDispositivoIot api;
        DEVICE_STATE_CONNECTION estado;
        api = new ApiDispositivoIot();
        textoComando = api.createSimpleCommand(command);
        if ((estado = sendCommand(textoComando)) != DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
            return estado;
        }

        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
    }

    public DEVICE_STATE_CONNECTION getStatusDeviceCommand() {

        return simpleCommand(COMANDO_IOT.ESTADO);


    }

    protected DEVICE_STATE_CONNECTION sendCommand(String textoComando) {

        DEVICE_STATE_CONNECTION estado;
        if ((estado = cnx.publishTopic(getPublishTopic(), textoComando)) != DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
            setConnectionState(estado);
            return estado;
        }
        
        setConnectionState(DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE);

        if (timerCommand == null) {
            timerCommand = new Timer();
        }
        timerCommand.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getConnectionState() != DEVICE_STATE_CONNECTION.DEVICE_CONNECTED) {
                    if (onErrorAnswerDevice != null) {
                        Log.i(TAG, "Enviamos timeout: " + getToken(textoComando));
                        onErrorAnswerDevice.receivedErrorAnswerDevice(RESULT_CODE.RESULT_CODE_TIMEOUT);
                    }
                }
            }
        }, 10000);
        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
    }

    private String getToken(String textoComando) {

        String key;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        key = api.getJsonString(textoComando, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson());
        return key;

    }

    protected RESULT_CODE processStatus(String respuesta) {

        getCurrentOtaVersion(respuesta);
        setConnectionState(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
        getDeviceStatus(respuesta);
        getScheduleState(respuesta);
        getDeviceType(respuesta);
        device2Json();
        return RESULT_CODE.RESUT_CODE_OK;
    }

    protected RESULT_CODE getDeviceStatus(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int estado;
        IOT_DEVICE_STATE estadoDispositivo = IOT_DEVICE_STATE.INDETERMINADO;
        estado = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_DISPOSITIVO.getValorTextoJson());
        if (estado > IOT_DEVICE_STATE.INDETERMINADO.getDeviceState()) {
            setDeviceState(estadoDispositivo.fromId(estado));
            return RESULT_CODE.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return RESULT_CODE.RESULT_CODE_ERROR;
        }

    }


    public DEVICE_STATE_CONNECTION getInfoDeviceCommand() {

        return simpleCommand(COMANDO_IOT.CONSULTAR_CONF_APP);

    }

    protected RESULT_CODE processInfoDevice(String message) {
        Log.i(TAG, message);
        return RESULT_CODE.RESUT_CODE_OK;
    }



}