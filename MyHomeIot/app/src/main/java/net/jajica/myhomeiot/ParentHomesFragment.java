package net.jajica.myhomeiot;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import net.jajica.libiot.IOT_DEVICE_USERS_RESULT;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.FragmentParentHomesBinding;
import java.util.ArrayList;


public class ParentHomesFragment extends Fragment implements ListHomesAdapter.OnRowSelectedData, View.OnClickListener {

    private final String TAG= "ParentHomesFragment";
    private FragmentParentHomesBinding mbinding;
    private ArrayList<IotSitesDevices> listSites;
    private ListHomesAdapter adapter;
    private String currentSite;
    private IotUsersDevices user;



    public ParentHomesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recibimos los datos del fragment_admin_home para actualizar currentSite y actualizar la vista.
        if (savedInstanceState == null) {
            getParentFragmentManager().setFragmentResultListener(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(),
                    this, new FragmentResultListener() {
                        @Override
                        public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                            currentSite = result.getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson());
                            initFragment(getActivity().getApplicationContext());
                        }
                    });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;
        mbinding = FragmentParentHomesBinding.inflate(inflater, container, false);
        rootView = mbinding.getRoot();
        mbinding.buttonAddHome.setOnClickListener(this);
        mbinding.buttonNewHome.setOnClickListener(this);
        currentSite = requireArguments().getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson());
        initFragment(container.getContext());
        return rootView;

    }

    private void initFragment(Context context) {

        user = new IotUsersDevices(context);
        user.loadConfiguration();
        listSites = user.getSiteList();

        mbinding.recyclerAdminHomes2.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ListHomesAdapter(currentSite, listSites, getActivity().getApplicationContext());
        mbinding.recyclerAdminHomes2.setAdapter(adapter);
        adapter.setOnRowSelectedData(this);


    }

    @Override
    public void onRowSelectedData(String siteName, int position) {


        Log.i(TAG, "data");
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDeleteData(String siteName, int position) {

        IOT_DEVICE_USERS_RESULT result;
        if (user.deleteSiteForUser(siteName) == IOT_DEVICE_USERS_RESULT.RESULT_OK){
            user.saveConfiguration(getActivity().getApplicationContext());
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void onRowEditData(String siteName, int position) {
        Fragment fragment;
        Bundle bundle;
        bundle = new Bundle();
        bundle.putString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), siteName);
        fragment = new AdminHomeFragment();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.containerAdminHomes, AdminHomeFragment.class, bundle);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        getChildFragmentManager().setFragmentResultListener(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String cadena = result.getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson());
                Log.i(TAG, cadena);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.buttonAddHome):
                mbinding.editAddHome.setEnabled(true);
                mbinding.editAddHome.setVisibility(View.VISIBLE);
                mbinding.editAddHome.requestFocus();
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.buttonNewHome.setVisibility(View.VISIBLE);
                break;
            case (R.id.buttonNewHome):
                addSite();

                break;
        }

    }

    private void showKeyboard(int action) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(action, 0);
    }

    private IOT_DEVICE_USERS_RESULT addSite() {
        IotSitesDevices site;
        String siteName;
        IOT_DEVICE_USERS_RESULT result;
        site = new IotSitesDevices();
        siteName = mbinding.editAddHome.getText().toString();
        site.setSiteName(siteName);
        result = user.insertSiteForUser(site);
        if (result == IOT_DEVICE_USERS_RESULT.RESULT_OK) {
            Log.i(TAG, "jj");
            user.saveConfiguration(getActivity().getApplicationContext());
            adapter.notifyItemInserted(listSites.size());
            return IOT_DEVICE_USERS_RESULT.RESULT_OK;
        }

        return IOT_DEVICE_USERS_RESULT.RESULT_NOK;

    }

    private void deleteSite() {

    }



}


