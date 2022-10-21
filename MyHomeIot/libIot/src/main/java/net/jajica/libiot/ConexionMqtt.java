package net.jajica.libiot;


import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Timer;
import java.util.TimerTask;


public class ConexionMqtt implements Serializable {

    protected final String TAG = "conexionMqtt";
    private MqttAndroidClient client;
    private Context context;
    private IMqttToken token;
    private IMqttAsyncClient a;
    private CountDownTimer timer;
    private MqttConnectOptions options;
    private ConfiguracionConexionMqtt cnx;
    private String idClient;
    private String stringConnection;
    private MQTT_STATE_CONNECTION stateConnection;
    private OnConexionMqtt listenerConexion;
    private OnArrivedMessage listenerMessage;
    private OnSubscriptionTopic listenerTopic;
    private Boolean connectionCreated;

    public Boolean getConnectionCreated() {
        return connectionCreated;
    }

    public void setConnectionCreated(Boolean connectionCreated) {
        this.connectionCreated = connectionCreated;
    }

    public OnConexionMqtt getListenerConexion() {
        return listenerConexion;
    }

    public void setListenerConexion(OnConexionMqtt listenerConexion) {
        this.listenerConexion = listenerConexion;
    }

    public OnArrivedMessage getListenerMessage() {
        return listenerMessage;
    }

    public void setListenerMessage(OnArrivedMessage listenerMessage) {
        this.listenerMessage = listenerMessage;
    }

    public OnSubscriptionTopic getListenerTopic() {
        return listenerTopic;
    }

    public void setListenerTopic(OnSubscriptionTopic listenerTopic) {
        this.listenerTopic = listenerTopic;
    }

    /**
     * Interface para redefinicion de
     */
    public interface OnConexionMqtt {

        void conexionEstablecida(boolean reconnect, String serverURI);
        void conexionPerdida(Throwable cause);

    }

    public interface OnArrivedMessage {
        void arrivedMessage(String topic, MqttMessage message);
    }

    public interface OnSubscriptionTopic {

        void conExito(IMqttToken iMqttToken);
        void sinExito(IMqttToken iMqttToken, Throwable throwable);

    }




    public MQTT_STATE_CONNECTION getStateConnection() {
        return stateConnection;
    }

    public void setStateConnection(MQTT_STATE_CONNECTION stateConnection) {
        this.stateConnection = stateConnection;
        Log.w(TAG, "Se ha cambiado el estado de conexion a " + getStateConnection().toString());
    }

    /**
     * En el propio constructor, se carga la configuracion para la conexion. Tambien implementa
     * las opciones de la conexion.
     * @param context Contexto de la aplcacion, y necesario para crear la conexion mqtt.
     *
     */
    public ConexionMqtt(Context context) {

        client = null;
        this.context = context;
        token = null;
        timer = null;
        cnx = null;
        stateConnection = MQTT_STATE_CONNECTION.CONEXION_MQTT_DESCONECTADA;
        cnx = new ConfiguracionConexionMqtt(this.context);
        options = new MqttConnectOptions();
        options.setAutomaticReconnect(cnx.getAutoConnect());
        options.setCleanSession(cnx.getCleanSession());
        options.setConnectionTimeout(cnx.getConnectionTimeout());
        options.setMqttVersion(cnx.getMqttVersion());
        options.setHttpsHostnameVerificationEnabled(cnx.getHostnameVerification());
        idClient = MqttClient.generateClientId();
        setConnectionCreated(false);
        //idClient = UUID.randomUUID().toString();
        if (cnx.getTls()) {
            stringConnection = "ssl://" + cnx.getBrokerId() + ":" + cnx.getPuerto();
        } else {
            stringConnection = "tcp://" + cnx.getBrokerId() + ":" + cnx.getPuerto();
        }
    }

