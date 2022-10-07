package net.jajica.libiot;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;



/**
 * Esta funcion gestionará la configuracion de la aplicacion para saber cuales son los parametros
 * de conexion a utilizar
 */

public class ConfiguracionConexionMqtt {

    private final String FICHERO_CONFIGURACION_MQTT ="MyHomeIot.conf";
    /**
     * Contexto de la aplicacion
     */
    private Context context;
    /**
     * fichero de configuracion
     */
    private String ficheroConfiguracion;
    /**
     * json de la configuracion en la que aparecen todos los parametros
     */
    private JSONObject datosConfiguracion;
    /**
     * nombre del broker mqtt
     */
    private String brokerId = "jajicaiot.ddns.net";
    /**
     * Puerto de la conexion
     */
    private String puerto = "8883";
    /**
     * Usuario de la conexion mqtt
     */
    private String usuario = "";
    /**
     * password de la conexion mqtt
     */
    private String password = "";
    /**
     * Indica si la conexion al broker va a ser usada con ssl
     * true es que se usará conexion segura
     */
    private Boolean tls = true;
    /**
     * Etiqueta usada para identificar la clase en la traza de log.
     */
    private final String TAG = "ConfiguracionConexionMqtt";

    public String getBrokerId() {
        return brokerId;
    }

    /**
     * Este metodo modifica el broker y guarda la configuracion
     * @param brokerId identidad del broker
     * @return Se retorna el resultado de la operacion
     */
    public CONFIGURACION_CONEXION_MQTT setBrokerId(String brokerId) {
        this.brokerId = brokerId;
        return escribirConfiguracionMqtt();
    }

    public String getPuerto() {
        return puerto;
    }
    /**
     * Este metodo modifica el puerto y guarda la configuracion
     * @param puerto identidad del puerto
     * @return Se retorna el resultado de la operacion
     */
    public CONFIGURACION_CONEXION_MQTT setPuerto(String puerto) {
        this.puerto = puerto;
        return escribirConfiguracionMqtt();
    }

    public String getUsuario() {
        return usuario;
    }

    /**
     * Este metodo modifica el broker y guarda la configuracion
     * @param usuario identidad del usuario
     * @return Se retorna el resultado de la operacion
     */
    public CONFIGURACION_CONEXION_MQTT setUsuario(String usuario) {
        this.usuario = usuario;
        return escribirConfiguracionMqtt();
    }

