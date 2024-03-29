package net.jajica.libiot;


import android.util.Log;


import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Esta es la clase base para la gestion de todos los dispositivos que se comunican con la aplicacion.
 * En ella se gestiona todo lo comun relativo a todos los dispositivos. En las clases heredadas se tratan
 * los aspectos específicos de cada dispositivo
 */


public abstract class IotDevice implements Serializable {



    protected String publishOtaTopic;
    protected String subscribeOtaTopic;
    protected int endUpgradeFlag;

    public int getEndUpgradeFlag() {
        return endUpgradeFlag;
    }

    public void setEndUpgradeFlag(int endUpgradeFlag) {
        this.endUpgradeFlag = endUpgradeFlag;
    }

    protected ArrivedMessage listenerArrivedMessages;



    /**
     * Tag utilizado para las trazas
     */
    protected final String TAG = getClass().toString();

    /**
     * Es la estructura de alarmas en la que el dispositivo reporta el estado.
     */
    protected IotAlarmDevice alarms;
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
    protected IOT_DEVICE_STATUS deviceStatus;

    /**
     * Estado de la conexion al dispositivo
     */
    protected IOT_DEVICE_STATUS_CONNECTION connectionState;

    /**
     * identidad de la conexion mqtt
     */
    protected IotMqttConnection cnx;

    /**
     * Lista de items de info del dispositivo
     */
    protected ArrayList<IotInfoDevice> listInfoDevice;

    public ArrayList<IotInfoDevice> getListInfoDevice() {
        return listInfoDevice;
    }

    public void setListInfoDevice(ArrayList<IotInfoDevice> listInfoDevice) {
        this.listInfoDevice = listInfoDevice;
    }

    /**
     * Este metodo devuelve la identidad de la conexion mqtt
     * @return Se retorna la identidad de la conexion
     */
    public IotMqttConnection getCnx() {
        return cnx;
    }

    /**
     * Este metodo introduce una conexion previamente realizada con IotMqttConnection
     * @param cnx es la conexion IotMqttConnection
     */
    public void setCnx(IotMqttConnection cnx) {
        this.cnx = cnx;
    }

    /**
     * Puntero a la estructura de la version reportada por el dispositivo
     */
    protected IotOtaVersionAvailable dataOta;
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

    private int numberSchedules;

    protected int finUpgrade;
    /**
     * Mantiene la estructura del dispositivo en formato json
     */
    protected JSONObject dispositivoJson;



    /**
     * Es el estado global de programacion del dispositivo
     */
    protected IOT_SCHEDULE_CONDITION programmerState;

    /**
     * Topic de publicacion para el servidor
     */
    protected String topicOtaVersionAvailable;
    protected long freeMem;
    protected Double uptime;

    public String getCurrentOtaVersion() {
        return currentOtaVersion;
    }

    public String getPublishOtaTopic() {
        return publishOtaTopic;
    }

    public void setPublishOtaTopic(String publishOtaTopic) {
        this.publishOtaTopic = publishOtaTopic;
    }

    public String getSubscribeOtaTopic() {
        return subscribeOtaTopic;
    }

    public void setSubscribeOtaTopic(String subscribeOtaTopic) {
        this.subscribeOtaTopic = subscribeOtaTopic;
    }

    public IotAlarmDevice getAlarms() {
        return alarms;
    }

    public void setAlarms(IotAlarmDevice alarms) {
        this.alarms = alarms;
    }

    /**
     * Obtiene la memoria libre del dispositivo
     * @return Retorna la memoria libre
     */
    public long getFreeMem() {
        return freeMem;
    }

    /**
     * introduce la memoria libre leida desde el dispositivo
     * @param freeMem Es el parametro de memoria
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

    public Double getUptime() {
        return uptime;
    }

    /**
     * Metodo que devuelve el estado global de programacion del dispositivo
     * @return Devuelve el estado global de programacion del dispositivo
     */
    public IOT_SCHEDULE_CONDITION getProgrammerState() {
        return programmerState;
    }

    /**
     * Pone el estado Global de programacion del dispositivo
     * @param programmerState Es el estado que quire introducirse
     */
    public void setProgrammerState(IOT_SCHEDULE_CONDITION programmerState) {
        this.programmerState = programmerState;
    }

