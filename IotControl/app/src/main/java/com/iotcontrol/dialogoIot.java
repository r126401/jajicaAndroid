package com.iotcontrol;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

enum TIPO_INFORME {
    RESULTADO_COMANDO,
    INFORME_ESPONTANEO
}


enum ESPONTANEO_IOT {

    ARRANQUE_APLICACION(0),
    ACTUACION_RELE_LOCAL(1),
    ACTUACION_RELE_REMOTO(2),
    UPGRADE_FIRMWARE_FOTA(3),
    CAMBIO_DE_PROGRAMA(4),
    COMANDO_APLICACION(5),
    CAMBIO_TEMPERATURA(6),
    ESPONTANEO_DESCONOCIDO(-1);


    private int idTipoInforme;

    ESPONTANEO_IOT(int tipoInforme) {
        this.idTipoInforme = tipoInforme;
    }

    public int getIdInforme() {
        return this.idTipoInforme;
    }
    public ESPONTANEO_IOT fromId(int id) {


        for (ESPONTANEO_IOT orden : values() ) {

            if (orden.getIdInforme() == id) {

                return orden;
            }

        }
        return null;

    }


    }

enum COMANDO_IOT {
    CONSULTAR_CONF_APP(0), ACTUAR_RELE(50), ESTADO(51),
    CONSULTAR_PROGRAMACION(6), NUEVA_PROGRAMACION(7),
    ELIMINAR_PROGRAMACION(9), MODIFICAR_PROGRAMACION(8), MODIFICAR_APP(12),
    RESET(10), FACTORY_RESET(11), MODIFY_CLOCK(15),
    UPGRADE_FIRMWARE(26), MODIFICAR_UMBRAL_TEMPERATURA(52),
    ESPONTANEO(-1), VERSION_OTA(100), ERROR_RESPUESTA(-100);

    private int idComando;


    COMANDO_IOT(int idComando) {
        this.idComando = idComando;
    }

    public int getIdComando() {

        return this.idComando;
    }

    public COMANDO_IOT fromId(int id) {


        for (COMANDO_IOT orden : values() ) {

            if (orden.getIdComando() == id) {

                return orden;
            }

        }
        return null;

    }




}

enum TEXTOS_DIALOGO_IOT {

    COMANDO("comando"),
    CLAVE("token"),
    DLG_RESPUESTA("dlgRespuesta"),
    DLG_UPGDRADE_FIRMWARE("upgradeFirmware"),
    TIPO_INFORME_ESPONTANEO("tipoReport"),
    CODIGO_RESPUESTA("dlgResultCode"),
    DLG_COMANDO("dlgComando"),
    NOMBRE_COMANDO("nombreComando"),
    ID_DISPOSITIVO("idDevice"),
    NOMBRE_DISPOSITIVO("nombreDispositivo"),
    TIPO_DISPOSITIVO("device"),
    TOPIC_SUBSCRICION("topicSubscripcion"),
    TOPIC_PUBLICACION("topicPublicacion"),
    DISPOSITIVOS("dispositivos"),
    ESTADO_RELE("estadoRele"),
    TEMPERATURA("temperatura"),
    HUMEDAD("humedad"),
    UMBRAL_TEMPERATURA("temperaturaUmbral"),
    MARGEN_TEMPERATURA("margenTemperatura"),
    INTERVALO_LECTURA("intervaloLectura"),
    INTERVALO_REINTENTOS("intervaloReintentos"),
    REINTENTOS_LECTURA("reintentosLectura"),
    VALOR_CALIBRADO("valorCalibrado"),
    RELE("rele"),
    OP_RELE("opRele"),
    ESTADO_CONEXION("estadoConexion"),
    ESTADO_PROGRAMACION("programState"),
    ESTADO_DISPOSITIVO("deviceState"),
    SERVIDOR_OTA("otaServer"),
    PUERTO_OTA("otaPort"),
    URL_OTA("otaUrl"),
    FICHERO_OTA("otaFile"),
    VERSION_OTA("otaVersion"),
    PROGRAMAS("program"),
    ID_PROGRAMA("programId"),
    NUEVO_ID_PROGRAMA("newProgramId"),
    PROGRAMA_ACTIVO("currentProgramId"),
    MODIFICAR_APP("modificarApp"),
    CONFIGURACION_APP("configApp"),
    TIPO_PROGRAMA("programType"),
    HORA("hour"),
    MINUTO("minute"),
    SEGUNDO("second"),
    ANO("year"),
    MES("month"),
    DIA("day"),
    MASCARA_PROGRAMA("programMask"),
    DURACION("durationProgram"),
    DIA_SEMANA("weekDay"),
    CODIGO_OTA("otaCode");



