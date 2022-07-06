package com.example.controliot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;

public class ActivityTermostato extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnTouchListener, View.OnLongClickListener, Serializable {

    private ImageView imageUpgrade;
    private ImageView imageHeating;
    private ImageView imageEstadoBroker;
    private SwipeRefreshLayout swipeSchedule;
    private ListView listViewSchedule;
    private ImageView imageEstadoDispositivo;
    private BottomNavigationView bottommenuTermostato;
    private String idDispositivo;
    private conexionMqtt cnx;
    private final String TAG = "ActivityTermostato";
    private Context contexto;
    private dialogoIot dialogo;
    private ProgressBar barraProgreso;
    private TextView textConsolaMensajes;
    private CountDownTimer temporizador;
    private dispositivoIotTermostato dispositivoCronotermostato;
    private listaProgramasTermostatoAdapter programasTermostatoAdapter;
    private final String topicPeticionOta = "OtaIotCronoTemp";
    private final String topicRespuestaOta = "newVersionOtaIotCronoTemp";
    private boolean versionComprobada = false;
    private boolean nuevaVersionDisponible = false;
    private TextView textoTemperatura;
    private TextView textoUmbralTemperatura;
    private TextView textoModo;
    private TextView textoProgramaDesde;
    private ProgressBar progresoPrograma;
    private TextView textoProgramaHasta;
    private ImageButton botonMenosUmbral;
    private ImageButton botonMasUmbral;
    ConstraintLayout panelProgresoPrograma;
    private ProgressBar progressUpdate;


    private ArrayList<ProgramaDispositivoIotTermostato> programas;

    private Boolean autoincremento;
    private Boolean autodecremento;
    private Handler handler;

    private CountDownTimer contador;



    private void inicializarActivity() {
        autodecremento = false;
        autoincremento = false;
        handler = new Handler();
        contador = null;
    }


