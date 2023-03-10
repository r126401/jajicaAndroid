package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_STATE_SCHEDULE;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceThermostat;

import net.jajica.libiot.IotScheduleDeviceThermostat;
import net.jajica.myhomeiot.databinding.FragmentThermostatScheduleBinding;
public class ThermostatScheduleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private final String TAG = "ThermostatScheduleFragment";
    private FragmentThermostatScheduleBinding mBinding;
    private IotDeviceThermostat device;

    private ThermostatScheduleAdapter adapter;

    private ThermostatScheduleAdapter.OnItemClickSelected onItemClickSelected;

    private ThermostatScheduleFragment.OnSendEventSchedule onSendEventSchedule;

    /**
     * Interfaz utilizado para enviar a la activity los eventos que se produzcan en las operaciones
     * sobre los programas y de esta manera poder actualizar la vista principal del dispositivo
     */
    public interface OnSendEventSchedule {
        void onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE operation);
    }

    public void setOnSendEventSchedule(ThermostatScheduleFragment.OnSendEventSchedule onSendEventSchedule) {
        this.onSendEventSchedule = onSendEventSchedule;
    }



    public ThermostatScheduleFragment(IotDeviceThermostat device) {

        this.device = device;
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
        mBinding = FragmentThermostatScheduleBinding.inflate(inflater, container, false);
        rootView = mBinding.getRoot();
        configureListener();
        fillAdapter();
        return rootView;




    }

    private void fillAdapter() {

        if (adapter == null) {
            adapter = new ThermostatScheduleAdapter(device.getSchedulesThermostat(), getActivity().getApplicationContext());
            configureListenerAdapter();
        } else {
            adapter.setListSchedule(device.getSchedulesThermostat());
            adapter.notifyDataSetChanged();


        }

        mBinding.recyclerThermostatScheduleList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mBinding.recyclerThermostatScheduleList.setAdapter(adapter);
        mBinding.swipeThermostatScheduleList.setOnRefreshListener(this);

    }

    private void configureListener() {

        //Recibir programa
        device.setOnReceivedScheduleDevice(new IotDevice.OnReceivedScheduleDevice() {
            @Override
            public void onReceivedScheduleDevice(IOT_CODE_RESULT resultCode) {

            }
        });

        // Recibir modificar programa
        device.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
            @Override
            public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        //Recibir borrar programa
        device.setOnReceivedDeleteScheduleDevice(new IotDevice.OnReceivedDeleteScheduleDevice() {
            @Override
            public void onReceivedDeleteScheduleDevice(IOT_CODE_RESULT resultCode, String scheduleId) {

            }
        });

        //Recibir nuevo programa
        device.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {

            }
        });

        //Recibir timeout
        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {

            }
        });

    }

    private void configureListenerAdapter() {

        adapter.setOnItemClickSelected(new ThermostatScheduleAdapter.OnItemClickSelected() {
            @Override
            public void onItemClickSelected(ThermostatScheduleAdapter.ITEM_TYPE event, int position) {
                IotScheduleDeviceThermostat schedule;
                switch (event) {

                    case DELETE_SCHEDULE:
                        Log.i(TAG, "kk");
                        schedule = device.getSchedulesThermostat().get(position);
                        device.commandDeleteScheduleDevice(schedule.getScheduleId());
                        Log.i(TAG, "device es : adapter delete" + device.hashCode());
                        break;
                    case CHANGE_STATUS_SCHEDULE:
                        Log.i(TAG, "kk");

                        schedule = device.getSchedulesThermostat().get(position);
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
                        schedule = device.getSchedulesThermostat().get(position);
                        Log.i(TAG, "device: Fragment" + device.hashCode());
                        ActionThermostatScheduleFragment actionThermostatScheduleFragment;
                        actionThermostatScheduleFragment = new ActionThermostatScheduleFragment(schedule);
                        actionThermostatScheduleFragment.setOnActionSchedule(new ActionThermostatScheduleFragment.OnActionSchedule() {
                            @Override
                            public void onActionSchedule(IotScheduleDeviceThermostat schedule, ActionThermostatScheduleFragment.OPERATION_SCHEDULE operationSchedule, String adittionalInfo) {
                                device.commandModifyScheduleDevice(schedule);
                            }
                        });
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.containerThermostat, actionThermostatScheduleFragment, "modifyScheduleThermostat");
                        fragmentTransaction.setReorderingAllowed(true);
                        fragmentTransaction.addToBackStack("scheduleThermostat");
                        fragmentTransaction.commit();


                        break;
                }
            }
        });



    }

    @Override
    public void onRefresh() {

        if (onSendEventSchedule != null) {
            onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.REFRESH_SCHEDULE);
        }
        mBinding.swipeThermostatScheduleList.setRefreshing(false);

    }
}