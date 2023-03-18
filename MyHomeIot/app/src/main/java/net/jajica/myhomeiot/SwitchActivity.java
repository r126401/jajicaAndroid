package net.jajica.myhomeiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


import com.google.android.material.navigation.NavigationBarView;

import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_COMMANDS;
import net.jajica.libiot.IOT_DEVICE_STATUS;
import net.jajica.libiot.IOT_DEVICE_STATUS_CONNECTION;
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

    private ActivitySwitchBinding mBinding;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    /**
     * Este metodo inicializa y recibe los datos desde mainActivity
     * Crea un nuevo dispositivo con la conexion al servidor Mqtt
     * Configura los listeners y pinta el dispositivo
     */
    private void initActivity() {
        JSONObject jsonObject;

        mBinding.imagePanelSwitch.setOnClickListener(this);
        mBinding.bottomActionsSwitch.setOnItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        device = new IotDeviceSwitch();
        try {
            jsonObject = new JSONObject(data);
            if (!data.equals("")) {

                device.json2Object(jsonObject);
            }
        } catch (JSONException exception) {
            Log.e(TAG, "Error al recibir el json desde MainActivity");
        }

        if (createConnectionMqtt() == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO) {
            device.setCnx(cnx);
            configureListenersDeviceSwitch();
            updateDevice();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_switch);

        mBinding = ActivitySwitchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        initActivity();


    }

    /**
     * Este metodo configura los listener para la recepcion de comandos y espontaneos
     * En base a la informacion recibida se pintan los diferentes elementos del interfaz
     */
    private void configureListenersDeviceSwitch() {


        //Configuramos los listeners de comandos.

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "ff");
                updateDevice();
                device.commandGetScheduleDevice();


            }
        });


        device.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {

                Log.i(TAG, "kk " + device.getDeviceId());
                launchInfoDeviceFragment();


            }
        });


        device.setOnReceivedOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {

                paintOtaData();
            }
        });


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


        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });


        device.setOnReceivedSpontaneousActionRelay(new IotDeviceSwitch.OnReceivedSpontaneousActionRelay() {
            @Override
            public void onReceivedSpontaneousActionRelay(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });


        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });


        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });

        device.setOnReceivedResetDevice(new IotDevice.OnReceivedResetDevice() {
            @Override
            public void onReceivedResetDevice(IOT_CODE_RESULT resultCode) {
                MyHomeIotTools tool;
                tool = new MyHomeIotTools();
                tool.sceneResetDevice(fragmentManager, device, SwitchActivity.this);
                //sceneResetDevice();

                Log.i(TAG, "Recibido reset al dispositivo");

            }
        });

        device.setOnReceivedFactoryResetDevice(new IotDevice.OnReceivedFactoryResetDevice() {
            @Override
            public void onReceivedFactoryResetDevice(IOT_CODE_RESULT resultCode) {
                MyHomeIotTools tool;
                tool = new MyHomeIotTools();
                tool.sceneFactoryResetDevice(fragmentManager, device, SwitchActivity.this);

            }
        });

        device.setOnReceivedUpgradeFirmwareDevice(new IotDevice.OnReceivedUpgradeFirmwareDevice() {
            @Override
            public void onReceivedUpgradeFirmwareDevice(IOT_CODE_RESULT codeResult) {

                MyHomeIotTools tool;
                tool = new MyHomeIotTools();
                tool.sceneUpgradeFirmware(fragmentManager, device, SwitchActivity.this);

            }
        });


    }

    private void launchInfoDeviceFragment() {

        InfoDeviceFragment infoDeviceFragment = new InfoDeviceFragment(device.getListInfoDevice());
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerSwitch, infoDeviceFragment, "infoDeviceFragment");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack("scheduleSwitch");
        fragmentTransaction.commit();

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
                updateDevice();

            }
        });

        return status;

    }

    /**
     * Se refresca la vista del dispositivo en funcion de los mensajes recibidos
     */
    private void updateDevice() {

        //Pintamos si el dispositivo esta conectado o no
        paintStatusCommunication();
        paintTextStatus();
        paintAlarmStatus();
        paintDeviceStatus();
        paintOtaData();
        paintRelayStatus();

    }

    private void paintTextStatus() {
        mBinding.textDeviceInfoSwitch.setText(device.getDeviceName());
    }

    private void paintAlarmStatus() {

        if (device.getAlarms().activeAlarms()) {
            mBinding.imageAlarmSwitch.setVisibility(View.VISIBLE);
        } else {
            mBinding.imageAlarmSwitch.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Se actualiza el estado del broker
     */
    private void paintBrokerStatus() {

        if (cnx.isConnected()) {
            mBinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_on);
        } else {
            mBinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_off);
            device.setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_DISCONNECTED);
        }


    }


    /**
     * Este metodo actualiza el estado del dispositivo:
     *
     */
    private void paintDeviceStatus() {

        IOT_DEVICE_STATUS status;

        status = device.getDeviceStatus();

        switch (status) {

            case NORMAL_AUTO:
            case NORMAL_AUTOMAN:
                if (device.getSchedulesSwitch()!= null) {
                    if (device.getSchedulesSwitch().size() > 0) {
                        mBinding.textStatusSwitch.setVisibility(View.VISIBLE);
                        mBinding.textStatusSwitch.setText(getResources().getString(R.string.auto));
                    } else {
                        mBinding.textStatusSwitch.setVisibility(View.INVISIBLE);
                    }

                } else {
                    mBinding.textStatusSwitch.setVisibility(View.INVISIBLE);
                }

                break;
            case NORMAL_MANUAL:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.manual));
                break;
            case NORMAL_ARRANCANDO:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.arrancando));
                break;
            case NORMAL_SIN_PROGRAMACION:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.no_programs));
                break;
            case UPGRADE_IN_PROGRESS:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.upgrade_in_progress));
                break;
            case NORMAL_SYNCHRONIZING:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.syncronizing));
                break;
            case WAITING_END_START:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.waiting_start));
                break;
            case START_BEFORE_FACTORY:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.start_before_Factory));
                break;
            case NORMAL_END_ACTIVE_SCHEDULE:
                break;
            case INDETERMINADO:
                mBinding.textStatusSwitch.setText(getResources().getString(R.string.unknown));
                break;
        }


    }


    /**
     * Este metodo actualiza la informacion cuando hay una nueva version disponible
     */
    private void paintOtaData() {

    IotOtaVersionAvailable otaVersionAvailable;

        otaVersionAvailable = device.getDataOta();
        if (otaVersionAvailable == null) {
            return;
        }

        if (otaVersionAvailable.isOtaVersionAvailable(device.getCurrentOtaVersion())) {
            mBinding.imageUpgradeFirmwareSwitch.setVisibility(View.VISIBLE);

        } else {
            mBinding.imageUpgradeFirmwareSwitch.setVisibility(View.INVISIBLE);
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
            mBinding.gridLayoutSchedule.setVisibility(View.INVISIBLE);
            return;
        }
        mBinding.gridLayoutSchedule.setVisibility(View.VISIBLE);
        tool = new MyHomeIotTools();
        index = device.searchSchedule(device.getActiveSchedule());
        if (index >= 0) {
            schedule = device.getSchedulesSwitch().get(index);
            from = tool.formatHour(schedule.getHour(), schedule.getMinute());
            to = tool.convertDuration(schedule.getHour(), schedule.getMinute(), schedule.getDuration());
            currentTimeSchedule = tool.currentDate2DurationSchedule(from);
            progress = (currentTimeSchedule * 100) / schedule.getDuration();
            mBinding.progressSchedule.setProgress(progress);
            mBinding.textStartSchedule.setText(from);
            mBinding.textEndSchedule.setText(to);
        } else {
            mBinding.gridLayoutSchedule.setVisibility(View.INVISIBLE);
        }


}

    /**
     * Este metodo pinta el estado de la conexion del dispositivo
     */
    private void paintStatusCommunication() {

        paintBrokerStatus();

    switch (device.getStatusConnection()) {

        case UNKNOWN:
            mBinding.progressOperationSwitch.setVisibility(View.INVISIBLE);
            break;
        case DEVICE_CONNECTED:
            mBinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_connect_ok);
            break;
        case DEVICE_DISCONNECTED:
            mBinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_connect_nok);
            break;
        case DEVICE_WAITING_RESPONSE:
            mBinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_waiting_response);
            break;
        case DEVICE_ERROR_COMMUNICATION:
        case DEVICE_NO_SUBSCRIPT:
        case DEVICE_ERROR_NO_SUBSCRIPT:
        case DEVICE_ERROR_SUBSCRIPTION:
            mBinding.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_error);
            break;

    }

    if (device.getStatusConnection() == IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
        mBinding.progressOperationSwitch.setVisibility(View.VISIBLE);
    } else {
        mBinding.progressOperationSwitch.setVisibility(View.INVISIBLE);
    }

}

    /**
     * Este metodo pinta el estado del relay
     */
    private void paintRelayStatus() {


    switch (device.getRelay()) {

        case OFF:
            mBinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_off);
            mBinding.imagePanelSwitch.setTag(IOT_SWITCH_RELAY.OFF);
            break;
        case ON:
            mBinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_on);
            mBinding.imagePanelSwitch.setTag(IOT_SWITCH_RELAY.ON);
            break;
        case UNKNOWN:
            mBinding.imagePanelSwitch.setImageResource(R.drawable.ic_switch_unknown);
            mBinding.imagePanelSwitch.setTag(IOT_SWITCH_RELAY.UNKNOWN);
            break;
    }
}

    /**
     * Este metodo abre el fragment en el que se visualizan los programas del dispositivo
     */
    private void paintScheduleFragment() {
        Log.i(TAG, "device es : switch" + device.hashCode());
        SwitchScheduleFragment switchScheduleFragment = new SwitchScheduleFragment(device);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerSwitch, switchScheduleFragment, "ScheduleSwitch");
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
                    case REFRESH_SCHEDULE:
                        device.commandGetStatusDevice();
                        updateDevice();

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

            case (R.id.item_new_schedule_thermostat):
                fragmentTransaction = fragmentManager.beginTransaction();
                ActionSwitchScheduleFragment actionSwitchScheduleFragment = new ActionSwitchScheduleFragment(null);
                actionSwitchScheduleFragment.setOnActionSchedule(this);
                fragmentTransaction.replace(R.id.containerSwitch, actionSwitchScheduleFragment, "NewScheduleSwitch");
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack("scheduleSwitch");
                fragmentTransaction.commit();
                break;

            case (R.id.item_info_switch):
                device.commandGetInfoDevice();
                break;

            case (R.id.item_comandos_switch):
                PopupMenu menu;
                menu = new PopupMenu(getApplicationContext(), mBinding.bottomActionsSwitch);
                menu.inflate(R.menu.menu_action_device_switch);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    menu.setForceShowIcon(true);
                }
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case (R.id.item_reset_device):
                                confirmCommand(R.drawable.ic_action_reset, R.string.reset_device, R.string.confirm_reset, IOT_COMMANDS.RESET);
                                break;
                            case (R.id.item_factory_reset_device):
                                confirmCommand(R.drawable.ic_action_factory_reset, R.string.factory_reset_device, R.string.confirm_factory_reset, IOT_COMMANDS.FACTORY_RESET);
                                break;
                            case (R.id.item_upgrade_firmware_device):
                                confirmCommand(R.drawable.ic_action_upgrade, R.string.upgrade_device, R.string.confirm_upgrade_firmware, IOT_COMMANDS.UPGRADE_FIRMWARE);
                                break;
                        }

                        return false;
                    }
                });
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
    public void onActionSchedule(IotScheduleDeviceSwitch schedule, ActionSwitchScheduleFragment.OPERATION_SCHEDULE operationSchedule, String additionalInfo) {

        if (operationSchedule == ActionSwitchScheduleFragment.OPERATION_SCHEDULE.NEW_SCHEDULE) {
            if (device.checkValidSchedule(schedule, null)) {
                device.commandNewScheduleDevice(schedule);
            }

        }

        if (operationSchedule == ActionSwitchScheduleFragment.OPERATION_SCHEDULE.MODIFY_SCHEDULE) {

            if (device.checkValidSchedule(schedule, additionalInfo)) {
                device.commandModifyScheduleDevice(schedule);
            }

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
        statusRelay = (IOT_SWITCH_RELAY) mBinding.imagePanelSwitch.getTag();

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
        paintStatusCommunication();


    }

    private void confirmCommand(int icon, int title, int message, IOT_COMMANDS command) {

        AlertDialog.Builder alert;
        alert = new AlertDialog.Builder(this);
        alert.setIcon(icon);
        alert.setTitle(title);
        alert.setMessage(message);

        alert.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (command) {


                    case RESET:
                        device.commandResetDevice();
                        break;
                    case FACTORY_RESET:
                        device.commandFactoryReset();
                        break;
                    case UPGRADE_FIRMWARE:
                        device.commandUpgradeFirmware();

                }

            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();

    }




}