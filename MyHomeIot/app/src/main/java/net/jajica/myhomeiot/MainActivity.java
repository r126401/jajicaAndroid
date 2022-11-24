package net.jajica.myhomeiot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import net.jajica.libiot.IOT_CLASS_SCHEDULE;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IotScheduleDeviceThermostat;

import org.json.JSONException;
import org.json.JSONObject;


import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.ActivityMainBinding;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private IotUsersDevices configuration;
    private IotMqttConnection cnx;
    private APPLICATION_STATUS appStatus;
    private ActivityMainBinding mbinding;



    /**
     * Establece la toolbar como action bar
     */
    private void setToolbar() {
        setSupportActionBar(mbinding.toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        mbinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());



        //testUsers();
        initApplication();
        mbinding.tabs.addTab(mbinding.tabs.newTab().setText("Salon"));
        mbinding.tabs.addTab(mbinding.tabs.newTab().setText("Patio"));
        mbinding.tabs.addTab(mbinding.tabs.newTab().setText("Baño"));
        mbinding.tabs.addTab(mbinding.tabs.newTab().setText("..."));
        TabLayout.Tab tab =mbinding.tabs.getTabAt(1);
        tab.setIcon(R.drawable.ic_menu);
        mbinding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "Se selecciona el tab");

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(TAG, "Se deselecciona el tab");

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i(TAG, "Se selecciona cuando ya esta seleccionado");

            }
        });

        mbinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //setToolbar();


        Log.i(TAG, "HOLA");



    }


    private APPLICATION_STATUS initApplication() {

        IOT_OPERATION_CONFIGURATION_DEVICES result;

        result = loadConfiguration();

        switch (result) {

            case OK_CONFIGURATION:
                appStatus = APPLICATION_STATUS.APPLICATION_OK;
                break;
            case CORRUPTED_CONFIGURATION:
            case NO_CONFIGURATION:
                appStatus = APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
                break;

        }

        makeConnect();


        return APPLICATION_STATUS.APPLICATION_OK;
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

        IOT_OPERATION_CONFIGURATION_DEVICES result;
        configuration = new IotUsersDevices(getApplicationContext());
        return configuration.loadConfiguration();

    }

    private IOT_MQTT_STATUS_CONNECTION makeConnect() {

        IOT_MQTT_STATUS_CONNECTION state;
        cnx = new IotMqttConnection(getApplicationContext());
        state = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion estabilizada");

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        return state;

    }




    




}