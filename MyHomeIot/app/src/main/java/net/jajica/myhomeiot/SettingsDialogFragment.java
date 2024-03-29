package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import net.jajica.libiot.IOT_LABELS_JSON;

import net.jajica.myhomeiot.databinding.FragmentSettingsBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsDialogFragment extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private final String TAG = "SettingsFragment";

    String stringParameter;

    public FragmentSettingsBinding mBinding;

    private Context context;

    IOT_LABELS_JSON label;

    AlertDialog.Builder alertDialog;

    private int icon;
    private int title;
    private int message;

    private String value;

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public void setMessage(int message) {
        this.message = message;
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

    public String getValue() {

        return mBinding.textValueParameter.getText().toString();
    }

    private OnScanDeviceListener onScanDeviceListener;

    public interface OnScanDeviceListener {
        void onScanDevice(IOT_LABELS_JSON parameter);
    }

    public void setOnScanDeviceListener(OnScanDeviceListener onScanDeviceListener) {
        this.onScanDeviceListener = onScanDeviceListener;
    }

    public SettingsDialogFragment(IOT_LABELS_JSON parameter, String stringParameter, Context context) {

        this.stringParameter = stringParameter;
        this.context = context;
        label = parameter;
        alertDialog = new AlertDialog.Builder(context);
    }


    public SettingsDialogFragment(IOT_LABELS_JSON parameter, String stringParameter, Context context, String value) {

        this.stringParameter = stringParameter;
        this.context = context;
        label = parameter;
        alertDialog = new AlertDialog.Builder(context);
        this.value = value;
    }




    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d(TAG, "Cancelled scan");
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d(TAG, "Cancelled scan due to missing camera permission");
                        Toast.makeText(getContext(), "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Scanned");
                    Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    scanResult(result.getContents());
                }
            });


    private void scanResult(String contents) {

        JSONObject object;
        String deviceId;
        try {
            object = new JSONObject(contents);
            deviceId = object.getString(IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson());
            stringParameter = deviceId;
            mBinding.textSensor.setText(deviceId);
            if (onScanDeviceListener != null) {
                onScanDeviceListener.onScanDevice(label);
            }


        } catch (JSONException e) {

        }

        Log.i(TAG, contents);
    }

    public void scanDevice() {
        ScanOptions scanOptions;
        scanOptions = new ScanOptions();
        scanOptions.setOrientationLocked(false);
        scanOptions.setBarcodeImageEnabled(true);
        scanOptions.setCaptureActivity(ScanActivity.class);
        // barcodeLauncher.launch(scanOptions);
        //barcodeLauncher.launch(scanOptions);
        scanResult("{\"idDevice\":\"4C75250214E6\",\"nombreDispositivo\":\"Depuradora\"}");
    }


    public SettingsDialogFragment(IOT_LABELS_JSON parameter, String stringParameter) {

        this.stringParameter = stringParameter;
        this.context = null;
        label = parameter;


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mBinding = FragmentSettingsBinding.inflate(inflater);
        alertDialog.setView(mBinding.getRoot());
        mBinding.switchSensor.setOnClickListener(this);
        mBinding.switchSensor.setOnCheckedChangeListener(this);
        mBinding.buttonQrSensor.setOnClickListener(this);
        mBinding.textNameParameter.setText(label.getValorTextoJson());
        switch (label) {
            case DEVICE_NAME:
                mBinding.textValueParameter.setText(stringParameter);
                break;
            case DEFAULT_THRESHOLD_TEMPERATURE:
            case MARGIN_TEMPERATURE:
            case CALIBRATE_VALUE:
                mBinding.textValueParameter.setText(stringParameter);
                break;
            case READ_INTERVAL:
            case RETRY_INTERVAL:
            case READ_NUMBER_RETRY:
                mBinding.textValueParameter.setText(stringParameter);
                break;
            case TYPE_SENSOR:
                if (stringParameter.equals("false")) {
                    mBinding.switchSensor.setChecked(false);
                    sensorRemote();
                    Log.i(TAG, "kk");
                } else {
                    mBinding.switchSensor.setChecked(true);
                    sensorLocal();
                }
                mBinding.textSensor.setText(value);
                break;
            case SENSOR_ID:

                break;
            default:
                break;
        }


        alertDialog.setCancelable(false);
        setCancelable(false);

        alertDialog.setIcon(getIcon());
        alertDialog.setTitle(getTitle());
        alertDialog.setMessage(getMessage());

        return alertDialog.create();

    }

    private void sensorLocal() {


        mBinding.switchSensor.setVisibility(View.VISIBLE);
        mBinding.switchSensor.setText(getResources().getString(R.string.local));
        mBinding.layoutSensor.setVisibility(View.VISIBLE);
        mBinding.textValueParameter.setVisibility(View.INVISIBLE);
        mBinding.textSensor.setVisibility(View.GONE);
        mBinding.buttonQrSensor.setVisibility(View.GONE);




    }

    private void sensorRemote() {

        mBinding.switchSensor.setVisibility(View.VISIBLE);
        mBinding.switchSensor.setText(getResources().getString(R.string.remote));
        mBinding.textValueParameter.setVisibility(View.INVISIBLE);
        mBinding.layoutSensor.setVisibility(View.VISIBLE);
        mBinding.textSensor.setVisibility(View.VISIBLE);
        mBinding.buttonQrSensor.setVisibility(View.VISIBLE);
        mBinding.textValueParameter.setHint(R.string.settings);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.switchSensor):
                if (mBinding.switchSensor.isChecked()) {
                    sensorLocal();
                } else {
                    sensorRemote();
                }
                break;
            case (R.id.buttonQrSensor):
                scanDevice();
                /*
                if (onScanDeviceListener != null) {
                    onScanDeviceListener.onScanDevice(label);
                }

                 */

                break;
        }



    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            sensorRemote();
        } else {
            sensorLocal();
        }

    }

    public void setParameterDialog(int icon, int title, int message) {

        setIcon(icon);
        setTitle(title);
        setMessage(message);

    }

}