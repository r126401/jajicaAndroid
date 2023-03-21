package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.print.PageRange;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.myhomeiot.databinding.FragmentUpgradeBinding;

import java.util.Set;

import net.jajica.myhomeiot.databinding.FragmentSettingsBinding;
public class SettingsFragment extends DialogFragment {


    double doubleParameter;

    int intParameter;

    boolean booleanParameter;

    String stringParameter;

    private FragmentSettingsBinding mBinding;

    private Context context;

    IOT_LABELS_JSON label;

    AlertDialog.Builder alertDialog;


    public SettingsFragment(IOT_LABELS_JSON parameter, double doubleParameter, Context context) {

        this.doubleParameter = doubleParameter;
        this.context = context;
        label = parameter;
        intParameter = -1;
        doubleParameter = -1;
        booleanParameter = false;
        stringParameter = null;


        // Required empty public constructor
    }

    public SettingsFragment(IOT_LABELS_JSON parameter,int intParameter, Context context) {
        this.intParameter = intParameter;
        this.context = context;
        label = parameter;

    }

    public SettingsFragment(IOT_LABELS_JSON parameter,boolean booleanparameter, Context context) {

        this.booleanParameter = booleanparameter;
        this.context = context;
        label = parameter;

    }

    public SettingsFragment(IOT_LABELS_JSON parameter, String stringParameter, Context context) {

        this.stringParameter = stringParameter;
        this.context = context;
        label = parameter;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mBinding = FragmentSettingsBinding.inflate(inflater);
        alertDialog.setView(mBinding.getRoot());
        mBinding.textNameParameter.setText(label.getValorTextoJson());
        switch (label) {
            case DEVICE_NAME:
                mBinding.textValueParameter.setText(stringParameter);
                break;
            case DEFAULT_THRESHOLD_TEMPERATURE:
            case MARGIN_TEMPERATURE:
            case CALIBRATE_VALUE:
                mBinding.textValueParameter.setText(String.valueOf(doubleParameter));
                break;
            case READ_INTERVAL:
            case RETRY_INTERVAL:
            case READ_NUMBER_RETRY:
                mBinding.textValueParameter.setText(String.valueOf(intParameter));
                break;
            case TYPE_SENSOR:
                break;
            case SENSOR_ID:
                break;
            default:
                break;
        }

        return alertDialog.create();

    }


}