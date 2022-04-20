package com.example.controliot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ActivitySettingsTermostato extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

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

    private Button botonCancelar;
    private Button botonAceptar;




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
        radioSensorLocal = (RadioButton) findViewById(R.id.radioSensorLocal);
        radioSensorRemoto = (RadioButton) findViewById(R.id.radioSensorRemoto);

        botonCancelar = (Button) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);
        botonAceptar = (Button)  findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_termostato);
        registrarControles();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}