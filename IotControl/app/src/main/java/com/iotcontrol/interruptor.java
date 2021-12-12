package com.iotcontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

import static com.iotcontrol.COMANDO_IOT.ESTADO;
import static com.iotcontrol.COMANDO_IOT.FACTORY_RESET;
import static com.iotcontrol.COMANDO_IOT.MODIFICAR_PROGRAMACION;
import static com.iotcontrol.COMANDO_IOT.NUEVA_PROGRAMACION;
import static com.iotcontrol.COMANDO_IOT.RESET;
import static com.iotcontrol.COMANDO_IOT.UPGRADE_FIRMWARE;

public class interruptor extends AppCompatActivity implements View.OnClickListener, DialogosAplicacion.OnDialogosAplicacion, SwipeRefreshLayout.OnRefreshListener, Serializable {

    private Toolbar toolbarInterruptor;
    private dispositivoIotOnOff dispositivo;
    public ProgressBar animacion;
    private ProgressBar animacionInterruptor;
    public ProgressBar barra;
    private ImageView iconoNotificacion;
    private TextView textoNotificacion;
    private ImageView iconoEstadoBroker;
    private ImageView imageInterruptor;
    private ImageView imageConfiguracionesInterruptor;
    private ImageView botonSettings;
    private dialogoIot dialogo;
    private ImageView botonAnadirPrograma;
    SwipeRefreshLayout swipeRefreshLayoutListaPrograma;

    Handler handler;
    int contador;



    conexionMqtt cnx;
    PanelNotificacion panel;


    final String topicPeticionOta = "OtaIotOnOff";
    final String topicRespuestaOta = "newVersionOtaIotOnOff";
    boolean versionComprobada = false;


    ProgramasInterruptorAdapter programasAdapter = null;
    ListView listaProg;

