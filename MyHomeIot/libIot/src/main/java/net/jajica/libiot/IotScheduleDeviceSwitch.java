package net.jajica.libiot;

import android.util.Log;

import org.json.JSONException;
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

    public IotScheduleDeviceSwitch() {
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


    @Override
    public void createScheduleIdFromObject() {
        super.createScheduleIdFromObject();
        setRawScheduleFromObject();
    }

    public void setRawScheduleFromObject() {

        String state;
        String relay;
        state = String.valueOf(getScheduleState().getEstadoPrograma());
        relay = String.valueOf(getRelay().getEstadoRele());

        rawSchedule = scheduleId + state + relay;

    }

    @Override
    protected JSONObject schedule2Json(IotScheduleDevice schedule) {

        JSONObject objectSchedule;

        objectSchedule = super.schedule2Json(schedule);
        try {
            objectSchedule.put(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson(), getRelay().getEstadoRele());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return objectSchedule;
    }
}
