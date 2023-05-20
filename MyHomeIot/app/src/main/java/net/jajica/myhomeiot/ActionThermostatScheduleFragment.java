package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.jajica.libiot.IOT_CLASS_SCHEDULE;
import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotScheduleDeviceThermostat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.jajica.myhomeiot.databinding.FragmentActionThermostatScheduleBinding;


public class ActionThermostatScheduleFragment extends Fragment implements View.OnClickListener {



    /**
     * Enumera las operaciones que se pueden realizar desde este fragment.
     */
    enum OPERATION_SCHEDULE {
        NEW_SCHEDULE,
        DELETE_SCHEDULE,
        MODIFY_SCHEDULE,
        DISPLAY_SCHEDULE,
        REFRESH_SCHEDULE,
        UPDATE_SCHEDULE,
        TIMEOUT,
    }

    private final String TAG = "ActionThermostatScheduleFragment";

    private FragmentActionThermostatScheduleBinding binding;
    private IotScheduleDeviceThermostat schedule;
    private IotScheduleDeviceThermostat originalSchedule;

    private OPERATION_SCHEDULE operationSchedule;

    private OnActionSchedule onActionSchedule;

    private ArrayList<AppCompatTextView> listWeek;

    private String oldSchedule;
    private CountDownTimer counter;
    private Handler handler;
    private Boolean autoincremento;
    private Boolean autodecremento;




    /**
     * Este interface implementa las notificaciones de las operaciones que se realizan desde este
     * fragment con el objetivo de acutalizar las vistas del adapter y principal de la activity
     */
    public interface OnActionSchedule {

        Boolean onActionSchedule(IotScheduleDeviceThermostat schedule, ActionThermostatScheduleFragment.OPERATION_SCHEDULE operationSchedule, String aditionalInfo);
    }

    public void setOnActionSchedule(ActionThermostatScheduleFragment.OnActionSchedule onActionSchedule) {
        this.onActionSchedule = onActionSchedule;
    }




    public ActionThermostatScheduleFragment() {
        // Required empty public constructor
    }


    public ActionThermostatScheduleFragment(IotScheduleDeviceThermostat schedule) {

        if (schedule == null) {
            operationSchedule = OPERATION_SCHEDULE.NEW_SCHEDULE;
        } else {
            operationSchedule = OPERATION_SCHEDULE.MODIFY_SCHEDULE;
        }

        if (schedule != null) {
            this.originalSchedule = schedule;
            this.schedule = (IotScheduleDeviceThermostat) originalSchedule.clone();
            oldSchedule = schedule.getScheduleId();
        } else {
            oldSchedule = null;
        }

    }

    private void initListWeek() {
        int i;

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
        for (i=0; i< listWeek.size(); i++) {
            listWeek.get(i).setTag(false);
        }
    }

