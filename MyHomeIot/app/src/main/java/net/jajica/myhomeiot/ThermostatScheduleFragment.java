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
    static IotDeviceThermostat device;

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
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    if (onSendEventSchedule != null) {
                        onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.DISPLAY_SCHEDULE);
                    }
                }
                fillAdapter();

            }
        });

        // Recibir modificar programa
        device.setOnReceivedModifySchedule(new IotDevice.OnReceivedModifySchedule() {
            @Override
            public void onReceivedModifySchedule(IOT_CODE_RESULT resultCode) {
                Log.i(TAG, "hola");
                adapter.notifyDataSetChanged();
                if (onSendEventSchedule != null) {
                    onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.MODIFY_SCHEDULE);
                }

            }
        });

        //Recibir borrar programa
        device.setOnReceivedDeleteScheduleDevice(new IotDevice.OnReceivedDeleteScheduleDevice() {
            @Override
            public void onReceivedDeleteScheduleDevice(IOT_CODE_RESULT resultCode, String scheduleId) {
                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    device.commandGetScheduleDevice();
                    Log.i(TAG, "device es : onReceivedDeleteScheduleDevice " + device.hashCode());
                    if (onSendEventSchedule != null) {
                        onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.DELETE_SCHEDULE);
                    }
                }
            }
        });

        //Recibir nuevo programa
        device.setOnReceivedNewSchedule(new IotDevice.OnReceivedNewSchedule() {
            @Override
            public void onReceivedNewSchedule(IOT_CODE_RESULT resultCode) {

                if (resultCode == IOT_CODE_RESULT.RESUT_CODE_OK) {
                    Log.i(TAG, "Se recibe la respuesta del nuevo programa y estamos listos para insertarlo");
                    device.commandGetScheduleDevice();
                    if (onSendEventSchedule != null) {
                        onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.NEW_SCHEDULE);
                    }
                }

            }
        });

        //Recibir timeout
        device.setOnReceivedTimeoutCommand(new IotDevice.OnReceivedTimeoutCommand() {
            @Override
            public void onReceivedTimeoutCommand(String token) {
                Log.e(TAG, "Recibido timeout");
                onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.TIMEOUT);

            }
        });

        device.setOnReceivedSpontaneousStartSchedule(new IotDevice.OnReceivedSpontaneousStartSchedule() {
            @Override
            public void onReceivesSpontaneousStartSchedule(IOT_CODE_RESULT resultCode) {

                adapter.notifyDataSetChanged();
                if (onSendEventSchedule != null) {
                    onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.UPDATE_SCHEDULE);
                }

            }
        });




        device.setOnReceivedSpontaneousEndSchedule(new IotDevice.OnReceivedSpontaneousEndSchedule() {
            @Override
            public void onReceivesSpontaneousEndSchedule(IOT_CODE_RESULT resultCode) {

                adapter.notifyDataSetChanged();
                if (onSendEventSchedule != null) {
                    onSendEventSchedule.onSendEventSchedule(ActionThermostatScheduleFragment.OPERATION_SCHEDULE.UPDATE_SCHEDULE);
                }

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
                            public Boolean onActionSchedule(IotScheduleDeviceThermostat schedule, ActionThermostatScheduleFragment.OPERATION_SCHEDULE operationSchedule, String aditionalInfo) {
                                if (device.checkValidScheduleThermostatDevice(schedule, aditionalInfo)) {
                                    device.commandModifyScheduleDevice(schedule);
                                    return true;
                                } else {
                                    return false;
                                }

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