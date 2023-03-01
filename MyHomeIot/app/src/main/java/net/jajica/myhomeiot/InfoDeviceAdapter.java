package net.jajica.myhomeiot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.adapters.ImageViewBindingAdapter;
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

        View view;
        view = LayoutInflater.from(context).inflate(R.layout.list_info_device, parent, false);
        return new InfoDeviceAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoDeviceAdapter.InfoDeviceAdapterViewHolder holder, int position) {

        paintItem(holder, position);

    }

    private void paintItem(InfoDeviceAdapterViewHolder holder, int position) {

        if (infoDevice.get(position).getItemConfigurableInfoDevice()) {
            holder.imageItemExtendInfoDevice.setVisibility(View.VISIBLE);
        } else {
            holder.imageItemExtendInfoDevice.setVisibility(View.INVISIBLE);
        }

        holder.textLabelInfoDevice.setText(infoDevice.get(position).getItemLabelInfoDevice());
        holder.textValueInfoDevice.setText(infoDevice.get(position).getItemValueInfoDevice());



    }

    @Override
    public int getItemCount() {

        if (infoDevice == null) {
            return 0;
        } else {
            return infoDevice.size();
        }

    }

    public static class InfoDeviceAdapterViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textLabelInfoDevice;
        AppCompatTextView textValueInfoDevice;
        AppCompatImageView imageItemExtendInfoDevice;
        public InfoDeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textLabelInfoDevice = (AppCompatTextView) itemView.findViewById(R.id.textLabelInfoDevice);
            textValueInfoDevice = (AppCompatTextView) itemView.findViewById(R.id.textValueInfoDevice);
            imageItemExtendInfoDevice = (AppCompatImageView) itemView.findViewById(R.id.imageItemExtendInfoDevice);
        }
    }
}
