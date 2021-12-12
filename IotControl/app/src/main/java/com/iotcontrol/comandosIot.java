package com.iotcontrol;

import android.content.Context;
import android.content.*;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.os.Build;
import android.util.Log;
import android.widget.Switch;


public class comandosIot {

    public final String ficheroMqtt = "iotOnOffMqtt.conf";
    public final String ficheroDispositivos = "iotDispositivos.conf";
    public JSONObject datosDispositivos = null;
    public JSONObject datosMqtt = null;

    static final int IOT_ON_OFF = 0;
    static final int IOT_CRONO_TEMP = 1;


    /**
     * Mnemonicos de los parametros Mqtt
     */
    static final String MQTT = "mqtt";
    static final String BROKER = "broker";
    static final String PUERTO = "puerto";
    static final String USUARIO = "usuario";
    static final String PASSWORD = "password";
    static final String DISPOSITIVOS = "dispositivos";
    static final String MQTT_BROKER  =   "mqttBroker";
    static final String MQTT_PORT  =     "mqttPort";
    static final String MQTT_USER   =    "mqttUser";
    static final String MQTT_PASS    =   "mqttPass";
    static final String MQTT_PREFIX   =  "mqttPrefix";
    static final String MQTT_PUBLISH   = "mqttPublish";
    static final String MQTT_SUBSCRIBE  = "mqttSubscribe";


    static final String LIBRERIA = "comandosIot";

    /**
     * Mnemonicos de los parametros de dialogo json
     */
    static final String OP_RELE = "opRele";
    static final String COMANDO = "comando";
    static final String DLGKEY ="dlgKey";
    static final String FECHA = "date";
    static final String RELOJ = "clock";
    static final String DLG_COMANDO = "dlgComando";
    static final String DLG_NOMBRE_COMANDO = "nombreComando";
    static final String ID_DEVICE = "idDevice";
    static final String DLG_RESPUESTA = "dlgRespuesta";
    static final String DLG_NOTIFICACION = "dlgRespuesta";
    static final String DLG_RESULT_CODE = "dlgResultCode";
    static final String CLAVE = "dlgKey";
    static final String ESTADO_RELE = "estadoRele";
    static final String RELE = "rele";
    static final String SEPARADOR = "-------------------------------";
    static final int PROGRAMA_DIARIO = 0;
    static final int PROGRAMA_SEMANAL = 1;
    static final int PROGRAMA_FECHADO = 2;
    static final String PROGRAMA = "program";
    static final String ID_PROGRAMA = "programId";
    static final String ID_PROGRAMA_ACTIVO = "currentProgramId";
    static final String TIPO_PROGRAMA = "programType";
    static final String HORA_PROGRAMA = "hour";
    static final String MINUTO_PROGRAMA = "minute";
    static final String SEGUNDO_PROGRAMA = "second";
    static final String DIA_SEMANA_PROGRAMA = "weekDay";
    static final String ESTADO_PROGRAMA = "programState";
    static final String MASCARA_PROGRAMA = "programMask";
    static final String ANO_PROGRAMA = "year";
    static final String MES_PROGRAMA = "month";
    static final String DIA_PROGRAMA = "day";
    static final String ESTADO_RELE_PROGRAMA = "estadoRele";
    static final String DURATION_PROGRAM = "durationProgram";
    static final String FREE_MEMORY = "freeMem";
    static final String UPTIME =        "uptime";
    static final String MAP_MEMORY =     "memoryMap";
    static final String RTCTIME =        "rtcTime";
    static final String ADRESS_BIN =     "adressBin";
    static final int INVALID_PROG = 0;
    static final int VALID_PROG = 1;
    static final int INH_PROG = 2;
    static final String DEVICE_STATE = "deviceState";
    static final String DEVICE ="device";
    static int NORMAL_AUTO = 0;
    static int NORMAL_AUTOMAN = 1;
    static int NORMAL_MANUAL = 2;
    static int NORMAL_ARRANCANDO = 3;
    static int NORMAL_SIN_PROGRAMACION = 4;
    static final String TEMPERATURA = "temperatura";
    static final String HUMEDAD = "humedad";
    static final String UMBRAL_TEMPERATURA = "temperaturaUmbral";
    static final String MODIFICAR_APP = "modificarApp";




