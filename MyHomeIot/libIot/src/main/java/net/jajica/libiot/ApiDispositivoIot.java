package net.jajica.libiot;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Esta clase implementa todo el api de dialogo entre la aplicacion y el dispositivo.
 */
public class ApiDispositivoIot implements Serializable {

    private String commandKey;
    private MqttConnection cnx;
    ArrayList<TimersApi> timersApi;
    onTimeoutApiJson listenerTimeoutApiJson;
    protected final String TAG="ApiDispositivoIot";
    private OnMessagesReceived listenerArrivedMessages;

    public interface onTimeoutApiJson {

        public void commandTimeOut(IOT_COMMANDS command, String key, String idDevice);

    }

    public interface OnMessagesReceived {

        public void recibirRespuestaComando(IotDevice dispositivo);
        public void recibirMensajeEspontaneo(IotDevice dispositivo);
    }

    public OnMessagesReceived getListenerArrivedMessages() {
        return listenerArrivedMessages;
    }

    public void setListenerArrivedMessages(OnMessagesReceived listenerArrivedMessages) {
        this.listenerArrivedMessages = listenerArrivedMessages;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    public MqttConnection getCnx() {
        return cnx;
    }

    public void setCnx(MqttConnection cnx) {
        this.cnx = cnx;
    }

    public ArrayList<TimersApi> getTimersApi() {
        return timersApi;
    }

    public void setTimersApi(ArrayList<TimersApi> timersApi) {
        this.timersApi = timersApi;
    }

    public onTimeoutApiJson getListenerTimeoutApiJson() {
        return listenerTimeoutApiJson;
    }

    public void setListenerTimeoutApiJson(onTimeoutApiJson listenerTimeoutApiJson) {
        this.listenerTimeoutApiJson = listenerTimeoutApiJson;
    }


    public ApiDispositivoIot(MqttConnection cnx) {

        setCommandKey(UUID.randomUUID().toString());

        cnx.setOnListenerArrivedMessaged("hola", new MqttConnection.OnArrivedMessage() {
            @Override
            public void arrivedMessage(String topic, MqttMessage message) {
                Log.i(TAG, "Llego un mensaje");
                procesarMensaje(topic, message);
            }

        });

        timersApi = null;

    }

    public ApiDispositivoIot() {
        timersApi = null;
        this.cnx = cnx;
    }

    private JSONObject generarCabecera() {

        JSONObject cabecera;
        JSONObject comandoJson = new JSONObject();
        cabecera = new JSONObject();


        String fecha;

        this.commandKey = UUID.randomUUID().toString();
        Log.i(getClass().toString(), "generarCabecera: Generando clave: " + commandKey);
        Calendar date = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        fecha = formato.format(date.getTime());


        try {
            comandoJson.put(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson(), cabecera);
            cabecera.put(TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson(), commandKey);
            cabecera.put("date", fecha);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comandoJson;
    }


    /**
     * Este metodo crea un comando generico a partir del numero de comando
     * @param nComando es la id del comando a enviar
     * @return se retorna el texto del comando para enviar.
     */
    public String createSimpleCommand(IOT_COMMANDS nComando) {

        JSONObject comando;
        JSONObject intermedio;


        intermedio = new JSONObject();
        comando = generarCabecera();
        try {
            intermedio = comando.getJSONObject(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            intermedio.put(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson(), nComando.getIdComando());
            intermedio.put(TEXTOS_DIALOGO_IOT.NOMBRE_COMANDO.getValorTextoJson(), nComando.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comando.toString();

    }


    public String getJsonString(String texto, String parametro) {

        JSONObject objetoJson = null;
        String dato = null;

        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getString(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getString(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.DLG_RESPUESTA.toString());

            }
        }

        try {
            objetoJson = new JSONObject(texto);
            dato = objetoJson.getString(parametro);
        } catch (JSONException e) {

            e.printStackTrace();
            return null;
        }
        return dato;

    }

    public int getJsonInt(String texto, String parametro) {
        JSONObject objetoJson = null;
        int dato = -1000;

        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getInt(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getInt(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.DLG_RESPUESTA.toString());
                //return -1000;
            }
        }

        try {
            objetoJson = new JSONObject(texto);
            dato = objetoJson.getInt(parametro);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1000;
        }


        return dato;
    }

    public double getJsonDouble(String texto, String parametro) {
        JSONObject objetoJson = null;
        double dato = -1000;

        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getDouble(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getDouble(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.DLG_RESPUESTA.toString());

            }
        }
        try {
            objetoJson = new JSONObject(texto);
            dato = objetoJson.getDouble(parametro);
        } catch (JSONException e) {

            e.printStackTrace();
            return -1000;
        }
        return dato;

    }

    public Boolean getJsonboolean(String texto, String parametro) {
        JSONObject objetoJson = null;
        Boolean dato = true;

        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getBoolean(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getBoolean(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.DLG_RESPUESTA.toString());
                //return -1000;
            }
        }

        try {
            objetoJson = new JSONObject(texto);
            dato = objetoJson.getBoolean(parametro);
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }


        return dato;
    }

    /**
     * Esta funcion devuelve un objeto json con la parte del dialogo del dispositivo
     *
     * @param texto es el contenido del mensaje
     * @param parte COMANDO o DLG_respuesta
     * @return devuelve pa parte del mensaje o null si no se encuentra o es erroneo
     */
    protected JSONObject getJsonPart(String texto, TEXTOS_DIALOGO_IOT parte) {

        JSONObject objeto = null;
        JSONObject devuelto = null;

        try {
            objeto = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "textoJson erroneo");
            return null;
        }

        try {
            devuelto = objeto.getJSONObject(parte.getValorTextoJson());
        } catch (JSONException e) {
            //e.printStackTrace();
            //Log.w(getClass().toString(), "No se encuentra la parte del json: " + parte.toString());
        }

        return devuelto;

    }

