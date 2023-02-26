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

public class SwitchActivity extends AppCompatActivity implements  NavigationBarView.OnItemSelectedListener, ActionSwitchScheduleFragment.OnActionSchedule, View.OnClickListener {

    private final String TAG = "SwitchActivity";
    private IotDeviceSwitch device;
    private IotMqttConnection cnx;

    private ActivitySwitchBinding mbinding;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SwitchScheduleFragment switchScheduleFragment;
    private ActionSwitchScheduleFragment actionSwitchScheduleFragment;

    private Bundle bundleSchedule;


    /**
     * Este metodo inicializa y recibe los datos desde mainActivity
     * Crea un nuevo dispositivo con la conexion al servidor Mqtt
     * Configura los listeners y pinta el dispositivo
     */
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
        mbinding.imagePanelSwitch.setOnClickListener(this);

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

    /**
     * Este metodo configura los listener para la recepcion de comandos y espontaneos
     * En base a la informacion recibida se pintan los diferentes elementos del interfaz
     */
    private void configureListenersDeviceSwitch() {


        //Configuramos los listeners de comandos.

        /**
         * Recepcion del comando status
         */
        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "ff");
                updateDevice();
                device.commandGetScheduleDevice();


            }
        });

        /**
         * Recepcion del comando infoDevice
         */
        device.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        /**
         * Recepcion de la informacion de OTA disponible
         */
        device.setOnReceivedOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {

                paintOtaDataSwitch();
            }
        });

        /**
         * Recepcion del comando de visualizacion de programas
         */
        device.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
            @Override
            public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    Log.i(TAG, "Recibida programacion desde el dispositivo");
                    paintPanelProgressSchedule();
                    paintScheduleFragment();
                }
            }
        });

        device.setOnReceivedSetRelay(new IotDeviceSwitch.OnReceivedSetRelay() {
            @Override
            public void onReceivedSetRelay(IOT_CODE_RESULT codeResult) {
                updateDevice();
            }
        });

        /**
         * Recepcion de espontaneo de inicio del dispositivo
         */
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        /**
         * Recepcion de espontaneo de cambio de estado en el switch
         */
        device.setOnReceivedSpontaneousActionRelay(new IotDeviceSwitch.OnReceivedSpontaneousActionRelay() {
            @Override
            public void onReceivedSpontaneousActionRelay(IOT_CODE_RESULT resultCode) {

            }
        });

        /**
         * Recepcion de comienzo de un programa
         */
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        /**
         * Recepcion de fin de un programa
         */
        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {

            }
        });


    }

    /**
     * Crea la conexion mqtt para que el dispositivo se conecte al servidor mqtt y pueda
     * enviar comandos y recibir espontaneos.
     *
     * Cuando la conexion se establece se llama a connectionEstablished en la cual se comenzará
     * actualizar el dispositivo
     * @return Recibe el estado intermedio de la conexion.
     *
     */
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
                updateDevice();
                device.getOtaVersionAvailableCommand();
                //paintScheduleFragment();
            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        return status;

    }

    /**
     * Se refresca la vista del dispositivo en funcion de los mensajes recibidos
     */
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
        paintRelayStatus();
        paintConnections();
    }

    /**
     * Se actualiza el estado del broker
     */
    private void paintBrokerStatus() {

        if (cnx.isConnected()) {
            mbinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_on);
        } else {
            mbinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_off);
        }

    }


    /**
     * Este metodo actualiza el estado del dispositivo:
     *
     */
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


    /**
     * Este metodo actualiza la informacion cuando hay una nueva version disponible
     */
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

    /**
     * Este metodo pinta el progreso de un programa concreto cuando esta activo.
     */
    private void paintPanelProgressSchedule() {

        String from;
        String to;
        MyHomeIotTools tool;
        int currentTimeSchedule;
        int progress;
        int index;

        IotScheduleDeviceSwitch schedule;
        if (device.getActiveSchedule() == null) {
            mbinding.gridLayoutSchedule.setVisibility(View.INVISIBLE);
            return;
        }
        mbinding.gridLayoutSchedule.setVisibility(View.VISIBLE);
        tool = new MyHomeIotTools();
        index = device.searchSchedule(device.getActiveSchedule());
        if (index >= 0) {
            schedule = device.getSchedulesSwitch().get(index);
            from = tool.formatHour(schedule.getHour(), schedule.getMinute());
            to = tool.convertDuration(schedule.getHour(), schedule.getMinute(), schedule.getDuration());
            currentTimeSchedule = tool.currentDate2DurationSchedule(from);
            progress = (currentTimeSchedule * 100) / schedule.getDuration();
            mbinding.progressSchedule.setProgress(progress);
            mbinding.textStartSchedule.setText(from);
            mbinding.textEndSchedule.setText(to);
        } else {
            mbinding.gridLayoutSchedule.setVisibility(View.INVISIBLE);
        }


}

    /**
     * Este metodo pinta el estado de la conexion del dispositivo
     */
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

    /**
     * Este metodo pinta el estado del relay
     */
    private void paintRelayStatus() {


    switch (device.getRelay()) {

        case OFF:
            mbinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_off);
            mbinding.imagePanelSwitch.setTag(IOT_SWITCH_RELAY.OFF);
            break;
        case ON:
            mbinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_on);
            mbinding.imagePanelSwitch.setTag(IOT_SWITCH_RELAY.ON);
            break;
        case UNKNOWN:
            mbinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_unknown);
            mbinding.imagePanelSwitch.setTag(IOT_SWITCH_RELAY.UNKNOWN);
            break;
    }
}

    /**
     * Este metodo abre el fragment en el que se visualizan los programas del dispositivo
     */
    private void paintScheduleFragment() {
        Log.i(TAG, "device es : switch" + device.hashCode());
        switchScheduleFragment = new SwitchScheduleFragment(device);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerSwitch, switchScheduleFragment, "Schedule");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();

        switchScheduleFragment.setOnSendEventSchedule(new SwitchScheduleFragment.OnSendEventSchedule() {
            @Override
            public void onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE operation) {
                switch (operation) {

                    case NEW_SCHEDULE:
                        break;
                    case DELETE_SCHEDULE:
                        break;
                    case MODIFY_SCHEDULE:
                        break;
                    case DISPLAY_SCHEDULE:
                        paintPanelProgressSchedule();
                        break;
                }
            }
        });



}


    /**
     * Este metodo se utiliza para dar funcionalidad al control onNavigationItemSelected
     * y lanzar otras opciones.
     * @param item The selected item
     * @return
     */
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

    /**
     * Este metodo recibe la notificacion desde ActionSwitchScheduleFragment para actualizar la vista
     * despues de que se cree un nuevo programa desde dicho fragment.
     * De esta manera se comprueba si el nuevo programa es el actual y se debe actualizar
     * la barra de progreso del programa actual.
     * @param schedule
     * @param operationSchedule
     */
    @Override
    public void onActionSchedule(IotScheduleDeviceSwitch schedule, ActionSwitchScheduleFragment.OPERATION_SCHEDULE operationSchedule) {

        if (operationSchedule == ActionSwitchScheduleFragment.OPERATION_SCHEDULE.NEW_SCHEDULE) {
            device.commandNewScheduleDevice(schedule);
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.imagePanelSwitch):
                setSwitch();
                break;

        }

    }

    private void setSwitch() {

        IOT_SWITCH_RELAY statusRelay;
        statusRelay = (IOT_SWITCH_RELAY) mbinding.imagePanelSwitch.getTag();

        switch (statusRelay) {

            case OFF:
                statusRelay = IOT_SWITCH_RELAY.ON;
                break;
            case ON:
                statusRelay = IOT_SWITCH_RELAY.OFF;
                break;
            case UNKNOWN:
                break;
        }

        device.commandSetRelay(statusRelay);
        paintConnections();


    }





}