package net.jajica.myhomeiot;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.FragmentAdminRoomsBinding;

import java.util.ArrayList;

public class AdminRoomsFragment extends Fragment  implements View.OnClickListener, ListRoomsAdapter.OnRowSelectedData{


    private String siteName;
    private IotSitesDevices site;
    private FragmentAdminRoomsBinding mbinding;
    private ArrayList<IotRoomsDevices> roomsList;
    private ListRoomsAdapter adapter;
    private Context context;
    private IotUsersDevices configuration;


    public AdminRoomsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        mbinding = FragmentAdminRoomsBinding.inflate(getLayoutInflater());
        rootView = mbinding.getRoot();
        siteName = requireArguments().getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson());
        initDataAdminRooms();


        return rootView;

    }

    private void initDataAdminRooms() {

        int indexSite;
        int indexRoom;
        IotRoomsDevices room;

        context = getActivity().getApplicationContext();
        configuration = new IotUsersDevices(context);
        configuration.loadConfiguration();
        indexSite = configuration.searchSiteOfUser(siteName);
        site = configuration.getSiteList().get(indexSite);
        mbinding.recyclerAdminRoom.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ListRoomsAdapter(site.getRoomList(), site.getRoomList().get(0).getNameRoom(), context);
        mbinding.recyclerAdminRoom.setAdapter(adapter);
        mbinding.buttonAddRoom.setOnClickListener(this);
        mbinding.buttonNewRoom.setOnClickListener(this);
        adapter.setOnRowSelectedData(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.buttonAddRoom):
                mbinding.editAddRoom.setEnabled(true);
                mbinding.editAddRoom.setVisibility(View.VISIBLE);
                mbinding.editAddRoom.requestFocus();
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.buttonNewRoom.setVisibility(View.VISIBLE);
                break;
            case (R.id.buttonNewRoom):
                addRoom();

                break;
        }
    }

    private void addRoom() {

        IotRoomsDevices room;
        room = new IotRoomsDevices();
        room.setNameRoom(mbinding.editAddRoom.getText().toString());
        room.setIdRoom(site.getRoomList().size()-1);
        site.insertRoomForSite(room);
        configuration.saveConfiguration(context);
        adapter.notifyItemInserted(site.getRoomList().size());

    }

    private void showKeyboard(int action) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(action, 0);
    }

    @Override
    public void onRowSelectedData(String roomName, int position) {

    }

    @Override
    public void onDeleteData(String rootName, int position) {
        deleteRoom(rootName, position);

    }

    private void deleteRoom(String rootName, int position) {


        site.deleteRoomForSite(rootName, false);
        adapter.notifyItemRemoved(position);
        configuration.saveConfiguration(context);

    }

    @Override
    public void onRowEditData(String rootName, int position) {

    }
}