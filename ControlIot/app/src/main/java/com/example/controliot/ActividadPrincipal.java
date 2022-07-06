package com.example.controliot;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
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

import java.util.ArrayList;


public class ActividadPrincipal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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
    private Boolean arrancando;
    private ConstraintLayout panelBroker;
    private Notificaciones notificaciones;



    private void crearConexion() {


        cnx = new conexionMqtt(getApplicationContext(), dialogo);
        cnx.setEstadoConexion(false);
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



    }


    protected void inicioAplicacion() {
        // Se registran todos los controles de la activity
        registrarControles();
        inicializacionParametros();
        crearConexion();
        if (adapter == null) {
            if (!presentarDispositivos()) {
                swipeListaDispositivos.setBackgroundResource(R.drawable.sin_dispositivos);
            }
        }
        procesarMensajes();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicioAplicacion();

        arrancando = true;






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
        cnx.setEstadoConexion(true);
        imageViewEstadoBroker.setImageResource(R.drawable.bk_conectado);
        textIdBroker.setText(cnx.getBrokerId());
        textIdBroker.setTextColor(Color.BLACK);
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
        cnx.setEstadoConexion(false);
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
        notificaciones = new Notificaciones("ControlIot", "ControlIOt", "Notificacines de ControlIot", getApplicationContext());
        notificaciones.crearNotificacion();
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            public void temporizacionVencidaEnComando(COMANDO_IOT comando, String clave, String idDispositivo) {
                int i;
                i = buscarElementoEnListaDispositivos(idDispositivo);
                if (i >=0) {
                    if (lista.get(i).getEstadoConexion() == ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA) {
                        lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.DESCONECTADO);
                        Log.d(getLocalClassName(), idDispositivo + " vencida temporizacion de comando");
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
        swipeListaDispositivos = (SwipeRefreshLayout) findViewById(R.id.swipeProgramasInterruptor);
        swipeListaDispositivos.setOnRefreshListener(this);
        listViewListaDispositivos = (ListView) findViewById(R.id.listViewListaDispositivos);
        listViewListaDispositivos.setOnItemClickListener(this);
        listViewListaDispositivos.setOnItemLongClickListener(this);
        navigationMenuPrincipal = (BottomNavigationView) findViewById(R.id.navigationMenuPrincipal);
        navigationMenuPrincipal.setOnNavigationItemSelectedListener(this);
        progressEspera = (ProgressBar)  findViewById(R.id.barraProgreso);
        panelBroker = (ConstraintLayout) findViewById(R.id.idPanelBroker);

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
            if (tamArray == 0) {
                mensajeSinDispositivosConfigurados(true);

            } else {
                mensajeSinDispositivosConfigurados(false);
            }
            if (lista == null) {
                lista = new ArrayList<dispositivoIot>();
            }
            if (listViewListaDispositivos.getAdapter() == null) {
                adapter = new ListaDispositivosAdapter(this, R.layout.vista_dispositivo_desconocido, lista, cnx, dialogo);
            }
            if (tamArray == 0) adapter.notifyDataSetChanged();

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
        mensajeError(getApplicationContext(), "post", "post", R.drawable.ic_info);
    }


    private void refrescarLista() {


        configuracionDispositivos conf;
        conf = new configuracionDispositivos();
        if (conf.leerDispositivos(getApplicationContext())) {
            adapter.clear();
            lista.clear();
            presentarDispositivos();
            actualizarDispositivos();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();


        if (arrancando == false )  {

            if (cnx.getEstadoConexion()== false) {
                mensajeError(getApplicationContext(), "post", "post", R.drawable.ic_info);
                inicioAplicacion();
            }
        } else {
            arrancando = false;
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mensajeError(getApplicationContext(), "retart", "restart", R.drawable.ic_info);
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
        String idDispositivo = adapter.listaDispositivos.get(position).idDispositivo;

        if (estado_conexion_iot == ESTADO_CONEXION_IOT.CONECTADO) {
            switch(tipoDispositivo) {
                case DESCONOCIDO:
                    break;
                case INTERRUPTOR:

                    intent = new Intent(ActividadPrincipal.this, ActivityInterruptor.class);
                    intent.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), idDispositivo);
                    startActivity(intent);

                    break;
                case TERMOMETRO:
                    break;
                case CRONOTERMOSTATO:
                    intent = new Intent(ActividadPrincipal.this, ActivityTermostato.class);
                    intent.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), idDispositivo);
                    startActivity(intent);
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
            public void estadoInterruptor(String topic, String mensaje, dispositivoIotOnOff dispositivo) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void actuacionReleLocalInterruptor(String topic, String message, dispositivoIotOnOff dispositivo) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void actuacionReleRemotoInterruptor(String topic, String message, dispositivoIotOnOff dispositivo) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void consultarProgramacionInterruptor(String topic, String texto, ArrayList<ProgramaDispositivoIotOnOff> programa) {

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
            public void informacionDispositivo(String topic, String texto) {

            }

            @Override
            public void errorMensajeInterruptor(String topic, String mensaje) {

            }
        });

        cnx.setOnProcesarEspontaneosInterruptor(new conexionMqtt.OnProcesarEspontaneosInterruptor() {
            @Override
            public void arranqueAplicacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo) {

                actualizarEstadoInteruptor(dispositivo);

                notificaciones.enviarNotificacion("Reinicio del dispositivo", "El " + dispositivo.getNombreDispositivo() + " se ha reiniciado", R.drawable.ic_reset, R.drawable.ic_reset);

            }

            @Override
            public void cambioPrograma(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void actuacionRelelocal(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void actuacionReleRemoto(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarEstadoInteruptor(dispositivo);
            }

            @Override
            public void upgradeFirwareFota(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

            }

            @Override
            public void espontaneoDesconocido(String topic, String texto) {

            }

            @Override
            public void releTemporizado(String topic, String texto) {

            }

            @Override
            public void alarmaDispositivo(String topic, String texto) {

            }
        });
        cnx.setOnProcesarMensajesTermostato(new conexionMqtt.OnProcesarMensajesTermostato() {
            @Override
            public void estadoTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                actualizarEstadoTermometroTermostato(dispositivo);
            }

            @Override
            public void actuacionReleLocalTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {

            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {

            }

            @Override
            public void consultarProgramacionTermostato(String topic, String texto, String idDispositivo, ArrayList<ProgramaDispositivoIotTermostato> programa) {

            }

            @Override
            public void nuevoProgramacionTermostato(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void eliminarProgramacionTermostato(String topic, String texto, String idDispositivo, String idPrograma) {

            }

            @Override
            public void modificarProgramacionTermostato(String topic, String texto) {

            }

            @Override
            public void informacionDispositivo(String topic, String texto) {

            }

            @Override
            public void resetTermostato(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void factoryResetTermostato(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

            }

            @Override
            public void modificarUmbralTemperatura(String topic, String texto, double umbral) {

            }
            @Override
            public void modificarConfiguracionTermostato(String topic, String texto) {

            }
            @Override
            public void seleccionarSensorTemperatura(String topic, String texto) {

            }
        });

        cnx.setOnProcesarEspontaneosTermostato(new conexionMqtt.OnProcesarEspontaneosTermostato() {
            @Override
            public void arranqueAplicacionTermostato(String topic, String texto, dispositivoIotTermostato dispoisitivo) {
                actualizarEstadoTermometroTermostato(dispoisitivo);

            }

            @Override
            public void cambioProgramaTermostato(String topic, String texto, dispositivoIotTermostato dispositivo) {
                actualizarEstadoTermometroTermostato(dispositivo);
            }

            @Override
            public void atuacionReleLocalTermostato(String topic, String texto, String idDisositivo, ESTADO_RELE estadoRele) {

            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String texto, String idDispositivo, ESTADO_RELE estadoRele) {

            }

            @Override
            public void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

            }

            @Override
            public void cambioTemperaturaTermostato(String topic, String texto, dispositivoIotTermostato dispositivo) {
                actualizarEstadoTermometroTermostato(dispositivo);
            }

            @Override
            public void temporizadorCumplido(String topic, String texto, dispositivoIotTermostato dispositivo) {
                actualizarEstadoTermometroTermostato(dispositivo);

            }

            @Override
            public void cambioUmbralTemperatura(String topic, String texto, dispositivoIotTermostato dispositivo) {

            }
        });

        cnx.setOnProcesarMensajesTermometro(new conexionMqtt.OnProcesarMensajesTermometro() {
            @Override
            public void estadoTermometro(String topic, String message, dispositivoIotTermostato dispositivo) {
                actualizarEstadoTermometroTermostato(dispositivo);
            }

            @Override
            public void actuacionReleLocalTermometro(String topic, String message, dispositivoIotTermostato dispositivo) {

            }

            @Override
            public void actuacionReleRemotoTermometro(String topic, String message, dispositivoIotTermostato dispositivo) {

            }
        });
    }

    public android.app.AlertDialog mensajeError(Context contexto, String titulo, String mensaje, int icono) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(contexto);

        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setIcon(icono);
        builder.setPositiveButton(contexto.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();



    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        String nombreDispositivo = null;
        switch (parent.getId()) {
            case (R.id.listViewListaDispositivos):
                Log.i(getLocalClassName().toString(), "pulsacion larga en listview");
                nombreDispositivo = adapter.listaDispositivos.get(position).getNombreDispositivo();
                Log.i(getLocalClassName().toString(), "nombreDispositivo " + nombreDispositivo);

                break;

            default:
                break;
        }
        return false;
    }


    private void mensajeSinDispositivosConfigurados(Boolean pintar) {

        if (pintar == true) {

            panelBroker.setBackgroundResource(R.drawable.sin_dispositivos);
        } else {
            panelBroker.setBackgroundResource(0);
        }

    }

    public void Notificar(String titulo, String mensaje, int notID, Context contexto){
        NotificationCompat.Builder creador;
        String canalID = "MiCanal01";
        NotificationManager notificador = (NotificationManager) getSystemService(contexto.NOTIFICATION_SERVICE);
        creador = new NotificationCompat.Builder(contexto, canalID);
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String canalNombre = "Mensajes";
            String canalDescribe = "Canal de mensajes";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel miCanal = new NotificationChannel(canalID, canalNombre, importancia);
            miCanal.setDescription(canalDescribe);
            miCanal.enableLights(true);
            miCanal.setLightColor(Color.BLUE); // Esto no lo soportan todos los dispositivos
            miCanal.enableVibration(true);
            notificador.createNotificationChannel(miCanal);
            creador = new NotificationCompat.Builder(contexto, canalID);
        }
        Bitmap iconoNotifica = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.ic_info);
        int iconoSmall = R.drawable.ic_info;
        creador.setSmallIcon(iconoSmall);
        creador.setLargeIcon(iconoNotifica);
        creador.setContentTitle(titulo);
        creador.setContentText(mensaje);
        creador.setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje));
        creador.setChannelId(canalID);
        notificador.notify(notID, creador.build());
    }


}



