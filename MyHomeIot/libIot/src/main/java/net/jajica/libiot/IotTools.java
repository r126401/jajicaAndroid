package net.jajica.libiot;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Esta clase implementa todo el api de dialogo entre la aplicacion y el dispositivo.
 */
public class IotTools implements Serializable {

    private String commandKey;
    protected final String TAG="IotTools";

    public IotTools() {
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
            comandoJson.put(IOT_LABELS_JSON.JSON_COMMAND.getValorTextoJson(), cabecera);
            cabecera.put(IOT_LABELS_JSON.KEY_COMMAND.getValorTextoJson(), commandKey);
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
            intermedio = comando.getJSONObject(IOT_LABELS_JSON.JSON_COMMAND.getValorTextoJson());
            intermedio.put(IOT_LABELS_JSON.COMMAND.getValorTextoJson(), nComando.getIdComando());
            intermedio.put(IOT_LABELS_JSON.COMMAND_NAME.getValorTextoJson(), nComando.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comando.toString();

    }


    public String getJsonString(String texto, String parametro) {

        JSONObject objetoJson = null;
        String dato = null;

        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_COMMAND);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getString(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_COMMAND.toString());
            }
        }
        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_ANSWER);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getString(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_ANSWER.toString());

            }
        }

        try {
            objetoJson = new JSONObject(texto);
            dato = objetoJson.getString(parametro);
        } catch (JSONException e) {

            //e.printStackTrace();
            return null;
        }
        return dato;

    }

    public int getJsonInt(String texto, String parametro) {
        JSONObject objetoJson = null;
        int dato = -1000;

        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_COMMAND);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getInt(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_COMMAND.toString());
            }
        }
        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_ANSWER);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getInt(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_ANSWER.toString());
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

        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_COMMAND);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getDouble(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_COMMAND.toString());
            }
        }
        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_ANSWER);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getDouble(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_ANSWER.toString());

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

        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_COMMAND);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getBoolean(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_COMMAND.toString());
            }
        }
        objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_ANSWER);
        if (objetoJson != null) {
            try {
                dato = objetoJson.getBoolean(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + IOT_LABELS_JSON.JSON_ANSWER.toString());
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
    protected JSONObject getJsonPart(String texto, IOT_LABELS_JSON parte) {

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
            objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_COMMAND);
            if (objetoJson == null) {
                objetoJson = getJsonPart(texto, IOT_LABELS_JSON.JSON_ANSWER);
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


    public IOT_COMMANDS getCommandId(String texto) {

        int idComando = -100;
        JSONObject objetoJson = null;

        try {
            objetoJson = new JSONObject(texto);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            idComando = objetoJson.getInt(IOT_LABELS_JSON.COMMAND.getValorTextoJson());
        } catch (JSONException e) {
            //e.printStackTrace();
            return IOT_COMMANDS.SPONTANEOUS;
        }

        return IOT_COMMANDS.SPONTANEOUS.fromId(idComando);

    }



    protected int getFieldIntFromReport(String message, IOT_LABELS_JSON field) {
        IotTools api;
        int dat;
        api = new IotTools();
        dat = api.getJsonInt(message, field.getValorTextoJson());
        return dat;


    }

    protected String getFieldStringFromReport(String message, IOT_LABELS_JSON field) {

        IotTools api;
        String stream;
        api = new IotTools();
        stream = api.getJsonString(message, field.getValorTextoJson());
        return stream;
    }


}
