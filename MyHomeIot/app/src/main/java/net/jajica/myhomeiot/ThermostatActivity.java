package net.jajica.myhomeiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationBarView;

import net.jajica.libiot.IOT_ALARM_VALUE;
import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_COMMANDS;
import net.jajica.libiot.IOT_DEVICE_STATUS;
import net.jajica.libiot.IOT_DEVICE_STATUS_CONNECTION;
import net.jajica.libiot.IOT_JSON_RESULT;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotOtaVersionAvailable;
import net.jajica.libiot.IotScheduleDeviceThermostat;
import net.jajica.myhomeiot.databinding.ActivityThermostatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ThermostatActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener,
        NavigationBarView.OnItemSelectedListener, ActionThermostatScheduleFragment.OnActionSchedule {

    private final String TAG = "ThermostatActivity";
    private IotDeviceThermostat device;
    private IotMqttConnection cnx;
    private ActivityThermostatBinding mBinding;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private ThermostatScheduleFragment thermostatScheduleFragment;
    private ActionThermostatScheduleFragment actionThermostatScheduleFragment;
    private CountDownTimer counter;
    private Handler handler;
    private Boolean autoincremento;
    private Boolean autodecremento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityThermostatBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initActivity();

    }

    @SuppressLint("ClickableViewAccessibility")
    private Boolean initActivity() {

        JSONObject jsonObject = null;
        mBinding.imageDownTemperature.setOnClickListener(this);
        mBinding.imageDownTemperature.setOnLongClickListener(this);
        mBinding.imageDownTemperature.setOnTouchListener(this);
        mBinding.imageUpTemperature.setOnClickListener(this);
        mBinding.imageUpTemperature.setOnLongClickListener(this);
        mBinding.imageUpTemperature.setOnTouchListener(this);
        mBinding.bottomActionThermostat.setOnItemSelectedListener(this);
        autodecremento = false;
        autoincremento = false;
        handler = new Handler();
        counter = null;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
        try {
            jsonObject = new JSONObject(data);


        } catch (JSONException e) {
            Log.e(TAG, "Error al pasar los datos a la activity");
            return false;
        }
        device = new IotDeviceThermostat();
        if (device.json2Object(jsonObject) != IOT_JSON_RESULT.JSON_OK) {
            return false;
        }
        if (createConnectionMqtt() == IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO) {

            configureListenerDeviceThermostat();
            updateDevice();
            return true;
        }

        return true;

    }

    private void configureListenerDeviceThermostat() {



        //Configuramos los listeners de comandos.

        /**
         * Recepcion del comando status
         */
        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "ff");
                updateDevice();
                //device.commandGetScheduleDevice();


            }
        });

        /**
         * Recepcion del comando infoDevice
         */
        device.setOnReceivedInfoDevice(new IotDevice.OnReceivedInfoDevice() {
            @Override
            public void onReceivedInfoDevice(IOT_CODE_RESULT resultCode) {

                Log.i(TAG, "kk " + device.getDeviceId());
                launchInfoDeviceFragment();


            }
        });

        /**
         * Recepcion de la informacion de OTA disponible
         */
        device.setOnReceivedOtaVersionAvailableDevice(new IotDevice.OnReceivedOtaVersionAvailableDevice() {
            @Override
            public void onReceivedOtaVersionAvailableDevice(IOT_CODE_RESULT resultCode) {

                paintOtaData();
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

        device.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibida nueva programacion");
                device.commandGetScheduleDevice();
            }
        });



        /**
         * Recepcion de espontaneo de inicio del dispositivo
         */
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });



        /**
         * Recepcion de comienzo de un programa
         */
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });

        /**
         * Recepcion de fin de un programa
         */
        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {

                updateDevice();

            }
        });

        device.setOnReceivedResetDevice(new IotDevice.OnReceivedResetDevice() {
            @Override
            public void onReceivedResetDevice(IOT_CODE_RESULT resultCode) {
                MyHomeIotTools tools;
                tools = new MyHomeIotTools();
                tools.sceneResetDevice(fragmentManager, device, ThermostatActivity.this);


                Log.i(TAG, "Recibido reset al dispositivo");

            }
        });

        device.setOnReceivedFactoryResetDevice(new IotDevice.OnReceivedFactoryResetDevice() {
            @Override
            public void onReceivedFactoryResetDevice(IOT_CODE_RESULT resultCode) {

                MyHomeIotTools tools;
                tools = new MyHomeIotTools();
                tools.sceneFactoryResetDevice(fragmentManager, device, ThermostatActivity.this);


            }
        });

        device.setOnReceivedUpgradeFirmwareDevice(new IotDevice.OnReceivedUpgradeFirmwareDevice() {
            @Override
            public void onReceivedUpgradeFirmwareDevice(IOT_CODE_RESULT codeResult) {
                MyHomeIotTools tools;
                tools = new MyHomeIotTools();
                tools.sceneUpgradeFirmware(fragmentManager, device, ThermostatActivity.this);


            }
        });

        device.setOnReceivedModifyParametersDevice(new IotDevice.OnReceivedModifyParametersDevice() {
            @Override
            public void onReceivedMofifyParametersDevice(IOT_CODE_RESULT resultCode) {

                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    Log.i(TAG, "Recibido modificacion");
                    device.commandGetInfoDevice();
                }

            }
        });

        device.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {

                updateDevice();
            }
        });

        device.setOnReceivedSetThresholdTemperature(new IotDeviceThermostat.OnReceivedSetThresholdTemperature() {
            @Override
            public void onReceivedSetThresholdTemperature(IOT_CODE_RESULT resultCode) {
                updateDevice();
            }
        });
    }

    private void launchInfoDeviceFragment() {

        InfoDeviceFragment infoDeviceFragment = new InfoDeviceFragment(device);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerThermostat, infoDeviceFragment, "infoDeviceFragment");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack("infoDeviceFragment");
        fragmentTransaction.commit();

    }


    private IOT_MQTT_STATUS_CONNECTION createConnectionMqtt() {

        IOT_MQTT_STATUS_CONNECTION status;
        cnx = new IotMqttConnection(getApplicationContext());

        status = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion establecida");
                device.setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED);
                device.setCnx(cnx);
                device.unSubscribeDevice();
                device.subscribeDevice();
                device.subscribeOtaServer();
                updateDevice();
                device.getOtaVersionAvailableCommand();
                device.commandGetStatusDevice();
                device.commandGetScheduleDevice();
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
        paintTermostatStatus();
        paintRelayThermostat();
    }

    private void paintRelayThermostat() {

        IOT_SWITCH_RELAY status;

        status = device.getRelay();
        switch (status) {

            case OFF:
                mBinding.imageHeatingThermostat.setVisibility(View.INVISIBLE);
                break;
            case ON:
                mBinding.imageHeatingThermostat.setVisibility(View.VISIBLE);
                break;
            case UNKNOWN:
                mBinding.imageHeatingThermostat.setVisibility(View.INVISIBLE);
                break;
        }

    }

    private void paintTermostatStatus() {

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

        mBinding.textTemperature.setText(text);
        dat = tool.roundData(device.getThresholdTemperature(), 1);
        if ((dat == 0) || (dat < -100)) {
            text = "--.-";
        } else {
            text = String.valueOf(dat);
        }

        mBinding.textThresholdTemperature.setText(text);


    }

    private void paintOtaData() {

        IotOtaVersionAvailable otaVersionAvailable;

        otaVersionAvailable = device.getDataOta();
        if (otaVersionAvailable == null) {
            return;
        }

        if (otaVersionAvailable.isOtaVersionAvailable(device.getCurrentOtaVersion())) {
            mBinding.imageUpgradeFirmwareThermostat.setVisibility(View.VISIBLE);

        } else {
            mBinding.imageUpgradeFirmwareThermostat.setVisibility(View.INVISIBLE);
        }

        Log.i(TAG, "Recibidos datos Ota");
    }

    private void paintDeviceStatus() {

        IOT_DEVICE_STATUS status;

        status = device.getDeviceStatus();

        switch (status) {

            case AUTO:
            case AUTOMAN:
                mBinding.textStatusThermostat.setVisibility(View.VISIBLE);
                mBinding.textStatusThermostat.setText(status.toString());
                break;
            case MANUAL:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.manual));
                break;
            case STARTING:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.arrancando));
                break;
            case NO_PROGRAMS:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.no_programs));
                break;
            case UPGRADE_IN_PROGRESS:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.upgrade_in_progress));
                break;
            case NORMAL_SYNCHRONIZING:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.syncronizing));
                break;
            case WAITING_END_START:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.waiting_start));
                break;
            case START_BEFORE_FACTORY:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.start_before_Factory));
                break;
            case NORMAL_END_ACTIVE_SCHEDULE:
                break;
            case INDETERMINADO:
                mBinding.textStatusThermostat.setText(getResources().getString(R.string.unknown));
                break;
        }
    }

    private void paintAlarmStatus() {

        if (device.getAlarms().activeAlarms()) {
            mBinding.imageAlarmDevice.setVisibility(View.VISIBLE);
        } else {
            mBinding.imageAlarmDevice.setVisibility(View.INVISIBLE);
        }
    }

    private void paintTextStatus() {

        mBinding.textDeviceInfoThermostat.setText(device.getDeviceName());
    }

    private void paintStatusCommunication() {

        paintBrokerStatus();

        switch (device.getStatusConnection()) {

            case UNKNOWN:
                mBinding.progressOperationThermostat.setVisibility(View.INVISIBLE);
                mBinding.imageUpTemperature.setEnabled(false);
                mBinding.imageDownTemperature.setEnabled(false);
                break;
            case DEVICE_CONNECTED:
                mBinding.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_online);
                device.getAlarms().setWifiAlarm(IOT_ALARM_VALUE.ALARM_OFF);
                mBinding.imageUpTemperature.setEnabled(true);
                mBinding.imageDownTemperature.setEnabled(true);
                paintBrokerStatus();
                break;
            case DEVICE_DISCONNECTED:
                mBinding.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_offline);
                mBinding.imageUpTemperature.setEnabled(false);
                mBinding.imageDownTemperature.setEnabled(false);
                break;
            case DEVICE_WAITING_RESPONSE:
                mBinding.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_waiting_response);
                mBinding.imageUpTemperature.setEnabled(false);
                mBinding.imageDownTemperature.setEnabled(false);
                break;
            case DEVICE_ERROR_COMMUNICATION:
            case DEVICE_NO_SUBSCRIPT:
            case DEVICE_ERROR_NO_SUBSCRIPT:
            case DEVICE_ERROR_SUBSCRIPTION:
                mBinding.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_error);
                device.getAlarms().setWifiAlarm(IOT_ALARM_VALUE.ALARM_ON);
                mBinding.imageUpTemperature.setEnabled(false);
                mBinding.imageDownTemperature.setEnabled(false);
                break;

        }

        if (device.getStatusConnection() == IOT_DEVICE_STATUS_CONNECTION.DEVICE_WAITING_RESPONSE) {
            mBinding.progressOperationThermostat.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressOperationThermostat.setVisibility(View.INVISIBLE);
        }
    }

    private void paintPanelProgressSchedule() {
    }

    private void paintBrokerStatus() {

        if (cnx.isConnected()) {
            mBinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_on);
        } else {
            mBinding.imageBrokerConnected.setImageResource(R.drawable.ic_wifi_off);
            device.setConnectionState(IOT_DEVICE_STATUS_CONNECTION.DEVICE_DISCONNECTED);
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.imageUpTemperature):
                cancelCounter();
                modifyDoubleValue(mBinding.textThresholdTemperature, true, 0.5, 0, 30);
                createCounter();
                break;
            case (R.id.imageDownTemperature):
                cancelCounter();
                modifyDoubleValue(mBinding.textThresholdTemperature, false, 0.5, 0, 30);
                createCounter();
                break;
        }

    }


    private void cancelCounter() {

        if (counter != null) {
            counter.cancel();
            counter = null;
            Log.w(TAG, "Cancelado temporizador");
            mBinding.textThresholdTemperature.setTextColor(Color.RED);
        }


    }

    private void modifyDoubleValue(TextView controlTexto, Boolean incremento, double valor, double minVal, double maxVal) {

        double cantidad;

        cantidad = Double.parseDouble(controlTexto.getText().toString());
        if (incremento == true) {
            cantidad += valor;
            if (cantidad >= maxVal) cantidad = maxVal;
        } else {
            cantidad -= valor;
            if (cantidad <= minVal) cantidad = minVal;
        }

        DecimalFormat formater = new DecimalFormat("##.#");
        String dato;
        dato = formater.format(cantidad);
        String dat;
        dat = dato.substring(0,2);
        if (dato.length() > 2) {
            dat += ".";
            dat += dato.substring(3);
        } else {
            dat += ".0";
        }

        controlTexto.setText(dat);


    }

    private void createCounter() {

        if (counter == null) {
            mBinding.textThresholdTemperature.setTextColor(Color.rgb(0xFF, 0x3c, 0x00));
            counter = new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    device.setThresholdTemperature(Double.valueOf(mBinding.textThresholdTemperature.getText().toString()));
                    device.commandSetThresholdTemperarture(device.getThresholdTemperature());
                    //dialogo.enviarComando(dispositivoCronotermostato, dialogo.comandoModificarUmbralTemperatura(dispositivoCronotermostato.getUmbralTemperatura()));
                    Log.i(TAG, "Modificado umbral de temperatura");
                    mBinding.textThresholdTemperature.setTextColor(Color.BLACK);
                }
            };

            counter.start();
            Log.w(TAG, "Creado temporizador");
        }




    }


    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case (R.id.imageUpTemperature):
                autoincremento = true;
                autodecremento = false;
                handler.post(new ThermostatActivity.modificacionPulsacionLargaDoble(mBinding.textThresholdTemperature, true, 1.0, 0.0, 30.0));

                break;
            case (R.id.imageDownTemperature):
                autodecremento = true;
                autoincremento = false;
                handler.post(new ThermostatActivity.modificacionPulsacionLargaDoble(mBinding.textThresholdTemperature, false, 1.0, 0.0, 30.0));

                break;
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        long antes;
        long despues;
        long diferencia;


        switch (v.getId()) {
            case R.id.imageDownTemperature:
            case R.id.imageUpTemperature:

                if ((event.getAction() == MotionEvent.ACTION_UP) || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
                    autodecremento = false;
                    autoincremento = false;
                    antes = event.getEventTime();
                    despues = event.getDownTime();
                    diferencia = antes - despues;
                    if (diferencia > 500) {
                        cancelCounter();
                        createCounter();
                        Log.w(TAG, "contador de pulsacion larga");

                    }
                }
                break;
            default:
                break;
        }


        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case (R.id.item_info_thermostat):
                launchInfoThermostat();
                break;
            case (R.id.item_settings_thermostat):
                launchSettingsThermostat();
                break;
            case (R.id.item_new_schedule_switch):
                launchNewScheduleThermostat();

                break;
            case (R.id.item_commands_thermostat):
                launchMenucommandsThermostat();
                break;

        }


        return false;
    }

    private void launchMenucommandsThermostat() {
        PopupMenu menu;
        menu = new PopupMenu(getApplicationContext(), mBinding.bottomActionThermostat);
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

    private void launchNewScheduleThermostat() {

        fragmentTransaction = fragmentManager.beginTransaction();
        actionThermostatScheduleFragment = new ActionThermostatScheduleFragment(null);
        actionThermostatScheduleFragment.setOnActionSchedule(this);
        fragmentTransaction.replace(R.id.containerThermostat, actionThermostatScheduleFragment, "NewScheduleThermostat");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack("NewScheduleThermostat");
        fragmentTransaction.commit();
    }

    private void launchSettingsThermostat() {
    }

    private void launchInfoThermostat() {

        device.commandGetInfoDevice();
    }




    class modificacionPulsacionLargaDoble implements Runnable {

        TextView textControl;
        Boolean incremento;
        Double valor;
        Double minVal;
        Double maxVal;

        modificacionPulsacionLargaDoble(TextView control, Boolean incremento, Double valor, Double minVal, Double maxVal) {
            textControl = control;
            this.incremento = incremento;
            this.valor = valor;
            this.minVal = minVal;
            this.maxVal = maxVal;



        }

        public void run() {
            if (autoincremento) {
                handler.postDelayed(new ThermostatActivity.modificacionPulsacionLargaDoble(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "incrementando");
                modifyDoubleValue(textControl, incremento, valor, minVal, maxVal);

            } else if (autodecremento){
                handler.postDelayed(new ThermostatActivity.modificacionPulsacionLargaDoble(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "decrementando");
                modifyDoubleValue(textControl, incremento, valor, minVal, maxVal);



            }
            //textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));

        }
    }

    private void paintScheduleFragment() {
        Log.i(TAG, "device es : Thermostat" + device.hashCode());
        if (thermostatScheduleFragment == null) {
            thermostatScheduleFragment = new ThermostatScheduleFragment(device);
            thermostatScheduleFragment.setOnSendEventSchedule(new ThermostatScheduleFragment.OnSendEventSchedule() {
                @Override
                public void onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE operation) {
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
                            device.commandGetScheduleDevice();
                            updateDevice();
                            break;
                        case TIMEOUT:
                            notifyTimeoutCommand();
                            sendError();
                            break;
                        case UPDATE_SCHEDULE:
                            updateDevice();
                            break;

                    }
                }
            });

        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerThermostat, thermostatScheduleFragment, "ScheduleThermostat");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack("ScheduleThermostat");
        fragmentTransaction.commit();


    }

    @Override
    public Boolean onActionSchedule(IotScheduleDeviceThermostat schedule, ActionThermostatScheduleFragment.OPERATION_SCHEDULE operationSchedule, String aditionalInfo) {

        if (operationSchedule == ActionThermostatScheduleFragment.OPERATION_SCHEDULE.NEW_SCHEDULE) {
            if (device.checkValidScheduleThermostatDevice(schedule, null)) {
                device.commandNewScheduleDevice(schedule);
                return true;
            } else {
                return false;
            }

        }

        if (operationSchedule == ActionThermostatScheduleFragment.OPERATION_SCHEDULE.MODIFY_SCHEDULE) {

            if (device.checkValidScheduleThermostatDevice(schedule, aditionalInfo)) {
                device.commandModifyScheduleDevice(schedule);
                return true;
            } else {
                return false;
            }

        }

        return true;


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

    @Override
    public void onBackPressed() {

        String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        if (tag.equals("ScheduleThermostat")) {
            finish();
        } else {
            paintScheduleFragment();
        }
    }

    private void notifyTimeoutCommand() {



        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    updateDevice();
                    Log.d(TAG,"send update to disconnect");
                }
            });
        } catch (NullPointerException exception) {
            Log.e(TAG, "La tarea para cancelar es erronea");
        }


    }

    private void sendError() {


        MyHomeIotTools tools;
        AlertDialog.Builder dialog;

        if (device.getAlarms().getWifiAlarm() == IOT_ALARM_VALUE.ALARM_OFF) {
            tools = new MyHomeIotTools(getApplicationContext());
            dialog = tools.notifyError(R.drawable.ic_action_error,
                    R.string.error, R.string.error_connection_device, fragmentManager, this);
            dialog.setCancelable(true);
        }



    }

    @Override
    protected void onDestroy() {
        cnx.closeConnection();
        device.unSubscribeDevice();
        device.unsubscribeOtaServer();
        super.onDestroy();
    }
}