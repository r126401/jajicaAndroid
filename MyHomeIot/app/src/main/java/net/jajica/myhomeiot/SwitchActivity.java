package net.jajica.myhomeiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.navigation.NavigationBarView;

import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_DEVICE_STATE;
import net.jajica.libiot.IOT_DEVICE_STATE_CONNECTION;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotMqttConnection;

import org.json.JSONException;
import org.json.JSONObject;

import net.jajica.libiot.IotOtaVersionAvailable;
import net.jajica.libiot.IotScheduleDeviceSwitch;

import net.jajica.myhomeiot.databinding.ActivitySwitchBinding;

public class SwitchActivity extends AppCompatActivity implements  NavigationBarView.OnItemSelectedListener, ActionSwitchScheduleFragment.OnActionSchedule {

    private final String TAG = "SwitchActivity";
    private IotDeviceSwitch device;
    private IotMqttConnection cnx;

    private ActivitySwitchBinding mbinding;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SwitchScheduleFragment switchScheduleFragment;
    private ActionSwitchScheduleFragment actionSwitchScheduleFragment;

    private Bundle bundleSchedule;



    private void initActivity() {
        JSONObject jsonObject = null;

        mbinding.bottomActionsSwitch.setOnItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException exception) {
            Log.e(TAG, "Error al recibir el json desde MainActivity");
        }


        device = new IotDeviceSwitch();
        if (data != null) {

            device.json2Object(jsonObject);
        }
        createConnectionMqtt();
        device.setCnx(cnx);
        configureListenersDeviceSwitch();
        updateDevice();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_switch);

        mbinding = ActivitySwitchBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());


        initActivity();


    }

    private void configureListenersDeviceSwitch() {


        //Configuramos los listeners de comandos.
        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "ff");
                updateDevice();

            }
        });
        device.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {

                paintOtaDataSwitch();
            }
        });

        device.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "kk");
            }
        });



    }

    private IOT_MQTT_STATUS_CONNECTION createConnectionMqtt() {

        IOT_MQTT_STATUS_CONNECTION status;
        cnx = new IotMqttConnection(getApplicationContext());

        status = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion establecida");
                device.subscribeDevice();
                device.subscribeOtaServer();
                device.commandGetStatusDevice();
                //device.commandGetScheduleDevice();
                updateDevice();
                device.getOtaVersionAvailableCommand();
                getSwitchSchedule();
            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        return status;

    }

    private void updateDevice() {

        IOT_SWITCH_RELAY statusRelay;

        mbinding.textDeviceInfoSwitch.setText(device.getDeviceName());
        if (device.getAlarms().activeAlarms()) {
            mbinding.imageAlarmSwitch.setVisibility(View.VISIBLE);
        } else {
            mbinding.imageAlarmSwitch.setVisibility(View.INVISIBLE);
        }


        //Pintamos si el dispositivo esta conectado o no
        paintDeviceStateSwitch();
        paintOtaDataSwitch();
        paintPanelProgressSchedule();
        paintRelayStatus();
        paintConnections();




    }

    private void paintBrokerStatus() {

        if (cnx.isConnected()) {
            mbinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_on);
        } else {
            mbinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_off);
        }

    }



    private void paintDeviceStateSwitch() {

        IOT_DEVICE_STATE status;

        status = device.getDeviceStatus();

        switch (status) {

            case NORMAL_AUTO:
            case NORMAL_AUTOMAN:
                if (device.getSchedulesSwitch()!= null) {
                    if (device.getSchedulesSwitch().size() > 0) {
                        mbinding.textStatusSwitch.setVisibility(View.VISIBLE);
                        mbinding.textStatusSwitch.setText(getResources().getString(R.string.auto));
                    } else {
                        mbinding.textStatusSwitch.setVisibility(View.INVISIBLE);
                    }

                } else {
                    mbinding.textStatusSwitch.setVisibility(View.INVISIBLE);
                }

                break;
            case NORMAL_MANUAL:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.manual));
                break;
            case NORMAL_ARRANCANDO:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.arrancando));
                break;
            case NORMAL_SIN_PROGRAMACION:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.no_programs));
                break;
            case UPGRADE_IN_PROGRESS:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.upgrade_in_progress));
                break;
            case NORMAL_SYNCHRONIZING:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.syncronizing));
                break;
            case WAITING_END_START:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.waiting_start));
                break;
            case START_BEFORE_FACTORY:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.start_before_Factory));
                break;
            case NORMAL_END_ACTIVE_SCHEDULE:
                break;
            case INDETERMINADO:
                mbinding.textStatusSwitch.setText(getResources().getString(R.string.unknown));
                break;
        }


    }



