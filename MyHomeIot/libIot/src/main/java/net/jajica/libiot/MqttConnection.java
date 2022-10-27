package net.jajica.libiot;


import android.content.Context;
import android.util.Log;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
//import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Esta clase implementa la logica de conexion mqtt con la aplicacion apoyandose en la libreria
 * paho de eclipse.
 * Para poder realizar una conexion, es necesario:
 * 1.- Llamar al constructor pasandole el contexto de la aplicacion context.
 * 2.- El constructor se apoya en la clase ConfigurationMqtt en la que se guardan todos los parametros
 * para realizar la conexion.
 * 3.- Se crea la conexion llamando al metodo createConnetion siendo necesario crear el listener para
 * recibir los eventos de la comunicacion, excepto el de los mensajes que lleguen, los cuales se hará tratamiento especial
 * 5.- Una vez que la conexion esta activa CONEXION_MQTT_ACTIVE, se pueden utilizar los metodos publishTopic y subscribeTopic
 *
 */
public class MqttConnection implements Serializable {

    protected final String TAG = "MqttConnection";
    protected MqttAndroidClient client;
    private final Context context;
    protected IMqttToken token;
    protected MqttConnectOptions options;
    protected ConfigurationMqtt cnx;
    protected String idClient;
    protected String stringConnection;
    protected MQTT_STATE_CONNECTION stateConnection;
    protected OnMqttConnection onListenerConnection;
    protected OnArrivedMessage onListenerArrivedMessaged;
    protected OnSubscriptionTopic onListenerSubscribe;
    protected ArrivedMessage elemento;
    protected ArrayList<ArrivedMessage> lista;





    /**
     * Este método crea un listener para controlar los eventos de la conexion
     * @param onListenerConnection es el listener en uso
     */

    public void setOnListenerConnection(OnMqttConnection onListenerConnection) {
        this.onListenerConnection = onListenerConnection;
    }




    /**
     * Este metodo crea un listener para recibir los eventos que llegan desde la conexion mqtt
     * @param onListenerArrivedMessaged es el listener en uso
     */
    /*
    public void setOnListenerArrivedMessaged(OnArrivedMessage onListenerArrivedMessaged) {
        this.onListenerArrivedMessaged = onListenerArrivedMessaged;
    }
*/

    public void setOnListenerArrivedMessaged(String subscriptionTopic, OnArrivedMessage onListenerArrivedMessaged) {

        ArrivedMessage elemento;
        elemento = new ArrivedMessage();
        elemento.listener = onListenerArrivedMessaged;
        elemento.topic = subscriptionTopic;
        if (lista == null) {
            lista = new ArrayList<>();
        }
        lista.add(elemento);




    }

    /**
     * Este metodo crea un listener para controlar el resultado final de la subscripcion
     * @param onListenerSubscribe es el listener en uso
     */
    public void setOnListenerSubscribe(OnSubscriptionTopic onListenerSubscribe) {
        this.onListenerSubscribe = onListenerSubscribe;
    }

    /**
     * Interface utilizado para controlar los eventos de la conexion
     */
    public interface OnMqttConnection {

        void connectionEstablished(boolean reconnect, String serverURI);
        void connectionLost(Throwable cause);

    }

    /**
     * Se utiliza para controlar los eventos que llegan
     */

    public interface OnArrivedMessage {

        void arrivedMessage(String topic, MqttMessage message);

    }

    /**
     * Se utiliza para controlar el resultado final de las subscripcion a un topic
     */
    public interface OnSubscriptionTopic {

        void Unsuccessful(IMqttToken iMqttToken);
        void Successful(IMqttToken iMqttToken, Throwable throwable);

    }

    /**
     * Este metodo devuelve el estado de la conexion
     * @return un estado MQTT_STATE_CONNECTION
     */
    public MQTT_STATE_CONNECTION getStateConnection() {
        return stateConnection;
    }

    /**
     * Este metodo actualiza el estado de la conexion para hacerlo coherente
     * @param stateConnection
     */
    public void setStateConnection(MQTT_STATE_CONNECTION stateConnection) {
        this.stateConnection = stateConnection;
        Log.i(TAG, "Se ha cambiado el estado de conexion a " + getStateConnection().toString());
    }

