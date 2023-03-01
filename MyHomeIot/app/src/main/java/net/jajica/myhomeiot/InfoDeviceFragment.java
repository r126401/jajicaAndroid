package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.libiot.IotInfoDevice;
import net.jajica.myhomeiot.databinding.FragmentInfoDeviceBinding;
import net.jajica.myhomeiot.databinding.FragmentParentHomesBinding;

import java.util.ArrayList;


public class InfoDeviceFragment extends Fragment {

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

    }


}