package com.iotcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import static com.iotcontrol.COMANDO_IOT.CONSULTAR_CONF_APP;
import static com.iotcontrol.COMANDO_IOT.CONSULTAR_PROGRAMACION;
import static com.iotcontrol.COMANDO_IOT.ESTADO;
import static com.iotcontrol.COMANDO_IOT.FACTORY_RESET;
import static com.iotcontrol.COMANDO_IOT.NUEVA_PROGRAMACION;
import static com.iotcontrol.COMANDO_IOT.RESET;
import static com.iotcontrol.COMANDO_IOT.UPGRADE_FIRMWARE;

public class cronotermostato extends AppCompatActivity implements Serializable, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener,  DialogosAplicacion.OnDialogosAplicacion {

    dispositivoIotTermostato dispositivo;
    TextView textoUmbralParteEntera;
    TextView textoUmbralParteDecimal;
    TextView textoTemperaturaParteEntera;
    TextView textoTemperaturaParteDecimal;
    TextView textoHumedadPanel;
    ImageView imagenEstadoReleCronotermostato;
    ImageView imagenModoCronotermostato;
    ImageView iconoNotificacion;
    ImageView iconoEstadoBroker;
    TextClock reloj;
    ImageButton botonPuntosSuspensivos;
    TextView textoNotificacion;
    DialogosAplicacion dial;
    ProgressBar animacion;
    ProgressBar barra;
    conexionMqtt cnx;
    dialogoIot dialogo;
    final String topicPeticionOta = "OtaIotCronoTemp";
    final String topicRespuestaOta = "newVersionOtaIotCronoTemp";
    boolean versionComprobada = false;
    PanelNotificacion panel;
    ImageButton botonProgramacion;
    ImageButton botonSettings;
    ProgramasTermostatoAdapter programasAdapter = null;
    ListView listaProg;
    ImageButton botonMas;
    ImageButton botonMenos;
    ImageButton botonOk;
    final double INCREMENTO = 0.1;
    final double DECREMENTO = 0.1;
    final double INCREMENTO_LARGO = 0.5;
    final double DECREMENTO_LARGO = 0.5;
    final int COLOR_DEFECTO = Color.BLACK;
    final int COLOR_TRANSICION = Color.MAGENTA;
    SwipeRefreshLayout swipeRefreshLayoutListaProgramas;

    private Handler repetidor;
    boolean autoIncremento = false;
    boolean autoDecremento = false;


