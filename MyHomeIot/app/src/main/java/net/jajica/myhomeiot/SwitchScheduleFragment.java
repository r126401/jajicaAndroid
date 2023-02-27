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
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotScheduleDeviceSwitch;
import net.jajica.myhomeiot.databinding.FragmentSwitchScheduleBinding;



public class SwitchScheduleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private final String TAG = "SwitchScheduleFragment";
    private FragmentSwitchScheduleBinding mbinding;

    private SwitchScheduleAdapter adapter;

    static IotDeviceSwitch device;

    private OnSendEventSchedule onSendEventSchedule;
    public SwitchScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Contructor de la clase
     * @param device Es el dispositivo del cual hay que pintar los programas y actuar sobre ellos.
     */
    public SwitchScheduleFragment(IotDeviceSwitch device) {


        this.device = device;
    }

    @Override
    public void onRefresh() {

        if (onSendEventSchedule != null) {
            onSendEventSchedule.onSendEventSchedule(ActionSwitchScheduleFragment.OPERATION_SCHEDULE.REFRESH_SCHEDULE);
        }
        mbinding.swipeSwitchScheduleList.setRefreshing(false);

    }


    /**
     * Interfaz utilizado para enviar a la activity los eventos que se produzcan en las operaciones
     * sobre los programas y de esta manera poder actualizar la vista principal del dispositivo
     */
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

    /**
     * Este metodo configura los listener que va a utilizar el fragment.
     */
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

    /**
     * Este metodo crea la vista que se va a visualizar en el fragment
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        mbinding = FragmentSwitchScheduleBinding.inflate(inflater, container, false);
        rootView = mbinding.getRoot();
        configureListener();
        fillAdapter();
        return rootView;

    }

    /**
     * Este metodo pinta los programas del dispositivo.
     */
    private void fillAdapter() {

        if (adapter == null) {
            adapter = new SwitchScheduleAdapter(device.getSchedulesSwitch(), getActivity().getApplicationContext());
            configureListenerAdapter();
        }
        mbinding.recyclerSwitchScheduleList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mbinding.recyclerSwitchScheduleList.setAdapter(adapter);
        mbinding.swipeSwitchScheduleList.setOnRefreshListener(this);



    }

    /**
     * Este metodo implemente los listener suminitrados por el adapter para poder tratar los eventos
     * que se pueden hacer de cada uno de los programas que estan en el adapter.
     * Delete schedule
     * Change_status_schedule
     * Modify_schedule. Este último, abre un nuevo fragment para modificar el programa.
     */

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