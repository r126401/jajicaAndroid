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
 * 2.- El constructor se apoya en la clase IotMqttConfiguration en la que se guardan todos los parametros
 * para realizar la conexion.
 * 3.- Se crea la conexion llamando al metodo createConnetion siendo necesario crear el listener para
 * recibir los eventos de la comunicacion, excepto el de los mensajes que lleguen, los cuales se hará tratamiento especial
 * 5.- Una vez que la conexion esta activa CONEXION_MQTT_ACTIVE, se pueden utilizar los metodos publishTopic y subscribeTopic
 *
 */
public class IotMqttConnection implements Serializable {

    protected final String TAG = "IotMqttConnection";
    protected MqttAndroidClient client;
    private final Context context;
    protected IMqttToken token;
    protected MqttConnectOptions options;
    protected IotMqttConfiguration iotMqttConfiguration;
    protected String idClient;
    protected String stringConnection;
    protected IOT_MQTT_STATUS_CONNECTION stateConnection;
    protected OnMqttConnection onListenerConnection;
    protected OnArrivedMessage onListenerArrivedMessaged;
    protected OnSubscriptionTopic onListenerSubscribe;
    protected ArrivedMessage elemento;
    protected ArrayList<ArrivedMessage> lista;

    public IotMqttConfiguration getIotMqttConfiguration() {
        return iotMqttConfiguration;
    }

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

    public ArrivedMessage setOnListenerArrivedMessaged(String subscriptionTopic, OnArrivedMessage onListenerArrivedMessaged) {

        ArrivedMessage elemento;
        elemento = new ArrivedMessage();
        elemento.listener = onListenerArrivedMessaged;
        if (elemento.listTopics == null) {
            elemento.listTopics = new ArrayList<String>();
        }
        elemento.listTopics.add(subscriptionTopic);
        //elemento.listTopics = subscriptionTopic;
        if (lista == null) {
            lista = new ArrayList<>();
        }
        lista.add(elemento);
        return elemento;




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
        void Successful(IMqttToken iMqttToken);

    }

    /**
     * Este metodo devuelve el estado de la conexion
     * @return un estado IOT_MQTT_STATUS_CONNECTION
     */
    public IOT_MQTT_STATUS_CONNECTION getStateConnection() {
        return stateConnection;
    }

    /**
     * Este metodo actualiza el estado de la conexion para hacerlo coherente
     * @param stateConnection
     */
    public void setStateConnection(IOT_MQTT_STATUS_CONNECTION stateConnection) {
        this.stateConnection = stateConnection;
        Log.i(TAG, "Se ha cambiado el estado de conexion a " + getStateConnection().toString());
    }

    /**
     * En el propio constructor, se carga la configuracion para la conexion. Tambien implementa
     * las opciones de la conexion.
     * @param context Contexto de la aplcacion, y necesario para crear la conexion mqtt.
     *
     */
    public IotMqttConnection(Context context) {

        client = null;
        this.context = context;
        token = null;
        iotMqttConfiguration = null;
        stateConnection = IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_DESCONECTADA;
        iotMqttConfiguration = new IotMqttConfiguration(this.context);
        options = new MqttConnectOptions();
        options.setAutomaticReconnect(iotMqttConfiguration.getAutoConnect());
        options.setCleanSession(iotMqttConfiguration.getCleanSession());
        options.setConnectionTimeout(iotMqttConfiguration.getConnectionTimeout());
        options.setMqttVersion(iotMqttConfiguration.getMqttVersion());
        options.setHttpsHostnameVerificationEnabled(iotMqttConfiguration.getHostnameVerification());
        idClient = MqttClient.generateClientId();
        //idClient = UUID.randomUUID().toString();
        if (iotMqttConfiguration.getTls()) {
            stringConnection = "ssl://" + iotMqttConfiguration.getBrokerId() + ":" + iotMqttConfiguration.getPuerto();
        } else {
            stringConnection = "tcp://" + iotMqttConfiguration.getBrokerId() + ":" + iotMqttConfiguration.getPuerto();
        }
    }

