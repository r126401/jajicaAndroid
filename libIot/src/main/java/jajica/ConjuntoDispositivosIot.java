package jajica;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Esta clase gestiona la lista de dispositivos configurados en una aplicación que gestione los dispositivos
 * conectados a un mismo usuario.
 * El formato de la lista de dispositivos es la siguiente:
 * {"dispositivos":[{"nombreDispositivo":"Interruptor","idDevice":"4417931D0126","topicPublication":"\/sub_4417931D0126","topicSubscripcion":"\/pub_4417931D0126","device":0}]}
 */

public class ConjuntoDispositivosIot {

    protected final String ficheroDispositivos = "/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf";
    private JSONObject datosDispositivos;
    private ArrayList<DispositivoIot> dispositivosIot;

    /**
     * Esta funcion devuelve la lista de dispositivos de la configuracion de dispositivos
     * @return Devuelve la lista de dispositivos actual
     */
    public ArrayList<DispositivoIot> getDispositivosIot() {
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
        estadoOperacion = configuracion.leerFichero(ficheroDispositivos);
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
        JSONObject item;
        DispositivoIot dispositivo;
        int i;
        try {
            array = estructura.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        } catch (JSONException e) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.NINGUN_DISPOSITIVO;
        }

        for (i=0;i< array.length();i++) {

            item = array.getJSONObject(i);
            if (item != null) {
                dispositivo = new DispositivoIot();
                if (dispositivo.json2DispositivoIot(item) == OPERACION_JSON.JSON_OK) {
                    if (this.dispositivosIot == null) {
                        dispositivosIot = new ArrayList<>();
                    }
                    dispositivosIot.add(dispositivo);
                }
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

        DispositivoIot dispositivo;

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
        DispositivoIot dispositivo;

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
    public OPERACION_CONFIGURACION_DISPOSITIVOS insertarDispositivo(DispositivoIot dispositivo) {

        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        if ((op = anadirDispositivo(dispositivo)) != OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            return op;

        }
         actualizarDatosDispositivos(dispositivo);
        return op;
    }

    /**
     * Este metodo inserta en la estructura ArrayList un nuevo dispositivo
     * @param dispositivo es el dispositivo json
     */
    private OPERACION_CONFIGURACION_DISPOSITIVOS anadirDispositivo(DispositivoIot dispositivo) {

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

        DispositivoIot dispositivo;
        dispositivo = new DispositivoIot();
        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        // Se pasa introduce la parte json en el dispositivo
        if (dispositivo.json2DispositivoIot(dispositivoJson) != OPERACION_JSON.JSON_OK) {
            return OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_NO_JSON;
        }


        // Insertamos el dispositivo en la estructura
        if ((op = insertarDispositivo(dispositivo)) != OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
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
        return file.escribirFichero(ficheroDispositivos, texto);

    }


    /**
     * Esta funcion actualiza la cadena json del objeto para hacerla consistente con los objetos DispositivoIot existentes
     * @param dispositivo Es el dispositivo que se va a actualizar en la estructura json
     */
    private void actualizarDatosDispositivos(DispositivoIot dispositivo) {

        JSONArray array;

        if (datosDispositivos == null) {
            datosDispositivos = new JSONObject();
            array = new JSONArray();
            datosDispositivos.put(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson(), array);

        }
        array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
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
        array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        array.remove(i);
        dispositivosIot.remove(i);
        guardarDispositivos(datosDispositivos.toString());
        datosDispositivos.remove(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        cargarDispositivos();

        }

}
