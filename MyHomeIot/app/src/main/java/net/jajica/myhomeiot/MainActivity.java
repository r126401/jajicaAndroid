package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import net.jajica.libiot.IOT_CLASS_SCHEDULE;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_STATE_SCHEDULE;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    public void TextActuarRele() {
        IOT_MQTT_STATUS_CONNECTION estado;
        Context appContext;
        IotMqttConnection cnx;
        appContext = getApplicationContext();
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
                disp.subscribeOtaServer();
                disp.setRelayCommand(IOT_SWITCH_RELAY.ON);

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        disp.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                Log.e(TAG, "Temporizado comando con clave: " + token);
            }
        });

        disp.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Se ha recibido un estatus " +  disp.getDeviceId());

            }
        });
        disp.setOnReceivedSetRelay(new IotDeviceSwitch.OnReceivedSetRelay() {
            @Override
            public void onReceivedSetRelay(IOT_CODE_RESULT codeResult) {
                Log.i(TAG, "Se ha recibido un set relay " +  disp.getDeviceId());
                disp.getOtaVersionAvailableCommand();
            }
        });

        disp.setOnOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Se ha recibido un set relay " +  disp.getDeviceId());
            }
        });











    }

    public void testTermometro() {

        IotMqttConnection cnx;
        cnx = new IotMqttConnection(getApplicationContext());
        IotDeviceThermometer termometro;
        termometro = new IotDeviceThermometer();
        cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "conexion establecida");
                termometro.setCnx(cnx);
                termometro.setDeviceId("4C75250214E6");
                termometro.setDeviceName("termometro");
                termometro.subscribeDevice();
                termometro.subscribeOtaServer();
                termometro.getStatusDeviceCommand();
                termometro.getOtaVersionAvailableCommand();



            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "conexion perdida");
            }
        });

        termometro.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibido status del termometro" +  termometro.getDeviceId());
            }
        });

        termometro.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibido Arranque del termometro" + termometro.getDeviceId());
            }
        });

        termometro.setOnReceivedChangeTemperature(new IotDeviceThermometer.OnReceivedChangeTemperature() {
            @Override
            public void onReceivedChangeTemperature() {
                Log.i(TAG, "Recibido cambio de temperatura " + termometro.getTemperature());
            }
        });

        termometro.setOnOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibida version Ota disponible " + termometro.getDeviceId());
            }
        });




    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TextActuarRele();
        testTermometro();

        /*



        IotMqttConnection cnx;
        IotDeviceSwitch disp;
        disp = new IotDeviceSwitch();

        cnx = new IotMqttConnection(this.getApplicationContext());
        IOT_MQTT_STATUS_CONNECTION estado;
        Log.e("hh", "Comenzamos...");
        estado = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.e("hh", "establecida");
                disp.setCnx(cnx);
                disp.setDeviceId("A020A6026046");
                disp.setDeviceName("test");
                disp.subscribeDevice();
                disp.recibirMensajes();
                ejemplo(disp);

            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.i("hh", "hola");
            }
        });
        Log.i("hh", "hola");




    }

    public void ejemplo(IotDeviceSwitch disp) {


        Log.i(TAG, "Fin");

        disp.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT res) {
                Log.i(TAG, "Recibido status de test: " + disp.getDispositivoJson());
                createSchedule(disp);
            }
        });
        disp.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(IOT_CODE_RESULT result) {
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
                sch.setMinute(23);
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
        //disp.getScheduleCommand();
        //disp.deleteScheduleCommand("002100007f");
        disp.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
            @Override
            public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibida respuesta a modificar comando " + disp.setCurrentOtaVersionFromReport());

            }
        });
        */




    }





    public void createSchedule(IotDeviceSwitch disp) {

        //IotDeviceSwitch disp;
        //disp = new IotDeviceSwitch(cnx);
        disp.setDeviceId("A020A6026046");
        disp.setDeviceName("test");
        disp.subscribeDevice();
        //disp.RegisterListenerMqttConnection();
        IotScheduleDeviceSwitch schedule;
        schedule = new IotScheduleDeviceSwitch();
        schedule.setScheduleType(IOT_CLASS_SCHEDULE.DIARY_SCHEDULE);
        schedule.setHour(9);
        schedule.setMinute(3);
        schedule.setSecond(0);
        schedule.setMask(127);
        schedule.setRelay(IOT_SWITCH_RELAY.ON);
        schedule.setScheduleState(IOT_STATE_SCHEDULE.PROGRAMA_ACTIVO);
        schedule.createScheduleIdFromObject();
        //schedule.setRawScheduleFromObject();
        schedule.setDuration(3600);
        disp.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });
        disp.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibida respuesta de new schedule: " +  disp.getDeviceId());
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    disp.setNewSchedule(schedule);

                }
            }
        });


        disp.newScheduleCommand(schedule);
    }




}