    /**
     * En el propio constructor, se carga la configuracion para la conexion. Tambien implementa
     * las opciones de la conexion.
     * @param context Contexto de la aplcacion, y necesario para crear la conexion mqtt.
     *
     */
    public MqttConnection(Context context) {

        client = null;
        this.context = context;
        token = null;
        cnx = null;
        stateConnection = MQTT_STATE_CONNECTION.CONEXION_MQTT_DESCONECTADA;
        cnx = new ConfigurationMqtt(this.context);
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

    /**
     * Este metodo crea la conexion Mqtt. Es necesario crear un listener para controlar el estado
     * de dicha conexion.
     * @param listenerConexionMqtt es el listener usado por la aplicacion para la conexion
     * @return Devuelve el resultado de la conexion aunque es necesario esperar el evento connectComplete
     * para asegurar que la conexion esta estabilizada y es el punto de inicio del dialogo
     */
    public MQTT_STATE_CONNECTION createConnetion(OnMqttConnection listenerConexionMqtt) {


        client = new MqttAndroidClient(context, stringConnection, idClient, Ack.AUTO_ACK);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE);
                //Informamos de la conexion establecida a la aplicacion
                listenerConexionMqtt.connectionEstablished(reconnect, serverURI);

            }

            @Override
            public void connectionLost(Throwable cause) {
                //Informamos cuando se pierde la conexion
                setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_LOST);
                listenerConexionMqtt.connectionLost(cause);

            }

            @Override
            public void messageArrived(String topic, MqttMessage message)  {
                if (getStateConnection() != MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
                    setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE);
                }

                String datos = new String(message.getPayload());
                Log.d(TAG, topic +" : " + datos);
                //onListenerArrivedMessaged.arrivedMessage(topic, message);
                int i = searchListenerArriveMessage(topic);
                onListenerArrivedMessaged = lista.get(i).listener;
                onListenerArrivedMessaged.arrivedMessage(topic, message);

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


    private void connect() {


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


        setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO);
    }


    /**
     * Publica un mensaje de texto a traves del topic suministrado
     * @param topic sobre el que enviar el mensaje
     * @param texto es el mensaje a enviar
     */
    public DEVICE_STATE_CONNECTION publishTopic(String topic, String texto) {

        MqttMessage mensaje;

        if (client == null) {
            Log.e(TAG, "el cliente mqtt es nulo");
        }
        if (stateConnection == MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
            mensaje = new MqttMessage();
            mensaje.setPayload(texto.getBytes());
            client.publish(topic, mensaje);
            Log.w(TAG, topic + ": " + texto);

        } else {
            Log.w(TAG, "no estoy conectado y no puedo publica");
            return DEVICE_STATE_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        return DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
    }


    /**
     * Esta funcion subscribe a un dispoisitivo a un topic del broker
     * @param topic Es el topic por donde escuchara el dispositivo
     * @param listener Se dispara si finalmente no se puede suscribir el dispositivo
     * @return Restorna el resultado de la operacion
     */
    public MQTT_STATE_CONNECTION subscribeTopic(String topic, OnSubscriptionTopic listener) {

        Log.w(TAG, "topic " + topic);
        if (stateConnection == MQTT_STATE_CONNECTION.CONEXION_MQTT_ACTIVE) {
            client.subscribe(topic, 0, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.w(TAG, "subscrito al topic " + topic);
                    listener.Unsuccessful(iMqttToken);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.w(TAG, "subscrito sin exito al topic " + topic);
                    listener.Successful(iMqttToken, throwable);
                }
            });

        } else {
            Log.w(TAG, "No estas conectado al broker");
            stateConnection = MQTT_STATE_CONNECTION.CONEXION_MQTT_ERROR_SUSCRIPCION_TOPIC;
        }

        return stateConnection;

    }

    private MQTT_STATE_CONNECTION retryConnection(long timeRetry, long notifyInterval) {

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

    /**
     * Este metodo cierra la conexion definitivamente
     *
     */
    public void closeConnection() {
        client.disconnect();
        client.close();

        setStateConnection(MQTT_STATE_CONNECTION.CONEXION_MQTT_DESCONECTADA);
    }

    public Boolean isConnected() {

        return client.isConnected();

    }

    protected int searchListenerArriveMessage(String topic) {

        int i;
        if (lista == null) {
            return -1;
        }
        for (i=0;i<lista.size();i++) {
            if (topic.equals(lista.get(i).topic)) {
                return i;
            }

        }

        return -1;
    }




}