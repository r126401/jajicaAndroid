package net.jajica.libiot;


import android.content.Context;
import android.os.CountDownTimer;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.UUID;


public class ConexionMqtt {

    private MqttAndroidClient client;
    private Context context;
    private IMqttToken token;
    private CountDownTimer timer;
    private MqttConnectOptions options;
    private ConfiguracionConexionMqtt cnx;
    private String idClient;
    private String stringConnection;
    private ESTADO_CONEXION_MQTT stateConnection;

    public ESTADO_CONEXION_MQTT getStateConnection() {
        return stateConnection;
    }

    public void setStateConnection(ESTADO_CONEXION_MQTT stateConnection) {
        this.stateConnection = stateConnection;
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
        stateConnection = ESTADO_CONEXION_MQTT.CONEXION_MQTT_DESCONECTADA;
        cnx = new ConfiguracionConexionMqtt(this.context);
        options = new MqttConnectOptions();
        options.setAutomaticReconnect(cnx.getAutoConnect());
        options.setCleanSession(cnx.getCleanSession());
        options.setConnectionTimeout(cnx.getConnectionTimeout());
        options.setMqttVersion(cnx.getMqttVersion());
        options.setHttpsHostnameVerificationEnabled(cnx.getHostnameVerification());
        idClient = MqttClient.generateClientId();
        //idClient = UUID.randomUUID().toString();
        if (cnx.getTls()) {
            stringConnection = "ssl://" + cnx.getBrokerId() + ":" + cnx.getPuerto();
        } else {
            stringConnection = "tcp://" + cnx.getBrokerId() + ":" + cnx.getPuerto();
        }
    }

    public ESTADO_CONEXION_MQTT connect() {


        client = new MqttAndroidClient(context, stringConnection, idClient);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_COMPLETA);
            }

            @Override
            public void connectionLost(Throwable cause) {
                setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_LOST);

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_ACTIVE);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_ACTIVE);

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
                return ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_ALGORITMO_CERTIIFICADO;
            } catch (IOException e) {
                e.printStackTrace();
                return ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_IO;
            } catch (KeyManagementException e) {
                e.printStackTrace();
                return ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_KEY_MANAGEMENT;
            } catch (CertificateException e) {
                e.printStackTrace();
                return ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_CERTIFICATE;
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
                return ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_IRRECUPERABLE;
            }
        }

        try {
            token = client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_CON_EXITO);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_FAIL);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            setStateConnection(ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_CONNECT);
            return ESTADO_CONEXION_MQTT.CONEXION_MQTT_ERROR_CONNECT;
        }
/*
        try {
            token.waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        */

        return ESTADO_CONEXION_MQTT.CONEXION_MQTT_COMPLETA;
    }















}
