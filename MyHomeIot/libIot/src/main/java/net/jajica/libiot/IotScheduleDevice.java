package net.jajica.libiot;

import android.util.Log;
import android.view.View;

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
    protected STATE_SCHEDULE scheduleState;
    protected int mask;
    protected IOT_DEVICE_TYPE deviceType;
    protected boolean [] activeDays;
    protected boolean activeSchedule;
    protected int duration;

    IotScheduleDevice() {

        scheduleId = null;
        scheduleType = IOT_CLASS_SCHEDULE.UNKNOWN_SCHEDULE;
        hour = 0;
        minute = 0;
        second = 0;
        weekDay = 0;
        setScheduleState(STATE_SCHEDULE.PROGRAMA_INACTIVO);
        mask = 0;
        year = 0;
        month = 0;
        day = 0;
        deviceType = IOT_DEVICE_TYPE.DESCONOCIDO;
        activeDays = new boolean[7];
        activeSchedule = false;
    }

    IotScheduleDevice(String ScheduleId) {

        setScheduleId(ScheduleId);
        setTypeScheduleFromScheduleId();
        setStateScheduleFromScheduleId();
        setActiveSchedule(false);
        activeDays = new boolean[7];

    }

    IotScheduleDevice(JSONObject schedule) {

        try {
            setRawSchedule(schedule.getString(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson()));
            setDuration(schedule.getInt(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson()));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        setTypeScheduleFromScheduleId();
        setParametersDiarySchedule();
        activeDays = new boolean[7];
        setMaskDiarySchedule();
        setDeviceType(IOT_DEVICE_TYPE.DESCONOCIDO);
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
        STATE_SCHEDULE estado = STATE_SCHEDULE.PROGRAMA_DESCONOCIDO;
        i= Integer.parseInt(getRawSchedule().substring(10,11));
        setScheduleState(estado.fromId(i));
    }

    protected STATE_SCHEDULE setParametersDiarySchedule() {
        if(getRawSchedule() == null) {
            Log.e(TAG, "Error: No hay scheduleId");
            return STATE_SCHEDULE.PROGRAMA_INVALIDO;
        }
        if (getScheduleType() != IOT_CLASS_SCHEDULE.DIARY_SCHEDULE) {
            Log.e(TAG, "Error: El tipo de programa no es diario");
            return getScheduleState();
        }

        setHour(Integer.valueOf(getRawSchedule().substring(2,4)));
        setMinute(Integer.valueOf(getRawSchedule().substring(4,6)));
        setSecond(Integer.valueOf(getRawSchedule().substring(6,8)));



        return STATE_SCHEDULE.PROGRAMA_VALIDO;

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

    public STATE_SCHEDULE getScheduleState() {
        return scheduleState;
    }

    public void setScheduleState(STATE_SCHEDULE scheduleState) {
        this.scheduleState = scheduleState;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public IOT_DEVICE_TYPE getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(IOT_DEVICE_TYPE deviceType) {
        this.deviceType = deviceType;
    }

    public boolean[] getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(boolean[] activeDays) {
        this.activeDays = activeDays;
    }

    public boolean isActiveSchedule() {
        return activeSchedule;
    }

    public void setActiveSchedule(boolean activeSchedule) {
        this.activeSchedule = activeSchedule;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public JSONObject schedule2Json(IotScheduleDevice schedule) {

        JSONObject objectSchedule;
        objectSchedule = new JSONObject();
        try {

            objectSchedule.put(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), schedule.getScheduleId());
            objectSchedule.put(TEXTOS_DIALOGO_IOT.TIPO_PROGRAMA.getValorTextoJson(), schedule.getScheduleType().getTipoPrograma());
            objectSchedule.put(TEXTOS_DIALOGO_IOT.HORA.getValorTextoJson(), schedule.getHour());
            objectSchedule.put(TEXTOS_DIALOGO_IOT.MINUTO.getValorTextoJson(), schedule.getMinute());
            objectSchedule.put(TEXTOS_DIALOGO_IOT.SEGUNDO.getValorTextoJson(), schedule.getSecond());
            objectSchedule.put(TEXTOS_DIALOGO_IOT.STATE_SCHEDULE.getValorTextoJson(), schedule.getScheduleState());
            objectSchedule.put(TEXTOS_DIALOGO_IOT.MASCARA_PROGRAMA.getValorTextoJson(), schedule.getMask());

            objectSchedule.put(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson(), schedule.getDuration());

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

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int i;
        i = api.getJsonInt(message, TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson());

        if (i < 0) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setDuration(i);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    protected IOT_CODE_RESULT setScheduleStateFromReport(String message) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int i;
        i = api.getJsonInt(message, TEXTOS_DIALOGO_IOT.STATE_SCHEDULE.getValorTextoJson());
        if (i < 0)
        return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        setScheduleState(STATE_SCHEDULE.PROGRAMA_DESCONOCIDO.fromId(i));

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setNewScheduleIdFromReport(String respuesta) {

        ApiDispositivoIot api;
        String scheduleId;
        api = new ApiDispositivoIot();
        scheduleId = api.getJsonString(respuesta, TEXTOS_DIALOGO_IOT.NUEVO_ID_PROGRAMA.getValorTextoJson());
        if (scheduleId == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setScheduleId(scheduleId);
        return IOT_CODE_RESULT.RESUT_CODE_OK;



    }


}