    private String dialogo;

    TEXTOS_DIALOGO_IOT(String respuesta) {

        this.dialogo = respuesta;

    }

    public String getValorTextoJson() {

        return this.dialogo;
    }


}






public class dialogoIot implements Serializable {

    String clave;
    //private TemporizacionComandos temporizador;
    private conexionMqtt cnx;
    ArrayList<TemporizacionComandos> temporizadores;
    private dialogoIot.onDialogoIot listener;

    public interface onDialogoIot {

        void temporizacionVencidaEnComando(String idDispositivo, String clave, COMANDO_IOT comando);

    }




    public dialogoIot() {
        super();

        cnx = null;
        temporizadores = null;
        clave = UUID.randomUUID().toString();

    }
    public dialogoIot(conexionMqtt cnx) {
        super();
        this.cnx = cnx;
        temporizadores = null;


        clave = UUID.randomUUID().toString();

    }

    public void setConexionMqtt(conexionMqtt cnx) {
        this.cnx = cnx;

    }

    public conexionMqtt getConexionMqtt(conexionMqtt cnx) {
        return this.cnx;
    }


    public COMANDO_IOT descubrirComando(String texto) {

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
            e.printStackTrace();
            return COMANDO_IOT.ESPONTANEO;
        }
        return COMANDO_IOT.ESPONTANEO.fromId(idComando);

    }

    /**
     * Funcion que genera la cabecera de cada comando hacia los dispositivos.
     *
     * @return Retorna en formato json la cabecera a utilizar.
     */

    private JSONObject generarCabecera() {

        JSONObject cabecera;
        JSONObject comandoJson = new JSONObject();
        cabecera = new JSONObject();


        String fecha;

        this.clave = UUID.randomUUID().toString();
        Log.i(getClass().toString(), "generarCabecera: Generando clave: " + clave);
        Calendar date = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        fecha = formato.format(date.getTime());


        try {
            comandoJson.put(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson(), cabecera);
            cabecera.put(TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson(), clave);
            cabecera.put("date", fecha);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comandoJson;
    }


    /**
     * Funcion para crear un comando sin parametros.
     *
     * @param nComando es el comando a enviar.
     * @return retorna la cadena json del comando para enviar al dispositivo.
     */
    private String escribirComandoGenerico(COMANDO_IOT nComando) {

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

    public String escribirComandoInfoApp() {

        return escribirComandoGenerico(COMANDO_IOT.CONSULTAR_CONF_APP);
    }


    public String comandoEstadoDispositivo() {

        return escribirComandoGenerico(COMANDO_IOT.ESTADO);
    }

    public TIPO_DISPOSITIVO_IOT getTipoDispositivo(String textoJson) {

        int tipo = -100;
        JSONObject objeto;

        tipo = extraerDatoJsonInt(textoJson, TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
        return TIPO_DISPOSITIVO_IOT.DESCONOCIDO.fromId(tipo);


    }

    /**
     * Esta funcion devuelve un objeto json con la parte del dialogo del dispositivo
     *
     * @param texto es el contenido del mensaje
     * @param parte COMANDO o DLG_respuesta
     * @return devuelve pa parte del mensaje o null si no se encuentra o es erroneo
     */
    public JSONObject obtenerparteJson(String texto, TEXTOS_DIALOGO_IOT parte) {

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
            Log.e(getClass().toString(), "No se encuentra la parte del json: " + parte.toString());
        }

        return devuelto;

    }


    public ESTADO_RELE getEstadoRele(String texto) {

        JSONObject respuesta;
        int estado;
        ESTADO_RELE estadoRele = ESTADO_RELE.INDETERMINADO;

        estado = extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        if (estado == -1000) {
            return estadoRele.INDETERMINADO;
        } else {
            return estadoRele.INDETERMINADO.fromId(estado);
        }
    }


    public String getIdDispositivo(String texto) {

        JSONObject dato = null;
        String idDispositivo;

        return extraerDatoJsonString(texto,TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson() );



    }

    /**
     * Esta funcion extrae la temperatura del json
     *
     * @param texto
     * @return el valor de la temperatura o null si no se consigue extraer.
     */
    public double getTemperatura(String texto) {


        return extraerDatoJsonDouble(texto, TEXTOS_DIALOGO_IOT.TEMPERATURA.getValorTextoJson());

    }

    public double getHumedad(String texto) {

        return extraerDatoJsonDouble(texto, TEXTOS_DIALOGO_IOT.HUMEDAD.getValorTextoJson());


    }

    public int getEstadoProgramacion(String texto) {

        int estadoProg;

        estadoProg = extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson());

        return estadoProg;
    }

    public double getUmbralTemperatura(String texto) {

        return extraerDatoJsonDouble(texto, TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson());

    }

    public ESTADO_DISPOSITIVO getEstadoDispositivo(String texto) {

        ESTADO_DISPOSITIVO estado = ESTADO_DISPOSITIVO.INDETERMINADO;
        int est;

        return estado.fromId(extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.ESTADO_DISPOSITIVO.getValorTextoJson()));



    }


    public String extraerDatoJsonString(String texto, String parametro) {

        JSONObject objetoJson = null;
        String dato = null;

        objetoJson = obtenerparteJson(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getString(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = obtenerparteJson(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
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

    public int extraerDatoJsonInt(String texto, String parametro) {
        JSONObject objetoJson = null;
        int dato = -1000;

        objetoJson = obtenerparteJson(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getInt(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = obtenerparteJson(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
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

    public double extraerDatoJsonDouble(String texto, String parametro) {
        JSONObject objetoJson = null;
        double dato = -1000;

        objetoJson = obtenerparteJson(texto, TEXTOS_DIALOGO_IOT.COMANDO);
        if (objetoJson != null) {

            try {
                dato = objetoJson.getDouble(parametro);
                return dato;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(getClass().toString(), "No se ha encontrado el parametro en la parte " + TEXTOS_DIALOGO_IOT.COMANDO.toString());
            }
        }
        objetoJson = obtenerparteJson(texto, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
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


    public String comandoActuarRele(ESTADO_RELE estado) {

        JSONObject objeto = null;
        JSONObject parametros;
        JSONObject parametroOpRele;
        String comando;

        comando = escribirComandoGenerico(COMANDO_IOT.ACTUAR_RELE);

        try {
            objeto = new JSONObject(comando);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "Error al analizar el comando generico");
        }

        parametros = new JSONObject();
        parametroOpRele = new JSONObject();
        try {
            parametroOpRele.put(TEXTOS_DIALOGO_IOT.OP_RELE.getValorTextoJson(), estado.getEstadoRele());
            objeto.put(TEXTOS_DIALOGO_IOT.RELE.getValorTextoJson(), parametroOpRele);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return objeto.toString();

    }

    public String escribirComandoConsultarProgramacion() {
        String programacion;

        programacion = escribirComandoGenerico(COMANDO_IOT.CONSULTAR_PROGRAMACION);


        return programacion;
    }


    public String escribirComandoVersionOtaDisponible(TIPO_DISPOSITIVO_IOT tipo) {

        JSONObject upgrade = null;
        JSONObject otaVersion;

        try {
            upgrade = new JSONObject();
            otaVersion = new JSONObject();
            otaVersion.put("otaVersiontype", tipo.toString());
            upgrade.put("upgradeFirmware", otaVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.w(getClass().toString(), "Se envia: " + upgrade.toString());

        return upgrade.toString();


    }

    /**
     * Extrae la version OTA del dispositivo
     *
     * @param texto es la respuesta del dispositivo
     * @return devuelve el valor de la version OTA.
     */
    public String getOtaVersion(String texto) {

        return extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.VERSION_OTA.getValorTextoJson());
    }


    public String getProgramaActivo(String texto) {

        return extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.PROGRAMA_ACTIVO.getValorTextoJson());
    }


    public String comandoModificarUmbralTemperatura(double temperatura) {


        JSONObject comando;
        JSONObject intermedio;
        JSONObject parametros;

        comando = generarCabecera();

        try {
            intermedio = comando.getJSONObject(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            intermedio.put(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson(), COMANDO_IOT.MODIFICAR_UMBRAL_TEMPERATURA.getIdComando());
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson(), temperatura);
            comando.put(TEXTOS_DIALOGO_IOT.MODIFICAR_APP.getValorTextoJson(), parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comando.toString();


    }

    public boolean isRespuestaCorrecta(String texto) {

        int respuesta;
        respuesta = extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.CODIGO_RESPUESTA.getValorTextoJson());

        if (respuesta == 200) {
            return true;
        } else {
            return false;
        }
    }


    public String comandoInhibirProgramacion(ProgramaDispositivoIotOnOff programa) {

        return comandoModificarPrograma(programa);

    }

    public String comandoInhibirProgramacion(ProgramaDispositivoIotTermostato programa) {

        return comandoModificarPrograma(programa);

    }

    public String comandoEliminarProgramacion(String idPrograma) {

        String parteComando;
        JSONObject parametros;
        JSONObject comando;


        parteComando = escribirComandoGenerico(COMANDO_IOT.ELIMINAR_PROGRAMACION);
        try {
            comando = new JSONObject(parteComando);
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), idPrograma);
            comando.put(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parametros);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


        return comando.toString();
    }

    public String comandoNuevoPrograma(ProgramaDispositivoIotTermostato programa) {

        JSONObject comando = null;
        JSONObject parametros;
        String textoComando;
        ProgramaDispositivoIot programacion;

        programacion = programa;

        textoComando = comandoNuevoPrograma(programacion);
        try {
            comando = new JSONObject(textoComando);
            parametros = comando.getJSONObject(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            parametros.put(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson(), programa.getUmbralTemperatura());
            parametros.put(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson(), ESTADO_RELE.ON.getEstadoRele());
            comando.remove(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            comando.put(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comando.toString();

    }

    public String comandoNuevoPrograma(ProgramaDispositivoIotOnOff programa) {

        JSONObject comando = null;
        JSONObject intermedio;
        JSONObject parametros;
        String parteComando;

        parteComando = escribirComandoGenerico(COMANDO_IOT.NUEVA_PROGRAMACION);

        try {
            comando = new JSONObject(parteComando);
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.TIPO_PROGRAMA.getValorTextoJson(), programa.getTipoPrograma().getTipoPrograma());
            parametros.put(TEXTOS_DIALOGO_IOT.HORA.getValorTextoJson(), programa.getHora());
            parametros.put(TEXTOS_DIALOGO_IOT.MINUTO.getValorTextoJson(), programa.getMinuto());
            parametros.put(TEXTOS_DIALOGO_IOT.SEGUNDO.getValorTextoJson(), programa.getSegundo());
            parametros.put(TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson(), programa.getEstadoPrograma().getEstadoPrograma());
            parametros.put(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson(), programa.getEstadoRele().getEstadoRele());
            if (programa.getDuracion() > 0) {
                parametros.put(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson(), programa.getDuracion());
            }


            switch (programa.tipoPrograma) {
                case PROGRAMA_DIARIO:
                    parametros.put(TEXTOS_DIALOGO_IOT.MASCARA_PROGRAMA.getValorTextoJson(), programa.getMascara());
                    break;
                case PROGRAMA_FECHADO:
                    parametros.put(TEXTOS_DIALOGO_IOT.DIA.getValorTextoJson(), programa.getDia());
                    parametros.put(TEXTOS_DIALOGO_IOT.MES.getValorTextoJson(), programa.getMes());
                    parametros.put(TEXTOS_DIALOGO_IOT.ANO.getValorTextoJson(), programa.getAno());
                    break;
                case PROGRAMA_SEMANAL:
                    parametros.put(TEXTOS_DIALOGO_IOT.DIA_SEMANA.getValorTextoJson(), programa.getDiaSemana());
                    break;
                case PROGRAMA_DESCONOCIDO:
                    break;
            }

            comando.put(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parametros);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comando.toString();

    }




    private JSONObject construirPrograma(ProgramaDispositivoIot programa, COMANDO_IOT tipoComando) {

        JSONObject comando = null;
        JSONObject intermedio;
        JSONObject parametros;
        String parteComando;

        parteComando = escribirComandoGenerico(tipoComando);

        try {
            comando = new JSONObject(parteComando);
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.TIPO_PROGRAMA.getValorTextoJson(), programa.getTipoPrograma().getTipoPrograma());
            parametros.put(TEXTOS_DIALOGO_IOT.HORA.getValorTextoJson(), programa.getHora());
            parametros.put(TEXTOS_DIALOGO_IOT.MINUTO.getValorTextoJson(), programa.getMinuto());
            parametros.put(TEXTOS_DIALOGO_IOT.SEGUNDO.getValorTextoJson(), programa.getSegundo());
            parametros.put(TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson(), programa.getEstadoPrograma().getEstadoPrograma());

            switch (programa.tipoPrograma) {
                case PROGRAMA_DIARIO:
                    parametros.put(TEXTOS_DIALOGO_IOT.MASCARA_PROGRAMA.getValorTextoJson(), programa.getMascara());
                    break;
                case PROGRAMA_FECHADO:
                    parametros.put(TEXTOS_DIALOGO_IOT.DIA.getValorTextoJson(), programa.getDia());
                    parametros.put(TEXTOS_DIALOGO_IOT.MES.getValorTextoJson(), programa.getMes());
                    parametros.put(TEXTOS_DIALOGO_IOT.ANO.getValorTextoJson(), programa.getAno());
                    break;
                case PROGRAMA_SEMANAL:
                    parametros.put(TEXTOS_DIALOGO_IOT.DIA_SEMANA.getValorTextoJson(), programa.getDiaSemana());
                    break;
                case PROGRAMA_DESCONOCIDO:
                    break;
            }

            comando.put(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parametros);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comando;


    }


    public String comandoNuevoPrograma(ProgramaDispositivoIot programa) {

        JSONObject comando = null;
        JSONObject intermedio;
        JSONObject parametros;
        String parteComando;

        parteComando = escribirComandoGenerico(COMANDO_IOT.NUEVA_PROGRAMACION);

        try {
            comando = new JSONObject(parteComando);
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.TIPO_PROGRAMA.getValorTextoJson(), programa.getTipoPrograma().getTipoPrograma());
            parametros.put(TEXTOS_DIALOGO_IOT.HORA.getValorTextoJson(), programa.getHora());
            parametros.put(TEXTOS_DIALOGO_IOT.MINUTO.getValorTextoJson(), programa.getMinuto());
            parametros.put(TEXTOS_DIALOGO_IOT.SEGUNDO.getValorTextoJson(), programa.getSegundo());
            parametros.put(TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson(), programa.getEstadoPrograma().getEstadoPrograma());

            switch (programa.tipoPrograma) {
                case PROGRAMA_DIARIO:
                    parametros.put(TEXTOS_DIALOGO_IOT.MASCARA_PROGRAMA.getValorTextoJson(), programa.getMascara());
                    break;
                case PROGRAMA_FECHADO:
                    parametros.put(TEXTOS_DIALOGO_IOT.DIA.getValorTextoJson(), programa.getDia());
                    parametros.put(TEXTOS_DIALOGO_IOT.MES.getValorTextoJson(), programa.getMes());
                    parametros.put(TEXTOS_DIALOGO_IOT.ANO.getValorTextoJson(), programa.getAno());
                    break;
                case PROGRAMA_SEMANAL:
                    parametros.put(TEXTOS_DIALOGO_IOT.DIA_SEMANA.getValorTextoJson(), programa.getDiaSemana());
                    break;
                case PROGRAMA_DESCONOCIDO:
                    break;
            }

            comando.put(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), parametros);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return comando.toString();

    }



    public JSONObject comandoModificarPrograma(ProgramaDispositivoIot programa) {


        JSONObject comando = null;
        JSONObject parametroProgram;
        comando = construirPrograma(programa, COMANDO_IOT.MODIFICAR_PROGRAMACION);
        try {
            parametroProgram = comando.getJSONObject(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), programa.idProgramacion);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "No se ha encontrado los parametros del programa");
            return null;
        }





        return comando;
    }

    public String comandoModificarPrograma(ProgramaDispositivoIotOnOff programa) {

        JSONObject comando = null;
        JSONObject parametroProgram;
        comando = construirPrograma(programa, COMANDO_IOT.MODIFICAR_PROGRAMACION);
        try {
            parametroProgram = comando.getJSONObject(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), programa.getIdProgramacion());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson(), programa.getEstadoRele().getEstadoRele());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson(), programa.getEstadoPrograma().getEstadoPrograma());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson(), programa.getDuracion());
            //if (programa.getDuracion() > 0) parametroProgram.put(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson(), programa.getDuracion());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "No se ha encontrado los parametros del programa");
            return null;
        }





        return comando.toString();

    }

    public String comandoModificarPrograma(ProgramaDispositivoIotTermostato programa) {

        JSONObject comando = null;
        JSONObject parametroProgram;
        comando = construirPrograma(programa, COMANDO_IOT.MODIFICAR_PROGRAMACION);
        try {
            parametroProgram = comando.getJSONObject(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), programa.getIdProgramacion());
            parametroProgram.put(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson(), programa.getUmbralTemperatura());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "No se ha encontrado los parametros del programa");
            return null;
        }





        return comando.toString();

    }

    /**
     * Esta funcion configura los parametros internos del crontotermostato
     * @param dispositivo
     * @return
     */
    public String configurarTermostato(dispositivoIotTermostato dispositivo) {

        JSONObject comando = null;
        JSONObject intermedio;
        JSONObject parametros;
        String parteComando;

        parteComando = escribirComandoGenerico(COMANDO_IOT.MODIFICAR_APP);
        try {
            comando = new JSONObject(parteComando);
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson(), dispositivo.getMargenTemperatura());
            parametros.put(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson(), dispositivo.getReintentosLectura());
            parametros.put(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson(), dispositivo.getIntervaloReintentos());
            parametros.put(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson(), dispositivo.getIntervaloLectura());
            comando.put(TEXTOS_DIALOGO_IOT.CONFIGURACION_APP.getValorTextoJson(), parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return null;
    }



    public String escribirComandoEstadoConfiguracionTermostato() {


        return escribirComandoGenerico(COMANDO_IOT.CONSULTAR_CONF_APP);


    }

    public String escribirComandoReset() {

        return escribirComandoGenerico(COMANDO_IOT.RESET);
    }

    public String escribirComandoFactoryReset() {
        return escribirComandoGenerico(COMANDO_IOT.FACTORY_RESET);
    }

    public String escribirComandoModficarParametrosApp(dispositivoIotTermostato dispositivo) {

        JSONObject comando;
        JSONObject intermedio;
        JSONObject parametros;

        comando = generarCabecera();

        try {
            intermedio = comando.getJSONObject(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            intermedio.put(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson(), COMANDO_IOT.MODIFICAR_APP.getIdComando());
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson(), dispositivo.getIntervaloLectura());
            parametros.put(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson(), dispositivo.getIntervaloReintentos());
            parametros.put(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson(), dispositivo.getMargenTemperatura());
            parametros.put(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson(), dispositivo.getReintentosLectura());
            parametros.put(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson(), dispositivo.getValorCalibrado());
            comando.put(TEXTOS_DIALOGO_IOT.CONFIGURACION_APP.getValorTextoJson(), parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comando.toString();

    }



    public String escribirComando (COMANDO_IOT comando, float umbralTemperatura) {

        String textoComando = null;
        switch (comando) {
            case MODIFICAR_UMBRAL_TEMPERATURA:
                textoComando = comandoModificarUmbralTemperatura(umbralTemperatura);
                break;

        }
        return textoComando;
    }
    public String escribirComando (COMANDO_IOT comando, dispositivoIotTermostato dispositivo ) {

        return configurarTermostato(dispositivo);
    }
    public String escribirComando(COMANDO_IOT comando, ESTADO_RELE estado) {

        return comandoActuarRele(estado);
    }
    public String escribirComando(COMANDO_IOT comando, ProgramaDispositivoIotTermostato programa) {

        String textoComando = null;
        switch (comando) {
            case NUEVA_PROGRAMACION:
                textoComando = comandoNuevoPrograma(programa);
                break;
            case MODIFICAR_PROGRAMACION:
                textoComando = comandoModificarPrograma(programa);
                break;
            default:
                Log.e(getClass().toString(), "Comando no implementado en esta funcion");
                break;

        }
        return textoComando;

    }

    public String escribirComando(COMANDO_IOT comando) {

        String textoComando = null;

        switch (comando) {

            case CONSULTAR_CONF_APP:
                textoComando = escribirComandoEstadoConfiguracionTermostato();
                break;
            case ACTUAR_RELE:
                break;
            case ESTADO:
                textoComando = comandoEstadoDispositivo();
                break;
            case CONSULTAR_PROGRAMACION:
                textoComando = escribirComandoConsultarProgramacion();
                break;
            case NUEVA_PROGRAMACION:
                break;
            case ELIMINAR_PROGRAMACION:
                break;
            case MODIFICAR_PROGRAMACION:
                break;
            case MODIFICAR_APP:
                break;
            case RESET:
                textoComando = escribirComandoReset();
                break;
            case FACTORY_RESET:
                textoComando = escribirComandoFactoryReset();
                break;
            case MODIFY_CLOCK:
                break;
            case UPGRADE_FIRMWARE:
                break;
            case MODIFICAR_UMBRAL_TEMPERATURA:
                break;
            case VERSION_OTA:
                break;
                default:
                    Log.e(getClass().toString(), "Error, comando no implementado");
                    break;


        }

        return textoComando;


    }

    public String escribirComandoUpgradeFirmware(dispositivoIot dispositivo) {

        JSONObject comando;
        JSONObject intermedio;
        JSONObject parametros;

        comando = generarCabecera();

        try {
            intermedio = comando.getJSONObject(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            intermedio.put(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson(), COMANDO_IOT.UPGRADE_FIRMWARE.getIdComando());
            parametros = new JSONObject();
            parametros.put(TEXTOS_DIALOGO_IOT.SERVIDOR_OTA.getValorTextoJson(), dispositivo.datosOta.getOtaServer());
            parametros.put(TEXTOS_DIALOGO_IOT.PUERTO_OTA.getValorTextoJson(), dispositivo.datosOta.getOtaPort());
            parametros.put(TEXTOS_DIALOGO_IOT.URL_OTA.getValorTextoJson(), dispositivo.datosOta.getOtaUrl());
            parametros.put(TEXTOS_DIALOGO_IOT.FICHERO_OTA.getValorTextoJson(), dispositivo.datosOta.getOtaFile());
            parametros.put(TEXTOS_DIALOGO_IOT.VERSION_OTA.getValorTextoJson(), dispositivo.datosOta.getOtaVersionAvailable());
            comando.put(TEXTOS_DIALOGO_IOT.DLG_UPGDRADE_FIRMWARE.getValorTextoJson(), parametros);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return comando.toString();
    }

    /**
     * Esta funcion implementa el envio fisico del comando sobre el dispositivo. Ademas, controla si el dispositivo responde
     * al comando.
     * @param topic
     * @param comandoJson
     * @param idDispositivo
     */

    public void enviarComando(String topic, String comandoJson, String idDispositivo) {

        TemporizacionComandos temporizador;
        COMANDO_IOT idComando = COMANDO_IOT.ERROR_RESPUESTA;
        int a =  extraerDatoJsonInt(comandoJson, TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson());
        idComando = idComando.fromId(a);

        //idComando = descubrirComando(comandoJson);

        if (temporizadores == null) {
            temporizadores = new ArrayList<TemporizacionComandos>();
            Log.i(getClass().toString(), "enviarComando: Se crea el array");
        }


        temporizador = new TemporizacionComandos(idComando, idDispositivo);

        temporizador.setOnTemporizacionComandos(new TemporizacionComandos.onTemporizacionComandos() {
            @Override
            public void temporizacionVencida(COMANDO_IOT comando, String clave, String idDispositivo) {
                Log.i(getClass().toString(), "enviarComando: Temporizacion vencida con clave " + clave + " y dispositivo " + idDispositivo);
                listener.temporizacionVencidaEnComando(idDispositivo, clave, comando);
                eliminarTemporizador(clave);


            }

            @Override
            public void informeIntermedio(COMANDO_IOT comando) {
                Log.i(getClass().toString(), "enviarComando: mensaje intermedio");

            }
        });

        temporizador.crearTemporizacion(15000, 1000);
        temporizador.setClave(this.clave);
        Log.i(getClass().toString(), "enviarComando " + idComando.toString() + ": Envio comando Temporizador clave " + temporizador.getClave());
        temporizadores.add(temporizador);
        if (cnx == null) {
            Log.e(getClass().toString(), "no hay conexion mqtt:" + idComando.toString());
        } else {
            cnx.publicarTopic( topic, comandoJson);

        }
    }

    public void enviarComando(dispositivoIot dispositivo, String comandoJson) {

        TemporizacionComandos temporizador;
        COMANDO_IOT idComando = COMANDO_IOT.ERROR_RESPUESTA;
        //idComando = descubrirComando(comandoJson);
       int a =  extraerDatoJsonInt(comandoJson, TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson());
       idComando = idComando.fromId(a);
        if (temporizadores == null) {
            temporizadores = new ArrayList<TemporizacionComandos>();
            Log.i(getClass().toString(), "enviarComando: Se crea el array");
        }


        temporizador = new TemporizacionComandos(idComando, dispositivo.getIdDispositivo());

        temporizador.setOnTemporizacionComandos(new TemporizacionComandos.onTemporizacionComandos() {
            @Override
            public void temporizacionVencida(COMANDO_IOT comando, String clave, String idDispositivo) {
                Log.e(getClass().toString(), "enviarComando: Temporizacion vencida con clave " + clave + " y dispositivo " + idDispositivo);
                listener.temporizacionVencidaEnComando(idDispositivo, clave, comando);
                eliminarTemporizador(clave);


            }

            @Override
            public void informeIntermedio(COMANDO_IOT comando) {
                Log.i(getClass().toString(), "enviarComando: mensaje intermedio " + comando.toString());

            }
        });

        temporizador.crearTemporizacion(15000, 1000);
        temporizador.setClave(this.clave);
        Log.i(getClass().toString(), "enviarComando " + idComando.toString() + ": Envio comando Temporizador clave " + temporizador.getClave());
        temporizadores.add(temporizador);
        if (cnx == null) {
            Log.e(getClass().toString(), "no hay conexion mqtt:" + idComando.toString());
        } else {
            cnx.publicarTopic( dispositivo.getTopicPublicacion(), comandoJson);
            dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
        }





    }

    public void eliminarTemporizador(String clave) {

        int i = 0;
        if (temporizadores == null) {
            Log.e(getClass().toString(), "No hay ningun temporizador asociado");
            return;
        }
        Log.i(getClass().toString(), "eliminarTemporizador: clave: " + clave + " tamaño: " + temporizadores.size());

        for (i=0;i<temporizadores.size();i++) {
            if(temporizadores.get(i).getClave().equals(clave)) {
                Log.i(getClass().toString(), "eliminarTemporizador: Temporizador con clave " + temporizadores.get(i).getClave() + " borrado");
                temporizadores.get(i).temporizador.cancel();
                temporizadores.remove(i);
                return;

            }
        }
        Log.e(getClass().toString(), "eliminarTemporizador: No se ha encontrado el Temporizador con clave " + clave + " y valor " + String.valueOf(i));
    }

    public void setOnTemporizacionVencidaEnComando(dialogoIot.onDialogoIot listener) {
        this.listener = listener;
    }


    /**
     * Esta funcion devuelve el tipo de informe espontaneo recibido
     * @param texto
     * @return
     */
    public ESPONTANEO_IOT descubrirTipoInformeEspontaneo(String texto) {

        int tipoInforme;
        tipoInforme = extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.TIPO_INFORME_ESPONTANEO.getValorTextoJson());


        return ESPONTANEO_IOT.ESPONTANEO_DESCONOCIDO.fromId(tipoInforme);
    }

    public OTA_IOT tipoInformeOta(String texto) {

        int tipoInforme;
        tipoInforme = extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.CODIGO_OTA.getValorTextoJson());

        return OTA_IOT.OTA_ERROR.fromId(tipoInforme);
    }
}



