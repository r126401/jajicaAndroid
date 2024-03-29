package net.jajica.myhomeiot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.ListSwitchScheduleBinding;
import net.jajica.myhomeiot.databinding.ListScheduleEmptyBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SwitchScheduleAdapter extends RecyclerView.Adapter<SwitchScheduleAdapter.SwitchScheduleAdapterViewHolder> {

    private static final String TAG = "SwitchScheduleAdapter";
    private ArrayList<IotScheduleDeviceSwitch> listSchedule;
    private Context context;

    public void setListSchedule(ArrayList<IotScheduleDeviceSwitch> listSchedule) {

        this.listSchedule = listSchedule;
    }

    private OnItemClickSelected onItemClickSelected;

    enum ITEM_TYPE {
        DELETE_SCHEDULE,
        CHANGE_STATUS_SCHEDULE,
        MODIFY_SCHEDULE;

    }

    public interface OnItemClickSelected {
        void onItemClickSelected(ITEM_TYPE event, int position);
    }

    public void setOnItemClickSelected(OnItemClickSelected onItemClickSelected) {
        this.onItemClickSelected = onItemClickSelected;
    }

    public SwitchScheduleAdapter(ArrayList<IotScheduleDeviceSwitch> listSchedule, Context context) {
        this.listSchedule = listSchedule;
        this.context = context;


    }


    @NonNull
    @Override
    public SwitchScheduleAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListSwitchScheduleBinding binding;
        ListScheduleEmptyBinding emptyBinding;
        if (listSchedule != null) {
            if (listSchedule.size() == 0) {
                emptyBinding = ListScheduleEmptyBinding.inflate(layoutInflater, parent, false);
                return new SwitchScheduleAdapterViewHolder(emptyBinding);
            } else {
                binding = ListSwitchScheduleBinding.inflate(layoutInflater, parent, false);
                return new SwitchScheduleAdapterViewHolder(binding);

            }
        } else {
            emptyBinding = ListScheduleEmptyBinding.inflate(layoutInflater, parent, false);
            return new SwitchScheduleAdapterViewHolder(emptyBinding);
        }


    }

    private void initArrayWeek(ListSwitchScheduleBinding binding, ArrayList<AppCompatTextView> list) {

        list.add(binding.layoutWeekSchedule.textSunday);
        list.add(binding.layoutWeekSchedule.textMonday);
        list.add(binding.layoutWeekSchedule.textTuesday);
        list.add(binding.layoutWeekSchedule.textWednesday);
        list.add(binding.layoutWeekSchedule.textThursday);
        list.add(binding.layoutWeekSchedule.textFriday);
        list.add(binding.layoutWeekSchedule.textSaturday);

    }


    @Override
    public void onBindViewHolder(@NonNull SwitchScheduleAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (listSchedule != null) {
            if (listSchedule.size() > 0) {
                paintItemSchedule(holder, position);

                holder.mbinding.imageScheduleStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (onItemClickSelected != null) {
                            holder.mbinding.progressComandSchedule.setVisibility(View.VISIBLE);
                            onItemClickSelected.onItemClickSelected(ITEM_TYPE.CHANGE_STATUS_SCHEDULE, position);
                        }

                    }
                });

                holder.mbinding.imageDeleteSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        holder.mbinding.progressComandSchedule.setVisibility(View.VISIBLE);
                        onItemClickSelected.onItemClickSelected(ITEM_TYPE.DELETE_SCHEDULE, position);

                    }
                });

                holder.mbinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickSelected.onItemClickSelected(ITEM_TYPE.MODIFY_SCHEDULE, position);

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

    public class SwitchScheduleAdapterViewHolder extends RecyclerView.ViewHolder {

        private ListSwitchScheduleBinding mbinding;
        private ListScheduleEmptyBinding mEmptyBinding;

        private ArrayList<AppCompatTextView> listWeek;

        public SwitchScheduleAdapterViewHolder(ListScheduleEmptyBinding mEmptyBinding) {
            super(mEmptyBinding.getRoot());
            this.mEmptyBinding = mEmptyBinding;
        }

        public SwitchScheduleAdapterViewHolder(@NonNull ListSwitchScheduleBinding mbinding) {
            super(mbinding.getRoot());
            this.mbinding = mbinding;
            listWeek = new ArrayList<>();
            initArrayWeek(mbinding, listWeek);

        }

    }

    private void paintItemSchedule(SwitchScheduleAdapterViewHolder holder, int position) {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        String data;
        data = tool.formatHour(listSchedule.get(position).getHour(), listSchedule.get(position).getMinute());
        holder.mbinding.textFromHour.setText(data);
        data = tool.convertDuration(listSchedule.get(position).getHour(), listSchedule.get(position).getMinute(), listSchedule.get(position).getDuration());
        holder.mbinding.textToHour.setText(data);
        paintStatusSchedule(holder, position);
        paintWeekSchedule(holder, position);

        //paintWeekSchedule(holder, position);
        holder.mbinding.progressComandSchedule.setVisibility(View.INVISIBLE);

    }



    private void paintActiveSchedule(SwitchScheduleAdapterViewHolder holder, int position) {

        if (listSchedule.get(position).getScheduleState() == IOT_STATE_SCHEDULE.ACTIVE_SCHEDULE) {
            if (listSchedule.get(position).getActiveSchedule()) {
                holder.mbinding.imageCurrentSchedule.setVisibility(View.VISIBLE);
            } else {
                holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
        }
    }

    private void paintWeekSchedule(SwitchScheduleAdapterViewHolder holder, int position) {

        int mask;
        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        IotScheduleDeviceSwitch schedule;
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


    private void paintStatusSchedule(SwitchScheduleAdapterViewHolder holder, int position) {

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
        paintActiveSchedule(holder, position);

    }









}
