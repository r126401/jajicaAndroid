package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;
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

        return rootView;

    }

    private void fillAdapter() {

        if (adapter == null) adapter = new SwitchScheduleAdapter(device.getSchedulesSwitch(), getActivity().getApplicationContext());
        mbinding.recyclerSwitchScheduleList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mbinding.recyclerSwitchScheduleList.setAdapter(adapter);




    }




}