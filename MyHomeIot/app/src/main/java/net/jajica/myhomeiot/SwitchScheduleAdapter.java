package net.jajica.myhomeiot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.SwitchScheduleListBinding;
import net.jajica.myhomeiot.databinding.ScheduleWeekListBinding;

import java.util.ArrayList;

public class SwitchScheduleAdapter extends RecyclerView.Adapter<SwitchScheduleAdapter.SwitchScheduleAdapterViewHolder> {

    private static final String TAG = "SwitchScheduleAdapter";
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
        private ScheduleWeekListBinding mbindingWeek;



        public SwitchScheduleAdapterViewHolder(@NonNull SwitchScheduleListBinding mbinding) {
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
        data = tool.formatHour(listSchedule.get(position).getDuration())
        paintStatusSchedule(holder, position);
        paintWeekSchedule(holder, position);
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
                holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
                break;
            case INACTIVE_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_lock);
                holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
                break;
            case ACTIVE_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_unlock);
                holder.mbinding.imageCurrentSchedule.setVisibility(View.VISIBLE);

                break;
            case INVALID_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_lock);
                holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
                break;
            case VALID_SCHEDULE:
                holder.mbinding.imageScheduleStatus.setImageResource(R.drawable.ic_action_unlock);
                holder.mbinding.imageCurrentSchedule.setVisibility(View.INVISIBLE);
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
