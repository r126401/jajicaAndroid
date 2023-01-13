package net.jajica.myhomeiot;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

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
        View rootView = inflater.inflate(R.layout.list_devices, container, false);

        RecyclerView recyclerView = (RecyclerView)
                rootView.findViewById(R.id.recyclerDevices);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(new IotDeviceAdapter(getActivity(), R.id.recyclerDevices, devices));






        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



}