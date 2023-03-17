package net.jajica.libiot;


import static net.jajica.libiot.IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class TestIOTmQTTcONNECTION {

    Context appContext;
    IotMqttConnection cnx;
    final String TAG = "TestIOTmQTTcONNECTION";

    public TestIOTmQTTcONNECTION() {

        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new IotMqttConnection(appContext);


    }

    @Test
    public void conectarMqtt() {

        IOT_MQTT_STATUS_CONNECTION state;

        assertEquals(IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO, cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
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
