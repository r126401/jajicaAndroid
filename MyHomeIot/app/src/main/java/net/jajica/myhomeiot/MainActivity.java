package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import net.jajica.libiot.ConexionMqtt;
import net.jajica.libiot.ConjuntoDispositivosIot;
import net.jajica.libiot.DispositivoIot;
import net.jajica.libiot.ESTADO_CONEXION_MQTT;
import net.jajica.libiot.ESTADO_DISPOSITIVO;
import net.jajica.libiot.OPERACION_CONFIGURACION_DISPOSITIVOS;
import net.jajica.libiot.TIPO_DISPOSITIVO_IOT;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConexionMqtt cnx;
        cnx = new ConexionMqtt(getApplicationContext());
        ESTADO_CONEXION_MQTT estado;
        estado = cnx.connect();
        Log.i("hh", "hola");



    }




}