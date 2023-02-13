package net.jajica.myhomeiot;

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
            return new SwitchScheduleAdapterViewHolder(binding);
        }


    }



    @Override
    public void onBindViewHolder(@NonNull SwitchScheduleAdapterViewHolder holder, int position) {

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
        IotScheduleDeviceSwitch schedule;
        schedule = listSchedule.get(position);
        mask = schedule.getMask();
        switch (schedule.getScheduleType()) {

            case UNKNOWN_SCHEDULE:
                break;
            case DIARY_SCHEDULE:
                if (schedule.getActiveDay(0)) {
                    updateWeekDay(holder.mbinding.layoutWeekSchedule.textSunday, true);
                }else {
                    updateWeekDay(holder.mbinding.layoutWeekSchedule.textSunday, false);
                }
                if (schedule.getActiveDay(1)) updateWeekDay(holder.mbinding.layoutWeekSchedule.textMonday, true);
                else updateWeekDay(holder.mbinding.layoutWeekSchedule.textMonday, false);
                if (schedule.getActiveDay(2)) updateWeekDay(holder.mbinding.layoutWeekSchedule.textTuesday, true);
                else updateWeekDay(holder.mbinding.layoutWeekSchedule.textTuesday, false);
                if (schedule.getActiveDay(3)) updateWeekDay(holder.mbinding.layoutWeekSchedule.textWednesday, true);
                else updateWeekDay(holder.mbinding.layoutWeekSchedule.textWednesday, false);
                if (schedule.getActiveDay(4)) updateWeekDay(holder.mbinding.layoutWeekSchedule.textThursday, true);
                else updateWeekDay(holder.mbinding.layoutWeekSchedule.textThursday, false);
                if (schedule.getActiveDay(5)) updateWeekDay(holder.mbinding.layoutWeekSchedule.textFriday, true);
                else updateWeekDay(holder.mbinding.layoutWeekSchedule.textFriday, false);
                if (schedule.getActiveDay(6)) updateWeekDay(holder.mbinding.layoutWeekSchedule.textSaturday, true);
                else updateWeekDay(holder.mbinding.layoutWeekSchedule.textSaturday, false);
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

    private void updateWeekDay(AppCompatTextView day, boolean activado) {

        if (activado == false) {
            day.setBackgroundResource(R.drawable.round_corners_deactive_days);
            day.setTag(false);
        } else {
            day.setBackgroundResource(R.drawable.round_corners_active_days);
            day.setTag(true);
        }



    }


}
