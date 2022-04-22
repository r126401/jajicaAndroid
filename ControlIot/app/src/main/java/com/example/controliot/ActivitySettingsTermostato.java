package com.example.controliot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.text.DecimalFormat;

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

    private RadioGroup radioGrupoSensor;
    private RadioButton radioSensorLocal;
    private RadioButton radioSensorRemoto;
    private TextView textSensorRemoto;

    private Button botonCancelar;
    private Button botonAceptar;

    private Boolean autoincremento;
    private Boolean autodecremento;
    private Handler handler;

    dispositivoIotTermostato dispositivo;




    private void registrarControles() {

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
    }

    private void recibirDatosActivity() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        double valorDouble;
        int valorInt;
        Boolean master;
        String idSensorRemoto;
        dispositivo = new dispositivoIotTermostato();

        valorDouble = (Double) bundle.get(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson());
        textMargenTemperatura.setText(String.valueOf(valorDouble));
        valorInt = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson());
        textIntervaloLectura.setText(String.valueOf(valorInt));
        valorInt = (int) bundle.get(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson());
        textReintentosLectura.setText(String.valueOf(valorInt));
        valorInt = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson());
        textIntervaloReintentos.setText(String.valueOf(valorInt));
        valorDouble = (double) bundle.get(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson());
        textCalibrado.setText(String.valueOf(valorDouble));
        master = (Boolean) bundle.get(TEXTOS_DIALOGO_IOT.TIPO_SENSOR.getValorTextoJson());
        idSensorRemoto = (String) bundle.get(TEXTOS_DIALOGO_IOT.ID_SENSOR.getValorTextoJson());
        seleccionarSensor(master, idSensorRemoto);




    }

    private void seleccionarSensor(Boolean master, String sensor) {

        if (master == true) {
            radioSensorLocal.setChecked(true);
            textSensorRemoto.setText("");
            textSensorRemoto.setVisibility(View.INVISIBLE);
        } else {
            radioSensorRemoto.setChecked(true);
            if (sensor != null) textSensorRemoto.setText(sensor);
            textSensorRemoto.setVisibility(View.VISIBLE);
        }


    }




    private void inicializarActivity() {

        handler = new Handler();

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
            case R.id.botonCancelar:
                break;
            case R.id.botonAceptar:
                break;
            case R.id.radioSensorLocal:
                seleccionarSensor(true, null);
                break;
            case R.id.radioSensorRemoto:
                seleccionarSensor(false, null);
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


        switch (v.getId()) {
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
                if ((event.getAction() == MotionEvent.ACTION_UP) || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
                autoincremento = false;
            }

                break;
            default:
                break;
        }


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
        controlTexto.setText(String.valueOf(formater.format(cantidad)));


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


}