package net.jajica.myhomeiot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_DEVICE_STATE_CONNECTION;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_DEVICE_USERS_RESULT;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotDeviceUnknown;
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.ActivityMainBinding;
import net.jajica.myhomeiot.databinding.SwitchDeviceBinding;
import net.jajica.myhomeiot.databinding.ThermometerDeviceBinding;
import net.jajica.myhomeiot.databinding.ThermostatDeviceBinding;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class IotDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "DevicesAdapter";
    private Context context;
    private int idLayout;
    private ArrayList<IotDevice> deviceList;
    private String siteName;
    private String roomName;
    private SwitchDeviceBinding switchDeviceBinding;
    private ThermometerDeviceBinding thermometerDeviceBinding;
    private ThermostatDeviceBinding thermostatDeviceBinding;

    public IotDeviceAdapter(Context context, int idLayout, ArrayList<IotDevice> deviceList, String siteName, String roomName) {
        this.context = context;
        this.idLayout = idLayout;
        this.deviceList = deviceList;
        this.siteName = siteName;
        this.roomName = roomName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        IOT_DEVICE_TYPE type = IOT_DEVICE_TYPE.UNKNOWN;
        type = type.fromId(viewType);
        LayoutInflater inflater;
        switch (type) {

            case UNKNOWN:
                view = LayoutInflater.from(context).inflate(R.layout.unknown_device, parent, false);
                return new IotUnknownDeviceAdapterViewHolder(view);
            case INTERRUPTOR:
                view = LayoutInflater.from(context).inflate(R.layout.switch_device, parent, false);
                return new IotDeviceSwitchAdapterViewHolder(view);
            case THERMOMETER:
                view = LayoutInflater.from(context).inflate(R.layout.thermometer_device, parent, false);
                return new IotDeviceThermometerAdapterViewHolder(view);

            case CRONOTERMOSTATO:
                view = LayoutInflater.from(context).inflate(R.layout.thermostat_device, parent, false);
                return new IotDeviceThermostatAdapterViewHolder(view);
            default:
                break;
        }


        return new IotUnknownDeviceAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch(deviceList.get(position).getDeviceType()) {

            case UNKNOWN:
                paintUnknownDevice((IotUnknownDeviceAdapterViewHolder) holder, position);
                break;
            case INTERRUPTOR:
                paintSwitchDevice((IotDeviceSwitchAdapterViewHolder) holder, position);
                break;
            case THERMOMETER:
                paintThermometerDevice((IotDeviceThermometerAdapterViewHolder) holder, position);
                break;
            case CRONOTERMOSTATO:
                paintThermostatDevice((IotDeviceThermostatAdapterViewHolder) holder, position);
                break;
            default:
                break;
        }

        Log.i(TAG, "j");
    }



    @Override
    public int getItemViewType(int position) {
        IOT_DEVICE_TYPE deviceType;
        deviceType = deviceList.get(position).getDeviceType();
        return deviceType.getValorTipoDispositivo();

    }

    @Override
    public int getItemCount() {
        if (deviceList == null) {
            return 0;
        }
        return deviceList.size();
    }

    static public class IotUnknownDeviceAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageMenu;
        AppCompatImageView imageConnectedDeviceUnknown;
        TextInputEditText textDeviceUnknown;
        AppCompatImageView imageDeviceOperation;


        public IotUnknownDeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenu = (AppCompatImageView) itemView.findViewById(R.id.imageMenu);
            imageConnectedDeviceUnknown = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDevice);
            imageDeviceOperation = (AppCompatImageView) itemView.findViewById(R.id.imageDeviceOperation);
            textDeviceUnknown = (TextInputEditText) itemView.findViewById(R.id.textdevice);
        }
    }


    static public class IotDeviceSwitchAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageSwitch;
        AppCompatImageView imageMenuSwitch;
        AppCompatImageView imageConnectedDeviceSwitch;
        AppCompatTextView textDeviceSwitch;

        public IotDeviceSwitchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageSwitch);
            imageMenuSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageMenuSwitch);
            imageConnectedDeviceSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceSwitch);
            textDeviceSwitch = (AppCompatTextView) itemView.findViewById(R.id.textdeviceSwitch);
        }

    }

    static public class IotDeviceThermometerAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageMenuThermometer;
        AppCompatImageView imageConnectedDeviceThermometer;
        AppCompatTextView textTemperatureThermometer;
        AppCompatTextView textDeviceThermometer;

        public IotDeviceThermometerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenuThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermometer);
            imageConnectedDeviceThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceThermometer);
            textTemperatureThermometer = (AppCompatTextView) itemView.findViewById(R.id.textTemperatureThermometer);
            textDeviceThermometer = (AppCompatTextView) itemView.findViewById(R.id.textDeviceThermometer);
        }
    }

    static public class IotDeviceThermostatAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textTemperatureThermostat;
        AppCompatTextView textThresholdThermostat;
        AppCompatTextView textDeviceThermostat;
        AppCompatImageView imageMenuThermostat;
        AppCompatImageView imageHeating;
        AppCompatImageView imageConnectedDeviceThermostat;

        public IotDeviceThermostatAdapterViewHolder(@NonNull View itemView) {

            super(itemView);
            textTemperatureThermostat = (AppCompatTextView) itemView.findViewById(R.id.textTemperatureThermostat);
            textThresholdThermostat = (AppCompatTextView) itemView.findViewById(R.id.textThresholdThermostat);
            textDeviceThermostat = (AppCompatTextView) itemView.findViewById(R.id.textDeviceThermostat);
            imageMenuThermostat = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermostat);
            imageHeating = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermostat);
            imageConnectedDeviceThermostat = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceThermostat);

        }
    }



    private void paintThermostatDevice(IotDeviceThermostatAdapterViewHolder holder, int position) {

        IotDeviceThermostat thermostat;

        thermostat = (IotDeviceThermostat) deviceList.get(position);
        holder.textDeviceThermostat.setText(thermostat.getDeviceName());
        holder.textThresholdThermostat.setText(String.valueOf(thermostat.getThresholdTemperature()));
        holder.textTemperatureThermostat.setText(String.valueOf(thermostat.getTemperature()));
        IOT_DEVICE_STATE_CONNECTION status = thermostat.getConnectionState();
        switch (status) {


            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                break;
            case DEVICE_DISCONNECTED:
                break;
            case DEVICE_WAITING_RESPONSE:
                break;
            case DEVICE_ERROR_COMMUNICATION:
                break;
            case DEVICE_ERROR_SUBSCRIPTION:
                break;
        }

        holder.imageMenuThermostat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void paintThermometerDevice(IotDeviceThermometerAdapterViewHolder holder, int position) {

        IotDeviceThermometer thermometer;
        thermometer = (IotDeviceThermometer) deviceList.get(position);
        holder.textDeviceThermometer.setText(thermometer.getDeviceName());
        holder.textTemperatureThermometer.setText(String.valueOf(thermometer.getTemperature()));
        IOT_DEVICE_STATE_CONNECTION status = thermometer.getConnectionState();
        switch (status) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                break;
            case DEVICE_DISCONNECTED:
                break;
            case DEVICE_WAITING_RESPONSE:
                break;
            case DEVICE_ERROR_COMMUNICATION:
                break;
            case DEVICE_ERROR_SUBSCRIPTION:
                break;
        }

        holder.imageMenuThermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void paintSwitchDevice(IotDeviceSwitchAdapterViewHolder holder, int position) {
        holder.textDeviceSwitch.setText(deviceList.get(position).getDeviceName());
        IOT_DEVICE_STATE_CONNECTION status = deviceList.get(position).getConnectionState();
        switch (status) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                break;
            case DEVICE_DISCONNECTED:
                break;
            case DEVICE_WAITING_RESPONSE:
                break;
            case DEVICE_ERROR_COMMUNICATION:
                break;
            case DEVICE_ERROR_SUBSCRIPTION:
                break;
        }

        holder.imageMenuSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu;
                menu = new PopupMenu(context, holder.imageMenuSwitch);
                menu.inflate(R.menu.menu_device);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    menu.setForceShowIcon(true);
                }
                menu.show();
                execMenuSwitchDevice(menu, context);

            }
        });

    }

    private void execMenuSwitchDevice(PopupMenu menu, Context context) {

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.item_rename_device):
                        break;
                    case (R.id.item_delete_device):
                        Log.i(TAG, "delte");
                        break;

                }
                return false;
            }
        });
    }

    private void paintUnknownDevice(IotUnknownDeviceAdapterViewHolder holder, int position) {

        holder.textDeviceUnknown.setText(deviceList.get(position).getDeviceName());
        IOT_DEVICE_STATE_CONNECTION status = deviceList.get(position).getConnectionState();
        switch (status) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                break;
            case DEVICE_DISCONNECTED:
                break;
            case DEVICE_WAITING_RESPONSE:
                break;
            case DEVICE_ERROR_COMMUNICATION:
                break;
            case DEVICE_ERROR_SUBSCRIPTION:
                break;
        }
        holder.imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu;
                menu = new PopupMenu(context, holder.imageMenu);
                menu.inflate(R.menu.menu_device);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    menu.setForceShowIcon(true);
                }
                menu.show();
                execMenuUnknownDevice(menu, context, position, holder);

            }
        });
    }

    private void execMenuUnknownDevice(PopupMenu menu, Context context, int position, IotUnknownDeviceAdapterViewHolder holder) {

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case (R.id.item_rename_device):
                        modifyNameDevice(position, holder);
                        break;
                    case (R.id.item_delete_device):
                        Log.i(TAG, "delte");
                        deleteDevice(position);
                        break;

                }
                return false;
            }
        });
    }

    private void deleteDevice(int position) {

        IotDevice device;
        IotUsersDevices configuration;
        IotRoomsDevices rooms;
        IotSitesDevices site;
        configuration = new IotUsersDevices(context);
        configuration.loadConfiguration();
        site = configuration.searchSiteObject(siteName);
        device = configuration.getIotDeviceObject(siteName, roomName, deviceList.get(position).getDeviceId());

        if(device.getDeviceId().equals(deviceList.get(position).getDeviceId())) {
            configuration.deleteIotDevice(device.getDeviceId(), siteName, roomName);
            deviceList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void modifyNameDevice(int position, IotUnknownDeviceAdapterViewHolder holder) {

        holder.textDeviceUnknown.setEnabled(true);



    }



}
