package net.jajica.libiot;


import static net.jajica.libiot.DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class TestMqttConnection {

    Context appContext;
    MqttConnection cnx;
    final String TAG = "TestMqttConnection";

    public TestMqttConnection() {

        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new MqttConnection(appContext);


    }

    @Test
    public void conectarMqtt() {

        MQTT_STATE_CONNECTION state;

        assertEquals(MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO, cnx.createConnection(new MqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion estableciada");
                assertEquals(DEVICE_WAITING_RESPONSE, cnx.publishTopic("pepe", "hola"));
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "Conexion perdida");
            }
        }));

    }

    @Test
    public void publish() {
        conectarMqtt();
        assertEquals(DEVICE_WAITING_RESPONSE, cnx.publishTopic("pepe", "hola"));

    }


}
