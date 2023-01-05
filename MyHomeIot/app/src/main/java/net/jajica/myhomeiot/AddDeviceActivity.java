package net.jajica.myhomeiot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.myhomeiot.databinding.ActivityAddDeviceBinding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Example of format scan to insert a new device
 * {"idDevice":"4417931CF7EB","nombreDispositivo":"Depuradora"}
 */

public class AddDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityAddDeviceBinding mbinding;
    private final String TAG = "AddDeviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = ActivityAddDeviceBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());
        mbinding.buttonSaveDevice.setOnClickListener(this);
        mbinding.buttonScanDevice.setOnClickListener(this);
        //setContentView(R.layout.activity_add_device);



    }


    private Boolean validateName() {

        if (mbinding.editTextDeviceName.getText().toString().isEmpty()) {
            mbinding.imageDeviceNameError.setVisibility(View.VISIBLE);
            mbinding.editTextDeviceName.setText("");
            return false;
        }
        mbinding.imageDeviceNameError.setVisibility(View.INVISIBLE);
        return true;
    }

    private Boolean validateDeviceId() {

        if (mbinding.editTextDeviceId.getText().toString().isEmpty()) {
            mbinding.imageDeviceIdError.setVisibility(View.VISIBLE);
            mbinding.editTextDeviceId.setText("");
            return false;
        }
        mbinding.imageDeviceIdError.setVisibility(View.INVISIBLE);
        return true;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.buttonSaveDevice):
                saveNewDevice();
                break;
            case (R.id.buttonScanDevice):
                scanDevice();
                break;

        }

    }

    private void notifyErrorOperation() {
        mbinding.textResult.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_error, 0,0,0);
        mbinding.textResult.setText(R.string.incorrect_scan);

    }

    private void saveNewDevice() {

        Boolean resultName, resultId;
        JSONObject jsonObject;

        resultName = validateName();
        resultId = validateDeviceId();

        if ((!resultName) || (!resultId)) {
            setResult(RESULT_CANCELED);

        } else {
            setResult(RESULT_OK);


            try {
                jsonObject = new JSONObject();
                jsonObject.put(IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson(), mbinding.editTextDeviceName.getText().toString());
                jsonObject.put(IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson(), mbinding.editTextDeviceId.getText().toString());
                Intent intent;
                intent = new Intent();
                intent.setData(Uri.parse(jsonObject.toString()));
                finish();
           } catch (JSONException e) {
                setResult(RESULT_CANCELED);
                notifyErrorOperation();
            }


        }

    }




    private void scanDevice() {
        ScanOptions scanOptions;
        scanOptions = new ScanOptions();
        scanOptions.setOrientationLocked(false);
        scanOptions.setBarcodeImageEnabled(true);
        scanOptions.setCaptureActivity(ScanActivity.class);
        barcodeLauncher.launch(scanOptions);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(AddDeviceActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(AddDeviceActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(AddDeviceActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    scanResult(result.getContents());
                }
            });


    private boolean scanResult(String result) {

        JSONObject capture;


        try {
            capture = new JSONObject(result);
            mbinding.editTextDeviceId.setText(capture.getString(IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson()));
            mbinding.textResult.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_check, 0, 0, 0);
            mbinding.textResult.setText(R.string.correct_scan);
            mbinding.editTextDeviceName.setText(capture.getString(IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson()));



        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(AddDeviceActivity.this, "La capture no es un dispositivo valido", Toast.LENGTH_LONG).show();
            mbinding.textResult.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_error, 0,0,0);
            mbinding.textResult.setText(R.string.incorrect_scan);
            return false;
        }

        return true;

    }

}