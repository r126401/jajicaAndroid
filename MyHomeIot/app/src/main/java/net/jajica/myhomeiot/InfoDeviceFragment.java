package net.jajica.myhomeiot;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotDevice;
import net.jajica.libiot.IotDeviceThermostat;
import net.jajica.libiot.IotInfoDevice;
import net.jajica.myhomeiot.databinding.FragmentInfoDeviceBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class InfoDeviceFragment extends Fragment {

    private final String TAG = "InfoDeviceFragment";
    FragmentInfoDeviceBinding mBinding;
    private InfoDeviceAdapter adapter;

    private IotDevice device;

    private ArrayList<IotInfoDevice> listInfoDevice;


    public InfoDeviceFragment() {
        // Required empty public constructor
    }


    public InfoDeviceFragment(IotDevice device) {
        this.device = device;
        this.listInfoDevice = device.getListInfoDevice();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView;
        mBinding = FragmentInfoDeviceBinding.inflate(inflater, container, false);
        rootView = mBinding.getRoot();
        mBinding.recyclerInfoDevice.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        createListInfoDevice();


        // Inflate the layout for this fragment
        return rootView;
    }

    private void createListInfoDevice() {

        if (adapter == null) {
            adapter = new InfoDeviceAdapter(listInfoDevice, getActivity());
        } else {

        }

        mBinding.recyclerInfoDevice.setAdapter(adapter);
        InfoDeviceFragment kk = this;

        adapter.setOnSelectedParameterListener(new InfoDeviceAdapter.OnSelectedParameterListener() {
            @Override
            public void onSelectedParameter(IOT_LABELS_JSON parameter, String value, String value2) {

                SettingsDialogFragment settingsDialogFragment;
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = fragmentManager.beginTransaction();
                if (value2 == null) {
                    settingsDialogFragment = new SettingsDialogFragment(parameter, value, getContext());
                } else {
                    settingsDialogFragment = new SettingsDialogFragment(parameter, value, getContext(), value2);
                }

                settingsDialogFragment.setParameterDialog(R.drawable.ic_settings, R.string.settings, R.string.modify_parameter_message);

                settingsDialogFragment.show(fragmentTransaction, "infoDevice");

                settingsDialogFragment.setOnScanDeviceListener(new SettingsDialogFragment.OnScanDeviceListener() {
                    @Override
                    public void onScanDevice(IOT_LABELS_JSON parameter) {
                        //settingsDialogFragment.mBinding.textValueParameter.setText(settingsDialogFragment.stringParameter);
                    }
                });

                settingsDialogFragment.alertDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        switch (parameter) {
                            case DEVICE_NAME:
                                //mBinding.textValueParameter.setText(stringParameter);
                                break;
                            case DEFAULT_THRESHOLD_TEMPERATURE:
                            case MARGIN_TEMPERATURE:
                            case CALIBRATE_VALUE:
                                modifyDoubleValue(parameter, settingsDialogFragment.mBinding.textValueParameter.getText().toString());
                                break;
                            case READ_INTERVAL:
                            case RETRY_INTERVAL:
                            case READ_NUMBER_RETRY:
                                modifyIntValue(parameter, settingsDialogFragment.mBinding.textValueParameter.getText().toString());
                                break;
                            case TYPE_SENSOR:
                            case SENSOR_ID:
                                modifySensorValue(parameter,
                                        settingsDialogFragment.mBinding.textSensor.getText().toString(),
                                        settingsDialogFragment.mBinding.switchSensor.isChecked());
                                break;
                            default:
                                break;
                        }
                    }

                });

                settingsDialogFragment.alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });




            }
        });



    }

    private void modifySensorValue(IOT_LABELS_JSON parameter, String sensorId, Boolean isChecked) {

        JSONObject object = null;
        IotDeviceThermostat dev = (IotDeviceThermostat) device;
        MyHomeIotTools tool;
        tool = new MyHomeIotTools();

        if (isChecked) {
            if (dev.getMasterSensor()) {
                // el sensor era master y lo sigue siendo
                return;
            } else {
                object = new JSONObject();
                try {
                    object.put(IOT_LABELS_JSON.TYPE_SENSOR.getValorTextoJson(), true);
                    device.commandModifyAppParameter(object);
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } else {
            if (tool.checkValidMac(sensorId)) {
                if (!device.getDeviceName().equals(sensorId)) {
                    try {

                        object = new JSONObject();
                        object.put(IOT_LABELS_JSON.TYPE_SENSOR.getValorTextoJson(), false);
                        object.put(IOT_LABELS_JSON.SENSOR_ID.getValorTextoJson(), sensorId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    device.commandModifyAppParameter(object);
                }
            }
        }

    }

    private void modifyIntValue(IOT_LABELS_JSON parameter, String value) {

        JSONObject object;
        double intValue;
        object = new JSONObject();
        intValue = Integer.parseInt(value);
        try {
            object.put(parameter.getValorTextoJson(), intValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        device.commandModifyAppParameter(object);



    }

    private void modifyDoubleValue(IOT_LABELS_JSON parameter, String value) {

        JSONObject object;
        double doubleValue;
        object = new JSONObject();
        doubleValue = Double.parseDouble(value);
        try {
            object.put(parameter.getValorTextoJson(), doubleValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        device.commandModifyAppParameter(object);
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
        barcodeLauncher.launch(scanOptions);
    }




}