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

import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.ListSwitchScheduleBinding;
import net.jajica.myhomeiot.databinding.ListScheduleEmptyBinding;

import java.util.ArrayList;

public class SwitchScheduleAdapter extends RecyclerView.Adapter<SwitchScheduleAdapter.SwitchScheduleAdapterViewHolder> {

    private static final String TAG = "SwitchScheduleAdapter";
    private ArrayList<IotScheduleDeviceSwitch> listSchedule;
    private Context context;
    private SwitchScheduleAdapterViewHolder holder;

    private ArrayList<AppCompatTextView> listWeek;

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
        if (listSchedule == null) {
            emptyBinding = ListScheduleEmptyBinding.inflate(layoutInflater, parent, false);
            return new SwitchScheduleAdapterViewHolder(emptyBinding);
        } else {
            binding = ListSwitchScheduleBinding.inflate(layoutInflater, parent, false);
            initArrayWeek(binding);

            return new SwitchScheduleAdapterViewHolder(binding);
        }
    }

    private void initArrayWeek(ListSwitchScheduleBinding binding) {

        if (listWeek == null) {
            listWeek = new ArrayList<>();
        }
        listWeek.add(binding.layoutWeekSchedule.textSunday);
        listWeek.add(binding.layoutWeekSchedule.textMonday);
        listWeek.add(binding.layoutWeekSchedule.textTuesday);
        listWeek.add(binding.layoutWeekSchedule.textWednesday);
        listWeek.add(binding.layoutWeekSchedule.textThursday);
        listWeek.add(binding.layoutWeekSchedule.textFriday);
        listWeek.add(binding.layoutWeekSchedule.textSaturday);
    }


    @Override
    public void onBindViewHolder(@NonNull SwitchScheduleAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (listSchedule != null) {
            paintItemSchedule(holder, position);

            holder.mbinding.imageScheduleStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickSelected != null) {
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


    @Override
    public int getItemCount() {


        if (listSchedule == null) {
            return 1;
        } else {
            return listSchedule.size();
        }



    }

    public class SwitchScheduleAdapterViewHolder extends RecyclerView.ViewHolder {

        private ListSwitchScheduleBinding mbinding;
        private ListScheduleEmptyBinding mEmptyBinding;

        public SwitchScheduleAdapterViewHolder(ListScheduleEmptyBinding mEmptyBinding) {
            super(mEmptyBinding.getRoot());
            this.mEmptyBinding = mEmptyBinding;
        }

        public SwitchScheduleAdapterViewHolder(@NonNull ListSwitchScheduleBinding mbinding) {
            super(mbinding.getRoot());

            this.mbinding = mbinding;
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
        paintActiveSchedule(holder, position);
        paintWeekSchedule(holder, position);
        holder.mbinding.progressComandSchedule.setVisibility(View.INVISIBLE);

    }



    private void paintActiveSchedule(SwitchScheduleAdapterViewHolder holder, int position) {

        if (listSchedule.get(position).getActiveSchedule()) {
            holder.mbinding.imageCurrentSchedule.setVisibility(View.VISIBLE);
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
                listWeek = tool.mask2controls(listWeek, schedule.getActiveDays());
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

    }









}
