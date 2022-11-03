package net.jajica.libiot;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
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

    public IotDeviceSwitch() {
        super();
    }
    
    private ArrayList<IotScheduleDeviceSwitch> schedules;

    public ArrayList<IotScheduleDeviceSwitch> getSchedulesSwitch() {
        return schedules;
    }

    protected OnReceivedSetRelay onReceivedSetRelay;
    public interface OnReceivedSetRelay {
        void onReceivedSetRelay(IOT_CODE_RESULT codeResult);
    }

    public void setOnReceivedSetRelay(OnReceivedSetRelay onReceivedSetRelay) {
        this.onReceivedSetRelay = onReceivedSetRelay;
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


        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setCurrentScheduleFromReport(message);
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
        schedule = getSchedulesSwitch().get(i);

        // Ahora actualizamos los datos en la estructura

        schedule.setNewScheduleIdFromReport(message);
        setRelayStateFromReport(message);
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        schedule.setDurationFromReport(message);
        schedule.setScheduleStateFromReport(message);
        setCurrentScheduleFromReport(message);
        return IOT_CODE_RESULT.RESULT_CODE_ERROR;

    }

    protected IOT_CODE_RESULT setRelayStateFromReport(String message) {

        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        int i;
        i = api.getJsonInt(message, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        if (i < 0) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setRelay(IOT_SWITCH_RELAY.UNKNOWN.fromId(i));
        return IOT_CODE_RESULT.RESUT_CODE_OK;

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

    public void setNewSchedule(IotScheduleDeviceSwitch schedule) {

        this.addSchedule(schedule);

    }


    @Override
    protected void processCommand(String topic, MqttMessage message) {

        IOT_COMMANDS idComando;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        String mensaje = new String(message.getPayload());
        idComando = api.getCommandId(mensaje);
        IOT_CODE_RESULT res;

        switch (idComando) {
            case SET_RELAY:
                res = processSetRelayCommand(mensaje);
                if (onReceivedSetRelay != null) {
                    onReceivedSetRelay.onReceivedSetRelay(res);
                }
                break;
        }


        super.processCommand(topic, message);

    }

    public DEVICE_STATE_CONNECTION setRelayCommand(IOT_SWITCH_RELAY action) {

        JSONObject parameters;
        parameters = new JSONObject();
        DEVICE_STATE_CONNECTION state = DEVICE_STATE_CONNECTION.UNKNOWN;
        try {
            parameters.put(TEXTOS_DIALOGO_IOT.OP_RELE.getValorTextoJson(), action.getEstadoRele());
        } catch (JSONException e) {
            e.printStackTrace();
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }

        return commandwithParameters(IOT_COMMANDS.SET_RELAY, TEXTOS_DIALOGO_IOT.RELE.getValorTextoJson(), parameters);
    }


    protected IOT_CODE_RESULT setStateRelayFromReport(String message) {

        ApiDispositivoIot api;
        int i;
        api = new ApiDispositivoIot();
        i = api.getJsonInt(message, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        if (i<0) {
            return IOT_CODE_RESULT.RESULT_CODE_NOK;
        }
        setRelay(IOT_SWITCH_RELAY.ON.fromId(i));

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    protected IOT_CODE_RESULT processSetRelayCommand(String message) {

        IOT_CODE_RESULT res;

        if ((res = getCommandCodeResultFromReport(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            return res;
        }
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        setStateRelayFromReport(message);

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }




}
