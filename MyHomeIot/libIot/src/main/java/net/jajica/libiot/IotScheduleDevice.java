package net.jajica.libiot;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Formatter;

public class IotScheduleDevice implements Serializable {

    private final String TAG = "IotScheduleDevice";
    protected String rawSchedule;
    protected String scheduleId;
    protected IOT_CLASS_SCHEDULE scheduleType;
    protected int year;
    protected int month;
    protected int day;
    protected int hour;
    protected int minute;
    protected int second;
    protected int weekDay;
    protected IOT_STATE_SCHEDULE scheduleState;
    protected int mask;
    protected IOT_DEVICE_TYPE deviceType;
    protected Boolean[] activeDays;
    protected Boolean activeSchedule;
    protected int duration;

    IotScheduleDevice() {

        scheduleId = null;
        scheduleType = IOT_CLASS_SCHEDULE.UNKNOWN_SCHEDULE;
        hour = 0;
        minute = 0;
        second = 0;
        weekDay = 0;
        setScheduleState(IOT_STATE_SCHEDULE.INACTIVE_SCHEDULE);
        mask = 0;
        year = 0;
        month = 0;
        day = 0;
        deviceType = IOT_DEVICE_TYPE.UNKNOWN;
        activeDays = new Boolean[7];
        activeSchedule = false;
    }

    IotScheduleDevice(String ScheduleId) {

        setScheduleId(ScheduleId);
        setTypeScheduleFromScheduleId();
        setStateScheduleFromScheduleId();
        setActiveSchedule(false);
        activeDays = new Boolean[7];

    }

