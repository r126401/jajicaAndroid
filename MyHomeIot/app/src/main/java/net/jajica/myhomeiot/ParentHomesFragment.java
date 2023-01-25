package net.jajica.myhomeiot;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import net.jajica.libiot.IotRoomsDevices;
import net.jajica.libiot.IotSitesDevices;
import net.jajica.libiot.IotUsersDevices;
import net.jajica.myhomeiot.databinding.FragmentParentHomesBinding;
import java.util.ArrayList;
import java.util.List;


public class ParentHomesFragment extends Fragment implements ListHomesAdapter.OnRowSelectedData, View.OnClickListener {

    private final String TAG= "ParentHomesFragment";
    private FragmentParentHomesBinding mbinding;
    private ArrayList<IotSitesDevices> listSites;
    private ListHomesAdapter adapter;
    private String currentSite;
    private IotUsersDevices user;
    FragmentTransaction fragmentTransaction;

    private OnPassCurrentSite onPassCurrentSite;

    public void setOnPassCurrentSite(OnPassCurrentSite onPassCurrentSite) {
        this.onPassCurrentSite = onPassCurrentSite;
    }

    public interface OnPassCurrentSite {
        void onPassCurrentSite(String currentSite);
    }

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
        //Se capturan los controles para no tener que hacer el find
        mbinding = FragmentParentHomesBinding.inflate(inflater, container, false);
        rootView = mbinding.getRoot();
        mbinding.buttonAddHome.setOnClickListener(this);
        mbinding.buttonNewHome.setOnClickListener(this);
        //Recuperamos el site que le pasamos desde la actividad
        currentSite = requireArguments().getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson());
        initFragment(container.getContext());
        return rootView;
    }

    private void initFragment(Context context) {

        user = new IotUsersDevices(context);
        user.loadConfiguration();
        listSites = user.getSiteList();

        //Llenamos el fragment con los sites que leemos desde la configuracion
        mbinding.recyclerAdminHomes2.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ListHomesAdapter(currentSite, listSites, getActivity().getApplicationContext());
        mbinding.recyclerAdminHomes2.setAdapter(adapter);
        // Con este interface capturamos el valor del site seleccionado desde el fragment
        adapter.setOnRowSelectedData(this);


    }

    /**
     * Este metodo lo utilizamos para capturar el valor del site elegido en el interfaz
     * y lo actualizamos en la activity principal
     * @param siteName Es el site seleccionado
     * @param position es la posicion dentro del RecyclerView
     */
    @Override
    public void onRowSelectedData(String siteName, int position) {

/*
        Log.i(TAG, "data");
        Bundle bundle;
        bundle = new Bundle();
        this.currentSite = siteName;
        bundle.putString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), currentSite);

 */
        user.setCurrentSite(siteName);
        user.saveConfiguration(getActivity().getApplicationContext());
        onPassCurrentSite.onPassCurrentSite(siteName);
    }

    /**
     * En este metodo eliminamos un site desde el menu y actualizamos la configuracion y
     * el RecyclerView
     * @param siteName
     * @param position
     */
    @Override
    public void onDeleteData(String siteName, int position) {

        IOT_DEVICE_USERS_RESULT result;
        deleteSite(siteName, position);

    }

    @Override
    public void onRowEditData(String siteName, int position) {
        openSiteData(siteName);

    }


    private void openSiteData(String siteName) {
        Fragment fragment;
        Bundle bundle;
        bundle = new Bundle();
        bundle.putString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), siteName);
        fragment = new AdminHomeFragment();

        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.containerAdminHomes, AdminHomeFragment.class, bundle);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
        IotRoomsDevices room;
        String siteName;
        IOT_DEVICE_USERS_RESULT result;
        site = new IotSitesDevices();
        siteName = mbinding.editAddHome.getText().toString();
        site.setSiteName(siteName);
        room = new IotRoomsDevices();
        room.setNameRoom(getActivity().getResources().getString(R.string.default_room));
        room.setIdRoom(1);
        site.insertRoomForSite(room);

        result = user.insertSiteForUser(site);
        if (result == IOT_DEVICE_USERS_RESULT.RESULT_OK) {
            Log.i(TAG, "jj");
            user.saveConfiguration(getActivity().getApplicationContext());
            adapter.notifyItemInserted(listSites.size());
            return IOT_DEVICE_USERS_RESULT.RESULT_OK;
        }

        return IOT_DEVICE_USERS_RESULT.RESULT_NOK;

    }




    private void deleteSite(String siteName, int position) {
        IOT_DEVICE_USERS_RESULT result;
        AlertDialog.Builder builder;
        int index;

        if (user.getSiteList().size() == 1) {
            onPassCurrentSite.onPassCurrentSite(currentSite);
            return;
        }

        index = user.searchSiteOfUser(siteName);
        IotSitesDevices site = user.getSiteList().get(index);
        if (site.getRoomList() != null) {
            builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.site_no_empty);
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listSites.remove(position);
                    user.deleteSiteForUser(siteName, true);
                    user.saveConfiguration(getActivity().getApplicationContext());

                    adapter.notifyItemRemoved(position);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "hola");
                }
            });
            AlertDialog dialog = builder.create();

            dialog.show();
        } else {
            if (user.deleteSiteForUser(siteName, true) == IOT_DEVICE_USERS_RESULT.RESULT_OK){
                user.saveConfiguration(getActivity().getApplicationContext());
                adapter.notifyItemRemoved(position);
            }
        }



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        try {
            onPassCurrentSite = (OnPassCurrentSite) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Error al pasar datos del currentSite");
        }

    }



}


