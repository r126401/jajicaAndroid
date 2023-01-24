package net.jajica.myhomeiot;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import net.jajica.libiot.IotUsersDevices;

import java.util.ArrayList;


public class FragmentDevices extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IotDeviceAdapter.OnDeleteDevice {

    private final String TAG = "FragmentDevices";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeDeviceList;
    IotDeviceAdapter adapter;
    String siteName;
    String roomName;

    IotUsersDevices configuration;

    public FragmentDevices(ArrayList<IotDevice> deviceList, Context context, String siteName, String roomName) {
        this.deviceList = deviceList;
        this.context = context;
        this.siteName = siteName;
        this.roomName = roomName;
    }

    private ArrayList<IotDevice> deviceList;
    private Context context;



    public FragmentDevices() {
        // Required empty public constructor
    }


    private void createListDevices() {

        adapter = new IotDeviceAdapter(getActivity(), R.id.recyclerDevices, deviceList, siteName, roomName);
        adapter.setOnDeleteDevice(this);
        recyclerView.setAdapter(adapter);
        if (deviceList != null) {
            connectDevices();
            askDevices();
        }
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
        createListDevices();

        return rootView;
    }



    private void connectDevices() {

        int i;
        IOT_DEVICE_TYPE type;
        IotDevice device;
        for(i=0;i < deviceList.size();i++) {
            type = deviceList.get(i).getDeviceType();
            device = deviceList.get(i);
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
            Log.i(TAG, "dispositivo a conectar: " + device.hashCode());


        }
    }

    private void connectUnknownDevice(IotDeviceUnknown device, int position) {


        //Recepcion de los timeouts a los comandos
        Log.i(TAG, "conectando unknown device: " + device.getDeviceName() + "-- hash: " + device.hashCode());
        device.subscribeDevice();
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

                Log.i(TAG, "recibido status unknown " + device.getDeviceName() + "-- hash: " + device.hashCode());
                device.unSubscribeDevice();
                convertUnknownDevice(device, position);
                adapter.notifyItemChanged(position);

           }
        });

        //Comienzo del dispositivo
        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                device.unSubscribeDevice();
                convertUnknownDevice(device, position);
                adapter.notifyItemChanged(position);

            }
        });

        // comienzo de un programa
        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {
                device.unSubscribeDevice();
                convertUnknownDevice(device, position);
                adapter.notifyItemChanged(position);

            }
        });



        //Fin de un programa
device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
    @Override
    public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {
        device.unSubscribeDevice();
        convertUnknownDevice(device, position);
        adapter.notifyItemChanged(position);
    }
});



    }

    private void connectSwitchDevice(@NonNull IotDeviceSwitch device, int position) {

        Log.i(TAG, "conectando switch device: " + device.getDeviceName() + "-- hash: " + device.hashCode());
        device.subscribeDevice();
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
                Log.i(TAG, "recibido status switch " + device.getDeviceName() + "-- hash: " + device.hashCode());
                adapter.notifyItemChanged(position);

            }
        });

        ;

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


    private void modifyConfiguration(String deviceId, int position, IOT_DEVICE_TYPE type) {
        IotUsersDevices configuration;
        configuration = new IotUsersDevices(context);
        IotDevice dev;
        configuration.loadConfiguration();



        dev = configuration.getIotDeviceObject(siteName, roomName, deviceList.get(position).getDeviceId());
        dev.setDeviceType(type);
        configuration.saveConfiguration(context);
        configuration.reloadConfiguration();


        Log.i(TAG, "cambiado");
    }


    private void convertUnknownDevice(IotDeviceUnknown device, int position) {

        Log.i(TAG, "recibido convert " + device.getDeviceName() + "-- hash: " + device.hashCode());
        IOT_DEVICE_TYPE type;
        type = device.getDeviceType();
        switch (type) {
            case UNKNOWN:

                break;
            case INTERRUPTOR:
                IotDeviceSwitch deviceSwitch;
                deviceSwitch = device.unknown2Switch();
                modifyConfiguration(device.getDeviceId(), position, IOT_DEVICE_TYPE.INTERRUPTOR);
                deviceList.set(position, (IotDevice) deviceSwitch);
                adapter.notifyItemChanged(position);
                connectSwitchDevice((IotDeviceSwitch) deviceList.get(position), position);
                deviceSwitch.commandGetStatusDevice();

                Log.i(TAG, "ff");


                break;
            case THERMOMETER:
                IotDeviceThermometer deviceThermometer = device.unknown2Thermometer();
                deviceList.remove(position);
                deviceList.add(position, deviceThermometer);
                modifyConfiguration(device.getDeviceId(), position, IOT_DEVICE_TYPE.THERMOMETER);
                adapter.notifyItemChanged(position);
                break;
            case CRONOTERMOSTATO:
                IotDeviceThermostat deviceThermostat = device.unknown2Thermostat();
                deviceList.remove(position);
                deviceList.add(position, deviceThermostat);
                modifyConfiguration(device.getDeviceId(), position, IOT_DEVICE_TYPE.CRONOTERMOSTATO);
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
                Log.i(TAG, "preguntando " + deviceList.get(i).getDeviceName() + "-- hash: " + deviceList.get(i).hashCode());
                deviceList.get(i).commandGetStatusDevice();
                adapter.notifyItemChanged(i);
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

    @Override
    public void onDeleteDevice(int position) {

        Log.i(TAG, "Borrado device: " + deviceList.get(position).getDeviceName() + "-- hash: " + deviceList.get(position).hashCode());
        deviceList.get(position).unSubscribeDevice();
        deviceList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}