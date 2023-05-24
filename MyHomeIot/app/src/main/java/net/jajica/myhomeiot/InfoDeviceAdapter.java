package net.jajica.myhomeiot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.adapters.ImageViewBindingAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotInfoDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InfoDeviceAdapter extends RecyclerView.Adapter<InfoDeviceAdapter.InfoDeviceAdapterViewHolder>  {

    private final String TAG = "InfoDeviceAdapter";
    private ArrayList<IotInfoDevice> infoDevice;
    private Context context;

    private OnSelectedParameterListener onSelectedParameterListener;



    public interface OnSelectedParameterListener {


        void onSelectedParameter(IOT_LABELS_JSON parameter, String value, String value2);


    }


    public void setOnSelectedParameterListener(OnSelectedParameterListener onSelectedParameterListener) {
        this.onSelectedParameterListener = onSelectedParameterListener;
    }

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
    public void onBindViewHolder(@NonNull InfoDeviceAdapter.InfoDeviceAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {


        paintItem(holder, position);

        if ((Boolean) holder.imageItemExtendInfoDevice.getTag()) {
            holder.imageItemExtendInfoDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "kk");
                    launchModifyparameter(holder, position);
                }
            });
        }

    }

    private void launchModifyparameter(InfoDeviceAdapterViewHolder holder, int position) {

        IOT_LABELS_JSON label = IOT_LABELS_JSON.COMMAND;
        String value;
        int i;
        String value2 = null;



        if (onSelectedParameterListener != null) {

            value = holder.textValueInfoDevice.getText().toString();
            label = label.fromlabel(holder.textLabelInfoDevice.getText().toString());
            if (label != null) {
                switch (label) {
                    case DEVICE_NAME:
                    case READ_INTERVAL:
                    case RETRY_INTERVAL:
                    case READ_NUMBER_RETRY:
                    case DEFAULT_THRESHOLD_TEMPERATURE:
                    case MARGIN_TEMPERATURE:
                    case CALIBRATE_VALUE:
                    case TYPE_SENSOR:
                        if (value.equals("true")) {
                            onSelectedParameterListener.onSelectedParameter(label, value, null);
                        } else {

                            for (i=0;i<infoDevice.size();i++) {
                                if (infoDevice.get(i).getItemLabelInfoDevice().equals(IOT_LABELS_JSON.SENSOR_ID.getValorTextoJson())) {
                                    value2 = infoDevice.get(i).getItemValueInfoDevice();
                                    break;
                                }

                            }
                            onSelectedParameterListener.onSelectedParameter(label, value, value2);
                        }


                        break;
                    case SENSOR_ID:
                        break;
                    default:
                        break;
                }

            }



        }







    }


    private void paintItem(InfoDeviceAdapterViewHolder holder, int position) {

        if (infoDevice.get(position).getItemConfigurableInfoDevice()) {
            holder.imageItemExtendInfoDevice.setImageResource(R.drawable.ic_action_edit);
            holder.imageItemExtendInfoDevice.setTag(true);
        } else {
            holder.imageItemExtendInfoDevice.setImageResource(R.drawable.ic_action_colon);
            holder.imageItemExtendInfoDevice.setTag(false);
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

        private final String TAG = "InfoDeviceAdapterViewHolder";
        public InfoDeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textLabelInfoDevice = (AppCompatTextView) itemView.findViewById(R.id.textLabelInfoDevice);
            textValueInfoDevice = (AppCompatTextView) itemView.findViewById(R.id.textValueInfoDevice);
            imageItemExtendInfoDevice = (AppCompatImageView) itemView.findViewById(R.id.imageItemExtendInfoDevice);

        }
    }



}
