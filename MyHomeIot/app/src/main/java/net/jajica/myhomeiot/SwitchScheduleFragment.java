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

    IotDeviceSwitch device;
    public SwitchScheduleFragment() {
        // Required empty public constructor
    }

    public SwitchScheduleFragment(IotDeviceSwitch device) {
        this.device = device;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        mbinding = FragmentSwitchScheduleBinding.inflate(inflater, container, false);
        rootView = mbinding.getRoot();
        device.commandGetScheduleDevice();
        device.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
            @Override
            public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "hola");
                fillAdapter();
            }
        });

        device.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
            @Override
            public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "hola");
                adapter.notifyDataSetChanged();
            }
        });

        device.setOnReceivedDeleteScheduleDevice(new IotDevice.OnReceivedDeleteScheduleDevice() {
            @Override
            public void onReceivedDeleteScheduleDevice(IOT_CODE_RESULT resultCode) {
                adapter.notifyDataSetChanged();
            }
        });

        device.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "kk");
            }
        });





        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;

    }

    private void fillAdapter() {

        if (adapter == null) adapter = new SwitchScheduleAdapter(device.getSchedulesSwitch(), getActivity().getApplicationContext());

        adapter.setOnItemClickSelected(new SwitchScheduleAdapter.OnItemClickSelected() {
            @Override
            public void onItemClickSelected(SwitchScheduleAdapter.ITEM_TYPE event, int position) {

                IotScheduleDeviceSwitch schedule;
                switch (event) {

                    case DELETE_SCHEDULE:
                        Log.i(TAG, "kk");
                        break;
                    case CHANGE_STATUS_SCHEDULE:
                        Log.i(TAG, "kk");

                        schedule = device.getSchedulesSwitch().get(position);
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
                        NewSwitchScheduleFragment newSwitchScheduleFragment;
                        newSwitchScheduleFragment = new NewSwitchScheduleFragment(schedule);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.containerSwitch, newSwitchScheduleFragment, "modifySchedule");
                        fragmentTransaction.setReorderingAllowed(true);
                        fragmentTransaction.addToBackStack("schedule");
                        fragmentTransaction.commit();


                        break;
                }
            }
        });
        mbinding.recyclerSwitchScheduleList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mbinding.recyclerSwitchScheduleList.setAdapter(adapter);




    }




}