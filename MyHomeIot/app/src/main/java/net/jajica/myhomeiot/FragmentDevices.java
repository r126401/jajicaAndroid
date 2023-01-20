package net.jajica.myhomeiot;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_TYPE_ALARM_DEVICE;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotDeviceUnknown;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;

import java.util.ArrayList;


public class FragmentDevices extends Fragment {

    private final String TAG = "FragmentDevices";
    RecyclerView recyclerView;
    IotDeviceAdapter adapter;
    String siteName;
    String roomName;
    public FragmentDevices(ArrayList<IotDevice> devices, Context context, int idLayout, String siteName, String roomName) {
        this.devices = devices;
        this.context = context;
        this.idLayout = idLayout;
        this.siteName = siteName;
        this.roomName = roomName;
    }

    private ArrayList<IotDevice> devices;
    private Context context;
    private int idLayout;


    public FragmentDevices() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.list_devices, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerDevices);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new IotDeviceAdapter(getActivity(), R.id.recyclerDevices, devices, siteName, roomName);
        recyclerView.setAdapter(adapter);


        if (devices != null) {
            connectDevices(devices);
        }

        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void connectDevices(ArrayList<IotDevice> devices) {

        int i;
        IOT_DEVICE_TYPE type;
        IotDevice device;
        for(i=0;i < devices.size();i++) {
            type = devices.get(i).getDeviceType();
            device = devices.get(i);
            switch (type) {
                case UNKNOWN:
                    connectUnknownDevice((IotDeviceUnknown) device, i);
                    break;
                case INTERRUPTOR:
                    connectSwitchDevice((IotDeviceSwitch) device, i);
                    break;
                case THERMOMETER:
                    connectThemometerDevice((IotDeviceThermometer) device, i);
                    break;
                case CRONOTERMOSTATO:
                    connectThermostatDevice((IotDeviceThermostat) device, i);
                    break;
                default:

            }


        }


    }

    private void connectUnknownDevice(IotDeviceUnknown device, int position) {

        //Subscribir al dispositivo
        device.subscribeDevice();
        device.commandGetStatusDevice();

        //Recepcion de los timeouts a los comandos
        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });
        //Error en comando
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });
        //Recepcion del comando satus
        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {

                Log.i(TAG, "recibido status " + device.getDeviceName());
                convertUnknownDevice(device, position);
           }
        });

        //Comienzo del dispositivo
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                convertUnknownDevice(device, position);

            }
        });

        // comienzo de un programa
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                convertUnknownDevice(device, position);

            }
        });

        //Fin de un programa
device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
    @Override
    public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {
        convertUnknownDevice(device, position);
    }
});




    }

    private void connectSwitchDevice(IotDeviceSwitch device, int position) {

        device.subscribeDevice();
        device.commandGetStatusDevice();
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibido estatus en el switch" + device.getDeviceName()) ;
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSpontaneousActionRelay(new IotDeviceSwitch.OnReceivedSpontaneousActionRelay() {
            @Override
            public void onReceivedSpontaneousActionRelay(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);
            }
        });
        device.setOnReceivedSetRelay(new IotDeviceSwitch.OnReceivedSetRelay() {
            @Override
            public void onReceivedSetRelay(IOT_CODE_RESULT codeResult) {
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedAlarmReportDevice(new IotDevice.OnReceivedAlarmReportDevice() {
            @Override
            public void onReceivedAlarmReportDevice(IOT_TYPE_ALARM_DEVICE alarmType) {

            }
        });

        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });
    }


    private void connectThemometerDevice(IotDeviceThermometer device, int position) {

        device.subscribeDevice();
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibido estatus en el termometro" + device.getDeviceName()) ;

            }
        });

        device.setOnReceivedAlarmReportDevice(new IotDevice.OnReceivedAlarmReportDevice() {
            @Override
            public void onReceivedAlarmReportDevice(IOT_TYPE_ALARM_DEVICE alarmType) {

            }
        });
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {

            }
        });

    }

    private void connectThermostatDevice(IotDeviceThermostat device, int position) {

        device.subscribeDevice();
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });
        device.setOnReceivedAlarmReportDevice(new IotDevice.OnReceivedAlarmReportDevice() {
            @Override
            public void onReceivedAlarmReportDevice(IOT_TYPE_ALARM_DEVICE alarmType) {

            }
        });

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibido estatus en el termostato" + device.getDeviceName()) ;

            }
        });

        device.setOnReceivedSetThresholdTemperature(new IotDeviceThermostat.OnReceivedSetThresholdTemperature() {
            @Override
            public void onReceivedSetThresholdTemperature(IOT_CODE_RESULT resultCode) {

            }
        });
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

            }
        });
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {

            }
        });

    }


    private void modifyConfiguration(IotDevice device, int position, IOT_DEVICE_TYPE type) {
        IotUsersDevices configuration;
        IotSitesDevices site;
        configuration = new IotUsersDevices(context);
        configuration.loadConfiguration();
        int indexSite;
        int indexRoom;
        IotDevice dev;


        dev = configuration.getIotDeviceObject(siteName, roomName, device.getDeviceId());
        dev.setDeviceType(type);
        configuration.saveConfiguration(context);


        Log.i(TAG, "cambiado");
    }


    private void convertUnknownDevice(IotDeviceUnknown device, int position) {

        Log.i(TAG, "recibido status " + device.getDeviceName());
        IOT_DEVICE_TYPE type;
        type = device.getDeviceType();
        switch (type) {
            case UNKNOWN:

                break;
            case INTERRUPTOR:
                IotDeviceSwitch deviceSwitch = device.unknown2Switch();
                devices.remove(position);
                devices.add(position, deviceSwitch);
                adapter.notifyDataSetChanged();
                modifyConfiguration(device, position, IOT_DEVICE_TYPE.INTERRUPTOR);
                break;
            case THERMOMETER:
                IotDeviceThermometer deviceThermometer = device.unknown2Thermometer();
                devices.remove(position);
                devices.add(position, deviceThermometer);
                adapter.notifyItemChanged(position);
                modifyConfiguration(device, position, IOT_DEVICE_TYPE.THERMOMETER);
                break;
            case CRONOTERMOSTATO:
                IotDeviceThermostat deviceThermostat = device.unknown2Thermostat();
                devices.remove(position);
                devices.add(position, deviceThermostat);
                adapter.notifyItemChanged(position);
                modifyConfiguration(device, position, IOT_DEVICE_TYPE.CRONOTERMOSTATO);
                break;
            default:

        }


    }

}