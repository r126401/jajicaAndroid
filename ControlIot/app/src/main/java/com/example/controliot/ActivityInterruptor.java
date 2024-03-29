package com.example.controliot;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;

public class ActivityInterruptor extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Serializable {

    private ImageView imageUpgrade;
    private ImageView imageBotonOnOff;
    private ImageView imageEstadoBroker;
    private ListView listViewSchedule;
    private ImageView imageEstadoDispositivo;
    private BottomNavigationView bottommenuInterruptor;
    private String idDispositivo;
    private conexionMqtt cnx;
    private final String TAG = "ActivityInterruptor";
    private Context contexto;
    private dialogoIot dialogo;
    private ProgressBar barraProgreso;
    private TextView textConsolaMensajes;
    private CountDownTimer temporizador;
    private dispositivoIotOnOff dispositivo;
    private listaProgramasInterruptorAdapter programasInterruptorAdapter;
    private final String topicPeticionOta = "OtaIotOnOff";
    private final String topicRespuestaOta = "newVersionOtaIotOnOff";
    private boolean versionComprobada = false;
    private boolean nuevaVersionDisponible = false;
    private TextView textoProgramaDesde;
    private ProgressBar progresoPrograma;
    private TextView textoProgramaHasta;
    private ConstraintLayout panelProgresoPrograma;
    private ProgressBar progressUpdate;
    CountDownTimer contador;





