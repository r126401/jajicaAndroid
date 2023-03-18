package net.jajica.myhomeiot;

import android.content.Context;
import android.content.Intent;
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
import net.jajica.libiot.IOT_DEVICE_STATUS_CONNECTION;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IOT_TYPE_ALARM_DEVICE;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotDeviceUnknown;
import net.jajica.libiot.IotMqttConnection;

import java.util.ArrayList;


public class FragmentDevices extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IotDeviceAdapter.OnSelectedDevice, IotDeviceAdapter.OnAdapterOperationDeviceListener {

    private final String TAG = "FragmentDevices";
    RecyclerView recyclerView;
    GridLayoutManager grid;
    SwipeRefreshLayout swipeDeviceList;
    IotDeviceAdapter adapter;
    String siteName;
    String roomName;

    public ArrayList<IotDevice> getDeviceList() {
        return deviceList;
    }

    private ArrayList<IotDevice> deviceList;
    private Context context;


    public GridLayoutManager getGrid() {
        return grid;
    }

    private OnOperationDeviceListener onOperationDeviceListener;


    enum OPERATION_DEVICE {
        REFRESH_DEVICE,
        CREATE_DEVICE,
        DELETE_DEVICE,
        MODIFY_DEVICE,
        SELECTED_DEVICE,
        CUT_DEVICE
    }

    public interface OnOperationDeviceListener {
        IOT_OPERATION_CONFIGURATION_DEVICES onOperationDeviceListener(OPERATION_DEVICE operationDevice, IotDevice device, IOT_DEVICE_TYPE deviceType, int position);
    }

    public void setOnOperationDeviceListener(OnOperationDeviceListener onOperationDeviceListener) {
        this.onOperationDeviceListener = onOperationDeviceListener;
    }




    public FragmentDevices(ArrayList<IotDevice> deviceList, Context context, String siteName, String roomName) {
        this.deviceList = deviceList;
        this.context = context;
        this.siteName = siteName;
        this.roomName = roomName;
        if (this.deviceList == null) this.deviceList = new ArrayList<>();
    }





    public FragmentDevices() {
        // Required empty public constructor
    }

    public void setDeviceList(ArrayList<IotDevice> deviceList, OPERATION_DEVICE operationDevice, int position) {
        this.deviceList = deviceList;
        adapter.setDeviceList(deviceList);
        adapter.notifyDataSetChanged();

    }

    public int addNewDevice(IotDevice device) {


        int position;
        deviceList.add(device);
        position = deviceList.indexOf(device);
        Log.i(TAG, "device " + device.getDeviceId() + " añadido en posicion " + position);

        Log.i(TAG, "Se añade el dispositivo " + device.getDeviceId() + " hash: " + device.hashCode());
        adapter.setDeviceList(deviceList);
        return position;



    }

    public void connectNewDevice(String deviceId, String deviceName, IotMqttConnection cnx) {

        IotDeviceUnknown device;
        device = new IotDeviceUnknown();
        device.setCnx(cnx);
        device.setDeviceId(deviceId);
        device.setDeviceName(deviceName);
        device.setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        convertUnknownDevice(device);
        connectUnknownDevice(device);
        device.commandGetStatusDevice();




    }



