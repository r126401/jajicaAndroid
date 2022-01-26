package com.iotcontrol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.material.navigation.NavigationView;
import android.widget.ProgressBar;
import android.widget.TextView;




public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Serializable {


    ImageView iconoCnxMqtt;
    SwipeRefreshLayout swipeListaDispositivos;
    ListView listaDispositivos;
    Toolbar miToolbar;
    NavigationView navegador;
    conexionMqtt cnx;
    dialogoIot dialogo;
    ArrayList<dispositivoIot> lista = null;
    DispositivosAdapter adapter = null;
    DialogosAplicacion dial;
    ProgressBar animacion;
    TextView textoBroker;
    final int ACTIVITY_CONFIGURACION_MQTT = 0;
    final int ACTIVITY_ANADIR_DISPOSITIVO = 1;
    final int NO_CAMBIADO = 0;
    final int CAMBIADO = 1;
    boolean reconexion = false;


    // Notificaciones
    final String idCanalInterruputor = "EnventosInterruptor";
    final String idCanalCronotermostato = "EventosCronotermostato";
    final int importanciaEventosInterruptor = NotificationManager.IMPORTANCE_HIGH;
    final int importanciaEventosCronotermostato = NotificationManager.IMPORTANCE_HIGH;
    final String descripcionEventosInterruptor = "decripcion eventos interruptor";
    final String getDescripcionEventosCronotermostato = "descripcion eventos cronotermostato";
    private int idNotif1 = 0;


    /**
     * Funcion para asociar los controles a las variables de la activity
     */
    private void capturarControles() {

        capturarMenuNavegacion();
        iconoCnxMqtt = (ImageView) findViewById(R.id.iconoConexionMqtt);
        iconoCnxMqtt.setImageResource(R.drawable.bkconectadooff);
        swipeListaDispositivos = (SwipeRefreshLayout) findViewById(R.id.swipeListaDispositivosIot);
        swipeListaDispositivos.setOnRefreshListener(this);
        listaDispositivos = (ListView) findViewById(R.id.listaDispositivosIot);
        ejecutarCodigoListaDispositivos(listaDispositivos);
        animacion = (ProgressBar) findViewById(R.id.animacionMain);
        textoBroker = (TextView) findViewById(R.id.textoBrokerMqtt);


    }


    /**
     * Esta funcion crea la conexion mqtt por defecto.
     */

    private boolean crearConexionMqtt() {

        animacion.setVisibility(View.VISIBLE);

        // Se crea una nueva conexion mqtt
        if (cnx == null) {
            cnx = new conexionMqtt(getApplicationContext());
            String broker;
            broker = cnx.getBrokerId();
            if (broker.length() == 0) {
                textoBroker.setText(getString(R.string.brokerNoConfigurado));
            } else {
                textoBroker.setText(cnx.getBrokerId());
            }

            if (cnx == null) {
                Log.e(getClass().toString(), "Error al crear la conexion mqtt");
                return false;
            }


            cnx.cliente.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.i(getClass().toString(), "Conexion completa!!!");
                    animacion.setVisibility(View.INVISIBLE);
                    iconoCnxMqtt.setImageResource(R.drawable.bkconectado);
                    dialogo.setConexionMqtt(cnx);

                    actualizarDispositivos();

                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(getClass().toString(), ": Conexion perdida!!!");
                    iconoCnxMqtt.setImageResource(R.drawable.bkconectadooff);
                    actualizarEstadoConexionDispositivos(ESTADO_CONEXION_IOT.DESCONECTADO, false);
                    animacion.setVisibility(View.VISIBLE);

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i(getClass().toString(), ": Ha llegado un mensaje!!!");
                    procesarMensajeRecibido(message);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i(getClass().toString(), ": entrega completa!!!");

                }
            });

        } else {
            Log.e(getLocalClassName(), "conexion mqtt ya exsistente");
            //return false;
        }

        cnx.setOnConexionMqtt(new conexionMqtt.OnConexionMqtt() {
            @Override
            public void conexionPerdida() {
                Log.e(getLocalClassName(), "No se ha podido establecer la conexion Mqtt");
                //textoBroker.setText("Conexion perdida totalmente");
            }
        });
        cnx.establecerConexionMqtt(getApplicationContext());


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cnx = null;
        dialogo = new dialogoIot();
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            //Se considera que el dispositivo no esta accesible
            public void temporizacionVencidaEnComando(String idDispositivo, String clave, COMANDO_IOT comando) {
                Log.e(getLocalClassName(), "Ha fallado la ejeccion del comando en el dispositivo " + idDispositivo + " con clave " + clave + " y comando " + comando.toString());
                actualizarEstadoDispositivo(idDispositivo);

            }
        });
        dial = new DialogosAplicacion();
        capturarControles();
        comenzarFlujoActividad();
        //Creamos los canales para las notificaciones de cada tipo de dispositivo
        crearCanalNotificacion(idCanalInterruputor, importanciaEventosInterruptor, descripcionEventosInterruptor);
        crearCanalNotificacion(idCanalCronotermostato, importanciaEventosCronotermostato, getDescripcionEventosCronotermostato);


    }


    protected void comenzarFlujoActividad() {

        CountDownTimer contador = null;

        crearConexionMqtt();

        if (cnx != null) {
            if (cnx.isConnected()) {
                return;
            }

        }


        Log.e(getLocalClassName(), "No hay conexion, reintentamos en 30 segundos");

        contador = new CountDownTimer(30000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (cnx != null) {
                    if (cnx.isConnected()) {
                        cancel();
                        Log.i(getLocalClassName(), "conectados al broker. Cancelamos la temporizacion");
                    }
                    {
                        Log.w(getLocalClassName(), "Lo reintentaremos en " + (millisUntilFinished / 1000) + " segundos");
                    }
                } else {


                }

            }

            @Override
            public void onFinish() {
                if (cnx == null) {
                    Log.e(getLocalClassName(), "Fallo al crear la conexion. Reintentamos");
                    comenzarFlujoActividad();

                } else {
                    if (cnx.isConnected()) {
                        Log.i(getLocalClassName(), "Conexion realizada. Se termino la temporizacion.");
                    } else {
                        Log.e(getLocalClassName(), "Fallo al establecer la conexion. Reintentamos");
                        comenzarFlujoActividad();

                    }

                }


            }
        }.start();

        //if (adapter != null) adapter.clear();
        if (adapter == null) presentarDispositivos();

    }

    @Override
    public void onRefresh() {
        if (!cnx.isConnected()) {
            comenzarFlujoActividad();
        } else {
            actualizarDispositivos();
            swipeListaDispositivos.setRefreshing(false);
        }


    }


    /**
     * Funcion para tratar los eventos dentro de la lista de dispositivos
     *
     * @param listaDispositivos
     */
    private void ejecutarCodigoListaDispositivos(ListView listaDispositivos) {

        listaDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ESTADO_RELE estado;
                TIPO_DISPOSITIVO_IOT tipo;
                tipo = adapter.dispositivos.get(position).tipoDispositivo;
                switch (tipo) {
                    case INTERRUPTOR:
                        Intent interruptorActivity = new Intent(MainActivity.this, interruptor.class);
                        dispositivoIotOnOff interruptor = (dispositivoIotOnOff) adapter.dispositivos.get(position);
                        if (interruptor.estadoConexion == ESTADO_CONEXION_IOT.CONECTADO) {
                            interruptorActivity.putExtra(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson(), adapter.dispositivos.get(position).nombreDispositivo);
                            interruptorActivity.putExtra(TEXTOS_DIALOGO_IOT.ESTADO_CONEXION.getValorTextoJson(), adapter.dispositivos.get(position).estadoConexion);
                            estado = interruptor.estadoRele;
                            interruptorActivity.putExtra(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson(), estado);
                            interruptorActivity.putExtra(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson(), interruptor.getTopicSubscripcion());
                            interruptorActivity.putExtra(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson(), interruptor.getTopicPublicacion());
                            interruptorActivity.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), interruptor.getIdDispositivo());
                            startActivity(interruptorActivity);

                        }
                        break;


                    case CRONOTERMOSTATO:
                        Intent cronotermostatoActivity = new Intent(view.getContext(), com.iotcontrol.cronotermostato.class);
                        dispositivoIotTermostato a = (dispositivoIotTermostato) adapter.dispositivos.get(position);
                        if (a.getEstadoConexion() == ESTADO_CONEXION_IOT.CONECTADO) {
                            estado = a.estadoRele;
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson(), adapter.dispositivos.get(position).nombreDispositivo);
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.ESTADO_CONEXION.getValorTextoJson(), adapter.dispositivos.get(position).estadoConexion);
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson(), estado);
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.TEMPERATURA.getValorTextoJson(), a.temperatura);
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson(), a.umbralTemperatura);
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.HUMEDAD.getValorTextoJson(), a.humedad);
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson(), a.getTopicSubscripcion());
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson(), a.getTopicPublicacion());
                            cronotermostatoActivity.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), a.getIdDispositivo());
                            startActivity(cronotermostatoActivity);

                        }


                        break;

                    case DESCONOCIDO:
                        break;

                    default:
                        break;
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Captura los controles del menu de Navegacion
     */
    protected void capturarMenuNavegacion() {

        miToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        setSupportActionBar(miToolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, miToolbar, R.string.abrir, R.string.cerrar) {

            /*Método que será llamado cuando se cierre el drawer.*/
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                listaDispositivos.setVisibility(View.VISIBLE);
            }

            /*Método que será llamado cuando se abra el drawer.*/
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                listaDispositivos.setVisibility(View.GONE);
            }
        };

        /*Se establece un control que notificará de los eventos del drawer.*/
        drawer.setDrawerListener(actionBarDrawerToggle);
        /*Se invoca al método syncState(), que sincronizará el estado del drawer con el DrawerLayout.*/
        actionBarDrawerToggle.syncState();

        navegador = (NavigationView) findViewById(R.id.navigation_view);
        navegador.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                drawer.closeDrawers();
                switch (menuItem.getItemId()) {

                    case (R.id.menuConfigurarMqtt):
                        Intent intent = new Intent(getApplicationContext(), configuracionMqtt.class);
                        //startActivity(intent);
                        startActivityForResult(intent, ACTIVITY_CONFIGURACION_MQTT);

                        break;

                    case (R.id.menuAnadirDispositivo):
                        Intent intent2 = new Intent(getApplicationContext(), AnadirDispositivo.class);
                        startActivityForResult(intent2, ACTIVITY_ANADIR_DISPOSITIVO);
                        break;

                    case (R.id.menuInstalarDispositivo):
                        Intent intentInstalar = new Intent(getApplicationContext(), instalarDispositivo.class);

                        intentInstalar.putExtra(CONF_MQTT.BROKER.toString(), cnx.getBrokerId());
                        intentInstalar.putExtra(CONF_MQTT.PUERTO.toString(), cnx.getPuerto());
                        intentInstalar.putExtra(CONF_MQTT.USUARIO.toString(), cnx.getUsuario());
                        intentInstalar.putExtra(CONF_MQTT.PASSWORD.toString(), cnx.getPassword());

                        if (cnx.cliente.isConnected() == true) {
                            intentInstalar.putExtra("CONEXION", true);
                        } else {
                            intentInstalar.putExtra("CONEXION", false);
                        }


                        startActivity(intentInstalar);
                        break;

                    case (R.id.menuAyuda):
                        ayuda();
                        break;

                    case (R.id.acercaDe):
                        acercaDe();
                        break;


                }
                return true;
            }
        });
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
            default:
                Log.i(getLocalClassName(), " No se actualiza ningun parametro de dispositivo desconocido");
                break;
        }

        Log.i(getLocalClassName(), "degu");

        //Buscar el elemento en la lista
        //Actualizar los elementos con lo que viene en el mensaje
        //Actualizar la vista para que se vean los cambios en el listView.

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


    /**
     * Presenta la lista de dispositivos de la aplicacion.
     */

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
            if (listaDispositivos.getAdapter() == null) {
                adapter = new DispositivosAdapter(this, R.layout.lista_dispositivos, lista, cnx, dialogo);
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
                    default:
                        dispositivoIot desconocido = new dispositivoIotDesconocido(dispositivos);
                        adapter.add(desconocido);
                        break;
                }
            }
            listaDispositivos.setAdapter(adapter);
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
            //Cada elemento de la lista se subscribe a su topic.
            cnx.subscribirTopic(lista.get(i).topicSubscripcion);
            dialogo.enviarComando(lista.get(i), dialogo.comandoEstadoDispositivo());
            //cnx.publicarTopic(lista.get(i).topicPublicacion, comando.comandoEstadoDispositivo());
            //lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);

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


    private void actualizarInterruptor(dispositivoIotOnOff dispositivo, String texto, TIPO_INFORME tipoInforme, ESPONTANEO_IOT tipoEspontaneo) {

        ESTADO_RELE estadoRele;
        estadoRele = dialogo.getEstadoRele(texto);
        Log.i(getLocalClassName(), "Estado del rele: " + estadoRele.toString());
        dispositivo.setEstadoRele(estadoRele);
        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.CONECTADO);
        if (tipoInforme == TIPO_INFORME.INFORME_ESPONTANEO) {
            switch (tipoEspontaneo) {
                case ARRANQUE_APLICACION:
                    enviarNotificacion("Reinicio", "El dispositivo " + dispositivo.getNombreDispositivo() + " se ha reiniciado", R.drawable.warning, idCanalInterruputor, R.drawable.reset);
                    break;
                case ACTUACION_RELE_LOCAL:
                    if (dispositivo.getEstadoRele() == ESTADO_RELE.ON) {
                        enviarNotificacion("Actuacion Rele en local", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOnInterruptorlocal), R.drawable.interruptor, idCanalInterruputor, R.drawable.encendido);
                    } else {
                        enviarNotificacion("Actuacion Rele en local", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOffInterruptorlocal), R.drawable.interruptor, idCanalInterruputor, R.drawable.apagado);

                    }
                    break;
                case CAMBIO_DE_PROGRAMA:
                    if (dispositivo.getEstadoRele() == ESTADO_RELE.ON) {
                        enviarNotificacion("Cambio de programa", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOnInterruptorTemporizado), R.drawable.interruptor, idCanalInterruputor, R.drawable.encendido);
                    } else {
                        enviarNotificacion("Cambio de programa", "El dispositivo " + dispositivo.getNombreDispositivo() + " " + getString(R.string.eventoOffInterruptorTemporizado), R.drawable.interruptor, idCanalInterruputor, R.drawable.apagado);

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


    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i(getLocalClassName(), "restaurando la aplicacion...");

        if (reconexion == true) {
            cnx.cerrarConexion();
            cnx.cliente = null;
            cnx = null;
            comenzarFlujoActividad();
            if (adapter!=null) adapter.clear();
            presentarDispositivos();

            reconexion = false;
        }


    }


    private void actualizarEstadoConexionDispositivos(ESTADO_CONEXION_IOT estado, boolean reintentar) {
        int i;
        //dialogoIot comando = new dialogoIot();
        if (lista == null) {
            Log.i(getLocalClassName(), "No hay dispositivos registrados en la aplicacion");
            return;
        }

        for (i = 0; i < lista.size(); i++) {

            if (reintentar == false) {
                lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.DESCONECTADO);
            } else {
                cnx.subscribirTopic(lista.get(i).topicSubscripcion);
                dialogo.enviarComando(lista.get(i), dialogo.comandoEstadoDispositivo());
            }
            //Cada elemento de la lista se subscribe a su topic.
            //lista.get(i).setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
            //cnx.publicarTopic(lista.get(i).topicPublicacion, comando.comandoEstadoDispositivo());
            //lista.get(i).estadoConexion = estado;
        }
        adapter.notifyDataSetChanged();


    }

    void acercaDe() {

        dial.ventanaDialogo(this,
                COMANDO_IOT.ESTADO, getString(R.string.acercaDe),
                getString(R.string.avisoAcercaDe) + " 1.0"
                , R.drawable.icono_aplicacion).show();

        dial.setOnDialogosAplicacion(new DialogosAplicacion.OnDialogosAplicacion() {
            @Override
            public void OnAceptarListener(COMANDO_IOT idComando) {

            }

            @Override
            public void OnCancelarListener() {

            }
        });
    }

    void ayuda() {
        dial.ventanaDialogo(this,
                COMANDO_IOT.ESTADO, getString(R.string.ayuda),
                getString(R.string.avisoAyuda)
                , R.drawable.icono_aplicacion).show();
        dial.setOnDialogosAplicacion(new DialogosAplicacion.OnDialogosAplicacion() {
            @Override
            public void OnAceptarListener(COMANDO_IOT idComando) {


            }

            @Override
            public void OnCancelarListener() {

            }
        });
    }


    private void crearCanalNotificacion(String idCanal, int importancia, String descripcion) {


        NotificationChannel canalNotificacion;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            descripcion = getString(R.string.descripcionNotificacionDispositivo);
            canalNotificacion = new NotificationChannel(idCanal, idCanal, importancia);
            canalNotificacion.setDescription(descripcion);
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(canalNotificacion);
        }
    }

    private void enviarNotificacion(String titulo, String texto, int icono, String idCanal, int iconoGrande) {


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificador = new NotificationCompat.Builder(this, idCanal);
        notificador.setContentIntent(pendingIntent);
        notificador.setSmallIcon(icono);
        notificador.setContentTitle(titulo);
        //notificador.setContentText(texto);
        notificador.setWhen(System.currentTimeMillis());
        notificador.setShowWhen(true);
        notificador.setLargeIcon(BitmapFactory.decodeResource(getResources(), iconoGrande));
        notificador.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificador.setStyle(new NotificationCompat.BigTextStyle().bigText(texto)).build();
        notificador.setAutoCancel(true);
        NotificationManagerCompat notificacion = NotificationManagerCompat.from(this);
        notificacion.notify(idNotif1++, notificador.build());

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case ACTIVITY_CONFIGURACION_MQTT:
                if (resultCode == CAMBIADO) {
                    reconexion = true;
                } else {
                    reconexion = false;
                }
                break;
            case ACTIVITY_ANADIR_DISPOSITIVO:
                if(resultCode == CAMBIADO) {
                    reconexion = true;
                } else {
                    reconexion = false;
                }
                break;

        }
    }
}
