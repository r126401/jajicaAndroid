package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_CLASS_SCHEDULE;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotScheduleDeviceSwitch;

import net.jajica.myhomeiot.databinding.FragmentActionSwitchScheduleBinding;

import java.util.ArrayList;

public class ActionSwitchScheduleFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "ActionSwitchScheduleFragment";
    private FragmentActionSwitchScheduleBinding binding;
    private IotScheduleDeviceSwitch schedule;

    private OPERATION_SCHEDULE operationSchedule;

    private OnActionSchedule onActionSchedule;

    private ArrayList<AppCompatTextView> listControlWeek;

    public interface OnActionSchedule {

        void onActionSchedule(IotScheduleDeviceSwitch schedule, OPERATION_SCHEDULE operationSchedule);
    }

    public void setOnActionSchedule(OnActionSchedule onActionSchedule) {
        this.onActionSchedule = onActionSchedule;
    }

    enum OPERATION_SCHEDULE {
        NEW_SCHEDULE,
        DELETE_SCHEDULE,
        MODIFY_SCHEDULE;
    }

    public ActionSwitchScheduleFragment() {
        // Required empty public constructor
    }

    public ActionSwitchScheduleFragment(IotScheduleDeviceSwitch schedule) {

        if (schedule == null) {
            operationSchedule = OPERATION_SCHEDULE.NEW_SCHEDULE;
        } else {
            operationSchedule = OPERATION_SCHEDULE.MODIFY_SCHEDULE;
        }

        this.schedule = schedule;

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void initDataFragment() {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        String data;
        String hour;
        String minute;

        if (operationSchedule == OPERATION_SCHEDULE.MODIFY_SCHEDULE) {

            binding.timePickerFrom.setHour(schedule.getHour());
            binding.timePickerFrom.setMinute(schedule.getMinute());
            data = tool.convertDuration(schedule.getHour(), schedule.getMinute(), schedule.getDuration());
            hour = tool.extractHourForConvertDuration(data);
            minute = tool.extractMinuteForConvertDuration(data);
            binding.timePickerTo.setHour(Integer.parseInt(hour));
            binding.timePickerTo.setMinute(Integer.parseInt(minute));
            listControlWeek = new ArrayList<>();






        }
        binding.buttonAcceptSchedule.setOnClickListener(this);
        binding.buttonCancelSchedule.setOnClickListener(this);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;
        binding = FragmentActionSwitchScheduleBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        binding.timePickerFrom.setIs24HourView(true);
        binding.timePickerTo.setIs24HourView(true);
        if (schedule == null) {
            binding.textScheduleType.setText(getResources().getString(R.string.new_schedule));
        } else {
            binding.textScheduleType.setText(getResources().getString(R.string.modify_schedule));
        }
        initDataFragment();


        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.buttonAcceptSchedule):
                processActionSchedule();
                

                break;

            case (R.id.buttonCancelSchedule):
                processCancelSchedule();
                break;
        }

        if (onActionSchedule != null) {
            onActionSchedule.onActionSchedule(schedule, operationSchedule);
        }        

    }

    private void processCancelSchedule() {

    }

    private void processActionSchedule() {

        MyHomeIotTools tool;
        int duration;
        tool = new MyHomeIotTools();

        duration = tool.diffDate(
                binding.timePickerFrom.getHour(),
                binding.timePickerFrom.getMinute(),
                binding.timePickerTo.getHour(),
                binding.timePickerTo.getMinute());

        if (schedule == null) {
            schedule = new IotScheduleDeviceSwitch();
            schedule.setScheduleType(IOT_CLASS_SCHEDULE.DIARY_SCHEDULE);
            schedule.setHour(binding.timePickerFrom.getHour());
            schedule.setMinute(binding.timePickerFrom.getMinute());
            schedule.setDuration(duration);
            schedule.setMask(tool.readMask(listControlWeek));
            schedule.setRelay(IOT_SWITCH_RELAY.ON);

        }

        
    }










}