    public Boolean extraerDatoJsonBool(String texto, String parametro) {
        JSONObject objetoJson = null;
        Boolean dato = true;

        try {
            objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.COMANDO);
            if (objetoJson == null) {
                objetoJson = getJsonPart(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
                if (objetoJson == null) {
                    Log.e(TAG, "Error al buscar la etiqueta: " + parametro);
                    return false;
                }
            } else {
                dato = objetoJson.getBoolean(parametro);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return dato;
    }

/*
    public DEVICE_STATE_CONNECTION enviarComando(IotDevice dispositivo, String comandoJson, onTimeoutApiJson listenerTimout) {

        TimersApi temporizador;
        IOT_COMMANDS idComando = IOT_COMMANDS.ERROR_RESPUESTA;
        DEVICE_STATE_CONNECTION estado;
        String clave;
        clave = getJsonString(comandoJson, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson());

        int a =  getJsonInt(comandoJson, TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson());
        idComando = idComando.fromId(a);
        temporizador = new TimersApi(idComando, dispositivo.getIdDispositivo(), clave, new TimersApi.onTemporizacionComandos() {
            @Override
            public void temporizacionVencida(IOT_COMMANDS comando, String clave, String idDispositivo) {

            }

            @Override
            public void informeIntermedio(IOT_COMMANDS comando) {

            }
        });
        if (cnx.getStateConnection() == MQTT_STATE_CONNECTION.CONEXION_MQTT_COMPLETA) {
            estado = cnx.publicarTopic(dispositivo.getTopicPublicacion(), comandoJson);
            if (estado != DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
                return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;

            }
        }

        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;


    }

    public boolean enviarComando(IotDevice dispositivo, String comandoJson) {

        TimersApi temporizador;
        IOT_COMMANDS idComando = IOT_COMMANDS.ERROR_RESPUESTA;
        String clave;
        clave = getJsonString(comandoJson, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson());

        int a =  getJsonInt(comandoJson, TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson());
        idComando = idComando.fromId(a);



        temporizador = new TimersApi(idComando, dispositivo.getIdDispositivo(), clave);
        temporizador.setOnTemporizacionComandos(new TimersApi.onTemporizacionComandos() {
            @Override
            public void temporizacionVencida(IOT_COMMANDS comando, String clave, String idDispositivo) {
                if (listenerTimeoutApiJson != null) {
                    eliminarTemporizador(clave);
                    listenerTimeoutApiJson.commandTimeOut(comando, clave, idDispositivo);
                }

            }

            @Override
            public void informeIntermedio(IOT_COMMANDS comando) {

            }
        });
        if (timersApi == null) {
            timersApi = new ArrayList<TimersApi>();
            Log.i(getClass().toString(), "enviarComando: Se crea el array");
        }


        if (cnx!=null) {

            if (cnx.publicarTopic( dispositivo.getTopicPublicacion(), comandoJson) == DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
                temporizador.crearTemporizacion(10000, 5000);
                dispositivo.setEstadoConexion(DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE);
                timersApi.add(temporizador);
            } else {
                return false;
            }

        }
        return true;


    }


*/

    public void eliminarTemporizador(String clave) {

        int i = 0;
        if (timersApi == null) {
            Log.e(getClass().toString(), "No hay ningun temporizador asociado");
            return;
        }
        Log.i(getClass().toString(), "eliminarTemporizador: clave: " + clave + " tamaño: " + timersApi.size());

        for (i=0;i<timersApi.size();i++) {
            if(timersApi.get(i).getClave().equals(clave)) {
                Log.i(getClass().toString(), "eliminarTemporizador: Temporizador con clave " + timersApi.get(i).getClave() + " borrado");
                timersApi.get(i).temporizador.cancel();
                timersApi.remove(i);
                return;

            }
        }
        Log.e(getClass().toString(), "eliminarTemporizador: No se ha encontrado el Temporizador con clave " + clave + " y valor " + String.valueOf(i));
    }

    private void procesarMensaje(String topic, MqttMessage message) {

        IOT_REPORT_TYPE tipoInforme = null;
        String mensaje;
        IotDevice dispositivo;
        mensaje = new String (message.getPayload());

        IOT_COMMANDS comando;
        comando = getCommandId(mensaje);
        if (comando == IOT_COMMANDS.SPONTANEOUS) {
            tipoInforme = IOT_REPORT_TYPE.SPONTANEOUS_REPORT;
        } else {
            tipoInforme = IOT_REPORT_TYPE.COMMAND_REPORT;
        }

        switch (tipoInforme) {

            case COMMAND_REPORT:
                procesarComando(topic, message);
                break;
            case SPONTANEOUS_REPORT:
                procesarEspontaneo(topic, message);
                break;
        }

    }

    protected void procesarComando(String topic, MqttMessage message) {
        IOT_COMMANDS idComando;

        String mensaje = new String(message.getPayload());
        idComando = getCommandId(mensaje);

        switch (idComando) {

            case INFO_DEVICE:
                break;
            case SET_RELAY:
                break;
            case STATUS_DEVICE:
                break;
            case GET_SCHEDULE:
                break;
            case NEW_SCHEDULE:
                break;
            case REMOVE_SCHEDULE:
                break;
            case MODIFY_SCHEDULE:
                break;
            case MODIFY_PARAMETER_DEVICE:
                break;
            case RESET:
                break;
            case FACTORY_RESET:
                break;
            case MODIFY_CLOCK:
                break;
            case UPGRADE_FIRMWARE:
                break;
            case VERSION_OTA:
                break;
            case ERROR_REPORT:
                break;
            default:
                break;
        }




    }

    protected void procesarEspontaneo(String topic, MqttMessage message) {
        IotDevice dispositivo = null;
        listenerArrivedMessages.recibirMensajeEspontaneo(dispositivo);

    }


    public IOT_DEVICE_TYPE getDeviceType(String textoJson) {

        int tipo = -100;
        JSONObject objeto;

        tipo = getJsonInt(textoJson, TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        return IOT_DEVICE_TYPE.DESCONOCIDO.fromId(tipo);

    }

    public IOT_COMMANDS getCommandId(String texto) {

        int idComando = -100;
        JSONObject objetoJson = null;

        try {
            objetoJson = new JSONObject(texto);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            idComando = objetoJson.getInt(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson());
        } catch (JSONException e) {
            //e.printStackTrace();
            return IOT_COMMANDS.SPONTANEOUS;
        }

        return IOT_COMMANDS.SPONTANEOUS.fromId(idComando);

    }

    public void enviarComando(String Comando) {

        Timer timer;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 30000);
    }

    private String escribirComandoGenerico(IOT_COMMANDS nComando) {

        JSONObject comando;
        JSONObject intermedio;


        intermedio = new JSONObject();
        comando = generarCabecera();
        try {
            intermedio = comando.getJSONObject(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            intermedio.put(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson(), nComando.getIdComando());
            intermedio.put(TEXTOS_DIALOGO_IOT.NOMBRE_COMANDO.getValorTextoJson(), nComando.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comando.toString();

    }

}
