package com.example.controliot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ActivitySettingsTermostato extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, RadioGroup.OnCheckedChangeListener {

    private ImageButton botonMenosMargen;
    private TextView textMargenTemperatura;
    private ImageButton botonMasMargen;

    private ImageButton botonMenosIntervaloLectura;
    private TextView textIntervaloLectura;
    private ImageButton botonMasIntervaloLectura;

    private ImageButton botonMenosReintentosLectura;
    private TextView textReintentosLectura;
    private ImageButton botonMasReintentosLectura;

    private ImageButton botonMenosIntervaloReintentos;
    private TextView textIntervaloReintentos;
    private ImageButton botonMasIntervaloReintentos;

    private ImageButton botonMenosCalibrado;
    private TextView textCalibrado;
    private ImageButton botonMasCalibrado;

    private ImageButton botonMenosUmbral;
    private TextView textUmbral;
    private ImageButton botonMasUmbral;



    private RadioGroup radioGrupoSensor;
    private RadioButton radioSensorLocal;
    private RadioButton radioSensorRemoto;
    private TextView textSensorRemoto;

    private Button botonCancelar;
    private Button botonAceptar;

    private Boolean autoincremento;
    private Boolean autodecremento;
    private Handler handler;

    private dispositivoIotTermostato dispositivo;
    private Boolean modificadaConfiguracion;
    private Boolean modificadoSensor;

    private Button botonEscanear;

    private double margen;
    private int intervaloLectura;
    private int reintentosLectura;
    private int intervaloReintentos;
    private double calibrado;
    private double umbral;
    Boolean master;
    String idSensorRemoto;




    private void registrarControles() {

        botonMenosUmbral = (ImageButton) findViewById(R.id.boton_menos_umbral);
        botonMenosUmbral.setOnClickListener(this);
        botonMenosUmbral.setOnLongClickListener(this);
        botonMenosUmbral.setOnTouchListener(this);
        textUmbral = (TextView) findViewById(R.id.text_umbral);
        botonMasUmbral = (ImageButton) findViewById(R.id.boton_mas_umbral);
        botonMasUmbral.setOnClickListener(this);
        botonMasUmbral.setOnLongClickListener(this);
        botonMasUmbral.setOnTouchListener(this);
        botonMenosMargen = (ImageButton) findViewById(R.id.idBotonMenosMargen);
        botonMenosMargen.setOnClickListener(this);
        botonMenosMargen.setOnLongClickListener(this);
        botonMenosMargen.setOnTouchListener(this);
        textMargenTemperatura = (TextView) findViewById(R.id.text_margen_temperatura);
        botonMasMargen = (ImageButton) findViewById(R.id.idBotonMasMargen);
        botonMasMargen.setOnClickListener(this);
        botonMasMargen.setOnLongClickListener(this);
        botonMasMargen.setOnTouchListener(this);
        botonMenosIntervaloLectura = (ImageButton) findViewById(R.id.boton_menos_intervalo_lectura);
        botonMenosIntervaloLectura.setOnClickListener(this);
        botonMenosIntervaloLectura.setOnLongClickListener(this);
        botonMenosIntervaloLectura.setOnTouchListener(this);
        textIntervaloLectura = (TextView) findViewById(R.id.text_intervalo_lectura);
        botonMasIntervaloLectura = (ImageButton)  findViewById(R.id.boton_mas_intervalo_lectura);
        botonMasIntervaloLectura.setOnClickListener(this);
        botonMasIntervaloLectura.setOnLongClickListener(this);
        botonMasIntervaloLectura.setOnTouchListener(this);

        botonMenosReintentosLectura = (ImageButton) findViewById(R.id.boton_menos_reintentos_lectura);
        botonMenosReintentosLectura.setOnClickListener(this);
        botonMenosReintentosLectura.setOnLongClickListener(this);
        botonMenosReintentosLectura.setOnTouchListener(this);

        textReintentosLectura = (TextView) findViewById(R.id.text_reintentos_lectura);
        botonMasReintentosLectura = (ImageButton) findViewById(R.id.boton_mas_reintentos_lectura);
        botonMasReintentosLectura.setOnClickListener(this);
        botonMasReintentosLectura.setOnLongClickListener(this);
        botonMasReintentosLectura.setOnTouchListener(this);

        botonMenosIntervaloReintentos = (ImageButton) findViewById(R.id.boton_menos_intervalo_reintentos);
        botonMenosIntervaloReintentos.setOnClickListener(this);
        botonMenosIntervaloReintentos.setOnLongClickListener(this);
        botonMenosIntervaloReintentos.setOnTouchListener(this);
        textIntervaloReintentos = (TextView) findViewById(R.id.text_intervalo_reintentos);
        botonMasIntervaloReintentos = (ImageButton) findViewById(R.id.boton_mas_intervalo_reintentos);
        botonMasIntervaloReintentos.setOnClickListener(this);
        botonMasIntervaloReintentos.setOnLongClickListener(this);
        botonMasIntervaloReintentos.setOnTouchListener(this);

        botonMenosCalibrado = (ImageButton) findViewById(R.id.boton_menos_calibrado);
        botonMenosCalibrado.setOnClickListener(this);
        botonMenosCalibrado.setOnLongClickListener(this);
        botonMenosCalibrado.setOnTouchListener(this);
        textCalibrado = (TextView) findViewById(R.id.text_calibrado);
        botonMasCalibrado = (ImageButton) findViewById(R.id.boton_mas_calibrado);
        botonMasCalibrado.setOnClickListener(this);
        botonMasCalibrado.setOnLongClickListener(this);
        botonMasCalibrado.setOnTouchListener(this);

        radioGrupoSensor = (RadioGroup) findViewById(R.id.radioGrupoSensor);
        radioGrupoSensor.setOnCheckedChangeListener(this);
        radioSensorLocal = (RadioButton) findViewById(R.id.radioSensorLocal);
        radioSensorLocal.setOnClickListener(this);
        radioSensorRemoto = (RadioButton) findViewById(R.id.radioSensorRemoto);
        radioSensorRemoto.setOnClickListener(this);

        textSensorRemoto = (TextView) findViewById(R.id.textSensorRemoto);

        botonCancelar = (Button) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);
        botonAceptar = (Button)  findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);

        botonEscanear = (Button) findViewById(R.id.boton_escanear);
        botonEscanear.setOnClickListener(this);

    }

    private void recibirDatosActivity() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        double valorDouble;
        int valorInt;

        dispositivo = new dispositivoIotTermostato();

        margen = (Double) bundle.get(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson());
        textMargenTemperatura.setText(String.valueOf(margen));
        intervaloLectura = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson());
        textIntervaloLectura.setText(String.valueOf(intervaloLectura));
        reintentosLectura = (int) bundle.get(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson());
        textReintentosLectura.setText(String.valueOf(reintentosLectura));
        intervaloReintentos = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson());
        textIntervaloReintentos.setText(String.valueOf(intervaloReintentos));
        calibrado = (double) bundle.get(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson());
        textCalibrado.setText(String.valueOf(calibrado));
        master = (Boolean) bundle.get(TEXTOS_DIALOGO_IOT.TIPO_SENSOR.getValorTextoJson());
        idSensorRemoto = (String) bundle.get(TEXTOS_DIALOGO_IOT.ID_SENSOR.getValorTextoJson());
        seleccionarSensor(master, idSensorRemoto);
        umbral = (Double) bundle.get(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson());
        textUmbral.setText(String.valueOf(umbral));





    }

    private void seleccionarSensor(Boolean master, String sensor) {

        if (master == true) {
            radioSensorLocal.setChecked(true);
            textSensorRemoto.setText("");
            textSensorRemoto.setVisibility(View.INVISIBLE);
            botonEscanear.setVisibility(View.INVISIBLE);
        } else {
            radioSensorRemoto.setChecked(true);
            if (sensor != null) textSensorRemoto.setText(sensor);
            textSensorRemoto.setVisibility(View.VISIBLE);
            botonEscanear.setVisibility(View.VISIBLE);
        }


    }




    private void inicializarActivity() {

        handler = new Handler();
        modificadaConfiguracion = false;
        modificadoSensor = false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_termostato);
        registrarControles();
        inicializarActivity();
        recibirDatosActivity();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.idBotonMenosMargen:
                modificarValorDouble(textMargenTemperatura, false, 0.1, -4, 10);
                break;
            case R.id.idBotonMasMargen:
                modificarValorDouble(textMargenTemperatura, true, 0.1, -4, 10);
                break;
            case R.id.boton_menos_intervalo_lectura:
                modificarValorInt(textIntervaloLectura, false, 1, 10, 120);
                break;
            case R.id.boton_mas_intervalo_lectura:
                modificarValorInt(textIntervaloLectura, true, 1, 10, 120);
                break;
            case R.id.boton_menos_reintentos_lectura:
                modificarValorInt(textReintentosLectura, false, 1, 0, 10);
                break;
            case R.id.boton_mas_reintentos_lectura:
                modificarValorInt(textReintentosLectura, true, 1, 0, 10);
                break;
            case R.id.boton_menos_intervalo_reintentos:
                modificarValorInt(textIntervaloReintentos, false, 1, 10, 120);
                break;
            case R.id.boton_mas_intervalo_reintentos:
                modificarValorInt(textIntervaloReintentos, true, 1, 10, 120);
                break;
            case R.id.boton_menos_calibrado:
                modificarValorDouble(textCalibrado, false, 0.5, -4, 10);
                break;
            case R.id.boton_mas_calibrado:
                modificarValorDouble(textCalibrado, true, 0.5, -4, 10);
                break;
            case R.id.boton_menos_umbral:
                modificarValorDouble(textUmbral, false, 0.5, 0, 30);
                break;
            case R.id.boton_mas_umbral:
                modificarValorDouble(textUmbral, true, 0.5, 0, 30);
                break;
            case R.id.botonCancelar:
                finish();
                break;
            case R.id.botonAceptar:
                procesarBotonAceptar();
                break;
            case R.id.radioSensorLocal:
                seleccionarSensor(true, null);
                break;
            case R.id.radioSensorRemoto:
                seleccionarSensor(false, "Escanea sensor");
                break;
            case R.id.boton_escanear:
                escanearSensorRemoto();
                break;
            default:
                break;



        }

    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            case R.id.idBotonMenosMargen:
                autodecremento = true;
                autoincremento = false;
                handler.post(new modificacionPulsacionLargaDoble(textMargenTemperatura, false, 1.0, -4.0, 10.0));
                break;
            case R.id.idBotonMasMargen:
                autoincremento = true;
                autodecremento = false;
                handler.post(new modificacionPulsacionLargaDoble(textMargenTemperatura, true, 1.0, -4.0, 10.0));
                break;
            case R.id.boton_menos_intervalo_lectura:
                autodecremento = true;
                autoincremento = false;
                handler.post(new modificacionPulsacionLargaInt(textIntervaloLectura, false, 5, 10 ,120));
                break;
            case R.id.boton_mas_intervalo_lectura:
                autoincremento = true;
                autodecremento = false;
                handler.post(new modificacionPulsacionLargaInt(textIntervaloLectura, true, 5, 10, 120));
                break;
            case R.id.boton_menos_reintentos_lectura:
                autodecremento = true;
                autoincremento = false;
                handler.post(new modificacionPulsacionLargaInt(textReintentosLectura, false, 2, 0, 10));


                break;
            case R.id.boton_mas_reintentos_lectura:
                autoincremento = true;
                autodecremento = false;
                handler.post(new modificacionPulsacionLargaInt(textReintentosLectura, true, 2, 0, 10));


                break;
            case R.id.boton_menos_intervalo_reintentos:
                autodecremento = true;
                autoincremento = false;
                handler.post(new modificacionPulsacionLargaInt(textIntervaloReintentos, false, 5, 10, 120));


                break;
            case R.id.boton_mas_intervalo_reintentos:
                autoincremento = true;
                autodecremento = false;
                handler.post(new modificacionPulsacionLargaInt(textIntervaloReintentos, true, 5, 10, 120));


                break;
            case R.id.boton_menos_calibrado:
                autodecremento = true;
                autoincremento = false;
                handler.post(new modificacionPulsacionLargaDoble(textCalibrado, false, 1.0, -4.0, 10.0));
                break;
            case R.id.boton_mas_calibrado:
                autoincremento = true;
                autodecremento = false;
                handler.post(new modificacionPulsacionLargaDoble(textCalibrado, true, 1.0, -4.0, 10.0));
                break;
            case R.id.boton_menos_umbral:
                autodecremento = true;
                autoincremento = false;
                handler.post(new modificacionPulsacionLargaDoble(textUmbral, false, 1.0, 0.0, 30.0));
                break;
            case R.id.boton_mas_umbral:
                autoincremento = true;
                autodecremento = false;
                handler.post(new modificacionPulsacionLargaDoble(textUmbral, true, 1.0, 0.0, 30.0));
                break;
            case R.id.botonCancelar:
                break;
            case R.id.botonAceptar:
                break;
            default:
                break;
        }



            return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if ((event.getAction() == MotionEvent.ACTION_UP) || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
            autodecremento = false;
            autoincremento = false;
        }

    /*
        switch (v.getId()) {
            case R.id.boton_menos_umbral:
            case R.id.boton_menos_calibrado:
            case R.id.boton_menos_intervalo_reintentos:
            case R.id.boton_menos_reintentos_lectura:
            case R.id.boton_menos_intervalo_lectura:
            case R.id.idBotonMenosMargen:
                if ((event.getAction() == MotionEvent.ACTION_UP) || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
                    autodecremento = false;
                }

                break;
            case R.id.boton_mas_calibrado:
            case R.id.boton_mas_intervalo_reintentos:
            case R.id.boton_mas_reintentos_lectura:
            case R.id.boton_mas_intervalo_lectura:
            case R.id.idBotonMasMargen:
            case R.id.boton_mas_umbral:
                if ((event.getAction() == MotionEvent.ACTION_UP) || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
                autoincremento = false;
            }

                break;
            default:
                break;
        }

*/
        return false;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == R.id.radioSensorLocal) {
            textSensorRemoto.setVisibility(View.INVISIBLE);

        } else {
            textSensorRemoto.setVisibility(View.VISIBLE);


        }

    }

    private void modificarValorDouble(TextView controlTexto, Boolean incremento, double valor, double minVal, double maxVal) {

        double cantidad;

        cantidad = Double.valueOf(controlTexto.getText().toString());
        if (incremento == true) {
            cantidad += valor;
            if (cantidad >= maxVal) cantidad = maxVal;
        } else {
            cantidad -= valor;
            if (cantidad <= minVal) cantidad = minVal;
        }

        DecimalFormat formater = new DecimalFormat("0.0");
        String texto = formater.format((cantidad));
        texto = texto.replace(",", ".");
        controlTexto.setText(texto);
        //controlTexto.setText(String.valueOf(formater.format(cantidad)));




    }

    private void modificarValorInt(TextView controlTexto, Boolean incremento, int valor, int minVal, int maxVal) {

        int cantidad;

        cantidad = Integer.valueOf(controlTexto.getText().toString());
        if (incremento == true) {
            cantidad += valor;
            if (cantidad >= maxVal) cantidad = maxVal;
        } else {
            cantidad -= valor;
            if (cantidad <= minVal) cantidad = minVal;
        }

        controlTexto.setText(String.valueOf(cantidad));


    }


    class modificacionPulsacionLargaDoble implements Runnable {

        TextView textControl;
        Boolean incremento;
        Double valor;
        Double minVal;
        Double maxVal;

        modificacionPulsacionLargaDoble(TextView control, Boolean incremento, Double valor, Double minVal, Double maxVal) {
            textControl = control;
            this.incremento = incremento;
            this.valor = valor;
            this.minVal = minVal;
            this.maxVal = maxVal;



        }

        public void run() {
            if (autoincremento) {
                handler.postDelayed(new modificacionPulsacionLargaDoble(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "incrementando");
                modificarValorDouble(textControl, incremento, valor, minVal, maxVal);

            } else if (autodecremento){
                handler.postDelayed(new modificacionPulsacionLargaDoble(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "decrementando");
                modificarValorDouble(textControl, incremento, valor, minVal, maxVal);



            }
            //textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));

        }
    }

    class modificacionPulsacionLargaInt implements Runnable {

        TextView textControl;
        Boolean incremento;
        int valor;
        int minVal;
        int maxVal;

        modificacionPulsacionLargaInt(TextView control, Boolean incremento, int valor, int minVal, int maxVal) {
            textControl = control;
            this.incremento = incremento;
            this.valor = valor;
            this.minVal = minVal;
            this.maxVal = maxVal;



        }

        public void run() {
            if (autoincremento) {
                handler.postDelayed(new modificacionPulsacionLargaInt(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "incrementando");
                modificarValorInt(textControl, incremento, valor, minVal, maxVal);

            } else if (autodecremento){
                handler.postDelayed(new modificacionPulsacionLargaInt(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "decrementando");
                modificarValorInt(textControl, incremento, valor, minVal, maxVal);



            }
            //textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));

        }
    }

    private void procesarBotonAceptar() {

        ArrayList<String> listaComandos;
        String comando;
        dialogoIot dialogo;
        dialogo = new dialogoIot();
        Intent datosDevueltos = new Intent();



        if ((Double.valueOf(textMargenTemperatura.getText().toString()) == margen) &&
                (Integer.valueOf(textIntervaloLectura.getText().toString()) == intervaloLectura) &&
                (Integer.valueOf(textReintentosLectura.getText().toString()) == reintentosLectura) &&
                (Integer.valueOf(textIntervaloReintentos.getText().toString()) == intervaloReintentos) &&
                (Double.valueOf(textCalibrado.getText().toString()) == calibrado)&&
                (Double.valueOf(textUmbral.getText().toString()) == umbral)) {

            modificadaConfiguracion = false;
        } else {
            modificadaConfiguracion = true;
            dispositivo.setMargenTemperatura(Double.valueOf(textMargenTemperatura.getText().toString()));
            dispositivo.setIntervaloLectura(Integer.valueOf(textIntervaloLectura.getText().toString()));
            dispositivo.setIntervaloReintentos(Integer.valueOf(textIntervaloReintentos.getText().toString()));
            dispositivo.setReintentoLectura(Integer.valueOf(textReintentosLectura.getText().toString()));
            dispositivo.setValorCalibrado(Double.valueOf(textCalibrado.getText().toString()));
            dispositivo.setUmbralTemperaturaDefecto(Double.valueOf(textUmbral.getText().toString()));
            comando = dialogo.comandoConfigurarTermostato(dispositivo);
            datosDevueltos.putExtra(COMANDO_IOT.MODIFICAR_APP.toString(), comando);

        }

        if (master == radioSensorLocal.isChecked()) {
            modificadoSensor = false;
        } else {
            modificadoSensor = true;
            if (radioSensorLocal.isChecked() == true) {
                dispositivo.setSensorMaster(true);
            } else {
                dispositivo.setSensorMaster(false);
                dispositivo.setIdSensor(textSensorRemoto.getText().toString());

            }
            comando = dialogo.comandoSeleccionarSensorTemperatura(dispositivo);
            datosDevueltos.putExtra(COMANDO_IOT.SELECCIONAR_SENSOR_TEMPERATURA.toString(), comando);

        }


        if ((modificadaConfiguracion == true) || (modificadoSensor == true)) {
            setResult(RESULT_OK, datosDevueltos);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();

    }




    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(ActivitySettingsTermostato.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(ActivitySettingsTermostato.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(ActivitySettingsTermostato.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    resultadoEscaneo(result.getContents());
                }
            });

    private boolean resultadoEscaneo(String resultado) {

        JSONObject captura;
        int tipoDispositivo;
        String idSensor;
        dialogoIot dialogo;
        dialogo = new dialogoIot();

        idSensor = dialogo.extraerDatoJsonString(resultado, TEXTOS_DIALOGO_IOT.ID_SENSOR.getValorTextoJson());
        if (idSensor != null) {
            textSensorRemoto.setTextColor(Color.GREEN);
            textSensorRemoto.setText(idSensor);
            return true;
        } else {
            textSensorRemoto.setTextColor(Color.RED);
            textSensorRemoto.setText("Error!!!!");
            return false;
        }


    }

    private void escanearSensorRemoto() {
        ScanOptions opcionesEscaneo;
        opcionesEscaneo = new ScanOptions();
        opcionesEscaneo.setOrientationLocked(false);
        opcionesEscaneo.setBarcodeImageEnabled(true);
        opcionesEscaneo.setCaptureActivity(activityEscaneo.class);
        barcodeLauncher.launch(opcionesEscaneo);
    }

}