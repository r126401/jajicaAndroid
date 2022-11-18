package net.jajica.libiot;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.Serializable;

public class IotDeviceThermostat extends IotDeviceThermometer implements Serializable {




    private Boolean MasterSensor;
    private String RemoteSensor;
    private Double thresholdTemperature;
    private IOT_SWITCH_RELAY relay;


    public Double getThresholdTemperature() {
        return thresholdTemperature;
    }

    public IOT_SWITCH_RELAY getRelay() {
        return relay;
    }

    public void setRelay(IOT_SWITCH_RELAY relay) {
        this.relay = relay;
    }

    public void setThresholdTemperature(Double thresholdTemperature) {
        this.thresholdTemperature = thresholdTemperature;
    }

    public Boolean getMasterSensor() {
        return MasterSensor;
    }

    public void setMasterSensor(Boolean masterSensor) {
        MasterSensor = masterSensor;
    }

    public String getRemoteSensor() {
        return RemoteSensor;
    }

    public void setRemoteSensor(String remoteSensor) {
        RemoteSensor = remoteSensor;
    }



    public IotDeviceThermostat() {

        super();
        setPublishOtaTopic("OtaIotCronoTemp");
        setSubscribeOtaTopic("newVersionOtaIotCronoTemp");
        setDeviceType(IOT_DEVICE_TYPE.CRONOTERMOSTATO);

    }

    public IotDeviceThermostat(IotMqttConnection cnx) {

        super(cnx);
        setPublishOtaTopic("OtaIotCronoTemp");
        setSubscribeOtaTopic("newVersionOtaIotCronoTemp");
        setDeviceType(IOT_DEVICE_TYPE.CRONOTERMOSTATO);

    }


    @Override
    protected IOT_CODE_RESULT processStartDevice(String message) {

        setStateRelayFromReport(message);
        setSensorFromReport(message);
        setThresholdTemperatureFromReport(message);
        return super.processStartDevice(message);
    }


    @Override
    protected IOT_CODE_RESULT processStartSchedule(String message) {
        return super.processStartSchedule(message);
    }

    @Override
    protected IOT_CODE_RESULT processEndSchedule(String message) {
        return super.processEndSchedule(message);
    }

    @Override
    protected IOT_TYPE_ALARM_DEVICE processAlarmReport(String message) {
        return super.processAlarmReport(message);
    }

    @Override
    protected IOT_CODE_RESULT processInfoDeviceFromReport(String message) {
        return super.processInfoDeviceFromReport(message);
    }

    @Override
    protected IOT_CODE_RESULT processStatusFromReport(String respuesta) {
        return super.processStatusFromReport(respuesta);
    }

    @Override
    protected IOT_CODE_RESULT processGetScheduleFromReport(String message) {
        return super.processGetScheduleFromReport(message);
    }

    @Override
    protected IOT_CODE_RESULT processNewSchedule(String message) {
        return super.processNewSchedule(message);
    }

    @Override
    protected IOT_CODE_RESULT processDeleteScheduleFromReport(String message) {
        return super.processDeleteScheduleFromReport(message);
    }

    @Override
    protected IOT_CODE_RESULT processModifySchedule(String message) {
        return super.processModifySchedule(message);
    }



    protected IOT_CODE_RESULT setStateRelayFromReport(String message) {

        IotTools api;
        int i;
        api = new IotTools();
        i = api.getJsonInt(message, IOT_LABELS_JSON.STATE_RELAY.getValorTextoJson());
        if (i<0) {
            return IOT_CODE_RESULT.RESULT_CODE_NOK;
        }
        setRelay(IOT_SWITCH_RELAY.ON.fromId(i));

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }


    protected IOT_CODE_RESULT setSensorFromReport(String message) {

        IotTools api;
        Boolean localSensor;
        String remoteSensor;
        api = new IotTools();
        localSensor = api.getJsonboolean(message, IOT_LABELS_JSON.TYPE_SENSOR.getValorTextoJson());
        if (localSensor == true) {

            setMasterSensor(true);
            remoteSensor = api.getJsonString(message, IOT_LABELS_JSON.SENSOR_ID.getValorTextoJson());
            if(remoteSensor == null) {
                setMasterSensor(false);
                return IOT_CODE_RESULT.RESULT_CODE_ERROR;
            }
            setRemoteSensor(remoteSensor);

        } else {
            setMasterSensor(false);
            setRemoteSensor(null);
        }

        return IOT_CODE_RESULT.RESUT_CODE_OK;


    }


    protected IOT_CODE_RESULT setThresholdTemperatureFromReport(String message) {

        IotTools api;
        Double i;
        api = new IotTools();
        i = api.getJsonDouble(message, IOT_LABELS_JSON.THRESHOLD_TEMPERATURE.getValorTextoJson());
        if (i<= -1000) {
            return IOT_CODE_RESULT.RESULT_CODE_NOK;
        }
        setThresholdTemperature(i);

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    @Override
    protected void processCommand(String topic, MqttMessage message) {
        super.processCommand(topic, message);

    }
/*
    @Override
    protected void processSpontaneous(String topic, MqttMessage message) {
        super.processSpontaneous(topic, message);
        IOT_SPONTANEOUS_TYPE typeInform;
        String mensaje = new String(message.getPayload());
        typeInform = getSpontaneousType(mensaje);
        IOT_CODE_RESULT result;
        switch (typeInform) {

            case CAMBIO_TEMPERATURA:
                processSpontaneousChangeTemperature(mensaje);
                if (onReceivedSpontaneousChangeTemperature != null) {
                    onReceivedSpontaneousChangeTemperature.onReceivedSpontaneousChangeTemperature();
                }
                break;
        }

    }
*/
    @Override
    protected IOT_CODE_RESULT processSpontaneousChangeTemperature(String message) {
        setThresholdTemperatureFromReport(message);
        setSensorFromReport(message);
        return super.processSpontaneousChangeTemperature(message);
    }


    /*
    protected IOT_CODE_RESULT processSpontaneousChangeTemperature(String message) {

        processCommonParameters(message);
        setTemperatureFromReport(message);
        setHumidityFromReport(message);
        setThresholdTemperatureFromReport(message);
        setSensorFromReport(message);


        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

*/


}
