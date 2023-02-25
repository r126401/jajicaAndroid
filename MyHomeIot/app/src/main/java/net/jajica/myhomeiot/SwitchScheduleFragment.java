package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.FragmentSwitchScheduleBinding;



public class SwitchScheduleFragment extends Fragment {


    private final String TAG = "SwitchScheduleFragment";
    private FragmentSwitchScheduleBinding mbinding;

    private SwitchScheduleAdapter adapter;

    static IotDeviceSwitch device;

    private OnSendEventSchedule onSendEventSchedule;
    public SwitchScheduleFragment() {
        // Required empty public constructor
    }

    public SwitchScheduleFragment(IotDeviceSwitch device) {


        this.device = device;
        //device.subscribeDevice();
        Log.i(TAG, "device es : Constructor" + device.hashCode());
    }


    public interface OnSendEventSchedule {
        void onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE operation);
    }

    public void setOnSendEventSchedule(OnSendEventSchedule onSendEventSchedule) {
        this.onSendEventSchedule = onSendEventSchedule;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void configureListener() {

        Log.i(TAG, "device es : configureListener " + device.hashCode());
        device.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
            @Override
            public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    if (onSendEventSchedule != null) {
                        onSendEventSchedule.onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE.DISPLAY_SCHEDULE);
                    }
                }
                fillAdapter();

            }
        });

        device.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
            @Override
            public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "hola");
                adapter.notifyDataSetChanged();
                if (onSendEventSchedule != null) {
                    onSendEventSchedule.onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE.MODIFY_SCHEDULE);
                }
            }
        });

        device.setOnReceivedDeleteScheduleDevice(new IotDevice.OnReceivedDeleteScheduleDevice() {
            @Override
            public void onReceivedDeleteScheduleDevice(IOT_CODE_RESULT resultCode, String scheduleId) {
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    device.commandGetScheduleDevice();
                    Log.i(TAG, "device es : onReceivedDeleteScheduleDevice " + device.hashCode());
                    if (onSendEventSchedule != null) {
                        onSendEventSchedule.onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE.DELETE_SCHEDULE);
                    }
                }


            }
        });

        device.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    Log.i(TAG, "Se recibe la respuesta del nuevo programa y estamos listos para insertarlo");
                    device.commandGetScheduleDevice();
                    if (onSendEventSchedule != null) {
                        onSendEventSchedule.onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE.NEW_SCHEDULE);
                    }
                }
            }
        });

        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                //adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        mbinding = FragmentSwitchScheduleBinding.inflate(inflater, container, false);
        rootView = mbinding.getRoot();
        configureListener();
        fillAdapter();
        //device.commandGetScheduleDevice();
        //Log.i(TAG, "device es : commandGetScheduleDevice" + device.hashCode());

        return rootView;

    }

    private void fillAdapter() {

        if (adapter == null) {
            adapter = new SwitchScheduleAdapter(device.getSchedulesSwitch(), getActivity().getApplicationContext());
            configureListenerAdapter();
        }
        mbinding.recyclerSwitchScheduleList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mbinding.recyclerSwitchScheduleList.setAdapter(adapter);



    }


    private void configureListenerAdapter() {
        adapter.setOnItemClickSelected(new SwitchScheduleAdapter.OnItemClickSelected() {
            @Override
            public void onItemClickSelected(SwitchScheduleAdapter.ITEM_TYPE event, int position) {

                IotScheduleDeviceSwitch schedule;
                switch (event) {

                    case DELETE_SCHEDULE:
                        Log.i(TAG, "kk");
                        schedule = device.getSchedulesSwitch().get(position);
                        device.commandDeleteScheduleDevice(schedule.getScheduleId());
                        Log.i(TAG, "device es : adapter delete" + device.hashCode());
                        break;
                    case CHANGE_STATUS_SCHEDULE:
                        Log.i(TAG, "kk");

                        schedule = device.getSchedulesSwitch().get(position);
                        Log.i(TAG, "device es : change estatus" + device.hashCode());
                        if (schedule.getScheduleState() == IOT_STATE_SCHEDULE.ACTIVE_SCHEDULE) {
                            schedule.setScheduleState(IOT_STATE_SCHEDULE.INACTIVE_SCHEDULE);

                        } else {
                            schedule.setScheduleState(IOT_STATE_SCHEDULE.ACTIVE_SCHEDULE);
                        }
                        device.commandModifyScheduleDevice(schedule);
                        break;
                    case MODIFY_SCHEDULE:
                        Log.i(TAG, "kk");
                        schedule = device.getSchedulesSwitch().get(position);
                        Log.i(TAG, "device: Fragment" + device.hashCode());
                        ActionSwitchScheduleFragment actionSwitchScheduleFragment;
                        actionSwitchScheduleFragment = new ActionSwitchScheduleFragment(schedule);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.containerSwitch, actionSwitchScheduleFragment, "modifySchedule");
                        fragmentTransaction.setReorderingAllowed(true);
                        fragmentTransaction.addToBackStack("schedule");
                        fragmentTransaction.commit();


                        break;
                }
            }
        });

    }





}