    /**
     * Este metodo devuelve la lista de programas del dispositivo
     * @return Devuelve la lista de programas del dispositivo
     */
    private ArrayList<IotScheduleDevice> getSchedules() {
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
     * Actualiza en el dispositivo la version actual
     */
    public void setCurrentOtaVersion(String currentOtaVersion) {
        this.currentOtaVersion = currentOtaVersion;
    }

    public int getNumberSchedules() {
        return numberSchedules;
    }

    public void setNumberSchedules(int numberSchedules) {
        this.numberSchedules = numberSchedules;
    }

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

    protected OnReceivedDeleteScheduleDevice onReceivedDeleteScheduleDevice;

    public interface OnReceivedDeleteScheduleDevice {
        void onReceivedDeleteScheduleDevice(IOT_CODE_RESULT resultCode, String scheduleId);
    }

    public void setOnReceivedDeleteScheduleDevice(OnReceivedDeleteScheduleDevice onReceivedDeleteScheduleDevice) {
        this.onReceivedDeleteScheduleDevice = onReceivedDeleteScheduleDevice;
    }

    public void setOnReceivedDeleteDevice(OnReceivedDeleteScheduleDevice onReceivedDeleteScheduleDevice) {
        this.onReceivedDeleteScheduleDevice = onReceivedDeleteScheduleDevice;
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
        void onReceivedTimeoutCommand(String token);
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



    /*****************Interface spontaneous***************************************************/

    protected OnReceivedSpontaneousStartDevice onReceivedSpontaneousStartDevice;
    public interface OnReceivedSpontaneousStartDevice {
        void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedSpontaneousStartDevice(OnReceivedSpontaneousStartDevice onReceivedSpontaneousStartDevice) {
        this.onReceivedSpontaneousStartDevice = onReceivedSpontaneousStartDevice;
    }

    protected OnReceivedSpontaneousStartSchedule onReceivedSpontaneousStartSchedule;
    public interface OnReceivedSpontaneousStartSchedule {
        void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode);
    }


    protected OnReceivedSpontaneousEndSchedule onReceivedSpontaneousEndSchedule;
    public interface OnReceivedSpontaneousEndSchedule {
        void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode);
    }


    public void setOnReceivedOtaVersionAvailableDevice(OnReceivedOtaVersionAvailableDevice onReceivedOtaVersionAvailableDevice) {
        this.onReceivedOtaVersionAvailableDevice = onReceivedOtaVersionAvailableDevice;
    }

    public void setOnReceivedErrorReportDevice(OnReceivedErrorReportDevice onReceivedErrorReportDevice) {
        this.onReceivedErrorReportDevice = onReceivedErrorReportDevice;
    }

    public void setOnReceivedSpontaneousStartSchedule(OnReceivedSpontaneousStartSchedule onReceivedSpontaneousStartSchedule) {
        this.onReceivedSpontaneousStartSchedule = onReceivedSpontaneousStartSchedule;
    }

    public void setOnReceivedSpontaneousEndSchedule(OnReceivedSpontaneousEndSchedule onReceivedSpontaneousEndSchedule) {
        this.onReceivedSpontaneousEndSchedule = onReceivedSpontaneousEndSchedule;
    }

    protected OnReceivedAlarmReportDevice onReceivedAlarmReportDevice;
    public interface OnReceivedAlarmReportDevice {
        void onReceivedAlarmReportDevice(IOT_TYPE_ALARM_DEVICE alarmType);
    }

    public void setOnReceivedAlarmReportDevice(OnReceivedAlarmReportDevice onReceivedAlarmReportDevice) {
        this.onReceivedAlarmReportDevice = onReceivedAlarmReportDevice;
    }

    public IOT_DEVICE_TYPE getDeviceType() {
        return deviceType;
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
        this.deviceId = deviceId.toUpperCase();
        setPublishTopic("/sub_" + this.deviceId);
        setSubscriptionTopic("/pub_" + this.deviceId);
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
    public IOT_DEVICE_STATUS getDeviceStatus() {
        return deviceStatus;
    }

    /**
     * Pone el estado del dispositivo
     * @param deviceStatus Es el nuevo estado del dispositivo
     */
    public void setDeviceStatus(IOT_DEVICE_STATUS deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    /**
     * Obtiene el estado de la conexion
     * @return
     */
    public IOT_DEVICE_STATUS_CONNECTION getStatusConnection() {
        return connectionState;
    }

    /**
     * Actualiza el estado de la conexion
     * @param connectionState Es el nuevo estado de la conexion
     */
    public void setConnectionState(IOT_DEVICE_STATUS_CONNECTION connectionState) {
        Log.d(TAG, "device " + getDeviceName() + " : " + getDeviceId() + " " + connectionState.toString());
        this.connectionState = connectionState;
    }

    /**
     * Obtiene la estructura OTA donde viene la informacion par upgrade OTA
     * @return
     */
    public IotOtaVersionAvailable getDataOta() {
        return dataOta;
    }

    /**
     * Asigna la estructura ota al dispositivo
     * @param dataOta Es la estructura OTA en la que vienen los datos para upgrade
     */
    public void setDataOta(IotOtaVersionAvailable dataOta) {
        this.dataOta = dataOta;
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
        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        dataOta = null;
        deviceStatus = IOT_DEVICE_STATUS.INDETERMINADO;
        connectionState = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        alarms = new IotAlarmDevice();
        setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        listInfoDevice = null;

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
        dataOta = null;
        deviceStatus = IOT_DEVICE_STATUS.INDETERMINADO;
        connectionState = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        schedules = null;
        alarms = new IotAlarmDevice();
        listInfoDevice = null;

    }

    /**
     * Construator usado para meter comandos
     * @param deviceName es el nombre del dispositivo
     * @param deviceId es el Id del dispositivo
     * @param tipo es el tipo de dispositivo
     * @param cnx Es la identidad de la conexion mqtt de IotMqttConnection
     */
    public IotDevice(String deviceName, String deviceId, int tipo, IotMqttConnection cnx){

        this.deviceName = deviceName;
        this.deviceId = deviceId;
        setDeviceType(tipo);
        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        dataOta = null;
        deviceStatus = IOT_DEVICE_STATUS.INDETERMINADO;
        connectionState = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);
        schedules = null;
        alarms = new IotAlarmDevice();





    }
/*
    public IOT_DEVICE_STATE_CONNECTION RegisterListenerMqttConnection() {

        if (getSubscriptionTopic() == null) {
            return IOT_DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        cnx.setOnListenerArrivedMessaged(getSubscriptionTopic(), new IotMqttConnection.OnArrivedMessage() {
            @Override
            public void arrivedMessage(String topic, MqttMessage message) {

                setConnectionState(IOT_DEVICE_STATE_CONNECTION.DEVICE_CONNECTED);
                procesarMensaje(topic, message);

            }
        });

        return IOT_DEVICE_STATE_CONNECTION.DEVICE_CONNECTED;

    }

 */

    /**
     * Constructor preparado para el dialogo con el dispositivo
     * @param cnx Es un handle de la clase IotMqttConnection para la comunicacion del dispositivo
     */

    public IotDevice(IotMqttConnection cnx){

        versionOtaAvailable = null;
        subscriptionTopic = null;
        publishTopic = null;
        dataOta = null;
        deviceStatus = IOT_DEVICE_STATUS.INDETERMINADO;
        connectionState = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        finUpgrade = 0;
        activeSchedule = null;
        dispositivoJson = new JSONObject();
        setCnx(cnx);
        schedules = null;



    }

    /**
     * Este metodo vuelca la estructura json del dispositivo en la propia estructura de la clase
     * @param jsonDevice es la estructura json
     * @return Se devuelve el resultado de la operacion
     */
    public IOT_JSON_RESULT json2Object(JSONObject jsonDevice) {

        int value;
        try {
            deviceName = jsonDevice.getString(IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson());
            deviceId = jsonDevice.getString(IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson());
            publishTopic = jsonDevice.getString(IOT_LABELS_JSON.TOPIC_PUBLISH.getValorTextoJson());
            subscriptionTopic = jsonDevice.getString(IOT_LABELS_JSON.TOPIC_SUBSCRIPTION.getValorTextoJson());
            value = jsonDevice.getInt(IOT_LABELS_JSON.DEVICE_TYPE.getValorTextoJson());
            deviceType = deviceType.fromId(value);
            value = jsonDevice.optInt(IOT_LABELS_JSON.STATUS_DEVICE.getValorTextoJson());
            deviceStatus = deviceStatus.fromId(value);
            currentOtaVersion = jsonDevice.optString(IOT_LABELS_JSON.OTA_VERSION.getValorTextoJson());
        } catch (JSONException e) {
            return IOT_JSON_RESULT.JSON_CORRUPTO;
        }

        if (!isDeviceValid()) {
            return IOT_JSON_RESULT.JSON_CORRUPTO;
        }
        setDispositivoJson(jsonDevice);

        return IOT_JSON_RESULT.JSON_OK;
    }

    /**
     * Este método construye un objeto json a partir del dispositivo para la insercion en la estructura de configuracion
     * de la aplicacion.
     * @return Se retorna el objeto json creado
     */
    public JSONObject object2Json() {



        if (deviceName != null) {
            try {
                dispositivoJson.put(IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson(), getDeviceName());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (deviceId != null) {
            try {
                dispositivoJson.put(IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson(), getDeviceId());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        if(subscriptionTopic != null) {
            try {
                dispositivoJson.put(IOT_LABELS_JSON.TOPIC_SUBSCRIPTION.getValorTextoJson(), getSubscriptionTopic());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }


        if (publishTopic != null) {
            try {
                dispositivoJson.put(IOT_LABELS_JSON.TOPIC_PUBLISH.getValorTextoJson(), getPublishTopic());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            dispositivoJson.put(IOT_LABELS_JSON.DEVICE_TYPE.getValorTextoJson(), setDeviceTypeFromReport().getValorTipoDispositivo());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            dispositivoJson.put(IOT_LABELS_JSON.STATUS_DEVICE.getValorTextoJson(), getDeviceStatus().getDeviceState());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (currentOtaVersion != null) {
            try {
                dispositivoJson.put(IOT_LABELS_JSON.OTA_VERSION.getValorTextoJson(), getCurrentOtaVersion());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        try {
            dispositivoJson.put(IOT_LABELS_JSON.FREE_MEM.getValorTextoJson(), getFreeMem());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            dispositivoJson.put(IOT_LABELS_JSON.UPTIME.getValorTextoJson(), getUptime());
        } catch (JSONException e) {
            e.printStackTrace();
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

    /**
     * Este metodo chequea que el objeto tenga los elementos imprescindibles para poder
     * trabajar coherentemente con el
     * @return true si es valido. False si es invalido.
     */
    public Boolean isDeviceValid() {

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
        if (!dispositivo.isDeviceValid()) {
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
    public IOT_DEVICE_STATUS_CONNECTION subscribeDevice() {
        return subscribeDevice(this.subscriptionTopic);
    }

    /**
     * Este metodo se subscribe al topic del servidor OTA para dicho dispositivo
     * @return
     */

    public IOT_DEVICE_STATUS_CONNECTION subscribeOtaServer() {
        if (dataOta == null) {
            dataOta = new IotOtaVersionAvailable(getPublishOtaTopic(), getSubscribeOtaTopic());
        }
        return subscribeDevice(getSubscribeOtaTopic());
    }

    public IOT_DEVICE_STATUS_CONNECTION unsubscribeOtaServer() {

        return unSubscribeDevice(getSubscribeOtaTopic());
    }




    /**
     * Subscribe a la aplicacion a un topic diferente del propia para el dispositivo
     * @param topicDevice Es el topic
     * @return Se retorna el resultado de la operacion
     */
    public IOT_DEVICE_STATUS_CONNECTION subscribeDevice(String topicDevice) {

        if (cnx == null) {
            Log.e(TAG, "La conexion es nula para el topic " + topicDevice);
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        if (listenerArrivedMessages == null) {
            listenerArrivedMessages = cnx.setOnListenerArrivedMessaged(topicDevice, new IotMqttConnection.OnArrivedMessage() {
                @Override
                public void arrivedMessage(String topic, MqttMessage message) {
                    setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED);
                    processMessages(topic, message);
                }
            });

        } else {
            listenerArrivedMessages.listTopics.add(topicDevice);
        }

        cnx.subscribeTopic(topicDevice, new IotMqttConnection.OnSubscriptionTopic() {
            @Override
            public void Unsuccessful(IMqttToken iMqttToken) {
                Log.i(TAG, "Error en la subscripcion del topic " + topicDevice);

            }

            @Override
            public void Successful(IMqttToken iMqttToken) {

                Log.i(TAG, "Subscripcion completada al topic " + topicDevice);

            }
        });
        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED;
    }

    /**
     * Es el primer metodo al que se llama cuando llega un mensaje para el dispositivo.
     * El metodo determina si se trata de una respuesta a un comando o de un mensaje espontaneo
     * del dispositivo.
     * @param topic Es el topic del mensaje
     * @param message Es el contenido del mensaje
     */
    private void processMessages(String topic, MqttMessage message) {

        IOT_REPORT_TYPE tipoInforme = null;
        String mensaje;
        IotDevice dispositivo;
        mensaje = new String (message.getPayload());
        IotTools api;
        api = new IotTools();

        IOT_COMMANDS comando;
        comando = api.getCommandId(mensaje);
        if (comando == IOT_COMMANDS.SPONTANEOUS) {
            tipoInforme = IOT_REPORT_TYPE.SPONTANEOUS_REPORT;
        } else {
            tipoInforme = IOT_REPORT_TYPE.COMMAND_REPORT;
        }

        Log.i(TAG, "Recibimos " + tipoInforme.toString() + " de " + topic);

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
        IotTools api;
        api = new IotTools();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        IOT_CODE_RESULT res;


        switch (idComando) {

            case INFO_DEVICE:
                res = processInfoDeviceFromReport(mensaje);
                if (onReceivedInfoDevice != null) {
                    onReceivedInfoDevice.onReceivedInfoDevice(IOT_CODE_RESULT.RESUT_CODE_OK);
                }
                break;
            case STATUS_DEVICE:
                res = processStatusFromReport(mensaje);
                if (onReceivedStatus != null) {
                    onReceivedStatus.onReceivedStatus(IOT_CODE_RESULT.RESUT_CODE_OK);
                }

                break;
            case GET_SCHEDULE:
                res = processGetScheduleFromReport(mensaje);
                if(onReceivedScheduleDevice != null) {
                    onReceivedScheduleDevice.onReceivedScheduleDevice(IOT_CODE_RESULT.RESUT_CODE_OK);
                } else {
                    Log.w(TAG, "No se procesa el listener onReceivedScheduleDevice porque es nulo");
                    Log.w(TAG, "device es : IotDeviceGetSchedule" + hashCode());
                }

                break;
            case NEW_SCHEDULE:
                res = processNewSchedule(mensaje);
                if (onReceivedNewSchedule != null) {
                    onReceivedNewSchedule.onReceivedNewSchedule(res);
                }

                break;
            case REMOVE_SCHEDULE:
                String scheduleId = null;
                res = processDeleteScheduleFromReport(mensaje);
                if (res == IOT_CODE_RESULT.RESUT_CODE_OK) {
                scheduleId = getFieldStringFromReport(mensaje, IOT_LABELS_JSON.SCHEDULE_ID);
            }

                if (onReceivedDeleteScheduleDevice != null) {
                    onReceivedDeleteScheduleDevice.onReceivedDeleteScheduleDevice(res, scheduleId);
                } else {
                    Log.w(TAG, "No se procesa el listener onReceivedDeleteScheduleDevice porque es nulo");
                    Log.w(TAG, "device es : IotDevice RemoveSchedule" + hashCode());
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
            case GET_OTA_VERSION_AVAILABLE:
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

        Log.d(TAG, "Procesado comando " + idComando + " de " + topic);

    }

    /**
     * Esta funcion procesa los mensajes espontaneos
     * @param topic Es el topic de subscripcion
     * @param message Es el contenido del mensaje espontaneo
     */
    protected void processSpontaneous(String topic, MqttMessage message) {
        IotDevice dispositivo = null;
        IOT_SPONTANEOUS_TYPE typeInform;
        String mensaje = new String(message.getPayload());
        typeInform = getSpontaneousType(mensaje);
        IOT_CODE_RESULT result;
        switch (typeInform) {
            case START_DEVICE:
                result = processStartDevice(mensaje);
                if (onReceivedSpontaneousStartDevice != null) {
                    onReceivedSpontaneousStartDevice.onReceivedSpontaneousStartDevice(result);
                }
                break;
            case UPGRADE_FIRMWARE_FOTA:
                break;
            case START_SCHEDULE:
                result = processStartSchedule(mensaje);
                if (onReceivedSpontaneousStartSchedule != null) {
                    onReceivedSpontaneousStartSchedule.onReceivesSpontaneousStartSchedule(result);
                }
                break;
            case COMANDO_APLICACION:
                break;
            case CAMBIO_ESTADO:
                break;
            case END_SCHEDULE:
                result = processEndSchedule(mensaje);
                if (onReceivedSpontaneousEndSchedule != null) {
                    onReceivedSpontaneousEndSchedule.onReceivesSpontaneousEndSchedule(result);
                }
                break;
            case INFORME_ALARMA:
                IOT_TYPE_ALARM_DEVICE alarm = processAlarmReport(mensaje);
                if (onReceivedAlarmReportDevice != null) {
                    onReceivedAlarmReportDevice.onReceivedAlarmReportDevice(alarm);
                }
                break;
            case CAMBIO_UMBRAL_TEMPERATURA:
                break;
            case CAMBIO_ESTADO_APLICACION:
                break;
            case ERROR:
            case ESPONTANEO_DESCONOCIDO:
                processErrorCommand(mensaje);
                if (onReceivedErrorReportDevice != null) {
                    onReceivedErrorReportDevice.onReceivedErrorReportDevice(IOT_CODE_RESULT.RESULT_CODE_NOK);
                }
                break;
        }

    }

    /**
     * Metodo para obtener la version OTA del dispositivo
     * @param respuesta Es el texto recibido desde el dispositivo
     * @return Se retorna el codigo de respuesta enviado por el dispositivo
     */
    protected IOT_CODE_RESULT setCurrentOtaVersionFromReport(String respuesta) {

        IotTools api;
        api = new IotTools();
        String version;
        version = api.getJsonString(respuesta, IOT_LABELS_JSON.OTA_VERSION.getValorTextoJson());
        if (version != null) {
            setCurrentOtaVersion(version);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

    }


    /**
     * Este método actualiza desde el json el estado de programacion del dispositivo
     * @param respuesta es el mensaje json
     * @return Ok si se encuentra la etiqueta
     */
    protected IOT_CODE_RESULT setProgrammerStateFromReport(String respuesta) {
        IotTools api;
        api = new IotTools();
        int estado;
        IOT_SCHEDULE_CONDITION schedule = IOT_SCHEDULE_CONDITION.INH_PROG;
        estado = api.getJsonInt(respuesta, IOT_LABELS_JSON.STATUS_PROGRAMMER.getValorTextoJson());
        if (estado >= 0) {
            setProgrammerState(schedule.fromId(estado));
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

    }

    /**
     * Actualiza el tipo de dispositivo que viene en el mensaje json
     * @param respuesta es el mensaje json
     * @return Se retorna OK si se ha encontrado la etiqueta
     */
    protected IOT_CODE_RESULT setDeviceTypeFromReport(String respuesta) {

        IotTools api;
        api = new IotTools();
        int tipo;
        IOT_DEVICE_TYPE tipoDispositivo = IOT_DEVICE_TYPE.UNKNOWN;
        tipo = api.getJsonInt(respuesta, IOT_LABELS_JSON.DEVICE_TYPE.getValorTextoJson());
        setDeviceType(tipoDispositivo.fromId(tipo));
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    /**
     * Este metodo genera el mensaje json necesario para escribir un comando al dispositivo.
     * Este metodo se utiliza cuando el comando no lleva parametros.
     * @param command Es la identidad del comando
     * @return Se retorna el estado de conexion del dispositivo despues de lanzar la operacion
     */
    protected IOT_DEVICE_STATUS_CONNECTION simpleCommand(IOT_COMMANDS command) {

        String textoComando;
        IotTools api;
        IOT_DEVICE_STATUS_CONNECTION estado;
        api = new IotTools();
        textoComando = api.createSimpleCommand(command);
        if ((estado = sendCommand(textoComando)) != IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
            setConnectionState(estado);
            return estado;
        }
        setConnectionState(estado);

        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
    }

    /**
     * Metodo para enviar un comando para un topic especifico diferente al configurado en el dispositivo
     * @param command es la identidad el comando
     * @param topic es el topic de publicacion
     * @return Se devuelve el estado de conexion del dispositivo despues de lanzar la peticion
     */
    protected IOT_DEVICE_STATUS_CONNECTION simpleCommand(IOT_COMMANDS command, String topic) {

        String textoComando;
        IotTools api;
        IOT_DEVICE_STATUS_CONNECTION estado;
        api = new IotTools();
        textoComando = api.createSimpleCommand(command);
        if ((estado = sendCommand(textoComando, topic)) != IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
            Log.i(TAG, "Resultado del comando con timeout pendiente: " + estado.toString());
            return estado;
        }
        Log.i(TAG, "Resultado del comando con timeout pendiente: " + estado.toString());
        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
    }


    /**
     * metodo para invocar el comando status al dispositivo
     * @return Se devuelve el estado de la conexion del dispositivo despues de lanzar la peticion.
     */
    public IOT_DEVICE_STATUS_CONNECTION commandGetStatusDevice() {

        return simpleCommand(IOT_COMMANDS.STATUS_DEVICE);

    }

    /**
     * Metodo para invocar un reset al dispositivo
     * @return
     */

    public IOT_DEVICE_STATUS_CONNECTION commandResetDevice() {

        return simpleCommand(IOT_COMMANDS.RESET);
    }

    /**
     * Metodo para invocar un factory reset al dispositivo
     * @return
     */
    public IOT_DEVICE_STATUS_CONNECTION commandFactoryReset() {

        return simpleCommand(IOT_COMMANDS.FACTORY_RESET);
    }


    public IOT_DEVICE_STATUS_CONNECTION commandUpgradeFirmware() {

        JSONObject parameters;
        IOT_DEVICE_STATUS_CONNECTION state = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        parameters =dataOta.otaData2Json();
        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.UPGRADE_FIRMWARE, IOT_LABELS_JSON.DLG_UPGDRADE_FIRMWARE.getValorTextoJson(), parameters);
        }
        return state;

    }


    /**
     * Metodo para lanzar la peticion con el comando json ya construido
     * @param textoComando Es el texto del comando json
     * @return Se devuelve el estado de la conexion del dispositivo despues de lanzar la peticion
     */
    protected IOT_DEVICE_STATUS_CONNECTION sendCommand(String textoComando) {
        Log.i(TAG, "Enviando comando y esperando timeout con token " + getTokenFromReport(textoComando));
        return sendCommand(textoComando, getPublishTopic());
    }

    /**
     * Metodo para lanzar la peticion con el comando json ya construido sobre un topic especifico
     * @param textoComando Es el comando ya construido
     * @param topic es el topic especifico
     * @return Se devuelve el estado de la conexion del dispositivo despues de lanzar la peticion
     */
    protected IOT_DEVICE_STATUS_CONNECTION sendCommand(String textoComando, String topic) {
        Timer timerCommand = null;
        IOT_DEVICE_STATUS_CONNECTION status;
        if (topic == null) {
            Log.w(TAG, "topic vacio. Comando : " + textoComando);
        }
        if (textoComando == null) {
            Log.w(TAG, "textoComando vacio. topic : " + topic);
        }
        if (cnx == null) {
            Log.e(TAG, "No hay conexion mqtt en el dispositivo");
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        if ((status = cnx.publishTopic(topic, textoComando)) != IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
            setConnectionState(status);
            return status;
        }
        setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE);
        timerCommand = new Timer();

        timerCommand.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getStatusConnection() != IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED) {
                    if (onReceivedTimeoutCommand != null) {
                        setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_DISCONNECTED);
                        onReceivedTimeoutCommand.onReceivedTimeoutCommand(getTokenFromReport(textoComando));
                        Log.e(TAG, "Enviamos timeout: " + getTokenFromReport(textoComando));
                    }
                } else {
                    Log.i(TAG, "cancelado timeout al comando con token. " + getTokenFromReport(textoComando));
                }
            }
        }, 10000);
        Log.i(TAG, "Resultado del comando con token " + getTokenFromReport(textoComando) + " con timeout pendiente: " + IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE);
        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
    }


    /**
     * Este metodo actualiza los miembros del objeto con la informacion de la respuesta al comando status
     * @param respuesta Es la respuesta json
     * @return Es el codigo de respuesta del comando status
     */
    protected IOT_CODE_RESULT processStatusFromReport(String respuesta) {


        processCommonParameters(respuesta);

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    /**
     * Este metodo actualiza el estado del dispositivo desde la respuesta del comando
     * @param respuesta es la respuesta json
     * @return Se devuelve OK se la etiqueta existe
     */
    protected IOT_CODE_RESULT setDeviceStateFromReport(String respuesta) {

        IotTools api;
        api = new IotTools();
        int estado;
        IOT_DEVICE_STATUS estadoDispositivo = IOT_DEVICE_STATUS.INDETERMINADO;
        estado = api.getJsonInt(respuesta, IOT_LABELS_JSON.STATUS_DEVICE.getValorTextoJson());
        if (estado > IOT_DEVICE_STATUS.INDETERMINADO.getDeviceState()) {
            setDeviceStatus(estadoDispositivo.fromId(estado));
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            Log.e(TAG, "Error al obtener el estado del dispositivo en el Status");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

    }


    /**
     * Metodo para invocar el comando info sobre el dispositivo
     * @return Se devuelve el estado de la conexion del dispositivo despues de lanzar la peticion
     */
    public IOT_DEVICE_STATUS_CONNECTION commandGetInfoDevice() {

        return simpleCommand(IOT_COMMANDS.INFO_DEVICE);

    }

    protected IOT_CODE_RESULT json2InfoDevice() {

        int i;
        IotInfoDevice info;
        JSONArray labels;
        String name;
        String value;
        labels = object2Json().names();
        if (listInfoDevice == null) {
            listInfoDevice = new ArrayList<>();
        } else {
            listInfoDevice.clear();
        }
        for(i=0;i<labels.length();i++) {
            try {
                info = new IotInfoDevice();
                name = labels.getString(i);
                value = dispositivoJson.getString(name);
                info.setItemLabelInfoDevice(name);
                info.setItemValueInfoDevice(value);
                info.setItemConfigurableInfoDevice(isConfigurableField(name));
                listInfoDevice.add(info);
            } catch (JSONException e) {
                e.printStackTrace();
                return IOT_CODE_RESULT.RESULT_CODE_NOK;
            }



        }


        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    /**
     * Este metodo actualiza la el objeto con la respuesta json del dispositivo
     * @param message Es el mensaje json
     * @return Es el codigo de la respuesta del comando
     */
    protected IOT_CODE_RESULT processInfoDeviceFromReport(String message) {


        setCurrentOtaVersionFromReport(message);
        setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED);
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setDeviceTypeFromReport(message);
        setFreeMemFromReport(message);
        setUpTimeFromReport(message);
        object2Json();
        json2InfoDevice();

        Log.i(TAG, message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    /**
     * Actualiza el objeto con la informacion de memoria libre del comando info
     * @param respuesta Es la respuesta json
     * @return OK si se encuentra la etiqueta
     */
    protected IOT_CODE_RESULT setFreeMemFromReport(String respuesta) {
        IotTools api;
        api = new IotTools();
        int freeMem;
        freeMem = api.getJsonInt(respuesta, IOT_LABELS_JSON.FREE_MEM.getValorTextoJson());
        if (freeMem >= 0) {
            setFreeMem(freeMem);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
    }

    /**
     * Actualiza el objeto desde la respuesta info
     * @param respuesta Es la respuesta json
     * @return OK si se encuentra la etiqueta
     */
    protected IOT_CODE_RESULT setUpTimeFromReport(String respuesta) {
        IotTools api;
        api = new IotTools();
        Double uptime;
        uptime = api.getJsonDouble(respuesta, IOT_LABELS_JSON.UPTIME.getValorTextoJson());
        if (uptime >=0 ) {
            setUptime(uptime);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        } else {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
    }

    /**
     * Actualiza el objeto con el programa en curso
     * @param message es la respuesta json
     * @return OK si se encuentra la etiqueta
     */
    protected IOT_CODE_RESULT setCurrentScheduleFromReport(String message) {

        String field;
        if ((field = getFieldStringFromReport(message, IOT_LABELS_JSON.ACTIVE_SCHEDULE)) == null) {

            setActiveSchedule("");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;

        } else {
            setActiveSchedule(field);
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        }


    }
    public IOT_DEVICE_STATUS_CONNECTION commandGetScheduleDevice() {

        return simpleCommand(IOT_COMMANDS.GET_SCHEDULE);
    }

    /**
     * Este metodo actualiza el objeto con los programas del dispositivo. La informacion la guarda
     * en el array de programas del objeto.
     * @param message Es la respuesta json
     * @return  Se devuelve el resultado de la  peticion del comando
     */
    protected IOT_CODE_RESULT processGetScheduleFromReport(String message) {
        Log.i(TAG, "Recibido mensaje de programacion de " + getSubscriptionTopic());
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setCurrentScheduleFromReport(message);
        //loadSchedulesFromReport(message);


        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    /**
     * Este metodo carga en la estructura IotSchedulesDevice los programas almacenados en el dispositivo
     * @param textoRecibido Es el mensaje json del dispositivo
     * @return La lista de programas. Null si no hay ninguno
     */
    private ArrayList<IotScheduleDevice> loadSchedulesFromReport(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONObject objetoPrograma = null;
        JSONArray arrayProgramas;
        int i;
        IotScheduleDevice programa;




        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(IOT_LABELS_JSON.SCHEDULE.getValorTextoJson());
            for(i=0;i<arrayProgramas.length();i++) {
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

    /**
     * Funcion para añadir un schedule a la estructura
     * @param programa es el schedule a añadir
     * @return true si lo ha añadido. False en caso contrario
     */
    private boolean addSchedule(IotScheduleDevice programa) {

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


    /**
     * Comando al dispositivo para borrar un programa especifico
     * @param ScheduleId Es el idSchedule a borrar
     * @return Se retorna el estado de conexion del dispositivo despues de lanzar la peticion
     */
    public IOT_DEVICE_STATUS_CONNECTION commandDeleteScheduleDevice(String ScheduleId) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put(IOT_LABELS_JSON.SCHEDULE_ID.getValorTextoJson(), ScheduleId);
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }




        return commandwithParameters(IOT_COMMANDS.REMOVE_SCHEDULE, IOT_LABELS_JSON.SCHEDULE.getValorTextoJson(), parameters);
    }

    /**
     * Metodo para lanzar un comando con parametros
     * @param command Es la identidad del comando
     * @param labelParameter Es la etiqueta del parametro
     * @param parameters Es el conjunto de parametros de la etiqueta
     * @return Se retorna el estado de conexion del dispositivo despues de lanzar la peticion
     */
    public IOT_DEVICE_STATUS_CONNECTION commandwithParameters(IOT_COMMANDS command, String labelParameter, JSONObject parameters) {

        String textoComando;
        IotTools api;
        IOT_DEVICE_STATUS_CONNECTION estado;
        api = new IotTools();
        JSONObject jsonCommand;
        textoComando = api.createSimpleCommand(command);
        try {
            jsonCommand = new JSONObject(textoComando);
            jsonCommand.put(labelParameter, parameters);
            textoComando = jsonCommand.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
            if ((estado = sendCommand(textoComando)) != IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
                setConnectionState(estado);
                return estado;
        }
            setConnectionState(estado);

        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
    }

    /**
     *  Metodo que devuelve la respuesta de. comando deleteScheduleCommand
     * @param message Es el mensaje json
     * @return Se retorna el codigo de respuesta del borrado
     */
    protected IOT_CODE_RESULT processDeleteScheduleFromReport(String message) {

        IOT_CODE_RESULT result;
        result = getCommandCodeResultFromReport(message);


        return result;
    }

    /**
     * Metodo que lee del report el codigo de respuesta
     * @param respuesta es el mensaje json
     * @return Se retorna el codigo de la respuesta
     */
    protected IOT_CODE_RESULT getCommandCodeResultFromReport(String respuesta) {

        IotTools api;
        api = new IotTools();
        int codResult;
        codResult = api.getJsonInt(respuesta, IOT_LABELS_JSON.RESULT_CODE.getValorTextoJson());

        return IOT_CODE_RESULT.RESULT_CODE_ERROR.fromId(codResult);
    }


    /**
     * Metodo para lanzar el comando de modificar Schedule
     * @param schedule Es un objeto Schedule que contiene los parametros a modificar
     * @return Se retorna el estado de conexion del dispositivo despues de lanzar la peticion
     */
    public IOT_DEVICE_STATUS_CONNECTION commandModifyScheduleDevice(IotScheduleDevice schedule) {

        JSONObject parameters;
        IOT_DEVICE_STATUS_CONNECTION state = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        parameters = schedule.schedule2Json(schedule);
        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.MODIFY_SCHEDULE, IOT_LABELS_JSON.SCHEDULE.getValorTextoJson(), parameters);
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

        IotTools api;
        String scheduleId;
        api = new IotTools();
        return api.getJsonString(respuesta, IOT_LABELS_JSON.SCHEDULE_ID.getValorTextoJson());

    }



    protected int getFieldIntFromReport(String message, IOT_LABELS_JSON field) {
        IotTools api;
        int dat;
        api = new IotTools();
        dat = api.getJsonInt(message, field.getValorTextoJson());
        return dat;


    }

    protected double getFieldDoubleFromReport(String message, IOT_LABELS_JSON field) {

        IotTools api;
        double dat;
        api = new IotTools();
        dat = api.getJsonDouble(message, field.getValorTextoJson());
        return dat;


    }

    protected String getFieldStringFromReport(String message, IOT_LABELS_JSON field) {

        IotTools api;
        String stream;
        api = new IotTools();
        stream = api.getJsonString(message, field.getValorTextoJson());
        return stream;
    }

    protected Boolean getFieldBooleanFromReport(String message, IOT_LABELS_JSON field) {

        IotTools api;
        Boolean dat;
        api = new IotTools();
        dat = api.getJsonboolean(message, field.getValorTextoJson());
        return dat;
    }






    public IOT_DEVICE_STATUS_CONNECTION commandNewScheduleDevice(IotScheduleDevice schedule) {

        JSONObject parameters;
        IOT_DEVICE_STATUS_CONNECTION state = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;
        parameters = schedule.schedule2Json(schedule);
        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.NEW_SCHEDULE, IOT_LABELS_JSON.SCHEDULE.getValorTextoJson(), parameters);
        }
        return state;
    }

    protected IOT_CODE_RESULT processNewSchedule(String message) {

        IOT_CODE_RESULT res;
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

        IOT_CODE_RESULT code;
        Log.i(TAG, "se procesa la respuesta del servidor OTA");
        if (dataOta == null) {
            dataOta = new IotOtaVersionAvailable(getPublishOtaTopic(), getSubscribeOtaTopic());
        }
        if ((code = dataOta.setDataOtaFromReport(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            return code;
        }
        setVersionOtaAvailable(dataOta.getOtaVersionAvailable());

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    protected IOT_CODE_RESULT processErrorCommand(String message) {

        return IOT_CODE_RESULT.RESULT_CODE_NOK;
    }

    protected IOT_SPONTANEOUS_TYPE getSpontaneousType(String texto) {

        int tipoInforme;
        IotTools api;
        api = new IotTools();
        IOT_SPONTANEOUS_TYPE tipoEspontaneo = IOT_SPONTANEOUS_TYPE.ESPONTANEO_DESCONOCIDO;
        tipoInforme = api.getJsonInt(texto, IOT_LABELS_JSON.TYPE_SPONTANEOUS_REPORT.getValorTextoJson());

        tipoEspontaneo = tipoEspontaneo.fromId(tipoInforme);

        return tipoEspontaneo;
    }

    protected IOT_CODE_RESULT processStartDevice(String message) {



        return processCommonParameters(message);
    }

    protected IOT_CODE_RESULT processStartSchedule(String message) {

        return processCommonParameters(message);
    }

    protected IOT_CODE_RESULT processEndSchedule(String message) {

        return processCommonParameters(message);
    }

    protected IOT_TYPE_ALARM_DEVICE processAlarmReport(String message) {

        IOT_TYPE_ALARM_DEVICE alarmType;
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setCurrentScheduleFromReport(message);
        return alarmType = identifyTypeAlarm(message);
    }

    protected IOT_CODE_RESULT processCommonParameters(String message) {


        setCurrentOtaVersionFromReport(message);
        setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED);
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setDeviceTypeFromReport(message);
        setCurrentScheduleFromReport(message);
        setEndUpgradeFlagFromReport(message);
        object2Json();

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }



    protected IOT_TYPE_ALARM_DEVICE identifyTypeAlarm(String message) {

        IOT_ALARM_VALUE alarm = IOT_ALARM_VALUE.ALARM_UNKNOWN;
        int i;
        i = getFieldIntFromReport(message, IOT_LABELS_JSON.WIFI_ALARM);
        if (i >= 0) {
            alarms.setWifiAlarm(alarm.fromId(i));
            Log.i(TAG, "detectada alarma " + IOT_LABELS_JSON.WIFI_ALARM.getValorTextoJson() + "=" + String.valueOf(i));
            return IOT_TYPE_ALARM_DEVICE.WIFI_ALARM;
        }

        i = getFieldIntFromReport(message, IOT_LABELS_JSON.MQTT_ALARM);
        if (i >= 0) {
            alarms.setMqttAlarm(alarm.fromId(i));
            Log.i(TAG, "detectada alarma " + IOT_LABELS_JSON.MQTT_ALARM.getValorTextoJson() + "=" + String.valueOf(i));
            return IOT_TYPE_ALARM_DEVICE.MQTT_ALARM;
        }
        i = getFieldIntFromReport(message, IOT_LABELS_JSON.NTP_ALARM);
        if (i >= 0) {
            alarms.setNtpAlarm(alarm.fromId(i));
            Log.i(TAG, "detectada alarma " + IOT_LABELS_JSON.NTP_ALARM.getValorTextoJson() + "=" + String.valueOf(i));
            return IOT_TYPE_ALARM_DEVICE.NTP_ALARM;
        }
        i = getFieldIntFromReport(message, IOT_LABELS_JSON.NVS_ALARM);
        if (i >= 0) {
            alarms.setNvsAlarm(alarm.fromId(i));
            Log.i(TAG, "detectada alarma " + IOT_LABELS_JSON.NVS_ALARM.getValorTextoJson() + "=" + String.valueOf(i));
            return IOT_TYPE_ALARM_DEVICE.NVS_ALARM;
        }
        return IOT_TYPE_ALARM_DEVICE.UNKNOWN_ALARM;

    }

    protected String getTokenFromReport(String message) {

        return getFieldStringFromReport(message, IOT_LABELS_JSON.KEY_COMMAND);


    }

    public IOT_DEVICE_STATUS_CONNECTION getOtaVersionAvailableCommand() {
        JSONObject parameter;
        JSONObject command;

        command = new JSONObject();
        parameter = new JSONObject();

        try {
            parameter.put(IOT_LABELS_JSON.OTA_VERSION_TYPE.getValorTextoJson(), getDeviceType().toString());
            command.put(IOT_LABELS_JSON.DLG_UPGDRADE_FIRMWARE.getValorTextoJson(), parameter);
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }

        return cnx.publishTopic(dataOta.getTopicPublicacion(), command.toString());
        //return simpleCommand(IOT_COMMANDS.GET_OTA_VERSION_AVAILABLE, dataOta.getTopicPublicacion());

    }

    protected IOT_DEVICE_STATUS_CONNECTION setParametersDevicesCommand() {



        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED;

    }

    protected IOT_CODE_RESULT setEndUpgradeFlagFromReport(String message) {

        IotTools api;
        api = new IotTools();
        int endUpgrade;
        endUpgrade = api.getJsonInt(message, IOT_LABELS_JSON.END_UPGRADE.getValorTextoJson());
        if (endUpgrade < 0) {
            Log.w(TAG, "No aparece fin upgrade en el arranque");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setEndUpgradeFlag(endUpgrade);

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    public IOT_DEVICE_STATUS_CONNECTION unSubscribeDevice(String topic) {

        int i;
        if (listenerArrivedMessages == null) {
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        if (listenerArrivedMessages.listTopics == null) {
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;
        }
        for (i=0;i<listenerArrivedMessages.listTopics.size(); i++) {
            if(listenerArrivedMessages.listTopics.get(i).equals(topic)) {
                cnx.unSubscribeTopic(topic);
                listenerArrivedMessages.listTopics.remove(i);
                if (listenerArrivedMessages.listTopics.size() == 0) {
                    listenerArrivedMessages = null;
                    return IOT_DEVICE_STATUS_CONNECTION.DEVICE_NO_SUBSCRIPT;
                }
            }
        }
        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_SUBSCRIPTION;


    }

    public IOT_DEVICE_STATUS_CONNECTION unSubscribeDevice() {

        return unSubscribeDevice(getSubscriptionTopic());
    }


    /**
     * Este metodo valida si el programa que se quiere añadir o modificar entra en conflicto
     * con algun otro ya establecido.
     * @param schedule es el programa a chequear
     * @return true si se puede añadir o modificar. False en caso contrario.
     */
    public Boolean checkValidSchedule(IotScheduleDevice schedule, String oldScheduleId) {

        int i;

        if (getSchedules() != null) {
            for(i=0;i<getSchedules().size();i++) {
                if (getSchedules().get(i).getScheduleId().equals(oldScheduleId)) {
                    continue;
                }

                if (!checkValidProg(
                        getSchedules().get(i).getHour(),
                        getSchedules().get(i).getMinute(),
                        getSchedules().get(i).getSecond(),
                        getSchedules().get(i).getDuration(),
                        schedule.getHour(),
                        schedule.getMinute(),
                        schedule.getSecond(),
                        schedule.getDuration()
                )) {
                    Log.w(TAG, "El intervalo no es valido");
                    return false;
                }

            }
        }


        Log.i(TAG, "El schedule es valido y se puede lanzar");
        return true;


        /*
        int beginInterval;
        int endInterval;
        int seconds;
        int i;
        ArrayList<IotScheduleDevice> listSchedules;


        listSchedules = getSchedules();
        seconds = (schedule.getHour() * 3600) + (schedule.getMinute() * 60) + schedule.getSecond();

        if (listSchedules != null) {
            for(i=0;i<listSchedules.size();i++) {
                if (listSchedules.get(i).getScheduleId().equals(oldScheduleId)) {
                    continue;
                }
                beginInterval = (listSchedules.get(i).getHour() * 3600) +
                        (listSchedules.get(i).getMinute() * 60) + listSchedules.get(i).second;
                endInterval = beginInterval + listSchedules.get(i).duration;
                if (!checkInterval(seconds, beginInterval, endInterval)) {
                    return false;

                } else {
                    if(!checkInterval((seconds + listSchedules.get(i).duration), beginInterval, endInterval)) {
                        return false;
                    }
                }
            }
        }
        return true;

         */
    }

    private Boolean checkInterval(int seconds, int beginInterval, int endInterval) {

        if((seconds >= beginInterval) && (seconds < endInterval)) {
            return false;
        } else {
            return true;
        }
    }


    public IotDeviceSwitch unknown2Switch() {

        IotDeviceSwitch device;
        device = new IotDeviceSwitch();
        device.setDeviceName(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceStatus(this.getDeviceStatus());
        device.setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
        device.object2Json();
        return device;
    }

    public IotDeviceThermometer unknown2Thermometer() {

        IotDeviceThermometer device;
        device = new IotDeviceThermometer();
        device.setDeviceName(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceStatus(this.getDeviceStatus());
        device.setDeviceType(IOT_DEVICE_TYPE.THERMOMETER);
        device.object2Json();
        return device;
    }

    public IotDeviceThermostat unknown2Thermostat() {
        IotDeviceThermostat device;
        device = new IotDeviceThermostat();
        device.setDeviceName(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceStatus(this.getDeviceStatus());
        device.setDeviceType(IOT_DEVICE_TYPE.CRONOTERMOSTATO);
        device.object2Json();
        return device;
    }


    protected Boolean isConfigurableField(String field) {

        IOT_LABELS_JSON label  = IOT_LABELS_JSON.COMMAND;
        Boolean conf;

        label = label.fromlabel(field);
        return label.isConfigurable();

    }

    public IOT_DEVICE_STATUS_CONNECTION commandModifyAppParameter(JSONObject parameters) {



        IOT_DEVICE_STATUS_CONNECTION state = IOT_DEVICE_STATUS_CONNECTION.UNKNOWN;

        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.MODIFY_PARAMETER_DEVICE, IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson(), parameters);
            return state;
        }

        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
    }


    protected Boolean checkValidProg(int hour1, int minute1, int second1,int duration1, int hour2, int minute2, int second2, int duration2) {


        double interval1;
        double interval2;
        double intervalSchedule1;
        double intervalSchedule2;
        IotTools tool;
        tool = new IotTools();

        interval1 = tool.secondOfDay(hour1, minute1, second1);
        interval2 = interval1 + duration1;
        intervalSchedule1 = tool.secondOfDay(hour2, minute2, second2);
        intervalSchedule2 = intervalSchedule1 + duration2;

        if ((intervalSchedule1 >= interval1) && (intervalSchedule1 <= interval2)) {
            Log.w(TAG, "La hora inicial del schedule esta dentro del intervalo del programa ");
            return false;
        }

        if ((intervalSchedule2 >= interval1) && (intervalSchedule2 <= interval2)) {
            Log.w(TAG, "La hora final del schedule esta dentro del intervalo del programa ");
            return false;
        }

        if ((interval1 >= intervalSchedule1) && (interval2 <= intervalSchedule2)) {
            Log.w(TAG, "El intervalo elegido solapa el existente");
            return false;
        }

        return true;

    }



}