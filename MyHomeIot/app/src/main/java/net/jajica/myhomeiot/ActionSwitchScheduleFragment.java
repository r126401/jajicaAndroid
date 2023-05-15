package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_CLASS_SCHEDULE;
import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IOT_SWITCH_RELAY;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.FragmentActionSwitchScheduleBinding;
import java.util.ArrayList;
import java.util.Calendar;

public class ActionSwitchScheduleFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "ActionSwitchScheduleFragment";
    private FragmentActionSwitchScheduleBinding binding;
    private IotScheduleDeviceSwitch schedule;

    private OPERATION_SCHEDULE operationSchedule;

    private OnActionSchedule onActionSchedule;

    private ArrayList<AppCompatTextView> listWeek;

    private String oldSchedule;

    /**
     * Este interface implementa las notificaciones de las operaciones que se realizan desde este
     * fragment con el objetivo de acutalizar las vistas del adapter y principal de la activity
     */
    public interface OnActionSchedule {

        void onActionSchedule(IotScheduleDeviceSwitch schedule, OPERATION_SCHEDULE operationSchedule, String adittionalInfo);
    }

    public void setOnActionSchedule(OnActionSchedule onActionSchedule) {
        this.onActionSchedule = onActionSchedule;
    }

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

    public ActionSwitchScheduleFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param schedule es el programa en curso sobre el que se puede actuar
     */
    public ActionSwitchScheduleFragment(IotScheduleDeviceSwitch schedule) {

        if (schedule == null) {
            operationSchedule = OPERATION_SCHEDULE.NEW_SCHEDULE;
        } else {
            operationSchedule = OPERATION_SCHEDULE.MODIFY_SCHEDULE;
        }

        this.schedule = schedule;
        if (schedule != null) {
            oldSchedule = schedule.getScheduleId();
        } else {
            oldSchedule = null;
        }


    }


    /**
     * Pinta adecuadamente los controles de los dias de la semana según la informacion recibida.
     */

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Inicializa los parametros del fragment
     */
    private void initDataFragment() {

        MyHomeIotTools tool;
        tool = new MyHomeIotTools();
        String data;
        String hour;
        String minute;
        int hourDat;
        int minDat;
        int i;




        if (operationSchedule == OPERATION_SCHEDULE.MODIFY_SCHEDULE) {

            binding.timePickerFrom.setHour(schedule.getHour());
            binding.timePickerFrom.setMinute(schedule.getMinute());
            data = tool.convertDuration(schedule.getHour(), schedule.getMinute(), schedule.getDuration());
            hour = tool.extractHourForConvertDuration(data);
            minute = tool.extractMinuteForConvertDuration(data);
            binding.timePickerTo.setHour(Integer.parseInt(hour));
            binding.timePickerTo.setMinute(Integer.parseInt(minute));
        } else {
            schedule = new IotScheduleDeviceSwitch();
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

    /**
     * Este metodo pinta la vista del fragment.
     * Se utiliza la tecnica para capturar los controles dinamicamente con databinding.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

    /**
     * Este metodo procesa la informacion recogida en los controles y la envia al fragment padre
     * para que pueda lanzar al comando de modificacion del programa.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.buttonAcceptSchedule):
                if (processActionSchedule()) {
                    if (onActionSchedule != null) {
                        onActionSchedule.onActionSchedule(schedule, operationSchedule, oldSchedule);
                        getParentFragmentManager().popBackStack();
                    }
                }

                break;
            case (R.id.buttonCancelSchedule):
                processCancelSchedule();

                break;
        }



    }

    /**
     * Este metodo devuelve el control al fragment anterior.
     */
    private void processCancelSchedule() {

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

        if (!checkRulesControls()) {
            errorMessage();
            return false;
        }

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
        if (schedule.getScheduleState() == IOT_STATE_SCHEDULE.UNKNOWN_SCHEDULE) {
            schedule.setScheduleState(IOT_STATE_SCHEDULE.ACTIVE_SCHEDULE);
        }

        return true;
    }


    private Boolean checkRulesControls() {

        int time1;
        int time2;

        time1 = (binding.timePickerFrom.getHour() * 3600) + (binding.timePickerFrom.getMinute() * 60);
        time2 = (binding.timePickerTo.getHour() * 3600) + (binding.timePickerTo.getMinute() * 60);

        if (time1 >= time2) {
            return false;
        } else {
            return true;
        }
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