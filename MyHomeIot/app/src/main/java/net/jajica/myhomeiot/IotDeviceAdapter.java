package net.jajica.myhomeiot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_DEVICE_STATE_CONNECTION;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_DEVICE_USERS_RESULT;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
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

import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class IotDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "DevicesAdapter";
    private Context context;
    private int idLayout;
    private ArrayList<IotDevice> deviceList;
    private String siteName;
    private String roomName;

    private OnDeleteDevice onDeleteDevice;

    public void setOnDeleteDevice(OnDeleteDevice onDeleteDevice) {
        this.onDeleteDevice = onDeleteDevice;
    }

    public interface OnDeleteDevice {

        public void onDeleteDevice(int position);
    }


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

        ProgressBar progressBarUnknownDevice;


        public IotUnknownDeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenu = (AppCompatImageView) itemView.findViewById(R.id.imageMenu);
            imageConnectedDeviceUnknown = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDevice);
            imageDeviceOperation = (AppCompatImageView) itemView.findViewById(R.id.imageDeviceOperation);
            textDeviceUnknown = (TextInputEditText) itemView.findViewById(R.id.textdeviceUnknown);
            progressBarUnknownDevice = (ProgressBar) itemView.findViewById(R.id.progressBarUnknownDevice);
        }
    }


    static public class IotDeviceSwitchAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageSwitch;
        AppCompatImageView imageMenuSwitch;
        AppCompatImageView imageConnectedDeviceSwitch;
        TextInputEditText textDeviceSwitch;
        ProgressBar progressBarSwitchDevice;

        public IotDeviceSwitchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageSwitch);
            imageMenuSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageMenuSwitch);
            imageConnectedDeviceSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceSwitch);
            textDeviceSwitch = (TextInputEditText) itemView.findViewById(R.id.textdeviceSwitch);
            progressBarSwitchDevice = (ProgressBar) itemView.findViewById(R.id.progressBarSwitchDevice);
        }

    }

    static public class IotDeviceThermometerAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageMenuThermometer;
        AppCompatImageView imageConnectedDeviceThermometer;
        AppCompatTextView textTemperatureThermometer;
        TextInputEditText textDeviceThermometer;
        ProgressBar progressBarThermometerDevice;

        public IotDeviceThermometerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenuThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermometer);
            imageConnectedDeviceThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceThermometer);
            textTemperatureThermometer = (AppCompatTextView) itemView.findViewById(R.id.textTemperatureThermometer);
            textDeviceThermometer = (TextInputEditText) itemView.findViewById(R.id.textDeviceThermometer);
            progressBarThermometerDevice = (ProgressBar) itemView.findViewById(R.id.progressBarThermometerDevice);
        }
    }

    static public class IotDeviceThermostatAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textTemperatureThermostat;
        AppCompatTextView textThresholdThermostat;
        TextInputEditText textDeviceThermostat;
        AppCompatImageView imageMenuThermostat;
        AppCompatImageView imageHeating;
        AppCompatImageView imageConnectedDeviceThermostat;
        ProgressBar progressBarThermostatDevice;

        public IotDeviceThermostatAdapterViewHolder(@NonNull View itemView) {

            super(itemView);
            textTemperatureThermostat = (AppCompatTextView) itemView.findViewById(R.id.textTemperatureThermostat);
            textThresholdThermostat = (AppCompatTextView) itemView.findViewById(R.id.textThresholdThermostat);
            textDeviceThermostat = (TextInputEditText) itemView.findViewById(R.id.textDeviceThermostat);
            imageMenuThermostat = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermostat);
            imageHeating = (AppCompatImageView) itemView.findViewById(R.id.imageHeating);
            imageConnectedDeviceThermostat = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceThermostat);
            progressBarThermostatDevice = (ProgressBar) itemView.findViewById(R.id.progressBarThermostatDevice);

        }
    }



    private void paintThermostatDevice(IotDeviceThermostatAdapterViewHolder holder, int position) {
        paintDevice(holder.textDeviceThermostat, holder.imageMenuThermostat, position);

    }

    private void paintThermometerDevice(IotDeviceThermometerAdapterViewHolder holder, int position) {

        IotDeviceThermometer device;
        device = (IotDeviceThermometer) deviceList.get(position);
        paintDevice(holder.textDeviceThermometer, holder.imageMenuThermometer, position);
        //paintStatusIconDevice(holder.progressBarThermometerDevice, holder.imageConnectedDeviceThermometer, holder.imageMenuThermometer, position);
        paintStatusIconThermometerDevice(holder, position);
        holder.textDeviceThermometer.setText(device.getDeviceName());
        String dato;
        MyHomeIotTools tools;
        Double temp;
        tools = new MyHomeIotTools();
        temp = tools.roundData(device.getTemperature(), 1);

        holder.textTemperatureThermometer.setText(String.valueOf(temp) + " ºC");


    }




    private void paintSwitchDevice(IotDeviceSwitchAdapterViewHolder holder, int position) {

        IotDeviceSwitch device;
        device = (IotDeviceSwitch) deviceList.get(position);
        paintDevice(holder.textDeviceSwitch, holder.imageMenuSwitch, position);
        paintStatusIconSwitchDevice(holder, position);
        if (device.getRelay() == null) return;
        switch (device.getRelay()) {

            case OFF:
                holder.imageSwitch.setImageResource(R.drawable.ic_switch_off);
                break;
            case ON:
                holder.imageSwitch.setImageResource(R.drawable.ic_switch_on);
                break;
            case UNKNOWN:
                holder.imageSwitch.setImageResource(R.drawable.ic_switch_unknown);
                break;
        }
        Log.i("jj", "kk");


    }

    private void paintDevice(TextInputEditText editText, AppCompatImageView imageMenu, int position) {

        editText.setText(deviceList.get(position).getDeviceName());
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processMenuDevice(imageMenu, editText, position);
            }
        });
    }

    private void paintUnknownDevice(IotUnknownDeviceAdapterViewHolder holder, int position) {

        paintDevice(holder.textDeviceUnknown, holder.imageMenu, position);
        paintStatusIconDevice(holder.progressBarUnknownDevice, holder.imageConnectedDeviceUnknown, holder.imageDeviceOperation, position);
    }



    private void modifyNameDevice(TextInputEditText editText, int position) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = s.toString();
                int index;
                if ((index = data.indexOf("\n")) > 0) {
                    data = data.replace("\n","");
                    editText.setText(data);
                    editText.setEnabled(false);
                    deviceList.get(position).setDeviceName(data);
                    notifyItemChanged(position);
                    IotUsersDevices configuration;
                    configuration = new IotUsersDevices(context);
                    configuration.loadConfiguration();
                    IotDevice device;

                    device = configuration.getIotDeviceObject(siteName, roomName, deviceList.get(position).getDeviceId());
                    if (device != null) {
                        device.setDeviceName(data);
                        configuration.saveConfiguration(context);
                    }


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setEnabled(true);
        editText.requestFocus();
        MyHomeIotTools tools;
        tools = new MyHomeIotTools(context);
        editText.setSelection(editText.getText().length());
        tools.showKeyboard(InputMethodManager.SHOW_FORCED);
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
            configuration.saveConfiguration(context);
            //deviceList.remove(position);
            //notifyItemRemoved(position);
            onDeleteDevice.onDeleteDevice(position);
        }
    }
    private void processMenuDevice(AppCompatImageView imageMenu , TextInputEditText editText, int position) {
        PopupMenu menu;
        menu = new PopupMenu(context, imageMenu);
        menu.inflate(R.menu.menu_device);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            menu.setForceShowIcon(true);
        }
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.item_rename_device):
                        modifyNameDevice(editText, position);
                        break;
                    case (R.id.item_delete_device):
                        deleteDevice(position);
                        Log.i(TAG, "delte");
                        break;

                }
                return false;
            }
        });

    }

    private void paintStatusIconDevice(ProgressBar progressBar, AppCompatImageView imageConnection, AppCompatImageView imageDevice, int position) {

        switch (deviceList.get(position).getConnectionState()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_switch_unknown);
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                progressBar.setVisibility(View.VISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_unknown_device);
                break;


        }

    }

    private void paintStatusIconUnknownDevice(ProgressBar progressBar, AppCompatImageView imageConnection, AppCompatImageView imageDevice, int position) {

        switch (deviceList.get(position).getConnectionState()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_switch_unknown);
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                progressBar.setVisibility(View.VISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_unknown_device);
                break;


        }

    }


    private void paintStatusIconSwitchDevice(IotDeviceSwitchAdapterViewHolder holder, int position) {

        switch (deviceList.get(position).getConnectionState()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                holder.progressBarSwitchDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                holder.progressBarSwitchDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_connect_nok);
                holder.imageSwitch.setImageResource(R.drawable.ic_switch_unknown);
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                holder.progressBarSwitchDevice.setVisibility(View.VISIBLE);
                holder.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_connect_nok);
                holder.imageSwitch.setImageResource(R.drawable.ic_unknown_device);
                break;


        }

    }

    private void paintStatusIconThermometerDevice(IotDeviceThermometerAdapterViewHolder holder, int position) {

        switch (deviceList.get(position).getConnectionState()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                holder.progressBarThermometerDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceThermometer.setImageResource(R.drawable.ic_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                holder.progressBarThermometerDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceThermometer.setImageResource(R.drawable.ic_connect_nok);
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                holder.progressBarThermometerDevice.setVisibility(View.VISIBLE);
                holder.imageConnectedDeviceThermometer.setImageResource(R.drawable.ic_connect_nok);
                break;


        }

    }


    private void paintStatusIconThermostatDevice(ProgressBar progressBar, AppCompatImageView imageConnection, AppCompatImageView imageDevice, int position) {

        switch (deviceList.get(position).getConnectionState()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_switch_unknown);
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                progressBar.setVisibility(View.VISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_unknown_device);
                break;
        }


    }


}