    private void registrarControles() {

        progressUpdate = (ProgressBar) findViewById(R.id.progress_update);
        panelProgresoPrograma = (ConstraintLayout) findViewById(R.id.panelProgresoPrograma);
        botonMenosUmbral = (ImageButton) findViewById(R.id.boton_menos_umbral);
        botonMenosUmbral.setOnClickListener(this);
        botonMenosUmbral.setOnLongClickListener(this);
        botonMenosUmbral.setOnTouchListener(this);
        botonMasUmbral = (ImageButton) findViewById(R.id.boton_mas_umbral);
        botonMasUmbral.setOnClickListener(this);
        botonMasUmbral.setOnLongClickListener(this);
        botonMasUmbral.setOnTouchListener(this);
        textoProgramaDesde = (TextView) findViewById(R.id.programa_desde);
        textoProgramaHasta = (TextView) findViewById(R.id.programa_hasta);
        progresoPrograma = (ProgressBar) findViewById(R.id.progreso_programa);
        textoModo = (TextView)  findViewById(R.id.textModo);
        textoTemperatura = (TextView) findViewById(R.id.textoTemperatura);
        textoUmbralTemperatura = (TextView) findViewById(R.id.textoUmbralTemperatura);
        imageEstadoDispositivo = (ImageView) findViewById(R.id.imageEstadoDispositivo);
        imageHeating = (ImageView) findViewById(R.id.imageHeating);
        imageHeating.setImageResource(R.drawable.switch_indeterminado);
        imageHeating.setOnClickListener(this);
        imageEstadoBroker = (ImageView) findViewById(R.id.imageEstadoBroker);
        //swipeSchedule = (SwipeRefreshLayout) findViewById(R.id.swipeSchedule);
        //swipeSchedule.setOnRefreshListener(this);
        listViewSchedule = (ListView) findViewById(R.id.listViewSchedule);
        bottommenuTermostato = (BottomNavigationView) findViewById(R.id.bottommenuTermostato);
        bottommenuTermostato.setOnNavigationItemSelectedListener(this);
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
                lanzarActivityProgramaTermostato(position, COMANDO_IOT.MODIFICAR_PROGRAMACION);
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

        dispositivoDisponible(dispositivoCronotermostato.getNombreDispositivo() + "disponible");
        cnx.subscribirTopic(dispositivoCronotermostato.getTopicSubscripcion());
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
        dispositivoCronotermostato = new dispositivoIotTermostato(disp);
        cnx = new conexionMqtt(getApplicationContext(), dialogo);
        cnx.setOnRecibirMensajes(new conexionMqtt.OnRecibirMensaje() {
            @Override
            public void recibirMensaje() {
                pararAnimacionComando();
                dispositivoDisponible(dispositivoCronotermostato.getNombreDispositivo() + " disponible");
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

    private void subscribirDispositivo() {

        dispositivoCronotermostato.setTopicSubscripcion(dispositivoCronotermostato.getTopicSubscripcion());
    }

    private void subscribirTopicOta() {
        cnx.subscribirTopic(topicRespuestaOta);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termostato);
        inicializarActivity();
        registrarControles();
        ConectarAlBrokerMqtt();
        subscribirDispositivo();
        procesarMensajesTermostato();

        Log.i(TAG, "Clase inicializada");
    }

    private void actualizarModo() {

        switch (dispositivoCronotermostato.getEstadoDispositivo()) {

            case NORMAL_AUTO:
            case NORMAL_AUTOMAN:
                textoModo.setText("AUTO");
                break;
            case NORMAL_MANUAL:
                textoModo.setText("MANUAL");
                break;
            case NORMAL_ARRANCANDO:
                break;
            case NORMAL_SIN_PROGRAMACION:
                textoModo.setText("NO PROG");
                break;
            case INDETERMINADO:
                break;
        }
    }

    private void actualizarTemperatura() {
        textoTemperatura.setText(String.valueOf(dispositivoCronotermostato.getTemperatura()));
        textoUmbralTemperatura.setText(String.valueOf(dispositivoCronotermostato.getUmbralTemperatura()));

    }

    private void actualizarProgramaEnCurso(String idPrograma) {
        int hora;
        int minuto;
        int i;
        int duracion;
        String hasta;
        CalculoFechas calculo;
        calculo = new CalculoFechas();
        ProgramaDispositivoIotTermostato programa;
        i = dispositivoCronotermostato.buscarPrograma(idPrograma);
        if ( i < 0 ) {
            panelProgresoPrograma.setVisibility(View.INVISIBLE);
            if (dispositivoCronotermostato.getProgramas() != null) {
                for(i=0;i<dispositivoCronotermostato.getProgramasTermostato().size();i++) {
                    dispositivoCronotermostato.getProgramasTermostato().get(i).setProgramaEnCurso(false);
                }
            }

            return;

        } else {
            panelProgresoPrograma.setVisibility(View.VISIBLE);
        }
        programa = dispositivoCronotermostato.getProgramasTermostato().get(i);
        hasta = calculo.duracionAfecha(calculo.formatearHora(programa.getHora(), programa.getMinuto()), programa.getDuracion());
        hora = programa.getHora();
        minuto = programa.getMinuto();
        textoProgramaDesde.setText(calculo.formatearHora(hora, minuto));
        textoProgramaHasta.setText(hasta);
        duracion = calculo.restarHoras(textoProgramaDesde.getText().toString(), textoProgramaHasta.getText().toString());
        if (duracion >= programa.getDuracion()) {
            dispositivoCronotermostato.actualizarProgramaActivo("");
            dispositivoCronotermostato.setUmbralTemperatura(dispositivoCronotermostato.getUmbralTemperaturaDefecto());
            actualizarTemperatura();
            programasTermostatoAdapter.notifyDataSetChanged();


        } else {

            int lleva = calculo.fechaADuracion(textoProgramaDesde.getText().toString());

            Log.i(TAG, "duracion es " + lleva);
            int progreso = ((int) lleva * 100) / programa.getDuracion();
            progresoPrograma.setProgress(progreso);
        }

    }


/*
    public void registrarTermostato(dispositivoIotTermostato dispositivo, String texto) {
        this.dispositivoCronotermostato = dispositivo;
        actualizarDispositivo(dispositivo, texto);
        actualizarTermostato(dispositivo);

    }

    public void actualizarDispositivo(dispositivoIotTermostato dispositivo, String texto) {

        dispositivoCronotermostato.setEstadoRele(dispositivo.getEstadoRele());
        dispositivoCronotermostato.setEstadoDispositivo(dispositivo.getEstadoDispositivo());
        dispositivoCronotermostato.setEstadoProgramacion(dispositivo.getEstadoProgramacion());
        dispositivoCronotermostato.setTemperatura(dispositivo.getTemperatura());
        dispositivoCronotermostato.setUmbralTemperatura(dispositivo.getUmbralTemperatura());
        dispositivoCronotermostato.setSensorMaster(dispositivo.isSensorLocal());
        dispositivoCronotermostato.setIdSensor(dispositivo.getIdSensor());
        dispositivoCronotermostato.setProgramaActivo(dispositivo.getProgramaActivo());
        dispositivoCronotermostato.setFinUpgrade(dispositivo.getFinUpgrade());
        actualizarTermostato(dispositivo);



    }

    public void actualizarTermostato(dispositivoIotTermostato dispositivo) {

        if (this.dispositivoCronotermostato == null) {
            this.dispositivoCronotermostato = dispositivo;
        }
        //Modifica la vista para indicar que el dispositivo esta disponible.
        dispositivoDisponible(dispositivo.getNombreDispositivo() + " disponible");
        //Actualiza el texto del panel para indicar el modo del termostato
        actualizarModo();
        //Actualiza los valores de temperatura del termostato
        actualizarTemperatura();
        actualizarRele();
        //Actualiza la vista para indicar el programa en curso.
        actualizarProgramaEnCurso(dispositivo.getProgramaActivo());

        if (versionComprobada == false) {
            consultarNuevaVersionOta();
        }




    }
*/
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
                        envioComando(dialogo.escribirComandoUpgradeFirmware(dispositivoCronotermostato));
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
                lanzarActivitySettingsTermostato();

                break;
            case(R.id.itemNuevoProgramaTermostato):
                lanzarActivityProgramaTermostato(0, COMANDO_IOT.NUEVA_PROGRAMACION);
                break;
            case(R.id.itemmasTermostato):
                 PopupMenu menumas = new PopupMenu(ActivityTermostato.this, bottommenuTermostato);
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


    private void actualizarPantalla() {

        dispositivoDisponible(dispositivoCronotermostato.getNombreDispositivo() + " disponible");
        //Actualiza el texto del panel para indicar el modo del termostato
        actualizarModo();
        //Actualiza los valores de temperatura del termostato
        actualizarTemperatura();
        actualizarRele();
        //Actualiza la vista para indicar el programa en curso.
        actualizarProgramaEnCurso(dispositivoCronotermostato.getProgramaActivo());

        if (versionComprobada == false) {
            consultarNuevaVersionOta();
        }


    }

    private void registrarCronotermostato(dispositivoIotTermostato dispositivo) {

        this.dispositivoCronotermostato = dispositivo;
        actualizarPantalla();
    }

    private void actualizarCronotermostato(dispositivoIotTermostato dispositivo) {

        dispositivoCronotermostato.setEstadoRele(dispositivo.getEstadoRele());
        dispositivoCronotermostato.setEstadoDispositivo(dispositivo.getEstadoDispositivo());
        dispositivoCronotermostato.setEstadoProgramacion(dispositivo.getEstadoProgramacion());
        dispositivoCronotermostato.setTemperatura(dispositivo.getTemperatura());
        dispositivoCronotermostato.setUmbralTemperatura(dispositivo.getUmbralTemperatura());
        dispositivoCronotermostato.setSensorMaster(dispositivo.isSensorLocal());
        dispositivoCronotermostato.setIdSensor(dispositivo.getIdSensor());
        dispositivoCronotermostato.setProgramaActivo(dispositivo.getProgramaActivo());
        dispositivoCronotermostato.setFinUpgrade(dispositivo.getFinUpgrade());
        actualizarPantalla();
    }


    private void procesarMensajesTermostato (){

        cnx.setOnProcesarMensajesTermostato(new conexionMqtt.OnProcesarMensajesTermostato() {
            @Override
            public void estadoTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                //registrarTermostato(dispositivo, message);
                if (dispositivoCronotermostato == null) {
                    registrarCronotermostato(dispositivo);
                } else {
                    actualizarCronotermostato(dispositivo);
                }

            }

            @Override
            public void actuacionReleLocalTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                //actualizarTermostato(dispositivo);
                actualizarCronotermostato(dispositivo);
            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                //actualizarTermostato(dispositivo);
                actualizarCronotermostato(dispositivo);
            }

            @Override
            public void consultarProgramacionTermostato(String topic, String texto, String idDispositivo, ArrayList<ProgramaDispositivoIotTermostato> programa) {

                procesarProgramasRecibidos(programa);
                Log.i(TAG, "hola");
            }

            @Override
            public void nuevoProgramacionTermostato(String topic, String texto, String idDispositivo) {
                Log.i(TAG, "Se recibe la informacion de la aplicacion");
                envioComando(dialogo.escribirComandoConsultarProgramacion());
            }

            @Override
            public void eliminarProgramacionTermostato(String topic, String texto, String idDispositivo, String idPrograma) {
                procesarEliminarPrograma(idPrograma);
            }

            @Override
            public void modificarProgramacionTermostato(String topic, String texto) {
                procesarModificarPrograma(texto);
                //actualizarTermostato(dispositivoCronotermostato);

            }
            @Override
            public void informacionDispositivo(String topic, String texto) {
                procesarInformacionDispositivo(texto);
            }
            @Override
            public void resetTermostato(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void factoryResetTermostato(String topic, String texto, String idDispositivo) {

            }

            @Override
            public void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

                procesarUpgradeFirmware();
            }

            @Override
            public void modificarUmbralTemperatura(String topic, String texto, double umbral) {
                Log.i(TAG, "el umbral es " + umbral);
                dispositivoCronotermostato.setUmbralTemperatura(umbral);
                textoUmbralTemperatura.setText(String.valueOf(umbral));
                dialogo.enviarComando(dispositivoCronotermostato, dialogo.comandoEstadoDispositivo());

            }
            @Override
            public void modificarConfiguracionTermostato(String topic, String texto) {
                Log.i(TAG, "ConfiguracionModificada");


            }
            @Override
            public void seleccionarSensorTemperatura(String topic, String texto) {
                Log.i(TAG, "Sensor seleccionado");

            }

        });
        cnx.setOnProcesarEspontaneosTermostato(new conexionMqtt.OnProcesarEspontaneosTermostato() {
            @Override
            public void arranqueAplicacionTermostato(String topic, String texto, dispositivoIotTermostato dispositivo) {
                //actualizarTermostato(dispositivo, texto);
                actualizarCronotermostato(dispositivo);
                notificarFinUpgrade(texto);
            }

            @Override
            public void cambioProgramaTermostato(String topic, String texto, dispositivoIotTermostato dispositivo) {
                //actualizarDispositivo(dispositivo, texto);
                actualizarCronotermostato((dispositivo));
            }

            @Override
            public void atuacionReleLocalTermostato(String topic, String texto, String idDisositivo, ESTADO_RELE estadoRele) {

            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String texto, String idDispositivo, ESTADO_RELE estadoRele) {

            }

            @Override
            public void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

                Log.i(TAG, "hola");
            }

            @Override
            public void cambioTemperaturaTermostato(String topic, String texto, dispositivoIotTermostato dispositivo) {
                //actualizarDispositivo(dispositivo, texto);
                actualizarCronotermostato(dispositivo);
            }

            @Override
            public void temporizadorCumplido(String topic, String texto, dispositivoIotTermostato dispositivo) {
                //actualizarDispositivo(dispositivo, texto);
                actualizarCronotermostato(dispositivo);
            }

            @Override
            public void cambioUmbralTemperatura(String topic, String texto, dispositivoIotTermostato dispositivo) {
                //actualizarDispositivo(dispositivo, texto);
                actualizarCronotermostato(dispositivo);
            }
        });
        cnx.setOnProcesarVersionServidorOta(new conexionMqtt.OnProcesarVersionServidorOta() {
            @Override
            public void lastVersion(OtaVersion otaVersion) {
                long versionDispositivo;
                long versionDisponible;
                dispositivoCronotermostato.setDatosOta(otaVersion);
                versionDispositivo = Long.valueOf(dispositivoCronotermostato.getOtaVersion());
                versionDisponible =  Long.valueOf(dispositivoCronotermostato.getDatosOta().getOtaVersionAvailable());
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
                if ((int) imageHeating.getTag() == R.drawable.heating_off) {
                    envioComando(dialogo.comandoActuarRele(ESTADO_RELE.ON));
                } else {
                    envioComando(dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                }

                break;
            case R.id.imageUpgrade:
                ventanaConfirmacionComando(R.drawable.ic_upgrade, "Se va a actualizar el dispositivo. Pulsa Aceptar para continuar", COMANDO_IOT.UPGRADE_FIRMWARE, this);
                break;

            case R.id.boton_menos_umbral:
                cancelarContador();
                modificarValorDouble(textoUmbralTemperatura, false, 0.5, 0, 30);
                crearContador();
                break;
            case R.id.boton_mas_umbral:
                cancelarContador();
                modificarValorDouble(textoUmbralTemperatura, true, 0.5, 0, 30);
                crearContador();
                break;



            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }




    private void procesarProgramasRecibidos(ArrayList<ProgramaDispositivoIotTermostato> programas) {

        if (programas != null) {
            this.programas = programas;
            dispositivoCronotermostato.setProgramasTermostato(this.programas);
            programasTermostatoAdapter = new listaProgramasTermostatoAdapter(this, R.layout.vista_programas_termostato, dispositivoCronotermostato.getProgramasTermostato(), cnx, dispositivoCronotermostato);
            listViewSchedule.setAdapter(programasTermostatoAdapter);
            programasTermostatoAdapter.notifyDataSetChanged();
        }

        actualizarProgramaEnCurso(dispositivoCronotermostato.getProgramaActivo());



    }

    private void refrescarDispositivo() {
        envioComando(dialogo.comandoEstadoDispositivo());
        if (programasTermostatoAdapter != null) {
            if (programasTermostatoAdapter.listaProgramas != null) {
                programasTermostatoAdapter.clear();
                programasTermostatoAdapter = null;

            }

        }
        actualizarProgramacion();



    }


    @Override
    public void onRefresh() {
        refrescarDispositivo();
        //swipeSchedule.setRefreshing(false);
    }

    private void actualizarProgramacion() {
        envioComando(dialogo.escribirComandoConsultarProgramacion());

    }

    private void consultarNuevaVersionOta() {

        subscribirTopicOta();
        cnx.publicarTopic(topicPeticionOta, dialogo.escribirComandoVersionOtaDisponible(TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO));
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
            fila = "Nombre: " + dispositivoCronotermostato.getNombreDispositivo();
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
        caja.setIcon(R.drawable.heating_on);
        View contenedor = getLayoutInflater().inflate(R.layout.info_dispositivo_termostato, null);
        caja.setView(contenedor);
        listaInfo = (ListView) contenedor.findViewById(R.id.listaInfoDispositivo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, lista);
        listaInfo.setAdapter(adapter);
        caja.show();



    }

    private void procesarEliminarPrograma(String idPrograma) {
        dispositivoCronotermostato.eliminarPrograma(idPrograma);
        programasTermostatoAdapter.notifyDataSetChanged();
    }

    //Rutina para lanzar una activityForResult
    ActivityResultLauncher<Intent> lanzadorActivityProgramaTermostato = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String textoComando = result.getData().getDataString();
                        Log.i(getLocalClassName(), "Recibimos datos: " + textoComando);
                        //Introducimos loa logica para modificar la programacion.
                        cnx.publicarTopic(dispositivoCronotermostato.getTopicPublicacion(), textoComando);

                    } else {
                        Log.w(getLocalClassName(), "No se puede modificar la programacion");
                    }
                }
            }
    );



