package net.jajica.libiot;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IotDeviceSwitch extends IotDevice {

    private IOT_SWITCH_RELAY Relay;

    public IOT_SWITCH_RELAY getRelay() {
        return Relay;
    }

    public void setRelay(IOT_SWITCH_RELAY relay) {
        this.Relay = relay;
    }

    public IotDeviceSwitch(MqttConnection cnx) {
        super(cnx);
    }
    
    private ArrayList<IotScheduleDeviceSwitch> schedules;

    public ArrayList<IotScheduleDeviceSwitch> getSchedulesSwitch() {
        return schedules;
    }

    public void setSchedulesSwitch(ArrayList<IotScheduleDeviceSwitch> schedules) {
        this.schedules = schedules;
    }

    @Override
    protected IOT_CODE_RESULT processStatus(String respuesta) {

        int res;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        IOT_SWITCH_RELAY estado = IOT_SWITCH_RELAY.UNKNOWN;
        res = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        setRelay(estado.fromId(res));
        return super.processStatus(respuesta);
    }

    
    private ArrayList<IotScheduleDeviceSwitch> loadSchedules(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONObject objetoPrograma = null;
        JSONArray arrayProgramas;
        int i;
        IotScheduleDeviceSwitch programa;


        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            for(i=0;i<arrayProgramas.length();i++) {
                if (schedules == null) schedules = new ArrayList<IotScheduleDeviceSwitch>();

                objeto = arrayProgramas.getJSONObject(i);
                programa = new IotScheduleDeviceSwitch(objeto);
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

    @Override
    public DEVICE_STATE_CONNECTION recibirMensajes() {
        return super.recibirMensajes();
    }

    @Override
    protected IOT_CODE_RESULT processGetSchedule(String message) {


        getDeviceStatus(message);
        getScheduleState(message);
        getCurrentSchedule(message);
        loadSchedules(message);
        //return super.processGetSchedule(message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }
    protected boolean addSchedule(IotScheduleDeviceSwitch programa) {

        int i;
        int tam;
        if (schedules == null) {
            schedules = new ArrayList<IotScheduleDeviceSwitch>();
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
    protected IOT_CODE_RESULT processModifySchedule(String message) {
        IOT_CODE_RESULT res;
        IotScheduleDeviceSwitch schedule;
        if ((res = getCommandCodeResult(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            Log.e(TAG, "error en la peticion de modificacion");
            return res;
        }

        // Localizamos el schedule a modificar

        int i;
        i = searchSchedule(getScheduleId(message));
        if (i < 0 ) {
            return IOT_CODE_RESULT.RESULT_CODE_NOK;
        }
        schedule = getSchedulesSwitch().get(i);

        // Ahora actualizamos los datos en la estructura
        schedule.setScheduleId(getNewScheduleId(message));




        return IOT_CODE_RESULT.RESULT_CODE_ERROR;

                /*
        {
	"idDevice":	"A020A6026046",
	"device":	0,
	"otaVersion":	"2206251749",
	"date":	"27/10/2022 18:07:41",
	"token":	"4714ea94-eff7-4483-aaa9-a7d1d7c6c415",
	"dlgComando":	8,
	"programId":	"001800007f",
	"newProgramId":	"002145337f",
	"deviceState":	4,
	"programState":	1,
	"estadoRele":	1,
	"durationProgram":	8700,
	"dlgResultCode":	200
}

         */
    }

    @Override
    protected int searchSchedule(String schedule) {
        int i;
        if (getSchedulesSwitch() == null) {
            Log.w(TAG, "No hay schedules");
            return -1;
        }
        for (i=0; i< getSchedulesSwitch().size();i++) {
            if (getSchedulesSwitch().get(i).getScheduleId().equals(schedule)) {
                return i;
            }
        }
        Log.w(TAG, "schedule " + schedule + "no encontrado");
        return -1;

    }
}