private void paintOtaDataSwitch() {

    IotOtaVersionAvailable otaVersionAvailable;

        otaVersionAvailable = device.getDataOta();
        if (otaVersionAvailable == null) {
            return;
        }

        if (otaVersionAvailable.isOtaVersionAvailable(device.getCurrentOtaVersion())) {
            mbinding.imageUpgradeFirmwareSwitch.setVisibility(View.VISIBLE);

        } else {
            mbinding.imageUpgradeFirmwareSwitch.setVisibility(View.INVISIBLE);
        }

    Log.i(TAG, "Recibidos datos Ota");


}

private void paintPanelProgressSchedule() {

        if (device.getActiveSchedule() == null) {
            mbinding.gridLayoutSchedule.setVisibility(View.INVISIBLE);
            return;
        }
        mbinding.gridLayoutSchedule.setVisibility(View.VISIBLE);
}

private void paintConnections() {

    switch (device.getConnectionState()) {

        case UNKNOWN:
            mbinding.progressOperationSwitch.setVisibility(View.INVISIBLE);
            break;
        case DEVICE_CONNECTED:
            mbinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_connect_ok);
            break;
        case DEVICE_DISCONNECTED:
            mbinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_connect_nok);
            break;
        case DEVICE_WAITING_RESPONSE:
            mbinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_waiting_response);
            break;
        case DEVICE_ERROR_COMMUNICATION:
        case DEVICE_NO_SUBSCRIPT:
        case DEVICE_ERROR_NO_SUBSCRIPT:
        case DEVICE_ERROR_SUBSCRIPTION:
            mbinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_error);
            break;

    }

    if (device.getConnectionState() == IOT_DEVICE_STATE_CONNECTION.DEVICE_WAITING_RESPONSE) {
        mbinding.progressOperationSwitch.setVisibility(View.VISIBLE);
    } else {
        mbinding.progressOperationSwitch.setVisibility(View.INVISIBLE);
    }

}

private void paintRelayStatus() {


    switch (device.getRelay()) {

        case OFF:
            mbinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_off);
            break;
        case ON:
            mbinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_on);
            break;
        case UNKNOWN:
            mbinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_unknown);
            break;
    }
}

private void getSwitchSchedule() {

        switchScheduleFragment = new SwitchScheduleFragment(device);
        fragmentTransaction.add(R.id.containerSwitch, switchScheduleFragment, "Schedule");
        fragmentTransaction.setReorderingAllowed(true);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();



}





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case (R.id.item_new_schedule):
                fragmentTransaction = fragmentManager.beginTransaction();
                actionSwitchScheduleFragment = new ActionSwitchScheduleFragment(null);
                actionSwitchScheduleFragment.setOnActionSchedule(this);
                fragmentTransaction.replace(R.id.containerSwitch, actionSwitchScheduleFragment, "FragmentActionSchedule");
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack("schedule");
                fragmentTransaction.commit();
                break;
        }
        return false;
    }

    @Override
    public void onActionSchedule(IotScheduleDeviceSwitch schedule, ActionSwitchScheduleFragment.OPERATION_SCHEDULE operationSchedule) {

        if (operationSchedule == ActionSwitchScheduleFragment.OPERATION_SCHEDULE.NEW_SCHEDULE) {
            device.commandNewScheduleDevice(schedule);
        }



    }
}