package net.jajica.myhomeiot;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import net.jajica.libiot.IOT_DEVICE_USERS_RESULT;
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
    private final String TAG = "AdminRoomsFragment";


    public AdminRoomsFragment(IotUsersDevices configuration, String siteName) {
        this.configuration = configuration;
        this.siteName = siteName;
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
        initDataAdminRooms();


        return rootView;

    }

    private void initDataAdminRooms() {

        int indexSite;
        int indexRoom;
        IotRoomsDevices room;

        context = getActivity();
        indexSite = configuration.searchSiteOfUser(siteName);
        site = configuration.getSiteList().get(indexSite);
        mbinding.recyclerAdminRoom.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ListRoomsAdapter(site.getRoomList(), site.getRoomList().get(0).getNameRoom(), context);
        mbinding.recyclerAdminRoom.setAdapter(adapter);
        mbinding.buttonAddRoom.setOnClickListener(this);
        adapter.setOnRowSelectedData(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.buttonAddRoom):
                DialogName window;
                window = new DialogName(context);
                window.setCancelable(false);
                window.setParameterDialog(R.drawable.ic_action_add_room, R.string.add_room, R.string.error_message_uniq_home);
                window.alertDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addRoom(window.getTextName());

                    }
                });
                window.alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                window.show(getParentFragmentManager(), getResources().getString(R.string.add_room));
                break;
        }
    }

    private void addRoom(String nameRoom) {

        IOT_DEVICE_USERS_RESULT result;
        IotRoomsDevices room;
        room = new IotRoomsDevices();
        room.setNameRoom(nameRoom);
        room.setIdRoom(site.getRoomList().size()-1);
        result = site.insertRoomForSite(room);
        configuration.saveConfiguration(context);
        adapter.notifyItemInserted(site.getRoomList().size());

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
        Log.i(TAG, "kk");

    }
}