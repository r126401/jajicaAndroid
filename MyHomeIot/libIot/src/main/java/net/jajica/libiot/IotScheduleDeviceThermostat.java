package net.jajica.libiot;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class IotScheduleDeviceThermostat extends IotScheduleDeviceSwitch implements Serializable, Cloneable {

    protected double thresholdTemperature;


    public double getThresholdTemperature() {
        return thresholdTemperature;
    }

    public void setThresholdTemperature(double thresholdTemperature) {
        this.thresholdTemperature = thresholdTemperature;
    }

    public IotScheduleDeviceThermostat() {
        super();
        setDeviceType(IOT_DEVICE_TYPE.CRONOTERMOSTATO);

    }

    public IotScheduleDeviceThermostat(JSONObject schedule) {

        super(schedule);
        setDeviceType(IOT_DEVICE_TYPE.CRONOTERMOSTATO);
        setDurationFromReport(schedule.toString());
        setThresholdTemperatureFromReport(schedule.toString());

    }

    protected IOT_CODE_RESULT setThresholdTemperatureFromReport(String message) {

        double dat;
        IotTools api;
        api = new IotTools();
        dat = api.getJsonDouble(message, IOT_LABELS_JSON.THRESHOLD_TEMPERATURE.getValorTextoJson());
        if (dat <= -1000) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        setThresholdTemperature(dat);
        return IOT_CODE_RESULT.RESUT_CODE_OK;


    }

    @Override
    protected JSONObject schedule2Json(IotScheduleDevice schedule) {

        JSONObject objectSchedule;

        objectSchedule = super.schedule2Json(schedule);
        try {
            objectSchedule.put(IOT_LABELS_JSON.THRESHOLD_TEMPERATURE.getValorTextoJson(), getThresholdTemperature());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return objectSchedule;
    }




}
