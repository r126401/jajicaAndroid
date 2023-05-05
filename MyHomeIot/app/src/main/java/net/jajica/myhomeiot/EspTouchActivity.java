package net.jajica.myhomeiot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;

import net.jajica.libiot.IOT_DEVICE_TYPE;
import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.libiot.IotDeviceUnknown;
import net.jajica.myhomeiot.databinding.ActivityEsptouchBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EspTouchActivity extends EspTouchActivityAbs {
    private static final String TAG = EspTouchActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 0x01;

    private EsptouchAsyncTask4 mTask;

    private ActivityEsptouchBinding mBinding;

    private String mSsid;
    private byte[] mSsidBytes;
    private String mBssid;
    private String idDispositivo;
    private String nombreDispositivo;
    private Boolean resultadoSmartConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityEsptouchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.confirmBtn.setOnClickListener(v -> executeEsptouch());
        resultadoSmartConfig = false;

        mBinding.cancelButton.setOnClickListener(v -> {
            showProgress(false);
            if (mTask != null) {
                mTask.cancelEsptouch();
            }
        });

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(permissions, REQUEST_PERMISSION);


        EspTouchApp.getInstance().observeBroadcast(this, broadcast -> {
            Log.d(TAG, "onCreate: Broadcast=" + broadcast);
            onWifiChanged();
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onWifiChanged();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.esptouch1_location_permission_title)
                        .setMessage(R.string.esptouch1_location_permission_message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                        .show();
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showProgress(boolean show) {
        if (show) {
            mBinding.testResult.setText(R.string.esptouch1_send_data_to_device);
            mBinding.progessExecuting.setVisibility(View.VISIBLE);
        } else {
            mBinding.progessExecuting.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected String getEspTouchVersion() {
        return getString(R.string.esptouch1_about_version, IEsptouchTask.ESPTOUCH_VERSION);
    }

    private StateResult check() {
        StateResult result = checkPermission();
        if (!result.permissionGranted) {
            return result;
        }
        result = checkLocation();
        result.permissionGranted = true;
        if (result.locationRequirement) {
            return result;
        }
        result = checkWifi();
        result.permissionGranted = true;
        result.locationRequirement = false;
        return result;
    }

    private void onWifiChanged() {
        StateResult stateResult = check();
        mSsid = stateResult.ssid;
        mSsidBytes = stateResult.ssidBytes;
        mBssid = stateResult.bssid;
        CharSequence message = stateResult.message;
        boolean confirmEnable = false;
        if (stateResult.wifiConnected) {
            confirmEnable = true;
            if (stateResult.is5G) {
                message = getString(R.string.esptouch1_wifi_5g_message);
                mBinding.confirmBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            mBinding.confirmBtn.setVisibility(View.VISIBLE);
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(EspTouchActivity.this)
                        .setMessage(R.string.esptouch1_configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        }

        mBinding.apSsidText.setText(mSsid);
        mBinding.apBssidText.setText(mBssid);
        mBinding.messageView.setText(message);
        mBinding.confirmBtn.setEnabled(confirmEnable);
    }

    private void executeEsptouch() {
        byte[] ssid = mSsidBytes == null ? ByteUtil.getBytesByString(this.mSsid)
                : mSsidBytes;
        CharSequence pwdStr = mBinding.apPasswordEdit.getText();
        byte[] password = pwdStr == null ? null : ByteUtil.getBytesByString(pwdStr.toString());
        byte[] bssid = TouchNetUtil.parseBssid2bytes(this.mBssid);
        CharSequence devCountStr = "1";
        byte[] deviceCount = devCountStr.toString().getBytes();
        byte[] broadcast = {(byte) (mBinding.packageModeGroup.getCheckedRadioButtonId() == R.id.packageBroadcast
                ? 1 : 0)};

        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this);
        mTask.execute(ssid, bssid, password, deviceCount, broadcast);
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private final WeakReference<EspTouchActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(EspTouchActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {
            cancel(true);
            EspTouchActivity activity = mActivity.get();
            if (activity != null) {
                activity.showProgress(false);
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            EspTouchActivity activity = mActivity.get();
            activity.mBinding.testResult.setText("");
            activity.showProgress(true);
        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            EspTouchActivity activity = mActivity.get();
            if (activity != null) {
                IEsptouchResult result = values[0];
                Log.i(TAG, "EspTouchResult: " + result);
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();

                activity.mBinding.testResult.append(String.format(
                        Locale.ENGLISH,
                        "%s,%s\n",
                        result.getInetAddress().getHostAddress(),
                        result.getBssid()
                ));
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            EspTouchActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            EspTouchActivity activity = mActivity.get();
            activity.mTask = null;
            activity.showProgress(false);
            if (result == null) {
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed_port)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
                activity.resultadoSmartConfig = false;
                return;
            }

            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                activity.resultadoSmartConfig = false;
                return;
            }
            // the task received some results including cancelled while
            // executing before receiving enough results

            if (!firstResult.isSuc()) {
                activity.resultadoSmartConfig = false;
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
                return;
            }

            activity.resultadoSmartConfig = true;
            activity.idDispositivo = result.get(0).getBssid();
            activity.saveNewDevice(activity.idDispositivo);
            ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());
            for (IEsptouchResult touchResult : result) {
                String message = activity.getString(R.string.esptouch1_configure_result_success_item,
                        touchResult.getBssid(), touchResult.getInetAddress().getHostAddress());
                resultMsgList.add(message);
            }
            //Contemplamos un solo dispositivo ya que solo permitimos sincronizar un dispositivo a la vez.

            CharSequence[] items = new CharSequence[resultMsgList.size()];
            mResultDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.esptouch1_configure_result_success)
                    .setItems(resultMsgList.toArray(items), null)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            mResultDialog.setCanceledOnTouchOutside(false);
        }
    }

    private void saveNewDevice(String idDevice) {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(IOT_LABELS_JSON.DEVICE_NAME.getValorTextoJson(), mBinding.apNombreDispositivo.getText().toString());
            jsonObject.put(IOT_LABELS_JSON.DEVICE_ID.getValorTextoJson(), idDevice);
            Intent intent;
            intent = new Intent();
            intent.setData(Uri.parse(jsonObject.toString()));
            setResult(RESULT_OK, intent);
            finish();
        } catch (JSONException e) {
            setResult(RESULT_CANCELED);
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
