package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.libiot.MqttConnection;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.MQTT_STATE_CONNECTION;
import net.jajica.libiot.IOT_CODE_RESULT;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MqttConnection cnx;
        cnx = new MqttConnection(this.getApplicationContext());
        MQTT_STATE_CONNECTION estado;
        Log.e("hh", "Comenzamos...");
        estado = cnx.createConnetion(new MqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.e("hh", "establecida");
                ejemplo(cnx);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.i("hh", "hola");
            }
        });
        Log.i("hh", "hola");




    }

    public void ejemplo(MqttConnection cnx) {
        IotDeviceSwitch disp, disp2;
        disp2 = new IotDeviceSwitch(cnx);
        disp2.setDeviceName("Depuradora");
        disp2.setDeviceId("8CCE4EFC0915");
        disp = new IotDeviceSwitch(cnx);
        disp.setDeviceId("A020A6026046");
        disp.setDeviceName("test");
        disp.subscribeDevice();
        disp2.subscribeDevice();
        disp.recibirMensajes();
        disp2.recibirMensajes();

        disp2.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {

                Log.i(TAG, "Recibido status de depuradora: " + disp2.getDispositivoJson());
            }
        });
        disp2.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibido info de depuradora: " + disp2.getDispositivoJson());
            }
        });
        disp2.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
            @Override
            public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibida programacion de depuradora " +  disp2.getDeviceId());



                disp.modifyScheduleCommand(disp.getSchedules().get(1));
            }
        });

        Log.i(TAG, "Fin");

        disp.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT res) {
                Log.i(TAG, "Recibido status de test: " + disp.getDispositivoJson());
            }
        });
        disp.setOnErrorAnswerDevice(new IotDevice.OnErrorAnswerDevice() {
            @Override
            public void receivedErrorAnswerDevice(IOT_CODE_RESULT result) {
                Log.e(TAG, "Error timeout de test: " + result);
            }
        });

        disp.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibido info de test: " + disp.getDispositivoJson());
            }
        });

        disp.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
            @Override
            public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibido programas de test: " + disp.getDispositivoJson());
                IotScheduleDeviceSwitch sch;
                sch = disp.getSchedulesSwitch().get(1);
                sch.setHour(21);
                sch.setMinute(49);
                sch.setSecond(33);
                disp.modifyScheduleCommand(sch);
            }
        });

        disp.setOnReceivedDeleteDevice(new IotDevice.OnReceivedDeleteDevice() {
            @Override
            public void onReceivedDeleteDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibida respuesta a eliminar comando");
            }
        });

        disp.getStatusDeviceCommand();
        disp.getScheduleCommand();
        //disp.deleteScheduleCommand("002100007f");
        disp.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
            @Override
            public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibida respuesta a modificar comando");
            }
        });




        //disp2.getStatusDeviceCommand();





    }






}