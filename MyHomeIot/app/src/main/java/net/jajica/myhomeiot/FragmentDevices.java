package net.jajica.myhomeiot;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import net.jajica.libiot.IotDevice;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class FragmentDevices extends Fragment {

    public FragmentDevices(ArrayList<IotDevice> devices, Context context, int idLayout) {
        this.devices = devices;
        this.context = context;
        this.idLayout = idLayout;
    }

    private ArrayList<IotDevice> devices;
    private Context context;
    private int idLayout;


    public FragmentDevices() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_devices, container, false);

        GridViewWithHeaderAndFooter grid = (GridViewWithHeaderAndFooter)
                rootView.findViewById(R.id.gridview);

        grid.setAdapter(new DevicesAdapter(getActivity(), idLayout, devices));





        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



}