package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.jajica.myhomeiot.databinding.DialogNameBinding;

public class DialogName extends DialogFragment {

    Context context;
    AlertDialog.Builder alertDialog;
    DialogNameBinding mbinding;

    public DialogName(Context context) {
        this.context = context;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        MyHomeIotTools tools;
        AlertDialog dialog;
        tools = new MyHomeIotTools(context);
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.ic_home_admin);
        alertDialog.setTitle(getResources().getString(R.string.add_home));
        alertDialog.setMessage("Escribe el nombre de la estancia que quieres añadir");
        setCancelable(false);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mbinding = DialogNameBinding.inflate(inflater);
        alertDialog.setView(mbinding.getRoot());
        mbinding.textIn.requestFocus();
        mbinding.textIn.setHint(R.string.name_home);
        mbinding.textIn.setInputType(InputType.TYPE_CLASS_TEXT);




        alertDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = alertDialog.create();
        tools.showHideSoftKeyboard(mbinding.getRoot(), true);
        return dialog;

    }
}
