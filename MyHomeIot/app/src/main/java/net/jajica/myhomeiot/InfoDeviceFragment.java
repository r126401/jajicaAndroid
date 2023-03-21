package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotInfoDevice;
import net.jajica.myhomeiot.databinding.FragmentInfoDeviceBinding;
import net.jajica.myhomeiot.databinding.FragmentParentHomesBinding;

import java.util.ArrayList;


public class InfoDeviceFragment extends Fragment {

    private final String TAG = "InfoDeviceFragment";
    FragmentInfoDeviceBinding mBinding;
    private InfoDeviceAdapter adapter;

    private ArrayList<IotInfoDevice> listInfoDevice;


    public InfoDeviceFragment() {
        // Required empty public constructor
    }


    public InfoDeviceFragment(ArrayList<IotInfoDevice> listInfoDevice) {
        this.listInfoDevice = listInfoDevice;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView;
        mBinding = FragmentInfoDeviceBinding.inflate(inflater, container, false);
        rootView = mBinding.getRoot();
        mBinding.recyclerInfoDevice.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        createListInfoDevice();


        // Inflate the layout for this fragment
        return rootView;
    }

    private void createListInfoDevice() {

        if (adapter == null) {
            adapter = new InfoDeviceAdapter(listInfoDevice, getActivity());
        } else {

        }

        mBinding.recyclerInfoDevice.setAdapter(adapter);
        adapter.setOnSelectedParameterListener(new InfoDeviceAdapter.OnSelectedParameterListener() {

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            @Override
            public void onSelectedIntParameter(IOT_LABELS_JSON parameter, int value) {
                SettingsFragment settingsFragment = new SettingsFragment(parameter, value, getContext());
                settingsFragment.show(fragmentTransaction, "SettingsInt");
                Log.i(TAG, "kk");
            }

            @Override
            public void onSelectedDoubleParameter(IOT_LABELS_JSON parameter, double value) {
                SettingsFragment settingsFragment = new SettingsFragment(parameter, value, getContext());
                settingsFragment.show(fragmentTransaction, "SettingsDouble");
                Log.i(TAG, "kk");
            }

            @Override
            public void onSelectedBooleanParameter(IOT_LABELS_JSON parameter, Boolean value) {
                SettingsFragment settingsFragment = new SettingsFragment(parameter, value, getContext());
                settingsFragment.show(fragmentTransaction, "SettingsBoolean");
                Log.i(TAG, "kk");
            }

            @Override
            public void onSelectedStringParameter(IOT_LABELS_JSON parameter, String value) {
                SettingsFragment settingsFragment = new SettingsFragment(parameter, value, getContext());
                settingsFragment.show(fragmentTransaction, "SettingsString");



                Log.i(TAG, "kk");
            }
        });

    }




}