package net.jajica.myhomeiot;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import net.jajica.libiot.IotRoomsDevices;
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
    private IotUsersDevices configuration;
    FragmentTransaction fragmentTransaction;

    private OnPassCurrentSite onPassCurrentSite;


    public interface OnPassCurrentSite {
        void onPassCurrentSite(String currentSite);
    }

    public ParentHomesFragment() {
        // Required empty public constructor
    }

public ParentHomesFragment(IotUsersDevices configuration) {
        this.configuration = configuration;
}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recibimos los datos del fragment_admin_home para actualizar currentSite y actualizar la vista.
        if (savedInstanceState == null) {
            currentSite = configuration.getCurrentSite();

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
        currentSite = configuration.getCurrentSite();
        initFragment(container.getContext());
        return rootView;
    }

    private void initFragment(Context context) {

        //configuration = new IotUsersDevices(context);
        //configuration.loadConfiguration();
        listSites = configuration.getSiteList();
        //Llenamos el fragment con los sites que leemos desde la configuracion
        mbinding.buttonAddHome.setOnClickListener(this);
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

        configuration.setCurrentSite(siteName);
        configuration.saveConfiguration(getActivity().getApplicationContext());
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
        AdminHomeFragment adminHomeFragment;
        adminHomeFragment = new AdminHomeFragment(configuration, siteName);
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.containerAdminHomes, adminHomeFragment, "AdminHomeFragment");
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack("ParentHomesFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.buttonAddHome):
                DialogName window;
                window = new DialogName(this.getContext());
                window.setCancelable(false);
                window.setParameterDialog(R.drawable.ic_home_admin, R.string.add_home, R.string.add_home_text);
                window.alertDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addSite(window.getTextName());

                    }
                });
                window.alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                window.show(getParentFragmentManager(), "hola");
                /*
                mbinding.editAddHome.setEnabled(true);
                mbinding.editAddHome.setVisibility(View.VISIBLE);
                mbinding.editAddHome.requestFocus();
                showKeyboard(InputMethodManager.SHOW_FORCED);
                mbinding.buttonNewHome.setVisibility(View.VISIBLE);

                 */
                break;

        }

    }

    private void showKeyboard(int action) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(action, 0);
    }

    private IOT_DEVICE_USERS_RESULT addSite(String siteName) {
        IotSitesDevices site;
        IotRoomsDevices room;
        IOT_DEVICE_USERS_RESULT result;
        site = new IotSitesDevices();
        site.setSiteName(siteName);
        room = new IotRoomsDevices();
        room.setNameRoom(getActivity().getResources().getString(R.string.default_room));
        room.setIdRoom(1);
        site.insertRoomForSite(room);

        result = configuration.insertSiteForUser(site);
        if (result == IOT_DEVICE_USERS_RESULT.RESULT_OK) {
            Log.i(TAG, "jj");
            configuration.saveConfiguration(getActivity().getApplicationContext());
            adapter.notifyItemInserted(listSites.size());
            adapter.notifyItemChanged(0);
            return IOT_DEVICE_USERS_RESULT.RESULT_OK;
        }

        return IOT_DEVICE_USERS_RESULT.RESULT_NOK;


    }




    private void deleteSite(String siteName, int position) {
        IOT_DEVICE_USERS_RESULT result;
        AlertDialog.Builder builder;
        int index;

        //Esto en teoria nunca ocurre porque se borra el icono de deleteSite
        if (configuration.getSiteList().size() == 1) {
            onPassCurrentSite.onPassCurrentSite(currentSite);
            return;
        }

        index = configuration.searchSiteOfUser(siteName);
        IotSitesDevices site = configuration.getSiteList().get(index);
        if (site.getRoomList() != null) {
            builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.site_no_empty);
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IOT_DEVICE_USERS_RESULT a = configuration.deleteSiteForUser(siteName, true);
                    //listSites.remove(position);
                    //configuration.saveConfiguration(getActivity().getApplicationContext());
                    adapter.notifyItemRemoved(position);
                    if (listSites.size()== 1) adapter.notifyItemChanged(0);
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
            if (configuration.deleteSiteForUser(siteName, true) == IOT_DEVICE_USERS_RESULT.RESULT_OK){
                //configuration.saveConfiguration(getActivity().getApplicationContext());
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


