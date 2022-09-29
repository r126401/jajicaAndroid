package jajica;

import org.json.JSONArray;
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

    public ArrayList<DispositivoIot> getDispositivosIot() {
        return dispositivosIot;
    }

    public void setDispositivosIot(ArrayList<DispositivoIot> dispositivosIot) {
        this.dispositivosIot = dispositivosIot;
    }

    /**
     * Esta función se utiliza para cargar la configuracion de dispositivos en la aplicacion.
     * @return Se retorna el estado de la peticion.
     */
    public ESTADO_FICHEROS cargarDispositivos() {

        //leer fichero
        Ficheros configuracion;
        ESTADO_FICHEROS estadoOperacion;
        String texto = null;
        configuracion = new Ficheros();
        estadoOperacion = configuracion.leerFichero(ficheroDispositivos);
        if (estadoOperacion != ESTADO_FICHEROS.FICHERO_OK) {
            return ESTADO_FICHEROS.ERROR_LEER_FICHERO;
        }
        texto = configuracion.getTextoFichero();
        //chequear consistencia del fichero
        JSONObject objeto;
        objeto = new JSONObject(texto);
        if (objeto == null) {
            return ESTADO_FICHEROS.ERROR_LEER_FICHERO;
        }

        //Cargar la configuracion en le estructura de dispositivos
        if (cargarEstructura(objeto) == false) {
            return ESTADO_FICHEROS.ERROR_LEER_FICHERO;
        }



        return ESTADO_FICHEROS.FICHERO_OK;
    }


    /**
     * Esta funcion carga en el ArrayList<DispositivosIot>  la lista de dispositivos a partir del json de dispositivos.
     * @param estructura
     * @return
     */
    private Boolean cargarEstructura(JSONObject estructura) {

        JSONArray array = null;
        JSONObject item = null;
        DispositivoIot dispositivo;
        OPERACION_JSON resultado = OPERACION_JSON.JSON_ERROR;
        int i;
        array = estructura.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        if (array == null) {
            return false;
        }
        for (i=0;i< array.length();i++) {

            item = array.getJSONObject(i);
            if (item != null) {
                dispositivo = new DispositivoIot();
                if (dispositivo.json2DispositivoIot(item) == OPERACION_JSON.JSON_OK) {
                    if (this.dispositivosIot == null) {
                        dispositivosIot = new ArrayList<DispositivoIot>();
                    }
                    dispositivosIot.add(dispositivo);
                }
            }
        }
        return true;
    }

    /**
     * Este método buscará un dispositivo por su nombre en la lista de dispositivos activos
     * @param valor valor de la etiqueta que se busca
     * @return Se retorna el índice al elemento dentro de la estructura. -1 si no se encuentra.
     */
    public int buscarDispositivoPorNombre(String valor) {

        int i;
        String dato;
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
        String dato;
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


    public Boolean insertarDispositivo(DispositivoIot dispositivo) {

        if (dispositivosIot == null) {
            dispositivosIot = new ArrayList<DispositivoIot>();

        }
        dispositivosIot.add(dispositivo);

        return insertarDispositivoJson(dispositivo);
    }

    /**
     * Este metodo inserta un dispositivo dentro del array y mantiene coherencia con el fichero json.
     * @param dispositivo
     * @return
     */
    private Boolean insertarDispositivoJson(DispositivoIot dispositivo) {

        JSONArray array;

        array = datosDispositivos.getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        if (array == null) {
            return false;
        }
        array.put(dispositivo);
        datosDispositivos.remove(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());
        datosDispositivos.put(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson(), array);

        return true;
    }

    private OPERACION_JSON insertarDispositivoJson(JSONObject dispositivoJson) {

        DispositivoIot dispositivo;
        dispositivo = new DispositivoIot();
        dispositivo.dispositivo2Json();
        if (dispositivosIot == null) {
            dispositivosIot = new ArrayList<DispositivoIot>();
        }
        dispositivosIot.add(dispositivo);

        return OPERACION_JSON.JSON_OK;
    }






}
