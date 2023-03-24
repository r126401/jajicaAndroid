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
            public void onSelectedParameter(IOT_LABELS_JSON parameter, String value) {

                SettingsDialogFragment settingsDialogFragment;
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = fragmentManager.beginTransaction();
                settingsDialogFragment = new SettingsDialogFragment(parameter, value, getContext());
                settingsDialogFragment.show(fragmentTransaction, "infoDevice");

                settingsDialogFragment.setOnScanDeviceListener(new SettingsDialogFragment.OnScanDeviceListener() {
                    @Override
                    public void onScanDevice(IOT_LABELS_JSON parameter) {

                        scanDevice();
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
                                break;
                            case SENSOR_ID:
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
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(getContext(), "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    scanResult(result.getContents());
                }
            });

    private void scanResult(String contents) {

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