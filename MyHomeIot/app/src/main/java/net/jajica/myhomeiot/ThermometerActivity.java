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
import net.jajica.libiot.IOT_TYPE_ALARM_DEVICE;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotOtaVersionAvailable;
import net.jajica.myhomeiot.databinding.ActivityThermometerBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ThermometerActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private final String TAG = "ThermometerActivity";
    private IotMqttConnection cnx;
    private ActivityThermometerBinding binding;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private IotDeviceThermometer device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThermometerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initActivity();
    }

    /**
     * Este metodo inicializa y recibe los datos desde mainActivity
     * Crea un nuevo dispositivo con la conexion al servidor Mqtt
     * Configura los listeners y pinta el dispositivo
     */
    private void initActivity() {
        JSONObject jsonObject;

        binding.bottomActionThermometer.setOnItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        device = new IotDeviceThermometer();
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
            configureListenersDeviceThermometer();
            updateDevice();
        }
    }

    private void configureListenersDeviceThermometer() {

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                updateDevice();

            }
        });

        device.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {
                launchInfoDeviceFragment();

            }
        });

        device.setOnReceivedOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {
                paintOtaData();

            }
        });

        device.setOnReceivedResetDevice(new IotDevice.OnReceivedResetDevice() {
            @Override
            public void onReceivedResetDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedFactoryResetDevice(new IotDevice.OnReceivedFactoryResetDevice() {
            @Override
            public void onReceivedFactoryResetDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedUpgradeFirmwareDevice(new IotDevice.OnReceivedUpgradeFirmwareDevice() {
            @Override
            public void onReceivedUpgradeFirmwareDevice(IOT_CODE_RESULT codeResult) {

            }
        });

        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedAlarmReportDevice(new IotDevice.OnReceivedAlarmReportDevice() {
            @Override
            public void onReceivedAlarmReportDevice(IOT_TYPE_ALARM_DEVICE alarmType) {

            }
        });

        device.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {

            }
        });


    }

    private void paintOtaData() {

        IotOtaVersionAvailable otaVersionAvailable;

        otaVersionAvailable = device.getDataOta();
        if (otaVersionAvailable == null) {
            return;
        }

        if (otaVersionAvailable.isOtaVersionAvailable(device.getCurrentOtaVersion())) {
            binding.imageUpgradeFirmwarethermometer.setVisibility(View.VISIBLE);

        } else {
            binding.imageUpgradeFirmwarethermometer.setVisibility(View.INVISIBLE);
        }

        Log.i(TAG, "Recibidos datos Ota");
    }

    private void launchInfoDeviceFragment() {

        InfoDeviceFragment infoDeviceFragment = new InfoDeviceFragment(device.getListInfoDevice());
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerThermometer, infoDeviceFragment, "infoDeviceFragment");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case (R.id.item_info_thermostat):
                launchInfoThermometer();
                break;
            case (R.id.item_settings_thermostat):
                launchSettingsThermometer();
                break;

            case (R.id.item_commands_thermostat):
                launchMenucommandsThermometer();
                break;

        }



        return false;
    }

    private void launchMenucommandsThermometer() {

        PopupMenu menu;
        menu = new PopupMenu(getApplicationContext(), binding.bottomActionThermometer);
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
                        //device.commandUpgradeFirmware();
                        sceneUpgradeFirmware();

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


    private void launchSettingsThermometer() {
    }

    private void launchInfoThermometer() {

        device.commandGetInfoDevice();
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

    private void updateDevice() {

        paintStatusCommunication();
        paintTextStatus();
        paintAlarmStatus();
        paintDeviceStatus();
        paintOtaData();
        paintThermometerStatus();


    }

    private void paintThermometerStatus() {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        double dat;
        String text;

        dat = tool.roundData(device.getTemperature(), 1);
        if (dat == 0) {
            text = "--.-";
        } else {
            text = String.valueOf(dat);
        }

        binding.textTemperature.setText(text);


    }

    private void paintDeviceStatus() {

        IOT_DEVICE_STATUS status;

        status = device.getDeviceStatus();

        switch (status) {

            case NORMAL_AUTO:
            case NORMAL_MANUAL:
                binding.textStatusThermometer.setText(getResources().getString(R.string.manual));
                break;
            case NORMAL_ARRANCANDO:
                binding.textStatusThermometer.setText(getResources().getString(R.string.arrancando));
                break;
            case NORMAL_SIN_PROGRAMACION:
                binding.textStatusThermometer.setText(getResources().getString(R.string.no_programs));
                break;
            case UPGRADE_IN_PROGRESS:
                binding.textStatusThermometer.setText(getResources().getString(R.string.upgrade_in_progress));
                break;
            case NORMAL_SYNCHRONIZING:
                binding.textStatusThermometer.setText(getResources().getString(R.string.syncronizing));
                break;
            case WAITING_END_START:
                binding.textStatusThermometer.setText(getResources().getString(R.string.waiting_start));
                break;
            case START_BEFORE_FACTORY:
                binding.textStatusThermometer.setText(getResources().getString(R.string.start_before_Factory));
                break;
            case NORMAL_END_ACTIVE_SCHEDULE:
                break;
            case INDETERMINADO:
                binding.textStatusThermometer.setText(getResources().getString(R.string.unknown));
                break;
        }
    }

    private void paintAlarmStatus() {

        if (device.getAlarms().activeAlarms()) {
            binding.imageAlarmDevice.setVisibility(View.VISIBLE);
        } else {
            binding.imageAlarmDevice.setVisibility(View.INVISIBLE);
        }


    }

    private void paintTextStatus() {
        binding.textDeviceInfothermometer.setText(device.getDeviceName());
    }

    private void paintStatusCommunication() {

        paintBrokerStatus();

        switch (device.getStatusConnection()) {

            case UNKNOWN:
                binding.progressOperationThermometer.setVisibility(View.INVISIBLE);
                break;
            case DEVICE_CONNECTED:
                binding.imageConnectedDevicethermometer.setImageResource(R.drawable.ic_action_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                binding.imageConnectedDevicethermometer.setImageResource(R.drawable.ic_action_connect_nok);
                break;
            case DEVICE_WAITING_RESPONSE:
                binding.imageConnectedDevicethermometer.setImageResource(R.drawable.ic_action_waiting_response);
                break;
            case DEVICE_ERROR_COMMUNICATION:
            case DEVICE_NO_SUBSCRIPT:
            case DEVICE_ERROR_NO_SUBSCRIPT:
            case DEVICE_ERROR_SUBSCRIPTION:
                binding.imageConnectedDevicethermometer.setImageResource(R.drawable.ic_action_error);
                break;

        }

        if (device.getStatusConnection() == IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
            binding.progressOperationThermometer.setVisibility(View.VISIBLE);
        } else {
            binding.progressOperationThermometer.setVisibility(View.INVISIBLE);
        }


    }

    private void paintBrokerStatus() {

        if (cnx.isConnected()) {
            binding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_on);
        } else {
            binding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_off);
            device.setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_DISCONNECTED);
        }

    }

    private void sceneUpgradeFirmware() {

        InteractiveFragment scene;
        device.commandUpgradeFirmware();
        scene = new InteractiveFragment(this, device, 120000, IOT_COMMANDS.UPGRADE_FIRMWARE);
        scene.show(fragmentManager.beginTransaction(), "upgrade device");

    }

}