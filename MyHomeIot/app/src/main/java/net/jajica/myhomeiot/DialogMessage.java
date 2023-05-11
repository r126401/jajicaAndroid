package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import net.jajica.myhomeiot.databinding.DialogNameBinding;

public class DialogMessage extends DialogFragment {

    AlertDialog.Builder builder;
    public AlertDialog alertDialog;

    private Boolean showEditText;

    private int icon;
    private int title;
    private int message;

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public void setShowEditText(Boolean showEditText) {
        this.showEditText = showEditText;
    }

    public int getIcon() {
        return icon;
    }

    public int getTitle() {
        return title;
    }

    public int getMessage() {
        return message;
    }

    DialogMessage() {
        setShowEditText(true);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        MyHomeIotTools tools;
        AlertDialog dialog;
        DialogNameBinding mbinding;
        builder = new AlertDialog.Builder(getActivity());
        tools = new MyHomeIotTools(getActivity());
        setCancelable(false);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mbinding = DialogNameBinding.inflate(inflater);
        builder.setView(mbinding.getRoot());
        if (showEditText) {
            mbinding.textIn.requestFocus();
            mbinding.textIn.setHint(R.string.name_home);
            mbinding.textIn.setInputType(InputType.TYPE_CLASS_TEXT);
            tools.showHideSoftKeyboard(mbinding.getRoot(), true);
        } else {
            mbinding.textIn.setVisibility(View.INVISIBLE);
        }
        builder.setIcon(getIcon());
        builder.setTitle(getTitle());
        builder.setMessage(getMessage());
        dialog = builder.create();


        return dialog;

    }



    public void setParameterDialog(int icon, int title, int message) {

        setIcon(icon);
        setTitle(title);
        setMessage(message);

    }


}