    //Rutina para lanzar una activityForResult
    ActivityResultLauncher<Intent> lanzadorActivitySettingsTermostato = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK) {
                        String comando1 = result.getData().getStringExtra(COMANDO_IOT.MODIFICAR_APP.toString());
                        String comando2 = result.getData().getStringExtra(COMANDO_IOT.SELECCIONAR_SENSOR_TEMPERATURA.toString());
                        //Introducimos loa logica para modificar la programacion.
                        if (comando1 != null) {
                            cnx.publicarTopic(dispositivoCronotermostato.getTopicPublicacion(), comando1);
                        }
                        if (comando2 != null) {
                            cnx.publicarTopic(dispositivoCronotermostato.getTopicPublicacion(), comando2);
                        }


                    } else {
                        Log.w(getLocalClassName(), "No de modifica nada de la configuracion");
                    }
                }
            }
    );





    private void lanzarActivityProgramaTermostato(int posicion, COMANDO_IOT idComando) {

        ProgramaDispositivoIotTermostato programa;

        Intent lanzador = new Intent(ActivityTermostato.this, ActivityProgramador.class);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO);
        //lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), dispositivo.idDispositivo);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson(), idComando);
        if (idComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            programa = programasTermostatoAdapter.listaProgramas.get(posicion);
            lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), programa);

        }
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson(), programas);


        lanzadorActivityProgramaTermostato.launch(lanzador);
    }

    private void lanzarActivitySettingsTermostato() {



        Intent lanzador = new Intent(ActivityTermostato.this, ActivitySettingsTermostato.class);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson(), dispositivoCronotermostato.getMargenTemperatura());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson(), dispositivoCronotermostato.getIntervaloLectura());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson(), dispositivoCronotermostato.getReintentosLectura());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson(), dispositivoCronotermostato.getIntervaloReintentos());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson(), dispositivoCronotermostato.getValorCalibrado());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.TIPO_SENSOR.getValorTextoJson(), dispositivoCronotermostato.isSensorLocal());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_SENSOR.getValorTextoJson(), dispositivoCronotermostato.getIdSensor());
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson(), dispositivoCronotermostato.getUmbralTemperatura());
        lanzadorActivitySettingsTermostato.launch(lanzador);
    }



    private void procesarModificarPrograma(String textoRecibido) {

        String idPrograma;
        String idNuevoPrograma;
        String estadoPrograma;
        String idProgramaActivo;
        double umbral;
        int estadoRele;
        int duracion;
        ESTADO_RELE rele = ESTADO_RELE.INDETERMINADO;
        ESTADO_PROGRAMA prog = ESTADO_PROGRAMA.PROGRAMA_DESCONOCIDO;
        String estado;

        Log.i(getLocalClassName(), "Se procesa el texto del programa modificado");
        idNuevoPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.NUEVO_ID_PROGRAMA.getValorTextoJson());
        idPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
        idProgramaActivo = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.PROGRAMA_ACTIVO.getValorTextoJson());
        estadoPrograma = dialogo.extraerDatoJsonString(textoRecibido, TEXTOS_DIALOGO_IOT.ESTADO_PROGRAMACION.getValorTextoJson());
        estadoRele = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        umbral = dialogo.extraerDatoJsonDouble(textoRecibido, TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson());
        dispositivoCronotermostato.setUmbralTemperatura(umbral);
        if (estadoRele == 0) {
            dispositivoCronotermostato.setEstadoRele(ESTADO_RELE.OFF);
        } else {
            dispositivoCronotermostato.setEstadoRele(ESTADO_RELE.ON);
        }
        duracion = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson());
        if(duracion == -1000) duracion = 0;
        dispositivoCronotermostato.setProgramasTermostato(this.programas);
        dispositivoCronotermostato.modificarPrograma(idPrograma, idNuevoPrograma, estadoPrograma, umbral, duracion );
        dispositivoCronotermostato.actualizarProgramaActivo(idProgramaActivo);
        programasTermostatoAdapter.notifyDataSetChanged();
        actualizarProgramaEnCurso(idProgramaActivo);

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
        imageHeating.setImageResource(R.drawable.switch_indeterminado);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes.setTextColor(Color.RED);
        textConsolaMensajes.setText(dispositivoCronotermostato.getIdDispositivo() + " no disponible");
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);

    }
    
    private void envioComando(String comando) {

        dialogo.enviarComando(dispositivoCronotermostato, comando);
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
        textConsolaMensajes.setText(dispositivoCronotermostato.getIdDispositivo() + " no disponible");
        imageHeating.setImageResource(R.drawable.switch_indeterminado);
        imageEstadoBroker.setImageResource(R.drawable.bk_no_conectado);

    }






    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case R.id.boton_menos_umbral:
                autodecremento = true;
                autoincremento = false;
                handler.post(new ActivityTermostato.modificacionPulsacionLargaDoble(textoUmbralTemperatura, false, 1.0, 0.0, 30.0));


                break;
            case R.id.boton_mas_umbral:
                autoincremento = true;
                autodecremento = false;
                handler.post(new ActivityTermostato.modificacionPulsacionLargaDoble(textoUmbralTemperatura, true, 1.0, 0.0, 30.0));
                break;
            default:
                break;
        }


        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        long antes;
        long despues;
        long diferencia;


        switch (v.getId()) {
            case R.id.boton_menos_umbral:
            case R.id.boton_mas_umbral:

                if ((event.getAction() == MotionEvent.ACTION_UP) || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
                    autodecremento = false;
                    autoincremento = false;
                    antes = event.getEventTime();
                    despues = event.getDownTime();
                    diferencia = antes - despues;
                    if (diferencia > 500) {
                        cancelarContador();
                        crearContador();
                        Log.w(TAG, "contador de pulsacion larga");

                    }
                }
                break;
            default:
                break;
        }


        return false;
    }

    class modificacionPulsacionLargaDoble implements Runnable {

        TextView textControl;
        Boolean incremento;
        Double valor;
        Double minVal;
        Double maxVal;

        modificacionPulsacionLargaDoble(TextView control, Boolean incremento, Double valor, Double minVal, Double maxVal) {
            textControl = control;
            this.incremento = incremento;
            this.valor = valor;
            this.minVal = minVal;
            this.maxVal = maxVal;



        }

        public void run() {
            if (autoincremento) {
                handler.postDelayed(new ActivityTermostato.modificacionPulsacionLargaDoble(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "incrementando");
                modificarValorDouble(textControl, incremento, valor, minVal, maxVal);

            } else if (autodecremento){
                handler.postDelayed(new ActivityTermostato.modificacionPulsacionLargaDoble(textControl, incremento, valor, minVal, maxVal), 200);
                Log.i(getLocalClassName().toString(), "decrementando");
                modificarValorDouble(textControl, incremento, valor, minVal, maxVal);



            }
            //textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));

        }
    }

    private void modificarValorDouble(TextView controlTexto, Boolean incremento, double valor, double minVal, double maxVal) {

        double cantidad;

        cantidad = Double.valueOf(controlTexto.getText().toString());
        if (incremento == true) {
            cantidad += valor;
            if (cantidad >= maxVal) cantidad = maxVal;
        } else {
            cantidad -= valor;
            if (cantidad <= minVal) cantidad = minVal;
        }

        DecimalFormat formater = new DecimalFormat("##.#");
        String dato;
        dato = String.valueOf(formater.format(cantidad));
        String dat;
        dat = dato.substring(0,2);
        if (dato.length() > 2) {
            dat += ".";
            dat += dato.substring(3);
        } else {
            dat += ".0";
        }

        controlTexto.setText(dat);


    }

    private void crearContador() {

        if (contador == null) {
            textoUmbralTemperatura.setTextColor(Color.rgb(0xFF, 0x3c, 0x00));
            contador = new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    dispositivoCronotermostato.setUmbralTemperatura(Double.valueOf(textoUmbralTemperatura.getText().toString()));
                    dialogo.enviarComando(dispositivoCronotermostato, dialogo.comandoModificarUmbralTemperatura(dispositivoCronotermostato.getUmbralTemperatura()));
                    Log.i(TAG, "Modificado umbral de temperatura");
                    textoUmbralTemperatura.setTextColor(Color.BLACK);
                }
            };

            contador.start();
            Log.w(TAG, "Creado temporizador");
        }




    }

    private void cancelarContador() {

        if (contador != null) {
            contador.cancel();
            contador = null;
            Log.w(TAG, "Cancelado temporizador");
            textoUmbralTemperatura.setTextColor(Color.RED);
        }


    }

    private void actualizarRele() {

        switch (dispositivoCronotermostato.getEstadoRele()) {


            case OFF:
                imageHeating.setImageResource(R.drawable.heating_off);
                imageHeating.setTag(ESTADO_RELE.OFF);
                imageHeating.setVisibility(View.INVISIBLE);
                break;
            case ON:
                imageHeating.setImageResource(R.drawable.heating_on);
                imageHeating.setTag(ESTADO_RELE.ON);
                imageHeating.setVisibility(View.VISIBLE);
                break;
            case INDETERMINADO:
                imageHeating.setImageResource(R.drawable.switch_indeterminado);
                imageHeating.setTag(ESTADO_RELE.INDETERMINADO);
                break;
        }
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
                if (dispositivoCronotermostato.getFinUpgrade() == -1000) {
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
    private void notificarFinUpgrade(String texto) {


        if (dispositivoCronotermostato.getFinUpgrade() == 0) {
            Log.e(TAG, "Error al hacer el upgrade");
            contador.cancel();
            textConsolaMensajes.setText("Error al hacer el update");
            progressUpdate.setVisibility(View.INVISIBLE);
            imageUpgrade.setImageResource(R.drawable.ic_info);
            dispositivoDisponible("Error al hacer el update");
        }

        if (dispositivoCronotermostato.getFinUpgrade() == 1) {
            Log.w(TAG, "upgrade realizado con exito");
            contador.cancel();
            textConsolaMensajes.setText("Actualizacion realizada con exito");
            progressUpdate.setVisibility(View.INVISIBLE);
            imageUpgrade.setVisibility(View.INVISIBLE);
            dispositivoDisponible("Actualizacion realizada con exito");
        }

        if (dispositivoCronotermostato.getFinUpgrade() == -1000 ) {
            Log.i(TAG, "No se encuentra el finUprgade" +  texto);
        }




    }

}