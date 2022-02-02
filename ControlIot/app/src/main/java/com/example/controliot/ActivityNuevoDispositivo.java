package com.example.controliot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class ActivityNuevoDispositivo extends AppCompatActivity implements View.OnClickListener{


    private ScanContract contract;
    private Button botonEscanear;
    private Button botonCancelar;
    private Button botonAceptar;
    private EditText editIdDispositivo;
    private EditText editNombreDispositivo;
    private TextView textIdDispositivo;
    private TextView textNombreDispositivo;
    private TextView textResultadoOperacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_dispositivo);
        registrarControles();


    }

    private void registrarControles() {

        botonEscanear = (Button) findViewById(R.id.botonEscanear);
        botonEscanear.setOnClickListener(this);
        botonCancelar = (Button) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);
        botonAceptar = (Button) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        editIdDispositivo = (EditText) findViewById(R.id.editIdDispositivo);
        editNombreDispositivo = (EditText) findViewById(R.id.apNombreDispositivo);
        textIdDispositivo = (TextView) findViewById(R.id.textIdDispositivo);
        textNombreDispositivo = (TextView) findViewById(R.id.textNombreDispositivo);
        textResultadoOperacion = (TextView) findViewById(R.id.textResultadoOperacion);
    }



    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(ActivityNuevoDispositivo.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(ActivityNuevoDispositivo.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(ActivityNuevoDispositivo.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    resultadoEscaneo(result.getContents());
                }
            });





    private void escanearDispositivo() {
        ScanOptions opcionesEscaneo;
        opcionesEscaneo = new ScanOptions();
        opcionesEscaneo.setOrientationLocked(false);
        opcionesEscaneo.setBarcodeImageEnabled(true);
        opcionesEscaneo.setCaptureActivity(activityEscaneo.class);
        barcodeLauncher.launch(opcionesEscaneo);
    }

    private boolean resultadoEscaneo(String resultado) {

        JSONObject captura;
        int tipoDispositivo;
        String dispositivo;

        try {
            captura = new JSONObject(resultado);
            editIdDispositivo.setText(captura.getString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson()));
            textResultadoOperacion.setTextColor(Color.GREEN);
            textResultadoOperacion.setText("El dispositivo se ha escaneado correctamente");
            editNombreDispositivo.setText(captura.getString(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson()));


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ActivityNuevoDispositivo.this, "La captura no es un dispositivo valido", Toast.LENGTH_LONG).show();
            textResultadoOperacion.setTextColor(Color.RED);
            textResultadoOperacion.setText("El texto escaneado no es correcto. Intentalo de nuevo");
            return false;
        }

        return true;

    }

    private void nuevoDispositivo() {
        dispositivoIotDesconocido dispositivo;

        if (!editIdDispositivo.getText().toString().isEmpty() || !editNombreDispositivo.toString().isEmpty()) {
            dispositivo = new dispositivoIotDesconocido(editNombreDispositivo.getText().toString(),
                    editIdDispositivo.getText().toString(), TIPO_DISPOSITIVO_IOT.DESCONOCIDO);
            if (dispositivo.guardarDispositivo(getApplicationContext())) {
                Intent datos = new Intent();
                datos.setData(Uri.parse(editNombreDispositivo.getText().toString()));
                setResult(RESULT_OK, datos);
            } else {
                setResult(RESULT_CANCELED);
            }

            finish();
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.botonEscanear):
                escanearDispositivo();
                break;
            case (R.id.botonCancelar):
                finish();
                break;
            case (R.id.botonAceptar):
                nuevoDispositivo();
                break;
            default:
                break;
        }

    }
}