    private void registrarControles() {

        progressUpdate = (ProgressBar) findViewById(R.id.progress_update);
        panelProgresoPrograma = (ConstraintLayout) findViewById(R.id.panelProgresoPrograma);
        textoProgramaDesde = (TextView) findViewById(R.id.programa_desde);
        textoProgramaHasta = (TextView) findViewById(R.id.programa_hasta);
        progresoPrograma = (ProgressBar) findViewById(R.id.progreso_programa);
        imageEstadoDispositivo = (ImageView) findViewById(R.id.imageEstadoDispositivo);
        imageBotonOnOff = (ImageView) findViewById(R.id.imageHeating);
        imageBotonOnOff.setImageResource(R.drawable.switch_indeterminado);
        imageBotonOnOff.setOnClickListener(this);
        imageEstadoBroker = (ImageView) findViewById(R.id.imageEstadoBroker);
        listViewSchedule = (ListView) findViewById(R.id.listViewSchedule);
        bottommenuInterruptor = (BottomNavigationView) findViewById(R.id.bottommenuInterruptor);
        bottommenuInterruptor.setOnNavigationItemSelectedListener(this);
        barraProgreso = (ProgressBar) findViewById(R.id.barraProgreso);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes = (TextView) findViewById(R.id.textConsolaMensajes);
        imageUpgrade = (ImageView) findViewById(R.id.imageUpgrade);
        imageUpgrade.setVisibility(View.INVISIBLE);
        imageUpgrade.setOnClickListener(this);
        listViewSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "hola");
                lanzarActivityProgramaInterruptor(position, COMANDO_IOT.MODIFICAR_PROGRAMACION);
            }
        });
        listViewSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "hola");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "hola");
            }
        });
    }





    private void notificarBrokerConectado() {

        dispositivoDisponible("");
        cnx.subscribirTopic(dispositivo.getTopicSubscripcion());
        envioComando(dialogo.comandoEstadoDispositivo());
        actualizarProgramacion();
    }

    private void notificarBrokerDesconectado() {
        pintarDispositivoIndisponible();
    }

    private void notificarBrokerReintentoConexion() {
        pintarDispositivoIndisponible();
        barraProgreso.setVisibility(View.VISIBLE);
    }

    private void crearConexion() {
        cnx = new conexionMqtt(getApplicationContext(), dialogo);
        cnx.setOnRecibirMensajes(new conexionMqtt.OnRecibirMensaje() {
            @Override
            public void recibirMensaje() {
                pararAnimacionComando();
                dispositivoDisponible(dispositivo.getIdDispositivo() + " disponible");
            }
        });
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
            public void temporizacionVencidaEnComando(COMANDO_IOT comando, String clave, String idDispositivo) {

                dispositivoIndisponible();

            }
        });




        configuracionDispositivos listaDispositivos;
        listaDispositivos = new configuracionDispositivos();
        listaDispositivos.leerDispositivos(contexto);
        disp = listaDispositivos.getDispositivoPorId(idDispositivo);
        dispositivo = new dispositivoIotOnOff(disp);
        crearConexion();
        /*
        cnx = new conexionMqtt(getApplicationContext(), dialogo);
        cnx.setOnRecibirMensajes(new conexionMqtt.OnRecibirMensaje() {
            @Override
            public void recibirMensaje() {
                pararAnimacionComando();
                dispositivoDisponible();
            }
        });
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


         */





    }

    private void subscribirDispositivo() {

        dispositivo.setTopicSubscripcion(dispositivo.getTopicSubscripcion());
    }

    private void subscribirTopicOta() {
        cnx.subscribirTopic(topicRespuestaOta);
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

        dispositivoDisponible(dispositivo.getIdDispositivo() + " disponible");

        this.dispositivo.setEstadoRele(dispositivo.getEstadoRele());
        this.dispositivo.setTipoDispositivo(dispositivo.getTipoDispositivo());
        this.dispositivo.setVersionOta(dispositivo.getVersionOta());
        this.dispositivo.setProgramaActivo(dispositivo.getProgramaActivo());
        this.dispositivo.setFinUpgrade(dispositivo.getFinUpgrade());
        notificarFinUpgrade();
        actualizarProgramaEnCurso(this.dispositivo.getProgramaActivo());
        actualizarEstadoRele(dispositivo);



        if (versionComprobada == false) {
            consultarNuevaVersionOta();
        }




    }

    private void ventanaConfirmacionComando(int icono, String texto, COMANDO_IOT idComando, Context contexto) {
        AlertDialog.Builder ventana;
        ventana = new AlertDialog.Builder(contexto);
        ventana.setIcon(icono);
        ventana.setTitle(texto);

        switch (idComando) {
            case RESET:
                ventana.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        envioComando(dialogo.escribirComandoReset());
                    }
                });
                ventana.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case FACTORY_RESET:
                ventana.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        envioComando(dialogo.escribirComandoFactoryReset());
                    }
                });
                ventana.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case UPGRADE_FIRMWARE:
                ventana.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        envioComando(dialogo.escribirComandoUpgradeFirmware(dispositivo));
                    }
                });
                ventana.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                break;


            default:
                throw new IllegalStateException("Unexpected value: " + idComando);
        }


        ventana.show();
        Log.i(TAG, "boton upgrade");

    }


    private void ejecutarMenuMas(PopupMenu menu, Context contexto) {
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {





                switch (item.getItemId()) {
                    case (R.id.reset):
                        ventanaConfirmacionComando(R.drawable.ic_reset,"vas a reiniciar el dispositivo...", COMANDO_IOT.RESET, contexto );
                         break;
                    case (R.id.factoryReset):
                        ventanaConfirmacionComando(R.drawable.ic_factory_reset, "Todas las configuraciones se borraran incluida la configuracion wifi", COMANDO_IOT.FACTORY_RESET, contexto);


                        break;
                    default:
                    case (R.id.upgrade):
                        ventanaConfirmacionComando(R.drawable.ic_upgrade, "Se va a actualizar el dispositivo. Pulsa Aceptar para continuar", COMANDO_IOT.UPGRADE_FIRMWARE, contexto);

                        break;


                }

                return false;
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()) {

            case (R.id.itemInfo):
                envioComando(dialogo.escribirComandoInfoApp());
                break;
            case(R.id.itemConfiguracion):
                break;
            case(R.id.itemNuevoProgramaInterruptor):
                lanzarActivityProgramaInterruptor(0, COMANDO_IOT.NUEVA_PROGRAMACION);
                break;
            case(R.id.itemmasInterruptor):
                 PopupMenu menumas = new PopupMenu(ActivityInterruptor.this, bottommenuInterruptor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    menumas.setForceShowIcon(true);
                }
                menumas.inflate(R.menu.menu_mas_opciones);
                 menumas.setGravity(Gravity.RIGHT);
                 ejecutarMenuMas(menumas, this);
                 menumas.show();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
        }

        return false;
    }

    private void procesarUpgradeFirmware() {


        progressUpdate.setVisibility(View.VISIBLE);
        progressUpdate.setProgress(0);
        textConsolaMensajes.setText("Actualizando el dispositivo");
        contador = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                progressUpdate.setProgress(progressUpdate.getProgress() + 2);
                Log.i(TAG, "contador : " + progressUpdate.getProgress());

            }

            @Override
            public void onFinish() {
                if (dispositivo.getFinUpgrade() == -1000) {
                    dispositivoDisponible("Temporizacion vencida en upgrade");
                    progressUpdate.setVisibility(View.INVISIBLE);

                }


            }
        };
        contador.start();


        //Programar el control de progreso
        //Enseñar el control de progreso
        //Coundowntimer para actualizar el progreso



    }


    private void procesarMensajesInterruptor (){

        cnx.setOnProcesarMensajesInterruptor(new conexionMqtt.OnProcesarMensajesInterruptor() {
            @Override
            public void estadoInterruptor(String topic, String mensaje, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);
                Log.i(TAG, "Actualizando interruptor...");

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
            public void consultarProgramacionInterruptor(String topic, String texto, ArrayList<ProgramaDispositivoIotOnOff> programa) {

                Log.i(TAG, texto);
                dispositivo.setProgramasOnOff(programa);
                procesarProgramasRecibidos();


            }

            @Override
            public void nuevoProgramacionInterruptor(String topic, String texto, String idDispositivo) {
                Log.i(TAG, "Se recibe la informacion de la aplicacion");
                envioComando(dialogo.escribirComandoConsultarProgramacion());

            }

            @Override
            public void eliminarProgramacionInterruptor(String topic, String texto, String idDispositivo, String programa) {
                procesarEliminarPrograma(programa);

            }

            @Override
            public void modificarProgramacionInterruptor(String topic, String texto, String idDispositivo) {
                //envioComando(dialogo(dialogo dialogo.escribirComandoConsultarProgramacion());
                procesarModificarPrograma(texto);

                //envioComando(dialogo.escribirComandoConsultarProgramacion());

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

                procesarUpgradeFirmware();


            }

            @Override
            public void recibirVersionOtaDisponibleInterruptor(String topic, String texto, String idDispositivo, OtaVersion version) {

                Log.i(TAG, "Recibiendo version");
            }

            @Override
            public void informacionDispositivo(String topic, String texto) {
                Log.i(TAG, "recibida informacion");
                procesarInformacionDispositivo(texto);

            }

            @Override
            public void errorMensajeInterruptor(String topic, String mensaje) {

            }
        });

        cnx.setOnProcesarEspontaneosInterruptor(new conexionMqtt.OnProcesarEspontaneosInterruptor() {
            @Override
            public void arranqueAplicacionInterruptor(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);
            }

            @Override
            public void cambioPrograma(String topic, String texto, dispositivoIotOnOff dispositivo) {
                actualizarInterruptor(dispositivo);
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

            @Override
            public void releTemporizado(String topic, String texto) {

            }

            @Override
            public void alarmaDispositivo(String topic, String texto) {

            }
        });



        cnx.setOnProcesarVersionServidorOta(new conexionMqtt.OnProcesarVersionServidorOta() {
            @Override
            public void lastVersion(OtaVersion otaVersion) {
                long versionDispositivo;
                long versionDisponible;
                dispositivo.setDatosOta(otaVersion);
                versionDispositivo = Long.valueOf(dispositivo.getOtaVersion());
                versionDisponible =  Long.valueOf(dispositivo.getDatosOta().getOtaVersionAvailable());
                if (versionDisponible > versionDispositivo) {
                    nuevaVersionDisponible = true;
                    imageUpgrade.setVisibility(View.VISIBLE);
                } else {
                    imageUpgrade.setVisibility(View.INVISIBLE);
                }


            }
        });
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageHeating:
                pintarEsperandoComando();
                if ((Boolean) imageBotonOnOff.getTag() == false) {
                    envioComando(dialogo.comandoActuarRele(ESTADO_RELE.ON));
                } else {
                    envioComando(dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                }

                break;
            case R.id.imageUpgrade:
                ventanaConfirmacionComando(R.drawable.ic_upgrade, "Se va a actualizar el dispositivo. Pulsa Aceptar para continuar", COMANDO_IOT.UPGRADE_FIRMWARE, this);
                break;


            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }




    private void procesarProgramasRecibidos() {

        programasInterruptorAdapter = new listaProgramasInterruptorAdapter(this, R.layout.vista_programas_interruptor, dispositivo.getProgramasOnOff(), cnx, dispositivo);
        listViewSchedule.setAdapter(programasInterruptorAdapter);
        programasInterruptorAdapter.notifyDataSetChanged();
        actualizarProgramaEnCurso(dispositivo.getProgramaActivo());

    }

    private void refrescarDispositivo() {
        envioComando(dialogo.comandoEstadoDispositivo());
        if (programasInterruptorAdapter != null) {
            if (programasInterruptorAdapter.listaProgramas != null) {
                programasInterruptorAdapter.clear();
                programasInterruptorAdapter = null;

            }

        }
        actualizarProgramacion();


    }




    private void actualizarProgramacion() {
        envioComando(dialogo.escribirComandoConsultarProgramacion());

    }

    private void consultarNuevaVersionOta() {

        subscribirTopicOta();
        cnx.publicarTopic(topicPeticionOta, dialogo.escribirComandoVersionOtaDisponible(TIPO_DISPOSITIVO_IOT.INTERRUPTOR));
        versionComprobada = true;
    }

    private void notificarNuevaVersionDisponible() {

    }

    private void procesarInformacionDispositivo(String texto) {

        JSONObject info;
        ArrayList<String> lista;
        ListView listaInfo;
        String fila;
        int i;
        JSONArray etiquetas;
        String nombre;
        String valor;


        lista = new ArrayList<String>();
        try {
            info = new JSONObject(texto);
            etiquetas = info.names();
            fila = "Nombre: " + dispositivo.getNombreDispositivo();
            lista.add(fila);
            for (i=0;i<etiquetas.length();i++) {
                nombre = etiquetas.getString(i);
                valor = String.valueOf(info.getString(nombre));
                fila = nombre + ": " + valor;
                lista.add(fila);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder caja;
        caja = new AlertDialog.Builder(this);

        caja.setTitle("Informacion del dispositivo");
        caja.setIcon(R.drawable.switch_on);
        View contenedor = getLayoutInflater().inflate(R.layout.info_dispositivo_interruptor, null);
        caja.setView(contenedor);
        listaInfo = (ListView) contenedor.findViewById(R.id.listaInfoDispositivo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, lista);
        listaInfo.setAdapter(adapter);
        caja.show();



    }

    private void procesarEliminarPrograma(String idPrograma) {
        dispositivo.eliminarPrograma(idPrograma);
        programasInterruptorAdapter.notifyDataSetChanged();
    }

    //Rutina para lanzar una activityForResult
    ActivityResultLauncher<Intent> lanzadorActivityProgramaInterruptor = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String textoComando = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos: " + textoComando);
                        //Introducimos loa logica para modificar la programacion.
                        cnx.publicarTopic(dispositivo.getTopicPublicacion(), textoComando);

                    } else {
                        Log.w(getLocalClassName(), "No se puede modificar la programacion");
                    }
                }
            }
    );

    private void lanzarActivityProgramaInterruptor(int posicion, COMANDO_IOT idComando) {

        ProgramaDispositivoIotOnOff programa;


        Intent lanzador = new Intent(ActivityInterruptor.this, ActivityProgramador.class);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), TIPO_DISPOSITIVO_IOT.INTERRUPTOR);
        //lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), dispositivo.idDispositivo);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson(), idComando);
        if (idComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            programa = programasInterruptorAdapter.listaProgramas.get(posicion);
            lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), programa);

        }
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), dispositivo.getProgramasOnOff());

        lanzadorActivityProgramaInterruptor.launch(lanzador);
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
        /*
        if (estadoRele == 0) {
            dispositivo.setEstadoRele(ESTADO_RELE.OFF);
        } else {
            dispositivo.setEstadoRele(ESTADO_RELE.ON);
        }
        actualizarEstadoRele(dispositivo);
        */
        duracion = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson());
        if(duracion == -1000) duracion = 0;
        dispositivo.modificarPrograma(idPrograma, idNuevoPrograma, estadoPrograma, String.valueOf(estadoRele), duracion );
        dispositivo.actualizarProgramaActivo(idProgramaActivo);
        programasInterruptorAdapter.notifyDataSetChanged();

    }

    private void iniciarAnimacionComando() {
        barraProgreso.setVisibility(View.VISIBLE);

    }
    
    private void pararAnimacionComando() {
        barraProgreso.setVisibility(View.INVISIBLE);
    }

    private void dispositivoDisponible(String mensaje)
    {
        imageEstadoDispositivo.setImageResource(R.drawable.dispositivo_disponible);
        imageEstadoBroker.setImageResource(R.drawable.bk_conectado);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes.setText(mensaje);
        textConsolaMensajes.setTextColor(Color.BLUE);
        imageEstadoBroker.setImageResource(R.drawable.bk_conectado);

    }

    private void dispositivoIndisponible() {
        imageEstadoDispositivo.setImageResource(R.drawable.dispositivo_indisponible);
        barraProgreso.setVisibility(View.INVISIBLE);
        imageBotonOnOff.setImageResource(R.drawable.switch_indeterminado);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes.setTextColor(Color.RED);
        textConsolaMensajes.setText(dispositivo.getIdDispositivo() + " no disponible");
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);

    }
    
    private void envioComando(String comando) {

        Log.w(TAG, "Enviando Comando " + comando);
        dialogo.enviarComando(dispositivo, comando);
        iniciarAnimacionComando();
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

    private void actualizarEstadoRele(dispositivoIotOnOff dispositivo) {

        if (dispositivo.getEstadoRele() == ESTADO_RELE.ON) {
            imageBotonOnOff.setImageResource(R.drawable.switch_on);
            imageBotonOnOff.setTag(true);

        } else {
            imageBotonOnOff.setImageResource(R.drawable.switch_off);
            imageBotonOnOff.setTag(false);
        }

    }

    private void actualizarProgramaEnCurso(String idPrograma) {
        int hora;
        int minuto;
        int i;
        int duracion;
        String hasta;
        ProgramaDispositivoIotOnOff programa;

        i = dispositivo.buscarPrograma(idPrograma);
        if ( i < 0 ) {
            panelProgresoPrograma.setVisibility(View.INVISIBLE);
            if (dispositivo.getProgramas() != null) {
                for(i=0;i<dispositivo.getProgramasOnOff().size();i++) {
                    dispositivo.getProgramasOnOff().get(i).setProgramaEnCurso(false);
                }
            }

            return;
        } else {
            panelProgresoPrograma.setVisibility(View.VISIBLE);

        }
        programa = dispositivo.getProgramasOnOff().get(i);
        hasta = duracionAfecha(formatearHora(programa.getHora(), programa.getMinuto()), programa.getDuracion());
        hora = programa.getHora();
        minuto = programa.getMinuto();
        textoProgramaDesde.setText(formatearHora(hora, minuto));
        textoProgramaHasta.setText(hasta);
        duracion = restarHoras(textoProgramaDesde.getText().toString(), textoProgramaHasta.getText().toString());
        dispositivo.getProgramasOnOff().get(i).setProgramaEnCurso(true);
        programasInterruptorAdapter.notifyDataSetChanged();
        if (duracion >= programa.getDuracion()) {
            dispositivo.actualizarProgramaActivo("");
            programasInterruptorAdapter.notifyDataSetChanged();


        } else {
            int lleva = fechaADuracion(textoProgramaDesde.getText().toString());

            Log.i(TAG, "duracion es " + lleva);
            int progreso = ((int) lleva * 100) / programa.getDuracion();
            progresoPrograma.setProgress(progreso);
        }

    }

    private String duracionAfecha(String inicio, int duracion) {

        Calendar fecha;
        int hora;
        int minuto;
        String horaFinal;

        fecha = Calendar.getInstance();
        hora = Integer.valueOf(inicio.substring(0,2));
        minuto = Integer.valueOf(inicio.substring(3,5));
        fecha.set(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH), hora, minuto);
        fecha.setTimeInMillis(fecha.getTimeInMillis() + duracion * 1000);

        horaFinal = formatearHora(fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));
        return horaFinal;


    }

    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }

    private int restarHoras(String hora1, String hora2) {

        Calendar fecha1;
        Calendar fecha2;
        int hora;
        int minuto;


        hora = Integer.valueOf(hora1.substring(0,2));
        minuto = Integer.valueOf(hora1.substring(3,5));
        fecha1 = Calendar.getInstance();
        fecha2 = Calendar.getInstance();
        fecha1.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), Integer.valueOf(hora), Integer.valueOf(minuto));
        hora = Integer.valueOf(hora2.substring(0,2));
        minuto = Integer.valueOf(hora2.substring(3,5));
        //fecha2.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), Integer.valueOf(hora), Integer.valueOf(minuto));
        return (int) ((fecha2.getTime().getTime() - fecha1.getTime().getTime())/1000);
    }

    private int fechaADuracion(String inicio) {

        Calendar fechaInicio, fechaFin;
        int hora;
        int minuto;
        long milisFin, milisInicio;
        hora = Integer.valueOf(inicio.substring(0,2));
        minuto = Integer.valueOf(inicio.substring(3,5));
        fechaFin = Calendar.getInstance();
        fechaInicio = Calendar.getInstance();
        fechaInicio.set(fechaInicio.get(Calendar.YEAR), fechaInicio.get(Calendar.MONTH), fechaInicio.get(Calendar.DAY_OF_MONTH), hora, minuto);
        milisInicio = fechaInicio.getTimeInMillis()/1000;
        milisFin = fechaFin.getTimeInMillis()/1000;


        return (int) (milisFin - milisInicio);



    }
    private void notificarFinUpgrade() {


        if (dispositivo.getFinUpgrade() == 0) {
            Log.e(TAG, "Error al hacer el upgrade");
            contador.cancel();
            textConsolaMensajes.setText("Error al hacer el update");
            dispositivoDisponible("Error al hacer el update");
        }

        if (dispositivo.getFinUpgrade() == 1) {
            Log.w(TAG, "upgrade realizado con exito");
            contador.cancel();
            textConsolaMensajes.setText("Actualizacion realizada con exito");
            progressUpdate.setVisibility(View.INVISIBLE);
            dispositivoDisponible("Actualizacion realizada con exito");
        }




    }


}