    /**
     * Este metodo crea la conexion Mqtt. Es necesario crear un listener para controlar el estado
     * de dicha conexion.
     * @param listenerConexionMqtt es el listener usado por la aplicacion para la conexion
     * @return Devuelve el resultado de la conexion aunque es necesario esperar el evento connectComplete
     * para asegurar que la conexion esta estabilizada y es el punto de inicio del dialogo
     */
    public IOT_MQTT_STATUS_CONNECTION createConnection(OnMqttConnection listenerConexionMqtt) {


        client = new MqttAndroidClient(context, stringConnection, idClient, Ack.AUTO_ACK);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE);
                //Informamos de la conexion establecida a la aplicacion
                listenerConexionMqtt.connectionEstablished(reconnect, serverURI);

            }

            @Override
            public void connectionLost(Throwable cause) {
                //Informamos cuando se pierde la conexion
                setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_LOST);
                listenerConexionMqtt.connectionLost(cause);

            }

            @Override
            public void messageArrived(String topic, MqttMessage message)  {
                if (getStateConnection() != IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE) {
                    setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE);
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
                setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE);

            }
        });
        if (iotMqttConfiguration.getTls()) {
            SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
            socketFactoryOptions.withCaInputStream(context.getResources().openRawResource(R.raw.certificado));
            try {
                options.setSocketFactory(new SocketFactory(socketFactoryOptions));
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_ALGORITMO_CERTIIFICADO;
            } catch (IOException e) {
                e.printStackTrace();
                return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_IO;
            } catch (KeyManagementException e) {
                e.printStackTrace();
                return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_KEY_MANAGEMENT;
            } catch (CertificateException e) {
                e.printStackTrace();
                return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_CERTIFICATE;
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
                return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_IRRECUPERABLE;
            }
        }

        //connectionRetry(20000, 1000);
        retryConnection(10000, 10000);
        setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO);
        return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO;
    }


    private void connect() {


        token = client.connect(options, context, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE);

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_FAIL);

            }
        });


        setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO);
    }


    /**
     * Publica un mensaje de texto a traves del topic suministrado
     * @param topic sobre el que enviar el mensaje
     * @param texto es el mensaje a enviar
     */
    public IOT_DEVICE_STATUS_CONNECTION publishTopic(String topic, String texto) {

        MqttMessage mensaje;

        if ((client == null) || (topic == null)){
            Log.e(TAG, "el cliente mqtt o el topic es nulo");
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        if (stateConnection == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE) {
            mensaje = new MqttMessage();
            mensaje.setPayload(texto.getBytes());
            client.publish(topic, mensaje);
            Log.w(TAG, topic + ": " + texto);

        } else {
            Log.w(TAG, "no estoy conectado y no puedo publica");
            return IOT_DEVICE_STATUS_CONNECTION.DEVICE_ERROR_COMMUNICATION;
        }
        return IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
    }


    /**
     * Esta funcion subscribe a un dispoisitivo a un topic del broker
     * @param topic Es el topic por donde escuchara el dispositivo
     * @param listener Se dispara si finalmente no se puede suscribir el dispositivo
     * @return Restorna el resultado de la operacion
     */
    public IOT_MQTT_STATUS_CONNECTION subscribeTopic(String topic, OnSubscriptionTopic listener) {

        Log.w(TAG, "topic " + topic);
        if (topic == null) {
            return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_CONNECT;

        }
        if (stateConnection == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE) {
            client.subscribe(topic, 0, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.w(TAG, "subscrito al topic " + topic);

                    listener.Successful(iMqttToken);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.w(TAG, "subscrito sin exito al topic " + topic);
                    listener.Unsuccessful(iMqttToken);
                }
            });

        } else {
            Log.w(TAG, "No estas conectado al broker para el topic " + topic);
            stateConnection = IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_SUSCRIPCION_TOPIC;
        }

        return stateConnection;

    }

    private IOT_MQTT_STATUS_CONNECTION retryConnection(long timeRetry, long notifyInterval) {

        Timer timerConnection;
        connect();
        timerConnection = new Timer();
        timerConnection.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getStateConnection() == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE) {
                   this.cancel();
                   timerConnection.purge();
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
        client.unregisterResources();
        client.disconnect();
        //client.close();

        setStateConnection(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_DESCONECTADA);
    }

    public Boolean isConnected() {

        return client.isConnected();

    }

    protected int searchListenerArriveMessage(String topic) {

        int i;
        int j;
        /*
        if (lista == null) {
            return -1;
        }
        for (i=0;i<lista.size();i++) {
            if (topic.equals(lista.get(i).listTopics)) {
                return i;
            }

        }

         */
        for(i=0; i < lista.size(); i++) {

            if (lista.get(i).listTopics != null) {
                for(j=0;j<lista.get(i).listTopics.size(); j++) {
                    if (topic.equals(lista.get(i).listTopics.get(j))) {
                        return i;
                    }
                }
            }
        }




        return -1;
    }

    public IOT_MQTT_STATUS_CONNECTION unSubscribeTopic(String topic) {

        Log.w(TAG, "topic " + topic);
        if (topic == null) {
            return IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_CONNECT;

        }
        if (stateConnection == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ACTIVE) {
            client.unsubscribe(topic);


        } else {
            Log.w(TAG, "No estas conectado al broker");
            stateConnection = IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_ERROR_SUSCRIPCION_TOPIC;
        }

        return stateConnection;

    }





}