    public void connectUnknownDevice(IotDeviceUnknown device) {

        device.subscribeDevice();

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                //device.unSubscribeDevice();
                notifyTimeoutUnknownDevice(device);

            }
        });

        device.setOnReceivedStatus(new IotDevice.OnReceivedStatus() {
            @Override
            public void onReceivedStatus(IOT_CODE_RESULT resultCode) {
                device.unSubscribeDevice();
                Log.i(TAG, "Recibido status de " + device.getDeviceId() + " como unknown" );
                convertUnknownDevice(device);

            }
        });

        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "Recibido Start de " + device.getDeviceId() + " como unknown" );
                device.unSubscribeDevice();
                convertUnknownDevice(device);
            }
        });


    }

    private void convertUnknownDevice(IotDeviceUnknown device) {

        Log.i(TAG, "recibido convert " + device.getDeviceName() + "-- hash: " + device.hashCode());
        IOT_DEVICE_TYPE type;
        IOT_OPERATION_CONFIGURATION_DEVICES result;
        int position;
        if (deviceList != null) {
            position = deviceList.size();
        } else {
            position = -1;
        }
        if (onOperationDeviceListener != null) {
            type = device.getDeviceType();
            switch (type) {

                case UNKNOWN:
                    if ((onOperationDeviceListener.onOperationDeviceListener(OPERATION_DEVICE.CREATE_DEVICE,
                            device, device.getDeviceType(), -1) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED)) {
                        adapter.notifyItemInserted(position);
                            //adapter.setDeviceList(deviceList);
                    } else {
                        Log.i(TAG, "ahora ya esta creado");
                    }
                    break;

                case INTERRUPTOR:
                    IotDeviceSwitch deviceSwitch = device.unknown2Switch();
                    if (((result = onOperationDeviceListener.onOperationDeviceListener(OPERATION_DEVICE.MODIFY_DEVICE,
                            deviceSwitch, deviceSwitch.getDeviceType(), -1)) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED)) {
                        Log.i(TAG, "convertUnknownDevice: Se ha insertado un dispositivo interruptor");
                        adapter.setDeviceList(deviceList);
                        connectSwitchDevice(deviceSwitch);
                        deviceSwitch.commandGetStatusDevice();
                    } else {
                    Log.e(TAG, "Error resultado: " + result.toString());
                }
                    break;
                case THERMOMETER:
                    IotDeviceThermometer deviceThermometer = device.unknown2Thermometer();
                    if ((onOperationDeviceListener.onOperationDeviceListener(OPERATION_DEVICE.MODIFY_DEVICE,
                            deviceThermometer, deviceThermometer.getDeviceType(), -1) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED)) {
                        connectThemometerDevice(deviceThermometer);
                        adapter.setDeviceList(deviceList);
                        deviceThermometer.commandGetStatusDevice();
                        Log.i(TAG, "Convertido status de " + deviceThermometer.getDeviceId() + " como termometro" );
                    } else {
                        Log.e(TAG, "Error");
                    }
                    break;
                case CRONOTERMOSTATO:
                    IotDeviceThermostat deviceThermostat = device.unknown2Thermostat();
                    if ((onOperationDeviceListener.onOperationDeviceListener(OPERATION_DEVICE.MODIFY_DEVICE,
                            deviceThermostat, deviceThermostat.getDeviceType(), -1) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED)) {
                        connectThermostatDevice(deviceThermostat);
                        adapter.setDeviceList(deviceList);
                        deviceThermostat.commandGetStatusDevice();
                        Log.i(TAG, "Convertido status de " + deviceThermostat.getDeviceId() + " como cronotermostato" );

                    } else {
                        Log.e(TAG, "Error");
                    }

                    break;
                default:

            }

        }



    }


    private void createListDevices() {

        adapter = new IotDeviceAdapter(getActivity(), R.id.recyclerDevices, deviceList, siteName, roomName);
        adapter.setOnAdapterOperationDeviceListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnSelectedDeviceListener(this);
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
        recyclerView.setLayoutManager( grid = new GridLayoutManager(getActivity(), 2));
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
                    connectUnknownDevice((IotDeviceUnknown) device);

                    break;
                case INTERRUPTOR:
                    connectSwitchDevice((IotDeviceSwitch) device);
                    break;
                case THERMOMETER:
                    connectThemometerDevice((IotDeviceThermometer) device);
                    break;
                case CRONOTERMOSTATO:
                    connectThermostatDevice((IotDeviceThermostat) device);
                    break;
            }
            Log.i(TAG, "dispositivo a conectar: " + device.hashCode());


        }
    }






    private void connectSwitchDevice(@NonNull IotDeviceSwitch device) {

        int position;
        position = deviceList.indexOf(device);
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
                if (position == -1) {
                    //addNewDevice(device);
                } else {
                    adapter.notifyItemChanged(position);
                }

                Log.i(TAG, "modificado status switch " + device.getDeviceName() + "-- hash: " + device.hashCode());
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



    private void connectThemometerDevice(IotDeviceThermometer device) {

        int position;
        position = deviceList.indexOf(device);
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
                Log.i(TAG, "recibido estatus en el termometro " + device.getDeviceName() + " hash: " + device.hashCode()) ;
                if (position == -1) {
                   //addNewDevice(device);
                } else {
                    adapter.notifyItemChanged(position);
                }

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

    private void connectThermostatDevice(IotDeviceThermostat device) {

        int position;
        position = deviceList.indexOf(device);
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
                if (position == -1) {
                    //addNewDevice(device);
                } else {
                    adapter.notifyItemChanged(position);
                }

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


    private void notifyTimeoutUnknownDevice(IotDeviceUnknown device) {

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    convertUnknownDevice(device);
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (NullPointerException exception) {
            Log.e(TAG, "La tarea para cancelar es erronea");
        }


    }


    @Override
    public void onSelectedDevice(IotDevice device) {

        device.object2Json();
        switch (device.getDeviceType()) {

            case INTERRUPTOR:
                if (device.getStatusConnection() == IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED) {
                    Intent launcherSwitch = new Intent(context, SwitchActivity.class);
                    device.object2Json();

                    launcherSwitch.putExtra(IOT_LABELS_JSON.DEVICES.getValorTextoJson(), device.getDispositivoJson().toString());
                    startActivity(launcherSwitch);

                }

                break;
            case THERMOMETER:
                if (device.getStatusConnection() == IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED) {
                    Intent launcherThermometer = new Intent(context, ThermometerActivity.class);
                    launcherThermometer.putExtra(IOT_LABELS_JSON.DEVICES.getValorTextoJson(), device.getDispositivoJson().toString());
                    startActivity(launcherThermometer);
                }

                break;
            case CRONOTERMOSTATO:
                if(device.getStatusConnection() == IOT_DEVICE_STATUS_CONNECTION.DEVICE_CONNECTED) {
                    Intent launcherThermostat = new Intent(context, ThermostatActivity.class);
                    launcherThermostat.putExtra(IOT_LABELS_JSON.DEVICES.getValorTextoJson(), device.getDispositivoJson().toString());
                    startActivity(launcherThermostat);
                }

                break;
        }

    }


    /**
     * La implementacion de este interface recibe la informacion de la operacion del dispositivo que se realiza desde el adapter
     * @param operationDevice Operacion sobre el dispositibo
     * @param device dispositivo implicado en la operacion
     * @param deviceType tipo de dispositivo implicado en la operacion
     * @param position posicion del dispositivo dentro del adapter
     * @return
     */

    @Override
    public IOT_OPERATION_CONFIGURATION_DEVICES onAdapterOperationDeviceListener(OPERATION_DEVICE operationDevice, IotDevice device, IOT_DEVICE_TYPE deviceType, int position) {

        IOT_OPERATION_CONFIGURATION_DEVICES result = IOT_OPERATION_CONFIGURATION_DEVICES.NO_CONFIGURATION;

        if (onOperationDeviceListener != null) {

            switch (operationDevice) {

                case CREATE_DEVICE:
                    result = onOperationDeviceListener.onOperationDeviceListener(
                            OPERATION_DEVICE.CREATE_DEVICE,
                            device,
                            deviceType,
                            position);
                    break;
                case DELETE_DEVICE:
                    result = onOperationDeviceListener.onOperationDeviceListener(
                            OPERATION_DEVICE.DELETE_DEVICE,
                            device,
                            deviceType, position);
                    break;
                case MODIFY_DEVICE:
                    result = onOperationDeviceListener.onOperationDeviceListener(
                            OPERATION_DEVICE.MODIFY_DEVICE,
                            device,
                            deviceType,
                            position);
                    break;
                case SELECTED_DEVICE:
                    break;
                case CUT_DEVICE:
                    onOperationDeviceListener.onOperationDeviceListener(
                            OPERATION_DEVICE.CUT_DEVICE,
                            device,
                            deviceType,
                            position);
                    break;
            }

        }
        return result;
    }


}