    public String getPassword() {
        return password;
    }
    /**
     * Este metodo modifica el broker y guarda la configuracion
     * @param password identidad del password
     * @return Se retorna el resultado de la operacion
     */
    public CONFIGURACION_CONEXION_MQTT setPassword(String password) {
        this.password = password;
        return escribirConfiguracionMqtt();
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setFicheroConfiguracion(String ficheroConfiguracion) {
        this.ficheroConfiguracion = ficheroConfiguracion;
    }

    /**
     * Este metodo devuelve la configuracion de la aplicacion en formato json
     * @return Se devuelve la configuracion en formato json
     */
    public JSONObject getDatosConfiguracion() {
        return datosConfiguracion;
    }

    public void setDatosConfiguracion(JSONObject datosConfiguracion) {
        this.datosConfiguracion = datosConfiguracion;
    }

    public Boolean getTls() {
        return tls;
    }
    /**
     * Este metodo modifica la configuracion de la conexion y guarda la configuracion
     * @param tls true si se va a usar ssl. False si se usa conexion no segura
     * @return Se retorna el resultado de la operacion
     */
    public CONFIGURACION_CONEXION_MQTT setTls(Boolean tls) {
        this.tls = tls;
        return escribirConfiguracionMqtt();
    }

    /**
     * Constructor de la aplicacion para entorno android
     * @param context Es el contexto de la aplicacion. Se establece el nombre por defecto del
     * fichero de configuracion
     */
    public ConfiguracionConexionMqtt(Context context) {

        setFicheroConfiguracion(FICHERO_CONFIGURACION_MQTT);
        setDatosConfiguracion(null);
        setContext(context);


    }

    /**
     * Constructor usado para entornos linux. En el caso de que se quiera usar para Android, se
     * deberia poner el contexto en la clase para que se lean y escriban correctamente las configuraciones.
     */
    public ConfiguracionConexionMqtt() {
        setFicheroConfiguracion(FICHERO_CONFIGURACION_MQTT);
        setDatosConfiguracion(null);
        setContext(null);
    }

    /**
     * Este metodo lo usará la aplicacion para leer los parametros de la conexion al broker.
     * @return Retorna el resultado de la operacion.
     */
    public CONFIGURACION_CONEXION_MQTT cargarConfiguracion() {

        Ficheros configuracion;
        ESTADO_FICHEROS estadoOperacion;
        CONFIGURACION_CONEXION_MQTT res;
        String texto;

        configuracion = new Ficheros();
        if (context == null) {
            estadoOperacion = configuracion.leerFichero(ficheroConfiguracion);
        } else {
            estadoOperacion = configuracion.leerFichero(context, ficheroConfiguracion);
        }

        if(estadoOperacion != ESTADO_FICHEROS.FICHERO_OK) {
            return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_INEXISTENTE;
        }
        texto = configuracion.getTextoFichero();
        try {
            datosConfiguracion = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_CORRUPTA;
        }
        if ((res = json2Configuracion()) != CONFIGURACION_CONEXION_MQTT.CONEXION_MQTT_OK) {
            return res;
        }

        return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK;

    }

    /**
     * Esta funcion escribe la configuracion de los parametros a disco.
     * @return Se retorna el resultado de la operacion
     */
    public CONFIGURACION_CONEXION_MQTT escribirConfiguracionMqtt() {

        JSONObject objeto;
        objeto = new JSONObject();
        Ficheros configuracion;
        ESTADO_FICHEROS estado;
        datosConfiguracion = new JSONObject();
        try {
            objeto.put(CONF_MQTT.BROKER.getValorTextoJson(), getBrokerId());
            objeto.put(CONF_MQTT.PUERTO.getValorTextoJson(), getPuerto());
            objeto.put(CONF_MQTT.USUARIO.getValorTextoJson(), getUsuario());
            objeto.put(CONF_MQTT.PASSWORD.getValorTextoJson(), getPassword());
            objeto.put(CONF_MQTT.TLS.getValorTextoJson(), getTls());
            datosConfiguracion.put(CONF_MQTT.MQTT.getValorTextoJson(), objeto);

        } catch(JSONException e) {
            e.printStackTrace();
            return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_DEFECTO_ERRONEA;

        }
        configuracion = new Ficheros();
        if ((estado = configuracion.escribirFichero(ficheroConfiguracion, datosConfiguracion.toString(), context))!= ESTADO_FICHEROS.FICHERO_OK) {
            Log.e(TAG, "Error al escribir el fichero con error: " + estado);
            return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_ERROR_ESCRIBIR_CONFIGURACION;
        }

        return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK;


    }

    /**
     * Este metodo se usará para volcar la configuracion en formato json a la estructura de la clase
     * @return Se retorna el resultado de la operacion.
     */
    private CONFIGURACION_CONEXION_MQTT json2Configuracion() {

        JSONObject datos;
        if (datosConfiguracion == null) {
            return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_INEXISTENTE;
        }
        try {
            datos = datosConfiguracion.getJSONObject(CONF_MQTT.MQTT.getValorTextoJson());
            setBrokerId(datos.getString(CONF_MQTT.BROKER.getValorTextoJson()));
            setPuerto(datos.getString(CONF_MQTT.PUERTO.getValorTextoJson()));
            setUsuario(datos.getString(CONF_MQTT.USUARIO.getValorTextoJson()));
            setPassword(datos.getString(CONF_MQTT.PASSWORD.getValorTextoJson()));
            setTls(datos.getBoolean(CONF_MQTT.TLS.getValorTextoJson()));
        } catch (JSONException e) {
            e.printStackTrace();
            return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_CORRUPTA;
        }
        return CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK;
    }




}
