package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IOT_OPERATION_CONFIGURATION_DEVICES;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.FragmentAdminHomeBinding;

import java.util.ArrayList;

public class AdminHomeFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "AdminHomeFragment";
    FragmentAdminHomeBinding mbinding;
    String currentSite;
    IotUsersDevices configuration;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;



    public AdminHomeFragment(IotUsersDevices configuration, String currentSite) {
        this.configuration = configuration;
        this.currentSite = currentSite;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        mbinding = FragmentAdminHomeBinding.inflate(getLayoutInflater());
        rootView = mbinding.getRoot();
        mbinding.buttonSaveSite.setOnClickListener(this);
        mbinding.imageEditNameHome.setOnClickListener(this);
        mbinding.imageEditAdress.setOnClickListener(this);
        mbinding.imageEditNumberAddress.setOnClickListener(this);
        mbinding.imageEditCP.setOnClickListener(this);
        mbinding.imageEditCity.setOnClickListener(this);
        mbinding.imageEditProvince.setOnClickListener(this);
        mbinding.imageEditCountry.setOnClickListener(this);
        mbinding.imageEditLatitude.setOnClickListener(this);
        mbinding.imageEditLongitude.setOnClickListener(this);
        mbinding.buttonSaveSite.setOnClickListener(this);
        mbinding.imageEditRooms.setOnClickListener(this);
        mbinding.editRooms.setOnClickListener(this);
        //currentSite = configuration.getCurrentSite();
        //currentSite = requireArguments().getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson());
        paintData();



        return rootView;



        //return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }



    private void showKeyboard(int action) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(action, 0);
    }


    @Override
    public void onClick(View v) {

        Bundle bundle;
        bundle = new Bundle();
        switch (v.getId()) {
            case (R.id.imageEditNameHome):
                mbinding.editNameHome.setEnabled(true);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editNameHome.requestFocus();

                break;
            case (R.id.imageEditAdress):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(true);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editAdress.requestFocus();
                break;
            case (R.id.imageEditNumberAddress):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(true);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editNumberAddress.requestFocus();
                break;
            case (R.id.imageEditCP):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(true);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editCP.requestFocus();
                break;
            case (R.id.imageEditCity):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(true);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editCity.requestFocus();
                break;
            case (R.id.imageEditProvince):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(true);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editProvince.requestFocus();
                break;
            case (R.id.imageEditCountry):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(true);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editCountry.requestFocus();
                break;
            case (R.id.imageEditLatitude):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(true);
                mbinding.editLongitude.setEnabled(false);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editLatitude.requestFocus();
                break;
            case (R.id.imageEditLongitude):
                mbinding.editNameHome.setEnabled(false);
                mbinding.editAdress.setEnabled(false);
                mbinding.editNumberAddress.setEnabled(false);
                mbinding.editCP.setEnabled(false);
                mbinding.editCity.setEnabled(false);
                mbinding.editProvince.setEnabled(false);
                mbinding.editCountry.setEnabled(false);
                mbinding.editLatitude.setEnabled(false);
                mbinding.editLongitude.setEnabled(true);
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.editLongitude.requestFocus();
                break;
            case (R.id.buttonSaveSite):
                Fragment fragment;
                fragment = new ParentHomesFragment();

                //fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager = getParentFragmentManager();
                saveData();
                //fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.containerAdminHomes, ParentHomesFragment.class, bundle);
                bundle.putString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), currentSite);
                fragmentManager.setFragmentResult(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), bundle);
                fragmentManager.popBackStack();


                fragmentTransaction = fragmentManager.beginTransaction();
                break;
            case (R.id.editRooms):
            case(R.id.imageEditRooms):
                // Lanzar el fragment AdminRooms_Fragment
                AdminRoomsFragment adminRoomsFragment = new AdminRoomsFragment(configuration, currentSite);
                bundle.putString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), mbinding.editNameHome.getText().toString());
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerAdminHomes, adminRoomsFragment, "AdminRoomsFragment");
                //fragmentTransaction.replace(R.id.containerAdminHomes, configuration, "AdminRommsFragment");
                fragmentTransaction.addToBackStack("AdminHomeFragment");
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
                break;
        }

    }

    private IotSitesDevices extractDataSite() {
        int i;
        ArrayList<IotSitesDevices> devicesList;
        IotSitesDevices site;
        //configuration = new IotUsersDevices(getActivity().getApplicationContext());
        //configuration.loadConfiguration();
        devicesList = configuration.getSiteList();
        for (i=0;i<devicesList.size();i++) {
            if (currentSite.equals(devicesList.get(i).getSiteName())) {
                site = devicesList.get(i);
                return site;

            }
        }
        return null;

    }

    private void paintData() {
        IotSitesDevices site;
        int dataInt;
        Double dataDouble;
        if ((site = extractDataSite()) != null) {
            mbinding.editNameHome.setText(site.getSiteName());
            mbinding.editAdress.setText(site.getStreet());
            if (site.getStreetNumber() <= 0) {
                mbinding.editNumberAddress.setText("");
            } else {
                mbinding.editNumberAddress.setText(String.valueOf(site.getStreetNumber()));
            }
            if (site.getPoBox() <= 0) {
                mbinding.editCP.setText("");
            } else {
                mbinding.editCP.setText(String.valueOf(site.getPoBox()));
            }
            mbinding.editCity.setText(site.getCity());
            mbinding.editCountry.setText(site.getCountry());
            mbinding.editLatitude.setText(String.valueOf(site.getLatitude()));
            mbinding.editLongitude.setText(String.valueOf(site.getLongitude()));

        }
    }

    public void saveData() {

        ArrayList<IotSitesDevices> siteList;
        IotSitesDevices site;
        int i;
        String datString;
        int datInt;
        Double datDouble;

        siteList = configuration.getSiteList();
        if (siteList != null) {
            for (i=0;i<siteList.size();i++) {
                if (currentSite.equals(siteList.get(i).getSiteName())) {
                    site = siteList.get(i);


                    if ((datString = mbinding.editNameHome.getText().toString()) != null) {
                        site.setSiteName(mbinding.editNameHome.getText().toString());
                        currentSite = datString;
                    }
                    if ((datString = mbinding.editAdress.getText().toString()) != null) {
                        site.setStreet(datString);
                    }
                    if ((datInt = isValidfieldInt(mbinding.editNumberAddress)) >= 0) {
                        site.setStreetNumber(datInt);
                    }

                    if ((datInt = isValidfieldInt(mbinding.editCP)) >= 0) {
                        site.setPoBox(datInt);
                    }

                    if ((datString = mbinding.editCity.getText().toString()) != null) {
                        site.setCity(datString);
                    }
                    if ((datString = mbinding.editProvince.getText().toString()) != null) {
                        site.setProvince(datString);
                    }
                    if ((datString = mbinding.editCountry.getText().toString()) != null) {
                        site.setCountry(datString);
                    }

                    if ((datDouble = isValidfieldDouble(mbinding.editLatitude)) >= 0 ) {
                        site.setLatitude(datDouble);
                    }
                    if ((datDouble = isValidfieldDouble(mbinding.editLongitude)) >= 0 ) {
                        site.setLongitude(datDouble);
                    }

                }
            }

        }

        IOT_OPERATION_CONFIGURATION_DEVICES res = configuration.saveConfiguration(getActivity().getApplicationContext());


    }

    private int isValidfieldInt(TextInputEditText control) {

        String dat;


        Log.i(TAG, " El control vale : " + control.toString());
        if ((control.getText() != null) && (!control.getText().toString().equals(""))) {

            dat = control.getText().toString();
            return Integer.parseInt(dat);
        } else {
            return -1;
        }
    }


    private double isValidfieldDouble(TextInputEditText control) {

        String dat;


        if (control.getText() != null) {

            dat = control.getText().toString();
            return Double.parseDouble(dat);
        } else {
            return -1;
        }
    }

}