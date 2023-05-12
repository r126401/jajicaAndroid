package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import net.jajica.myhomeiot.databinding.DialogNameBinding;

public class DialogName extends DialogFragment {

    static Context context;
    AlertDialog.Builder alertDialog;
    DialogNameBinding mbinding;

    Boolean showEditText;



    public void setShowEditText(Boolean showEditText) {
        this.showEditText = showEditText;
    }

    public DialogName(Context context) {

        this.context = context;
        alertDialog = new AlertDialog.Builder(context);
        setShowEditText(true);
        setCancelable(false);

    }







    public void setParameterDialog(int icon, int title, int message) {

        alertDialog.setIcon(icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

    }


    public String getTextName() {

        return mbinding.textIn.getText().toString();
    }





    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        MyHomeIotTools tools;
        AlertDialog dialog;
        tools = new MyHomeIotTools(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mbinding = DialogNameBinding.inflate(inflater);
        alertDialog.setView(mbinding.getRoot());
        if (showEditText) {
            mbinding.textIn.requestFocus();
            mbinding.textIn.setHint(R.string.name_home);
            mbinding.textIn.setInputType(InputType.TYPE_CLASS_TEXT);
            tools.showHideSoftKeyboard(mbinding.getRoot(), true);
        } else {
            mbinding.textIn.setVisibility(View.INVISIBLE);
        }

        dialog = alertDialog.create();

        return dialog;

    }




}
