package net.jajica.myhomeiot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IotDevice;

import java.util.ArrayList;


public class IotDeviceAdapter extends RecyclerView.Adapter<IotDeviceAdapter.ViewHolderIotDevice> {

    ArrayList<IotDevice> deviceList;
    private LayoutInflater layoutInflater;
    private Context context;


    /**
     * Constructor adaptado para la lista de elementos que le vamos a pasar
     * @param deviceList Es la lista de elementos
     * @param layoutInflater Es el inflater
     * @param context Es el contexto de la aplicacion
     */
    public IotDeviceAdapter(ArrayList deviceList, LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater.from(context);
        this.context = context;
        this.deviceList = deviceList;
    }

    public IotDeviceAdapter(ArrayList<IotDevice> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public IotDeviceAdapter.ViewHolderIotDevice onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = layoutInflater.inflate(R.layout.iot_devices_list, null);
        return new IotDeviceAdapter.ViewHolderIotDevice(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IotDeviceAdapter.ViewHolderIotDevice holder, int position) {

        holder.bindData(deviceList.get(position));

    }

    @Override
    public int getItemCount() {

        if (deviceList != null) {
            return deviceList.size();
        } else {
            return 0;
        }

    }

    public class ViewHolderIotDevice extends RecyclerView.ViewHolder {
        public ViewHolderIotDevice(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(IotDevice iotDevice) {
        }
    }
}
