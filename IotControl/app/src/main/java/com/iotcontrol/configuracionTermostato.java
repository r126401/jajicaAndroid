package com.iotcontrol;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.math.BigDecimal;

public class configuracionTermostato extends AppCompatActivity implements View.OnClickListener, Serializable {

    ImageView botonAceptar;
    ImageView botonCancelar;
    ImageButton botonMasMargenTemperatura;
    ImageButton botonMenosMargenTemperatura;
    ImageButton botonMasIntervaloLectura;
    ImageButton botonMenosIntervaloLectura;
    ImageButton botonMasReintentosLectura;
    ImageButton botonMenosReintentoLectura;
    ImageButton botonMasIntervaloReintentos;
    ImageButton botonMenosIntervaloReintentos;
    ImageButton botonMasCalibrado;
    ImageButton botonMenosCalibrado;
    TextView textoMargenTemperatura;
    TextView textoIntervaloLectura;
    TextView textoReintentoLectura;
    TextView textoIntervaloReintentos;
    TextView textoCalibrado;
    dispositivoIotTermostato dispositivo;
    final double INCREMENTO_MARGEN = 0.1;
    final int INCREMENTO_INTERVALO_LECTURA = 1;
    final int INCREMENTO_INTERVALO_REINTENTOS = 1;
    final int INCREMENTO_NUMERO_REINTENTOS = 1;
    final double INCREMENTO_CALIBRADO = 0.1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_termostato);

        capturarControles();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int dato;
        double datoDoble;

        if(bundle != null) {

            datoDoble = (double) bundle.get(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson());
            textoMargenTemperatura.setText(String.valueOf(datoDoble));
            dato = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson());
            textoIntervaloLectura.setText(String.valueOf(dato));
            dato = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson());
            textoIntervaloReintentos.setText(String.valueOf(dato));
            dato = (int) bundle.get(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson());
            textoReintentoLectura.setText(String.valueOf(dato));
            datoDoble = (double) bundle.get(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson());
            textoCalibrado.setText(String.valueOf(datoDoble));

            //dispositivo = (dispositivoIotTermostato) getIntent().getSerializableExtra(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.toString());

        }



    }


    protected void capturarControles() {

        botonAceptar = (ImageView) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        botonCancelar = (ImageView) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);
        botonMasMargenTemperatura = (ImageButton) findViewById(R.id.botonMasMargen);
        botonMasMargenTemperatura.setOnClickListener(this);
        botonMenosMargenTemperatura = (ImageButton) findViewById(R.id.botonMenosMargen);
        botonMenosMargenTemperatura.setOnClickListener(this);
        botonMasIntervaloLectura = (ImageButton) findViewById(R.id.botonMasIntervaloLectura);
        botonMasIntervaloLectura.setOnClickListener(this);
        botonMenosIntervaloLectura = (ImageButton) findViewById(R.id.botonMenosIntervaloLectura);
        botonMenosIntervaloLectura.setOnClickListener(this);
        botonMasReintentosLectura = (ImageButton) findViewById(R.id.botonMasReintentosLectura);
        botonMasReintentosLectura.setOnClickListener(this);
        botonMenosReintentoLectura = (ImageButton) findViewById(R.id.botonMenosReintentosLectura);
        botonMenosReintentoLectura.setOnClickListener(this);
        botonMasIntervaloReintentos = (ImageButton) findViewById(R.id.botonMasintervaloReintentos);
        botonMasIntervaloReintentos.setOnClickListener(this);
        botonMenosIntervaloReintentos = (ImageButton) findViewById(R.id.botonMenosintervaloReintentos);
        botonMenosIntervaloReintentos.setOnClickListener(this);
        botonMasCalibrado = (ImageButton) findViewById(R.id.botonMasCalibrado);
        botonMasCalibrado.setOnClickListener(this);
        botonMenosCalibrado = (ImageButton) findViewById(R.id.botonMenosCalibrado);
        botonMenosCalibrado.setOnClickListener(this);
        textoMargenTemperatura = (TextView) findViewById(R.id.textMargenTemperatura);
        textoIntervaloLectura = (TextView) findViewById(R.id.textIntervaloLectura);
        textoReintentoLectura = (TextView) findViewById(R.id.textReintentosLectura);
        textoIntervaloReintentos = (TextView) findViewById(R.id.textintervaloReintentos);
        textoCalibrado = (TextView) findViewById(R.id.textCalibrado);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.botonMasMargen:
                modificarValor(textoMargenTemperatura, true, INCREMENTO_MARGEN);
                break;
            case R.id.botonMenosMargen:
                modificarValor(textoMargenTemperatura, false, INCREMENTO_MARGEN);
                break;
            case R.id.botonMasIntervaloLectura:
                modificarValor(textoIntervaloLectura, true, INCREMENTO_INTERVALO_LECTURA);
                break;
            case R.id.botonMenosIntervaloLectura:
                modificarValor(textoIntervaloLectura, false, INCREMENTO_INTERVALO_LECTURA);
                break;
            case R.id.botonMasintervaloReintentos:
                modificarValor(textoIntervaloReintentos, true, INCREMENTO_INTERVALO_REINTENTOS);
                break;
            case R.id.botonMenosintervaloReintentos:
                modificarValor(textoIntervaloReintentos, false, INCREMENTO_INTERVALO_REINTENTOS);
                break;
            case R.id.botonMasReintentosLectura:
                modificarValor(textoReintentoLectura, true, INCREMENTO_NUMERO_REINTENTOS);
                break;
            case R.id.botonMenosReintentosLectura:
                modificarValor(textoReintentoLectura, false, INCREMENTO_NUMERO_REINTENTOS);
                break;
            case R.id.botonMasCalibrado:
                modificarValor(textoCalibrado, true, INCREMENTO_CALIBRADO);
                break;
            case R.id.botonMenosCalibrado:
                modificarValor(textoCalibrado, false, INCREMENTO_CALIBRADO);
                break;
            case R.id.botonAceptar:
                procesarAceptar();
                finish();
                break;
            case R.id.botonCancelar:
                finish();
                break;
                default:
                    break;


        }


    }

    private void modificarValor(TextView texto, boolean incrementar, int valor) {


        int dato;
        dato = Integer.valueOf(texto.getText().toString());

        if (incrementar == true) {
            dato += valor;

        } else {
            dato -= valor;
        }

        texto.setText(String.valueOf(dato));

    }

    private void modificarValor(TextView texto, boolean incrementar, double valor) {

        double dato;
        String cadena;
        BigDecimal numero;

        dato = Double.valueOf(texto.getText().toString());

        if (incrementar == true) {
            dato += valor;

        } else {
            dato -= valor;
        }

        numero = new BigDecimal(dato);
        numero = numero.setScale(1, BigDecimal.ROUND_HALF_UP);

        texto.setText(String.valueOf(numero));

    }

    private void procesarAceptar() {
        Intent datosDevueltos = new Intent();
        datosDevueltos.putExtra(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson(), Double.valueOf(textoMargenTemperatura.getText().toString()));
        datosDevueltos.putExtra(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson(), Integer.valueOf(textoIntervaloReintentos.getText().toString()));
        datosDevueltos.putExtra(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson(), Integer.valueOf(textoIntervaloLectura.getText().toString()));
        datosDevueltos.putExtra(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson(), Integer.valueOf(textoReintentoLectura.getText().toString()));
        datosDevueltos.putExtra(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson(), Double.valueOf(textoCalibrado.getText().toString()));
        //datosDevueltos.setData(Uri.parse(textoComando));
        setResult(COMANDO_IOT.MODIFICAR_APP.getIdComando(), datosDevueltos);

    }
}
