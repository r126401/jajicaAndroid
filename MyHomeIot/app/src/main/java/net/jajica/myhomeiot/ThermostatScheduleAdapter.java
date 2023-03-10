package net.jajica.myhomeiot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import net.jajica.libiot.IotScheduleDeviceThermostat;

import java.util.ArrayList;


import net.jajica.myhomeiot.databinding.ListScheduleEmptyBinding;
import net.jajica.myhomeiot.databinding.ListThermostatScheduleBinding;

public class ThermostatScheduleAdapter extends RecyclerView.Adapter<ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder> {


    private final String TAG = "ThermostatScheduleAdapter";
    private ArrayList<IotScheduleDeviceThermostat> listSchedule;
    private Context context;
    private ThermostatScheduleAdapterViewHolder holder;


    public void setListSchedule(ArrayList<IotScheduleDeviceThermostat> listSchedule) {
        this.listSchedule = listSchedule;
    }


    public ThermostatScheduleAdapter(ArrayList<IotScheduleDeviceThermostat> listSchedule, Context context) {
        this.listSchedule = listSchedule;
        this.context = context;
    }

    private ThermostatScheduleAdapter.OnItemClickSelected onItemClickSelected;

    enum ITEM_TYPE {
        DELETE_SCHEDULE,
        CHANGE_STATUS_SCHEDULE,
        MODIFY_SCHEDULE;

    }

    public interface OnItemClickSelected {
        void onItemClickSelected(ThermostatScheduleAdapter.ITEM_TYPE event, int position);
    }

    public void setOnItemClickSelected(ThermostatScheduleAdapter.OnItemClickSelected onItemClickSelected) {
        this.onItemClickSelected = onItemClickSelected;
    }



    @NonNull
    @Override
    public ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListThermostatScheduleBinding binding;
        ListScheduleEmptyBinding emptyBinding;

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (listSchedule != null) {
            if (listSchedule.size() == 0) {
                emptyBinding = ListScheduleEmptyBinding.inflate(layoutInflater, parent, false);
                return new ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder(emptyBinding);
            } else {
                binding = ListThermostatScheduleBinding.inflate(layoutInflater, parent, false);
                return new ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder(binding);

            }
        } else {
            emptyBinding = ListScheduleEmptyBinding.inflate(layoutInflater, parent, false);
            return new ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder(emptyBinding);
        }






    }

    private void initArrayWeek(ListThermostatScheduleBinding binding, ArrayList<AppCompatTextView> list) {

        list.add(binding.layoutWeekSchedule.textSunday);
        list.add(binding.layoutWeekSchedule.textMonday);
        list.add(binding.layoutWeekSchedule.textTuesday);
        list.add(binding.layoutWeekSchedule.textWednesday);
        list.add(binding.layoutWeekSchedule.textThursday);
        list.add(binding.layoutWeekSchedule.textFriday);
        list.add(binding.layoutWeekSchedule.textSaturday);

    }


    @Override
    public void onBindViewHolder(@NonNull ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (listSchedule != null) {
            if (listSchedule.size() > 0) {
                paintItemSchedule(holder, position);

                holder.mbinding.imageScheduleStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (onItemClickSelected != null) {
                            holder.mbinding.progressComandSchedule.setVisibility(View.VISIBLE);
                            onItemClickSelected.onItemClickSelected(ThermostatScheduleAdapter.ITEM_TYPE.CHANGE_STATUS_SCHEDULE, position);
                        }

                    }
                });

                holder.mbinding.imageDeleteSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        holder.mbinding.progressComandSchedule.setVisibility(View.VISIBLE);
                        onItemClickSelected.onItemClickSelected(ThermostatScheduleAdapter.ITEM_TYPE.DELETE_SCHEDULE, position);

                    }
                });

                holder.mbinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickSelected.onItemClickSelected(ThermostatScheduleAdapter.ITEM_TYPE.MODIFY_SCHEDULE, position);

                    }
                });
            }

        }


    }


    @Override
    public int getItemCount() {

        if (listSchedule != null) {
            if (listSchedule.size() == 0) {
                return 1;
            } else {
                return listSchedule.size();
            }
        } else {
            return 1;
        }

    }



    public class ThermostatScheduleAdapterViewHolder extends RecyclerView.ViewHolder {

        private ListThermostatScheduleBinding mbinding;
        private ListScheduleEmptyBinding mEmptyBinding;
        private ArrayList<AppCompatTextView> listWeek;

        public ThermostatScheduleAdapterViewHolder(ListScheduleEmptyBinding mEmptyBinding) {
            super(mEmptyBinding.getRoot());
            this.mEmptyBinding = mEmptyBinding;
        }

        public ThermostatScheduleAdapterViewHolder(@NonNull ListThermostatScheduleBinding mbinding) {
            super(mbinding.getRoot());
            this.mbinding = mbinding;
            listWeek = new ArrayList<>();
            initArrayWeek(mbinding, listWeek);

        }

    }


    private void paintItemSchedule(ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder holder, int position) {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        String data;
        data = tool.formatHour(listSchedule.get(position).getHour(), listSchedule.get(position).getMinute());
        holder.mbinding.textFromHour.setText(data);
        data = tool.convertDuration(listSchedule.get(position).getHour(), listSchedule.get(position).getMinute(), listSchedule.get(position).getDuration());
        holder.mbinding.textToHour.setText(data);
        paintStatusSchedule(holder, position);
        paintWeekSchedule(holder, position);
        paintActiveSchedule(holder, position);
        paintThresholdSchedule(holder, position);
        holder.mbinding.progressComandSchedule.setVisibility(View.INVISIBLE);

    }

    private void paintThresholdSchedule(ThermostatScheduleAdapterViewHolder holder, int position) {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        String dat;
        double threshold;
        threshold = listSchedule.get(position).getThresholdTemperature();
        dat = String.valueOf(threshold);
        //dat = tool.formatTemperature(dat);

        holder.mbinding.textThresholdTemperature.setText(dat);

    }


    private void paintActiveSchedule(ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder holder, int position) {

        if (listSchedule.get(position).getActiveSchedule()) {
            holder.mbinding.imageCurrentSchedule.setVisibility(View.VISIBLE);
        } else {
            holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
        }
    }

    private void paintWeekSchedule(ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder holder, int position) {

        int mask;
        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        IotScheduleDeviceThermostat schedule;
        schedule = listSchedule.get(position);
        mask = schedule.getMask();
        switch (schedule.getScheduleType()) {

            case UNKNOWN_SCHEDULE:
                break;
            case DIARY_SCHEDULE:
                tool.mask2controls(holder.listWeek, schedule.getActiveDays());
                break;
            case WEEKLY_SCHEDULE:
                break;
            case DATE_SCHEDULE:
                break;
        }
    }



    private void paintStatusSchedule(ThermostatScheduleAdapter.ThermostatScheduleAdapterViewHolder holder, int position) {

        switch (listSchedule.get(position).getScheduleState()) {

            case UNKNOWN_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_lock);
                break;
            case INACTIVE_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_lock);
                break;
            case ACTIVE_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_unlock);

                break;
            case INVALID_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_lock);
                break;
            case VALID_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_unlock);
                break;
        }

    }




}
