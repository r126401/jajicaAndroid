package net.jajica.myhomeiot;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotDeviceSwitch;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotDeviceUnknown;
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

import net.jajica.myhomeiot.databinding.ActivityMainBinding;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, Serializable, View.OnClickListener {

    private final String TAG = "MainActivity";
    private IotUsersDevices configuration;
    private IotMqttConnection cnx;
    private APPLICATION_STATUS appStatus;
    private ActivityMainBinding mbinding;
    private ViewPagerAdapter viewPagerAdapter;
    






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        IOT_OPERATION_CONFIGURATION_DEVICES res;
        super.onCreate(savedInstanceState);
        
        if (initApplication() == APPLICATION_STATUS.APPLICATION_OK) {
            connectAllDevices();
        }
        
    }

    private void connectAllDevices() {
        
        int i;
        connectUnknownDevices();
        connectSwitchDevices();
        connectThermometerDevices();
        connectThermostatDevices();
        
        
        
    }

    private void collectDeviceList() {

        ArrayList<IotDevice> deviceList;

        //deviceList = configuration.getAllDevices();
        deviceList = configuration.getDeviceListFotDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);

        Log.i(TAG, "jjj");

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
        // capturamos los controles
        mbinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());
        setToolbar(getResources().getResourceName(R.string.app_name), android.R.drawable.ic_delete);
        mbinding.bottomNavigationMenu.setOnItemSelectedListener(this);
        mbinding.textHome.setOnClickListener(this);

        if (mbinding.navView!= null) {
            prepararDrawer(mbinding.navView);
            // Seleccionar item por defecto
            //seleccionarItem(mbinding.navView.getMenu().getItem(0));
        }


        result = loadConfiguration();

        switch (result) {

            case OK_CONFIGURATION:
                if (configuration.getSiteList() == null) {
                    createDefaultSiteRoomConfiguration();
                }
                appStatus = APPLICATION_STATUS.APPLICATION_OK;
                break;
            case CORRUPTED_CONFIGURATION:
            case NO_CONFIGURATION:
                appStatus = APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
                createDefaultSiteRoomConfiguration();
                break;

        }

        makeConnect();
        //createStructure();
        mbinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //collectDeviceList();
        return APPLICATION_STATUS.APPLICATION_OK;
    }

    private void createDefaultSiteRoomConfiguration() {

        IotSitesDevices site;
        IotRoomsDevices room;
        ArrayList<IotSitesDevices> listSites;
        ArrayList<IotRoomsDevices> listRooms;

        site = new IotSitesDevices();
        site.setSiteName(getResources().getString(R.string.default_home));
        room = new IotRoomsDevices();
        room.setIdRoom(1);
        room.setNameRoom(getResources().getString(R.string.default_room));
        site.insertRoomForSite(room);
        configuration.insertSiteForUser(site);
        configuration.object2json();
        configuration.saveConfiguration(getApplicationContext());
        Log.i(TAG, "hola");



    }




    private IOT_OPERATION_CONFIGURATION_DEVICES loadConfiguration() {

        IOT_OPERATION_CONFIGURATION_DEVICES result;
        configuration = new IotUsersDevices(getApplicationContext());
        return configuration.loadConfiguration();

    }

    private void notifConnectOk() {

        mbinding.toolbar.getMenu().getItem(0).setTitle(cnx.getIotMqttConfiguration().getBrokerId());
        mbinding.toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_connect_ok);


    }

    private void notifConnectNok() {
        mbinding.toolbar.getMenu().getItem(0).setTitle(cnx.getIotMqttConfiguration().getBrokerId());
        mbinding.toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_connect_nok);

    }

    private IOT_MQTT_STATUS_CONNECTION makeConnect() {

        IOT_MQTT_STATUS_CONNECTION state;
        cnx = new IotMqttConnection(getApplicationContext());
        state = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion estabilizada");
                createStructure();
                notifConnectOk();

            }

            @Override
            public void connectionLost(Throwable cause) {
                notifConnectNok();

            }
        });

        return state;

    }

    private APPLICATION_STATUS createStructure() {

        int i;
        int indexSite;
        ArrayList<IotRoomsDevices> rooms = null;
        ArrayList<IotDevice> devices;


        if (configuration.getSiteList() != null) {
            indexSite = configuration.searchSiteOfUser(configuration.getCurrentSite());
            rooms = configuration.getSiteList().get(indexSite).getRoomList();
            mbinding.textHome.setText(configuration.getSiteList().get(indexSite).getSiteName());
        } else {
            mbinding.textHome.setText("Ninguna casa");
        }

        if (rooms == null)  {

            return APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
        }
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        int j;
        for (i=0;i < rooms.size();i++) {
            devices = rooms.get(i).getDeviceList();
            if (devices != null) {
                for (j = 0; j < devices.size(); j++) {
                    devices.get(j).setCnx(cnx);
                }
            }

            ArrayList<IotDevice> finalDevices = devices;
            viewPagerAdapter.addFragment(new FragmentDevices(finalDevices, getApplicationContext(), configuration.getCurrentSite(), rooms.get(i).getNameRoom() ));

        }
        mbinding.pager.setAdapter(viewPagerAdapter);
        ArrayList<IotRoomsDevices> finalRooms = rooms;
        new TabLayoutMediator(mbinding.tabs, mbinding.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                int i;
                for(i=0; i< finalRooms.size(); i++) {
                    if (i==position) {
                        tab.setText(finalRooms.get(i).getNameRoom());
                    }

                }

            }
        }).attach();





        return APPLICATION_STATUS.APPLICATION_OK;


    }


    private void prepararDrawer(@NonNull NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem);
                        mbinding.drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }


    private void seleccionarItem(@NonNull MenuItem itemDrawer) {
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (itemDrawer.getItemId()) {
            case (R.id.item_user_profile):
                Log.i(TAG, "user_profile");
                break;
            case R.id.item_connection:
                Log.i(TAG, "connection");
                break;
            case R.id.item_home_admin:
                Log.i(TAG, "home_admin");
                launchHomesActivity();
                break;
            case R.id.item_notifications:
                Log.i(TAG, "notifications");
                break;
        }
        if (fragmentoGenerico != null) {
            /*
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedor_principal, fragmentoGenerico)
                    .commit();

             */
        }


        // Setear título actual
        setTitle(itemDrawer.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mbinding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Establece la toolbar como action bar
     */
    private void setToolbar(String server, int statusServer) {
        setSupportActionBar(mbinding.toolbar);
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
        getMenuInflater().inflate(R.menu.menu_main_toolbar, mbinding.toolbar.getMenu());

        return super.onCreateOptionsMenu(menu);
    }

    private void lanzarActivityInstalarDispositivo() {

        Intent instalarDispositivo = new Intent(MainActivity.this, EspTouchActivity.class);
        //startActivity(instalarDispositivo);
        lanzadorActivityInstalarDispositivo.launch(instalarDispositivo);
    }




    ActivityResultLauncher<Intent>lanzadorActivityInstalarDispositivo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String dato = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos " + dato);

                    } else {
                        Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                    }

                }
            }
    );


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

    private void createNewHome(IotSitesDevices site) {



    }




    ActivityResultLauncher<Intent> lanzadorActivitySettings = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String data = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos " + data);
                        insertSettingsIntoConfiguration(data);


                    } else {
                        Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                    }

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

        }
    }

    ActivityResultLauncher<Intent> launcherActivityNewDevice = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String data = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos " + data);
                        String deviceId;
                        deviceId = insertDeviceIntoConfiguration(data);
                        if (deviceId != null) {
                            notifyNewDevice(deviceId);
                        }

                        //localeFragment(data);

                    } else {
                        Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                    }

                }
            }
    );

    private void localeFragment(String data) {

        FragmentManager fragmentManager;
        Fragment fragment;
        List<Fragment> list;
        FragmentDevices a;
        fragmentManager = getSupportFragmentManager();
        list = fragmentManager.getFragments();
        int i;
        ArrayList<IotDevice> deviceList;
        TextInputEditText editText;
        for (i=0;i<list.size();i++) {
            a = (FragmentDevices) list.get(i);
            deviceList = a.getDeviceList();
            Log.i(TAG, "kk");


        }





    }

    private String  insertDeviceIntoConfiguration(String data) {
        String roomName;
        String siteName;
        IotSitesDevices site;
        IotRoomsDevices room;
        IotDevice device;
        IotTools tools;
        String text;
        int indexSite;
        int indexRoom;
        TabLayout.Tab tab;
        tab = mbinding.tabs.getTabAt(mbinding.tabs.getSelectedTabPosition());
        roomName = tab.getText().toString();
        siteName = mbinding.textHome.getText().toString();
        indexSite = configuration.searchSiteOfUser(siteName);
        if (indexSite < 0) {
            Log.e(TAG, "No existe el site en la configuracion");
            return null;
        }

        device = new IotDeviceUnknown();
        tools = new IotTools();
        device.setDeviceId(tools.getJsonString(data, IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson()));
        device.setDeviceName(tools.getJsonString(data, IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson()));
        device.setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        device.setCnx(cnx);
        configuration.insertIotDevice(device, siteName, roomName);
        configuration.saveConfiguration(getApplicationContext());
        return device.getDeviceId();

        //configuration.reloadConfiguration();
        //createStructure();


    }

    private void launchActivityNewDevice() {

        Intent launcherActivity = new Intent(MainActivity.this, AddDeviceActivity.class);
        launcherActivityNewDevice.launch(launcherActivity);
    }


    ActivityResultLauncher<Intent> launcherHomesActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    String data = result.getData().getDataString();
                    if (!configuration.getCurrentSite().equals(data)) {
                        configuration.setCurrentSite(data);
                        mbinding.textHome.setText(data);
                        createStructure();
                    }

                }
            }
    );

    private void launchHomesActivity() {
        Intent launcherActivity = new Intent(MainActivity.this, HomesActivity.class);
        if (configuration.getJsonObject() != null) {
            launcherActivity.putExtra(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), mbinding.textHome.getText().toString());
        } else {
            launcherActivity.putExtra(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), "null");
        }

        launcherHomesActivity.launch(launcherActivity);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.textHome):
                launchHomesActivity();
                break;
        }
    }

    private void notifyNewDevice(String deviceId) {

        FragmentManager fragmentManager;
        Fragment fragment;
        int i, j;
        FragmentDevices fragmentDevices;
        List<Fragment> listFragments;
        ArrayList<IotDevice> devices;
        IotDevice device;
        int index = mbinding.tabs.getSelectedTabPosition();
        mbinding.tabs.getTabAt(index);
        String dat = mbinding.tabs.getTabAt(index).getText().toString();
        device = configuration.getIotDeviceObject(configuration.getCurrentSite(), dat, deviceId);
        fragmentManager = getSupportFragmentManager();
        listFragments = fragmentManager.getFragments();
        fragmentDevices = (FragmentDevices) listFragments.get(index);
        //fragmentDevices = (FragmentDevices) fragmentManager.findFragmentById(index);
        fragmentDevices.addNewDevice(device);
        if (fragmentDevices.getDeviceList() == null) {
            fragmentDevices.connectUnknownDevice((IotDeviceUnknown) device, 0);
            device.commandGetStatusDevice();
        } else {
            for (j = 0; j < listFragments.size(); j++) {
                fragmentDevices = (FragmentDevices) listFragments.get(j);
                devices = fragmentDevices.getDeviceList();
                if (devices != null) {
                    for (i = 0; i < devices.size(); i++) {
                        if ((device = devices.get(i)).getDeviceId().equals(deviceId)) {
                            fragmentDevices.connectUnknownDevice((IotDeviceUnknown) device, i);
                            device.commandGetStatusDevice();
                        }
                    }
                }

            }
        }
    }


}