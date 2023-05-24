package net.jajica.myhomeiot;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_DEVICE_STATUS;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotUsersDevices;

import java.util.ArrayList;

public class IotDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "DevicesAdapter";
    private Context context;
    protected int idLayout;
    private ArrayList<IotDevice> deviceList;
    private String siteName;
    private String roomName;



    private OnDeleteDevice onDeleteDeviceListener;
    private OnSelectedDevice onSelectedDeviceListener;

    private OnAdapterOperationDeviceListener onAdapterOperationDeviceListener;

 public interface OnAdapterOperationDeviceListener {
     IOT_OPERATION_CONFIGURATION_DEVICES onAdapterOperationDeviceListener(FragmentDevices.OPERATION_DEVICE operationDevice, IotDevice device, IOT_DEVICE_TYPE deviceType, int position);
 }

    public void setOnAdapterOperationDeviceListener(OnAdapterOperationDeviceListener onAdapterOperationDeviceListener) {
        this.onAdapterOperationDeviceListener = onAdapterOperationDeviceListener;
    }

    public void setOnDeleteDeviceListener(OnDeleteDevice onDeleteDeviceListener) {
        this.onDeleteDeviceListener = onDeleteDeviceListener;
    }

    public interface OnDeleteDevice {

        public void onDeleteDevice(String deviceId, int position);
    }

    public interface OnSelectedDevice {
        public void onSelectedDevice(IotDevice device);
    }


    public void setOnSelectedDeviceListener(OnSelectedDevice onSelectedDeviceListener) {
        this.onSelectedDeviceListener = onSelectedDeviceListener;
    }

    public IotDeviceAdapter(Context context, int idLayout, ArrayList<IotDevice> deviceList, String siteName, String roomName) {
        this.context = context;
        this.idLayout = idLayout;
        this.deviceList = deviceList;
        this.siteName = siteName;
        this.roomName = roomName;
        if (this.deviceList == null) this.deviceList = new ArrayList<>();
    }

    public void setDeviceList(ArrayList<IotDevice> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
        Log.i(TAG, "FragmentDevices insertado un nuevo dispositivo en el adapter");
    }

    public ArrayList<IotDevice> getDeviceList() {
        return deviceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        IOT_DEVICE_TYPE type = IOT_DEVICE_TYPE.UNKNOWN;
        type = type.fromId(viewType);
        LayoutInflater inflater;
        IotDeviceSwitchAdapterViewHolder holderSwitch;
        IotDeviceThermometerAdapterViewHolder holderThermometer;
        IotDeviceThermostatAdapterViewHolder holderThermostat;
        IotUnknownDeviceAdapterViewHolder holderUnknown;

        switch (type) {


            case UNKNOWN:
                view = LayoutInflater.from(context).inflate(R.layout.unknown_device, parent, false);;
                holderUnknown = new IotUnknownDeviceAdapterViewHolder(view);

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holderUnknown.getLayoutPosition();
                        if (onAdapterOperationDeviceListener != null) {
                            onAdapterOperationDeviceListener.onAdapterOperationDeviceListener(FragmentDevices.OPERATION_DEVICE.CUT_DEVICE,
                                    deviceList.get(position), deviceList.get(position).getDeviceType(), position);
                            showCuttingModeUnknown(holderUnknown, position, true);
                        }
                        Log.i(TAG, "kk");
                        return true;
                    }
                });


                return holderUnknown;
            case INTERRUPTOR:
                view = LayoutInflater.from(context).inflate(R.layout.switch_device, parent, false);

                holderSwitch = new IotDeviceSwitchAdapterViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holderSwitch.getLayoutPosition();
                        if (onSelectedDeviceListener != null) {
                            onSelectedDeviceListener.onSelectedDevice(deviceList.get(position));

                        }
                        Log.i(TAG, "iii");
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        int position = holderSwitch.getLayoutPosition();

                        if (onAdapterOperationDeviceListener != null) {
                            onAdapterOperationDeviceListener.onAdapterOperationDeviceListener(FragmentDevices.OPERATION_DEVICE.CUT_DEVICE,
                                    deviceList.get(position), deviceList.get(position).getDeviceType(), position);
                            View itemView = holderSwitch.itemView;
                            holderSwitch.imageSwitch.setImageResource(R.drawable.ic_action_cut);
                            showCuttingModeSwitch(holderSwitch, position, true);
                            }
                        Log.i(TAG, "kk");
                        notifyItemChanged(position);
                        return true;
                    }
                });
                //return new IotDeviceSwitchAdapterViewHolder(view);
                return holderSwitch;
            case THERMOMETER:
                view = LayoutInflater.from(context).inflate(R.layout.thermometer_device, parent, false);
                holderThermometer = new IotDeviceThermometerAdapterViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holderThermometer.getLayoutPosition();
                        if (onSelectedDeviceListener != null) {
                            onSelectedDeviceListener.onSelectedDevice(deviceList.get(position));

                        }
                        Log.i(TAG, "iii");
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holderThermometer.getLayoutPosition();
                        if (onAdapterOperationDeviceListener != null) {
                            onAdapterOperationDeviceListener.onAdapterOperationDeviceListener(FragmentDevices.OPERATION_DEVICE.CUT_DEVICE,
                                    deviceList.get(position), deviceList.get(position).getDeviceType(), position);
                            showCuttingModeThermometer(holderThermometer, position, true);
                        }
                        Log.i(TAG, "kk");
                        return true;
                    }
                });
                //return new IotDeviceSwitchAdapterViewHolder(view);
                return holderThermometer;


            case CRONOTERMOSTATO:
                view = LayoutInflater.from(context).inflate(R.layout.thermostat_device, parent, false);
                holderThermostat = new IotDeviceThermostatAdapterViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holderThermostat.getLayoutPosition();
                        if (onSelectedDeviceListener != null) {
                            onSelectedDeviceListener.onSelectedDevice(deviceList.get(position));
                        }
                        Log.i(TAG, "iii");
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holderThermostat.getLayoutPosition();
                        if (onAdapterOperationDeviceListener != null) {
                            onAdapterOperationDeviceListener.onAdapterOperationDeviceListener(FragmentDevices.OPERATION_DEVICE.CUT_DEVICE,
                                    deviceList.get(position), deviceList.get(position).getDeviceType(), position);
                            showCuttingModeThermostat(holderThermostat, position, true);
                        }
                        Log.i(TAG, "kk");
                        return true;
                    }
                });
                //return new IotDeviceSwitchAdapterViewHolder(view);
                return holderThermostat;

            default:
                break;
        }


        return new IotUnknownDeviceAdapterViewHolder(view);

    }

    private void showCuttingModeThermostat(IotDeviceThermostatAdapterViewHolder holderThermostat, int position, boolean b) {

     if (b) {
         holderThermostat.textThresholdThermostat.setVisibility(View.INVISIBLE);
         holderThermostat.textTemperatureThermostat.setVisibility(View.INVISIBLE);
         holderThermostat.imageHeating.setVisibility(View.INVISIBLE);
         holderThermostat.imageCut.setVisibility(View.VISIBLE);
     } else {
         holderThermostat.textThresholdThermostat.setVisibility(View.VISIBLE);
         holderThermostat.textTemperatureThermostat.setVisibility(View.VISIBLE);
         holderThermostat.imageHeating.setVisibility(View.VISIBLE);
         holderThermostat.imageCut.setVisibility(View.INVISIBLE);
     }

    }

    private void showCuttingModeThermometer(IotDeviceThermometerAdapterViewHolder holderThermometer, int position, boolean b) {

     if (b) {
         holderThermometer.textTemperatureThermometer.setVisibility(View.INVISIBLE);
         holderThermometer.imageCut.setVisibility(View.VISIBLE);
     } else {
         holderThermometer.textTemperatureThermometer.setVisibility(View.VISIBLE);
         holderThermometer.imageCut.setVisibility(View.INVISIBLE);
     }
    }

    private void showCuttingModeSwitch(IotDeviceSwitchAdapterViewHolder holderSwitch, int position, boolean b) {

     if (b) {
         holderSwitch.imageSwitch.setVisibility(View.INVISIBLE);
         holderSwitch.imageCut.setVisibility(View.VISIBLE);
     } else {
         holderSwitch.imageSwitch.setVisibility(View.VISIBLE);
         holderSwitch.imageCut.setVisibility(View.INVISIBLE);

     }
    }

    private void showCuttingModeUnknown(IotUnknownDeviceAdapterViewHolder holderUnknown, int position, boolean b) {

     if (b) {
         holderUnknown.imageDeviceOperation.setVisibility(View.INVISIBLE);
         holderUnknown.imageCut.setVisibility(View.VISIBLE);
     } else {
         holderUnknown.imageDeviceOperation.setVisibility(View.VISIBLE);
         holderUnknown.imageCut.setVisibility(View.INVISIBLE);
     }



    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.i(TAG, "Pintamos " + deviceList.get(position).getDeviceId() + " hash:" + deviceList.get(position).hashCode());
        switch(deviceList.get(position).getDeviceType()) {

            case UNKNOWN:
                paintUnknownDevice((IotUnknownDeviceAdapterViewHolder) holder, position);
                paintStatusDeviceUnknown((IotUnknownDeviceAdapterViewHolder) holder, position);
                break;
            case INTERRUPTOR:
                paintSwitchDevice((IotDeviceSwitchAdapterViewHolder) holder, position);
                paintStatusSwitchDevice((IotDeviceSwitchAdapterViewHolder) holder, position);
                break;
            case THERMOMETER:
                paintThermometerDevice((IotDeviceThermometerAdapterViewHolder) holder, position);
                paintStatusThermometerDevice((IotDeviceThermometerAdapterViewHolder) holder, position);
                 break;
            case CRONOTERMOSTATO:
                paintThermostatDevice((IotDeviceThermostatAdapterViewHolder) holder, position);
                paintStatusThermostatDevice((IotDeviceThermostatAdapterViewHolder) holder, position);

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
        int i;
        for (i=0;i<deviceList.size();i++) {
            Log.i(TAG, "FragmentDevices, el tamaño del adapter es " + deviceList.size() + " hash: " + deviceList.get(i).hashCode());
        }
        Log.i(TAG, "FragmentDevices, el tamaño del adapter es " + deviceList.size());
        return deviceList.size();
    }

    static public class IotUnknownDeviceAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageMenu;
        AppCompatImageView imageConnectedDeviceUnknown;
        AppCompatTextView textDeviceUnknown;
        AppCompatImageView imageDeviceOperation;

        ProgressBar progressBarUnknownDevice;

        AppCompatImageView imageCut;


        public IotUnknownDeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenu = (AppCompatImageView) itemView.findViewById(R.id.imageMenu);
            imageConnectedDeviceUnknown = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDevice);
            imageDeviceOperation = (AppCompatImageView) itemView.findViewById(R.id.imageDeviceOperation);
            textDeviceUnknown = (AppCompatTextView) itemView.findViewById(R.id.textdeviceUnknown);
            progressBarUnknownDevice = (ProgressBar) itemView.findViewById(R.id.progressBarUnknownDevice);
            imageCut = (AppCompatImageView) itemView.findViewById(R.id.imageCut);

        }
    }


    static public class IotDeviceSwitchAdapterViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "IotDeviceSwitchAdapterViewHolder";
        AppCompatImageView imageSwitch;
        AppCompatImageView imageMenuSwitch;
        AppCompatImageView imageConnectedDeviceSwitch;
        AppCompatTextView textDeviceSwitch;
        ProgressBar progressBarSwitchDevice;
        AppCompatImageView imageCut;




        public IotDeviceSwitchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageSwitch);
            imageMenuSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageMenuSwitch);
            imageConnectedDeviceSwitch = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceSwitch);
            textDeviceSwitch = (AppCompatTextView) itemView.findViewById(R.id.textdeviceSwitch);
            progressBarSwitchDevice = (ProgressBar) itemView.findViewById(R.id.progressBarSwitchDevice);
            imageCut = (AppCompatImageView) itemView.findViewById(R.id.imageCut);
        }



    }

    static public class IotDeviceThermometerAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageMenuThermometer;
        AppCompatImageView imageConnectedDeviceThermometer;
        AppCompatTextView textTemperatureThermometer;
        AppCompatTextView textDeviceThermometer;
        ProgressBar progressBarThermometerDevice;
        AppCompatImageView imageThermometer;

        AppCompatImageView imageCut;

        public IotDeviceThermometerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenuThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermometer);
            imageConnectedDeviceThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceThermometer);
            textTemperatureThermometer = (AppCompatTextView) itemView.findViewById(R.id.textTemperatureThermometer);
            textDeviceThermometer = (AppCompatTextView) itemView.findViewById(R.id.textDeviceThermometer);
            progressBarThermometerDevice = (ProgressBar) itemView.findViewById(R.id.progressBarThermometerDevice);
            imageThermometer = (AppCompatImageView) itemView.findViewById(R.id.imageThermometer);
            imageCut = (AppCompatImageView) itemView.findViewById(R.id.imageCut);

        }
    }

    static public class IotDeviceThermostatAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textTemperatureThermostat;
        AppCompatTextView textThresholdThermostat;
        AppCompatTextView textDeviceThermostat;
        AppCompatImageView imageMenuThermostat;
        AppCompatImageView imageHeating;
        AppCompatImageView imageConnectedDeviceThermostat;
        ProgressBar progressBarThermostatDevice;

        AppCompatImageView imageCut;
        public IotDeviceThermostatAdapterViewHolder(@NonNull View itemView) {

            super(itemView);
            textTemperatureThermostat = (AppCompatTextView) itemView.findViewById(R.id.textTemperatureThermostat);
            textThresholdThermostat = (AppCompatTextView) itemView.findViewById(R.id.textThresholdThermostat);
            textDeviceThermostat = (AppCompatTextView) itemView.findViewById(R.id.textDeviceThermostat);
            imageMenuThermostat = (AppCompatImageView) itemView.findViewById(R.id.imageMenuThermostat);
            imageHeating = (AppCompatImageView) itemView.findViewById(R.id.imageHeating);
            imageConnectedDeviceThermostat = (AppCompatImageView) itemView.findViewById(R.id.imageConnectedDeviceThermostat);
            progressBarThermostatDevice = (ProgressBar) itemView.findViewById(R.id.progressBarThermostatDevice);
            imageCut = (AppCompatImageView) itemView.findViewById(R.id.imageCut);

        }
    }



    private void paintThermostatDevice(IotDeviceThermostatAdapterViewHolder holder, int position) {
        IotDeviceThermostat device;
        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        device = (IotDeviceThermostat) deviceList.get(position);
        paintDevice(holder.textDeviceThermostat, holder.imageMenuThermostat, position);
        paintStatusConnectionThermostatDevice(holder, position);



    }

    private void paintThermometerDevice(IotDeviceThermometerAdapterViewHolder holder, int position) {

        IotDeviceThermometer device;
        device = (IotDeviceThermometer) deviceList.get(position);
        paintDevice(holder.textDeviceThermometer, holder.imageMenuThermometer, position);
        //paintStatusIconDevice(holder.progressBarThermometerDevice, holder.imageConnectedDeviceThermometer, holder.imageMenuThermometer, position);
        paintStatusConnectionThermometerDevice(holder, position);
        /*
        holder.textDeviceThermometer.setText(device.getDeviceName());
        String dato;
        MyHomeIotTools tools;
        Double temp;
        tools = new MyHomeIotTools();
        temp = tools.roundData(device.getTemperature(), 1);

        holder.textTemperatureThermometer.setText(String.valueOf(temp) + " ºC");


         */

    }




    private void paintSwitchDevice(IotDeviceSwitchAdapterViewHolder holder, int position)  {

        IotDeviceSwitch device;
        device = (IotDeviceSwitch) deviceList.get(position);
        paintDevice(holder.textDeviceSwitch, holder.imageMenuSwitch, position);
        paintStatusConnectionSwitchDevice(holder, position);
        if (device.getRelay() == null) return;
        switch (device.getRelay()) {

            case OFF:
                holder.imageSwitch.setImageResource(R.drawable.ic_switch_off);
                break;
            case ON:
                holder.imageSwitch.setImageResource(R.drawable.ic_switch_on);
                break;
            case UNKNOWN:
                holder.imageSwitch.setImageResource(R.drawable.ic_unknown_device);
                break;
        }
        Log.i("jj", "kk");




    }

    private void paintDevice(AppCompatTextView editText, AppCompatImageView imageMenu, int position) {

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
        paintStatusConnectionDevice(holder.progressBarUnknownDevice, holder.imageConnectedDeviceUnknown, holder.imageDeviceOperation, position);
    }





    private void deleteDevice(int position) {

        String deviceId = deviceList.get(position).getDeviceId();

        if (onAdapterOperationDeviceListener != null) {
            if (onAdapterOperationDeviceListener.onAdapterOperationDeviceListener
                    (FragmentDevices.OPERATION_DEVICE.DELETE_DEVICE, deviceList.get(position),
                            deviceList.get(position).getDeviceType(), position) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_REMOVED) {
            }

        }
        /*
        if (onDeleteDeviceListener != null) {
            onDeleteDeviceListener.onDeleteDevice(deviceId, position);
        }
        notifyItemRemoved(position);

         */
    }

    private void processMenuDevice(AppCompatImageView imageMenu , AppCompatTextView editText, int position) {
        PopupMenu menu;
        menu = new PopupMenu(context, imageMenu);
        menu.setGravity(Gravity.CENTER);
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
                        if (onAdapterOperationDeviceListener != null) {
                            onAdapterOperationDeviceListener.onAdapterOperationDeviceListener(
                                    FragmentDevices.OPERATION_DEVICE.RENAME_DEVICE,
                                    deviceList.get(position),
                                    deviceList.get(position).getDeviceType(),
                                    position );
                        }
                        break;
                    case (R.id.item_delete_device):
                        deleteDevice(position);

                        Log.i(TAG, "delete");
                        break;

                }
                return false;
            }
        });

    }

    private void paintStatusConnectionDevice(ProgressBar progressBar, AppCompatImageView imageConnection, AppCompatImageView imageDevice, int position) {

        switch (deviceList.get(position).getStatusConnection()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_ok);
                break;
            case DEVICE_DISCONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_connect_nok);
                imageDevice.setImageResource(R.drawable.ic_unknown_device);
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

        switch (deviceList.get(position).getStatusConnection()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_action_online);
                break;
            case DEVICE_DISCONNECTED:
                progressBar.setVisibility(View.INVISIBLE);
                imageConnection.setImageResource(R.drawable.ic_action_offline);
                imageDevice.setImageResource(R.drawable.ic_unknown_device);
                Log.d(TAG, "Disconnect update in adapter");
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                progressBar.setVisibility(View.VISIBLE);
                imageConnection.setImageResource(R.drawable.ic_action_offline);
                imageDevice.setImageResource(R.drawable.ic_unknown_device);
                break;


        }

    }


    private void paintStatusConnectionSwitchDevice(IotDeviceSwitchAdapterViewHolder holder, int position) {

        switch (deviceList.get(position).getStatusConnection()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                holder.progressBarSwitchDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_online);
                break;
            case DEVICE_DISCONNECTED:
                holder.progressBarSwitchDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_offline);
                holder.imageSwitch.setImageResource(R.drawable.ic_unknown_device);
                Log.d(TAG, "Disconnect update in adapter");
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                holder.progressBarSwitchDevice.setVisibility(View.VISIBLE);
                holder.imageConnectedDeviceSwitch.setImageResource(R.drawable.ic_action_offline);
                holder.imageSwitch.setImageResource(R.drawable.ic_unknown_device);
                break;


        }

    }

    private void paintStatusConnectionThermometerDevice(IotDeviceThermometerAdapterViewHolder holder, int position) {

        IotDeviceThermometer device;
        device = (IotDeviceThermometer) deviceList.get(position);
        switch (device.getStatusConnection()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                holder.progressBarThermometerDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceThermometer.setImageResource(R.drawable.ic_action_online);
                holder.textDeviceThermometer.setText(deviceList.get(position).getDeviceName());
                holder.imageThermometer.setVisibility(View.INVISIBLE);
                String dato;
                MyHomeIotTools tools;
                Double temp;
                tools = new MyHomeIotTools();
                temp = tools.roundData(device.getTemperature(), 1);

                holder.textTemperatureThermometer.setText(String.valueOf(temp) + " ºC");
                break;
            case DEVICE_DISCONNECTED:
                holder.progressBarThermometerDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceThermometer.setImageResource(R.drawable.ic_action_offline);
                holder.imageThermometer.setImageResource(R.drawable.ic_unknown_device);
                holder.textTemperatureThermometer.setText("--.-");
                holder.imageThermometer.setVisibility(View.VISIBLE);
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                holder.progressBarThermometerDevice.setVisibility(View.VISIBLE);
                holder.imageConnectedDeviceThermometer.setImageResource(R.drawable.ic_action_offline);
                holder.imageThermometer.setImageResource(R.drawable.ic_unknown_device);
                holder.textTemperatureThermometer.setText("--.-");
                holder.imageThermometer.setVisibility(View.VISIBLE);
                break;


        }

    }


    private void paintStatusConnectionThermostatDevice(IotDeviceThermostatAdapterViewHolder holder, int position) {

        IotDeviceThermostat device;
        device = (IotDeviceThermostat) deviceList.get(position);
        MyHomeIotTools tool;
        tool = new MyHomeIotTools();

        switch (device.getStatusConnection()) {

            case UNKNOWN:
                break;
            case DEVICE_CONNECTED:
                holder.progressBarThermostatDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_online);
                holder.imageHeating.setImageResource(R.drawable.heating);
                holder.textDeviceThermostat.setText(device.getDeviceName());
                double data;
                data = tool.roundData(device.getTemperature(), 1);
                holder.textTemperatureThermostat.setText(String.valueOf(data) + " ºC");
                data = tool.roundData(device.getThresholdTemperature(), 1);
                holder.textThresholdThermostat.setText(String.valueOf(data) + " ºC");
                break;
            case DEVICE_DISCONNECTED:
                holder.progressBarThermostatDevice.setVisibility(View.INVISIBLE);
                holder.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_offline);
                holder.imageHeating.setImageResource(R.drawable.ic_unknown_device);
                holder.textTemperatureThermostat.setText("--.-");
                holder.textThresholdThermostat.setText("--.-");
                break;
            case DEVICE_WAITING_RESPONSE:
            case DEVICE_ERROR_SUBSCRIPTION:
            case DEVICE_ERROR_COMMUNICATION:
                holder.progressBarThermostatDevice.setVisibility(View.VISIBLE);
                holder.imageConnectedDeviceThermostat.setImageResource(R.drawable.ic_action_offline);
                holder.imageHeating.setImageResource(R.drawable.ic_unknown_device);
                holder.textTemperatureThermostat.setText("--.-");
                holder.textThresholdThermostat.setText("--.-");
                break;
        }

        switch (device.getRelay()) {


            case OFF:
                holder.imageHeating.setVisibility(View.INVISIBLE);
                break;
            case ON:
                holder.imageHeating.setVisibility(View.VISIBLE);
                break;
            case UNKNOWN:
                break;
        }


    }

    private void paintStatusThermostatDevice(IotDeviceThermostatAdapterViewHolder holder, int position) {

     if (deviceList.get(position).getDeviceStatus() == IOT_DEVICE_STATUS.CUTTING_DEVICE) {
         showCuttingModeThermostat(holder, position, true);
     }

    }

    private void paintStatusThermometerDevice(IotDeviceThermometerAdapterViewHolder holder, int position) {

     if (deviceList.get(position).getDeviceStatus() == IOT_DEVICE_STATUS.CUTTING_DEVICE) {
         showCuttingModeThermometer(holder, position, true);
     }

    }

    private void paintStatusSwitchDevice(IotDeviceSwitchAdapterViewHolder holder, int position) {

        if (deviceList.get(position).getDeviceStatus() == IOT_DEVICE_STATUS.CUTTING_DEVICE) {
           showCuttingModeSwitch(holder, position, true);
        }
    }


    private void paintStatusDeviceUnknown(IotUnknownDeviceAdapterViewHolder holder, int position) {

     if (deviceList.get(position).getDeviceStatus() == IOT_DEVICE_STATUS.CUTTING_DEVICE) {
         showCuttingModeUnknown(holder, position, true);
     }
    }



}
