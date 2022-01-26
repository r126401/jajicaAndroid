package com.example.controliot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class ActividadPrincipal extends AppCompatActivity {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        cnx = null;
        CountDownTimer temporizador;
        dialogo = new dialogoIot();
        // Se registran todos los controles de la activity
        registrarControles();
        //Se crea la conexion mqtt. A partir de aqui el programa es asincrono y gobernado por los
        //eventos.
        crearConexionMqtt();

        temporizador = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (cnx.getEstadoConexion()) {
                    Log.i(getLocalClassName(), "conexion completa, se cancela el temporizador");
                    this.cancel();
                }

            }

            @Override
            public void onFinish() {
                Log.i(getLocalClassName(), "termino el temporizador");
                if (!cnx.getEstadoConexion()) {
                    Log.e(getLocalClassName(), "No se ha podido establecer la conexion, se reintenta");
                    crearConexionMqtt();
                    this.start();
                }

            }
        };
        temporizador.start();




    }

    private void registrarControles() {

        textIdBroker = (TextView) findViewById(R.id.textEstadoBroker);
        imageViewEstadoBroker = (ImageView) findViewById(R.id.imageViewEstadoBroker);
        textListaDispositivo = (TextView) findViewById(R.id.textListaDispositivo);
        imageViewImagenDecorativa = (ImageView) findViewById(R.id.imageViewImagenDecorativa);
        swipeListaDispositivos = (SwipeRefreshLayout) findViewById(R.id.swipeListaDispositivos);
        listViewListaDispositivos = (ListView) findViewById(R.id.listViewListaDispositivos);
        navigationMenuPrincipal = (BottomNavigationView) findViewById(R.id.navigationMenuPrincipal);

    }


    private boolean crearConexionMqtt() {


        // Se crea una nueva conexion mqtt
        if (cnx == null) {
            cnx = new conexionMqtt(getApplicationContext());
            String broker;
            broker = cnx.getBrokerId();
            if (broker.length() == 0) {
                textIdBroker.setText(getString(R.string.broker_no_configurado));
            } else {
                textIdBroker.setText(cnx.getBrokerId());
            }

            if (cnx == null) {
                Log.e(getClass().toString(), "Error al crear la conexion mqtt");
                return false;
            }


            cnx.cliente.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.d(getClass().toString(), "Conexion completa!!!");
                    imageViewEstadoBroker.setImageResource(R.drawable.bk_conectado);
                    dialogo.setConexionMqtt(cnx);

                    //actualizarDispositivos();

                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(getClass().toString(), ": Conexion perdida!!!");
                    imageViewEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
                    //actualizarEstadoConexionDispositivos(ESTADO_CONEXION_IOT.DESCONECTADO, false);
                    //animacion.setVisibility(View.VISIBLE);

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(getClass().toString(), ": Ha llegado un mensaje!!!");
                    //procesarMensajeRecibido(message);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(getClass().toString(), ": entrega completa!!!");

                }
            });

        } else {
            Log.w(getLocalClassName(), "conexion mqtt ya existente");
            //return false;
        }

        cnx.setOnConexionMqtt(new conexionMqtt.OnConexionMqtt() {
            @Override
            public void conexionPerdida() {
                Log.e(getLocalClassName(), "No se ha podido establecer la conexion Mqtt ");
            }
        });
        cnx.establecerConexionMqtt(this.getApplicationContext());

       if (cnx != null) {
            if (cnx.cliente != null) {
                return cnx.cliente.isConnected();
            } else {
                return false;
            }


        } else {
            return false;
        }
        //return cnx.cliente.isConnected();


    }


}


