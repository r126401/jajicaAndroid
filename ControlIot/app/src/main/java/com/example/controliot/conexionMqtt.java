package com.example.controliot;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.UUID;
import java.io.*;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Para poder usar esta clase es necesario.
 * Incluir en el Manifest los permisos
 *     <uses-permission android:name="android.permission.INTERNET" />
 *     <uses-permission android:name="android.permission.WAKE_LOCK" />
 *     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 *     <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
 *     <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 *     <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 *     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *
 *     Ademas es necesario incluir en gradle
 *     implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5'
 *     implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
 *     implementation 'com.jakewharton.timber:timber:4.1.2'
 *
 *     Por ultimo es necesario incluir el servicio en el AndroidManifest
 *     <service android:name="org.eclipse.paho.android.service.MqttService" />
 *
 *     Ademas es necesario arrastrar la clare SocketFactory para poder usar la conexion SSL para Mqtt.
 *
 */


/**
 * Enum en el que aparecen los valores json a utilizar para el fichero de configuracion mqtt
 */
enum CONF_MQTT {
    MQTT("mqtt"), BROKER("broker"), PUERTO("puerto"), USUARIO("usuario"), PASSWORD("password"), TLS("tls");

    private String confMqtt;

    CONF_MQTT(String dato) {
        this.confMqtt = dato;

    }

    String getValorTextoJson() {

        return this.confMqtt;
    }

}


/**
 * Esta clase gestiona las conexiones de los dispositivos a traves de mqtt
 */

public class conexionMqtt implements Serializable, Parcelable {

    public MqttAndroidClient cliente;
    private boolean estadoConexion;
    private MqttConnectOptions opcionesConexion;
    private Context contexto;
    private String idCliente;
    private String brokerId;
    private String puerto;
    private String usuario;
    private String password;
    private boolean tls;
    private String ficheroMqtt = "iotControlMqtt.conf";
    private JSONObject datosMqtt;
    private OnConexionMqtt listener;
    private IMqttToken token;


    protected conexionMqtt(Parcel in) {
        estadoConexion = in.readByte() != 0;
        idCliente = in.readString();
        brokerId = in.readString();
        puerto = in.readString();
        usuario = in.readString();
        password = in.readString();
        ficheroMqtt = in.readString();
        estadoConexion = false;
    }

    public static final Creator<conexionMqtt> CREATOR = new Creator<conexionMqtt>() {
        @Override
        public conexionMqtt createFromParcel(Parcel in) {
            return new conexionMqtt(in);
        }

        @Override
        public conexionMqtt[] newArray(int size) {
            return new conexionMqtt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (estadoConexion ? 1 : 0));
        dest.writeString(idCliente);
        dest.writeString(brokerId);
        dest.writeString(puerto);
        dest.writeString(usuario);
        dest.writeString(password);
        dest.writeString(ficheroMqtt);
    }


    public interface OnConexionMqtt {

        void conexionPerdida();

    }

    public void setOnConexionMqtt(conexionMqtt.OnConexionMqtt listener) {
        this.listener = listener;
    }


    private void datosDefecto() {

        brokerId = "jajicaiot.ddns.net";
        puerto = "8883";
        usuario = "";
        password = "";
        tls = true;
    }

