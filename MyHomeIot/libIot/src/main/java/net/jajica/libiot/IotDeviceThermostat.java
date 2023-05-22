package net.jajica.libiot;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class IotDeviceThermostat extends IotDeviceThermometer implements Serializable {




    protected Boolean MasterSensor;
    protected String RemoteSensor;
    protected Double thresholdTemperature;
    protected IOT_SWITCH_RELAY relay;

    protected double marginTemperature;
    protected double defaultThresholdTemperature;

    private ArrayList<IotScheduleDeviceThermostat> schedules;

    public double getMarginTemperature() {
        return marginTemperature;
    }

    public void setMarginTemperature(double marginTemperature) {
        this.marginTemperature = marginTemperature;
    }

    public Double getDefaultThresholdTemperature() {
        return defaultThresholdTemperature;
    }

    public void setDefaultThresholdTemperature(Double defaultThresholdTemperature) {
        this.defaultThresholdTemperature = defaultThresholdTemperature;
    }

    OnReceivedSetThresholdTemperature onReceivedSetThresholdTemperature;
    public interface OnReceivedSetThresholdTemperature {
        void onReceivedSetThresholdTemperature(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedSetThresholdTemperature(OnReceivedSetThresholdTemperature onReceivedSetThresholdTemperature) {
        this.onReceivedSetThresholdTemperature = onReceivedSetThresholdTemperature;
    }

    public ArrayList<IotScheduleDeviceThermostat> getSchedulesThermostat() {
        return schedules;
    }

    public void setSchedulesThermostat(ArrayList<IotScheduleDeviceThermostat> schedules) {
        this.schedules = schedules;
    }

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
        setThresholdTemperature(-1000.0);
        setRelay(IOT_SWITCH_RELAY.UNKNOWN);

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
        setStateRelayFromReport(message);
        processCommonParameters(message);
        setActiveScheduleFromReport(message);
        return super.processStartSchedule(message);
    }

    @Override
    protected IOT_CODE_RESULT processEndSchedule(String message) {
        processCommonParameters(message);
        setStateRelayFromReport(message);
        setActiveSchedule(message);
        return super.processEndSchedule(message);
    }

    @Override
    protected IOT_TYPE_ALARM_DEVICE identifyTypeAlarm(String message) {

        IOT_ALARM_VALUE alarm = IOT_ALARM_VALUE.ALARM_UNKNOWN;
        int i;
        i = getFieldIntFromReport(message, IOT_LABELS_JSON.REMOTE_SENSOR_ALARM);
        if (i >= 0) {
            alarms.setWifiAlarm(alarm.fromId(i));
            Log.i(TAG, "detectada alarma " + IOT_LABELS_JSON.REMOTE_SENSOR_ALARM.getValorTextoJson() + "=" + String.valueOf(i));
            return IOT_TYPE_ALARM_DEVICE.REMOTE_SENSOR_ALARM;
        }

        return super.identifyTypeAlarm(message);
    }

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

        try {
            dispositivoJson.put(IOT_LABELS_JSON.MARGIN_TEMPERATURE.getValorTextoJson(), getMarginTemperature());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            dispositivoJson.put(IOT_LABELS_JSON.DEFAULT_THRESHOLD_TEMPERATURE.getValorTextoJson(), getDefaultThresholdTemperature());
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            dispositivoJson.put(IOT_LABELS_JSON.TYPE_SENSOR.getValorTextoJson(), getMasterSensor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!getMasterSensor()) {
             try {
                 dispositivoJson.put(IOT_LABELS_JSON.SENSOR_ID.getValorTextoJson(), getRemoteSensor());
             } catch (JSONException e) {
                 e.printStackTrace();
             }

        } else {
            dispositivoJson.remove(IOT_LABELS_JSON.SENSOR_ID.getValorTextoJson());

        }


        return super.processInfoDeviceFromReport(message);
    }

    protected IOT_CODE_RESULT setNumberProgramsFromReport(String message) {
        int dat;
        dat = getFieldIntFromReport(message, IOT_LABELS_JSON.NUMBER_PROGRAMS);
        if (dat <= 0) {
            Log.e(TAG, "No se encuentra el valor de programas");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setNumberSchedules(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }
    @Override
    protected IOT_CODE_RESULT processStatusFromReport(String respuesta) {
        processCommonParameters(respuesta);
        setSensorFromReport(respuesta);
        return super.processStatusFromReport(respuesta);
    }

    @Override
    protected IOT_CODE_RESULT processGetScheduleFromReport(String message) {
        processCommonParameters(message);
        loadSchedules(message);
        if (schedules == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        } else {
            return IOT_CODE_RESULT.RESUT_CODE_OK;
        }

    }


    @Override
    protected IOT_CODE_RESULT processDeleteScheduleFromReport(String message) {
        return super.processDeleteScheduleFromReport(message);
    }

    @Override
    protected IOT_CODE_RESULT processModifySchedule(String message) {
        IOT_CODE_RESULT res;
        IotScheduleDeviceSwitch schedule;
        if ((res = getCommandCodeResultFromReport(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            Log.e(TAG, "error en la peticion de modificacion");
            return res;
        }

        // Localizamos el schedule a modificar

        int i;
        i = searchSchedule(getScheduleIdFromReport(message));
        if (i < 0 ) {
            return IOT_CODE_RESULT.RESULT_CODE_NOK;
        }
        schedule = getSchedulesThermostat().get(i);

        // Ahora actualizamos los datos en la estructura

        schedule.setNewScheduleIdFromReport(message);
        setStateRelayFromReport(message);
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        schedule.setDurationFromReport(message);
        schedule.setScheduleStateFromReport(message);
        setCurrentScheduleFromReport(message);
        setThresholdTemperatureFromReport(message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
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
        setMasterSensor(localSensor);
        if (getMasterSensor()) {

            setRemoteSensor("");
        } else {
            setRemoteSensor(api.getJsonString(message, IOT_LABELS_JSON.SENSOR_ID.getValorTextoJson()));
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
        IOT_COMMANDS idComando;
        IotTools api;
        api = new IotTools();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        IOT_CODE_RESULT res;

        switch (idComando) {
            case MODIFY_THRESHOLD_TEMPERATURE:
                res = processSetThresholdTemperatureFromReport(mensaje);
                if (onReceivedSetThresholdTemperature != null) {
                    onReceivedSetThresholdTemperature.onReceivedSetThresholdTemperature(res);
                }
                break;

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

                case CAMBIO_UMBRAL_TEMPERATURA:
                    result = processSpontaneousChangeThresholdTemperature(mensaje);
                    if (onReceivedSetThresholdTemperature != null) {
                        onReceivedSetThresholdTemperature.onReceivedSetThresholdTemperature(result);
                    }
                    break;

            }


        }

    private IOT_CODE_RESULT processSpontaneousChangeThresholdTemperature(String mensaje) {

        return processCommonParameters(mensaje);

    }


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

    @Override
    protected IOT_CODE_RESULT processCommonParameters(String message) {

        setTemperatureFromReport(message);
        setHumidityFromReport(message);
        setThresholdTemperatureFromReport(message);
        setStateRelayFromReport(message);
        setMarginTemperatureFromReport(message);
        setDefaultThresholdTemperatureFromReport(message);
        return super.processCommonParameters(message);
    }

    private ArrayList<IotScheduleDeviceThermostat> loadSchedules(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONArray arrayProgramas;
        int i;
        IotScheduleDeviceThermostat programa;

        if (schedules!= null) {
            if (schedules.size() > 0) schedules.clear();
            Log.i(TAG, "device es :  " + hashCode() + " schedules borrados");
        }

        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(IOT_LABELS_JSON.SCHEDULE.getValorTextoJson());
            for(i=0;i<arrayProgramas.length();i++) {
                if (schedules == null) schedules = new ArrayList<IotScheduleDeviceThermostat>();

                objeto = arrayProgramas.getJSONObject(i);
                programa = new IotScheduleDeviceThermostat(objeto);
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
    protected boolean addSchedule(IotScheduleDeviceThermostat programa) {

        int i;
        int tam;
        if (schedules == null) {
            schedules = new ArrayList<IotScheduleDeviceThermostat>();
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

    @Override
    protected int searchSchedule(String schedule) {
        int i;
        if (getSchedulesThermostat() == null) {
            Log.w(TAG, "No hay schedules");
            return -1;
        }
        for (i=0; i< getSchedulesThermostat().size();i++) {
            if (getSchedulesThermostat().get(i).getScheduleId().equals(schedule)) {
                return i;
            }
        }
        Log.w(TAG, "schedule " + schedule + "no encontrado");
        return -1;
    }

    public IOT_DEVICE_STATUS_CONNECTION commandSetThresholdTemperarture(Double thresholdTemperature) {

        JSONObject parameter;
        parameter = new JSONObject();

        try {
            parameter.put(IOT_LABELS_JSON.THRESHOLD_TEMPERATURE.getValorTextoJson(), thresholdTemperature);
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }

        return commandwithParameters(IOT_COMMANDS.MODIFY_THRESHOLD_TEMPERATURE, IOT_LABELS_JSON.MODIFY_APP.getValorTextoJson(), parameter);

    }

    protected IOT_CODE_RESULT processSetThresholdTemperatureFromReport(String message) {

        IOT_CODE_RESULT result;
        if ((result = getCommandCodeResultFromReport(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            return result;
        }
        setThresholdTemperatureFromReport(message);
        setStateRelayFromReport(message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    private IOT_CODE_RESULT setMarginTemperatureFromReport(String message) {

        double dat;
        dat = getFieldDoubleFromReport(message, IOT_LABELS_JSON.MARGIN_TEMPERATURE);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setMarginTemperature(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    private IOT_CODE_RESULT setDefaultThresholdTemperatureFromReport(String message) {

        double dat;
        dat = getFieldDoubleFromReport(message, IOT_LABELS_JSON.DEFAULT_THRESHOLD_TEMPERATURE);
        if (dat <= -1000) {
            Log.e(TAG, "No se encuentra el valor de temperatura");
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setDefaultThresholdTemperature(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }


    @Override
    public void setConnectionState(IOT_DEVICE_STATUS_CONNECTION connectionState) {

        switch (connectionState) {

            case UNKNOWN:
            case DEVICE_DISCONNECTED:
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_COMMUNICATION:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_NO_SUBSCRIPT:
            case DEVICE_NO_SUBSCRIPT:
                setRelay(IOT_SWITCH_RELAY.UNKNOWN);
                break;
        }
        super.setConnectionState(connectionState);
    }

    public Boolean checkValidScheduleThermostatDevice(IotScheduleDeviceThermostat schedule, String oldScheduleId) {


        int i;

        if (getSchedulesThermostat() != null) {
            for(i=0;i<getSchedulesThermostat().size();i++) {
                if (getSchedulesThermostat().get(i).getScheduleId().equals(oldScheduleId)) {
                    continue;
                }

                if (!checkValidProg(
                        getSchedulesThermostat().get(i).getHour(),
                        getSchedulesThermostat().get(i).getMinute(),
                        getSchedulesThermostat().get(i).getSecond(),
                        getSchedulesThermostat().get(i).getDuration(),
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
    }

    protected void setActiveScheduleFromReport(String message) {

        int nSchedules;
        int i;
        IotScheduleDevice schedule;

        if (getSchedulesThermostat() != null) {
            nSchedules = getSchedulesThermostat().size();
            for (i=0;i<nSchedules;i++) {
                schedule = getSchedulesThermostat().get(i);
                if (schedule.getScheduleId().equals(getActiveSchedule())) {
                    schedule.setActiveSchedule(true);
                } else {
                    schedule.setActiveSchedule(false);
                }
            }
        }


    }


}
