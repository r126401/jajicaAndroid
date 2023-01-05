package net.jajica.myhomeiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.myhomeiot.databinding.ActivityHomesBinding;


import java.util.ArrayList;

public class HomesActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "HomesActivity";
    ActivityHomesBinding mbinding;
    ArrayList<IotSitesDevices> listSites;
    ListHomesAdapter adapter;
    Fragment mainAdminHomeFragment;
    Fragment adminHomeFragment;
    String currentSite;

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_admin_homes);
        mbinding = ActivityHomesBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());
        Intent intent;
        intent = getIntent();
        Bundle bundle;
        bundle = intent.getExtras();
        currentSite = (String) bundle.getString(IOT_LABELS_JSON.SITE.getValorTextoJson());
        if (savedInstanceState == null) {
            launchParentHomesFragment(bundle);
             }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.buttonAddHome):
                break;
        }

    }

    private void launchParentHomesFragment(Bundle bundle) {

        mainAdminHomeFragment = new ParentHomesFragment();
        adminHomeFragment = new AdminHomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerAdminHomes, ParentHomesFragment.class, bundle);
        fragmentTransaction.commit();

    }



}