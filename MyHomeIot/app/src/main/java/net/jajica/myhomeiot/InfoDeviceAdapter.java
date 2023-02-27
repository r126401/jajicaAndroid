package net.jajica.myhomeiot;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IotInfoDevice;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InfoDeviceAdapter extends RecyclerView.Adapter<InfoDeviceAdapter.InfoDeviceAdapterViewHolder> {

    private ArrayList<IotInfoDevice> infoDevice;
    private Context context;


    public InfoDeviceAdapter(ArrayList<IotInfoDevice> infoDevice, Context context) {

        this.infoDevice = infoDevice;
        this.context = context;
    }

    @NonNull
    @Override
    public InfoDeviceAdapter.InfoDeviceAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull InfoDeviceAdapter.InfoDeviceAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class InfoDeviceAdapterViewHolder extends RecyclerView.ViewHolder {
        public InfoDeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
