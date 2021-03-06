package com.iotcontrol;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.EspNetUtil;

import java.lang.ref.WeakReference;
import java.util.List;


public class instalarDispositivo extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IotOnOff";

    private static final int REQUEST_PERMISSION = 0x01;

    private TextView mApSsidTV;
    private TextView mApBssidTV;
    private EditText mApPasswordET;
    private EditText mDeviceCountET;
    private RadioGroup mPackageModeGroup;
    private TextView mMessageTV;
    private ImageView botonAceptar;
    private ImageView botonCancelar;
    private static EditText nombreDispositivo;
    private String broker;
    private String puerto;
    private String user;
    private String password;
    static final String SEPARADOR = ":";
    final String APP = "iotOnOff";
    static Context contexto;
    androidx.appcompat.widget.Toolbar toolbar;


    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private EsptouchAsyncTask4 mTask;

    private boolean mReceiverRegistered = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    //WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    WifiManager managerWifi = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                    WifiInfo wifiInfo = managerWifi.getConnectionInfo();
                    onWifiChanged(wifiInfo);
                    break;
            }
        }
    };

    private boolean mDestroyed = false;


    protected void capturarDatosActivityPrincipal() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        comandosIot comando = new comandosIot();
        boolean estadoConexion;


        if (bundle != null) {
            broker = (String) bundle.get(CONF_MQTT.BROKER.toString());
            puerto = (String) bundle.get(CONF_MQTT.PUERTO.toString());
            user = (String) bundle.get(CONF_MQTT.USUARIO.toString());
            password = (String) bundle.get(CONF_MQTT.PASSWORD.toString());
            estadoConexion = (boolean) bundle.get("CONEXION");
            if (estadoConexion == false) {
                Log.w(getLocalClassName(), "No hay conexion con el broker y no se puede instalar el disp.");
                finish();

            }



        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instalar_dispositivo);




        capturarDatosActivityPrincipal();
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarInstalarDispositivo);
        toolbar.setTitle(getString(R.string.instalarDispositivo));
        toolbar.setNavigationIcon(R.drawable.instalar_dispositivo);
        mApSsidTV = findViewById(R.id.ap_ssid_text);
        mApBssidTV = findViewById(R.id.ap_bssid_text);
        mApPasswordET = findViewById(R.id.ap_password_edit);
        mDeviceCountET = findViewById(R.id.device_count_edit);
        mDeviceCountET.setText("1");
        mPackageModeGroup = findViewById(R.id.package_mode_group);
        mMessageTV = findViewById(R.id.message);
        botonAceptar = (ImageView) findViewById(R.id.botonAceptar);
        botonAceptar.setEnabled(false);
        botonAceptar.setOnClickListener(this);
        botonCancelar = (ImageView) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);
        nombreDispositivo = (EditText) findViewById(R.id.editNombreNuevoDispositivo);



        contexto = getApplicationContext();

        TextView versionTV = findViewById(R.id.version_tv);
        versionTV.setText(IEsptouchTask.ESPTOUCH_VERSION);

        if (Build.VERSION.SDK_INT >= 28) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };

                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
            } else {
                registerBroadcastReceiver();
            }

        } else {
            registerBroadcastReceiver();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mDestroyed) {
                        registerBroadcastReceiver();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();



        mDestroyed = true;
        if (mReceiverRegistered) {
            unregisterReceiver(mReceiver);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        mReceiverRegistered = true;
    }

    private void onWifiChanged(WifiInfo info) {
        if (info == null) {
            mApSsidTV.setText("");
            mApSsidTV.setTag(null);
            mApBssidTV.setTag("");
            mMessageTV.setText("");
            botonAceptar.setEnabled(false);

            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(instalarDispositivo.this)
                        .setMessage("Wifi desconectado o cambiado")
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        } else {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            //ssid = "Salon";
            mApSsidTV.setText(ssid);
            mApSsidTV.setTag(ByteUtil.getBytesByString(ssid));
            byte[] ssidOriginalData = EspUtils.getOriginalSsidBytes(info);
            mApSsidTV.setTag(ssidOriginalData);

            String bssid = info.getBSSID();
            mApBssidTV.setText(bssid);

            botonAceptar.setEnabled(true);
            mMessageTV.setText("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int frequence = info.getFrequency();
                if (frequence > 4900 && frequence < 5900) {
                    // Connected 5G wifi. Device does not support 5G
                    mMessageTV.setText(R.string.wifi_5g_message);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.botonAceptar:
                String datosAlDispositivo;

                datosAlDispositivo = mApPasswordET.getText().toString() + SEPARADOR;
                datosAlDispositivo += broker + SEPARADOR;
                datosAlDispositivo += puerto + SEPARADOR;
                datosAlDispositivo += user + SEPARADOR;
                datosAlDispositivo += password;
                byte[] ssid = mApSsidTV.getTag() == null ? ByteUtil.getBytesByString(mApSsidTV.getText().toString())
                        : (byte[]) mApSsidTV.getTag();

                //byte[] password = ByteUtil.getBytesByString(datosAlDispositivo.toString());

                byte[] password = ByteUtil.getBytesByString(mApPasswordET.getText().toString());
                Log.w(APP, password.toString());
                byte [] bssid = EspNetUtil.parseBssid2bytes(mApBssidTV.getText().toString().toUpperCase());
                byte[] deviceCount = mDeviceCountET.getText().toString().getBytes();
                byte[] broadcast = {(byte) (mPackageModeGroup.getCheckedRadioButtonId() == R.id.package_broadcast
                        ? 1 : 0)};

                if(mTask != null) {
                    mTask.cancelEsptouch();
                }
                mTask = new EsptouchAsyncTask4(this);
                mTask.execute(ssid, bssid, password, deviceCount, broadcast);

                break;

            case R.id.botonCancelar:
                finish();
                break;


        }

    }

    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(instalarDispositivo.this, text,
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], Void, List<IEsptouchResult>> {
        private WeakReference<instalarDispositivo> mActivity;

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(instalarDispositivo activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {
            cancel(true);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
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
            Activity activity = mActivity.get();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Configurando el dispositivo. Espera un momento...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
                            Log.i(TAG, "Cancelado!");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            synchronized (mLock) {
                                if (__IEsptouchTask.DEBUG) {
                                    Log.i(TAG, "progress dialog cancel button canceled");
                                }
                                if (mEsptouchTask != null) {
                                    mEsptouchTask.interrupt();
                                }
                            }
                        }
                    });
            mProgressDialog.show();
        }


        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            instalarDispositivo activity = mActivity.get();
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
                mEsptouchTask.setEsptouchListener(activity.myListener);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            instalarDispositivo activity = mActivity.get();
            mProgressDialog.dismiss();
            mResultDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            mResultDialog.setCanceledOnTouchOutside(false);
            if (result == null) {
                mResultDialog.setMessage("Tarea cancelada. El puerto puede ser usado por otra aplicacion.");
                mResultDialog.show();
                return;
            }


            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    // anadimos el dispositivo
                    StringBuilder sb = new StringBuilder();
                    dispositivoIot dispositivo;
                    for (IEsptouchResult resultInList : result) {
                        dispositivo = new dispositivoIotDesconocido(nombreDispositivo.getText().toString(), resultInList.getBssid().toString().toUpperCase(), TIPO_DISPOSITIVO_IOT.DESCONOCIDO);
                        dispositivo.guardarDispositivo(instalarDispositivo.contexto);
                        sb.append("Con exito, bssid = ")
                                .append(resultInList.getBssid())
                                .append(", InetAddress = ")
                                .append(resultInList.getInetAddress().getHostAddress())
                                .append("\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's ")
                                .append(result.size() - count)
                                .append(" No hay datos que mostrar\n");
                    }
                    mResultDialog.setMessage(sb.toString());
                } else {
                    mResultDialog.setMessage("Fallo la configuracion del dispositivo");
                }

                mResultDialog.show();

            }

            activity.mTask = null;
        }
    }
}



