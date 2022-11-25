package net.jajica.myhomeiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotDeviceThermometer;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceSwitch;


import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.ActivityMainBinding;

import java.util.ArrayList;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private IotUsersDevices configuration;
    private IotMqttConnection cnx;
    private APPLICATION_STATUS appStatus;
    private ActivityMainBinding mbinding;
    private ViewPagerAdapter viewPagerAdapter;



    /**
     * Establece la toolbar como action bar
     */
    private void setToolbar() {
        setSupportActionBar(mbinding.toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        mbinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());



        //testUsers();
        initApplication();

        mbinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //setToolbar();


        Log.i(TAG, "HOLA");



    }


    private APPLICATION_STATUS initApplication() {

        IOT_OPERATION_CONFIGURATION_DEVICES result;
        result = loadConfiguration();

        switch (result) {

            case OK_CONFIGURATION:
                appStatus = APPLICATION_STATUS.APPLICATION_OK;
                break;
            case CORRUPTED_CONFIGURATION:
            case NO_CONFIGURATION:
                appStatus = APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
                break;

        }

        makeConnect();
        createStructure(0);


        return APPLICATION_STATUS.APPLICATION_OK;
    }




    private IOT_OPERATION_CONFIGURATION_DEVICES loadConfiguration() {

        IOT_OPERATION_CONFIGURATION_DEVICES result;
        configuration = new IotUsersDevices(getApplicationContext());
        return configuration.loadConfiguration();

    }

    private IOT_MQTT_STATUS_CONNECTION makeConnect() {

        IOT_MQTT_STATUS_CONNECTION state;
        cnx = new IotMqttConnection(getApplicationContext());
        state = cnx.createConnection(new IotMqttConnection.OnMqttConnection() {
            @Override
            public void connectionEstablished(boolean reconnect, String serverURI) {
                Log.i(TAG, "Conexion estabilizada");

            }

            @Override
            public void connectionLost(Throwable cause) {

            }
        });

        return state;

    }

    private APPLICATION_STATUS createStructure(int indexSite) {

        int i;
        ArrayList<IotRoomsDevices> rooms;
        ArrayList<IotDevice> devices;
        mbinding.textCasa.setText(configuration.getSiteList().get(indexSite).getSiteName() + ">");
        rooms = configuration.getSiteList().get(indexSite).getRoomList();
        if (rooms == null)  {

            return APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
        }
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        for (i=0;i < rooms.size();i++) {
            devices = rooms.get(i).getDeviceList();
            ArrayList<IotDevice> finalDevices = devices;
            viewPagerAdapter.addFragment(new FragmentDevices(finalDevices, getApplicationContext(), R.layout.switch_device));
            //mbinding.tabs.getTabAt(i).setText(rooms.get(i).getNameRoom());



        }
        mbinding.pager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(mbinding.tabs, mbinding.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                int i;
                for(i=0;i<rooms.size();i++) {
                    if (i==position) {
                        tab.setText(rooms.get(i).getNameRoom());
                    }

                }

            }
        }).attach();





        return APPLICATION_STATUS.APPLICATION_OK;


    }

    private APPLICATION_STATUS paintDevicesInRoom(IotRoomsDevices room) {


        int i;
        IotDevice deviceUnknown;
        IotDevice device;
        if (room.getDeviceList() == null) {
            return APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
        }











        


        return APPLICATION_STATUS.APPLICATION_OK;
    }




    public void testUsers() {


        IotDeviceSwitch device1;
        device1 = new IotDeviceSwitch();
        device1.setDeviceId("id1");
        device1.setDeviceName("name1");
        device1.object2Json();

        IotDeviceThermometer device2;
        device2 = new IotDeviceThermometer();
        device2.setDeviceId("id2");
        device2.setDeviceName("name2");
        device2.object2Json();

        IotDeviceThermometer device3;
        device3 = new IotDeviceThermometer();
        device3.setDeviceId("id3");
        device3.setDeviceName("name3");
        device3.object2Json();

        IotDeviceThermostat device4;
        device4 = new IotDeviceThermostat();
        device4.setDeviceId("id4");
        device4.setDeviceName("name4");
        device4.object2Json();


        IotDeviceThermostat device5;
        device5 = new IotDeviceThermostat();
        device5.setDeviceId("id5");
        device5.setDeviceName("name5");
        device5.object2Json();

        IotDeviceThermometer device6;
        device6 = new IotDeviceThermometer();
        device6.setDeviceId("id6");
        device6.setDeviceName("name6");
        device6.object2Json();

        IotDeviceThermostat device7;
        device7 = new IotDeviceThermostat();
        device7.setDeviceId("id7");
        device7.setDeviceName("name7");
        device7.object2Json();

        IotDeviceSwitch device8;
        device8 = new IotDeviceSwitch();
        device8.setDeviceId("id8");
        device8.setDeviceName("name8");
        device8.object2Json();



        IotRoomsDevices room1;
        room1 = new IotRoomsDevices();
        room1.setIdRoom(1);
        room1.setNameRoom("Salon");
        room1.insertDeviceForRoom(device1);
        room1.insertDeviceForRoom(device2);
        room1.object2json();
        IotRoomsDevices room2;
        room2 = new IotRoomsDevices();
        room2.setIdRoom(2);
        room2.setNameRoom("Patio");
        room2.insertDeviceForRoom(device3);
        room2.insertDeviceForRoom(device4);
        room2.object2json();
        IotRoomsDevices room3;
        room3 = new IotRoomsDevices();
        room3.setIdRoom(3);
        room3.setNameRoom("Trastero");
        room3.insertDeviceForRoom(device5);
        room3.insertDeviceForRoom(device6);
        room3.object2json();
        IotRoomsDevices room4;
        room4 = new IotRoomsDevices();
        room4.setIdRoom(4);
        room4.setNameRoom("Dormitorio1");
        room4.insertDeviceForRoom(device7);
        room4.insertDeviceForRoom(device8);
        room4.object2json();




        IotSitesDevices site;
        site = new IotSitesDevices("Navalcarnero");
        site.setCity("Navalcarnero");
        site.setCountry("España");
        site.setStreet("Avenida De Los Cinco Siglos");
        site.setStreetNumber(46);
        site.setLongitude(45);
        site.setLatitude(43);
        site.object2json();

        IotSitesDevices site2;
        site2 = new IotSitesDevices("Alcorcon");
        site2.setCity("Alcorcon");
        site2.setCountry("España");
        site2.setStreet("Polvoranca");
        site2.setStreetNumber(40);
        site2.setLongitude(-23.00043);
        site2.setLatitude(43.4334);
        site2.object2json();


        IotUsersDevices users;
        users = new IotUsersDevices(getApplicationContext());
        users.setUser("Manolo");
        users.setDni("08038287V");
        users.setPassword("pepeillo");
        users.setMail("r126401@gmail.com");
        users.setTelephone("618097200");
        users.insertSiteForUser(site);

        //Añadimos los sites
        users.insertSiteForUser(site);
        users.insertSiteForUser(site2);

        //Añadimos las rooms
        site.insertRoomForSite(room1);
        site.insertRoomForSite(room2);
        site2.insertRoomForSite(room3);
        site2.insertRoomForSite(room4);
        users.object2json();
        users.saveConfiguration(getApplicationContext());
        Log.i("hh", "jj");


    }





}