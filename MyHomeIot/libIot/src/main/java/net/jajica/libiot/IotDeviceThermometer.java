package net.jajica.libiot;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class IotDeviceThermometer extends IotDevice implements Serializable {

    protected double temperature;
    protected double humidity;
    protected double marginTemperature;
    protected int readInterval;
    protected int retryInterval;
    protected int readRetry;
    protected double calibrateValue;


    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getMarginTemperature() {
        return marginTemperature;
    }

    public void setMarginTemperature(double marginTemperature) {
        this.marginTemperature = marginTemperature;
    }

    public int getReadInterval() {
        return readInterval;
    }

    public void setReadInterval(int readInterval) {
        this.readInterval = readInterval;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getReadRetry() {
        return readRetry;
    }

    public void setReadRetry(int readRetry) {
        this.readRetry = readRetry;
    }

    public double getCalibrateValue() {
        return calibrateValue;
    }

    public void setCalibrateValue(double calibrateValue) {
        this.calibrateValue = calibrateValue;
    }

    /**
     * Constructor en el que se le pasan los topics para las actualizaciones OTA en
     * funcion del tipo de dispositivo
     */
    public IotDeviceThermometer() {

    super();
    setPublishOtaTopic("OtaIotThermometer");
    setSubscribeOtaTopic("newVersionOtaIotThermometer");
    setDeviceType(IOT_DEVICE_TYPE.THERMOMETER);
}

    public IotDeviceThermometer(IotMqttConnection cnx) {
        super(cnx);
        setPublishOtaTopic("OtaIotThermometer");
        setSubscribeOtaTopic("newVersionOtaIotThermometer");
        setDeviceType(IOT_DEVICE_TYPE.THERMOMETER);

    }

    OnReceivedSpontaneousChangeTemperature onReceivedSpontaneousChangeTemperature;
    public interface OnReceivedSpontaneousChangeTemperature {
        void onReceivedSpontaneousChangeTemperature();
    }

    public void setOnReceivedSpontaneousChangeTemperature(OnReceivedSpontaneousChangeTemperature onReceivedSpontaneousChangeTemperature) {
        this.onReceivedSpontaneousChangeTemperature = onReceivedSpontaneousChangeTemperature;
    }



    @Override
    protected void processCommand(String topic, MqttMessage message) {
        super.processCommand(topic, message);
        IOT_COMMANDS idComando;
        IotTools api;
        api = new IotTools();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        IOT_CODE_RESULT res;

        switch (idComando) {
            case STATUS_DEVICE:
                res = processStatusFromReport(mensaje);
                if (onReceivedStatus != null) {
                    onReceivedStatus.onReceivedStatus(res);
                }
                break;

            case MODIFY_PARAMETER_DEVICE:
                res = processModifyParametersDevice(mensaje);
                if (onReceivedModifyParametersDevice != null) {
                    onReceivedModifyParametersDevice.onReceivedMofifyParametersDevice(res);
                }
        }

    }

    @Override
    protected void processSpontaneous(String topic, MqttMessage message) {
        super.processSpontaneous(topic, message);

        IOT_SPONTANEOUS_TYPE typeInform;
        String mensaje = new String(message.getPayload());
        typeInform = getSpontaneousType(mensaje);
        IOT_CODE_RESULT result;
        switch (typeInform) {
            case UPGRADE_FIRMWARE_FOTA:
                break;
            case COMANDO_APLICACION:
                break;
            case CAMBIO_TEMPERATURA:
                processSpontaneousChangeTemperature(mensaje);
                if (onReceivedSpontaneousChangeTemperature != null) {
                    onReceivedSpontaneousChangeTemperature.onReceivedSpontaneousChangeTemperature();
                }
                break;

            case INFORME_ALARMA:
                break;

            case CAMBIO_ESTADO_APLICACION:
                break;
            case ERROR:
                break;
            case ESPONTANEO_DESCONOCIDO:
                break;
        }

    }

    /**
     * Metodo para actualizar el objeto desde el informe de cambio de temperatura
     * @return
     */
    protected IOT_CODE_RESULT processSpontaneousChangeTemperature(String message) {

        processCommonParameters(message);
        setTemperatureFromReport(message);
        setHumidityFromReport(message);
        processCommonParameters(message);
    return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    /**
     * Este metodo se sobreescribe para incorporar las alarmas especificas al termometro
     * @param message
     * @return
     */
    @Override
    protected IOT_TYPE_ALARM_DEVICE identifyTypeAlarm(String message) {
        IOT_ALARM_VALUE alarm = IOT_ALARM_VALUE.ALARM_UNKNOWN;

        super.identifyTypeAlarm(message);
        int i;
        i = getFieldIntFromReport(message, IOT_LABELS_JSON.SENSOR_ALARM);
        if (i >= 0) {
            alarms.setWifiAlarm(alarm.fromId(i));
            Log.i(TAG, "detectada alarma " + IOT_LABELS_JSON.SENSOR_ALARM.getValorTextoJson() + "=" + String.valueOf(i));
            return IOT_TYPE_ALARM_DEVICE.SENSOR_ALARM;
        }

        return IOT_TYPE_ALARM_DEVICE.UNKNOWN_ALARM;

    }

    @Override
    protected IOT_CODE_RESULT processStatusFromReport(String respuesta) {

        setTemperatureFromReport(respuesta);
        setHumidityFromReport(respuesta);
        setMarginTemperatureFromReport(respuesta);
        setReadIntervalFromReport(respuesta);
        setRetryIntervalFromReport(respuesta);
        setReadRetry(respuesta);
        setCalibrateValueFromReport(respuesta);
        return super.processStatusFromReport(respuesta);
    }

    protected IOT_CODE_RESULT setTemperatureFromReport(String message) {

        double dat;
        dat = getFieldDoubleFromReport(message, IOT_LABELS_JSON.TEMPERATURE);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setTemperature(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    protected IOT_CODE_RESULT setHumidityFromReport(String message) {

        double dat;
        dat = getFieldDoubleFromReport(message, IOT_LABELS_JSON.HUMIDITY);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de Humedad");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setHumidity(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    protected IOT_CODE_RESULT setMarginTemperatureFromReport(String message) {
        double dat;
        dat = getFieldDoubleFromReport(message, IOT_LABELS_JSON.MARGIN_TEMPERATURE);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setMarginTemperature(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    protected IOT_CODE_RESULT setReadIntervalFromReport(String message) {
        int dat;
        dat = getFieldIntFromReport(message, IOT_LABELS_JSON.READ_INTERVAL);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setReadInterval(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setRetryIntervalFromReport(String message) {
        int dat;
        dat = getFieldIntFromReport(message, IOT_LABELS_JSON.RETRY_INTERVAL);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setRetryInterval(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setReadRetry(String message) {
        int dat;
        dat = getFieldIntFromReport(message, IOT_LABELS_JSON.READ_NUMBER_RETRY);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setReadRetry(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setCalibrateValueFromReport(String message) {

        double dat;
        dat = getFieldDoubleFromReport(message, IOT_LABELS_JSON.CALIBRATE_VALUE);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setCalibrateValue(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    @Override
    protected IOT_CODE_RESULT processModifyParametersDevice(String message) {

        IOT_CODE_RESULT code;

        if ((code =getCommandCodeResultFromReport(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {

            setMarginTemperatureFromReport(message);
            setReadIntervalFromReport(message);
            setRetryIntervalFromReport(message);
            setReadRetry(message);
            setCalibrateValueFromReport(message);
        }
        return code;
    }

    public IOT_DEVICE_STATUS_CONNECTION commandSetParametersDevice(JSONObject parameters) {

        JSONObject parameter;
        IOT_DEVICE_STATUS_CONNECTION state;
        parameter = new JSONObject();
        if (parameters != null) {
            state = commandwithParameters(IOT_COMMANDS.MODIFY_PARAMETER_DEVICE, IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson(), parameters);
            if (state != IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
                return state;
            } else {
                return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
            }

        } else {
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }

    }

    @Override
    protected IOT_CODE_RESULT processStartDevice(String message) {

        setTemperatureFromReport(message);
        setHumidityFromReport(message);

        return super.processStartDevice(message);
    }

    /*
    @Override
    protected IOT_CODE_RESULT processInfoDeviceFromReport(String message) {

        setNumberProgramsFromReport(message);
        setProgrammerStateFromReport(message);

        try {
            dispositivoJson.put(IOT_LABELS_JSON.STATUS_PROGRAMMER.getValorTextoJson(), getProgrammerState());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            dispositivoJson.put(IOT_LABELS_JSON.NUMBER_PROGRAMS.getValorTextoJson(), getNumberSchedules());
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return super.processInfoDeviceFromReport(message);
    }


     */




}
