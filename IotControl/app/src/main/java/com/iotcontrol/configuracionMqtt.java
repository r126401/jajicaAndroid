package com.iotcontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;

public class configuracionMqtt extends AppCompatActivity implements View.OnClickListener, Serializable {

    EditText editBroker;
    EditText editPuerto;
    EditText editUser;
    EditText editPass;
    ImageView botonAceptar;
    ImageView botonCancelar;
    android.support.v7.widget.Toolbar toolbar;


    conexionMqtt conexion = null;
    Button botonGuardar;
    public JSONObject mqtt;
    /*static final String MQTT = "mqtt";
    static final String BROKER = "broker";
    static final String PUERTO = "puerto";
    static final String USUARIO = "usuario";
    static final String PASSWORD = "password";*/
    public String textoJson= null;
    private String ficheroMqtt ="iotOnOffMqtt.conf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_mqtt);
        boolean estado;

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarConfiguracionMqtt);
        toolbar.setTitle(getString(R.string.configurarMqtt));
        toolbar.setNavigationIcon(R.drawable.mqtt);
        editBroker = (EditText) findViewById(R.id.editBroker);
        editPuerto = (EditText) findViewById(R.id.editPuerto);
        editPuerto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int i = -1;
                String puerto;
                switch (v.getId()) {
                    case R.id.editBroker:
                        break;
                    case R.id.editPuerto:
                        if (!TextUtils.isDigitsOnly(editPuerto.getText().toString()))
                            editPuerto.setText(conexion.getPuerto());
                        break;
                    case R.id.editUsuario:
                        break;
                    case R.id.editPassword:
                        break;
                }
                puerto = editPuerto.getText().toString();



            }
        });
        editUser = (EditText) findViewById(R.id.editUsuario);
        editPass = (EditText) findViewById(R.id.editPassword);
        botonAceptar = (ImageView) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        botonCancelar = (ImageView) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);



        conexion = new conexionMqtt(getApplicationContext());
        estado = conexion.leerConfiguracion();
        editBroker.setText(conexion.getBrokerId());
        editPuerto.setText(conexion.getPuerto());
        editUser.setText(conexion.getUsuario());
        editPass.setText(conexion.getPassword());
    }



    private boolean errorEnControles() {


        boolean error;
        boolean errorBroker;
        boolean errorPuerto;

        if ((editBroker.getText().length() == 0) ||
                (editBroker.getText().length()> 32)){
            errorBroker = true;
        } else {
            errorBroker = false;
        }


        if (editPuerto.length() == 0) {
            errorPuerto = true;
        } else {
            int puerto = Integer.valueOf(editPuerto.getText().toString());
            if ((puerto <= 0) || (puerto >= 65535)) {
                errorPuerto = true;
            } else {
                errorPuerto = false;
            }
        }
        if (editBroker.getText().toString().equals(conexion.getBrokerId()) &&
                (editPuerto.getText().toString().equals(conexion.getPuerto())) &&
                (editPass.getText().toString().equals(conexion.getPassword())) &&
                (editUser.getText().toString().equals(conexion.getUsuario()))) {
            Log.i(getLocalClassName(), "No ha cambiado nada");
            error = true;

        } else {
            error = false;
        }


        if ((error) || (errorBroker) || (errorPuerto)) {
            AlertDialog.Builder ventana;
            ventana = new AlertDialog.Builder(this);
            ventana.setTitle("Consistencia de parametros");
            ventana.setIcon(R.drawable.warning);
            ventana.setMessage("Error en parametros o no has hecho ningun cambio");
            ventana.show();
            return true;

        } else return false;


    }



    private boolean guardarConfiguracionMqtt() {



        if (errorEnControles()) {
            return false;
        } else {

            setResult(0);
            mqtt = new JSONObject();
            JSONObject mqttConf = new JSONObject();

            conexion.setBrokerId(editBroker.getText().toString());
            conexion.setPuerto(editPuerto.getText().toString());
            conexion.setUsuario(editUser.getText().toString());
            conexion.setPassword(editPass.getText().toString());
            conexion.escribirConfiguracion();
            //datosDevueltos.setData(Uri.parse("CAMBIADO"));
            setResult(1);
            return true;

        }




    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.botonAceptar):
                if (guardarConfiguracionMqtt()) {
                    setResult(1);
                    finish();
                }
                break;
            case (R.id.botonCancelar):
                setResult(0);
                finish();

            default:
                break;

        }



    }
}