    private void actualizarDispositivo() {

        cnx.subscribirTopic(topicRespuestaOta);
        if (versionComprobada == false) comprobarVersionDispositivo();
        cnx.subscribirTopic(dispositivo.getTopicSubscripcion());
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.comandoEstadoDispositivo());
        activarAnimacion();
        actualizarProgramasDispositivo();

    }





    private void actualizarProgramasDispositivo() {


        dialogo.enviarComando(dispositivo, dialogo.escribirComandoConsultarProgramacion());
        activarAnimacion();

    }

    private void procesarProgramasRecibidos(String textoRecibido) {

        if (dispositivo.cargarProgramas(textoRecibido) == null) return;

        if (programasAdapter == null) {
            programasAdapter = new ProgramasTermostatoAdapter(this, R.layout.vista_programas_termostato, dispositivo.programas,cnx, dispositivo);
        }

        listaProg.setAdapter(programasAdapter);
        programasAdapter.notifyDataSetChanged();



    }



    private void actualizarEstadoDispositivo(String textoRecibido) {


        //barra.setVisibility(View.INVISIBLE);
        Log.i(getLocalClassName().toString(), "Actualizando estado por mqtt");
        dispositivo.setEstadoDispositivo(dialogo.getEstadoDispositivo(textoRecibido));
        dispositivo.setProgramaActivo(dialogo.getProgramaActivo(textoRecibido));
        dispositivo.setEstadoRele(dialogo.getEstadoRele(textoRecibido));
        dispositivo.setTemperatura(dialogo.getTemperatura(textoRecibido));
        dispositivo.setUmbralTemperatura(dialogo.getUmbralTemperatura(textoRecibido));
        dispositivo.setHumedad(dialogo.getHumedad(textoRecibido));
        dispositivo.setVersionOta(dialogo.getOtaVersion(textoRecibido));
        if (dispositivo.programas != null) {
            dispositivo.actualizarProgramaActivo(dialogo.getProgramaActivo(textoRecibido));
            programasAdapter.notifyDataSetChanged();

        }

        Log.i(getLocalClassName().toString(), "Actualizando el pandel con la info mqtt");
        Log.i(getLocalClassName().toString(), "Version ota:" + dispositivo.getOtaVersion());
        actualizarPanelCronotermostato(dispositivo);
        panel.hayNuevaVersionDisponible(dispositivo);
        actualizarConfiguracionDispositivo(textoRecibido);
        Log.i(getLocalClassName(), "estado actualizado");
        }


    private void getVersionOtaDisponible(String textoRecibido) {


        if (dispositivo.datosOta.setDatosOtaDispositivo(textoRecibido) == true) {

            Log.i(getLocalClassName(),"Datos OTA extraidos del servidor de OTA");
        } else {
            Log.e(getLocalClassName(), "Error al extraer los datos OTA del servidor OTA");
        }



    }

    void procesarNuevoPrograma (String textoRecibido) {

        Log.i(getLocalClassName(), "Se procesa el texto del nuevo programa");
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoConsultarProgramacion());
       // enviarComandoAlDispositivo(CONSULTAR_PROGRAMACION, 10000, 1000);
        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.escribirComandoConsultarProgramacion());
    }

    void procesarModificarPrograma(String textoRecibido) {

        String idNuevoPrograma;
        String idProgramaActivo;
        String idPrograma;
        String estadoPrograma;
        double umbral;
        Log.i(getLocalClassName(), "Se procesa el texto del programa modificado");
        idNuevoPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.NUEVO_ID_PROGRAMA.getValorTextoJson());
        idPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
        idProgramaActivo = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.PROGRAMA_ACTIVO.getValorTextoJson());
        estadoPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson());
        umbral = dialogo.getUmbralTemperatura(textoRecibido);
        dispositivo.modificarPrograma(idPrograma, idNuevoPrograma, estadoPrograma, umbral);
        dispositivo.actualizarProgramaActivo(idProgramaActivo);






        //programasAdapter.clear();
        programasAdapter.notifyDataSetChanged();
        //activarAnimacion();
        //dialogo.enviarComando(dispositivo, dialogo.escribirComandoConsultarProgramacion());
        //enviarComandoAlDispositivo(CONSULTAR_PROGRAMACION, 10000, 1000);
       // //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.escribirComandoConsultarProgramacion());


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



        //return caja;
        caja.show();




    }


    void procesarModicarApp(String textoRecibido) {

        Log.i(getLocalClassName(), "Se esta procesando la respuesta de modificarApp");
    }

    void procesarReset(String textoRexibido) {

        panel.escribirMensajePanel(R.drawable.ready, "procesado reset, en linea en 5 sg...");

        Log.i(getLocalClassName(), "Se esta procesando la respuesta de reset");

    }

    void procesarFactoryReset(String textoRecibido) {
        Log.i(getLocalClassName(), "Se esta procesando la resputesta de factory reset");


    }

    private void procesarMensajeRecibido(MqttMessage mensajeRecibido) {

        COMANDO_IOT idComando;
        dialogoIot dialogo = new dialogoIot();
        String textoRecibido = new String(mensajeRecibido.getPayload());
        Log.i(getClass().toString(), "mensaje: " + textoRecibido);
        idComando = dialogo.descubrirComando(textoRecibido);
        Log.i(getClass().toString(), "idComando:" + idComando.toString());


        Log.i(getLocalClassName(), "idComando: " + idComando);
        switch (idComando) {


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

            case MODIFICAR_UMBRAL_TEMPERATURA:
                Log.i(getLocalClassName(), "Se recibe la respuesta a la modficacion de umbral de temperatura");
                procesarModificacionUmbralTemperatura(textoRecibido);
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
        actualizarIconoEstadoDispositivo();
    }

    private void procesarEliminarPrograma(String texto) {

        int indice;
        ProgramaDispositivoIotTermostato programa;
        String idPrograma = dialogo.extraerDatoJsonString(texto, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
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


    private void procesarModificacionUmbralTemperatura(String texto) {

        Log.i(getLocalClassName(), "Procesando respuesta de modificaion de umbral");

        if (dialogo.isRespuestaCorrecta(texto) == true) {
            textoUmbralParteEntera.setTextColor(COLOR_DEFECTO);
            textoUmbralParteDecimal.setTextColor(COLOR_DEFECTO);
            textoNotificacion.setText("Umbral de temperatura modificado.");
        } else {
            Log.e(getLocalClassName(), "Error en la modificacion del umbral de temperatura");

        }
        dialogo.enviarComando(dispositivo, dialogo.comandoEstadoDispositivo());
        //enviarComandoAlDispositivo(ESTADO, 10000, 1000);
        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoEstadoDispositivo());


    }
    private boolean crearConexionMqtt() {

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
        cnx.establecerConexionMqtt(getApplicationContext());

        return cnx.cliente.isConnected();


    }



    void presentarProgramasDispositivo() {

    }


    void comprobarVersionDispositivo() {


        activarAnimacion();
        cnx.publicarTopic(topicPeticionOta, dialogo.escribirComandoVersionOtaDisponible(TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO));
        panel.escribirMensajePanel(R.drawable.schedule, "Consultando version OTA...");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cronotermostato);

        capturarControles();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        repetidor = new Handler();

        dialogo = new dialogoIot();
        dialogo.setOnTemporizacionVencidaEnComando(new dialogoIot.onDialogoIot() {
            @Override
            public void temporizacionVencidaEnComando(String idDispositivo, String clave, COMANDO_IOT comando) {
                Log.e(getLocalClassName(), "Temporizacion vencida en comando!!!");

            }
        });
        panel = new PanelNotificacion();
        panel.setIconoNotificacion(iconoNotificacion);
        panel.setTextoNotificacion(textoNotificacion);
        dial = new DialogosAplicacion();


        if (bundle != null) {

            dispositivo = new dispositivoIotTermostato();
            dispositivo.nombreDispositivo = (String) bundle.get(TEXTOS_DIALOGO_IOT.NOMBRE_DISPOSITIVO.getValorTextoJson());
            dispositivo.estadoConexion = (ESTADO_CONEXION_IOT) bundle.get(TEXTOS_DIALOGO_IOT.ESTADO_CONEXION.getValorTextoJson());
            dispositivo.estadoRele = (ESTADO_RELE) bundle.get(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
            dispositivo.temperatura = (double) bundle.get(TEXTOS_DIALOGO_IOT.TEMPERATURA.getValorTextoJson());
            dispositivo.umbralTemperatura = (double) bundle.get(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson());
            dispositivo.humedad = (double) bundle.get(TEXTOS_DIALOGO_IOT.HUMEDAD.getValorTextoJson());
            dispositivo.topicSubscripcion = (String) bundle.get(TEXTOS_DIALOGO_IOT.TOPIC_SUBSCRICION.getValorTextoJson());
            dispositivo.topicPublicacion = (String) bundle.get(TEXTOS_DIALOGO_IOT.TOPIC_PUBLICACION.getValorTextoJson());
            dispositivo.idDispositivo = (String) bundle.get(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
            //Acciones que debe hacer el cronotermostato.
            // 1.-
            /**
             * Acciones que debe hacer el cronotermostato.
             * 1.- Conectarse al broker
             * 2.- Comprobar si hay una version nueva
             * 3.- Actualizar el estado del dispositivo enviando un status
             * 4.- Pintar los programas del dispositivo.
             */
            crearConexionMqtt();
            actualizarPanelCronotermostato(dispositivo);




        }
    }

    void actualizarPanelCronotermostato(dispositivoIotTermostato dispositivo) {

        //Pintamos el umbral
        BigDecimal umbral = new BigDecimal(dispositivo.umbralTemperatura);
        umbral = umbral.setScale(1, BigDecimal.ROUND_HALF_UP);
        int entero = umbral.intValue();
        BigDecimal decimal = umbral.remainder(BigDecimal.ONE);
        BigDecimal diez = new BigDecimal(10);
        decimal = decimal.multiply(diez);
        int decimales = decimal.intValue();
        textoUmbralParteEntera.setText(String.valueOf(entero) + ".");
        textoUmbralParteDecimal.setText(String.valueOf(decimales) + " ");

        // Pintamos la temperatura
        BigDecimal temperatura = new BigDecimal(dispositivo.temperatura);
        temperatura = temperatura.setScale(1, BigDecimal.ROUND_HALF_UP);
        entero = temperatura.intValue();
        decimal = temperatura.remainder(BigDecimal.ONE);
        decimal = decimal.multiply(diez);
        decimales = decimal.intValue();
        textoTemperaturaParteEntera.setText(String.valueOf(entero) + ".");
        textoTemperaturaParteDecimal.setText(String.valueOf(decimales) + " ");
        // Pintamos la humedad
        textoHumedadPanel.setText(String.valueOf(dispositivo.humedad));

        switch (dispositivo.estadoRele) {
            case ON:
                imagenEstadoReleCronotermostato.setVisibility(View.VISIBLE);
                imagenEstadoReleCronotermostato.setImageResource(R.drawable.heatingicon);
                break;
            case OFF:
                imagenEstadoReleCronotermostato.setVisibility(View.INVISIBLE);
                break;
            case INDETERMINADO:
                break;
                default:
                    break;
        }




    }

    void  capturarControles() {



        botonPuntosSuspensivos = (ImageButton) findViewById(R.id.botonPuntosSuspensivos);
        botonPuntosSuspensivos.setOnClickListener(this);
        //registerForContextMenu(botonPuntosSuspensivos);
        animacion = (ProgressBar) findViewById(R.id.animacion);
        barra = (ProgressBar) findViewById(R.id.barra);
        botonSettings = (ImageButton) findViewById(R.id.botonSettings);
        botonSettings.setOnClickListener(this);
        textoUmbralParteEntera = (TextView) findViewById(R.id.textoUmbralParteEntera);
        textoUmbralParteDecimal = (TextView) findViewById(R.id.textoUmbralParteDecimal);
        textoTemperaturaParteEntera = (TextView) findViewById(R.id.textoTemperaturaParteEntera);
        textoTemperaturaParteDecimal = (TextView) findViewById(R.id.textoTemperaturaParteDecimal);
        textoHumedadPanel = (TextView) findViewById(R.id.textoHumedadPanel);
        imagenEstadoReleCronotermostato = (ImageView) findViewById(R.id.imagenEstadoReleCronotermostato);
        imagenModoCronotermostato = (ImageView) findViewById(R.id.imagenModoCronotermostato);
        iconoNotificacion = (ImageView) findViewById(R.id.iconoNotificacion);
        iconoEstadoBroker = (ImageView) findViewById(R.id.iconoEstadoMqtt);
        textoNotificacion = (TextView) findViewById(R.id.textoNotificacion);
        botonProgramacion = (ImageButton) findViewById(R.id.botonProgramacion);
        botonProgramacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), programador.class);
                intent.putExtra("TIPO_DISPOSITIVO", TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO);
                startActivityForResult(intent, NUEVA_PROGRAMACION.getIdComando());

            }
        });

        swipeRefreshLayoutListaProgramas = (SwipeRefreshLayout) findViewById(R.id.swipeListaProgramas);
        swipeRefreshLayoutListaProgramas.setOnRefreshListener(this);
        listaProg = (ListView) findViewById(R.id.listaProgr);
        listaProg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ProgramaDispositivoIotTermostato programa = programasAdapter.getItem(position);
                Intent intent = new Intent(cronotermostato.this, programador.class);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString(), programa);
                //intent.putExtras(bundle);
                intent.putExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString(), programa);
                intent.putExtra("TIPO_DISPOSITIVO", TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO);
                startActivityForResult(intent, COMANDO_IOT.MODIFICAR_PROGRAMACION.getIdComando());
            }
        });


        botonMenos = (ImageButton) findViewById(R.id.botonMenos);
        botonMenos.setOnLongClickListener(this);
        botonMenos.setOnTouchListener(this);
        /*
        botonMenos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                modificarUmbralEnPanel(false, DECREMENTO_LARGO);
                return true;
            }
        });*/
        botonMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarUmbralEnPanel(false, DECREMENTO);
            }
        });
        botonOk = (ImageButton) findViewById(R.id.botonOk);
        botonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getLocalClassName(), "Lanzamos modificacion de umbral de temperatura");
                activarAnimacion();
                dialogo.enviarComando(dispositivo, dialogo.comandoModificarUmbralTemperatura(dispositivo.umbralTemperatura));
                //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoModificarUmbralTemperatura(dispositivo.umbralTemperatura));
                panel.escribirMensajePanel(R.drawable.schedule, "Modificando umbral...");
            }
        });
        botonMas = (ImageButton) findViewById(R.id.botonMas);
        botonMas.setOnLongClickListener(this);
        botonMas.setOnTouchListener(this);
        /*
        botonMas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                modificarUmbralEnPanel(true, INCREMENTO_LARGO);



                return true;
            }
        });*/
        botonMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarUmbralEnPanel(true, INCREMENTO);

            }
        });


    }

    private void modificarUmbralEnPanel(boolean incdec, double valor) {

        double umbral = dispositivo.getUmbralTemperatura();
        if (incdec == true) {
            umbral = umbral + valor;
        } else {
            umbral = umbral - valor;
        }

        dispositivo.setUmbralTemperatura(umbral);
        textoUmbralParteDecimal.setTextColor(COLOR_TRANSICION);
        textoUmbralParteEntera.setTextColor(COLOR_TRANSICION);
        textoNotificacion.setText("Pulsa OK para enviar el cambio al dispositivo");
        actualizarPanelCronotermostato(dispositivo);


    }


    @Override
    public void onRefresh() {
        actualizarDispositivo();
        swipeRefreshLayoutListaProgramas.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        String textoComando = null;
        textoComando = data.getDataString();
        switch (resultCode) {

            case 7: //NUEVA PROGRAMACION
                Log.i(getLocalClassName(), "Creando nuevo programa...");
                activarAnimacion();
                dialogo.enviarComando(dispositivo, textoComando);
                break;
            case 8: //MODIFICAR PROGRAMACION
                Log.i(getLocalClassName(), "Modificando programa...");
                activarAnimacion();
                dialogo.enviarComando(dispositivo, textoComando);

                break;
            case 12: //MODIFICAR_APP
                textoComando = recibirDatosModificarApp(data);
                activarAnimacion();
                dialogo.enviarComando(dispositivo, textoComando);

                break;

                default:
                    break;
        }
        //if (textoComando != null) cnx.publicarTopic(dispositivo.getTopicPublicacion(), textoComando);
        //if (resultCode == 12) cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.escribirComandoReset());


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.botonSettings:

                Intent intent = new Intent(cronotermostato.this, configuracionTermostato.class);
                intent.putExtra(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson(), dispositivo.getMargenTemperatura());
                intent.putExtra(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson(), dispositivo.getIntervaloLectura());
                intent.putExtra(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson(), dispositivo.getReintentosLectura());
                intent.putExtra(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson(), dispositivo.getIntervaloReintentos());
                intent.putExtra(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson(), dispositivo.getValorCalibrado());
                startActivityForResult(intent,34);
                break;
            case R.id.botonPuntosSuspensivos:
                visualizarMenuPuntosSuspensivos(v);
                break;


        }
    }

    private String recibirDatosModificarApp(Intent data) {

        int datoEntero;
        double datoDoble;
        Bundle bundle;
        String textoComando = null;
        bundle = data.getExtras();
        datoDoble = (double) bundle.get(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson());
        dispositivo.setMargenTemperatura(datoDoble);
        datoDoble = (double) bundle.get(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson());
        dispositivo.setValorCalibrado(datoDoble);
        datoEntero = (int) bundle.get((TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson()));
        dispositivo.setIntervaloReintentos(datoEntero);
        datoEntero = (int) bundle.get(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson());
        dispositivo.setIntervaloLectura(datoEntero);
        datoEntero = (int) bundle.get(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson());
        dispositivo.setReintentoLectura(datoEntero);
        textoComando = dialogo.escribirComandoModficarParametrosApp(dispositivo);

        return textoComando;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_operaciones_dispositivo, menu);

        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_operaciones_dispositivo, menu);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //DialogosAplicacion dial;

        switch (item.getItemId()) {
            case R.id.menuReset:
                Log.i(getLocalClassName(), "Menu reset");
                ejecutarReset();
                break;

            case R.id.menuFactoryReset:
                Log.i(getLocalClassName(), "Menu reset");
                ejectuarFactoryReset();

                break;

            case R.id.menuInfo:
                Log.i(getLocalClassName(), "Menu info");
                ejecutarMenuInfo();
                break;

            case R.id.menuUpgradeFirmware:
                Log.i(getLocalClassName(), " Menu upgrade firmware");
                ejecutarUpgradeFirmware();
                break;

        }


        Log.i(getLocalClassName(), "paso");
        return super.onContextItemSelected(item);




    }

    private void ejecutarReset() {


        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoReset());
        //enviarComandoAlDispositivo(RESET, 50000, 1000);
        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.escribirComandoReset());
        Log.i(getLocalClassName(), "Comando reset enviado");
    }

    private void ejectuarFactoryReset() {
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoFactoryReset());
        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.escribirComandoFactoryReset());
    }

    private void ejecutarMenuInfo() {

        dialogo.enviarComando(dispositivo, dialogo.escribirComandoEstadoConfiguracionTermostato());
        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.escribirComandoEstadoConfiguracionTermostato());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cnx.cerrarConexion();
    }

    private void visualizarMenuPuntosSuspensivos(final View view) {

        PopupMenu menuSettings = new PopupMenu(this, view);
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

                }


                return true;
            }


        });
        menuSettings.show();
    }

    private void ejecutarUpgradeFirmware() {
        activarAnimacion();
        dialogo.enviarComando(dispositivo, dialogo.escribirComandoUpgradeFirmware(dispositivo));
        //enviarComandoAlDispositivo(UPGRADE_FIRMWARE, 120, 1);

    }

    private void procesarUpgradeFirmware(String textoRecibido) {

        Log.i(getLocalClassName(), "Procesando upgrade firmware");
    }


    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            case R.id.botonMas:
                autoIncremento = true;
                repetidor.post(new RepetirLong());
                break;
            case R.id.botonMenos:
                autoDecremento = true;
                repetidor.post(new RepetirLong());

                break;

        }


        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {

            case R.id.botonMas:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoIncremento = false;
                }
                break;
            case R.id.botonMenos:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoDecremento = false;
                }
                break;
        }
        return false;
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

    class RepetirLong implements Runnable {

        public void run() {
            if (autoIncremento) {
                repetidor.postDelayed(new RepetirLong(), 200);
                modificarUmbralEnPanel(true, INCREMENTO_LARGO);
                Log.i(getLocalClassName(), "Incrementando...");
            } else if (autoDecremento){
                repetidor.postDelayed(new RepetirLong(), 200);
                modificarUmbralEnPanel(false, DECREMENTO_LARGO);
                Log.i(getLocalClassName(), "Decrementando...");

            }

        }


        double presentarDecimales(double dato, int precision) {

            BigDecimal umbral = new BigDecimal(dato);
            umbral = umbral.setScale(precision, BigDecimal.ROUND_HALF_UP);
            return umbral.doubleValue();
        }
    }

    private void actualizarIconoEstadoDispositivo() {

        switch (dispositivo.getEstadoDispositivo()) {
            case NORMAL_AUTO:
                imagenModoCronotermostato.setImageResource(R.drawable.auto);
                break;
            case NORMAL_AUTOMAN:
                imagenModoCronotermostato.setImageResource(R.drawable.automan);
                break;
            case NORMAL_MANUAL:
                imagenModoCronotermostato.setImageResource(R.drawable.mano);
                break;
            case NORMAL_SIN_PROGRAMACION:
                imagenModoCronotermostato.setImageResource(R.drawable.noschedule);
                break;
            case INDETERMINADO:
                //imagenModoCronotermostato.setImageResource(R.drawable.warning);
                break;
            case NORMAL_ARRANCANDO:
                imagenModoCronotermostato.setImageResource(R.drawable.warning);
                break;
            default:
                //imagenModoCronotermostato.setImageResource(R.drawable.nook);
                break;
        }


    }

    private void activarAnimacion() {
        animacion.setVisibility(View.VISIBLE);

    }
    private void desactivarAnimacion() {

        animacion.setVisibility(View.INVISIBLE);
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
                //no se contempla actualmente que este tipo report se espontaneo, sino una respuesta a comando.
                break;
            case UPGRADE_FIRMWARE_FOTA:
                procesarMensajesOta(textoRecibido);
                break;
            case CAMBIO_TEMPERATURA:
                procesarCambioTemperatura(textoRecibido);
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

    void actualizarConfiguracionDispositivo(String textoRecibido) {

        JSONObject objeto;
        double datoDoble;
        int datoInt;
        try {
            objeto = new JSONObject(textoRecibido);
            datoDoble = dialogo.extraerDatoJsonDouble(textoRecibido, TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson());
            if (datoDoble != -1000) dispositivo.setMargenTemperatura(datoDoble);
            datoInt = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson());
            if (datoInt != -1000) dispositivo.setIntervaloLectura(datoInt);
            datoInt = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson());
            if (datoDoble != -1000) dispositivo.setIntervaloReintentos(datoInt);
            datoInt = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson());
            if (datoDoble != -1000) dispositivo.setReintentoLectura(datoInt);
            datoInt = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson());
            if (datoDoble != -1000) dispositivo.setValorCalibrado(datoInt);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getLocalClassName(), "Error al recoger los datos del dispositivo");
        }

        Log.i(getLocalClassName(), "datos de configuracion almacenados en el dispositivo");


    }

    protected void procesarCambioTemperatura(String textoRecibido) {

        actualizarPanelCronotermostato(dispositivo);
        dispositivo.setEstadoRele(dialogo.getEstadoRele(textoRecibido));
        ESTADO_PROGRAMACION estadoProgramacion = ESTADO_PROGRAMACION.INDETERMINADO;
        dispositivo.setEstadoProgramacion(estadoProgramacion.fromId(dialogo.getEstadoProgramacion(textoRecibido)));
        dispositivo.setEstadoDispositivo(dialogo.getEstadoDispositivo(textoRecibido));
        dispositivo.setTemperatura(dialogo.getTemperatura(textoRecibido));
        dispositivo.setUmbralTemperatura(dialogo.getUmbralTemperatura(textoRecibido));
        dispositivo.setHumedad(dialogo.getHumedad(textoRecibido));
        actualizarPanelCronotermostato(dispositivo);

    }

}