    IotScheduleDevice(JSONObject schedule) {

        try {
            setRawSchedule(schedule.getString(IOT_LABELS_JSON.SCHEDULE_ID.getValorTextoJson()));
            setDuration(schedule.getInt(IOT_LABELS_JSON.DURATION.getValorTextoJson()));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        setTypeScheduleFromScheduleId();
        setParametersDiarySchedule();
        activeDays = new Boolean[7];
        setMaskDiarySchedule();
        setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        setStateScheduleFromScheduleId();
        createScheduleIdFromObject();
        setActiveSchedule(false);


    }

    public String getRawSchedule() {
        return rawSchedule;
    }

    public void setRawSchedule(String rawSchedule) {
        this.rawSchedule = rawSchedule;
    }

    /**
     * Este metodo anota el tipo de temporizador. Es necesario que el ScheduleId sea valido.
     */
    protected void setTypeScheduleFromScheduleId() {
        if(getRawSchedule() == null) {
            Log.e(TAG, "Error: No hay scheduleId");
            return;
        }
        IOT_CLASS_SCHEDULE classSchedule = IOT_CLASS_SCHEDULE.UNKNOWN_SCHEDULE;
        int tipo;
        tipo = Integer.parseInt(getRawSchedule().substring(0,2));
        this.scheduleType = classSchedule.fromId(tipo);

    }


    protected void setStateScheduleFromScheduleId() {
        if(getRawSchedule() == null) {
            Log.e(TAG, "Error: No hay scheduleId");
            return;
        }
        int i;
        IOT_STATE_SCHEDULE estado = IOT_STATE_SCHEDULE.UNKNOWN_SCHEDULE;
        i= Integer.parseInt(getRawSchedule().substring(10,11));
        setScheduleState(estado.fromId(i));
    }

    protected IOT_STATE_SCHEDULE setParametersDiarySchedule() {
        if(getRawSchedule() == null) {
            Log.e(TAG, "Error: No hay scheduleId");
            return IOT_STATE_SCHEDULE.INVALID_SCHEDULE;
        }
        if (getScheduleType() != IOT_CLASS_SCHEDULE.DIARY_SCHEDULE) {
            Log.e(TAG, "Error: El tipo de programa no es diario");
            return getScheduleState();
        }

        setHour(Integer.valueOf(getRawSchedule().substring(2,4)));
        setMinute(Integer.valueOf(getRawSchedule().substring(4,6)));
        setSecond(Integer.valueOf(getRawSchedule().substring(6,8)));



        return IOT_STATE_SCHEDULE.VALID_SCHEDULE;

    }

    /**
     * Esta funcion lee la mascara de programa y coloca true en el dia de la semana en el que
     * hay programa activo.
     */
    protected void setMaskDiarySchedule() {

        int i;
        double comparador;
        int valor;
        String mask;

        mask = "0x" + getRawSchedule().substring(8,10);
        setMask((Integer.decode(mask)).intValue());
        for(i=0;i<7;i++) {
            comparador = Math.pow(2,i);
            valor = this.mask & (int) comparador;
            if (valor == comparador) {
                this.activeDays[i] = true;
            } else {
                this.activeDays[i] = false;
            }
        }
    }

    protected void setActiveDaysFromMask() {

        int i;
        double comparador;
        int valor;

        for(i=0;i<7;i++) {
            comparador = Math.pow(2,i);
            valor = this.mask & (int) comparador;
            if (valor == comparador) {
                this.activeDays[i] = true;
            } else {
                this.activeDays[i] = false;
            }
        }

    }

    public void createScheduleIdFromObject() {

        String schedule;
        IOT_CLASS_SCHEDULE classSchedule = IOT_CLASS_SCHEDULE.UNKNOWN_SCHEDULE;
        String dato;
        Formatter formato;
        formato = new Formatter();


        switch (getScheduleType()) {

            case DIARY_SCHEDULE:
                dato = formato.format("%02d", 0).toString();
                dato = formato.format("%02d", getHour()).toString();
                dato = formato.format("%02d", getMinute()).toString();
                dato = formato.format("%02d", getSecond()).toString();
                dato = formato.format("%02x", getMask()).toString();
                setScheduleId(dato);

            default:
                break;
        }

    }



    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public IOT_CLASS_SCHEDULE getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(IOT_CLASS_SCHEDULE scheduleType) {
        this.scheduleType = scheduleType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public IOT_STATE_SCHEDULE getScheduleState() {
        return scheduleState;
    }

    public void setScheduleState(IOT_STATE_SCHEDULE scheduleState) {
        this.scheduleState = scheduleState;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
        setActiveDaysFromMask();
    }

    public IOT_DEVICE_TYPE getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(IOT_DEVICE_TYPE deviceType) {
        this.deviceType = deviceType;
    }

    public Boolean[] getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(Boolean[] activeDays) {
        this.activeDays = activeDays;
    }

    public Boolean isActiveSchedule() {
        return activeSchedule;
    }

    public void setActiveSchedule(Boolean activeSchedule) {
        this.activeSchedule = activeSchedule;
    }

    public Boolean getActiveSchedule() {
        return this.activeSchedule;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected JSONObject schedule2Json(IotScheduleDevice schedule) {

        JSONObject objectSchedule;
        objectSchedule = new JSONObject();
        try {

            objectSchedule.put(IOT_LABELS_JSON.SCHEDULE_ID.getValorTextoJson(), schedule.getScheduleId());
            objectSchedule.put(IOT_LABELS_JSON.TYPE_SCHEDULE.getValorTextoJson(), schedule.getScheduleType().getTipoPrograma());
            objectSchedule.put(IOT_LABELS_JSON.HOUR.getValorTextoJson(), schedule.getHour());
            objectSchedule.put(IOT_LABELS_JSON.MINUTE.getValorTextoJson(), schedule.getMinute());
            objectSchedule.put(IOT_LABELS_JSON.SECOND.getValorTextoJson(), schedule.getSecond());
            objectSchedule.put(IOT_LABELS_JSON.MASK_SCHEDULE.getValorTextoJson(), schedule.getMask());
            objectSchedule.put(IOT_LABELS_JSON.STATUS_SCHEDULE.getValorTextoJson(), schedule.getScheduleState().getEstadoPrograma());
            objectSchedule.put(IOT_LABELS_JSON.DURATION.getValorTextoJson(), schedule.getDuration());

        } catch (JSONException e) {
            return null;

        }
        return objectSchedule;

    }

    protected void setRawScheduleFromObject() {

        String estado;
        estado = String.valueOf(getScheduleState().getEstadoPrograma());

        rawSchedule = getScheduleId() + estado;
        setRawSchedule(rawSchedule);


    }

    protected IOT_CODE_RESULT setDurationFromReport(String message) {

        IotTools api;
        api = new IotTools();
        int i;
        i = api.getJsonInt(message, IOT_LABELS_JSON.DURATION.getValorTextoJson());

        if (i < 0) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setDuration(i);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    @SuppressLint("SuspiciousIndentation")
    protected IOT_CODE_RESULT setScheduleStateFromReport(String message) {

        IotTools api;
        api = new IotTools();
        int i;
        i = api.getJsonInt(message, IOT_LABELS_JSON.STATUS_SCHEDULE.getValorTextoJson());
        if (i < 0)
        return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        setScheduleState(IOT_STATE_SCHEDULE.UNKNOWN_SCHEDULE.fromId(i));

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setNewScheduleIdFromReport(String respuesta) {

        IotTools api;
        String scheduleId;
        api = new IotTools();
        scheduleId = api.getJsonString(respuesta, IOT_LABELS_JSON.NEW_SCHEDULE_ID.getValorTextoJson());
        if (scheduleId == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setScheduleId(scheduleId);
        return IOT_CODE_RESULT.RESUT_CODE_OK;



    }

    public Boolean getActiveDay(int day) {

        return activeDays[day];
    }

}
