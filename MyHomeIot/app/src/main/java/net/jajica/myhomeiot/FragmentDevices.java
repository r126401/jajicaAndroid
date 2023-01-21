package net.jajica.myhomeiot;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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


public class FragmentDevices extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private final String TAG = "FragmentDevices";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeDeviceList;
    IotDeviceAdapter adapter;
    String siteName;
    String roomName;
    public FragmentDevices(ArrayList<IotDevice> deviceList, Context context, int idLayout, String siteName, String roomName) {
        this.deviceList = deviceList;
        this.context = context;
        this.idLayout = idLayout;
        this.siteName = siteName;
        this.roomName = roomName;
    }

    private ArrayList<IotDevice> deviceList;
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
        swipeDeviceList = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeDeviceList);
        swipeDeviceList.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new IotDeviceAdapter(getActivity(), R.id.recyclerDevices, deviceList, siteName, roomName);
        recyclerView.setAdapter(adapter);
        if (deviceList != null) {
            connectDevices(deviceList);
        }
        return rootView;
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
            }
        }
    }

    private void connectUnknownDevice(IotDeviceUnknown device, int position) {

        //Subscribir al dispositivo

        device.subscribeDevice();
        device.commandGetStatusDevice();
        adapter.notifyItemChanged(position);

        //Recepcion de los timeouts a los comandos
        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                Log.i("hh", "hh");
                notifyTimeoutCommand(position);

            }
        });
        //Error en comando
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });
        //Recepcion del comando satus
        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {

                Log.i(TAG, "recibido status " + device.getDeviceName());
                convertUnknownDevice(device, position);
                adapter.notifyItemChanged(position);
           }
        });

        //Comienzo del dispositivo
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                convertUnknownDevice(device, position);
                adapter.notifyItemChanged(position);

            }
        });

        // comienzo de un programa
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                convertUnknownDevice(device, position);
                adapter.notifyItemChanged(position);

            }
        });

        //Fin de un programa
device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
    @Override
    public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {
        convertUnknownDevice(device, position);
        adapter.notifyItemChanged(position);
    }
});




    }

    private void connectSwitchDevice(@NonNull IotDeviceSwitch device, int position) {

        device.subscribeDevice();
        device.commandGetStatusDevice();
        adapter.notifyItemChanged(position);
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                Log.i(TAG, "timeout " + device.getDeviceName());
                notifyTimeoutCommand(position);

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
        device.commandGetStatusDevice();
        adapter.notifyItemChanged(position);
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                notifyTimeoutCommand(position);

            }
        });

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "recibido estatus en el termometro" + device.getDeviceName()) ;
                adapter.notifyItemChanged(position);

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
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {
                adapter.notifyItemChanged(position);

            }
        });

    }

    private void connectThermostatDevice(IotDeviceThermostat device, int position) {

        device.subscribeDevice();
        device.commandGetStatusDevice();
        adapter.notifyItemChanged(position);
        device.setOnErrorReportDevice(new IotDevice.OnReceivedErrorReportDevice() {
            @Override
            public void onReceivedErrorReportDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                notifyTimeoutCommand(position);
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
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSetThresholdTemperature(new IotDeviceThermostat.OnReceivedSetThresholdTemperature() {
            @Override
            public void onReceivedSetThresholdTemperature(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });

        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {
                adapter.notifyItemChanged(position);

            }
        });


        device.setOnReceivedSpontaneousChangeTemperature(new IotDeviceThermometer.OnReceivedSpontaneousChangeTemperature() {
            @Override
            public void onReceivedSpontaneousChangeTemperature() {
                adapter.notifyItemChanged(position);

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
                deviceList.remove(position);
                deviceList.add(position, deviceSwitch);
                modifyConfiguration(device, position, IOT_DEVICE_TYPE.INTERRUPTOR);
                adapter.notifyItemChanged(position);
                break;
            case THERMOMETER:
                IotDeviceThermometer deviceThermometer = device.unknown2Thermometer();
                deviceList.remove(position);
                deviceList.add(position, deviceThermometer);
                modifyConfiguration(device, position, IOT_DEVICE_TYPE.THERMOMETER);
                adapter.notifyItemChanged(position);
                break;
            case CRONOTERMOSTATO:
                IotDeviceThermostat deviceThermostat = device.unknown2Thermostat();
                deviceList.remove(position);
                deviceList.add(position, deviceThermostat);
                modifyConfiguration(device, position, IOT_DEVICE_TYPE.CRONOTERMOSTATO);
                adapter.notifyItemChanged(position);
                break;
            default:

        }


    }

    @Override
    public void onRefresh() {
        askDevices();
        swipeDeviceList.setRefreshing(false);
    }

    public void askDevices() {

        int i;
        if (deviceList != null) {
            for(i=0;i<deviceList.size();i++) {
                deviceList.get(i).commandGetStatusDevice();
                adapter.notifyItemChanged(i);
            }
        }

    }

    public void subscribeDevices() {
        int i;
        if (deviceList != null) {
            for(i=0;i<deviceList.size();i++) {
                deviceList.get(i).subscribeDevice();
            }
        }

    }

    private void notifyTimeoutCommand(int position) {

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemChanged(position);
                }
            });
        } catch (NullPointerException exception) {
            Log.e(TAG, "La tarea para cancelar es erronea");
        }


    }
}