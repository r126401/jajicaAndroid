package com.example.controliot;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;




public class ActividadPrincipal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    /**
     * identifica la conexion mqtt principal del dispositivo
     */
    private conexionMqtt cnx;
    private dialogoIot dialogo;
    private TextView textIdBroker;
    private ImageView imageViewEstadoBroker;
    private TextView textListaDispositivo;
    private ImageView imageViewImagenDecorativa;
    private SwipeRefreshLayout swipeListaDispositivos;
    private ListView listViewListaDispositivos;
    private BottomNavigationView navigationMenuPrincipal;
    private CountDownTimer temporizador;
    private ProgressBar progressEspera;
    private ScanOptions opcionesEscaneo;
    private ScanContract contract;
    private String texto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


         // Se registran todos los controles de la activity
        registrarControles();
        inicializacionParametros();
        //Se crea la conexion mqtt. A partir de aqui el programa es asincrono y gobernado por los
        //eventos que lleguen.
        cnx = new conexionMqtt(getApplicationContext());
        cnx.setOnConexionMqtt(new conexionMqtt.OnConexionMqtt() {
            @Override
            public void conexionEstablecida(boolean reconnect, String serverURI) {
                notificarBrokerActivado();

            }

            @Override
            public void conexionPerdida(Throwable cause) {
                notificarBrokerReintentoConexion();

            }

            @Override
            public void mensajeRecibido(String topic, MqttMessage message) {

            }

            @Override
            public void entregaCompletada(IMqttDeliveryToken token) {

            }

            @Override
            public void notificacionIntermediaReintento(long intervalo) {
                Log.i(getLocalClassName(), "reintentando");

            }

            @Override
            public void finTemporizacionReintento(long temporizador) {
                Log.i(getLocalClassName(), "Temporizacion de reintento terminada");

            }
        });
        cnx.conectarseAlBrokerConReintento(10000, 1000);




    }

    
    private void notificarBrokerDesactivado() {

        imageViewEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
        if (cnx != null) {
            textIdBroker.setText("Conectando a " + cnx.getBrokerId());
        }

        textIdBroker.setTextColor(Color.RED);
        progressEspera.setVisibility(View.VISIBLE);

    }
    private void notificarBrokerActivado() {
        imageViewEstadoBroker.setImageResource(R.drawable.bk_conectado);
        textIdBroker.setText(cnx.getBrokerId());
        textIdBroker.setTextColor(Color.GREEN);
        progressEspera.setVisibility(View.INVISIBLE);

    }
    private void notificarBrokerReintentoConexion() {
        imageViewEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
        textIdBroker.setTextColor(Color.MAGENTA);
        textIdBroker.setText("Reintentando conexion a " + cnx.getBrokerId());
        progressEspera.setVisibility(View.VISIBLE);

    }
    private void inicializacionParametros() {
        cnx = null;
        dialogo = new dialogoIot();
        notificarBrokerDesactivado();


    }
    private void registrarControles() {

        textIdBroker = (TextView) findViewById(R.id.textEstadoBroker);
        imageViewEstadoBroker = (ImageView) findViewById(R.id.imageViewEstadoBroker);
        textListaDispositivo = (TextView) findViewById(R.id.textListaDispositivo);
        imageViewImagenDecorativa = (ImageView) findViewById(R.id.imageViewImagenDecorativa);
        swipeListaDispositivos = (SwipeRefreshLayout) findViewById(R.id.swipeListaDispositivos);
        listViewListaDispositivos = (ListView) findViewById(R.id.listViewListaDispositivos);
        navigationMenuPrincipal = (BottomNavigationView) findViewById(R.id.navigationMenuPrincipal);
        navigationMenuPrincipal.setOnNavigationItemSelectedListener(this);
        progressEspera = (ProgressBar)  findViewById(R.id.progressEspera);

    }


    private void lanzarActivityNuevoDispositivo() {

        Intent nuevoDispositivo = new Intent(this, ActivityNuevoDispositivo.class);
        lanzadorActivityNuevoDispositivo.launch(nuevoDispositivo);


    }

    private void lanzarActivityInstalarDispositivo() {

        Intent instalarDispositivo = new Intent(ActividadPrincipal.this, EspTouchActivity.class);
        //startActivity(instalarDispositivo);
        lanzadorActivityInstalarDispositivo.launch(instalarDispositivo);
    }





    //Rutina para lanzar una activityForResult
    ActivityResultLauncher<Intent> lanzadorActivityNuevoDispositivo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String dato = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos: " + dato);
                    } else {
                        Log.w(getLocalClassName(), "Error al guardar el dispositivo");
                    }
                }
            }
    );


    ActivityResultLauncher<Intent> lanzadorActivityInstalarDispositivo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String dato = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos " + dato);

                    } else {
                        Log.w(getLocalClassName(), "Error al instalar el dispositivo");
                    }

                }
            }
    );


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case (R.id.itemConfigurarBroker):
                Log.i(getLocalClassName(), "broker");
                break;
            case (R.id.itemInstalarDispositivo):
                Log.i(getLocalClassName(), "Lanzamos la activity InstalarDispositivo");
                lanzarActivityInstalarDispositivo();
                break;
            case(R.id.itemNuevoDispositivo):
                Log.i(getLocalClassName(), "Lanzamos la activity nuevoDispositivo");
                lanzarActivityNuevoDispositivo();
                break;
        }



        return false;
    }


    private void resultadoEscaneo() {

        Log.i(getLocalClassName(), "El resultado del escaneo es: " + texto);
    }








}


