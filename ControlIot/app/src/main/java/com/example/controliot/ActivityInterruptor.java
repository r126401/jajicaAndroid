package com.example.controliot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ActivityInterruptor extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView imageBotonOnOff;
    private ImageView imageEstadoBroker;
    private SwipeRefreshLayout swipeSchedule;
    private ListView listViewSchedule;
    private BottomNavigationView bottommenuInterruptor;
    private String idDispositivo;
    private conexionMqtt cnx;
    private final String TAG = "ActivityInterruptor";
    private Context contexto;
    private dialogoIot dialogo;
    private CountDownTimer temporizador;

    dispositivoIotOnOff dispositivo;




    private void registrarControles() {

        imageBotonOnOff = (ImageView) findViewById(R.id.imageBotonOnOff);
        imageEstadoBroker = (ImageView) findViewById(R.id.imageEstadoBroker);
        swipeSchedule = (SwipeRefreshLayout) findViewById(R.id.swipeSchedule);
        listViewSchedule = (ListView) findViewById(R.id.listViewSchedule);
        bottommenuInterruptor = (BottomNavigationView) findViewById(R.id.bottommenuInterruptor);
        bottommenuInterruptor.setOnNavigationItemSelectedListener(this);
    }


    private void notificarBrokerConectado() {

        imageEstadoBroker.setImageResource(R.drawable.bk_conectado);
        cnx.subscribirTopic(dispositivo.getTopicSubscripcion());
        dialogo.enviarComando(dispositivo,dialogo.comandoEstadoDispositivo());


    }

    private void notificarBrokerDesconectado() {
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);

    }

    private void notificarBrokerReintentoConexion() {
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
    }



    private void inicializacionClase() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        dispositivoIot disp;


        contexto = getApplicationContext();
        dialogo = new dialogoIot();
        if (bundle != null) {
            idDispositivo = (String) bundle.get(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        }

        configuracionDispositivos listaDispositivos;
        listaDispositivos = new configuracionDispositivos();
        listaDispositivos.leerDispositivos(contexto);
        disp =listaDispositivos.getDispositivoPorId(idDispositivo);
        dispositivo = new dispositivoIotOnOff(disp);
        cnx = new conexionMqtt(getApplicationContext());
        cnx.setOnConexionMqtt(new conexionMqtt.OnConexionMqtt() {
            @Override
            public void conexionEstablecida(boolean reconnect, String serverURI) {
                dialogo.setConexionMqtt(cnx);
                notificarBrokerConectado();

            }

            @Override
            public void conexionPerdida(Throwable cause) {
                notificarBrokerReintentoConexion();

            }

            @Override
            public void mensajeRecibido(String topic, MqttMessage message) {
                Log.i(TAG, "mensaje recibido");

            }

            @Override
            public void entregaCompletada(IMqttDeliveryToken token) {

            }

            @Override
            public void notificacionIntermediaReintento(long intervalo) {

            }

            @Override
            public void finTemporizacionReintento(long temporizador) {

            }
        });
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
        cnx.conectarseAlBrokerConReintento(10000, 1000);





    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interruptor);
        registrarControles();
        inicializacionClase();
        Log.i(TAG, "Clase inicializada");
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()) {

            case (R.id.itemInfo):
                break;
            case(R.id.itemConfiguracion):
                break;
            case(R.id.itemNuevoProgramaInterruptor):
                break;
            case(R.id.itemmasInterruptor):
                /*
                PopupMenu menumas = new PopupMenu(ActivityInterruptor.this, bottommenuInterruptor );
                MenuInflater inflater = menumas.getMenuInflater();
                inflater.inflate(R.menu.menu_mas_opciones, menumas.getMenu());
                menumas.show();
                */
                 PopupMenu menumas = new PopupMenu(ActivityInterruptor.this, bottommenuInterruptor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    menumas.setForceShowIcon(true);
                }
                menumas.inflate(R.menu.menu_mas_opciones);
                 menumas.setGravity(Gravity.RIGHT);
                 menumas.show();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
        }

        return false;
    }
}