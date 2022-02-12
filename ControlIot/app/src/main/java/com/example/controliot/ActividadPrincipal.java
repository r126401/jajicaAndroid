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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActividadPrincipal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

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
    private ArrayList<dispositivoIot> lista = null;
    private ListaDispositivosAdapter adapter = null;





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

                dialogo.setConexionMqtt(cnx);
                notificarBrokerActivado();


            }

            @Override
            public void conexionPerdida(Throwable cause) {
                notificarBrokerReintentoConexion();

            }

            @Override
            public void mensajeRecibido(String topic, MqttMessage message) {
                //procesarMensajeRecibido(message);

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
        if (adapter == null) presentarDispositivos();
        procesarMensajes();





    }

    
    private void notificarBrokerDesactivado() {

        int i;
        imageViewEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
        if (cnx != null) {
            textIdBroker.setText("Conectando a " + cnx.getBrokerId());
        }

        textIdBroker.setTextColor(Color.RED);
        progressEspera.setVisibility(View.VISIBLE);
        if (adapter != null) {
            if (lista != null) {
                for (i=0;i<lista.size();i++) {
                    lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.DESCONECTADO);
                }
            }
            adapter.notifyDataSetChanged();
        }


    }
    private void notificarBrokerActivado() {
        int i;
        imageViewEstadoBroker.setImageResource(R.drawable.bk_conectado);
        textIdBroker.setText(cnx.getBrokerId());
        textIdBroker.setTextColor(Color.GREEN);
        progressEspera.setVisibility(View.INVISIBLE);
        if (adapter != null) {
            if (lista != null) {
                for (i=0;i<lista.size();i++) {
                    lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
                }
            }
            adapter.notifyDataSetChanged();
        }
        actualizarDispositivos();

    }
    private void notificarBrokerReintentoConexion() {
        int i;
        imageViewEstadoBroker.setImageResource(R.drawable.bk_no_conectado);
        textIdBroker.setTextColor(Color.MAGENTA);
        textIdBroker.setText("Reintentando conexion a " + cnx.getBrokerId());
        progressEspera.setVisibility(View.VISIBLE);
        if (adapter != null) {
            if (lista != null) {
                for (i=0;i<lista.size();i++) {
                    lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.DESCONECTADO);
                }
            }
            adapter.notifyDataSetChanged();
        }

    }


    private void inicializacionParametros() {
        cnx = null;
        dialogo = new dialogoIot();
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            public void temporizacionVencidaEnComando(dispositivoIot dispositivo) {

                int i;
                i = buscarElementoEnListaDispositivos(dispositivo.getIdDispositivo());
                if (i >=0) {
                    if (lista.get(i).getEstadoConexion() == ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA) {
                        lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.DESCONECTADO);
                        adapter.notifyDataSetChanged();
                    }

                }

            }
        });
        notificarBrokerDesactivado();
    }
    private void registrarControles() {

        textIdBroker = (TextView) findViewById(R.id.textEstadoBroker);
        imageViewEstadoBroker = (ImageView) findViewById(R.id.imageViewEstadoBroker);
        textListaDispositivo = (TextView) findViewById(R.id.textListaDispositivo);
        imageViewImagenDecorativa = (ImageView) findViewById(R.id.imageViewImagenDecorativa);
        swipeListaDispositivos = (SwipeRefreshLayout) findViewById(R.id.swipeProgramasInterruptor);
        swipeListaDispositivos.setOnRefreshListener(this);
        listViewListaDispositivos = (ListView) findViewById(R.id.listViewProgramasInterruptor);
        listViewListaDispositivos.setOnItemClickListener(this);
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
                        refrescarLista();

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



    private boolean presentarDispositivos() {


        JSONArray arrayDispositivos;
        int tamArray;
        int i;
        dispositivoIot dispositivos;

        configuracionDispositivos grupoDispositivos = new dispositivoIotDesconocido();


        if (grupoDispositivos.leerDispositivos(getApplicationContext()) == false) {
            Log.w(getClass().toString(), ": no hay dispositivos configurados");
            return false;
        } else {
            Log.i(getClass().toString(), "Vamos a presentar los dispositivos en el listview");

            try {
                arrayDispositivos = grupoDispositivos.getDatosDispositivos().getJSONArray(TEXTOS_DIALOGO_IOT.DISPOSITIVOS.getValorTextoJson());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(getClass().toString(), "El json de dispositivos esta corrupto");
                return false;
            }

            tamArray = arrayDispositivos.length();
            if (lista == null) {
                lista = new ArrayList<dispositivoIot>();
            }
            if (listViewListaDispositivos.getAdapter() == null) {
                adapter = new ListaDispositivosAdapter(this, R.layout.vista_dispositivo_desconocido, lista, cnx, dialogo);
            }

            for (i = 0; i < tamArray; i++) {


                try {
                    dispositivos = grupoDispositivos.json2Dispositivo(arrayDispositivos.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }

                switch (dispositivos.tipoDispositivo) {
                    case INTERRUPTOR:
                        dispositivoIot interruptor = new dispositivoIotOnOff(dispositivos);
                        adapter.add(interruptor);

                        break;
                    case CRONOTERMOSTATO:
                        dispositivoIot cronotermostato = new dispositivoIotTermostato(dispositivos);
                        adapter.add(cronotermostato);
                        break;
                    case TERMOMETRO:
                        dispositivoIot termometro = new dispositivoIotTermostato(dispositivos, TIPO_DISPOSITIVO_IOT.TERMOMETRO);
                        adapter.add(termometro);
                        break;
                    default:
                        dispositivoIot desconocido = new dispositivoIotDesconocido(dispositivos);
                        adapter.add(desconocido);
                        break;
                }
            }
            listViewListaDispositivos.setAdapter(adapter);
        }
        return true;
    }

    private void actualizarDispositivos() {


        int i;

        //dialogoIot comando = new dialogoIot();
        if (lista == null) {
            Log.i(getLocalClassName(), "No hay dispositivos registrados en la aplicacion");
            return;
        }

        for (i = 0; i < lista.size(); i++) {
            cnx.subscribirTopic(lista.get(i).topicSubscripcion);
            dialogo.enviarComando(lista.get(i), dialogo.comandoEstadoDispositivo());
        }
        adapter.notifyDataSetChanged();

    }



    private int buscarElementoEnListaDispositivos(String idDispositivo) {

        int i;

        for (i = 0; i < lista.size(); i++) {
            if (lista.get(i).idDispositivo.equals(idDispositivo)) {
                Log.i(getLocalClassName(), "dispositivo " + idDispositivo + " encontrado");
                return i;
            }

        }

        return -1;
    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    private void refrescarLista() {
        adapter.clear();
        lista.clear();
        presentarDispositivos();
        actualizarDispositivos();




    }


    @Override
    protected void onResume() {
        super.onResume();
        refrescarLista();




    }

    @Override
    public void onRefresh() {
        refrescarLista();
        swipeListaDispositivos.setRefreshing(false);



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TIPO_DISPOSITIVO_IOT tipoDispositivo;
        ESTADO_CONEXION_IOT estado_conexion_iot;
        Intent intent = null;
        tipoDispositivo = adapter.listaDispositivos.get(position).tipoDispositivo;
        estado_conexion_iot = adapter.listaDispositivos.get(position).getEstadoConexion();
        if (estado_conexion_iot == ESTADO_CONEXION_IOT.CONECTADO) {
            switch(tipoDispositivo) {
                case DESCONOCIDO:
                    break;
                case INTERRUPTOR:
                    String idDispositivo = adapter.listaDispositivos.get(position).idDispositivo;
                    intent = new Intent(ActividadPrincipal.this, ActivityInterruptor.class);
                    intent.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), idDispositivo);
                    startActivity(intent);

                    break;
                case TERMOMETRO:
                    break;
                case CRONOTERMOSTATO:
                    break;
            }
        }


    }


    private void actualizarEstadoInteruptor(dispositivoIotOnOff dispositivo) {
        int i;
        dispositivo.modificarTipoDispositivo(dispositivo, TIPO_DISPOSITIVO_IOT.INTERRUPTOR, getApplicationContext());
        i = buscarElementoEnListaDispositivos(dispositivo.getIdDispositivo());
        if (i>=0) {
            dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
            adapter.remove(lista.get(i));
            adapter.insert(dispositivo,i);
            adapter.notifyDataSetChanged();

        }

    }

    private void actualizarEstadoTermometroTermostato(dispositivoIotTermostato dispositivo) {
        int i;
        if (dispositivo.getTipoDispositivo() == TIPO_DISPOSITIVO_IOT.TERMOMETRO) {
            dispositivo.modificarTipoDispositivo(dispositivo, TIPO_DISPOSITIVO_IOT.TERMOMETRO, getApplicationContext());
        } else {
            dispositivo.modificarTipoDispositivo(dispositivo, TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO, getApplicationContext());
        }

        i = buscarElementoEnListaDispositivos(dispositivo.getIdDispositivo());
        if (i>=0) {
            dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
            adapter.remove(lista.get(i));
            adapter.insert(dispositivo,i);
            adapter.notifyDataSetChanged();


        }

    }


    private void procesarMensajes() {

        cnx.setOnProcesarMensajesInterruptor(new conexionMqtt.OnProcesarMensajesInterruptor() {
            @Override
            public void estadoInterruptor(String topic, String mensaje, dispositivoIotOnOff dispositivo, TIPO_INFORME tipoInforme) {

                actualizarEstadoInteruptor(dispositivo);

            }

            @Override
            public void actuacionReleLocalInterruptor(String topic, String message, dispositivoIotOnOff dispositivo, TIPO_INFORME tipoInforme) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void actuacionReleRemotoInterruptor(String topic, String message, dispositivoIotOnOff dispositivo, TIPO_INFORME tipoInforme) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void errorMensaje(String topic, MqttMessage mensaje) {

            }
        });
        cnx.setOnProcesarMensajesTermostato(new conexionMqtt.OnProcesarMensajesTermostato() {
            @Override
            public void estadoTermostato(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {
                actualizarEstadoTermometroTermostato(dispositivo);

            }

            @Override
            public void actuacionReleLocalTermostato(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }
        });
        cnx.setOnProcesarMensajesTermometro(new conexionMqtt.OnProcesarMensajesTermometro() {
            @Override
            public void estadoTermometro(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {
                actualizarEstadoTermometroTermostato(dispositivo);
            }

            @Override
            public void actuacionReleLocalTermometro(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }

            @Override
            public void actuacionReleRemotoTermometro(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }
        });
    }




}



