package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class UpgradeFragment extends DialogFragment {

    AlertDialog.Builder alertDialog;
    Context context;

    public UpgradeFragment(Context context) {
        this.context = context;
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        alertDialog.setView(inflater.inflate(R.layout.fragment_upgrade, null));
        return alertDialog.create();
    }
}