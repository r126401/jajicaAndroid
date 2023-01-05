package net.jajica.myhomeiot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;

import java.util.ArrayList;

import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.myhomeiot.databinding.ActivityMainBinding;
import net.jajica.myhomeiot.databinding.SwitchDeviceBinding;
import net.jajica.myhomeiot.databinding.ThermometerDeviceBinding;
import net.jajica.myhomeiot.databinding.ThermostatDeviceBinding;

public class DevicesAdapter extends BaseAdapter {


    private Context context;
    private int idLayout;
    private ArrayList<IotDevice> deviceList;
    private SwitchDeviceBinding switchDeviceBinding;
    private ThermometerDeviceBinding thermometerDeviceBinding;
    private ThermostatDeviceBinding thermostatDeviceBinding;



    public DevicesAdapter(Context context, int idLayout, ArrayList<IotDevice> devices) {
        this.context = context;
        this.idLayout = idLayout;
        this.deviceList = devices;

    }




    @Override
    public int getCount() {
        if (deviceList == null) {
            return 0;
        }
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = paintElement(position, convertView, parent);



        return view;
    }

    private View paintElement(int position, View convertView, ViewGroup parent) {

        IotDevice device;
        IOT_DEVICE_TYPE type;
        device = deviceList.get(position);
        type = device.getDeviceType();
        switch (type) {


            case UNKNOWN:
                break;
            case INTERRUPTOR:
                return paintListSwitchDevice(position, convertView, parent);

            case THERMOMETER:
                return paintListThermometerDevice(position, convertView, parent);

            case CRONOTERMOSTATO:
                return paintThermostatDevice(position, convertView, parent);

        }
        return null;



    }

    private View paintListSwitchDevice(int position, View convertView, ViewGroup parent) {

        IotDeviceSwitch device;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switchDeviceBinding = SwitchDeviceBinding.inflate(inflater, parent, false);
            convertView = switchDeviceBinding.getRoot();
        }
        device = (IotDeviceSwitch) deviceList.get(position);
        switchDeviceBinding.textdevice.setText(device.getDeviceName());
        switchDeviceBinding.imageThermometer.setImageResource(R.drawable.ic_switch_off);




        return convertView;
    }

    private View paintListThermometerDevice(int position, View convertView, ViewGroup parent) {
        IotDeviceThermometer device;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            thermometerDeviceBinding = thermometerDeviceBinding.inflate(inflater, parent, false);
            convertView = thermometerDeviceBinding.getRoot();
        }
        device = (IotDeviceThermometer) deviceList.get(position);
        thermometerDeviceBinding.textdevice.setText(device.getDeviceName());
        return convertView;
    }

    private View paintThermostatDevice(int position, View convertView, ViewGroup parent) {
        IotDeviceThermostat device;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            thermostatDeviceBinding = thermostatDeviceBinding.inflate(inflater, parent, false);
            convertView = thermostatDeviceBinding.getRoot();
        }
        device = (IotDeviceThermostat) deviceList.get(position);
        thermostatDeviceBinding.textDeviceThermostat.setText(device.getDeviceName());
        return convertView;
    }

    private View paintUnknownDevice(int position, View convertView, ViewGroup parent) {

        return convertView;
    }



}