    /**
     * Establece el objeto MqttAndroidClient con los datos de la conexion.
     * Si no hay configuracion, se aplica la de defecto
     * @param contexto es el contexto de la aplicacion.
     */
    conexionMqtt(Context contexto) {

        JSONObject mqtt;
        cliente = null;
        estadoConexion = false;
        this.contexto = contexto;
        String cadenaConexion;

        if (leerConfiguracion() == false) {
            Log.i(getClass().toString(), ": No hay configuracion mqtt, se crea por defecto");

            escribirConfiguracionMqttDefecto();
        }


        try {
            mqtt = datosMqtt.getJSONObject(CONF_MQTT.MQTT.getValorTextoJson());
            brokerId = mqtt.optString(CONF_MQTT.BROKER.getValorTextoJson());
            puerto = mqtt.optString(CONF_MQTT.PUERTO.getValorTextoJson());
            usuario = mqtt.optString(CONF_MQTT.USUARIO.getValorTextoJson());
            password = mqtt.optString(CONF_MQTT.PASSWORD.getValorTextoJson());
            tls = mqtt.optBoolean(CONF_MQTT.TLS.getValorTextoJson());
            idCliente = UUID.randomUUID().toString();

            if (tls == false) {
                cadenaConexion = "tcp://" + brokerId + ":" + puerto;
            } else {
                cadenaConexion = "ssl://" + brokerId + ":" + puerto;
            }


            //cadenaConexion = "ssl://" + brokerId + ":" + puerto;
            Log.w(getClass().toString(), "cadena: " + cadenaConexion);
            opcionesConexion = new MqttConnectOptions();
            opcionesConexion.setAutomaticReconnect(true);
            opcionesConexion.setCleanSession(false);
            opcionesConexion.setConnectionTimeout(120);
            opcionesConexion.setMqttVersion(3);
            opcionesConexion.setHttpsHostnameVerificationEnabled(false);
            cliente = new MqttAndroidClient(contexto, cadenaConexion, idCliente );

            if (cadenaConexion.contains("ssl")) {
                SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
                socketFactoryOptions.withCaInputStream(contexto.getResources().openRawResource(R.raw.certificado));
                opcionesConexion.setSocketFactory(new SocketFactory(socketFactoryOptions));

            }
        } catch (JSONException e) {
            Log.e(getClass().toString(), "Error al procesar el json del fichero de configuracion mqtt");
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "Error en certificado");
        } catch (NoSuchAlgorithmException e) {
            Log.e(getClass().toString(), "Error en el algoritmo");
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            Log.e(getClass().toString(), "Error no recuperable");
            e.printStackTrace();
        } catch (KeyStoreException e) {
            Log.e(getClass().toString(), "Error en la clage del certificado");
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.e(getClass().toString(), "Error en KeyManagementException");
            e.printStackTrace();
        }
    }

    public void establecerConexionMqtt(Context contexto) {



        try {
            //Log.w(getClass().toString(), "MQTT: Nos conectamos al broker" + opcionesConexion.getSocketFactory().toString());

            cliente.connect(opcionesConexion, contexto, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.d(getClass().toString(), "MQTT: Conectado al broker con exito...");
                    token = iMqttToken;
                    setEstadoConexion(true);


                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.e(getClass().toString(), "MQTT: Error al conectar con el broker " + getEstadoConexion());
                    setEstadoConexion(false);
                    listener.conexionPerdida();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }



    /**
     * Escribe la configuracion de defecto en el que caso de que no ha hubiera.
     * @return
     */
    private boolean escribirConfiguracionMqttDefecto() {

        OutputStreamWriter escritor = null;
        JSONObject mqtt = null;

        try {
            escritor = new OutputStreamWriter(contexto.openFileOutput(ficheroMqtt, contexto.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        datosDefecto();
        mqtt = new JSONObject();
        datosMqtt = new JSONObject();
        try {
            mqtt.put(CONF_MQTT.BROKER.getValorTextoJson(), brokerId);
            mqtt.put(CONF_MQTT.PUERTO.getValorTextoJson(), puerto);
            mqtt.put(CONF_MQTT.USUARIO.getValorTextoJson(), usuario);
            mqtt.put(CONF_MQTT.PASSWORD.getValorTextoJson(), password);
            mqtt.put(CONF_MQTT.TLS.getValorTextoJson(), tls);
            datosMqtt.put(CONF_MQTT.MQTT.getValorTextoJson(), mqtt);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        try {
            escritor.write(datosMqtt.toString());
            escritor.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }



        return true;
    }



    /**
     * Esta funcion lee la configuracion desde el fichero de configuracionMqtt
     * @return false en caso de que no se pueda leer la configuracion. true lectura correcta
     */
    public boolean leerConfiguracion() {



        InputStreamReader flujo;
        BufferedReader lector;
        String linea = null;
        String texto = null;
        datosMqtt = null;



        try {
            flujo = new InputStreamReader(contexto.openFileInput(ficheroMqtt));
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
                datosMqtt = new JSONObject(texto);
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

    /**
     * Esta funcion obtiene nos dice si la conexion mqtt por ssl esta configurada
     * @return
     */
    public boolean getTls() {
        return tls;
    }

    /**
     * Esta funcion configura la conexion SSL
     * @param tls
     */
    public void setTls(Boolean tls) {

        this.tls = tls;
    }

    /**
     * Obtiene el identificativo del cliente de la conexion
     * @return
     */
    public String getIdCliente() {
        return idCliente;
    }

    /**
     * Funcion que determina si estamos conectados al broker.
     * @return
     */
    public boolean isConnected() {
        return estadoConexion;
    }


    /**
     * Configura la reconexion automatica
     * @param accion
     */
    public void setReconexionAutomatica(boolean accion) {

        opcionesConexion.setAutomaticReconnect(accion);
    }

    /**
     * Establece el limpiado de sesion
     * @param accion
     */

    public void setCleanSession(boolean accion) {
        opcionesConexion.setCleanSession(accion);
    }

    /**
     * Establece en segundos la temporizacion de timeout de la conexion mqtt
     * @param segundos
     */
    public void setConnetionTimeOut(int segundos) {
        opcionesConexion.setConnectionTimeout(segundos);
    }

    /**
     * Establece la version de la conexion mqtt
     * @param version
     */
    public void setMqttVersion(int version) {

        opcionesConexion.setMqttVersion(version);
    }

    public void setEstadoConexion(boolean estado) {
        this.estadoConexion = estado;
    }


    /**
     * Publica un mensaje de texto a traves del topic suministrado
     * @param topic sobre el que enviar el mensaje
     * @param texto es el mensaje a enviar
     */
    public void publicarTopic(String topic, String texto) {

        MqttMessage mensaje;

        if (cliente == null) {
            Log.e(getClass().toString(), "el cliente mqtt es nulo");
        }
        if (estadoConexion == true) {
            mensaje = new MqttMessage();
            mensaje.setPayload(texto.getBytes());
            try {
                cliente.publish(topic, mensaje);
                Log.w(getClass().toString(), topic + ": " + texto);
            } catch (MqttException e) {
                e.printStackTrace();
            }

        } else {
            Log.w(getClass().toString(), "rno estoy conectado y no puedo publica");
        }
    }


    /**
     * Funcion utilizada para subscribirse a un topic dado como parametro
     * @param topic
     */
    public void subscribirTopic(final String topic) {

        Log.w(getClass().toString(), "topic " + topic);
        if (estadoConexion == true) {
            try {
                cliente.subscribe(topic, 0, contexto, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        Log.w(getClass().toString(), "subscrito al topic " + topic);
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        Log.w(getClass().toString(), "subscrito sin exito al topic " + topic);

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

        } else {
            Log.w(getClass().toString(), "No estas conectado al broker");
            estadoConexion = false;
        }

    }


    /**
     * Cierre de la conexion mqtt
     */
    public void cerrarConexion() {
        try {
            cliente.disconnect();
            cliente = null;
            this.estadoConexion = false;
            //cliente.close();
            Log.w(getClass().toString(), "Conexion Mqtt cerrada");
        } catch (MqttException e) {
            e.printStackTrace();
            Log.w(getClass().toString(), "Error al intentar desconectar");
        }
    }

    public String getBrokerId() {
        return this.brokerId;
    }

    public String getPuerto() {
        return this.puerto;
    }

    public String getUsuario() {
        return  this.usuario;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getEstadoConexion() { return  this.estadoConexion;}

    public String getFicheroMqtt() {
        return this.ficheroMqtt;
    }

    public void setBrokerId(String broker) {
        this.brokerId = broker;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;

    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean escribirConfiguracion() {

        OutputStreamWriter escritor = null;
        JSONObject confMqtt = null;


        try {
            escritor = new OutputStreamWriter(contexto.openFileOutput(ficheroMqtt, contexto.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject mqtt = null;
        try {
            mqtt = new JSONObject();
            confMqtt = new JSONObject();
            confMqtt.put(CONF_MQTT.BROKER.getValorTextoJson(), getBrokerId());
            confMqtt.put(CONF_MQTT.PUERTO.getValorTextoJson(), getPuerto());
            confMqtt.put(CONF_MQTT.USUARIO.getValorTextoJson(), getUsuario() );
            confMqtt.put(CONF_MQTT.PASSWORD.getValorTextoJson(), getPassword());
            confMqtt.put(CONF_MQTT.TLS.getValorTextoJson(), getTls());
            mqtt.putOpt(CONF_MQTT.MQTT.getValorTextoJson(), confMqtt);


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



}
