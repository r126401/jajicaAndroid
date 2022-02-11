package com.example.controliot;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;

public  class configuracionDispositivos implements Serializable {

    protected final String ficheroDispositivos = "datosDispositivos.conf";
    private JSONObject datosDispositivos;
    private Context contexto;




    configuracionDispositivos() {

        datosDispositivos = null;


    }
    configuracionDispositivos(Context contexto) {
        this.contexto = contexto;
    }



    /**
     * Lee la configuracion de dispositivos de la aplicacion
     * @param context es el contexto de la aplicacion
     * @return true si es leido con exito.
     */
    public boolean leerDispositivos(Context context) {



        InputStreamReader flujo;
        BufferedReader lector;
        String linea = null;
        String texto = null;



        try {
            flujo = new InputStreamReader(context.openFileInput(ficheroDispositivos));
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
                datosDispositivos = new JSONObject(texto);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
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





    /**
     * Añade un nuevo dispositivo en el fichero de la aplicacion
     * @param jsonDispositivo es el json con la configuracion de los dispositivos
     * @param contexto es el contexto de la aplicacion
     * @return true si se escribe con exito en el dispositivo.
     */
    private boolean escribirDispositivo(JSONObject jsonDispositivo, Context contexto) {

        OutputStreamWriter escritor;
        JSONObject root;
        JSONArray array;
        dispositivoIot dispositivo;
        int i;

        if (leerDispositivos(contexto) == true) {


            try {
                datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson()).put(jsonDispositivo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            //root = new JSONObject();
            array = new JSONArray();

            array.put(jsonDispositivo);
            try {
                datosDispositivos = new JSONObject();
                datosDispositivos.put(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson(), array);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(this.getClass().toString(), "Error al guardar el fichero: razon json");
                return false;
            }

        }


        if ((escribirFichero(datosDispositivos.toString(), contexto)) == true) {

            Log.i(this.getClass().toString(), "fichero de dispositivos guardado");
        } else {

            Log.e(this.getClass().toString(), "Error al guardar el fichero");
            return false;
        }


        return true;


    }

    public JSONObject getDatosDispositivos() {

        return this.datosDispositivos;
    }


    private boolean escribirFichero(String texto, Context context) {

        OutputStreamWriter escritor = null;


        try {
            escritor = new OutputStreamWriter(context.openFileOutput(ficheroDispositivos, context.MODE_PRIVATE));
            try {
                escritor.write(texto);
                escritor.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public dispositivoIot json2Dispositivo(JSONObject dispositivo) {

        dispositivoIot disp;
        String nombreDispositivo = null;
        String idDispositivo = null;
        int tipoDispositivo;
        TIPO_DISPOSITIVO_IOT tipo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;

        disp = new dispositivoIotDesconocido();

        try {

            disp.nombreDispositivo = dispositivo.getString(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson());
            disp.idDispositivo = dispositivo.getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
            tipoDispositivo = dispositivo.getInt(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
            disp.topicSubscripcion = dispositivo.getString(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson());
            disp.topicPublicacion = dispositivo.getString(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson());
            disp.tipoDispositivo = tipo.fromId(tipoDispositivo);





        } catch (JSONException e) {
            e.printStackTrace();
        }

        return disp;


    }


    public JSONObject dispositivo2Json(dispositivoIot dispositivo) {

        JSONObject objetoDispositivo;
        JSONObject root = new JSONObject();


        objetoDispositivo = new JSONObject();
        try {
            objetoDispositivo.put(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson(), dispositivo.nombreDispositivo);
            objetoDispositivo.put(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), dispositivo.idDispositivo);
            objetoDispositivo.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), dispositivo.tipoDispositivo.getValorTipoDispositivo());
            objetoDispositivo.put(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson(), dispositivo.topicPublicacion);
            objetoDispositivo.put(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson(), dispositivo.topicSubscripcion);
            objetoDispositivo.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), dispositivo.tipoDispositivo.getValorTipoDispositivo());

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return objetoDispositivo;

    }

    public boolean guardarDispositivo(dispositivoIot dispositivo, Context contexto) {

        JSONObject objeto;
        int i;

        i = localizarDispositivo(dispositivo, contexto);

        if (i>=0) {
            Log.w(getClass().toString(), "dispositivo ya existente. No se añade.");
            return false;
        }


        objeto = dispositivo2Json(dispositivo);
        if (objeto == null) {
            Log.e(getClass().toString(), "Error al guardar el dispositivo");
            return false;
        } else {

            return (escribirDispositivo(objeto, contexto));
        }

    }


    private int localizarDispositivo(dispositivoIot dispositivo, Context contexto) {

        int i;
        JSONArray array;
        int tamArray;
        String id= null;

        if (datosDispositivos == null) {
            if (leerDispositivos(contexto) == false) {
                Log.e(getClass().toString(), "Error al leer el fichero de configuracion");
                return -1;
            }

        }

        try {
            array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "json de configuracion corrupto");
            return -1;
        }

        tamArray = array.length();
        for (i=0;i<tamArray;i++) {

            try {
                id = array.getJSONObject(i).getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(getClass().toString(), "Error al extraer el idDispositivo del json");
            }

            if(id.equals(dispositivo.idDispositivo)) {

                Log.i(getClass().toString(), "Elemento encontrado con id" + id );
                return i;
            }
        }

        Log.i(getClass().toString(), "dispositivo no localizado");
        return -1;


    }



    public boolean eliminarDispositivo(dispositivoIot dispositivo, Context contexto) {

        int i;
        JSONArray array = null;


        JSONObject objeto;

        i = localizarDispositivo(dispositivo, contexto);

        if (i == -1) {
            Log.e(getClass().toString(), "No se puede eliminar el dispositivo porque no se ha localizado en la configuracion");
            return false;
        } else {

            try {
                array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            array.remove(i);
            escribirFichero(datosDispositivos.toString(), contexto);
            return true;
        }

     }


    public boolean modificarDispositivo(dispositivoIot dispositivo, Context contexto) {

        if (eliminarDispositivo(dispositivo, contexto) == false) {

            Log.e(getClass().toString(), "Error... No se ha podido modificar el dispositivo");
            return false;
        } else {
            Log.i(getClass().toString(), "Dispositivo modificado");

            escribirDispositivo(dispositivo2Json(dispositivo), contexto);

            return true;
        }


    }

    public boolean modificarTipoDispositivo(dispositivoIot dispositivo, TIPO_DISPOSITIVO_IOT tipo, Context contexto) {

        int i;
        JSONObject dato;


        i = localizarDispositivo(dispositivo, contexto);

        if (datosDispositivos == null) {
            if (leerDispositivos(contexto) == false) {
                Log.e(getClass().toString(), "No se ha podido leer la configuracion del dispositivo");
                return false;
            }
        } else {

            try {
                dato = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson()).getJSONObject(i);
                dato.remove(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
                dato.put(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), tipo.getValorTipoDispositivo() );
                escribirFichero(datosDispositivos.toString(), contexto);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }



    public dispositivoIot getDispositivoPorId(String id) {

        int i;

        JSONObject dispositivo;
        JSONArray array;
        dispositivoIot disp;

        if (datosDispositivos == null) {
            if (!leerDispositivos(contexto)) {
                Log.e(getClass().toString(), "No se ha podido leer la configuracion de disositivos");
                return null;

            }
        }
        i = buscarDispositivoPorId(id);

        try {
            array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
            dispositivo = array.getJSONObject(i);
            disp = json2Dispositivo(dispositivo);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }







        return disp;
    }

    public int buscarDispositivoPorId(String idDispositivo) {

        int i;
        JSONArray array;
        int tamArray;
        String id = null;
        try {
            array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "json de configuracion corrupto");
            return -1;
        }
        tamArray = array.length();
        for (i=0;i<tamArray;i++) {

            try {
                id = array.getJSONObject(i).getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(getClass().toString(), "Error al extraer el idDispositivo del json");
            }
            if (id.equals(idDispositivo)) {

                Log.i(getClass().toString(), "indice encontrado: " + i);
                return i;

            }

        }


        return -1;
    }

}