    /**
     * Mnemonicos de los parametros de dispositivosIot
     */

    static final String ID_DISPOSITIVO = "idDispositivo";
    static final String NOMBRE_DISPOSITIVO ="nombreDispositivo";
    static final String TOPIC_PUBLICACION = "topicPublicacion";
    static final String TOPIC_SUBSCRIPCION = "topicSubscripcion";

    /**
     * Constantes de comandos
     */

    static final int ACTUAR_RELE = 50;
    static final int STATUS = 51;
    static final int CONSULTAR_PROGRAMACION = 6;
    static final int NUEVA_PROGRAMACION = 7;
    static final String NOMBRE_NUEVA_PROGRAMACION = "nuevoPrograma";
    static final int ELIMINAR_PROGRAMACION = 9;
    static final String NOMBRE_ELIMINAR_PROGRAMACION = "eliminarPrograma";
    static final int MODIFICAR_PROGRAMACION = 8;
    static final String NOMBRE_MODIFICAR_PROGRAMACION = "modificarPrograma";
    static final int RESET = 10;
    static final int FACTORY_RESET = 11;
    static final int MODIFY_CLOCK = 15;
    static final int UPGRADE_FIRMWARE = 26;
    static final int INFO_DISPOSITIVO = 0;
    static final int MODIFICAR_UMBRAL_TEMPERATURA = 52;
    static final int APAGADO = 0;
    static final int ENCENDIDO = 1;
    static final String jsonOTA = "{\"upgradeFirmware\":{\"otaVersiontype\":\"iotOnOff\"}}";
    static final String topicPeticionOTA ="OtaIotOnOff";
    static final String topicRespuestaOTA = "newVersionOtaIotOnOff";
    static final String topicOtaIotOnOff = "OtaIotOnOff";
    static final String OTA_SERVER = "otaServer";
    static final String OTA_PORT = "otaPort";
    static final String OTA_URL ="otaUrl";
    static final String OTA_FILE = "otaFile";
    static final String OTA_VERSION = "otaVersion";
    static final String DLG_UPGRADE_FIRMWARE  = "upgradeFirmware";

    public JSONObject comandoJson = null;
    private int estadoRele;
    private final String clave;

    /**
     * Parametros OTA
     */
    public String otaServer;
    public int otaPort;
    public String otaUrl;
    public String otaFile;
    public int currentOtaVersion;
    public int newOtaVersion;
    boolean nuevaVersionDisponible;

    /**
     * parametros de configuracion de la aplicacion
     */

    static final String ESTADO_PROGRAMACION = "programmerState";
    static final String NUMERO_PROGRAMAS = "programNumbers";
    static final String MEMORIA_ACTIVA = "configActive";




    public String getIdClave() {
        return clave;
    }




    public comandosIot() {
        super();
        clave = UUID.randomUUID().toString();
        nuevaVersionDisponible = false;
        currentOtaVersion = 0;
        newOtaVersion = 0;




    }





    public int getEstadoRele() {
        return estadoRele;
    }
    public String getNombreDispositivo(JSONObject dispositivo) { return null; }
    public boolean isNuevaVersionDisponible() { return nuevaVersionDisponible;}


    /**
     * Este metodo comprueba si hay una nueva version disponible para hacer upgrade.
     * @return true si hay una nueva version.
     */
    public boolean comprobarNuevaVersionDisponible(int versionDispositivo) {


        currentOtaVersion = versionDispositivo;
        if (currentOtaVersion > 0) {
            if (newOtaVersion > currentOtaVersion) {
                nuevaVersionDisponible = true;
                Log.w(LIBRERIA, "Encontrada nueva version OTA " + newOtaVersion);
                return true;
            } else {
                nuevaVersionDisponible = false;
                Log.w(LIBRERIA, "No hay nueva version OTA disponible");
                return false;
            }
        } else {
            nuevaVersionDisponible = false;
            Log.w(LIBRERIA, "Todavia no hay datos de currentVersion");
            return false;
        }

    }

