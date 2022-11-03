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

    public void prepararEntorno() {




    }


    @Test
    public void TestGenerarComando() {

        MQTT_STATE_CONNECTION estado;
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new MqttConnection(appContext);
        estado = cnx.createConnection(new MqttConnection.OnMqttConnection() {
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

    @Test
    public void TextActuarRele() {
        MQTT_STATE_CONNECTION estado;
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new MqttConnection(appContext);
        estado = cnx.createConnection(new MqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        IotDeviceSwitch disp;
        disp = new IotDeviceSwitch(cnx);
        disp.setDeviceId("A020A6026046");
        disp.setDeviceName("test");
        disp.subscribeDevice();
        disp.recibirMensajes();
        disp.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(IOT_CODE_RESULT result) {

            }
        });

        disp.setOnReceivedSetRelay(new IotDeviceSwitch.OnReceivedSetRelay() {
            @Override
            public void onReceivedSetRelay(IOT_CODE_RESULT codeResult) {
                Log.i("test", "recibido setRelay" + disp.getRelay());
            }
        });
        disp.setRelayCommand(IOT_SWITCH_RELAY.ON);





    }
}
