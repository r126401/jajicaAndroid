package com.example.controliot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class ActivityInterruptor extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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
    private ProgressBar barraProgreso;
    private TextView textConsolaMensajes;
    private CountDownTimer temporizador;

    dispositivoIotOnOff dispositivo;




    private void registrarControles() {

        imageBotonOnOff = (ImageView) findViewById(R.id.imageBotonOnOff);
        imageBotonOnOff.setImageResource(R.drawable.switch_indeterminado);
        imageBotonOnOff.setOnClickListener(this);
        imageEstadoBroker = (ImageView) findViewById(R.id.imageEstadoBroker);
        swipeSchedule = (SwipeRefreshLayout) findViewById(R.id.swipeSchedule);
        listViewSchedule = (ListView) findViewById(R.id.listViewSchedule);
        bottommenuInterruptor = (BottomNavigationView) findViewById(R.id.bottommenuInterruptor);
        bottommenuInterruptor.setOnNavigationItemSelectedListener(this);
        barraProgreso = (ProgressBar) findViewById(R.id.barraProgreso);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes = (TextView) findViewById(R.id.textConsolaMensajes);
    }


    private void notificarBrokerConectado() {

        pintarDispositivoDisponible();
        cnx.subscribirTopic(dispositivo.getTopicSubscripcion());
        dialogo.enviarComando(dispositivo,dialogo.comandoEstadoDispositivo());
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoConsultarProgramacion());
        barraProgreso.setVisibility(View.VISIBLE);
    }

    private void notificarBrokerDesconectado() {
        pintarDispositivoIndisponible();
    }

    private void notificarBrokerReintentoConexion() {
        pintarDispositivoIndisponible();
        barraProgreso.setVisibility(View.VISIBLE);
    }



    private void ConectarAlBrokerMqtt() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        dispositivoIot disp;


        contexto = getApplicationContext();
        dialogo = new dialogoIot();
        if (bundle != null) {
            idDispositivo = (String) bundle.get(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        }
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            public void temporizacionVencidaEnComando(String idDispositivo) {
                Log.e(TAG, "Temporizacion vencida en comando");
            }
        });



        configuracionDispositivos listaDispositivos;
        listaDispositivos = new configuracionDispositivos();
        listaDispositivos.leerDispositivos(contexto);
        disp = listaDispositivos.getDispositivoPorId(idDispositivo);
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
                notificarBrokerDesconectado();

            }
        });
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
        cnx.conectarseAlBrokerConReintento(10000, 1000);






    }

    private void subscribirDispositivo() {

        dispositivo.setTopicSubscripcion(dispositivo.getTopicSubscripcion());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interruptor);
        registrarControles();
        ConectarAlBrokerMqtt();
        subscribirDispositivo();
        procesarMensajesInterruptor();

        Log.i(TAG, "Clase inicializada");
    }



    public void actualizarInterruptor(dispositivoIotOnOff dispositivo) {

        pintarDispositivoDisponible();
        if (dispositivo.getEstadoRele() == ESTADO_RELE.ON) {
            imageBotonOnOff.setImageResource(R.drawable.switchon);
            imageBotonOnOff.setTag(Integer.valueOf(R.drawable.switchon));
        } else {
            imageBotonOnOff.setImageResource(R.drawable.switchoff);
            imageBotonOnOff.setTag(Integer.valueOf(R.drawable.switchoff));
        }


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


    private void procesarMensajesInterruptor (){

        cnx.setOnProcesarMensajesInterruptor(new conexionMqtt.OnProcesarMensajesInterruptor() {
            @Override
            public void estadoAplicacion(String topic, String mensaje, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);

            }

            @Override
            public void actuacionReleLocalInterruptor(String topic, String message, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);
            }

            @Override
            public void actuacionReleRemotoInterruptor(String topic, String message, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);

            }

            @Override
            public void consultarProgramacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo) {

                Log.i(TAG, texto);


            }

            @Override
            public void nuevoProgramacionInterruptor(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void eliminarProgramacionInterruptor(String topic, String texto, String idDispositivo, String programa) {

            }

            @Override
            public void modificarProgramacionInterruptor(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void modificarAplicacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo) {

            }

            @Override
            public void resetInterruptor(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void factoryResetInterruptor(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void upgradeFirmwareInterruptor(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

            }

            @Override
            public void recibirVersionOtaDisponibleInterruptor(String topic, String texto, String idDispositivo, OtaVersion version) {

            }

            @Override
            public void errorMensajeInterruptor(String topic, String mensaje) {

            }
        });
        cnx.setOnProcesarMensajeEspontaneoInterruptor(new conexionMqtt.OnProcesarEspontaneosInterruptor() {
            @Override
            public void arranqueAplicacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);
            }

            @Override
            public void cambioProgramaInterruptor(String topic, String texto, String idDispositivo, String idPrograma) {

            }

            @Override
            public void actuacionRelelocal(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);

            }

            @Override
            public void actuacionReleRemoto(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);

            }

            @Override
            public void upgradeFirwareFota(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

            }

            @Override
            public void espontaneoDesconocido(String topic, String texto) {

            }
        });






    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageBotonOnOff:
                pintarEsperandoComando();
                if ((int) imageBotonOnOff.getTag() == R.drawable.switchoff) {
                    dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.ON));
                } else {
                    dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                }

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }

    private void pintarDispositivoDisponible() {
        imageEstadoBroker.setImageResource(R.drawable.bk_conectado);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes.setText(dispositivo.getIdDispositivo() + " disponible");
        textConsolaMensajes.setTextColor(Color.BLUE);
        imageEstadoBroker.setImageResource(R.drawable.bk_conectado);

    }

    private void pintarEsperandoComando() {

        barraProgreso.setVisibility(View.VISIBLE);
        textConsolaMensajes.setText(R.string.esperando_respuesta);
        textConsolaMensajes.setTextColor(Color.MAGENTA);
    }

    private void pintarDispositivoIndisponible() {
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes.setTextColor(Color.RED);
        textConsolaMensajes.setText(dispositivo.getIdDispositivo() + " no disponible");
        imageBotonOnOff.setImageResource(R.drawable.switch_indeterminado);
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);

    }


}