    /**
     * Esta funcion envia el objeto json para detectar la nueva version de OTA disponible para el
     * dispositivo
     * @return
     */

    public String enviarComandoOtaDisponible() {

        JSONObject upgrade = null;
        JSONObject otaVersion;

        try {
            upgrade = new JSONObject();
            otaVersion = new JSONObject();
            otaVersion.put("otaVersiontype", "iotOnOff");
            upgrade.put("upgradeFirmware", otaVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.w(LIBRERIA, "Se envia: " + upgrade.toString());

        return upgrade.toString();



    }

    public void modificarTipoDispositivoIot(String idDispositivo, int tipoDispositivo, Context contexto) {

        JSONArray array;
        int tamArray, i;
        JSONObject elemento;

        array = datosDispositivos.optJSONArray(DISPOSITIVOS);
        for (i=0;i<array.length();i++) {
            if (idDispositivo.equals(array.optJSONObject(i).optString(NOMBRE_DISPOSITIVO))) {
                elemento = array.optJSONObject(i);
                elemento.remove(DEVICE);
                try {
                    elemento.put(DEVICE, tipoDispositivo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                escribirConfiguracion(ficheroDispositivos, datosDispositivos.toString(), contexto);


                break;

            }
        }



    }

    /**
     * Esta funcion recibe el objeto a modificar y lo guarda en el fichero de configuracion.
     *
     * @param dispositivo
     * @param contexto
     * @return true si se ha modificado con exito
     */
    public boolean modificarDispositivoIot(String nombreAntiguo, String nombre, String id, String pub, String sub, int tipoDispositivo, Context contexto) {

        JSONObject dispositivo;
        int i, tamArray=0;
        JSONArray array = null;
        String idDispositivo;

        dispositivo = new JSONObject();


        try {
            dispositivo.put(NOMBRE_DISPOSITIVO, nombre );
            dispositivo.put(ID_DISPOSITIVO, id);
            dispositivo.put(TOPIC_PUBLICACION, pub);
            dispositivo.put(TOPIC_SUBSCRIPCION, sub);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        if (leerConfiguracion(contexto, ficheroDispositivos) == true) {




            try {
                array = datosDispositivos.getJSONArray(DISPOSITIVOS);
                tamArray = array.length();
                for (i=0;i<tamArray;i++) {
                    if (nombreAntiguo.equals(array.getJSONObject(i).getString(NOMBRE_DISPOSITIVO))) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            array.remove(i);
                        }
                        array.put(dispositivo);
                        escribirConfiguracion(ficheroDispositivos, datosDispositivos.toString(), contexto);
                        //borrar el viejo
                        //poner el neuvo.

                        break;

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
        return false;

    }


    public boolean PonerNuevoDispositivoIot(JSONObject dispositivo, Context contexto) {

        JSONArray array = null;
        JSONObject root = null;
        String topicPublicacion = null;
        String topicSubscripcion = null;
        String idDispositivo;


        idDispositivo = dispositivo.optString(ID_DISPOSITIVO);
        idDispositivo = idDispositivo.toUpperCase();
        topicPublicacion = dispositivo.optString(TOPIC_PUBLICACION);

        if (topicPublicacion.equals("")) {
            try {
                dispositivo.put(TOPIC_PUBLICACION, "/sub_" + idDispositivo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        topicSubscripcion = dispositivo.optString(TOPIC_SUBSCRIPCION);
        if (topicSubscripcion.equals("")) {
            try {
                dispositivo.put(TOPIC_SUBSCRIPCION, "/pub_" + idDispositivo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        leerConfiguracion(contexto, ficheroDispositivos);



        if (datosDispositivos == null) {
            root = new JSONObject();
            array = new JSONArray();

            array.put(dispositivo);

            try {
                root.put(DISPOSITIVOS, array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            escribirConfiguracion(ficheroDispositivos, root.toString(), contexto );


        } else {
            try {
                datosDispositivos.getJSONArray(DISPOSITIVOS).put(dispositivo);

                escribirConfiguracion(ficheroDispositivos, datosDispositivos.toString(), contexto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    /**
     * Funcion para eliminar el dispositivo de la estructura de deatos de dispositivos
     * @param nombreDispositivo
     */
    public void eliminarDispositivo(String nombreDispositivo, String tipoBusqueda, Context contexto) {

        int i, tamArray=0;
        JSONArray array = null;


        try {
            array = datosDispositivos.getJSONArray(DISPOSITIVOS);
            tamArray = array.length();
            for (i=0;i<tamArray;i++) {
                if (nombreDispositivo.equals(array.getJSONObject(i).getString(tipoBusqueda))) {
                    array.remove(i);
                    escribirConfiguracion(ficheroDispositivos, datosDispositivos.toString(), contexto);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }





    }


    /**
     * Esta funcion valida la recepcion del mensaje, analiza la cabecera y determina si la respuesta
     * es valida y si es para un dispositivo de la aplicacion.
     * @param texto
     * @return
     */
    public JSONObject analizarCabecera(String texto) {

        JSONObject respuesta = null;
        JSONObject dlgComando;
        JSONObject dlgNotificacion;
        JSONObject dlgRespuesta;
        JSONObject dispositivo;
        String idDispositivo;
        String otaVersion;
        String fecha;
        String clave;
        int comando;
        int codRespuesta;
        int indice = -1;


        // Se comprueba que la respuesta es un json valido
        try {
            respuesta = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        // Comprobamos si pudiera ser un mensaje de programacion.

        indice = texto.indexOf(SEPARADOR);
        if (indice != -1) {
            //Es un mensaje de programacion.
            texto = texto.replace(SEPARADOR, " ");
            dlgComando = respuesta.optJSONObject(COMANDO);
            //currentOtaVersion = dlgComando.optString(OTA_VERSION);
            if (dlgComando != null) {
                idDispositivo = dlgComando.optString(ID_DEVICE);
                dispositivo = buscarDispositivoPorId(idDispositivo);
                if (dispositivo != null) {
                    //dispositivo encontrado
                    return respuesta;
                } else {
                    return null;
                }


            }
        }
        // Determinar si el mensaje es de mi dispositivo.
        //Determinar si es una respuesta a un comando o una notificacion

        dlgComando = respuesta.optJSONObject(COMANDO);
        if (dlgComando == null) {
            //Es una notificacion
            dlgNotificacion = respuesta.optJSONObject(DLG_NOTIFICACION);
            //currentOtaVersion = dlgNotificacion.optString(OTA_VERSION);
            idDispositivo = dlgNotificacion.optString(ID_DEVICE);
            if (!idDispositivo.equals("OtaServer") ) {
                dispositivo = buscarDispositivoPorId(idDispositivo);
                if (dispositivo != null) {
                    //Si se encuentra el dispositivo
                    return respuesta;
                } else {
                    //no se ha encontrado el dispositivo en la aplicacion.
                    return null;
                }
            }


        } else {
            // Es una respuesta
            dlgRespuesta = respuesta.optJSONObject(DLG_RESPUESTA);
            codRespuesta = dlgRespuesta.optInt(DLG_RESULT_CODE);
            if (codRespuesta == 200) {
                // respuesta valida
                Log.w(LIBRERIA, "analizarCabecera. respuesta recibida valida");
                idDispositivo = dlgComando.optString(ID_DEVICE);
                currentOtaVersion = dlgComando.optInt(OTA_VERSION);
                if (!idDispositivo.equals("OtaServer")) {
                    dispositivo = buscarDispositivoPorId(idDispositivo);
                    if (dispositivo != null) {
                        Log.w(LIBRERIA, "analizarCabecera. dispositivo encontrado");
                        return respuesta;
                    } else {
                        Log.w(LIBRERIA, "analizarCabecera. dispositivo no valido");
                        return null;
                    }
                }



            } else {
                Log.w(LIBRERIA, "analizarCabecera. retorno invalido");
                return null;
            }


        }
        Log.w(LIBRERIA, "analizarCabecera. retorno valido");
        return respuesta;

    }

    /**
     * Esta funcion devuelve el estado del rele como mensaje espontaneo
     * @param notificacionStatus
     * @return 0 si esta desactivado y 1 si esta activado.
     * Se asume que previamente se sabe que es una notificacion porque se ha consultado a
     * la funcion esRespuesta.
     */
    public int procesarStatus(JSONObject comandoStatus) {

        JSONObject respuesta;
        JSONObject dlgComando;

        try {
            dlgComando = comandoStatus.getJSONObject(COMANDO);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        respuesta = comandoStatus.optJSONObject(DLG_NOTIFICACION);
        return respuesta.optInt(ESTADO_RELE);

    }




    public void consultarVersionOtaIotOnOff(JSONObject respuesta) {


        JSONObject contenido;
        int codRespuesta = 0;

        try {


            contenido = respuesta.getJSONObject(DLG_RESPUESTA);
            codRespuesta = contenido.getInt(DLG_RESULT_CODE);
            if (codRespuesta == 200) {
                otaServer = contenido.getString(OTA_SERVER);
                otaPort = contenido.getInt(OTA_PORT);
                otaUrl = contenido.getString(OTA_URL);
                otaFile = contenido.getString(OTA_FILE);
                newOtaVersion = contenido.getInt(OTA_VERSION);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }







    }


    /**
     * Esta funcion determina si un mensaje es notificacion o respuesta
     * @param objetoRecibido
     * @return el numero del comando o 0 si es una notificacion.
     */

    public int esRespuesta (JSONObject objetoRecibido) {

        JSONObject respuesta = null;
        JSONObject objetoComando = null;
        int codResp;
        int idComando = -1;

        respuesta = objetoRecibido.optJSONObject(DLG_RESPUESTA);

        if (respuesta != null) {
            codResp = respuesta.optInt(DLG_RESULT_CODE);

            if (codResp == 200) {
                Log.w(LIBRERIA, "Respuesta correcta");
                objetoComando = objetoRecibido.optJSONObject(COMANDO);
                idComando = objetoComando.optInt(DLG_COMANDO);
                return idComando;

            } else if (codResp == 400) {
                Log.w(LIBRERIA, "Respuesta erronea");
                objetoComando = objetoRecibido.optJSONObject(COMANDO);
                idComando = objetoComando.optInt(DLG_COMANDO);
                return idComando;

            } else {
                Log.w(LIBRERIA, "El mensaje es una notificacion");
                return -1;
            }


        }
        return -1;
    }

/*
    public int esRespuesta(JSONObject objetoRecibido) {





        int idComando = -1;
        JSONObject comando;

        comando = objetoRecibido.optJSONObject(COMANDO);

        if (comando == null) {
            return -1; // Es una notificacion
        }

        idComando = comando.optInt(DLG_COMANDO);

        return idComando;




    }
    */
    protected JSONObject generarCabecera() {

        JSONObject cabecera = null;
        comandoJson = new JSONObject();
        cabecera = new JSONObject();
        String texto = null;


        String fecha;

        Calendar date = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        fecha = formato.format(date.getTime());





        try {
            comandoJson.put(COMANDO, cabecera);
            cabecera.put("dlgKey", clave);
            cabecera.put("date", fecha);
            texto = comandoJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comandoJson;
    }


    public String enviarComandoUpgradeFirmware() {

        JSONObject comando;
        JSONObject intermedio;
        JSONObject upgrade;
        String textoComando = null;

        intermedio = new JSONObject();
        upgrade = new JSONObject();

        comando = generarCabecera();

        try {
            intermedio = comando.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, UPGRADE_FIRMWARE );
            upgrade = new JSONObject();
            upgrade.put(OTA_SERVER, otaServer);
            upgrade.put(OTA_PORT, otaPort);
            upgrade.put(OTA_URL, otaUrl);
            upgrade.put(OTA_FILE, otaFile);
            upgrade.put(OTA_VERSION, newOtaVersion);
            comando.put(DLG_UPGRADE_FIRMWARE, upgrade);
            textoComando = comando.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return textoComando;
    }

    public String enviarComandoModificarUmbralTemperatura( double temperatura) {


        JSONObject comando;
        JSONObject intermedio;
        JSONObject parametros;

        comando = generarCabecera();

        try {
            intermedio = comando.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, MODIFICAR_UMBRAL_TEMPERATURA);
            parametros = new JSONObject();
            parametros.put(UMBRAL_TEMPERATURA, temperatura);
            comando.put(MODIFICAR_APP, parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comando.toString();



    }

    public String enviarComandoInfoDispositivo() {


        String textoReset;

        textoReset = enviarComandoGenerico(INFO_DISPOSITIVO);

        return textoReset;

    }

    public String enviarComandoGenerico(int nComando) {

        JSONObject comando;
        JSONObject intermedio;


        intermedio = new JSONObject();

        comando = generarCabecera();
        try {
            intermedio = comando.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, nComando);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return comando.toString();

    }



    public String enviarComandoStatus() {

        JSONObject status;
        JSONObject intermedio;

        intermedio = new JSONObject();

        status = generarCabecera();
        try {
            intermedio = status.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return status.toString();
    }

    public String enviarComandoActuarRele(int accion) {

        JSONObject actuarRele;
        JSONObject intermedio;
        JSONObject comandoOpRele;

        intermedio = new JSONObject();

        actuarRele = generarCabecera();
        try {
            intermedio = actuarRele.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, ACTUAR_RELE);
            comandoOpRele = new JSONObject();
            comandoOpRele.put("opRele", accion);
            actuarRele.put("rele", comandoOpRele);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return actuarRele.toString();
    }

    public String enviarComandoReset() {

        String textoReset;

        textoReset = enviarComandoGenerico(RESET);

        return textoReset;
    }

    public String enviarComandoFactoryReset() {

        String textoReset;

        textoReset = enviarComandoGenerico(FACTORY_RESET);

        return textoReset;
    }

    public String enviarComandoActivarInhibirProgramacion(int flag) {

        String textoComando = null;
        JSONObject parametros;
        JSONObject comand;
        JSONObject intermedio;

        comand = generarCabecera();
        intermedio = comand.optJSONObject(COMANDO);
        try {
            intermedio.put(DLG_COMANDO, MODIFY_CLOCK);
            parametros = new JSONObject();
            parametros.put(ESTADO_PROGRAMACION, flag);
            comand.put(RELOJ, parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return comand.toString();

    }


    public String enviarComandoConsultarProgramacion() {
        String programacion;

        programacion = enviarComandoGenerico(CONSULTAR_PROGRAMACION);


        return programacion;
    }

    public String enviarComandoModificarProgramacion(programacionDispositivo programa, String idPrograma) {

        String textoPrograma = null;
        String dato;
        JSONObject nuevoPrograma;
        JSONObject comando;
        JSONObject intermedio;



        nuevoPrograma = generarCabecera();
        try {
            intermedio = nuevoPrograma.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, MODIFICAR_PROGRAMACION);
            intermedio.put(DLG_NOMBRE_COMANDO, NOMBRE_MODIFICAR_PROGRAMACION);
            comando = new JSONObject();
            comando.put(TIPO_PROGRAMA, programa.tipoPrograma);
            switch (programa.tipoPrograma) {
                case 0:
                    comando.put(ID_PROGRAMA, idPrograma.substring(0,10));
                    comando.put(MASCARA_PROGRAMA, programa.mascara);
                    break;
                case 1:
                    comando.put(DIA_SEMANA_PROGRAMA, programa.diaSemana);
                    break;
                case 2:
                    comando.put(ID_PROGRAMA, idPrograma.substring(0,16));
                    comando.put(ANO_PROGRAMA, programa.ano);
                    comando.put(MES_PROGRAMA, programa.mes);
                    comando.put(DIA_PROGRAMA, programa.dia);
            }

            comando.put(HORA_PROGRAMA, programa.hora);
            comando.put(MINUTO_PROGRAMA, programa.minuto);
            comando.put(SEGUNDO_PROGRAMA, programa.segundo);
            comando.put(ESTADO_PROGRAMA, programa.estadoPrograma);
            comando.put(ESTADO_RELE, programa.estadoRele);
            switch (programa.tipoDispositivo) {

                case 0: //IOT_ON_OFF
                    if (programa.duracion > 0) {
                        comando.put(DURATION_PROGRAM, programa.duracion * 60);
                    }
                    break;

                case 1: //IOT_CRONO_TEMP
                    comando.put(UMBRAL_TEMPERATURA, programa.temperaturaUmbral);
                    break;

                default:
                    Log.w(LIBRERIA, "Error al asignar tipo de dispositivo");
                    break;


            }

            nuevoPrograma.put(PROGRAMA, comando);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nuevoPrograma.toString();


    }


    public String enviarComandoNuevaProgramacion(programacionDispositivo programa) {

        String textoPrograma = null;
        String dato;
        JSONObject nuevoPrograma;
        JSONObject comando;
        JSONObject intermedio;
        String mes;
        String dia;

        // Crear el id de programacion.

        textoPrograma = programa.adaptarDato(programa.tipoPrograma);
        textoPrograma += programa.adaptarDato(programa.hora);
        textoPrograma += programa.adaptarDato(programa.minuto);
        textoPrograma +="00"; //segundos
        if (programa.tipoPrograma == 1) textoPrograma += programa.adaptarDato(programa.diaSemana);
        if (programa.tipoPrograma == 2) {
            if (programa.mes < 10) {
                mes = "0" + String.valueOf(programa.mes);
            } else {
                mes = String.valueOf(programa.mes);
            }

            if (programa.dia < 10) {
                dia = "0" + String.valueOf(programa.dia);
            } else {
                dia = String.valueOf(programa.dia);
            }

            textoPrograma += String.valueOf(programa.ano) + mes + dia;
        }
        nuevoPrograma = generarCabecera();
        try {
            intermedio = nuevoPrograma.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, NUEVA_PROGRAMACION);
            intermedio.put(DLG_NOMBRE_COMANDO, NOMBRE_NUEVA_PROGRAMACION);
            comando = new JSONObject();
            comando.put(ID_PROGRAMA, textoPrograma);
            comando.put(TIPO_PROGRAMA, programa.tipoPrograma);
            comando.put(HORA_PROGRAMA, programa.hora);
            comando.put(MINUTO_PROGRAMA, programa.minuto);
            if (programa.tipoPrograma == 1) comando.put(DIA_SEMANA_PROGRAMA, programa.diaSemana);
            if (programa.tipoPrograma == 2) {
                comando.put(ANO_PROGRAMA, programa.ano);
                comando.put(MES_PROGRAMA, programa.mes);
                comando.put(DIA_PROGRAMA, programa.dia);
            }
            comando.put(ESTADO_PROGRAMA, programa.estadoPrograma);
            comando.put(ESTADO_RELE, programa.estadoRele);
            comando.put(MASCARA_PROGRAMA, programa.mascara);

            switch (programa.tipoDispositivo) {

                case 0: //IOT_ON_OFF
                    if (programa.duracion > 0) {
                        comando.put(DURATION_PROGRAM, programa.duracion * 60);
                    }
                    break;

                case 1: //IOT_CRONO_TEMP
                    comando.put(UMBRAL_TEMPERATURA, programa.temperaturaUmbral);
                    break;

                    default:
                        Log.w(LIBRERIA, "Error al asignar tipo de dispositivo");
                        break;


            }

            nuevoPrograma.put(PROGRAMA, comando);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return nuevoPrograma.toString();


    }

    public String enviarComandoEliminarProgramacion(String idPrograma) {

        JSONObject eliminarProgramacion;
        JSONObject intermedio;
        JSONObject parametrosEliminarProgramacion;

        intermedio = new JSONObject();

        eliminarProgramacion = generarCabecera();
        try {
            intermedio = eliminarProgramacion.getJSONObject(COMANDO);
            intermedio.put(DLG_COMANDO, ELIMINAR_PROGRAMACION);
            intermedio.put(DLG_NOMBRE_COMANDO, NOMBRE_ELIMINAR_PROGRAMACION);
            parametrosEliminarProgramacion = new JSONObject();
            parametrosEliminarProgramacion.put(ID_PROGRAMA, idPrograma);
            Log.w("conexionMqtt", "Se elimina : " + idPrograma);
            eliminarProgramacion.put(PROGRAMA, parametrosEliminarProgramacion);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return eliminarProgramacion.toString();
    }



    public boolean escribirConfiguracion(String fichero, String texto, Context context) {

        OutputStreamWriter escritor = null;


        try {
            escritor = new OutputStreamWriter(context.openFileOutput(fichero, context.MODE_PRIVATE));
            try {
                escritor.write(texto);
                escritor.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }


    protected boolean leerConfiguracion(Context context, String fichero) {



        InputStreamReader flujo;
        BufferedReader lector;
        String linea = null;
        String texto = null;



        try {
            flujo = new InputStreamReader(context.openFileInput(fichero));
            lector = new BufferedReader(flujo);
            linea = lector.readLine();
            while (linea != null) {
                if (texto == null) {
                    texto = linea;
                } else {
                    texto = texto + linea;
                }

                linea = lector.readLine();

            }
            lector.close();
            flujo.close();




            try {


                if (fichero == ficheroMqtt) {
                    datosMqtt = new JSONObject(texto);
                } else {
                    if (fichero == ficheroDispositivos) {
                        datosDispositivos = new JSONObject(texto);
                    } else {
                        //error
                    }
                }
                //datos = new JSONObject(texto);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

            //escribirMensaje("No hay configuracion activa");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        return true;

    }


    protected boolean escribirConfiguracionMqttDefecto(Context context) {

        OutputStreamWriter escritor = null;
        JSONObject confMqtt = null;


        try {
            escritor = new OutputStreamWriter(context.openFileOutput(ficheroMqtt, context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject mqtt = null;
            try {
                mqtt = new JSONObject();
                confMqtt = new JSONObject();
                confMqtt.put(BROKER, "jajicaiot.ddns.net");
                confMqtt.put(PUERTO, "1883");
                confMqtt.put(USUARIO, "");
                confMqtt.put(PASSWORD, "");
                mqtt.putOpt(MQTT, confMqtt);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        try {
            escritor.write(mqtt.toString());
            escritor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return true;
    }


    public JSONObject buscarDispositivoPorId(String idDispositivo) {

        JSONObject objetoBuscado = null;
        objetoBuscado = buscarDispositivoIot(idDispositivo, ID_DISPOSITIVO);

        return objetoBuscado;


    }

    public JSONObject buscarDispositivoPorNombre(String nombreDispositivo) {


        JSONObject objetoBuscado = null;
        objetoBuscado = buscarDispositivoIot(nombreDispositivo, NOMBRE_DISPOSITIVO);

        return objetoBuscado;

    }

    /**
     * Esta funcion busca el dispositivo pasado como parametro para identificarlo en la aplicacion
     * @param idDispositivo es el texto json que identifica al dispositivo
     * @return Se retorna el objeto json encontrado correspondiente a los datos del dispositivo buscado.
     * En caso de no encontrar ninguno devuelve null.
     */
    private JSONObject buscarDispositivoIot(String paramBusqueda, String tipoBusqueda) {

        int i, tamArray=0;
        JSONArray array = null;


        try {
            array = datosDispositivos.getJSONArray(DISPOSITIVOS);
            tamArray = array.length();
            for (i=0;i<tamArray;i++) {
                if (paramBusqueda.equals(array.getJSONObject(i).getString(tipoBusqueda))) {
                    int p = 0;
                    return array.getJSONObject(i);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }






        return null;
    }

}
