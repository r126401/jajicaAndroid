package net.jajica.myhomeiot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.SwitchScheduleListBinding;

import java.util.ArrayList;

public class SwitchScheduleAdapter extends RecyclerView.Adapter<SwitchScheduleAdapter.SwitchScheduleAdapterViewHolder> {

    ArrayList<IotScheduleDeviceSwitch> listSchedule;
    Context context;

    public SwitchScheduleAdapter(ArrayList<IotScheduleDeviceSwitch> listSchedule, Context context) {
        this.listSchedule = listSchedule;
        this.context = context;
    }

    @NonNull
    @Override
    public SwitchScheduleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SwitchScheduleListBinding binding;
        binding = SwitchScheduleListBinding.inflate(layoutInflater, parent, false);
        return new SwitchScheduleAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SwitchScheduleAdapterViewHolder holder, int position) {

        paintItemSchedule(holder, position);

    }


    @Override
    public int getItemCount() {
        return listSchedule.size();
    }

    public class SwitchScheduleAdapterViewHolder extends RecyclerView.ViewHolder {

        private SwitchScheduleListBinding mbinding;

        public SwitchScheduleAdapterViewHolder(@NonNull SwitchScheduleListBinding mbinding) {
            super(mbinding.getRoot());
            this.mbinding = mbinding;
        }

    }

    private void paintItemSchedule(SwitchScheduleAdapterViewHolder holder, int position) {

        holder.mbinding.textFromHour.setText(String.valueOf(listSchedule.get(position).getHour()));
        holder.mbinding.textToHour.setText(String.valueOf(listSchedule.get(position).getMinute()));
    }

}
