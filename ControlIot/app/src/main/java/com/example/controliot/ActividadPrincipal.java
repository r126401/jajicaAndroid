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
                notificarBrokerActivado();
                dialogo.setConexionMqtt(cnx);
                actualizarDispositivos();


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
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            public void temporizacionVencidaEnComando(String idDispositivo, String clave, COMANDO_IOT comando) {
                Log.i(getLocalClassName(), "Temporizacion vencida en el comando");
                actualizarEstadoDispositivo(idDispositivo);
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


    private void resultadoEscaneo() {

        Log.i(getLocalClassName(), "El resultado del escaneo es: " + texto);
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


    private void actualizarEstadoDispositivo(String idDispositivo) {

        int i;

        i = buscarElementoEnListaDispositivos(idDispositivo);
        if (i != -1) {
            lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.DESCONECTADO);
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


    private void procesarMensajeRecibido(MqttMessage mensajeRecibido) {

        COMANDO_IOT idComando;
        //dialogoIot dialogo = new dialogoIot();
        JSONObject objeto = null;
        String clave;
        String textoRecibido = new String(mensajeRecibido.getPayload());
        Log.i(getClass().toString(), "mensaje: " + textoRecibido);
        idComando = dialogo.descubrirComando(textoRecibido);
        Log.i(getClass().toString(), "idComando:" + idComando.toString());


        switch (idComando) {

            case ESTADO:
                actualizarEstadoDispositivos(textoRecibido, TIPO_INFORME.RESULTADO_COMANDO, ESPONTANEO_IOT.ESPONTANEO_DESCONOCIDO);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                break;

            case ESPONTANEO:
                Log.i(getLocalClassName(), "Recibido espontaneo de status");
                procesarInformeEspontaneo(textoRecibido);
                break;

            case ACTUAR_RELE:
                Log.i(getLocalClassName(), "Se ha actuado el rele del dispositivo");
                actualizarEstadoDispositivos(textoRecibido, TIPO_INFORME.RESULTADO_COMANDO, ESPONTANEO_IOT.ESPONTANEO_DESCONOCIDO);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                break;

            default:
                Log.i(getLocalClassName(), "Mensaje no procesado por la activity " + getLocalClassName());
                break;

        }


    }


    private void actualizarEstadoDispositivos(String textoRecibido, TIPO_INFORME tipoInforme, ESPONTANEO_IOT tipoEspontaneo) {

        int i;
        String idDispositivo;
        String nombreClase = null;
        TIPO_DISPOSITIVO_IOT tipoDispositivo;

        tipoDispositivo = dialogo.getTipoDispositivo(textoRecibido);
        Log.i(getLocalClassName(), "tipo de dispositivo: " + tipoDispositivo.toString());
        idDispositivo = dialogo.getIdDispositivo(textoRecibido);
        Log.i(getLocalClassName(), "el id del dispositivo: " + idDispositivo);


        for (i = 0; i < lista.size(); i++) {
            if (lista.get(i).idDispositivo.equals(idDispositivo)) {
                Log.i(getLocalClassName(), "dispositivo " + idDispositivo + " encontrado");
                break;
            }
        }

        if (i == lista.size()) {
            Log.w(getLocalClassName(), "El mensaje recibido no es de ningun dispositivo registrado");
            return;
        }


        nombreClase = lista.get(i).getClass().getSimpleName();
        switch (tipoDispositivo) {


            case INTERRUPTOR:
                if (nombreClase.equals("dispositivoIotDesconocido")) {
                    Log.i(getLocalClassName(), "Interruptor  " + lista.get(i).nombreDispositivo + " no registrado como tal.");
                    dispositivoIot nuevoInterruptor = new dispositivoIotOnOff(lista.get(i));
                    //((dispositivoIotOnOff) nuevoInterruptor).setEstadoRele(estadoRele);
                    //nuevoInterruptor.setEstadoConexionDispositivo(ESTADO_CONEXION_IOT.CONECTADO);
                    nuevoInterruptor.modificarTipoDispositivo(nuevoInterruptor, tipoDispositivo, getApplicationContext());
                    adapter.remove(lista.get(i));
                    adapter.add(nuevoInterruptor);

                }
                actualizarInterruptor((dispositivoIotOnOff) lista.get(i), textoRecibido, tipoInforme, tipoEspontaneo);
                adapter.notifyDataSetChanged();

                break;
            case CRONOTERMOSTATO:
                if (nombreClase.equals("dispositivoIotDesconocido")) {
                    Log.i(getLocalClassName(), "Cronotermostato  " + lista.get(i).nombreDispositivo + " no registrado como tal.");
                    dispositivoIot nuevoTermostato = new dispositivoIotTermostato(lista.get(i));
                    nuevoTermostato.modificarTipoDispositivo(nuevoTermostato, tipoDispositivo, getApplicationContext());
                    adapter.remove(lista.get(i));
                    adapter.add(nuevoTermostato);
                }
                actualizarTermostato((dispositivoIotTermostato) lista.get(i), textoRecibido);
                adapter.notifyDataSetChanged();

                break;
            case TERMOMETRO:
                if (nombreClase.equals("dispositivoIotDesconocido")) {
                    Log.i(getLocalClassName(), "Termometro  " + lista.get(i).nombreDispositivo + " no registrado como tal.");
                    dispositivoIot nuevoTermostato = new dispositivoIotTermostato(lista.get(i), TIPO_DISPOSITIVO_IOT.TERMOMETRO);
                    nuevoTermostato.modificarTipoDispositivo(nuevoTermostato, tipoDispositivo, getApplicationContext());
                    adapter.remove(lista.get(i));
                    adapter.add(nuevoTermostato);
                }
                actualizarTermostato((dispositivoIotTermostato) lista.get(i), textoRecibido);
                adapter.notifyDataSetChanged();
                break;

            default:
                Log.i(getLocalClassName(), " No se actualiza ningun parametro de dispositivo desconocido");
                break;
        }

        Log.i(getLocalClassName(), "degu");

        //Buscar el elemento en la lista
        //Actualizar los elementos con lo que viene en el mensaje
        //Actualizar la vista para que se vean los cambios en el listView.

    }


    private void actualizarInterruptor(dispositivoIotOnOff dispositivo, String texto, TIPO_INFORME tipoInforme, ESPONTANEO_IOT tipoEspontaneo) {

        ESTADO_RELE estadoRele;
        estadoRele = dialogo.getEstadoRele(texto);
        Log.i(getLocalClassName(), "Estado del rele: " + estadoRele.toString());
        dispositivo.setEstadoRele(estadoRele);
        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
        if (tipoInforme == TIPO_INFORME.INFORME_ESPONTANEO) {
            switch (tipoEspontaneo) {
                case ARRANQUE_APLICACION:
                    //enviarNotificacion("Reinicio", "El dispositivo " + dispositivo.getNombreDispositivo() + " se ha reiniciado", R.drawable.warning, idCanalInterruputor, R.drawable.reset);
                    break;
                case ACTUACION_RELE_LOCAL:
                    if (dispositivo.getEstadoRele() == ESTADO_RELE.ON) {
                        //enviarNotificacion("Actuacion Rele en local", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOnInterruptorlocal), R.drawable.interruptor, idCanalInterruputor, R.drawable.encendido);
                    } else {
                        //enviarNotificacion("Actuacion Rele en local", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOffInterruptorlocal), R.drawable.interruptor, idCanalInterruputor, R.drawable.apagado);

                    }
                    break;
                case CAMBIO_DE_PROGRAMA:
                    if (dispositivo.getEstadoRele() == ESTADO_RELE.ON) {
                        //enviarNotificacion("Cambio de programa", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOnInterruptorTemporizado), R.drawable.interruptor, idCanalInterruputor, R.drawable.encendido);
                    } else {
                        //enviarNotificacion("Cambio de programa", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOffInterruptorTemporizado), R.drawable.interruptor, idCanalInterruputor, R.drawable.apagado);

                    }
                    break;


            }
            Log.i(getLocalClassName(), "Informe espontaneo");
        }


    }

    private void actualizarTermostato(dispositivoIotTermostato dispositivo, String texto) {

        ESTADO_RELE estadoRele;
        double temperatura, umbral, humedad;
        estadoRele = dialogo.getEstadoRele(texto);
        Log.i(getLocalClassName() + ": actualizarTermostato", "Estado del rele: " + estadoRele.toString());
        dispositivo.setEstadoRele(estadoRele);
        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
        temperatura = dialogo.getTemperatura(texto);
        humedad = dialogo.getHumedad(texto);
        umbral = dialogo.getUmbralTemperatura(texto);
        dispositivo.setTemperatura(temperatura);
        dispositivo.setHumedad(humedad);
        dispositivo.setUmbralTemperatura(umbral);


    }

    private void procesarInformeEspontaneo(String textoRecibido) {

        ESPONTANEO_IOT tipoInformeEspontaneo;

        tipoInformeEspontaneo = dialogo.descubrirTipoInformeEspontaneo(textoRecibido);
        switch (tipoInformeEspontaneo) {
            case ARRANQUE_APLICACION:
                actualizarEstadoDispositivos(textoRecibido, TIPO_INFORME.INFORME_ESPONTANEO, ESPONTANEO_IOT.ARRANQUE_APLICACION);
                break;
            case CAMBIO_DE_PROGRAMA:
                actualizarEstadoDispositivos(textoRecibido, TIPO_INFORME.INFORME_ESPONTANEO, ESPONTANEO_IOT.CAMBIO_DE_PROGRAMA);
                break;
            case COMANDO_APLICACION:
                break;
            case ACTUACION_RELE_LOCAL:
                actualizarEstadoDispositivos(textoRecibido, TIPO_INFORME.INFORME_ESPONTANEO, ESPONTANEO_IOT.ACTUACION_RELE_LOCAL);

                break;
            case ACTUACION_RELE_REMOTO:
                break;
            case UPGRADE_FIRMWARE_FOTA:

                break;

            case CAMBIO_TEMPERATURA:
                actualizarEstadoDispositivos(textoRecibido, TIPO_INFORME.INFORME_ESPONTANEO, ESPONTANEO_IOT.ARRANQUE_APLICACION);
                break;
            default:
                break;
        }

        Log.i(getLocalClassName(), "Tipo de informe espontaneo " + tipoInformeEspontaneo.toString());


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
        tipoDispositivo = adapter.listaDispositvos.get(position).tipoDispositivo;
        estado_conexion_iot = adapter.listaDispositvos.get(position).getEstadoConexion();
        if (estado_conexion_iot == ESTADO_CONEXION_IOT.CONECTADO) {
            switch(tipoDispositivo) {
                case DESCONOCIDO:
                    break;
                case INTERRUPTOR:
                    String idDispositivo = adapter.listaDispositvos.get(position).idDispositivo;
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
        i = buscarElementoEnListaDispositivos(dispositivo.getIdDispositivo());
        if (i>=0) {
            dispositivo.modificarDispositivo(dispositivo, getApplicationContext());
            lista.remove(lista.get(i));
            lista.add(dispositivo);
            //dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
            adapter.notifyDataSetChanged();

        }

    }

    private void actualizarEstadoTermometroTermostato(dispositivoIotTermostato dispositivo) {
        int i;
        i = buscarElementoEnListaDispositivos(dispositivo.getIdDispositivo());
        if (i>=0) {
            dispositivo.modificarDispositivo(dispositivo, getApplicationContext());
            lista.remove(lista.get(i));
            lista.add(dispositivo);
            //dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
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
            public void actuacionReleLocalInterruptor(String topic, MqttMessage message, dispositivoIotOnOff dispositivo, TIPO_INFORME tipoInforme) {

            }

            @Override
            public void actuacionReleRemotoInterruptor(String topic, MqttMessage message, dispositivoIotOnOff dispositivo, TIPO_INFORME tipoInforme) {

            }

            @Override
            public void errorMensaje(String topic, MqttMessage mensaje) {

            }
        });
        cnx.setOnProcesarMensajesTermometro(new conexionMqtt.OnProcesarMensajesTermometro() {
            @Override
            public void estadoTermometro(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {
                actualizarEstadoTermometroTermostato(dispositivo);

            }
        });
        cnx.setOnProcesarMensajesTermostato(new conexionMqtt.OnProcesarMensajesTermostato() {
            @Override
            public void estadoTermostato(String topic, String message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }

            @Override
            public void actuacionReleLocalTermostato(String topic, MqttMessage message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, MqttMessage message, dispositivoIotTermostato dispositivo, TIPO_INFORME tipoInforme) {

            }
        });

    }
}



