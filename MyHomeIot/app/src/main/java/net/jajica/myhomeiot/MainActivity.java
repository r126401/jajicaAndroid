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


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_MQTT_STATUS_CONNECTION;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotMqttConnection;
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotDevice;


import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.util.ArrayList;

//import org.eclipse.paho.client.mqttv3.IMqttToken;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, Serializable {

    private final String TAG = "MainActivity";
    private IotUsersDevices configuration;
    private IotMqttConnection cnx;
    private APPLICATION_STATUS appStatus;
    private ActivityMainBinding mbinding;
    private ViewPagerAdapter viewPagerAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        mbinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());





        setToolbar(getResources().getResourceName(R.string.app_name), android.R.drawable.ic_delete);
        initApplication();

        mbinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        //setToolbar();


        Log.i(TAG, "HOLA");



    }


    private APPLICATION_STATUS initApplication() {

        IOT_OPERATION_CONFIGURATION_DEVICES result;

        mbinding.bottomNavigationMenu.setOnItemSelectedListener(this);

        if (mbinding.navView!= null) {
            prepararDrawer(mbinding.navView);
            // Seleccionar item por defecto
            //seleccionarItem(mbinding.navView.getMenu().getItem(0));
        }


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
        createStructure(1);


        return APPLICATION_STATUS.APPLICATION_OK;
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
                notifConnectOk();

            }

            @Override
            public void connectionLost(Throwable cause) {
                notifConnectNok();

            }
        });

        return state;

    }

    private APPLICATION_STATUS createStructure(int indexSite) {

        int i;
        ArrayList<IotRoomsDevices> rooms = null;
        ArrayList<IotDevice> devices;


        if (configuration.getSiteList() != null) {
            rooms = configuration.getSiteList().get(indexSite).getRoomList();
            mbinding.textCasa.setText(configuration.getSiteList().get(indexSite).getSiteName() + ">");
        } else {
            mbinding.textCasa.setText("Ninguna casa");
        }

        if (rooms == null)  {

            return APPLICATION_STATUS.NO_DEVICES_CONFIGURATION;
        }
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        for (i=0;i < rooms.size();i++) {
            devices = rooms.get(i).getDeviceList();
            ArrayList<IotDevice> finalDevices = devices;
            viewPagerAdapter.addFragment(new FragmentDevices(finalDevices, getApplicationContext(), R.layout.switch_device));




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
            case R.id.item_user_profile:
                Log.i(TAG, "user_profile");
                //fragmentoGenerico = new FragmentoInicio();
                break;
            case R.id.item_connection:
                Log.i(TAG, "connection");
                //fragmentoGenerico = new FragmentoCuenta();
                break;
            case R.id.item_home_admin:
                Log.i(TAG, "home_admin");
                //fragmentoGenerico = new FragmentoCategorias();
                break;
            case R.id.item_notifications:
                Log.i(TAG, "notifications");
                //startActivity(new Intent(this, ActividadConfiguracion.class));
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
                        String dato = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos " + dato);

                    } else {
                        Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                    }

                }
            }
    );

    private void lanzarActivitySettings() {

        Intent lanzadorActivitysettings = new Intent(MainActivity.this, SettingsActivity.class);
        lanzadorActivitysettings.putExtra(IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson(), configuration.getJsonObject().toString());
        lanzadorActivitySettings.launch(lanzadorActivitysettings);
    }


}