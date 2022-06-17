package com.example.controliot;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.UUID;
import java.io.*;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
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
    private IMqttToken token;
    private CountDownTimer temporizacionReintento;
    private final String LOG_CLASS = "conexionMqtt";
    private dialogoIot dialogo;
    private dispositivoIotOnOff dispositivoOnOff;
    private dispositivoIotTermostato dispositivoTermostato;
    private dispositivoIotTermostato dispositivoTermometro;

    private OnConexionMqtt listener;
    private OnRecibirMensaje listenerRecibirMensaje;
    private OnProcesarMensajesInterruptor listenerMensajesInterruptor;
    private OnProcesarMensajesTermometro listenerMensajesTermometro;
    private OnProcesarMensajesTermostato listenerMensajesTermostato;
    private OnProcesarEspontaneosInterruptor listenerEspontaneosInterruptor;
    private OnProcesarEspontaneosTermometro listenerEspontaneosTermometro;
    private OnProcesarEspontaneosTermostato listenerEspontaneosTermostato;
    private OnProcesarVersionServidorOta listenerVersionOta;

    public interface OnConexionMqtt {

        void conexionEstablecida(boolean reconnect, String serverURI);
        void conexionPerdida(Throwable cause);
        void mensajeRecibido(String topic, MqttMessage message);
        void entregaCompletada(IMqttDeliveryToken token);
        void notificacionIntermediaReintento(long intervalo);
        void finTemporizacionReintento(long temporizador);

    }

    public interface OnProcesarVersionServidorOta {
        void lastVersion(OtaVersion otaVersion);
    }

    public void setOnProcesarVersionServidorOta(OnProcesarVersionServidorOta listener) {
        this.listenerVersionOta = listener;
    }


    /**
     * Definicion de los interfaces para termometro
     */
    public interface OnProcesarMensajesTermometro {
        void estadoTermometro(String topic, String message, dispositivoIotTermostato dispositivo);
        void actuacionReleLocalTermometro(String topic, String message, dispositivoIotTermostato dispositivo);
        void actuacionReleRemotoTermometro(String topic, String message, dispositivoIotTermostato dispositivo);


    }
    public void setOnProcesarMensajesTermometro(OnProcesarMensajesTermometro listener) {
        this.listenerMensajesTermometro = listener;
    }
    public interface OnProcesarEspontaneosTermometro{

    }
    public void setOnProcesarEspontaneoTermometro(OnProcesarEspontaneosTermometro listener) {
        this.listenerEspontaneosTermometro = listener;
    }
    /****************************************************************************************************/

    /**
     * Defiicion de los interfaces para termostato
     */
    public interface OnProcesarMensajesTermostato  {
        void estadoTermostato(String topic, String message, dispositivoIotTermostato dispositivo);
        void actuacionReleLocalTermostato(String topic, String message, dispositivoIotTermostato dispositivo);
        void actuacionReleRemotoTermostato(String topic, String message, dispositivoIotTermostato dispositivo);
        void consultarProgramacionTermostato(String topic, String texto, String idDispositivo, ArrayList<ProgramaDispositivoIotTermostato> programa);
        void nuevoProgramacionTermostato(String topic, String texto, String idDispositivo);
        void eliminarProgramacionTermostato(String topic, String texto, String idDispositivo, String idPrograma);
        void modificarProgramacionTermostato(String topic, String texto);
        void informacionDispositivo(String topic, String texto);
        void resetTermostato(String topic, String texto, String idDispositivo);
        void factoryResetTermostato(String topic, String texto, String idDispositivo);
        void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion);
        void modificarUmbralTemperatura(String topic, String texto, double umbral);
        void seleccionarSensorTemperatura(String topic, String texto);
        void modificarConfiguracionTermostato(String topic, String texto);



    }
    public void setOnProcesarMensajesTermostato(OnProcesarMensajesTermostato listener) {
        this.listenerMensajesTermostato = listener;
    }
    public interface OnProcesarEspontaneosTermostato{

        void arranqueAplicacionTermostato(String topic, String texto, dispositivoIotTermostato dispoisitivo);
        void cambioProgramaTermostato(String topic, String texto, dispositivoIotTermostato dispositivo);
        void atuacionReleLocalTermostato(String topic, String texto, String idDisositivo, ESTADO_RELE estadoRele);
        void actuacionReleRemotoTermostato(String topic, String texto, String idDispositivo, ESTADO_RELE estadoRele);
        void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion);
        void cambioTemperaturaTermostato(String topic, String texto, dispositivoIotTermostato dispositivo);
        void temporizadorCumplido(String topic, String texto, dispositivoIotTermostato dispositivo);
        void cambioUmbralTemperatura(String topic, String texto, dispositivoIotTermostato dispositivo);

    }
    public void setOnProcesarEspontaneosTermostato(OnProcesarEspontaneosTermostato listener) {
        this.listenerEspontaneosTermostato = listener;
    }

