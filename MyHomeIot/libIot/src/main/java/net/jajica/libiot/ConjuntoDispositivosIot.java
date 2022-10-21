package net.jajica.libiot;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConjuntoDispositivosIot {

    private String ficheroDispositivos = "/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf";
    private JSONObject datosDispositivos;
    private ArrayList<IotDevice> dispositivosIot;
    private Context context;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ConjuntoDispositivosIot(){

        setFicheroDispositivos("datosDispositivos.conf");
        setContext(null);



    }

    public ConjuntoDispositivosIot(String nombreFichero) {

        setFicheroDispositivos(nombreFichero);
        setContext(null);
    }

    public ConjuntoDispositivosIot(Context context) {
        setFicheroDispositivos("datosDispositivos.conf");
        setContext(context);
    }

    public String getFicheroDispositivos() {
        return ficheroDispositivos;
    }

    public void setFicheroDispositivos(String ficheroDispositivos) {
        this.ficheroDispositivos = ficheroDispositivos;
    }

    /**
     * Esta funcion devuelve la lista de dispositivos de la configuracion de dispositivos
     * @return Devuelve la lista de dispositivos actual
     */
    public ArrayList<IotDevice> getDispositivosIot() {
        return dispositivosIot;
    }

    /**
     * Este método devuelve la configuracion de dispositivos en formato json
     * @return Se retorna la configuracion en formato json
     */
    public JSONObject getDatosDispositivos() {
        return datosDispositivos;
    }


    /**
     * Este metodo se utiliza para cargar la configuracion de dispositivos en la aplicacion.
     * @return Se retorna el resultado de la peticion.
     */
    public OPERACION_CONFIGURACION_DISPOSITIVOS cargarDispositivos()  {

        //leer fichero
        Ficheros configuracion;
        ESTADO_FICHEROS estadoOperacion;
        OPERACION_CONFIGURACION_DISPOSITIVOS res;
        String texto;
        configuracion = new Ficheros();
        if (context == null) {
            estadoOperacion = configuracion.leerFichero(ficheroDispositivos);
        } else {
            estadoOperacion = configuracion.leerFichero(context, ficheroDispositivos);
        }

        if (estadoOperacion != ESTADO_FICHEROS.FICHERO_OK) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.NINGUN_DISPOSITIVO;
        }
        texto = configuracion.getTextoFichero();
        //chequear consistencia del fichero
        JSONObject objeto;
        if (texto == null) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_CORRUPTA;
        }
        try {
            objeto = new JSONObject(texto);
        } catch(JSONException e) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_NO_JSON;
        }


        //Cargar la configuracion en le estructura de dispositivos
        res = cargarEstructura(objeto);
        if (res != OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_OK) {

            return res;
        }

        datosDispositivos = objeto;


        return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_OK;
    }


    /**
     * Esta funcion carga en el ArrayList<DispositivosIot>  la lista de dispositivos a partir del json de dispositivos.
     * @param estructura Es la lista de dispositivos en formato json
     * @return Se retorna el estado de la operación
     */
    private OPERACION_CONFIGURACION_DISPOSITIVOS cargarEstructura(JSONObject estructura) {

        JSONArray array;
        JSONObject item = null;
        IotDevice dispositivo;
        int i;
        try {
            array = estructura.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        } catch (JSONException e) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.NINGUN_DISPOSITIVO;
        }

        if (datosDispositivos != null) {
            datosDispositivos = null;
        }

        if (dispositivosIot != null) {
            dispositivosIot.removeAll(dispositivosIot);
        }

        for (i=0;i< array.length();i++) {

            try {
                item = array.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dispositivo = new IotDevice();
            if (dispositivo.json2DispositivoIot(item) == OPERACION_JSON.JSON_OK) {
                if (this.dispositivosIot == null) {
                    dispositivosIot = new ArrayList<>();
                }
                dispositivosIot.add(dispositivo);
            }

        }
        return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_OK;
    }

    /**
     * Este método buscará un dispositivo por su nombre en la lista de dispositivos activos
     * @param valor valor de la etiqueta que se busca
     * @return Se retorna el índice al elemento dentro de la estructura. -1 si no se encuentra.
     */
    public int buscarDispositivoPorNombre(String valor) {

        int i;

        IotDevice dispositivo;

        if (dispositivosIot != null) {

            for (i=0;i<dispositivosIot.size();i++) {
                dispositivo = dispositivosIot.get(i);
                if (dispositivo.nombreDispositivo.equals(valor)) {
                    //dispositivo encontrado
                    return i;
                }
            }

        }


        return -1;

    }

    /**
     * Este método buscará un dispositivo por su nombre en la lista de dispositivos activos
     * @param valor valor de la etiqueta que se busca
     * @return Se retorna el índice al elemento dentro de la estructura. -1 si no se encuentra.
     */
    public int buscarDispositivoPorId(String valor) {

        int i;
        IotDevice dispositivo;

        if (dispositivosIot != null) {

            for (i=0;i<dispositivosIot.size();i++) {
                dispositivo = dispositivosIot.get(i);
                if (dispositivo.idDispositivo.equals(valor)) {
                    //dispositivo encontrado
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * Este metodo inserta un nuevo dispositivoIot
     * @param dispositivo Es el dispositivo iot
     * @return true si se ha insertado con exito
     */
    private OPERACION_CONFIGURACION_DISPOSITIVOS nuevoDispositivo(IotDevice dispositivo) {

        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        if ((op = anadirDispositivoAlArray(dispositivo)) != OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            return op;

        }
         actualizarDatosDispositivos(dispositivo);


        return op;
    }

    public OPERACION_CONFIGURACION_DISPOSITIVOS insertarDispositivo(IotDevice dispositivo) {

        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        if (dispositivo == null) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_NULO;
        }
        if ((op = nuevoDispositivo(dispositivo)) != OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            return op;
        }
        dispositivo.dispositivo2Json();
        if (guardarDispositivos(datosDispositivos.toString()) != ESTADO_FICHEROS.FICHERO_OK) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_CORRUPTA;
        }
        return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO;
    }

    /**
     * Este metodo inserta en la estructura ArrayList un nuevo dispositivo
     * @param dispositivo es el dispositivo json
     */
    private OPERACION_CONFIGURACION_DISPOSITIVOS anadirDispositivoAlArray(IotDevice dispositivo) {

        if (buscarDispositivoPorId(dispositivo.getIdDispositivo()) >= 0) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_EXISTENTE;
        }

        if(buscarDispositivoPorNombre(dispositivo.getNombreDispositivo()) >= 0) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_EXISTENTE;
        }

        if (!dispositivo.dispositivoValido()) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_CORRUPTA;
        }

        if (dispositivosIot == null) {
            dispositivosIot = new ArrayList<>();

        }
        dispositivosIot.add(dispositivo);
        return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO;

    }


    /**
     * Este metodo inserta un dispositivo a partir de un objeto JSON válido correspondiente a un dispositivoIot
     * @param dispositivoJson es el dispositivo
     * @return Retorna el valor de la operación.
     */
    public OPERACION_CONFIGURACION_DISPOSITIVOS insertarDispositivoDesdeJson(JSONObject dispositivoJson) {

        IotDevice dispositivo;
        dispositivo = new IotDevice();
        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        // Se pasa introduce la parte json en el dispositivo
        if (dispositivo.json2DispositivoIot(dispositivoJson) != OPERACION_JSON.JSON_OK) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_NO_JSON;
        }


        // Insertamos el dispositivo en la estructura
        if ((op = nuevoDispositivo(dispositivo)) != OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            return op;
        }
        if (guardarDispositivos(datosDispositivos.toString()) != ESTADO_FICHEROS.FICHERO_OK) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_CORRUPTA;
        }




        return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO;
    }


    /**
     * Esta función insertará un dispositivo en la estructura desde un fichero json
     * @param texto Es el texto que contiene el disositivo en formato json
     * @return Se retorna el resultado del analisis json del dispositivo
     */
    public OPERACION_CONFIGURACION_DISPOSITIVOS insertarDispositivoDesdeTexto(String texto) {

        JSONObject objeto;
        // Chequeamos que la estructura es json
        try {
            objeto = new JSONObject(texto);
        } catch(JSONException e) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_NO_JSON;
        }

        return insertarDispositivoDesdeJson(objeto);
    }

    /**
     * Guarda en el fichero de configuracion la configuracion de los dispositivos de la aplicacion.
     * @param texto Es el texto con la configuracion de dispositivos en formato json
     * @return Se retorna el estado de la escritura en disco de la configuracion
     */
    private ESTADO_FICHEROS guardarDispositivos(String texto) {

        Ficheros file;
        file = new Ficheros();
        if (context == null) {
            return file.escribirFichero(ficheroDispositivos, texto);
        } else {
            return file.escribirFichero(getFicheroDispositivos(), texto, context);
        }
    }


    /**
     * Esta funcion actualiza la cadena json del objeto para hacerla consistente con los objetos IotDevice existentes
     * @param dispositivo Es el dispositivo que se va a actualizar en la estructura json
     */
    private void actualizarDatosDispositivos(IotDevice dispositivo) {

        JSONArray array = null;

        if (datosDispositivos == null) {
            datosDispositivos = new JSONObject();
            array = new JSONArray();
            try {
                datosDispositivos.put(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson(), array);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

        }
        try {
            array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        array.put(dispositivo.getDispositivoJson());


    }

    /**
     * Este metodo devuelve el numero de dispositivos configurados.
     * @return Devuelve el número de dispositivos en la estructura
     */
    public int getNumeroDispositivos() {

        JSONArray array;
        if (datosDispositivos != null) {
            try {
                array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
            } catch (JSONException e) {
                return 0;
            }
            return array.length();
        }
        return 0;
    }

    /**
     * Este metodo elimina un dispositivo de la configuracion cualificandolo por el id
     * @param idDispositivo Es el id del dispositivo
     * @return retorna el resultado de la operacion.
     */
    public OPERACION_CONFIGURACION_DISPOSITIVOS eliminarDispositivoPorId(String idDispositivo) {

        int i;
        if ((i = buscarDispositivoPorId(idDispositivo)) >= 0) {
            eliminar(i);
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_ELIMINADO;
        }
        return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_NO_ENCONTRADO;
    }

    /**
     * Este método elimina un dispositivo de la configuracion cualificandolo por el nombre
     * @param nombreDispositivo Es el nombre del dispositivo
     * @return retorna el resultado de la operación
     */
    public OPERACION_CONFIGURACION_DISPOSITIVOS eliminarDispositivoPorNombre(String nombreDispositivo) {

        int i;
        if ((i = buscarDispositivoPorNombre(nombreDispositivo)) >= 0) {
            eliminar(i);
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_ELIMINADO;
        }
        return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_NO_ENCONTRADO;
    }

    private void eliminar(int i) {

        JSONArray array;
        try {
            array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        array.remove(i);
        dispositivosIot.remove(i);
        guardarDispositivos(datosDispositivos.toString());
        datosDispositivos.remove(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        cargarDispositivos();

        }

        private OPERACION_CONFIGURACION_DISPOSITIVOS modificarDispositivo(int i, IotDevice dispositivo) {


            JSONArray array;
            try {
                array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
            } catch (JSONException e) {
                e.printStackTrace();
                return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_CORRUPTA;
            }
            array.remove(i);
            dispositivosIot.remove(i);
            array.put(dispositivo.dispositivo2Json());
            guardarDispositivos(datosDispositivos.toString());
            datosDispositivos.remove(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
            cargarDispositivos();

            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_MODIFICADO;
        }

    /**
     * Este método modifica un dispositivo buscandolo por el nombre
     * @param nombreDispositivo es el nombre del dispositivo
     * @param dispositivo Es el dispositivo a modificar
     * @return Se retorna el resultado de la operacion
     */
        public OPERACION_CONFIGURACION_DISPOSITIVOS modificarDispositivoPorNombre(String nombreDispositivo, IotDevice dispositivo) {

            int i;
            if ((i = buscarDispositivoPorNombre(nombreDispositivo)) >= 0) {
                modificarDispositivo(i, dispositivo);
                return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_MODIFICADO;
            }
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_NO_ENCONTRADO;


        }


    /**
     * Este métodoi modifica un dispositivo buscandolo por el id
     * @param idDispositivo es el id del dispositivo
     * @param dispositivo es el dispositivo que se va a modificar
     * @return se devuelve el resultado de la operacion
     */
        public OPERACION_CONFIGURACION_DISPOSITIVOS modificarDispositivoporId(String idDispositivo, IotDevice dispositivo) {

            int i;
            if ((i = buscarDispositivoPorId(idDispositivo)) >= 0) {
                modificarDispositivo(i, dispositivo);
                return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_MODIFICADO;
            }
            return OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_NO_ENCONTRADO;


        }

}
