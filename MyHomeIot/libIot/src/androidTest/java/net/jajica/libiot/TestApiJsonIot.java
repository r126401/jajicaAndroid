package net.jajica.libiot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestApiJsonIot {

    MqttConnection cnx;
    Context appContext;

    public void TextApiJson() {





    }

    @Test
    public void TestGenerarComando() {

        MQTT_STATE_CONNECTION estado;
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new MqttConnection(appContext);
        estado = cnx.createConnetion(new MqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });
        JSONObject cabecera = null;
        String texto;
        ApiDispositivoIot api;

        if (estado == MQTT_STATE_CONNECTION.CONEXION_MQTT_CON_EXITO) {
            api = new ApiDispositivoIot(cnx);
            assertNotNull(texto = api.createSimpleCommand(IOT_COMMANDS.STATUS_DEVICE));
            Log.i("texto", texto);


        }




    }
}