    public MQTT_STATE_CONNECTION createConnetion(OnConexionMqtt listenerConexionMqtt) {


        client = new MqttAndroidClient(context, stringConnection, idClient);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE);
                //Informamos de la conexion establecida a la aplicacion
                listenerConexionMqtt.conexionEstablecida(reconnect, serverURI);

            }

            @Override
            public void connectionLost(Throwable cause) {
                //Informamos cuando se pierde la conexion
                setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_LOST);
                listenerConexionMqtt.conexionPerdida(cause);

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (getStateConnection() != MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
                    setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE);
                }

                String datos = new String(message.getPayload());
                Log.d(TAG, topic +" : " + datos);
                listenerMessage.arrivedMessage(topic, message);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE);

            }
        });
        if (cnx.getTls()) {
            SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
            socketFactoryOptions.withCaInputStream(context.getResources().openRawResource(R.raw.certificado));
            try {
                options.setSocketFactory(new SocketFactory(socketFactoryOptions));
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_ALGORITMO_CERTIIFICADO;
            } catch (IOException e) {
                e.printStackTrace();
                return MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_IO;
            } catch (KeyManagementException e) {
                e.printStackTrace();
                return MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_KEY_MANAGEMENT;
            } catch (CertificateException e) {
                e.printStackTrace();
                return MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_CERTIFICATE;
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
                return MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_IRRECUPERABLE;
            }
        }

        //connectionRetry(20000, 1000);
        retryConnection(10000, 10000);







        setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO);
        return MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO;
    }


    private MQTT_STATE_CONNECTION connect() {


        try {
            token = client.connect(options, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_FAIL);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_CONNECT);
            return MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_CONNECT;
        }


        setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO);
        return MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO;
    }

    /**
     * Publica un mensaje de texto a traves del topic suministrado
     * @param topic sobre el que enviar el mensaje
     * @param texto es el mensaje a enviar
     */
    public DEVICE_STATE_CONNECTION publicarTopic(String topic, String texto) {

        MqttMessage mensaje;

        if (client == null) {
            Log.e(TAG, "el cliente mqtt es nulo");
        }
        if (stateConnection == MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
            mensaje = new MqttMessage();
            mensaje.setPayload(texto.getBytes());
            try {
                client.publish(topic, mensaje);
                Log.w(TAG, topic + ": " + texto);
            } catch (MqttException e) {
                e.printStackTrace();
                return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
            }

        } else {
            Log.w(TAG, "no estoy conectado y no puedo publica");
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
    }


    /**
     * Esta funcion suscribe a un dispoisitivo a un topic del broker
     * @param topic Es el topic por donde escuchara el dispositivo
     * @param listener Se dispara si finalmente no se puede suscribir el dispositivo
     * @return Restorna el resultado de la operacion
     */
    public MQTT_STATE_CONNECTION subscribirTopic(String topic, OnSubscriptionTopic listener) {

        Log.w(TAG, "topic " + topic);
        if (stateConnection == MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
            try {
                client.subscribe(topic, 0, context, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        Log.w(TAG, "subscrito al topic " + topic);
                        listener.conExito(iMqttToken);
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        Log.w(TAG, "subscrito sin exito al topic " + topic);
                        listener.sinExito(iMqttToken, throwable);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

        } else {
            Log.w(TAG, "No estas conectado al broker");
            stateConnection = MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_SUSCRIPCION_TOPIC;
        }
        return stateConnection;

    }

    public MQTT_STATE_CONNECTION retryConnection(long timeRetry, long notifyInterval) {

        Timer timerConnection;
        connect();
        timerConnection = new Timer();
        timerConnection.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getStateConnection() == MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
                   this.cancel();
                   Log.i(TAG, "SE CANCELA EL TEMPORIZADOR...");
                } else {
                    Log.e(TAG, "Se reintenta conexion");
                    connect();
                }

            }
        }, timeRetry,notifyInterval);



        return getStateConnection();

    }



    public MQTT_STATE_CONNECTION connectionRetry(long tiempoReintento, long intervaloNotificacion) {

        CountDownTimer temporizador;
        connect();
        temporizador = new CountDownTimer(tiempoReintento, intervaloNotificacion) {
            @Override
            public void onTick(long l) {
                if(getStateConnection() == MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
                    this.cancel();
                    Log.i(TAG, "SE CANCELA EL TEMPORIZADOR");
                } else {
                    Log.i(TAG, "EL TIEMPO PASA");
                }

            }

            @Override
            public void onFinish() {
                if(getStateConnection() != MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
                    Log.e(TAG, "NO SE CONSIGUE CONECTAR, SE REINTENTA");
                    connect();
                }

            }
        };

        temporizador.start();

        return getStateConnection();

    }






}
