package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import net.jajica.libiot.COMANDO_IOT;
import net.jajica.libiot.MqttConnection;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.DEVICE_STATE_CONNECTION;
import net.jajica.libiot.MQTT_STATE_CONNECTION;
import net.jajica.libiot.RESULT_CODE;

import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MqttConnection cnx;
        cnx = new MqttConnection(getApplicationContext());
        MQTT_STATE_CONNECTION estado;
        Log.e("hh", "Comenzamos...");
        estado = cnx.createConnetion(new MqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.e("hh", "establecida");
                ejemplo(cnx);
                cnx.subscribeTopic("pepe", new MqttConnection.OnSubscriptionTopic() {
                    @Override
                    public void Unsuccessful(IMqttToken iMqttToken) {
                        Log.e("hh", "suscrito");
                    }

                    @Override
                    public void Successful(IMqttToken iMqttToken, Throwable throwable) {
                        Log.e("hh", "Error al suscribirse");
                    }
                });
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.i("hh", "hola");
            }
        });
        Log.i("hh", "hola");




    }

    public void ejemplo(MqttConnection cnx) {
        IotDeviceSwitch disp;
        disp = new IotDeviceSwitch(cnx);
        disp.setDeviceId("8CCE4EFC0915");
        disp.setDeviceName("Depuradora");
        DEVICE_STATE_CONNECTION estadoConexion;
        estadoConexion = disp.subscribeDevice();
        disp.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(RESULT_CODE res) {
                Log.i(TAG, "El resultado ha sido " + disp.getCurrentOtaVersion());
            }
        });
        Log.i(TAG, "Fin");

        disp.setOnErrorAnswerDevice(new IotDevice.OnErrorAnswerDevice() {
            @Override
            public void receivedErrorAnswerDevice(RESULT_CODE result) {
                Log.e(TAG, "Error: " + result);
            }
        });

        disp.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(RESULT_CODE resultCode) {
                Log.i(TAG, " recibido infodevice");
            }
        });

        disp.getStatusDeviceCommand();
        disp.getInfoDeviceCommand();


    }






}