    DialogosAplicacion dial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interruptor);

        definicionControles();
        panel = new PanelNotificacion();
        panel.setIconoNotificacion(iconoNotificacion);
        panel.setTextoNotificacion(textoNotificacion);
        dialogo = new dialogoIot();
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            public void temporizacionVencidaEnComando(String idDispositivo, String clave, COMANDO_IOT comando) {

            }
        });


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            dispositivo = new dispositivoIotOnOff();
            dispositivo.setNombreDispositivo((String) bundle.get(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson()));
            dispositivo.setIdDispositivo((String) bundle.get(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson()));
            dispositivo.setEstadoRele((ESTADO_RELE) bundle.get(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson()));
            switch (dispositivo.getEstadoRele()) {

                case ON:
                    imageInterruptor.setTag((ESTADO_RELE) ESTADO_RELE.ON);
                    break;
                case OFF:
                    imageInterruptor.setTag((ESTADO_RELE) ESTADO_RELE.OFF);
                    break;
                case INDETERMINADO:
                    imageInterruptor.setTag((ESTADO_RELE) ESTADO_RELE.INDETERMINADO);
                    break;

            }
            dispositivo.setEstadoConexion((ESTADO_CONEXION_IOT) bundle.get(TEXTOS_DIALOGO_IOT.ESTADO_CONEXION.getValorTextoJson()));
            dispositivo.setTopicPublicacion((String) bundle.get(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson()));
            dispositivo.setTopicSubscripcion((String) bundle.get(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson()));
            //cnx = (conexionMqtt) intent.getSerializableExtra("conexion");
        }
        toolbarInterruptor.setTitle(dispositivo.getNombreDispositivo() + ": " + dispositivo.getIdDispositivo());

        //crearConexionMqtt();
        abrirConexionMqtt();


    }

    /**
     * Esta funcion abre la conexionMqtt y lo reintenta cada 30 segundos el broker esta indisponible.
     */
    private void abrirConexionMqtt() {

        CountDownTimer contador;

        crearConexionMqtt();
        contador = new CountDownTimer(30000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(getLocalClassName(), "Lo reintentamos en " + String.valueOf(millisUntilFinished / 1000) + " segundos" );
            }

            @Override
            public void onFinish() {
                if (cnx == null) {
                    Log.e(getLocalClassName(), "Fallo al conectarse al broker, reintamos...");
                    abrirConexionMqtt();

                }

            }
        }.start();
    }

    void definicionControles() {

        animacionInterruptor = (ProgressBar) findViewById(R.id.animacionComandos);
        toolbarInterruptor = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarInterruptor);
        toolbarInterruptor.setNavigationIcon(R.drawable.interruptor);
        animacion = (ProgressBar) findViewById(R.id.animacion);
        barra = (ProgressBar) findViewById(R.id.barra);
        iconoNotificacion = (ImageView) findViewById(R.id.iconoNotificacion);
        iconoNotificacion.setOnClickListener(this);
        textoNotificacion = (TextView) findViewById(R.id.textoNotificacion);
        textoNotificacion.setOnClickListener(this);
        iconoEstadoBroker = (ImageView) findViewById(R.id.iconoEstadoMqtt);
        iconoEstadoBroker.setOnClickListener(this);
        imageInterruptor = (ImageView) findViewById(R.id.imageInterruptor);
        imageInterruptor.setOnClickListener(this);
        imageConfiguracionesInterruptor = (ImageView) findViewById(R.id.botonPuntosSuspensivos);
        imageConfiguracionesInterruptor.setOnClickListener(this);
        botonSettings = (ImageView) findViewById(R.id.botonSettings);
        botonSettings.setOnClickListener(this);
        swipeRefreshLayoutListaPrograma = (SwipeRefreshLayout) findViewById(R.id.swipeListaProgramas);
        swipeRefreshLayoutListaPrograma.setOnRefreshListener(this);
        listaProg = (ListView) findViewById(R.id.listaProgramasInterruptor);
        listaProg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProgramaDispositivoIotOnOff programa = programasAdapter.getItem(position);
                Intent intent = new Intent(interruptor.this, programador.class);
                intent.putExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString(), programa);
                intent.putExtra("TIPO_DISPOSITIVO", TIPO_DISPOSITIVO_IOT.INTERRUPTOR);
                startActivityForResult(intent, COMANDO_IOT.MODIFICAR_PROGRAMACION.getIdComando());

            }
        });
        botonAnadirPrograma = (ImageView) findViewById(R.id.botonAnadirPrograma);
        botonAnadirPrograma.setOnClickListener(this);



    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageInterruptor:
                Log.i(getLocalClassName(), "pulsado el interruptor");
                actuarInterruptor();
                break;

            case R.id.botonAnadirPrograma:
                Log.i(getLocalClassName(), "pulsado añadir programa");
                crearNuevaProgramacion();
                break;
            case R.id.botonSettings:
                Log.i(getLocalClassName(), "pulsado el boton settings");
                visualizarMenuSettings(v);
                break;
            case R.id.botonPuntosSuspensivos:
                Log.i(getLocalClassName(), "pulsado el boton Puntos suspensivos");
                visualizarMenuPuntosSuspensivos(v);
                break;

        }

    }


    private boolean crearConexionMqtt() {

        // Se crea una nueva conexion mqtt
        if (cnx == null) {
            cnx = new conexionMqtt(getApplicationContext());
            if (cnx == null) {
                Log.e(getClass().toString(), "Error al crear la conexion mqtt");
                return false;
            }

            cnx.cliente.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.i(getClass().toString(), "Conexion completa!!!");
                    panel.notificarEstadoConexionBroker(iconoEstadoBroker, textoNotificacion, ESTADO_CONEXION_IOT.CONECTADO);
                    animacion.setVisibility(View.INVISIBLE);
                    dialogo.setConexionMqtt(cnx);
                    actualizarDispositivo();

                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(getClass().toString(), ": Conexion perdida!!!");
                    panel.notificarEstadoConexionBroker(iconoEstadoBroker, textoNotificacion, ESTADO_CONEXION_IOT.DESCONECTADO);
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


        cnx.establecerConexionMqtt();
        cnx.setOnConexionMqtt(new conexionMqtt.OnConexionMqtt() {
            @Override
            public void conexionPerdida() {
                Log.e(getLocalClassName(), "No se ha podido establecer la conexion Mqtt");
            }
        });

        return cnx.cliente.isConnected();
    }












/*
    }

        // Se crea una nueva conexion mqtt
        if (cnx == null) {
            cnx = new conexionMqtt(getApplicationContext());
            if (cnx == null) {
                Log.e(getClass().toString(), "Error al crear la conexion mqtt");
                return false;
            }

        cnx.cliente.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.i(getClass().toString(), "Conexion completa!!!");
                panel.notificarEstadoConexionBroker(iconoEstadoBroker, textoNotificacion, ESTADO_CONEXION_IOT.CONECTADO);
                animacion.setVisibility(View.INVISIBLE);
                actualizarDispositivo();



            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(getClass().toString(), ": Conexion perdida!!!");
                panel.notificarEstadoConexionBroker(iconoEstadoBroker, textoNotificacion, ESTADO_CONEXION_IOT.DESCONECTADO);



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
        cnx.establecerConexionMqtt();
        cnx.setOnConexionMqtt(new conexionMqtt.OnConexionMqtt() {
            @Override
            public void conexionPerdida() {
                Log.e(getLocalClassName(), "Se ha perdido la conexion");
            }
        });

        return cnx.cliente.isConnected();
*/

//}



    private void procesarMensajeRecibido(MqttMessage mensajeRecibido) {

        COMANDO_IOT idComando;
        //dialogoIot dialogo = new dialogoIot();
        String textoRecibido = new String(mensajeRecibido.getPayload());
        Log.i(getClass().toString(), "procesarMensajeRecibido: mensaje: " + textoRecibido);
        idComando = dialogo.descubrirComando(textoRecibido);
        Log.i(getClass().toString(), "idComando:" + idComando.toString());


        Log.i(getLocalClassName(), "idComando: " + idComando);


        switch (idComando) {



            //dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
            case ESTADO:
                actualizarEstadoDispositivo(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();

                break;

            case ESPONTANEO:
                Log.i(getLocalClassName(), "Recibido informe espontaneo");
                procesarInformeEspontaneo(textoRecibido);
                break;

            case ACTUAR_RELE:
                Log.i(getLocalClassName(), "Se ha actuado el rele del dispositivo");
                actualizarEstadoDispositivo(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;

            case VERSION_OTA:
                Log.i(getLocalClassName(), "Se recibe la version OTA del servidor");
                getVersionOtaDisponible(textoRecibido);
                desactivarAnimacion();
                break;

            case CONSULTAR_PROGRAMACION:
                Log.i(getLocalClassName(), "Se recibe la programacion del dispositivo");
                procesarProgramasRecibidos(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;


            case ELIMINAR_PROGRAMACION:
                Log.i(getLocalClassName(), "Se recibe la respuesta de eliminar un programa");
                procesarEliminarPrograma(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;

            case NUEVA_PROGRAMACION:
                Log.i(getLocalClassName(), "Se recibe la respuesta de crear un programa");
                procesarNuevoPrograma(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;

            case MODIFICAR_PROGRAMACION:
                Log.i(getLocalClassName(), "Se recibe la respues para modificar un programa");
                procesarModificarPrograma(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case CONSULTAR_CONF_APP:
                Log.i(getLocalClassName(), "Se recibe la configuracion del dispositivo");
                procesarConfiguracionDispositivo(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case MODIFICAR_APP:
                Log.i(getLocalClassName(), "Se recibe la respuesta de la configuracion del dispositivo");
                procesarModicarApp(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;

            case RESET:
                Log.i(getLocalClassName(), "Se recibe la respuesta del reset");
                procesarReset(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case FACTORY_RESET:
                Log.i(getLocalClassName(), "Se recibe la respuesta del factory reset");
                procesarFactoryReset(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case UPGRADE_FIRMWARE:
                Log.i(getLocalClassName(), "Se recibe la respuesta del upgrade firmware");
                procesarUpgradeFirmware(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;


            default:
                Log.i(getLocalClassName(), "Mensaje no procesado por la activity " + idComando);
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cnx != null) {
            cnx.cerrarConexion();
            cnx = null;
        }

    }

    private void actualizarDispositivo() {

        if (cnx != null) {
            cnx.subscribirTopic(topicRespuestaOta);
            if (versionComprobada == false) comprobarVersionDispositivo();
            cnx.subscribirTopic(dispositivo.getTopicSubscripcion());
            //enviarComandoAlDispositivo(ESTADO, 10000, 1000);
            activarAnimacion();
            dialogo.enviarComando(dispositivo, dialogo.comandoEstadoDispositivo());
            actualizarProgramasDispositivo();
            //activarAnimacion();
            //dialogo.enviarComando(dispositivo,dialogo.escribirComandoEstadoConfiguracionTermostato() );

        }
    }
    
     private void activarAnimacion() {
         animacionInterruptor.setVisibility(View.VISIBLE);

    }
    
    private void desactivarAnimacion() {

        animacionInterruptor.setVisibility(View.INVISIBLE);
    }

    void comprobarVersionDispositivo() {

        //dialogo.enviarComando(topicPeticionOta,dialogo.escribirComandoVersionOtaDisponible(TIPO_DISPOSITIVO_IOT.INTERRUPTOR), dispositivo.idDispositivo );
        Log.i(getLocalClassName(), "comprobarVersionDispositivo: pidiendo la version OTA...");
        activarAnimacion();
        cnx.publicarTopic(topicPeticionOta, dialogo.escribirComandoVersionOtaDisponible(TIPO_DISPOSITIVO_IOT.INTERRUPTOR));


    }






    private void actualizarProgramasDispositivo() {
        activarAnimacion();
        dialogo.enviarComando(dispositivo,dialogo.escribirComandoConsultarProgramacion() );

    }




    private void actualizarEstadoDispositivo(String textoRecibido) {

        ESTADO_RELE estado;
        barra.setVisibility(View.INVISIBLE);
        Log.i(getLocalClassName().toString(), "Actualizando estado por mqtt");
        dispositivo.setProgramaActivo(dialogo.getProgramaActivo(textoRecibido));
        dispositivo.setEstadoRele(dialogo.getEstadoRele(textoRecibido));
        dispositivo.setVersionOta(dialogo.getOtaVersion(textoRecibido));
        //Actualizamos el programa en curso cuando hay programas.
        if (dispositivo.programas != null) {
            dispositivo.actualizarProgramaActivo(dialogo.getProgramaActivo(textoRecibido));
            programasAdapter.notifyDataSetChanged();

        }

        //dispositivo.setProgramaActivo(dialogo.getProgramaActivo(textoRecibido));
        Log.i(getLocalClassName().toString(), "Actualizando el pandel con la info mqtt");
        Log.i(getLocalClassName().toString(), "Version ota:" + dispositivo.getOtaVersion());
        actualizarPanelInterruptor(dispositivo);
        panel.hayNuevaVersionDisponible( dispositivo);

    }

    private void getVersionOtaDisponible(String textoRecibido) {


        if (dispositivo.datosOta.setDatosOtaDispositivo(textoRecibido) == true) {

            Log.i(getLocalClassName(),"Datos OTA extraidos del servidor de OTA");
        } else {
            Log.e(getLocalClassName(), "Error al extraer los datos OTA del servidor OTA");
        }

    }


    private void procesarEliminarPrograma(String textoRecibido) {
        int indice;
        ProgramaDispositivoIotOnOff programa;
        String idPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
        indice = dispositivo.buscarPrograma(idPrograma);
        programa = dispositivo.programas.get(indice);
        //programasAdapter.remove(programa);
        dispositivo.eliminarPrograma(idPrograma);
        int i;
        for(i=0;i<dispositivo.programas.size();i++) {
            Log.i(getLocalClassName(), "idProgramadispo:" + dispositivo.programas.get(i).getIdProgramacion());
            Log.i(getLocalClassName(), "idProgramaAdapt:" + programasAdapter.getItem(i).getIdProgramacion());
        }
        programasAdapter.notifyDataSetChanged();


    }

    private void procesarNuevoPrograma(String textoRecibido) {
        Log.i(getLocalClassName(), "Se procesa el texto del nuevo programa");
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoConsultarProgramacion());

    }

    private void procesarModificarPrograma(String textoRecibido) {

        String idPrograma;
        String idNuevoPrograma;
        String estadoPrograma;
        String idProgramaActivo;
        int estadoRele;
        ESTADO_RELE rele = ESTADO_RELE.INDETERMINADO;
        ESTADO_PROGRAMA prog = ESTADO_PROGRAMA.PROGRAMA_DESCONOCIDO;
        String estado;
        int duracion;
        Log.i(getLocalClassName(), "Se procesa el texto del programa modificado");
        idNuevoPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.NUEVO_ID_PROGRAMA.getValorTextoJson());
        idPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
        idProgramaActivo = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.PROGRAMA_ACTIVO.getValorTextoJson());
        estadoPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson());
        estadoRele = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        duracion = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson());
        if(duracion == -1000) duracion = 0;
        dispositivo.modificarPrograma(idPrograma, idNuevoPrograma, estadoPrograma, String.valueOf(estadoRele), duracion );
        dispositivo.actualizarProgramaActivo(idProgramaActivo);
        programasAdapter.notifyDataSetChanged();

    }

    private void procesarConfiguracionDispositivo(String textoRecibido) {


        JSONObject objeto = null;
        JSONArray nombres = null;
        String datoString = null;
        String etiqueta = null;
        String dato = null;
        int datoInt = -1;
        long datoLong = -1;
        Boolean datoBool;
        Double datoDouble;
        int i;
        ArrayList<String> lista;
        ListView cajaNombre = null;

        lista = new ArrayList<String>();

        try {
            objeto = new JSONObject(textoRecibido);
            //objeto = dialogo.obtenerparteJson(textoRecibido, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);
            nombres = new JSONArray();
            nombres = objeto.names();



        } catch (JSONException e) {
            e.printStackTrace();
        }

        lista.add("idDispositivo : " + dispositivo.getIdDispositivo());
        lista.add("Version : " + dispositivo.getVersionOta());
        for(i=0;i< nombres.length();i++) {
            if (i==0) continue;

            etiqueta = nombres.optString(i);
            try {
                datoString = objeto.getString(etiqueta);
                dato = etiqueta + " : " + datoString;

                Log.w(getLocalClassName(), etiqueta + " : "  +datoString);
            } catch (JSONException e) {
                try {
                    datoInt = objeto.getInt(etiqueta);
                    Log.w(getLocalClassName(), etiqueta + " : "  + datoInt);
                    dato = etiqueta + " : " + datoInt;
                } catch (JSONException ex) {
                    try {
                        datoLong = objeto.getLong(etiqueta);
                        dato = etiqueta + " : " + datoLong;
                        Log.w(getLocalClassName(), etiqueta + " : "  + datoLong);
                    } catch (JSONException exc) {
                        try {
                            datoBool = objeto.getBoolean(etiqueta);
                            dato = etiqueta + " : " + datoBool;
                            Log.w(getLocalClassName(), etiqueta + " : "  + datoBool);
                        } catch (JSONException e1) {
                            try {
                                datoDouble = objeto.getDouble(etiqueta);
                                dato = etiqueta + " : " + datoDouble;
                                Log.w(getLocalClassName(), etiqueta + " : "  + datoDouble);

                            } catch (JSONException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }


            lista.add(dato);

        }



        AlertDialog.Builder caja;
        caja = new AlertDialog.Builder(this);
        caja.setTitle("Nombre : " + dispositivo.getNombreDispositivo());
        caja.setIcon(R.drawable.icono_aplicacion);



        View contenedorVentana = getLayoutInflater().inflate(R.layout.info_dispositivo_interruptor, null);
        caja.setView(contenedorVentana);

        cajaNombre = (ListView) contenedorVentana.findViewById(R.id.listaInfoDispositivo);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, lista);
        cajaNombre.setAdapter(adaptador);



        caja.show();




    }

    private void procesarModicarApp(String textoRecibido) {

    }

    private void procesarReset(String textoRecibido) {
        Log.i(getLocalClassName(), "Se esta procesando la respuesta de reset");

    }

    private void procesarFactoryReset(String textoRecibido) {
        Log.i(getLocalClassName(), "Se esta procesando la respuesta de Factory reset");

    }

    private void actualizarPanelInterruptor(dispositivoIotOnOff dispositivo) {


        switch (dispositivo.getEstadoRele()) {
            case ON:
                imageInterruptor.setImageResource(R.drawable.interruptor_encendido);
                break;
            case OFF:
                imageInterruptor.setImageResource(R.drawable.interruptor_apagado);
                break;
            case INDETERMINADO:
                break;
            default:
                break;
        }
    }

    private void actuarInterruptor() {


        switch ((ESTADO_RELE) imageInterruptor.getTag()) {

            case ON:
                activarAnimacion();
                dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.OFF) );
                //enviarComandoResetAlDispositivo(COMANDO_IOT.ACTUAR_RELE, ESTADO_RELE.OFF, 10000, 1000);
                imageInterruptor.setTag((ESTADO_RELE) ESTADO_RELE.OFF);
                break;
            case OFF:
                activarAnimacion();
                dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.ON));
                //enviarComandoResetAlDispositivo(COMANDO_IOT.ACTUAR_RELE, ESTADO_RELE.ON, 10000, 1000);
                imageInterruptor.setTag((ESTADO_RELE) ESTADO_RELE.ON);
                break;
            default:
                Log.e(getLocalClassName(), "Estado del rele no valido");
                break;

        }
    }

    private void procesarProgramasRecibidos(String textoRecibido) {
/*
        if (programasAdapter == null) {
            if (dispositivo.cargarProgramas(textoRecibido) == null) {
                return;
            }
            programasAdapter = new ProgramasInterruptorAdapter(this, R.layout.vista_programas_interruptor, dispositivo.programas, cnx, dispositivo);
            listaProg.setAdapter(programasAdapter);
            programasAdapter.notifyDataSetChanged();
        } else {
            programasAdapter.notifyDataSetChanged();
        }
*/




        if (dispositivo.cargarProgramas(textoRecibido) == null) return;

        if (programasAdapter == null) {
            programasAdapter = new ProgramasInterruptorAdapter(this, R.layout.vista_programas_interruptor, dispositivo.programas,cnx, dispositivo);
        }

        listaProg.setAdapter(programasAdapter);
        programasAdapter.notifyDataSetChanged();



    }

    private void crearNuevaProgramacion() {

        Intent intent = new Intent(getApplicationContext(), programador.class);
        intent.putExtra("TIPO_DISPOSITIVO", TIPO_DISPOSITIVO_IOT.INTERRUPTOR);
        startActivityForResult(intent, NUEVA_PROGRAMACION.getIdComando());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        String textoComando = null;
        textoComando = data.getDataString();
        switch (resultCode) {

            case 7: //NUEVA PROGRAMACION
                activarAnimacion();
                dialogo.enviarComando(dispositivo, textoComando);
                Log.i(getLocalClassName(), "Creando nuevo programa...");
                break;
            case 8: //MODIFICAR PROGRAMACION
                activarAnimacion();
                dialogo.enviarComando(dispositivo, textoComando);
                Log.i(getLocalClassName(), "Modificando programa...");
                break;
            case 12: //MODIFICAR_APP
                //textoComando = recibirDatosModificarApp(data);

                break;

            default:
                break;
        }


    }


    @Override
    public void onRefresh() {
        actualizarDispositivo();
        swipeRefreshLayoutListaPrograma.setRefreshing(false);

    }

    private void visualizarMenuSettings(final View view) {

        PopupMenu menuSettings = new PopupMenu(interruptor.this, view);
        menuSettings.inflate(R.menu.menu_operaciones_dispositivo);
        menuSettings.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menuReset:
                        Log.i(getLocalClassName(), "Has pulsado reset");
                        dial = new DialogosAplicacion();
                        dial.ventanaDialogo(view.getContext(), RESET, getString(R.string.reset), getString(R.string.avisoReset), R.drawable.reset);
                        dial.show(getSupportFragmentManager(), getString(R.string.reset));
                        break;
                    case R.id.menuFactoryReset:
                        Log.i(getLocalClassName(), "Has pulsado Factoryreset");
                        dial = new DialogosAplicacion();
                        dial.ventanaDialogo(view.getContext(), FACTORY_RESET, getString(R.string.factoryReset), getString(R.string.avisoFactoryReset), R.drawable.reset);
                        dial.show(getSupportFragmentManager(), getString(R.string.reset));
                        break;
                    case R.id.menuInfo:
                        Log.i(getLocalClassName(), "Has pulsado info");
                        activarAnimacion();
                        dialogo.enviarComando(dispositivo, dialogo.escribirComandoInfoApp());

                        break;

                }


                return true;
            }


        });
        menuSettings.show();
    }
    private void visualizarMenuPuntosSuspensivos(final View view) {

        PopupMenu menuSettings = new PopupMenu(interruptor.this, view);
        menuSettings.inflate(R.menu.menu_operaciones_dispositivo);
        menuSettings.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menuReset:
                        Log.i(getLocalClassName(), "Has pulsado reset");
                        dial = new DialogosAplicacion();
                        dial.ventanaDialogo(view.getContext(), RESET, getString(R.string.reset), getString(R.string.avisoReset), R.drawable.reset);
                        dial.show(getSupportFragmentManager(), getString(R.string.reset));
                        break;
                    case R.id.menuFactoryReset:
                        Log.i(getLocalClassName(), "Has pulsado Factoryreset");
                        dial = new DialogosAplicacion();
                        dial.ventanaDialogo(view.getContext(), FACTORY_RESET, getString(R.string.factoryReset), getString(R.string.avisoFactoryReset), R.drawable.reset);
                        dial.show(getSupportFragmentManager(), getString(R.string.reset));
                        break;
                    case R.id.menuInfo:
                        Log.i(getLocalClassName(), "Has pulsado info");
                        activarAnimacion();
                        dialogo.enviarComando(dispositivo, dialogo.escribirComandoInfoApp());

                        break;
                    case R.id.menuUpgradeFirmware:
                        dial = new DialogosAplicacion();
                        Log.i(getLocalClassName(), "Has pulsado upgrade firmware");
                        dial.ventanaDialogo(view.getContext(), UPGRADE_FIRMWARE, getString(R.string.upgradeFirmware), getString(R.string.avisoUpgradeFirmware), R.drawable.reset);
                        dial.show(getSupportFragmentManager(), getString(R.string.upgradeFirmware));
                        break;

                    case R.id.menuCambiarNombre:
                        cambiarNombreDispositivo(dispositivo, interruptor.this);
                        break;
                }


                return true;
            }


        });
        menuSettings.show();
    }

    @Override
    public void OnAceptarListener(COMANDO_IOT idComando) {
        Log.i(getLocalClassName(), "Se ha pulsado aceptar");

        switch (idComando) {

            case RESET:
                ejecutarReset();
                break;
            case FACTORY_RESET:
                ejectuarFactoryReset();
                break;
            case CONSULTAR_CONF_APP:
                break;

            case UPGRADE_FIRMWARE:
                ejecutarUpgradeFirmware();
                break;
            default:
                break;

        }

    }

    @Override
    public void OnCancelarListener() {
        Log.i(getLocalClassName(), "Se ha pulsado cancelar");

    }

    private void ejecutarReset() {
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoReset());
        Log.i(getLocalClassName(), "Comando reset enviado");
    }

    private void ejectuarFactoryReset() {
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoFactoryReset());
    }

    private void ejecutarUpgradeFirmware() {
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoUpgradeFirmware(dispositivo));

    }

    private void procesarUpgradeFirmware(String textoRecibido) {

        handler = new Handler();
        barra.setVisibility(View.VISIBLE);
        barra.setProgress(0);
        contador = 0;

        panel.escribirMensajePanel(R.drawable.upgrade, "Iniciando actualizacion");
        new Thread(new Runnable() {
            @Override
            public void run() {

                contador = barra.getProgress();
                while (contador < 120) {
                    contador++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            barra.setProgress(contador);
                            if (contador == 120) {
                                barra.setVisibility(View.INVISIBLE);
                                panel.escribirMensajePanel(R.drawable.warning, "Reinciando...");


                            }
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }


        }).start();

        Log.i(getLocalClassName(), "Procesando upgrade firmware");
    }

    private void cambiarNombreDispositivo(dispositivoIotOnOff dispositivo, Context contexto) {

        View ventana = getLayoutInflater().inflate(R.layout.cambiar_nombre, null);
        //View ventana = inflater.inflate(R.layout.cambiar_nombre, null);
        AlertDialog.Builder caja;
        EditText cajaNombre = (EditText) ventana.findViewById(R.id.nuevoNombre);
        cajaNombre.setText(dispositivo.getNombreDispositivo());
        caja = new AlertDialog.Builder(contexto);
        caja.setTitle("Cambiar nombre");
        caja.setIcon(R.drawable.icono_aplicacion);
        caja.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispositivo.setNombreDispositivo(cajaNombre.getText().toString());
                dispositivo.modificarDispositivo(dispositivo, contexto);

            }
        });

        caja.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        caja.setView(ventana);


        caja.show();

    }

    private void procesarInformeEspontaneo(String textoRecibido) {

        ESPONTANEO_IOT tipoInformeEspontaneo;

        tipoInformeEspontaneo = dialogo.descubrirTipoInformeEspontaneo(textoRecibido);
        switch (tipoInformeEspontaneo) {
            case ARRANQUE_APLICACION:
                actualizarEstadoDispositivo(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case CAMBIO_DE_PROGRAMA:
                actualizarEstadoDispositivo(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case COMANDO_APLICACION:
                break;
            case ACTUACION_RELE_LOCAL:
                actualizarEstadoDispositivo(textoRecibido);
                dialogo.eliminarTemporizador(dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.CLAVE.getValorTextoJson()));
                desactivarAnimacion();
                break;
            case ACTUACION_RELE_REMOTO:
                break;
            case UPGRADE_FIRMWARE_FOTA:
                procesarMensajesOta(textoRecibido);
                break;
            default:
                break;
        }

        Log.i(getLocalClassName(), "Tipo de informe espontaneo " + tipoInformeEspontaneo.toString());



    }

    private void procesarMensajesOta(String textoRecibido) {

        OTA_IOT tipoInforme;

        tipoInforme = dialogo.tipoInformeOta(textoRecibido);

        switch (tipoInforme) {
            case OTA_ERROR:
                panel.escribirMensajePanel(R.drawable.warning, "Error al iniciar OTA" );
                break;
            case OTA_CRC_ERRONEO:
                panel.escribirMensajePanel(R.drawable.warning, "Fichero origen erroneo" );
                break;
            case OTA_ERROR_MEMORIA:
                panel.escribirMensajePanel(R.drawable.warning, "Error de memoria" );
                break;
            case OTA_FALLO_CONEXION:
                panel.escribirMensajePanel(R.drawable.warning, "Error de conexion" );
                break;
            case OTA_DATOS_CORRUPTOS:
                panel.escribirMensajePanel(R.drawable.warning, "Datos corruptos" );
                break;
            case OTA_BORRANDO_SECTORES:
                panel.escribirMensajePanel(R.drawable.upgrade, "Borrando sectores..." );
                break;
            case OTA_COPIANDO_SECTORES:
                panel.escribirMensajePanel(R.drawable.upgrade, "Copiando sectores." );
                break;
            case OTA_DESCARGA_FIRMWARE:
                panel.escribirMensajePanel(R.drawable.upgrade, "Descargando firmware..." );
                break;
            case OTA_PAQUETES_ERRONEOS:
                panel.escribirMensajePanel(R.drawable.warning, "Error en la descarga" );
                break;
            case OTA_UPGRADE_FINALIZADO:
                panel.escribirMensajePanel(R.drawable.upgrade, "Actualizacoin finalizada. Esperando reinicio..." );
                break;

        }



        Log.e(getLocalClassName(), "Mensaje OTA: " + tipoInforme.toString().toLowerCase());

    }


}
