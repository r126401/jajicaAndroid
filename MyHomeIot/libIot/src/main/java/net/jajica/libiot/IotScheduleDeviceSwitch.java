package net.jajica.libiot;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Formatter;

public class IotScheduleDeviceSwitch extends IotScheduleDevice {

    private final String TAG = "IotScheduleDeviceSwitch";
    private IOT_SWITCH_RELAY relay;


    public IOT_SWITCH_RELAY getRelay() {
        return relay;
    }

    public void setRelay(IOT_SWITCH_RELAY relay) {
        this.relay = relay;
    }

    IotScheduleDeviceSwitch(JSONObject schedule) {
        super(schedule);
        setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
        setScheduleState(STATE_SCHEDULE.PROGRAMA_INACTIVO);
        setStateSwitchFromScheduleId();
    }

    IotScheduleDeviceSwitch() {
        super();
        setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
    }

    IotScheduleDeviceSwitch(String ScheduleId) {
        super(ScheduleId);
        setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
    }

    protected void setStateSwitchFromScheduleId() {

        if(getScheduleId() == null) {
            Log.e(TAG, "Error: No hay scheduleId");
            return;
        }
        int i;
        IOT_SWITCH_RELAY estado = IOT_SWITCH_RELAY.UNKNOWN;
        i= Integer.parseInt(getRawSchedule().substring(10,11));
        setRelay(estado.fromId(i));
    }

    protected void setRawScheduleIdFromModifyScheduleReport(String scheduleId) {

        this.rawSchedule = scheduleId + "1";


    }



}
