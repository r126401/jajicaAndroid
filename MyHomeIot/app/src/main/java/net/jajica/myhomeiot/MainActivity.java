package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import net.jajica.libiot.IOT_CLASS_SCHEDULE;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotDeviceUnknown;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IotScheduleDeviceThermostat;

import org.json.JSONException;
import org.json.JSONObject;


import net.jajica.libiot.IotConfigurationDevices;
import net.jajica.myhomeiot.databinding.ActivityMainBinding;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private IotConfigurationDevices configuration;



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
                termometro.commandGetStatusDevice();
                termometro.getOtaVersionAvailableCommand();
                JSONObject objeto;
                objeto = new JSONObject();
                try {
                    objeto.put(IOT_LABELS_JSON.READ_INTERVAL.getValorTextoJson(), 30);
                    objeto.put(IOT_LABELS_JSON.MARGIN_TEMPERATURE.getValorTextoJson(), 0.5);
                    objeto.put(IOT_LABELS_JSON.READ_NUMBER_RETRY.getValorTextoJson(), 3);
                    objeto.put(IOT_LABELS_JSON.RETRY_INTERVAL.getValorTextoJson(), 30);
                    objeto.put(IOT_LABELS_JSON.CALIBRATE_VALUE.getValorTextoJson(), -3.5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                termometro.commandSetParametersDevice(objeto);



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

        termometro.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {

            }
        });

        termometro.setOnOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibida version Ota disponible " + termometro.getDeviceId());
            }
        });

        termometro.setOnReceivedModifyParametersDevice(new IotDevice.OnReceivedModifyParametersDevice() {
            @Override
            public void onReceivedMofifyParametersDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibido modificacion de parametros" + termometro.getDeviceId());
            }
        });

        termometro.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "se ha iniciado el dispositivo" + termometro.getDeviceId());
            }
        });




    }


    public void testTermostato() {

        IotMqttConnection cnx;
        cnx = new IotMqttConnection(getApplicationContext());
        IotDeviceThermostat termostato;
        termostato = new IotDeviceThermostat();
        cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "conexion establecida");
                termostato.setCnx(cnx);
                termostato.setDeviceId("943CC64E4928");
                termostato.setDeviceName("cronotermostato");

                termostato.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
                    @Override
                    public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "comienzo de aplicacion " + termostato.getDeviceId());
                    }
                });

                termostato.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermostat.OnReceivedSpontaneousChangeTemperature() {
                    @Override
                    public void onReceivedSpontaneousChangeTemperature() {

                        Log.i(TAG, "Recibido cambio de temperatura" + termostato.getTemperature());
                    }
                });
                termostato.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
                    @Override
                    public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Comienza el schedule " + termostato.getTemperature());
                    }
                });

                termostato.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
                    @Override
                    public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Fin del programa" + termostato.getTemperature());

                    }
                });

                termostato.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
                    @Override
                    public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Recibido Status" + termostato.getTemperature());
                    }
                });

                termostato.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
                    @Override
                    public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Recibido schedule" + termostato.getTemperature());
                        IotScheduleDeviceThermostat schedule;
                        schedule = new IotScheduleDeviceThermostat();
                        schedule.setScheduleType(IOT_CLASS_SCHEDULE.DIARY_SCHEDULE);
                        schedule.setHour(23);
                        schedule.setMinute(50);
                        schedule.setSecond(0);
                        schedule.setMask(127);
                        schedule.setRelay(IOT_SWITCH_RELAY.ON);
                        schedule.setScheduleState(IOT_STATE_SCHEDULE.PROGRAMA_ACTIVO);
                        schedule.createScheduleIdFromObject();
                        schedule.setThresholdTemperature(28.5);
                        //schedule.setRawScheduleFromObject();
                        schedule.setDuration(3620);

                        termostato.commandNewScheduleDevice(schedule);
                    }
                });

                termostato.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
                    @Override
                    public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "se Ha recibido respuesta de la creacion " + termostato.getTemperature() +  "-" + resultCode);
                        IotScheduleDeviceThermostat sch;
                        sch = termostato.getSchedulesThermostat().get(1);
                        sch.setHour(21);
                        sch.setMinute(23);
                        sch.setSecond(33);
                        termostato.commandModifyScheduleDevice(sch);
                    }
                });

                termostato.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
                    @Override
                    public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Recibido con codigo " + termostato.getTemperature() + resultCode.toString());
                        IotScheduleDeviceThermostat sch;
                        sch = termostato.getSchedulesThermostat().get(1);
                        termostato.commandDeleteScheduleDevice(sch.getScheduleId());

                    }
                });

                termostato.setOnReceivedDeleteDevice(new IotDevice.OnReceivedDeleteScheduleDevice() {
                    @Override
                    public void onReceivedDeleteScheduleDevice(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Borrado el dispositivo " + termostato.getDeviceId() + "con codigo" +  resultCode);
                        termostato.commandSetThresholdTemperarture(32.5);
                    }
                });

                termostato.setOnReceivedSetThresholdTemperature(new IotDeviceThermostat.OnReceivedSetThresholdTemperature() {
                    @Override
                    public void onReceivedSetThresholdTemperature(IOT_CODE_RESULT resultCode) {
                        Log.i(TAG, "Recibido nuevo umbral " + termostato.getThresholdTemperature());
                    }
                });



                termostato.subscribeDevice();
                termostato.subscribeOtaServer();
                termostato.commandGetStatusDevice();
                termostato.commandGetScheduleDevice();




            }


            @Override
            public void connectionLost(Throwable cause) {

            }
        });








    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityMainBinding binding;




        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadConfiguration();
        newDevice();
        Log.i(TAG, "HOLA");
        //setContentView(R.layout.activity_main);



        //TextActuarRele();
        //testTermometro();
        //testTermostato();

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


        disp.commandNewScheduleDevice(schedule);
    }

    private IOT_OPERATION_CONFIGURATION_DEVICES loadConfiguration() {

        IOT_OPERATION_CONFIGURATION_DEVICES op;
        configuration = new IotConfigurationDevices(getApplicationContext());
        if ((op = configuration.loadIotDevices()) != IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION) {
            return op;
        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION;
    }

    private IOT_OPERATION_CONFIGURATION_DEVICES newDevice() {

        IOT_OPERATION_CONFIGURATION_DEVICES op;
        IotDevice device;
        device = new IotDeviceUnknown();
        device.setDeviceId("A020A6026046");
        device.setDeviceName("esp8266Switch");
        device.setRoom("Casa");
        device.setSite("despacho");
        device.setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        device.setPublishTopic("sub_A020A6026046");
        device.setSubscriptionTopic("pub_A020A6026046");
        op =configuration.insertIotDevice(device);
        return op;
    }
    



}