/*******************************************************************************************************/

    public interface OnProcesarMensajesInterruptor {
        void estadoInterruptor(String topic, String mensaje, dispositivoIotOnOff dispositivo );
        void actuacionReleLocalInterruptor(String topic, String message, dispositivoIotOnOff dispositivo);
        void actuacionReleRemotoInterruptor(String topic, String message, dispositivoIotOnOff dispositivo);
        void consultarProgramacionInterruptor(String topic, String texto, ArrayList<ProgramaDispositivoIotOnOff> programa);
        void nuevoProgramacionInterruptor(String topic, String texto, String idDispositivo);
        void eliminarProgramacionInterruptor(String topic, String texto, String idDispositivo, String programa);
        void modificarProgramacionInterruptor(String topic, String texto, String idDispositivo);
        void modificarAplicacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo);
        void resetInterruptor(String topic, String texto, String idDispositivo);
        void factoryResetInterruptor(String topic, String texto, String idDispositivo);
        void upgradeFirmwareInterruptor(String topic, String texto, String idDispositivo, OtaVersion otaVersion);
        void recibirVersionOtaDisponibleInterruptor(String topic, String texto, String idDispositivo, OtaVersion version);
        void informacionDispositivo(String topic, String texto);
        void errorMensajeInterruptor(String topic, String  mensaje);

    }
    public interface OnProcesarEspontaneosInterruptor{
        void arranqueAplicacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo);
        void cambioPrograma(String topic, String texto, dispositivoIotOnOff dispositivo);
        void actuacionRelelocal(String topic, String texto, dispositivoIotOnOff dispositivo);
        void actuacionReleRemoto(String topic, String texto, dispositivoIotOnOff dispositivo);
        void upgradeFirwareFota(String topic, String texto, String idDispositivo, OtaVersion otaVersion);
        void espontaneoDesconocido(String topic, String texto);
        void releTemporizado(String topic, String texto);
        void alarmaDispositivo(String topic, String texto);

    }
    void setOnProcesarMensajesInterruptor (OnProcesarMensajesInterruptor listener) {
        this.listenerMensajesInterruptor = listener;

    }

    public interface OnRecibirMensaje {
        void recibirMensaje();
    }

    public void setOnRecibirMensajes(OnRecibirMensaje listener) {
        this.listenerRecibirMensaje = listener;

    }





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
    conexionMqtt(Context contexto, dialogoIot dialogo) {

        JSONObject mqtt;
        cliente = null;
        estadoConexion = false;
        this.contexto = contexto;
        String cadenaConexion;
        this.dialogo = dialogo;
        dispositivoOnOff = null;
        dispositivoTermometro = null;
        dispositivoTermostato = null;

        if (leerConfiguracion() == false) {
            Log.i(LOG_CLASS, ": No hay configuracion mqtt, se crea por defecto");

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
            Log.w(LOG_CLASS, "cadena: " + cadenaConexion);
            opcionesConexion = new MqttConnectOptions();
            opcionesConexion.setAutomaticReconnect(true);
            opcionesConexion.setCleanSession(false);
            opcionesConexion.setConnectionTimeout(20);
            opcionesConexion.setMqttVersion(3);
            opcionesConexion.setHttpsHostnameVerificationEnabled(false);
            cliente = new MqttAndroidClient(contexto, cadenaConexion, idCliente );

            if (cadenaConexion.contains("ssl")) {
                SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
                socketFactoryOptions.withCaInputStream(contexto.getResources().openRawResource(R.raw.certificado));
                opcionesConexion.setSocketFactory(new SocketFactory(socketFactoryOptions));

            }
        } catch (JSONException e) {
            Log.e(LOG_CLASS, "Error al procesar el json del fichero de configuracion mqtt");
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
            Log.e(LOG_CLASS, "Error en certificado");
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_CLASS, "Error en el algoritmo");
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            Log.e(LOG_CLASS, "Error no recuperable");
            e.printStackTrace();
        } catch (KeyStoreException e) {
            Log.e(LOG_CLASS, "Error en la clage del certificado");
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.e(LOG_CLASS, "Error en KeyManagementException");
            e.printStackTrace();
        }
    }

    public void establecerConexionMqtt() {



        try {
            //Log.w(LOG_CLASS, "MQTT: Nos conectamos al broker" + opcionesConexion.getSocketFactory().toString());

            cliente.connect(opcionesConexion, contexto, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.d(LOG_CLASS, "MQTT: Conectado al broker con exito...");
                    token = iMqttToken;
                    setEstadoConexion(true);


                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.e(LOG_CLASS, "MQTT: Error al conectar con el broker " + getEstadoConexion());
                    setEstadoConexion(false);
                    listener.conexionPerdida(throwable);

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
    private boolean leerConfiguracion() {



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
    public boolean publicarTopic(String topic, String texto) {

        MqttMessage mensaje;

        if (cliente == null) {
            Log.e(LOG_CLASS, "el cliente mqtt es nulo");
        }
        if (estadoConexion == true) {
            mensaje = new MqttMessage();
            mensaje.setPayload(texto.getBytes());
            try {
                cliente.publish(topic, mensaje);
                Log.w(LOG_CLASS, topic + ": " + texto);
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            Log.w(LOG_CLASS, "rno estoy conectado y no puedo publica");
            return false;
        }
        return true;
    }


    /**
     * Funcion utilizada para subscribirse a un topic dado como parametro
     * @param topic
     */
    public void subscribirTopic(final String topic) {

        Log.w(LOG_CLASS, "topic " + topic);
        if (estadoConexion == true) {
            try {
                cliente.subscribe(topic, 0, contexto, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        Log.w(LOG_CLASS, "subscrito al topic " + topic);
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        Log.w(LOG_CLASS, "subscrito sin exito al topic " + topic);

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

        } else {
            Log.w(LOG_CLASS, "No estas conectado al broker");
            estadoConexion = false;
        }

    }


    /**
     * Cierre de la conexion mqtt
     */
    public void cerrarConexion() {
        try {
            if (cliente != null) {
                if (cliente.isConnected() == true) {
                    cliente.disconnect();
                }
            }
            cliente = null;
            this.estadoConexion = false;
            //cliente.close();
            Log.w(LOG_CLASS, "Conexion Mqtt cerrada");
        } catch (MqttException e) {
            e.printStackTrace();
            Log.w(LOG_CLASS, "Error al intentar desconectar");
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



    public void conectarseAlBroker() {
        cliente.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                listener.conexionEstablecida(reconnect, serverURI);

            }

            @Override
            public void connectionLost(Throwable cause) {
                listener.conexionPerdida(cause);

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                listener.mensajeRecibido(topic, message);
                //dialogoIot dialogo = new dialogoIot();
                procesarMensajes(topic, message, contexto);


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                listener.entregaCompletada(token);


            }
        });

        establecerConexionMqtt();



    }

    public void conectarseAlBrokerConReintento(long tiempoReintento, long intervaloNotificacion) {

        conectarseAlBroker();



        temporizacionReintento = new CountDownTimer(tiempoReintento, intervaloNotificacion) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(getEstadoConexion()) {
                    Log.i(LOG_CLASS, "conexion completa, se cancela el temporizador");
                    this.cancel();
                }
                listener.notificacionIntermediaReintento(millisUntilFinished);

            }

            @Override
            public void onFinish() {
                if (!getEstadoConexion()) {
                    Log.e(LOG_CLASS, "No se ha podido establecer la conexion, se reintenta");
                    conectarseAlBroker();
                    this.start();

                }
                listener.finTemporizacionReintento(tiempoReintento);



            }
        };

        temporizacionReintento.start();



    }

    private void procesarMensajeServidorOta(String topic, MqttMessage message, Context contexto) {

        OtaVersion version;
        String texto;
        version = new OtaVersion();
        texto = new String(message.getPayload());
        version.setDatosOtaDispositivo(texto);
        if (listenerVersionOta != null) {
            listenerVersionOta.lastVersion(version);
        }



    }

    /**
     * Determina a que tipo de dispositivo se refiere el mensaje
     * @param topic
     * @param message
     * @param contexto
     * @throws JSONException
     */
    protected void procesarMensajes(String topic, MqttMessage message, Context contexto) throws JSONException {

        TIPO_DISPOSITIVO_IOT tipo;
        String respuesta;
        String clave;
        //dialogoIot dialogo;
        //dialogo = new dialogoIot();
        respuesta = new String(message.getPayload());
        dispositivoIot dispositivo;
        dispositivo = new dispositivoIotDesconocido();
        tipo = dialogo.getTipoDispositivo(respuesta);
        clave = dialogo.getClave(respuesta);
        dialogo.eliminarTemporizador(clave);

        if (listenerRecibirMensaje != null) {
            listenerRecibirMensaje.recibirMensaje();
        }

        switch (tipo) {


            case DESCONOCIDO:
                Log.e(getClass().toString(), "No se ha podido determinar el tipo de dispositivo");
                break;
            case INTERRUPTOR:
                procesarMensajesInterruptor(topic, message, contexto);
                break;
            case CRONOTERMOSTATO:
            case TERMOMETRO:
                procesarMensajesTermometroTermostato(topic, message, contexto);
                break;
            case SERVIDOR_OTA:
                procesarMensajeServidorOta(topic, message, contexto);
            default:
                throw new IllegalStateException("Unexpected value: " + tipo);
        }



    }


/********************************************************************************************************************/

/****************************** Procesamiento de mensajes de termostato *********************************************/

    private void procesarMensajesTermometroTermostato(String topic, MqttMessage message, Context contexto) {
        COMANDO_IOT idComando;
        dispositivoTermostato = new dispositivoIotTermostato();
        String texto = new String(message.getPayload());
        idComando = dialogo.descubrirComando(texto);

        if (idComando == COMANDO_IOT.ESPONTANEO) {

            procesarMensajeEspontaneoTermometroTermostato(topic, message, contexto);
        } else {
            procesarRespuestaComandoTermometroTermostato(topic, message, contexto);
        }

    }
    private void procesarMensajeEspontaneoTermometroTermostato(String topic, MqttMessage message, Context contexto) {

        ESPONTANEO_IOT tipoInformeEspontaneo;
        dispositivoIotTermostato dispositivo;

        if (listenerEspontaneosTermostato != null) {
            String texto = new String(message.getPayload());

            tipoInformeEspontaneo = dialogo.descubrirTipoInformeEspontaneo(texto);
            switch (tipoInformeEspontaneo) {

                case ARRANQUE_APLICACION:
                    dispositivo = procesarActualizacionEstadoTermostato(topic, texto, contexto);
                    listenerEspontaneosTermostato.arranqueAplicacionTermostato(topic, texto, dispositivo);
                    break;
                case ACTUACION_RELE_LOCAL:
;
                    break;
                case ACTUACION_RELE_REMOTO:
                    break;
                case UPGRADE_FIRMWARE_FOTA:
                    break;
                case CAMBIO_DE_PROGRAMA:
                    dispositivo = procesarActualizacionEstadoTermostato(topic, texto, contexto);
                    listenerEspontaneosTermostato.cambioProgramaTermostato(topic, texto, dispositivo);
                    break;
                case COMANDO_APLICACION:
                    break;
                case CAMBIO_TEMPERATURA:
                    dispositivo = procesarActualizacionEstadoTermostato(topic, texto, contexto);
                    listenerEspontaneosTermostato.cambioTemperaturaTermostato(topic, texto, dispositivo);
                    break;
                case CAMBIO_ESTADO:
                    break;
                case RELE_TEMPORIZADO:
                    dispositivo = procesarActualizacionEstadoTermostato(topic, texto, contexto);
                    if (listenerEspontaneosTermostato != null ) listenerEspontaneosTermostato.temporizadorCumplido(topic, texto, dispositivo);
                    break;
                case INFORME_ALARMA:
                    break;
                case CAMBIO_UMBRAL_TEMPERATURA:
                    dispositivo = procesarActualizacionEstadoTermostato(topic, texto, contexto);
                    listenerEspontaneosTermostato.cambioUmbralTemperatura(topic, texto, dispositivo);
                    break;
                case CAMBIO_ESTADO_APLICACION:
                    break;
                case ERROR:
                    break;
                case ESPONTANEO_DESCONOCIDO:
                    break;
            }

        }



    }
    private void procesarRespuestaComandoTermometroTermostato(String topic, MqttMessage message, Context contexto) {

        COMANDO_IOT idComando;
        String texto = new String(message.getPayload());
        idComando = dialogo.descubrirComando(texto);
        ArrayList<ProgramaDispositivoIotTermostato> programa = null;
        COMANDO_IOT_TERMOMETRO idcomandoTermostato;
        JSONObject textoJson = null;
        try {
            textoJson = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String idDispositivo = textoJson.optString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());





        switch (idComando) {

            case CONSULTAR_CONF_APP:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.informacionDispositivo(topic, texto);
                break;
            case ACTUAR_RELE:
                break;
            case ESTADO:
                dispositivoTermostato = procesarEstadoDispositivoTermometroTermostato(topic, texto, contexto);
                if (listenerMensajesTermostato!= null) listenerMensajesTermostato.estadoTermostato(topic, texto, dispositivoTermostato);
                if (listenerMensajesTermometro!= null) listenerMensajesTermometro.estadoTermometro(topic, texto, dispositivoTermostato);
                break;
            case CONSULTAR_PROGRAMACION:
                programa = procesarConsultaProgramaTermostato(texto, contexto);
                //dispositivo = procesarEstadoDispositivoTermometroTermostato(topic, texto, contexto);
                //dispositivo.setProgramasTermostato(programa);
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.consultarProgramacionTermostato(topic, texto, idDispositivo, programa);

                break;
            case NUEVA_PROGRAMACION:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.nuevoProgramacionTermostato(topic, texto,idDispositivo);
                break;
            case ELIMINAR_PROGRAMACION:
                int indice;
                String idPrograma = dialogo.extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.eliminarProgramacionTermostato(topic, texto, idDispositivo, idPrograma);
                break;
            case MODIFICAR_PROGRAMACION:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.modificarProgramacionTermostato(topic, texto);
                break;
            case MODIFICAR_APP:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.modificarConfiguracionTermostato(topic, texto);
                break;
            case RESET:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.resetTermostato(topic, texto, idDispositivo);
                break;
            case FACTORY_RESET:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.factoryResetTermostato(topic, texto, idDispositivo);
                break;
            case MODIFY_CLOCK:
                break;
            case UPGRADE_FIRMWARE:
                OtaVersion otaVersion = null;
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.upgradeFirmwareTermostato(topic, texto, idDispositivo, otaVersion);
                break;
            case MODIFICAR_UMBRAL_TEMPERATURA:
                double umbral = procesarUmbralTemperatura(texto, contexto);
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.modificarUmbralTemperatura(topic, texto, umbral);
                break;
            case ESPONTANEO:
                break;
            case VERSION_OTA:
                break;
            case SELECCIONAR_SENSOR_TEMPERATURA:
                if (listenerMensajesTermostato != null) listenerMensajesTermostato.seleccionarSensorTemperatura(topic, texto);
                break;
            case ERROR_RESPUESTA:
                break;
        }

    }


    /**
     * Esta función determina el tipo de mensaje que llega para el interruptor.
     *
     * @param topic
     * @param message
     * @param contexto
     * @throws JSONException
     */
    public void procesarMensajesInterruptor(String topic, MqttMessage message, Context contexto) throws JSONException {

        COMANDO_IOT idComando;
        dispositivoIot dispositivo;
        dispositivo = new dispositivoIotOnOff();
        String texto = new String(message.getPayload());
        idComando = dialogo.descubrirComando(texto);

        if (idComando == COMANDO_IOT.ESPONTANEO) {

            procesarMensajeEspontaneoInterruptor(topic, message, contexto);
        } else {
            procesarRespuestaComandoInterruptor(topic, message, contexto);
        }

    }
    void procesarMensajeEspontaneoInterruptor(String topic, MqttMessage message, Context contexto) {

        ESPONTANEO_IOT tipoInformeEspontaneo;
        dispositivoIotOnOff dispositivo;
        if (listenerEspontaneosInterruptor != null ) {
            String texto = new String(message.getPayload());
            tipoInformeEspontaneo = dialogo.descubrirTipoInformeEspontaneo(texto);
            switch (tipoInformeEspontaneo) {

                case ARRANQUE_APLICACION:
                    dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                    listenerEspontaneosInterruptor.arranqueAplicacionInterruptor(topic, texto, dispositivo);
                    break;
                case ACTUACION_RELE_LOCAL:
                    dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                    listenerEspontaneosInterruptor.actuacionRelelocal(topic, texto, dispositivo);
                    break;
                case ACTUACION_RELE_REMOTO:
                    dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                    listenerEspontaneosInterruptor.actuacionReleRemoto(topic, texto, dispositivo);
                    break;
                case UPGRADE_FIRMWARE_FOTA:
                    break;
                case CAMBIO_DE_PROGRAMA:
                    dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                    listenerEspontaneosInterruptor.cambioPrograma(topic, texto, dispositivo);
                    break;
                case COMANDO_APLICACION:
                    break;
                case CAMBIO_TEMPERATURA:
                    break;
                case CAMBIO_ESTADO:
                    break;
                case RELE_TEMPORIZADO:
                    dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                    listenerEspontaneosInterruptor.releTemporizado(topic, texto);
                    break;
                case INFORME_ALARMA:
                    listenerEspontaneosInterruptor.alarmaDispositivo(topic, texto);
                    break;
                case CAMBIO_UMBRAL_TEMPERATURA:
                    break;
                case CAMBIO_ESTADO_APLICACION:
                    break;
                case ERROR:
                    break;
                case ESPONTANEO_DESCONOCIDO:
                    listenerMensajesInterruptor.errorMensajeInterruptor(topic, texto);
                    break;
            }
        }

    }

    public void setOnProcesarEspontaneosInterruptor(OnProcesarEspontaneosInterruptor listener) {
        this.listenerEspontaneosInterruptor = listener;
    }

    void procesarRespuestaComandoInterruptor(String topic, MqttMessage message, Context contexto) throws JSONException {

        COMANDO_IOT idComando;
        dispositivoIotOnOff dispositivo;

        String texto = new String(message.getPayload());
        idComando = dialogo.descubrirComando(texto);
        ArrayList<ProgramaDispositivoIotOnOff> programa;

        JSONObject textoJson = new JSONObject(texto);
        String idDispositivo = textoJson.optString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());

        switch (idComando) {

            case CONSULTAR_CONF_APP:
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.informacionDispositivo(topic, texto);
                break;
            case ACTUAR_RELE:
                dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                listenerMensajesInterruptor.actuacionReleRemotoInterruptor(topic, texto, dispositivo);
                break;
            case ESTADO:

                dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                listenerMensajesInterruptor.estadoInterruptor(topic, texto, dispositivo);
                break;
            case CONSULTAR_PROGRAMACION:
                programa = procesarConsultaProgramaInterruptor(texto, contexto);
                dispositivo = procesarEstadoDispositivoInterruptor(topic, texto, contexto);
                dispositivo.setProgramasOnOff(programa);
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.consultarProgramacionInterruptor(topic, texto, programa);
                break;
            case NUEVA_PROGRAMACION:
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.nuevoProgramacionInterruptor(topic, texto, idDispositivo);
                break;
            case ELIMINAR_PROGRAMACION:
                int indice;
                String idPrograma = dialogo.extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.eliminarProgramacionInterruptor(topic, texto, idDispositivo, idPrograma);
                break;
            case MODIFICAR_PROGRAMACION:
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.modificarProgramacionInterruptor(topic, texto, idDispositivo);
                break;
            case MODIFICAR_APP:
                dispositivo = null;
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.modificarAplicacionInterruptor(topic, texto, dispositivo);
                break;
            case RESET:
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.resetInterruptor(topic, texto, idDispositivo);
                break;
            case FACTORY_RESET:
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.factoryResetInterruptor(topic, texto, idDispositivo);
                break;
            case MODIFY_CLOCK:
                break;
            case UPGRADE_FIRMWARE:
                OtaVersion otaVersion = null;
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.upgradeFirmwareInterruptor(topic, texto, idDispositivo, otaVersion);
                break;
            case ESPONTANEO:
                break;
            case VERSION_OTA:
                OtaVersion version = null;
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.recibirVersionOtaDisponibleInterruptor(topic, texto, idDispositivo, version);
            case ERROR_RESPUESTA:
                if (listenerMensajesInterruptor != null) listenerMensajesInterruptor.errorMensajeInterruptor(topic, texto);
                break;
        }

    }
    public void setOnProcesarRespuestaComandoInterruptor(OnProcesarMensajesInterruptor listener) {
        this.listenerMensajesInterruptor = listener;
    }

    /******************************************************************************************************************************/

    private dispositivoIotOnOff procesarEstadoDispositivoInterruptor(String topic, String texto, Context contexto) {

        JSONObject mensaje;
        String idDispositivo;
        configuracionDispositivos confDisp;
        try {
            mensaje = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "El mensaje no es json");
            return null;

        }

        dispositivoIot dispositivo;
        idDispositivo = mensaje.optString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        confDisp = new configuracionDispositivos(contexto);
        dispositivo = confDisp.getDispositivoPorId(idDispositivo);
        dispositivoIotOnOff dispIotOnOff;
        dispIotOnOff = new dispositivoIotOnOff(dispositivo);
        ESTADO_RELE estado = dialogo.getEstadoRele(texto);
        dispIotOnOff.setEstadoRele(estado);
        dispIotOnOff.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
        dispIotOnOff.setEstadoDispositivo(dialogo.getEstadoDispositivo(texto));
        dispIotOnOff.setVersionOta(dialogo.getOtaVersion(texto));
        dispIotOnOff.setProgramaActivo(dialogo.getProgramaActivo(texto));
        dispIotOnOff.setTipoDispositivo(dialogo.getTipoDispositivo(texto));
        dispIotOnOff.setFinUpgrade(dialogo.getFinUpgrade(texto));



        return dispIotOnOff;
    }

    private dispositivoIotTermostato procesarActualizacionEstadoTermostato(String topic, String texto, Context contexto) {
        JSONObject mensaje;
        String idDispositivo;
        configuracionDispositivos confDisp;
        try {
            mensaje = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "El mensaje no es json");
            return null;

        }

        dispositivoIot dispositivo;
        idDispositivo = mensaje.optString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        confDisp = new configuracionDispositivos(contexto);
        dispositivo = confDisp.getDispositivoPorId(idDispositivo);
        dispositivoIotTermostato dispositivoTermometroTermostato;
        dispositivoTermometroTermostato = new dispositivoIotTermostato(dispositivo);
        ESTADO_RELE estadoRele = dialogo.getEstadoRele(texto);
        dispositivoTermometroTermostato.setEstadoRele(estadoRele);
        dispositivoTermometroTermostato.setUmbralTemperatura(dialogo.getUmbralTemperatura(texto));
        dispositivoTermometroTermostato.setTemperatura(dialogo.getTemperatura(texto));
        dispositivoTermometroTermostato.setHumedad(dialogo.getHumedad(texto));
        dispositivoTermometroTermostato.setEstadoDispositivo(dialogo.getEstadoDispositivo(texto));
        dispositivoTermometroTermostato.setTipoDispositivo(dialogo.getTipoDispositivo(texto));
        dispositivoTermometroTermostato.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
        dispositivoTermometroTermostato.setEstadoDispositivo(dialogo.getEstadoDispositivo(texto));
        dispositivoTermometroTermostato.setProgramaActivo(dialogo.getProgramaActivo(texto));




        return dispositivoTermometroTermostato;

    }



    private dispositivoIotTermostato procesarEstadoDispositivoTermometroTermostato(String topic, String texto, Context contexto) {
        JSONObject mensaje;
        String idDispositivo;
        configuracionDispositivos confDisp;
        try {
            mensaje = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "El mensaje no es json");
            return null;

        }

        dispositivoIot dispositivo;
        idDispositivo = mensaje.optString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        confDisp = new configuracionDispositivos(contexto);
        dispositivo = confDisp.getDispositivoPorId(idDispositivo);
        dispositivoIotTermostato dispositivoTermometroTermostato;
        dispositivoTermometroTermostato = new dispositivoIotTermostato(dispositivo);
        ESTADO_RELE estadoRele = dialogo.getEstadoRele(texto);
        dispositivoTermometroTermostato.setEstadoRele(estadoRele);
        dispositivoTermometroTermostato.setUmbralTemperatura(dialogo.getUmbralTemperatura(texto));
        dispositivoTermometroTermostato.setTemperatura(dialogo.getTemperatura(texto));
        dispositivoTermometroTermostato.setHumedad(dialogo.getHumedad(texto));
        dispositivoTermometroTermostato.setEstadoDispositivo(dialogo.getEstadoDispositivo(texto));
        dispositivoTermometroTermostato.setTipoDispositivo(dialogo.getTipoDispositivo(texto));
        dispositivoTermometroTermostato.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
        dispositivoTermometroTermostato.setEstadoDispositivo(dialogo.getEstadoDispositivo(texto));
        dispositivoTermometroTermostato.setProgramaActivo(dialogo.getProgramaActivo(texto));
        dispositivoTermometroTermostato.setMargenTemperatura(dialogo.getmargenTemperatura(texto));
        dispositivoTermometroTermostato.setIntervaloLectura(dialogo.getIntervaloLectura(texto));
        dispositivoTermometroTermostato.setReintentoLectura(dialogo.getReintentosLectura(texto));
        dispositivoTermometroTermostato.setIntervaloReintentos(dialogo.getIntervaloReintentos(texto));
        dispositivoTermometroTermostato.setValorCalibrado(dialogo.getCalibradoTemperatura(texto));
        dispositivoTermometroTermostato.setSensorMaster(dialogo.isSensorMaster(texto));
        dispositivoTermometroTermostato.setIdSensor(dialogo.getIdSensorRemoto(texto));
        dispositivoTermometroTermostato.setUmbralTemperaturaDefecto(dialogo.getUmbralTemperaturaDefecto(texto));


        return dispositivoTermometroTermostato;

    }

    private ArrayList<ProgramaDispositivoIotOnOff> procesarConsultaProgramaInterruptor(String texto, Context contexto) {

        JSONObject mensaje;

        dispositivoIotOnOff dispositivo;
        dispositivo = new dispositivoIotOnOff();
        return (dispositivo.cargarProgramas(texto));

    }


    private ArrayList<ProgramaDispositivoIotTermostato> procesarConsultaProgramaTermostato(String texto, Context contexto) {

        JSONObject mensaje;

        dispositivoIotTermostato dispositivo;
        dispositivo = new dispositivoIotTermostato();

        return (dispositivo.cargarProgramas(texto));

    }

    private void procesarInformacionOta(String texto, Context contexto) {

        JSONObject mensaje;

        try {
            mensaje = new JSONObject(texto);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private double procesarUmbralTemperatura(String texto, Context contexto) {

        JSONObject mensaje;
        double umbral;
        configuracionDispositivos confDisp;
        try {
            mensaje = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "El mensaje no es json");
            return -1;

        }

        umbral = dialogo.extraerDatoJsonDouble(texto, TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson());
        return umbral;

    }



}
