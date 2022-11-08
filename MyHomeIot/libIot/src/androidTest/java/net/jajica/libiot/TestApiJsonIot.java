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

    IotMqttConnection cnx;
    Context appContext;

    public void prepararEntorno() {




    }


    @Test
    public void TestGenerarComando() {

        IOT_MQTT_STATUS_CONNECTION estado;
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new IotMqttConnection(appContext);
        estado = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });
        JSONObject cabecera = null;
        String texto;
        IotTools api;

        if (estado == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO) {
            api = new IotTools();
            assertNotNull(texto = api.createSimpleCommand(IOT_COMMANDS.STATUS_DEVICE));
            Log.i("texto", texto);


        }

    }

    @Test
    public void TextActuarRele() {
        IOT_MQTT_STATUS_CONNECTION estado;
        Context appContext;
        IotMqttConnection cnx;
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cnx = new IotMqttConnection(appContext);
        IotDeviceSwitch disp;
        disp = new IotDeviceSwitch();

        disp.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });

        disp.setOnReceivedSetRelay(new IotDeviceSwitch.OnReceivedSetRelay() {
            @Override
            public void onReceivedSetRelay(IOT_CODE_RESULT codeResult) {
                Log.i("test", "recibido setRelay" + disp.getRelay());
            }
        });
        disp.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                Log.i("test", "recibido StartDevice" + disp.getRelay());
            }
        });

        disp.setOnReceivedSpontaneousActionRelay(new IotDeviceSwitch.OnReceivedSpontaneousActionRelay() {
            @Override
            public void onReceivedSpontaneousActionRelay(IOT_CODE_RESULT resultCode) {
                Log.i("test", "recibido actionRelay" + disp.getRelay());
            }
        });


        estado = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                disp.setCnx(cnx);
                disp.setDeviceId("A020A6026046");
                disp.setDeviceName("test");
                disp.subscribeDevice();
                //disp.RegisterListenerMqttConnection();
                disp.setRelayCommand(IOT_SWITCH_RELAY.OFF);

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        disp.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                Log.e("test", "Temporizado comando con clave: " + token);
            }
        });











    }

}
