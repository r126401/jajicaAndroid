package net.jajica.myhomeiot;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.jajica.libiot.IOT_DEVICE_STATUS;
import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotDevice;


import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotTools;
import net.jajica.libiot.IotUsersDevices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.jajica.myhomeiot.databinding.ActivityMainBinding;




public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, Serializable, View.OnClickListener, FragmentDevices.OnOperationDeviceListener {

    private final String TAG = "MainActivity";
    private IotUsersDevices configuration;
    private IotMqttConnection cnx;
    private ActivityMainBinding binding;

    private String currentSite;
    private String currentRoom;

    private IotDevice deviceCut;
    private String cutRoom;
    private String cutSite;
    private int cutPosition;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (initApplication() == APPLICATION_STATUS.APPLICATION_OK) {
            connectAllDevices();
        }

    }

    private void connectAllDevices() {

        connectUnknownDevices();
        connectSwitchDevices();
        connectThermometerDevices();
        connectThermostatDevices();



    }



    private void connectThermometerDevices() {



    }

    private void connectThermostatDevices() {
    }

    private void connectSwitchDevices() {
    }

    private void connectUnknownDevices() {
    }


    private APPLICATION_STATUS initApplication() {

        IOT_OPERATION_CONFIGURATION_DEVICES result;
        APPLICATION_STATUS appStatus = APPLICATION_STATUS.APPLICATION_OK;
        // capture controls
        currentRoom = null;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolbar();
        binding.bottomNavigationMenu.setOnItemSelectedListener(this);
        binding.textHome.setOnClickListener(this);
        binding.buttonViewGrid.setOnClickListener(this);
        binding.imageMoveDevice.setOnClickListener(this);
        binding.imageMoveDevice.setTag(false);


        result = loadConfiguration();

        switch (result) {

            case OK_CONFIGURATION:
                if (configuration.getSiteList() == null) {
                    createDefaultSiteRoomConfiguration();
                }

                break;
            case CORRUPTED_CONFIGURATION:
            case NO_CONFIGURATION:
                appStatus = APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
                createDefaultSiteRoomConfiguration();
                break;

        }

        if (createEnvironment(-1) != IOT_MQTT_STATUS_CONNECTION.CONEXION_MQTT_CON_EXITO) {
            appStatus = APPLICATION_STATUS.APPLICATION_NOK;
            return appStatus;
        }
        binding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        return appStatus;
    }

    private void createDefaultSiteRoomConfiguration() {

        IotSitesDevices site;
        IotRoomsDevices room;
        site = new IotSitesDevices();
        site.setSiteName(getResources().getString(R.string.default_home));
        room = new IotRoomsDevices();
        room.setIdRoom(1);
        room.setNameRoom(getResources().getString(R.string.default_room));
        site.insertRoomForSite(room);
        configuration.insertSiteForUser(site);
        configuration.object2json();
        configuration.saveConfiguration(getApplicationContext());



    }




    private IOT_OPERATION_CONFIGURATION_DEVICES loadConfiguration() {

        configuration = new IotUsersDevices(getApplicationContext());
        return configuration.loadConfiguration();

    }

    private void notifyConnectOk() {

        binding.toolbar.getMenu().getItem(0).setTitle(cnx.getIotMqttConfiguration().getBrokerId());
        binding.toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_connect_ok);


    }

    private void notifyConnectNok() {
        binding.toolbar.getMenu().getItem(0).setTitle(cnx.getIotMqttConfiguration().getBrokerId());
        binding.toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_connect_nok);

    }

    private IOT_MQTT_STATUS_CONNECTION createEnvironment(int position) {

        IOT_MQTT_STATUS_CONNECTION state;
        cnx = new IotMqttConnection(getApplicationContext());
        state = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion estabilizada");
                if (createStructure(position) != APPLICATION_STATUS.APPLICATION_NOK) {
                    notifyConnectOk();
                }


            }

            @Override
            public void connectionLost(Throwable cause) {
                notifyConnectNok();

            }
        });

        return state;

    }



    private APPLICATION_STATUS createStructure(int position) {

        int i;
        int indexSite;
        ArrayList<IotRoomsDevices> rooms = null;
        ArrayList<IotDevice> devices;


        if (configuration.getSiteList() != null) {
            indexSite = configuration.searchSiteOfUser(configuration.getCurrentSite());
            if (indexSite == -1) {
                configuration.setCurrentSite(configuration.getSiteList().get(0).getSiteName());
                indexSite = 0;
                configuration.saveConfiguration(getApplicationContext());
            }
            rooms = configuration.getSiteList().get(indexSite).getRoomList();
            currentSite = configuration.getSiteList().get(indexSite).getSiteName();
            binding.textHome.setText(currentSite);
        } else {
            currentSite = getResources().getString(R.string.none_home);
            binding.textHome.setText(currentSite);
        }

        if (rooms == null)  {

            return APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        int j;
        for (i=0;i < rooms.size();i++) {
            devices = rooms.get(i).getDeviceList();
            if (devices != null) {
                for (j = 0; j < devices.size(); j++) {
                    devices.get(j).setCnx(cnx);
                }
            }
            FragmentDevices fragmentDevices = new FragmentDevices(devices, getApplicationContext(), configuration.getCurrentSite(), rooms.get(i).getNameRoom() );
            viewPagerAdapter.addFragment(fragmentDevices);
            fragmentDevices.setOnOperationDeviceListener(this);

        }


        binding.pager.setAdapter(viewPagerAdapter);
        ArrayList<IotRoomsDevices> finalRooms = rooms;
        new TabLayoutMediator(binding.tabs, binding.pager, (tab, position1) -> {
            int i1;
            for(i1 =0; i1 < finalRooms.size(); i1++) {
                if (i1 == position1) {
                    tab.setText(finalRooms.get(i1).getNameRoom());
                }

            }

        }).attach();


        currentRoom = Objects.requireNonNull(Objects.requireNonNull(binding.tabs.getTabAt(binding.tabs.getSelectedTabPosition())).getText()).toString();
        if (position >= 0) {
            Objects.requireNonNull(binding.tabs.getTabAt(position)).select();
        }


        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                currentRoom = Objects.requireNonNull(tab.getText()).toString();

                // Si estamos en la fase de pegado
                if ((Boolean) binding.imageMoveDevice.getTag()) {
                    //Si estamos en el mismo site y room donde cortamos, imageMoveDevice es invisible
                    if ((currentSite.equals(cutSite)) && (currentRoom.equals(cutRoom))) {
                        binding.imageMoveDevice.setVisibility(View.INVISIBLE);
                    } else {
                        binding.imageMoveDevice.setVisibility(View.VISIBLE);
                        binding.imageMoveDevice.setImageResource(R.drawable.ic_action_paste);
                    }
                } else {
                    binding.imageMoveDevice.setVisibility(View.INVISIBLE);
                }



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i(TAG, "me han reseleccionado");
                currentRoom = Objects.requireNonNull(tab.getText()).toString();
            }
        });


        return APPLICATION_STATUS.APPLICATION_OK;


    }



    /**
     * Establece la toolbar como action bar
     */
    private void setToolbar() {
        setSupportActionBar(binding.toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle


            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setTitle(null);



        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, binding.toolbar.getMenu());

        return super.onCreateOptionsMenu(menu);
    }

    private void lanzarActivityInstalarDispositivo() {

        Intent instalarDispositivo = new Intent(MainActivity.this, EspTouchActivity.class);
        //startActivity(instalarDispositivo);
        lanzadorActivityInstalarDispositivo.launch(instalarDispositivo);
    }




    ActivityResultLauncher<Intent>lanzadorActivityInstalarDispositivo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                    String data = result.getData().getDataString();
                    notifyNewDevice(data);
                }
            }
    );


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.itemSettings:
                lanzarActivitySettings();
                break;
            case R.id.itemInstallDevice:
                lanzarActivityInstalarDispositivo();
                break;
            case R.id.itemNewDevice:
                launchActivityNewDevice();
                break;

        }
        return false;
    }






    ActivityResultLauncher<Intent> lanzadorActivitySettings = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                    String data = result.getData().getDataString();
                    Log.i(getLocalClassName(), "Recibimos datos " + data);
                    insertSettingsIntoConfiguration(data);


                } else {
                    Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                }

            }
    );

    private void lanzarActivitySettings() {

        JSONObject data;
        Intent lanzadorActivitysettings = new Intent(MainActivity.this, SettingsActivity.class);
        data = configuration.getJsonObject();
        if (data != null) {
            lanzadorActivitysettings.putExtra(IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson(), configuration.getJsonObject().toString());
        } else {
            lanzadorActivitysettings.putExtra(IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson(), "null");

        }
        lanzadorActivitySettings.launch(lanzadorActivitysettings);
    }

    private void insertSettingsIntoConfiguration(String data) {

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(data);
            configuration.setUser(jsonObject.getString(IOT_LABELS_JSON.USER.getValorTextoJson()));
            configuration.setPassword(jsonObject.getString(IOT_LABELS_JSON.PASSWORD.getValorTextoJson()));
            configuration.setMail(jsonObject.getString(IOT_LABELS_JSON.MAIL.getValorTextoJson()));
            configuration.setDni(jsonObject.optString(IOT_LABELS_JSON.DNI.getValorTextoJson()));
            configuration.setTelephone(jsonObject.optString(IOT_LABELS_JSON.TELEPHONE.getValorTextoJson()));
            configuration.object2json();
            configuration.saveConfiguration(getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    ActivityResultLauncher<Intent> launcherActivityNewDevice = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                    String data = result.getData().getDataString();
                    Log.i(getLocalClassName(), "Recibimos datos " + data);
                    notifyNewDevice(data);


                } else {
                    Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                }

            }
    );





    private void launchActivityNewDevice() {

        Intent launcherActivity = new Intent(MainActivity.this, AddDeviceActivity.class);
        launcherActivityNewDevice.launch(launcherActivity);
    }


    ActivityResultLauncher<Intent> launcherHomesActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getData() != null) {
                        String data = result.getData().getDataString();
                        if (!configuration.getCurrentSite().equals(data)) {
                            configuration.setCurrentSite(data);
                            configuration.saveConfiguration(getApplicationContext());
                            binding.textHome.setText(configuration.getCurrentSite());


                        }
                    }
                    configuration.reloadConfiguration();
                    createEnvironment(-1);

                }
            }
    );

    private void launchHomesActivity() {
        Intent launcherActivity = new Intent(MainActivity.this, HomesActivity.class);
        launcherHomesActivity.launch(launcherActivity);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.textHome):
                launchHomesActivity();
                break;
            case (R.id.buttonViewGrid):
                FragmentDevices fragmentDevices;
                fragmentDevices = identifyActiveFragment();
                int gridcount;
                gridcount = fragmentDevices.getGrid().getSpanCount();
                if (gridcount == 1) {
                    fragmentDevices.getGrid().setSpanCount(2);
                } else {
                    fragmentDevices.getGrid().setSpanCount(1);
                }
                break;
            case (R.id.imageMoveDevice):
                pasteDevice();



                break;

        }
    }




    private void notifyNewDevice(String data) {

        IotTools tools;
        MyHomeIotTools myHome;
        String deviceId;
        String deviceName;
        FragmentDevices fragmentDevices;
        tools = new IotTools();
        fragmentDevices = identifyActiveFragment();
        deviceId = tools.getJsonString(data, IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson());
        deviceName = tools.getJsonString(data, IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson());

        if ((configuration.searchDeviceObject(deviceId) != null) || (configuration.searchDeviceObjectByName(deviceName) != null)){

            myHome = new MyHomeIotTools();
            myHome.errorMessage(R.drawable.ic_action_error, R.string.error_new_device, R.string.error_device_exits, this);
        } else {
            fragmentDevices.connectNewDevice(deviceId, deviceName, cnx);
        }





    }



    private FragmentDevices identifyActiveFragment() {

        FragmentManager fragmentManager;
        List<Fragment> listFragments;
        fragmentManager = getSupportFragmentManager();
        listFragments = fragmentManager.getFragments();
        int index = binding.tabs.getSelectedTabPosition();
        return (FragmentDevices) listFragments.get(index);

    }

    private ArrayList<IotDevice> getCurrentDeviceList() {

        IotRoomsDevices room;
        IotSitesDevices site;
        site = configuration.searchSiteObject(currentSite);
        room = site.searchRoomObject(currentRoom);
        return room.getDeviceList();

    }

    private IotRoomsDevices getCurrentRoom() {

        IotSitesDevices site;
        site = configuration.searchSiteObject(currentSite);
        return site.searchRoomObject(currentRoom);


    }
    @Override
    public IOT_OPERATION_CONFIGURATION_DEVICES onOperationDeviceListener(FragmentDevices.OPERATION_DEVICE operationDevice, IotDevice device, IOT_DEVICE_TYPE deviceType, int position) {



        FragmentDevices fragmentDevices;
        ArrayList<IotDevice> deviceList;
        IOT_OPERATION_CONFIGURATION_DEVICES result = IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_ERROR;

        if ((fragmentDevices = identifyActiveFragment()) == null) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_ERROR;
        }
        //deviceList = getCurrentDeviceList();

        switch (operationDevice) {

            case CREATE_DEVICE:
                if ((result = configuration.insertIotDevice(device, currentSite, currentRoom)) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED) {
                    deviceList = getCurrentDeviceList();
                    position = getCurrentRoom().searchDevice(device.getDeviceId());
                    fragmentDevices.setDeviceList(deviceList, operationDevice, position);
                    return result;
                }
                break;
            case DELETE_DEVICE:
                if ((result = configuration.deleteIotDevice(device.getDeviceId(), currentSite, currentRoom)) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_REMOVED) {
                    deviceList = getCurrentDeviceList();
                    fragmentDevices.setDeviceList(deviceList, operationDevice, position);
                    return result;
                }
                break;
            case MODIFY_DEVICE:
                if ((result = configuration.modifyIotDevice(device, currentSite, currentRoom)) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED) {
                    deviceList = getCurrentDeviceList();
                    fragmentDevices.setDeviceList(deviceList, operationDevice, position);
                }

                break;
            case SELECTED_DEVICE:
                break;

            case CUT_DEVICE:
                deviceCut = device;
                deviceCut.setDeviceStatus(IOT_DEVICE_STATUS.CUTTING_DEVICE);
                fragmentDevices.adapter.notifyItemChanged(position);
                cutRoom = currentRoom;
                cutSite = currentSite;
                cutPosition = binding.tabs.getSelectedTabPosition();
                binding.imageMoveDevice.setImageResource(R.drawable.ic_action_cut);
                binding.imageMoveDevice.setVisibility(View.VISIBLE);
                binding.imageMoveDevice.setTag(true);
                fragmentDevices.adapter.notifyItemChanged(position);
                break;


        }

        return result;
    }



    private void pasteDevice() {

        FragmentDevices fragmentDevices;
        deviceCut.setDeviceStatus(IOT_DEVICE_STATUS.INDETERMINADO);
        if (configuration.moveDevice(deviceCut.getDeviceId(), currentSite, currentRoom) == IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED) {
            fragmentDevices = identifyActiveFragment();
            fragmentDevices.setDeviceList(getCurrentDeviceList(), FragmentDevices.OPERATION_DEVICE.CUT_DEVICE, -1);
            cutPosition = binding.tabs.getSelectedTabPosition();
            createEnvironment(cutPosition);
            binding.imageMoveDevice.setTag(false);
            binding.tabs.selectTab(binding.tabs.getTabAt(cutPosition));
            Log.i(TAG, "kk");
        }
    }


    private void updateNumberDeviceOnTab() {

        int i;
        int j;
        int nDevices = 0;
        String text;
        FragmentManager fragmentManager;
        List<Fragment> listFragments;
        FragmentDevices fragmentDevices;
        fragmentManager = getSupportFragmentManager();
        listFragments = fragmentManager.getFragments();

        for (i=0; i< binding.tabs.getTabCount(); i++) {
            for (j=0;j<listFragments.size();i++) {
                fragmentDevices = (FragmentDevices) listFragments.get(j);
                nDevices = fragmentDevices.getDeviceList().size();
            }
            text = Objects.requireNonNull(Objects.requireNonNull(binding.tabs.getTabAt(i)).getText()).toString();
            text = text + "(" + nDevices + ")";
            Objects.requireNonNull(binding.tabs.getTabAt(i)).setText(text);

        }

    }

    private void errorApplication() {


    }



}