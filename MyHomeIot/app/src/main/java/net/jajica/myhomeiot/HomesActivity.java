package net.jajica.myhomeiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.ActivityHomesBinding;


import java.io.Serializable;
import java.util.ArrayList;

public class HomesActivity extends AppCompatActivity implements Serializable, View.OnClickListener, ParentHomesFragment.OnPassCurrentSite {

    private final String TAG = "HomesActivity";
    ActivityHomesBinding mbinding;
    ArrayList<IotSitesDevices> listSites;
    ListHomesAdapter adapter;
    ParentHomesFragment mainAdminHomeFragment;
    Fragment adminHomeFragment;
    String currentSite;
    IotUsersDevices configuration;



    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = ActivityHomesBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());

        configuration = new IotUsersDevices(getApplicationContext());
        configuration.loadConfiguration();
        currentSite = configuration.getCurrentSite();

        if (savedInstanceState == null) {
            launchParentHomesFragment();
             }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.buttonAddHome):
                break;
        }

    }

    private void launchParentHomesFragment() {

        mainAdminHomeFragment = new ParentHomesFragment(configuration);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerAdminHomes, mainAdminHomeFragment, "ParentHomeFragment");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack("AdminHomeFragment");
        fragmentTransaction.commit();


        Log.i(TAG, "hh");

    }


    @Override
    public void onPassCurrentSite(String currentSite) {

        Intent intent;

        intent = new Intent();
        this.currentSite = currentSite;
        intent.setData(Uri.parse(currentSite));
        setResult(RESULT_OK, intent);
        finish();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //fragmentManager.executePendingTransactions();
        Fragment fragment = fragmentManager.findFragmentByTag("ParentHomeFragment");
        if (fragment == null) {
            finish();
        }



    }


}