package com.example.controliot;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;

public class ActivityTermostato extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, Serializable {

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
    private final String topicPeticionOta = "OtaIotOnOff";
    private final String topicRespuestaOta = "newVersionOtaIotOnOff";
    private boolean versionComprobada = false;
    private boolean nuevaVersionDisponible = false;
    private TextView textoTemperatura;
    private TextView textoUmbralTemperatura;
    private TextView textoModo;
    private TextView textoProgramaDesde;
    private ProgressBar progresoPrograma;
    private TextView textoProgramaHasta;
    private ArrayList<ProgramaDispositivoIotTermostato> programas;





    private void registrarControles() {

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
        swipeSchedule = (SwipeRefreshLayout) findViewById(R.id.swipeSchedule);
        swipeSchedule.setOnRefreshListener(this);
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

        dispositivoDisponible();
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
        textoTemperatura.setText(dispositivoCronotermostato.getTemperatura() + " ºC");
        textoUmbralTemperatura.setText(dispositivoCronotermostato.getUmbralTemperatura() + " ºC");
    }

    private void actualizarProgramaEnCurso(String idPrograma) {
        int hora;
        int minuto;
        int i;
        ProgramaDispositivoIotTermostato programa;
        i = dispositivoCronotermostato.buscarPrograma(idPrograma);
        programa = dispositivoCronotermostato.getProgramasTermostato().get(i);
        hora = programa.getHora();
        minuto = programa.getMinuto();
        textoProgramaDesde.setText(formatearHora(hora, minuto));
        if ((i + 1) < dispositivoCronotermostato.getProgramasTermostato().size()) {
            programa = dispositivoCronotermostato.getProgramasTermostato().get(i+1);
            hora = programa.getHora();
            minuto = programa.getMinuto();
            textoProgramaHasta.setText(formatearHora(hora, minuto));
        } else {
            textoProgramaHasta.setText(formatearHora(0, 0));
        }



    }

    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }

    public void registrarTermostato(dispositivoIotTermostato dispositivo) {
        this.dispositivoCronotermostato = dispositivo;
        actualizarTermostato(dispositivo);

    }

    public void actualizarTermostato(dispositivoIotTermostato dispositivo) {

        dispositivoDisponible();
        actualizarEstadoRele();
        actualizarModo();
        actualizarTemperatura();



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



    private void procesarMensajesTermostato (){


        cnx.setOnProcesarMensajesTermostato(new conexionMqtt.OnProcesarMensajesTermostato() {
            @Override
            public void estadoTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                registrarTermostato(dispositivo);
            }

            @Override
            public void actuacionReleLocalTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                actualizarTermostato(dispositivo);
            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String message, dispositivoIotTermostato dispositivo) {
                actualizarTermostato(dispositivo);
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

            }

            @Override
            public void modificarUmbralTemperatura(String topic, String texto, String idDispositivo) {

            }
        });


        cnx.setOnProcesarEspontaneosTermostato(new conexionMqtt.OnProcesarEspontaneosTermostato() {
            @Override
            public void arranqueAplicacionTermostato(String topic, String texto, dispositivoIotTermostato dispoisitivo) {
                registrarTermostato(dispositivoCronotermostato);
            }

            @Override
            public void cambioProgramaTermostato(String topic, String texto, String idDispositivo, String idPrograma) {

            }

            @Override
            public void atuacionReleLocalTermostato(String topic, String texto, String idDisositivo, ESTADO_RELE estadoRele) {
                actualizarTermostato(dispositivoCronotermostato);
            }

            @Override
            public void actuacionReleRemotoTermostato(String topic, String texto, String idDispositivo, ESTADO_RELE estadoRele) {
                actualizarTermostato(dispositivoCronotermostato);

            }

            @Override
            public void upgradeFirmwareTermostato(String topic, String texto, String idDispositivo, OtaVersion otaVersion) {

            }

            @Override
            public void cambioTemperaturaTermostato(String topic, String texto, String idDispositivo, long temperatura, long humedadad) {

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


            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }




    private void procesarProgramasRecibidos(ArrayList<ProgramaDispositivoIotTermostato> programas) {

        this.programas = programas;
        dispositivoCronotermostato.setProgramasTermostato(this.programas);
        programasTermostatoAdapter = new listaProgramasTermostatoAdapter(this, R.layout.vista_programas_termostato, dispositivoCronotermostato.getProgramasTermostato(), cnx, dispositivoCronotermostato);
        listViewSchedule.setAdapter(programasTermostatoAdapter);
        programasTermostatoAdapter.notifyDataSetChanged();
        //dispositivo.setProgramasTermostato(dispositivo.getProgramasTermostato());
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
        swipeSchedule.setRefreshing(false);
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

    private void lanzarActivityProgramaTermostato(int posicion, COMANDO_IOT idComando) {

        ProgramaDispositivoIotTermostato programa;

        Intent lanzador = new Intent(ActivityTermostato.this, ActivityProgramaTermostato.class);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson(), TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO);
        //lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson(), dispositivo.idDispositivo);
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson(), idComando);
        if (idComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            programa = programasTermostatoAdapter.listaProgramas.get(posicion);
            lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson(), programa);
        }


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
        lanzador.putExtra(TEXTOS_DIALOGO_IOT.ID_SENSOR.getValorTextoJson(), dispositivoCronotermostato.getMaster());
        lanzadorActivityProgramaTermostato.launch(lanzador);
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
        if (estadoRele == 0) {
            dispositivoCronotermostato.setEstadoRele(ESTADO_RELE.OFF);
        } else {
            dispositivoCronotermostato.setEstadoRele(ESTADO_RELE.ON);
        }
        actualizarEstadoRele();
        duracion = dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson());
        if(duracion == -1000) duracion = 0;
        dispositivoCronotermostato.setProgramasTermostato(this.programas);
        dispositivoCronotermostato.modificarPrograma(idPrograma, idNuevoPrograma, estadoPrograma, umbral, duracion );
        dispositivoCronotermostato.actualizarProgramaActivo(idProgramaActivo);
        programasTermostatoAdapter.notifyDataSetChanged();

    }

    private void iniciarAnimacionComando() {
        barraProgreso.setVisibility(View.VISIBLE);

    }
    
    private void pararAnimacionComando() {
        barraProgreso.setVisibility(View.INVISIBLE);
    }

    private void dispositivoDisponible()
    {
        imageEstadoDispositivo.setImageResource(R.drawable.dispositivo_disponible);
        imageEstadoBroker.setImageResource(R.drawable.bk_conectado);
        barraProgreso.setVisibility(View.INVISIBLE);
        textConsolaMensajes.setText(dispositivoCronotermostato.getIdDispositivo() + " disponible");
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

    private void actualizarEstadoRele() {

        if (dispositivoCronotermostato.getEstadoRele() == ESTADO_RELE.ON) {
            imageHeating.setImageResource(R.drawable.heating_on);
            imageHeating.setTag(true);

        } else {
            imageHeating.setImageResource(R.drawable.heating_off);
            imageHeating.setTag(false);
        }

    }

}