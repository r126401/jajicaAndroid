package net.jajica.libiot;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class IotDeviceSwitch extends IotDevice implements Serializable {


    protected IOT_SWITCH_RELAY Relay;

    public IOT_SWITCH_RELAY getRelay() {
        return Relay;
    }

    public void setRelay(IOT_SWITCH_RELAY relay) {
        this.Relay = relay;
    }

    public IotDeviceSwitch(IotMqttConnection cnx) {
        super(cnx);
        setPublishOtaTopic("OtaIotOnOff");
        setSubscribeOtaTopic("newVersionOtaIotOnOff");
        setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);

    }

    public IotDeviceSwitch() {
        super();
        setPublishOtaTopic("OtaIotOnOff");
        setSubscribeOtaTopic("newVersionOtaIotOnOff");
        setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
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

    protected OnReceivedSpontaneousActionRelay onReceivedSpontaneousActionRelay;
    public interface OnReceivedSpontaneousActionRelay {
        void onReceivedSpontaneousActionRelay(IOT_CODE_RESULT resultCode);
    }

    public void setOnReceivedSpontaneousActionRelay(OnReceivedSpontaneousActionRelay onReceivedSpontaneousActionRelay) {
        this.onReceivedSpontaneousActionRelay = onReceivedSpontaneousActionRelay;
    }

    @Override
    protected IOT_CODE_RESULT processStatusFromReport(String respuesta) {

        int res;
        IotTools api;
        api = new IotTools();
        IOT_SWITCH_RELAY estado = IOT_SWITCH_RELAY.UNKNOWN;
        res = api.getJsonInt(respuesta, IOT_LABELS_JSON.STATE_RELAY.getValorTextoJson());
        setRelay(estado.fromId(res));
        return super.processStatusFromReport(respuesta);
    }

    
    private ArrayList<IotScheduleDeviceSwitch> loadSchedules(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONObject objetoPrograma = null;
        JSONArray arrayProgramas;
        int i;
        IotScheduleDeviceSwitch programa;

        if (schedules!= null) {
            if (schedules.size() > 0) schedules.clear();
            Log.i(TAG, "device es :  " + hashCode() + " schedules borrados");
        }


        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(IOT_LABELS_JSON.SCHEDULE.getValorTextoJson());
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
/*
    @Override
    public IOT_DEVICE_STATE_CONNECTION RegisterListenerMqttConnection() {
        return super.RegisterListenerMqttConnection();
    }
*/
    @Override
    protected IOT_CODE_RESULT processGetScheduleFromReport(String message) {

        processCommonParameters(message);
        loadSchedules(message);
        //return super.processGetSchedule(message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }
    private boolean addSchedule(IotScheduleDeviceSwitch programa) {

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
        setStateRelayFromReport(message);
        setDeviceStateFromReport(message);
        setProgrammerStateFromReport(message);
        schedule.setDurationFromReport(message);
        schedule.setScheduleStateFromReport(message);
        setCurrentScheduleFromReport(message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    @Override
    public int searchSchedule(String schedule) {
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
        IotTools api;
        api = new IotTools();
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

    public IOT_DEVICE_STATE_CONNECTION commandSetRelay(IOT_SWITCH_RELAY action) {

        JSONObject parameters;
        parameters = new JSONObject();
        IOT_DEVICE_STATE_CONNECTION state = IOT_DEVICE_STATE_CONNECTION.UNKNOWN;
        try {
            parameters.put(IOT_LABELS_JSON.ACTION_RELAY.getValorTextoJson(), action.getEstadoRele());
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }

        return commandwithParameters(IOT_COMMANDS.SET_RELAY, IOT_LABELS_JSON.RELAY.getValorTextoJson(), parameters);
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

    protected IOT_CODE_RESULT processSetRelayCommand(String message) {

        IOT_CODE_RESULT res;

        if ((res = processCommonParameters(message)) != IOT_CODE_RESULT.RESUT_CODE_OK) {
            return res;
        }
        setStateRelayFromReport(message);
        setCurrentOtaVersionFromReport(message);
        setDeviceTypeFromReport(message);

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    @Override
    protected IOT_CODE_RESULT processStartDevice(String message) {

        setStateRelayFromReport(message);
        return super.processStartDevice(message);

    }

    @Override
    protected IOT_CODE_RESULT processStartSchedule(String message) {
        setStateRelayFromReport(message);
        return super.processStartSchedule(message);
    }

    @Override
    protected IOT_CODE_RESULT processEndSchedule(String message) {
        setStateRelayFromReport(message);
        return super.processStartSchedule(message);
    }

    @Override
    protected IOT_TYPE_ALARM_DEVICE processAlarmReport(String message) {
        setStateRelayFromReport(message);
        return super.processAlarmReport(message);
    }

    @Override
    protected void processSpontaneous(String topic, MqttMessage message) {
        super.processSpontaneous(topic, message);
        IOT_SPONTANEOUS_TYPE typeInform;
        String mensaje = new String(message.getPayload());
        typeInform = getSpontaneousType(mensaje);
        IOT_CODE_RESULT result;
        switch (typeInform) {

            case START_DEVICE:
                processStartDevice(mensaje);
                break;
            case ACTUACION_RELE_LOCAL:
                result = processSpontaneousActionRelay(mensaje);
                if (onReceivedSpontaneousActionRelay != null) {
                    onReceivedSpontaneousActionRelay.onReceivedSpontaneousActionRelay(result);
                }
                break;
            case UPGRADE_FIRMWARE_FOTA:
                break;
            case START_SCHEDULE:
                break;
            case COMANDO_APLICACION:
                break;
            case END_SCHEDULE:
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

    protected IOT_CODE_RESULT processSpontaneousActionRelay(String message) {

        super.processCommonParameters(message);
        setStateRelayFromReport(message);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    @Override
    public JSONObject object2Json() {


        if(getRelay() != null) {
            try {
                dispositivoJson.put(IOT_LABELS_JSON.STATE_RELAY.getValorTextoJson(), getRelay().getEstadoRele());

            } catch (JSONException e) {
                //return null;
            }
        }
        return super.object2Json();
    }

    @Override
    public IOT_JSON_RESULT json2Object(JSONObject jsonDevice) {

        IOT_SWITCH_RELAY status = IOT_SWITCH_RELAY.UNKNOWN;
        try {
            setRelay(status.fromId(jsonDevice.getInt(IOT_LABELS_JSON.STATE_RELAY.getValorTextoJson())));
        } catch (JSONException e) {
            //return IOT_JSON_RESULT.JSON_CORRUPTO;
           // return null;
        }

        return super.json2Object(jsonDevice);
    }


}