    private void initDataFragment() {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        String data;
        String hour;
        String minute;
        int hourDat;
        int minDat;
        int i;



        binding.layoutThreshold.setVisibility(View.VISIBLE);
        binding.textThreshold.setText("22.5");

        if (operationSchedule == OPERATION_SCHEDULE.MODIFY_SCHEDULE) {

            binding.timePickerFrom.setHour(schedule.getHour());
            binding.timePickerFrom.setMinute(schedule.getMinute());
            data = tool.convertDuration(schedule.getHour(), schedule.getMinute(), schedule.getDuration());
            hour = tool.extractHourForConvertDuration(data);
            minute = tool.extractMinuteForConvertDuration(data);
            binding.timePickerTo.setHour(Integer.parseInt(hour));
            binding.timePickerTo.setMinute(Integer.parseInt(minute));
        } else {

            schedule = new IotScheduleDeviceThermostat();
            hourDat = tool.getCurrentHourMinute(true);
            minDat = tool.getCurrentHourMinute(false);
            binding.timePickerFrom.setHour(hourDat);
            binding.timePickerFrom.setMinute(minDat);

            if ((hourDat + 3) > 24) {
                hourDat = (hourDat + 3 ) - 24;
            }
            binding.timePickerTo.setHour(hourDat);
            binding.timePickerTo.setMinute(0);
        }
        initListWeek();
        tool.mask2controls(listWeek, schedule.getActiveDays());
        binding.buttonAcceptSchedule.setOnClickListener(this);
        binding.buttonCancelSchedule.setOnClickListener(this);
        for (i=0;i<listWeek.size();i++) {
            listWeek.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatTextView control;
                    control = (AppCompatTextView)v;
                    if ((Boolean) v.getTag()) {
                        tool.updateWeekDay(control, false);
                    } else {
                        tool.updateWeekDay(control, true);
                    }

                    Log.i(TAG, "kk");
                }
            });
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;
        binding = FragmentActionThermostatScheduleBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        binding.timePickerFrom.setIs24HourView(true);
        binding.timePickerTo.setIs24HourView(true);
        binding.imageDownThreshold.setOnClickListener(this);
        binding.imageUpThreshold.setOnClickListener(this);
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
                if (processActionSchedule()) {
                    if (onActionSchedule != null) {
                        if (onActionSchedule.onActionSchedule(schedule, operationSchedule, oldSchedule)) {
                            getParentFragmentManager().popBackStack();
                        } else {
                            errorMessage();

                        }

                    }
                }

                break;
            case (R.id.buttonCancelSchedule):
                processCancelSchedule();

                break;

            case (R.id.imageUpThreshold):
                modifyDoubleValue(binding.textThreshold, true, 0.5, 0, 30);
                break;
            case (R.id.imageDownThreshold):
                modifyDoubleValue(binding.textThreshold, false, 0.5, 0, 30);
                break;

        }




    }


    private void modifyDoubleValue(TextView controlTexto, Boolean incremento, double valor, double minVal, double maxVal) {

        double cantidad;

        cantidad = Double.parseDouble(controlTexto.getText().toString());
        if (incremento == true) {
            cantidad += valor;
            if (cantidad >= maxVal) cantidad = maxVal;
        } else {
            cantidad -= valor;
            if (cantidad <= minVal) cantidad = minVal;
        }

        DecimalFormat formater = new DecimalFormat("##.#");
        String dato;
        dato = formater.format(cantidad);
        String dat;
        dat = dato.substring(0,2);
        if (dato.length() > 2) {
            dat += ".";
            dat += dato.substring(3);
        } else {
            dat += ".0";
        }

        controlTexto.setText(dat);


    }




    /**
     * Este metodo devuelve el control al fragment anterior.
     */
    private void processCancelSchedule() {

        //schedule = originalSchedule;
        getParentFragmentManager().popBackStack();


    }

    /**
     * Este metodo captura la informacion de los controles y la carga en el objeto schedule
     * para poder lanzar la peticion al dispositivo.
     *
     */

    private Boolean processActionSchedule() {

        MyHomeIotTools tool;
        int duration;
        tool = new MyHomeIotTools();



        duration = tool.diffDate(
                binding.timePickerFrom.getHour(),
                binding.timePickerFrom.getMinute(),
                binding.timePickerTo.getHour(),
                binding.timePickerTo.getMinute());
        schedule.setScheduleType(IOT_CLASS_SCHEDULE.DIARY_SCHEDULE);
        schedule.setHour(binding.timePickerFrom.getHour());
        schedule.setMinute(binding.timePickerFrom.getMinute());
        schedule.setDuration(duration);
        schedule.setMask(tool.readMask(listWeek));
        schedule.setRelay(IOT_SWITCH_RELAY.ON);
        schedule.setThresholdTemperature(Double.parseDouble(binding.textThreshold.getText().toString()));
        if (!checkRulesControls()) {
            errorMessage();
            return false;
        }
        if (schedule.getScheduleState() == IOT_STATE_SCHEDULE.UNKNOWN_SCHEDULE) {
            schedule.setScheduleState(IOT_STATE_SCHEDULE.ACTIVE_SCHEDULE);
        }

        return true;
    }


    private Boolean checkRulesControls() {

        int duration;
        MyHomeIotTools tool;
        tool = new MyHomeIotTools();

        if ((duration = tool.diffDate(
                binding.timePickerFrom.getHour(),
                binding.timePickerFrom.getMinute(),
                binding.timePickerTo.getHour(),
                binding.timePickerTo.getMinute())) <= 0) {

            Log.e(TAG, "intervalos nok");
            return false;
        }

        return true;

    }

    private void errorMessage() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_action_error);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.error_dates);
        builder.create();
        builder.show();



    }


}