package net.jajica.libiot;


import android.util.Log;


import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
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



    /**
     * Tag utilizado para las trazas
     */
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
     * indica la identidad del programa activo segun el formate de la clase IotScheduleDevice
     */
    protected String activeSchedule;
    /**
     * Es un array a la lista de Programas del dispositivo
     */
    private ArrayList<IotScheduleDevice> schedules;

    protected int finUpgrade;
    /**
     * Mantiene la estructura del dispositivo en formato json
     */
    protected JSONObject dispositivoJson;

    /**
     * Es el estado global de programacion del dispositivo
     */
    protected SCHEDULE_CONDITION programmerState;

    protected Timer timerCommand;
    protected long freeMem;
    protected Double uptime;


    /**
     * Obtiene la memoria libre del dispositivo
     * @return Retorna la memoria libre
     */
    public long getFreeMem() {
        return freeMem;
    }

    /**
     * introduce la memoria libre leida desde el dispositivo
     * @param freeMem
     */
    public void setFreeMem(long freeMem) {
        this.freeMem = freeMem;
    }

    /**
     * Obtiene el tiempo de funcionmiento del dispositivo
     * @return Retorna el tiempo de funcionamiento
     */
    public Double setUpTimeFromReport() {
        return uptime;
    }

    /**
     * Introduce el tiempo de funcionamiento
     * @param uptime Tiempo de funcionamiento del dispositivo
     */
    public void setUptime(Double uptime) {
        this.uptime = uptime;
    }



    /**
     * Metodo que devuelve el estado global de programacion del dispositivo
     * @return Devuelve el estado global de programacion del dispositivo
     */
    public SCHEDULE_CONDITION getProgrammerState() {
        return programmerState;
    }

    /**
     * Pone el estado Global de programacion del dispositivo
     * @param programmerState Es el estado que quire introducirse
     */
    public void setProgrammerState(SCHEDULE_CONDITION programmerState) {
        this.programmerState = programmerState;
    }

    /**
     * Este metodo devuelve la lista de programas del dispositivo
     * @return Devuelve la lista de programas del dispositivo
     */
    public ArrayList<IotScheduleDevice> getSchedules() {
        return schedules;
    }

    /**
     * Asigna al dispositivo la lista de programas.
     * @param schedules Es la lista de programas
     */
    public void setSchedules(ArrayList<IotScheduleDevice> schedules) {
        this.schedules = schedules;
    }

    /**
     * Obtiene la version actual del dispositivo
     * @return
     */
    public String setCurrentOtaVersionFromReport() {
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

        void onReceivedStatus(IOT_CODE_RESULT resultCode);

    }

    protected OnReceivedInfoDevice onReceivedInfoDevice;
    public interface OnReceivedInfoDevice {
        void onReceivedInfoDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedInfoDevice(OnReceivedInfoDevice onReceivedInfoDevice) {
        this.onReceivedInfoDevice = onReceivedInfoDevice;
    }

    protected OnReceivedScheduleDevice onReceivedScheduleDevice;
    public interface OnReceivedScheduleDevice {
        void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode);

    }

    public void setOnReceivedScheduleDevice(OnReceivedScheduleDevice onReceivedScheduleDevice) {
        this.onReceivedScheduleDevice = onReceivedScheduleDevice;
    }

    protected OnReceivedDeleteDevice onReceivedDeleteDevice;

    public interface OnReceivedDeleteDevice {
        void onReceivedDeleteDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedDeleteDevice(OnReceivedDeleteDevice onReceivedDeleteDevice) {
        this.onReceivedDeleteDevice = onReceivedDeleteDevice;
    }

    protected OnReceivedModifySchedule onReceivedModifySchedule;
    public interface OnReceivedModifySchedule {
        void onReceivedModifySchedule(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedModifySchedule(OnReceivedModifySchedule onReceivedModifySchedule) {
        this.onReceivedModifySchedule = onReceivedModifySchedule;
    }
    protected OnReceivedNewSchedule onReceivedNewSchedule;
    public interface OnReceivedNewSchedule {
        void onReceivedNewSchedule(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedNewSchedule(OnReceivedNewSchedule onReceivedNewSchedule) {
        this.onReceivedNewSchedule = onReceivedNewSchedule;
    }

    protected OnReceivedModifyParametersDevice onReceivedModifyParametersDevice;
    public interface OnReceivedModifyParametersDevice {
        void onReceivedMofifyParametersDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedModifyParametersDevice(OnReceivedModifyParametersDevice onReceivedModifyParametersDevice) {
        this.onReceivedModifyParametersDevice = onReceivedModifyParametersDevice;
    }

    protected OnReceivedResetDevice onReceivedResetDevice;
    public interface OnReceivedResetDevice {
        void onReceivedResetDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedResetDevice(OnReceivedResetDevice onReceivedResetDevice) {
        this.onReceivedResetDevice = onReceivedResetDevice;
    }

    protected OnReceivedFactoryResetDevice onReceivedFactoryResetDevice;
    public interface OnReceivedFactoryResetDevice {
        void onReceivedFactoryResetDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedFactoryResetDevice(OnReceivedFactoryResetDevice onReceivedFactoryResetDevice) {
        this.onReceivedFactoryResetDevice = onReceivedFactoryResetDevice;
    }
    protected OnReceivedModifyClockDevice onReceivedModifyClockDevice;
    public interface OnReceivedModifyClockDevice {
        void onReceivedModifyClockDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedModifyClockDevice(OnReceivedModifyClockDevice onReceivedModifyClockDevice) {
        this.onReceivedModifyClockDevice = onReceivedModifyClockDevice;
    }

    protected OnReceivedUpgradeFirmwareDevice onReceivedUpgradeFirmwareDevice;
    public interface OnReceivedUpgradeFirmwareDevice {
        void onReceivedUpgradeFirmwareDevice(IOT_CODE_RESULT codeResult);
    }

    public void setOnReceivedUpgradeFirmwareDevice(OnReceivedUpgradeFirmwareDevice onReceivedUpgradeFirmwareDevice) {
        this.onReceivedUpgradeFirmwareDevice = onReceivedUpgradeFirmwareDevice;
    }

    protected OnReceivedOtaVersionAvailableDevice onReceivedOtaVersionAvailableDevice;
    public interface OnReceivedOtaVersionAvailableDevice {
        void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnOtaVersionAvailableDevice(OnReceivedOtaVersionAvailableDevice onReceivedOtaVersionAvailableDevice) {
        this.onReceivedOtaVersionAvailableDevice = onReceivedOtaVersionAvailableDevice;
    }

    /**
     * Interfaz que utiliza la aplicacion cuando falla la comunicacion con el dispositivo
     */
    protected OnReceivedTimeoutCommand onReceivedTimeoutCommand;
    public interface OnReceivedTimeoutCommand {
        void onReceivedTimeoutCommand(IOT_CODE_RESULT result);
    }

    public void setOnReceivedTimeoutCommand(OnReceivedTimeoutCommand onReceivedTimeoutCommand) {
        this.onReceivedTimeoutCommand = onReceivedTimeoutCommand;
    }




    public OnReceivedStatus getOnReceivedStatus() {
        return onReceivedStatus;
    }

    public void setOnReceivedStatus(OnReceivedStatus onReceivedStatus) {
        this.onReceivedStatus = onReceivedStatus;
    }

    protected OnReceivedErrorReportDevice onReceivedErrorReportDevice;
    public interface OnReceivedErrorReportDevice {
        void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnErrorReportDevice(OnReceivedErrorReportDevice onReceivedErrorReportDevice) {
        this.onReceivedErrorReportDevice = onReceivedErrorReportDevice;
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
    public IOT_DEVICE_TYPE setDeviceTypeFromReport() {
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
    public String getActiveSchedule() {
        return activeSchedule;
    }

    /**
     * Asigna al dispositivo el programa activo
     * @param activeSchedule Es el programa activo
     */
    public void setActiveSchedule(String activeSchedule) {
        this.activeSchedule = activeSchedule;
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
        activeSchedule = null;
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
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        schedules = null;

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
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);
        schedules = null;





    }

    public DEVICE_STATE_CONNECTION recibirMensajes() {

        if (getSubscriptionTopic() == null) {
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        cnx.setOnListenerArrivedMessaged(getSubscriptionTopic(), new MqttConnection.OnArrivedMessage() {
            @Override
            public void arrivedMessage(String topic, MqttMessage message) {
                if (topic.equals(getSubscriptionTopic())) {
                    setConnectionState(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
                    procesarMensaje(topic, message);
                } else {
                    Log.w(TAG, "Este mensajes no es para mi " + topic + "<->" + subscriptionTopic);
                }
            }
        });

        return DEVICE_STATE_CONNECTION.DEVICE_CONNECTED;

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
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);
        schedules = null;



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
            dispositivoJson.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), setDeviceTypeFromReport().getValorTipoDispositivo());
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
        this.setDeviceType(dispositivo.setDeviceTypeFromReport());
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
                Log.i(TAG, "Subscripcion completada al topic " + subscriptionTopic);

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

        IOT_COMMANDS comando;
        comando = api.getCommandId(mensaje);
        if (comando == IOT_COMMANDS.SPONTANEOUS) {
            tipoInforme = IOT_REPORT_TYPE.SPONTANEOUS_REPORT;
        } else {
            tipoInforme = IOT_REPORT_TYPE.COMMAND_REPORT;
        }

        switch (tipoInforme) {

            case COMMAND_REPORT:
                processCommand(topic, message);
                break;
            case SPONTANEOUS_REPORT:
                processSpontaneous(topic, message);
                break;
        }

    }

    /**
     * Este metodo distribuye el procesamiento para los diferentes mensajes que el dispositivo es capaz
     * de tratar.
     * @param topic Es el topic de subscripcion del dispositivo
     * @param message Es el contenido del mensaje
     */

    protected void processCommand(String topic, MqttMessage message) {
        IOT_COMMANDS idComando;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        IOT_CODE_RESULT res;

        switch (idComando) {

            case INFO_DEVICE:
                res = processInfoDevice(mensaje);
                if (onReceivedInfoDevice != null) {
                    onReceivedInfoDevice.onReceivedInfoDevice(IOT_CODE_RESULT.RESUT_CODE_OK);
                }
                break;
            case SET_RELAY:
                break;
            case STATUS_DEVICE:
                res = processStatus(mensaje);
                if (onReceivedStatus != null) {
                    onReceivedStatus.onReceivedStatus(IOT_CODE_RESULT.RESUT_CODE_OK);
                }

                break;
            case GET_SCHEDULE:
                res = processGetSchedule(mensaje);
                if(onReceivedScheduleDevice != null) {
                    onReceivedScheduleDevice.onReceivedScheduleDevice(IOT_CODE_RESULT.RESUT_CODE_OK);
                }

                break;
            case NEW_SCHEDULE:
                res = processNewSchedule(mensaje);
                if (onReceivedNewSchedule != null) {
                    onReceivedNewSchedule.onReceivedNewSchedule(res);
                }

                break;
            case REMOVE_SCHEDULE:
                res = processDeleteSchedule(mensaje);
                if (onReceivedDeleteDevice != null) {
                    onReceivedDeleteDevice.onReceivedDeleteDevice(res);
                }

                break;
            case MODIFY_SCHEDULE:
                res = processModifySchedule(mensaje);
                if (onReceivedModifySchedule != null) {
                    onReceivedModifySchedule.onReceivedModifySchedule(res);
                }
                break;
            case MODIFY_PARAMETER_DEVICE:
                res = processModifyParametersDevice(mensaje);
                if (onReceivedModifyParametersDevice != null) {
                    onReceivedModifyParametersDevice.onReceivedMofifyParametersDevice(res);
                }
                break;
            case RESET:
                res = processReset(mensaje);
                if (onReceivedResetDevice != null) {
                    onReceivedResetDevice.onReceivedResetDevice(res);
                }
                break;
            case FACTORY_RESET:
                res = processFactoryReset(mensaje);
                if (onReceivedFactoryResetDevice != null) {
                    onReceivedFactoryResetDevice.onReceivedFactoryResetDevice(res);
                }
                break;
            case MODIFY_CLOCK:
                res = processModifyClock(mensaje);
                if (onReceivedModifyClockDevice != null) {
                    onReceivedModifyClockDevice.onReceivedModifyClockDevice(res);
                }
                break;
            case UPGRADE_FIRMWARE:
                res = processUpgradeFirmware(mensaje);
                if (onReceivedUpgradeFirmwareDevice != null) {
                    onReceivedUpgradeFirmwareDevice.onReceivedUpgradeFirmwareDevice(res);
                }
                break;
            case VERSION_OTA:
                res = processOtaVersionAvailable(mensaje);
                if (onReceivedOtaVersionAvailableDevice != null) {
                    onReceivedOtaVersionAvailableDevice.onReceivedOtaVersionAvailableDevice(res);
                }
                break;
            case ERROR_REPORT:
                res = processErrorCommand(mensaje);
                if (onReceivedErrorReportDevice != null) {
                    onReceivedErrorReportDevice.onReceivedErrorReportDevice(res);
                }
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
    protected void processSpontaneous(String topic, MqttMessage message) {
        IotDevice dispositivo = null;


    }

    /**
     * Metodo para obtener la version OTA del dispositivo
     * @param respuesta Es el texto recibido desde el dispositivo
     * @return Se retorna el codigo de respuesta enviado por el dispositivo
     */
    protected IOT_CODE_RESULT setCurrentOtaVersionFromReport(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        String version;
        version = api.getJsonString(respuesta, TEXTOS_DIALOGO_IOT.VERSION_OTA.getValorTextoJson());
        if (version != null) {
            setCurrentOtaVersion(version);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

    }



    protected IOT_CODE_RESULT setProgrammerStateFromReport(String respuesta) {
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int estado;
        SCHEDULE_CONDITION schedule = SCHEDULE_CONDITION.INH_PROG;
        estado = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson());
        if (estado >= 0) {
            setProgrammerState(schedule.fromId(estado));
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

    }

    protected IOT_CODE_RESULT setDeviceTypeFromReport(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int tipo;
        IOT_DEVICE_TYPE tipoDispositivo = IOT_DEVICE_TYPE.DESCONOCIDO;
        tipo = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        setDeviceType(tipoDispositivo.fromId(tipo));
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }


    public DEVICE_STATE_CONNECTION simpleCommand(IOT_COMMANDS command) {

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

        return simpleCommand(IOT_COMMANDS.STATUS_DEVICE);


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
                    if (onReceivedTimeoutCommand != null) {
                        Log.i(TAG, "Enviamos timeout: " + getToken(textoComando));
                        onReceivedTimeoutCommand.onReceivedTimeoutCommand(IOT_CODE_RESULT.RESULT_CODE_TIMEOUT);
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

    protected IOT_CODE_RESULT processStatus(String respuesta) {

        setCurrentOtaVersionFromReport(respuesta);
        setConnectionState(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
        setDeviceStateFromReport(respuesta);
        setProgrammerStateFromReport(respuesta);
        setDeviceTypeFromReport(respuesta);
        setCurrentScheduleFromReport(respuesta);
        device2Json();
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setDeviceStateFromReport(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int estado;
        IOT_DEVICE_STATE estadoDispositivo = IOT_DEVICE_STATE.INDETERMINADO;
        estado = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_DISPOSITIVO.getValorTextoJson());
        if (estado > IOT_DEVICE_STATE.INDETERMINADO.getDeviceState()) {
            setDeviceState(estadoDispositivo.fromId(estado));
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

    }


    public DEVICE_STATE_CONNECTION getInfoDeviceCommand() {

        return simpleCommand(IOT_COMMANDS.INFO_DEVICE);

    }

    protected IOT_CODE_RESULT processInfoDevice(String message) {

        setCurrentOtaVersionFromReport(message);
        setConnectionState(DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setDeviceTypeFromReport(message);
        setFreeMemFromReport(message);
        setUpTimeFromReport(message);
        device2Json();
        Log.i(TAG, message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setFreeMemFromReport(String respuesta) {
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int freeMem;
        freeMem = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.FREE_MEM.getValorTextoJson());
        if (freeMem >= 0) {
            setFreeMem(freeMem);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
    }

    protected IOT_CODE_RESULT setUpTimeFromReport(String respuesta) {
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        Double uptime;
        uptime = api.getJsonDouble(respuesta, TEXTOS_DIALOGO_IOT.UPTIME.getValorTextoJson());
        if (uptime >=0 ) {
            setUptime(uptime);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
    }

    protected IOT_CODE_RESULT setCurrentScheduleFromReport(String message) {

        String field;
        if ((field = setFieldStringFromReport(message, TEXTOS_DIALOGO_IOT.PROGRAMA_ACTIVO)) == null) {

            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        } else {
            setActiveSchedule(field);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        }


    }
    public DEVICE_STATE_CONNECTION getScheduleCommand() {

        return simpleCommand(IOT_COMMANDS.GET_SCHEDULE);
    }

    protected IOT_CODE_RESULT processGetSchedule(String message) {
        Log.i(TAG, "Recibida programacion");
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setCurrentScheduleFromReport(message);
        loadSchedules(message);

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }




    private ArrayList<IotScheduleDevice> loadSchedules(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONObject objetoPrograma = null;
        JSONArray arrayProgramas;
        int i;
        IotScheduleDevice programa;


        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            for(i=0;i<arrayProgramas.length();i++) {
                if (schedules == null) schedules = new ArrayList<IotScheduleDevice>();

                objeto = arrayProgramas.getJSONObject(i);
                programa = new IotScheduleDevice(objeto);
                if (programa.getScheduleId().equals(getActiveSchedule())) {
                    programa.setActiveSchedule(true);
                }
                addSchedule(programa);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "Error al obtener los programas del dispositivo");
            return null;
        }


        return schedules;
    }

    protected boolean addSchedule(IotScheduleDevice programa) {

        int i;
        int tam;
        if (schedules == null) {
            schedules = new ArrayList<IotScheduleDevice>();
            tam = 0;
        } else {
            tam = schedules.size();
        }
        for(i=0;i<tam;i++) {

            if (schedules.get(i).getScheduleId().equals(programa.getScheduleId())) {
                Log.i(getClass().toString(), "Programa repetido, no se inserta");
                return  false;
            }

        }
        schedules.add(programa);
        return true;



    }

    protected String setFieldStringFromReport(String message, TEXTOS_DIALOGO_IOT field) {
        ApiDispositivoIot api;
        String dat;
        api = new ApiDispositivoIot();
        dat = api.getJsonString(message, field.getValorTextoJson());
        return dat;


    }

    public DEVICE_STATE_CONNECTION deleteScheduleCommand(String ScheduleId) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), ScheduleId);
        } catch (JSONException e) {
            e.printStackTrace();
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }




        return commandwithParameters(IOT_COMMANDS.REMOVE_SCHEDULE, TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parameters);
    }
        /*

        {
  "comando": {
    "token": "39701fb9-da35-4f2a-9f90-c4842e958df5",
    "date": "26/10/2022 23:32:30",
    "dlgComando": 9,
    "nombreComando": "ELIMINAR_PROGRAMACION"
  },
  "program": {
    "programId": "000100007f"
  }
}
        {
	"idDevice":	"A020A6026046",
	"device":	0,
	"otaVersion":	"2206251749",
	"date":	"26/10/2022 23:32:29",
	"token":	"39701fb9-da35-4f2a-9f90-c4842e958df5",
	"dlgComando":	9,
	"deviceState":	0,
	"programmerState":	1,
	"programId":	"000100007f",
	"currentProgramId":	"002100007f",
	"dlgResultCode":	200
}

         */

    public DEVICE_STATE_CONNECTION commandwithParameters(IOT_COMMANDS command, String labelParameter, JSONObject parameters) {

        String textoComando;
        ApiDispositivoIot api;
        DEVICE_STATE_CONNECTION estado;
        api = new ApiDispositivoIot();
        JSONObject jsonCommand;
        textoComando = api.createSimpleCommand(command);
        try {
            jsonCommand = new JSONObject(textoComando);
            jsonCommand.put(labelParameter, parameters);
            textoComando = jsonCommand.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
            if ((estado = sendCommand(textoComando)) != DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
            return estado;
        }

        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
    }

    protected IOT_CODE_RESULT processDeleteSchedule(String message) {

        IOT_CODE_RESULT result;
        result = getCommandCodeResultFromReport(message);


        return result;
    }

    protected IOT_CODE_RESULT getCommandCodeResultFromReport(String respuesta) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int codResult;
        codResult = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.CODIGO_RESPUESTA.getValorTextoJson());

        return IOT_CODE_RESULT.RESULT_CODE_ERROR.fromId(codResult);
    }


    public DEVICE_STATE_CONNECTION modifyScheduleCommand(IotScheduleDevice schedule) {

        JSONObject parameters;
        DEVICE_STATE_CONNECTION state = DEVICE_STATE_CONNECTION.UNKNOWN;
        parameters = schedule.schedule2Json(schedule);
        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.MODIFY_SCHEDULE, TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parameters);
            return state;
        }
        return state;


/*
{"comando":{"token":"6dc100d0-bc8e-45a7-bcce-e64f4139cc0d","date":"27\/10\/2022 16:06:56","dlgComando":8,"nombreComando":"MODIFICAR_PROGRAMACION"},"program":{"programType":0,"hour":13,"minute":40,"second":0,"programState":1,"programMask":103,"programId":"001230007f","estadoRele":1,"durationProgram":15600}}


 */


    }

    protected IOT_CODE_RESULT processModifySchedule(String message) {


        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected int searchSchedule(String schedule) {

        int i;
        if (getSchedules() == null) {
            Log.w(TAG, "No hay schedules");
            return -1;
        }
        for (i=0; i< getSchedules().size();i++) {
            if (getSchedules().get(i).getScheduleId().equals(schedule)) {
                return i;
            }
        }
        Log.w(TAG, "schedule " + schedule + "no encontrado");
        return -1;

    }

    protected String getScheduleIdFromReport(String respuesta) {

        ApiDispositivoIot api;
        String scheduleId;
        api = new ApiDispositivoIot();
        return api.getJsonString(respuesta, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());

    }



    protected int getFieldIntFromReport(String message, TEXTOS_DIALOGO_IOT field) {
        ApiDispositivoIot api;
        int dat;
        api = new ApiDispositivoIot();
        dat = api.getJsonInt(message, field.getValorTextoJson());
        return dat;


    }

    public DEVICE_STATE_CONNECTION newScheduleCommand(IotScheduleDevice schedule) {

        JSONObject parameters;
        DEVICE_STATE_CONNECTION state = DEVICE_STATE_CONNECTION.UNKNOWN;
        parameters = schedule.schedule2Json(schedule);
        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.NEW_SCHEDULE, TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parameters);
        }
        return state;
    }

    protected IOT_CODE_RESULT processNewSchedule(String message) {

        IOT_CODE_RESULT res;
        IotScheduleDeviceSwitch schedule;
        if ((res = getCommandCodeResultFromReport(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            Log.e(TAG, "error en la peticion de newSchedule");
            return res;
        }

        setProgrammerStateFromReport(message);
        setDeviceStateFromReport(message);

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processModifyParametersDevice(String message) {

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processReset(String message) {

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processFactoryReset(String message) {

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processModifyClock(String message) {

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processUpgradeFirmware(String message) {

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processOtaVersionAvailable(String message) {

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT processErrorCommand(String message) {

        return IOT_CODE_RESULT.RESULT_CODE_NOK;
    }




}