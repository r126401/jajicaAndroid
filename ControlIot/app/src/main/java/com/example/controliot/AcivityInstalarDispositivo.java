package com.example.controliot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class AcivityInstalarDispositivo extends EspTouchActivityAbs implements View.OnClickListener {

    private Button botonAceptar;
    private TextView textSSid;
    private EditText editSsid;
    private TextView textBssid;
    private EditText editBSSid;
    private TextView textPassword;
    private EditText editPassword;
    private TextView textNombreDispositivo;
    private EditText editNombreDispositivo;
    private TextView textResultadoOperacion;

    private boolean mReceiverRegistered = false;
    private boolean mDestroyed = false;
    private static final int REQUEST_PERMISSION = 0x01;


    @Override
    protected String getEspTouchVersion() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acivity_instalar_dispositivo);
        registrarControles();




    }




    private void registrarControles() {

        botonAceptar = (Button) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        textSSid = (TextView) findViewById(R.id.textSsid);
        editSsid = (EditText) findViewById(R.id.editSsid);
        textBssid = (TextView) findViewById(R.id.textBssid);
        editBSSid = (EditText) findViewById(R.id.editBssid);
        textPassword = (TextView) findViewById(R.id.textPassword);
        editPassword = (EditText) findViewById(R.id.editPassword);
        textNombreDispositivo = (TextView) findViewById(R.id.textNombreDispositivo);
        editNombreDispositivo = (EditText) findViewById(R.id.apNombreDispositivo);
        textResultadoOperacion = (TextView) findViewById(R.id.textResultadoOperacion);




    }




    @Override
    public void onClick(View v) {

    }



}