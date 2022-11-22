package net.jajica.libiot;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class IotSetDevices {

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

    public IotSetDevices(){

        setFicheroDispositivos("datosDispositivos.conf");
        setContext(null);



    }

    public IotSetDevices(String nombreFichero) {

        setFicheroDispositivos(nombreFichero);
        setContext(null);
    }

    public IotSetDevices(Context context) {
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
    public IOT_OPERATION_CONFIGURATION_DEVICES loadIotDevices()  {

        //leer fichero
        Ficheros configuracion;
        ESTADO_FICHEROS estadoOperacion;
        IOT_OPERATION_CONFIGURATION_DEVICES res;
        String texto;
        configuracion = new Ficheros();
        if (context == null) {
            estadoOperacion = configuracion.leerFichero(ficheroDispositivos);
        } else {
            estadoOperacion = configuracion.leerFichero(context, ficheroDispositivos);
        }

        if (estadoOperacion != ESTADO_FICHEROS.FICHERO_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NONE;
        }
        texto = configuracion.getTextoFichero();
        //chequear consistencia del fichero
        JSONObject objeto;
        if (texto == null) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }
        try {
            objeto = new JSONObject(texto);
        } catch(JSONException e) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.NO_JSON_CONFIGURATION;
        }


        //Cargar la configuracion en le estructura de dispositivos
        res = cargarEstructura(objeto);
        if (res != IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION) {

            return res;
        }

        datosDispositivos = objeto;


        return IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION;
    }


    /**
     * Esta funcion carga en el ArrayList<DispositivosIot>  la lista de dispositivos a partir del json de dispositivos.
     * @param estructura Es la lista de dispositivos en formato json
     * @return Se retorna el estado de la operación
     */
    private IOT_OPERATION_CONFIGURATION_DEVICES cargarEstructura(JSONObject estructura) {

        JSONArray array;
        JSONObject item = null;
        IotDevice dispositivo;
        int i;
        try {
            array = estructura.getJSONArray(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        } catch (JSONException e) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NONE;
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
            dispositivo = new IotDeviceUnknown();
            if (dispositivo.json2Device(item) == IOT_JSON_RESULT.JSON_OK) {
                if (this.dispositivosIot == null) {
                    dispositivosIot = new ArrayList<>();
                }
                dispositivosIot.add(dispositivo);
            }

        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION;
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
                if (dispositivo.deviceName.equals(valor)) {
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
                if (dispositivo.deviceId.equals(valor)) {
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
    private IOT_OPERATION_CONFIGURATION_DEVICES nuevoDispositivo(IotDevice dispositivo) {

        IOT_OPERATION_CONFIGURATION_DEVICES op;
        if ((op = anadirDispositivoAlArray(dispositivo)) != IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED) {
            return op;

        }
         actualizarDatosDispositivos(dispositivo);


        return op;
    }

    public IOT_OPERATION_CONFIGURATION_DEVICES insertIotDevice(IotDevice dispositivo) {

        IOT_OPERATION_CONFIGURATION_DEVICES op;
        if (dispositivo == null) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NULL;
        }
        if ((op = nuevoDispositivo(dispositivo)) != IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED) {
            return op;
        }
        dispositivo.device2Json();
        if (guardarDispositivos(datosDispositivos.toString()) != ESTADO_FICHEROS.FICHERO_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED;
    }

    /**
     * Este metodo inserta en la estructura ArrayList un nuevo dispositivo
     * @param dispositivo es el dispositivo json
     */
    private IOT_OPERATION_CONFIGURATION_DEVICES anadirDispositivoAlArray(IotDevice dispositivo) {

        if (buscarDispositivoPorId(dispositivo.getDeviceId()) >= 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_EXITS;
        }

        if(buscarDispositivoPorNombre(dispositivo.getDeviceName()) >= 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_EXITS;
        }

        if (!dispositivo.isDeviceValid()) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }

        if (dispositivosIot == null) {
            dispositivosIot = new ArrayList<>();

        }
        dispositivosIot.add(dispositivo);
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED;

    }


    /**
     * Este metodo inserta un dispositivo a partir de un objeto JSON válido correspondiente a un dispositivoIot
     * @param dispositivoJson es el dispositivo
     * @return Retorna el valor de la operación.
     */
    public IOT_OPERATION_CONFIGURATION_DEVICES insertarDispositivoDesdeJson(JSONObject dispositivoJson) {

        IotDevice dispositivo;
        dispositivo = new IotDeviceUnknown();
        IOT_OPERATION_CONFIGURATION_DEVICES op;
        // Se pasa introduce la parte json en el dispositivo
        if (dispositivo.json2Device(dispositivoJson) != IOT_JSON_RESULT.JSON_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.NO_JSON_CONFIGURATION;
        }


        // Insertamos el dispositivo en la estructura
        if ((op = nuevoDispositivo(dispositivo)) != IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED) {
            return op;
        }
        if (guardarDispositivos(datosDispositivos.toString()) != ESTADO_FICHEROS.FICHERO_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }




        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED;
    }


    /**
     * Esta función insertará un dispositivo en la estructura desde un fichero json
     * @param texto Es el texto que contiene el disositivo en formato json
     * @return Se retorna el resultado del analisis json del dispositivo
     */
    public IOT_OPERATION_CONFIGURATION_DEVICES insertarDispositivoDesdeTexto(String texto) {

        JSONObject objeto;
        // Chequeamos que la estructura es json
        try {
            objeto = new JSONObject(texto);
        } catch(JSONException e) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.NO_JSON_CONFIGURATION;
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
                datosDispositivos.put(IOT_LABELS_JSON.DEVICES.getValorTextoJson(), array);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

        }
        try {
            array = datosDispositivos.getJSONArray(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
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
                array = datosDispositivos.getJSONArray(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
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
    public IOT_OPERATION_CONFIGURATION_DEVICES eliminarDispositivoPorId(String idDispositivo) {

        int i;
        if ((i = buscarDispositivoPorId(idDispositivo)) >= 0) {
            eliminar(i);
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_REMOVED;
        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
    }

    /**
     * Este método elimina un dispositivo de la configuracion cualificandolo por el nombre
     * @param nombreDispositivo Es el nombre del dispositivo
     * @return retorna el resultado de la operación
     */
    public IOT_OPERATION_CONFIGURATION_DEVICES eliminarDispositivoPorNombre(String nombreDispositivo) {

        int i;
        if ((i = buscarDispositivoPorNombre(nombreDispositivo)) >= 0) {
            eliminar(i);
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_REMOVED;
        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
    }

    private void eliminar(int i) {

        JSONArray array;
        try {
            array = datosDispositivos.getJSONArray(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        array.remove(i);
        dispositivosIot.remove(i);
        guardarDispositivos(datosDispositivos.toString());
        datosDispositivos.remove(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        loadIotDevices();

        }

        private IOT_OPERATION_CONFIGURATION_DEVICES modificarDispositivo(int i, IotDevice dispositivo) {


            JSONArray array;
            try {
                array = datosDispositivos.getJSONArray(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
            } catch (JSONException e) {
                e.printStackTrace();
                return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
            }
            array.remove(i);
            dispositivosIot.remove(i);
            array.put(dispositivo.device2Json());
            guardarDispositivos(datosDispositivos.toString());
            datosDispositivos.remove(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
            loadIotDevices();

            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED;
        }

    /**
     * Este método modifica un dispositivo buscandolo por el nombre
     * @param nombreDispositivo es el nombre del dispositivo
     * @param dispositivo Es el dispositivo a modificar
     * @return Se retorna el resultado de la operacion
     */
        public IOT_OPERATION_CONFIGURATION_DEVICES modificarDispositivoPorNombre(String nombreDispositivo, IotDevice dispositivo) {

            int i;
            if ((i = buscarDispositivoPorNombre(nombreDispositivo)) >= 0) {
                modificarDispositivo(i, dispositivo);
                return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED;
            }
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;


        }


    /**
     * Este métodoi modifica un dispositivo buscandolo por el id
     * @param idDispositivo es el id del dispositivo
     * @param dispositivo es el dispositivo que se va a modificar
     * @return se devuelve el resultado de la operacion
     */
        public IOT_OPERATION_CONFIGURATION_DEVICES modificarDispositivoporId(String idDispositivo, IotDevice dispositivo) {

            int i;
            if ((i = buscarDispositivoPorId(idDispositivo)) >= 0) {
                modificarDispositivo(i, dispositivo);
                return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED;
            }
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;


        }

}
