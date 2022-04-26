package com.example.controliot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

public class ActivityProgramaInterruptor extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private final String TAG = "ActivityProgramaInterruptor";
    private TextView textRepetir;
    private SwitchCompat switchRepetir;
    private TextView textHoraPrograma;
    private ConstraintLayout panelRepetir;
    private TextView textoLunes;
    private TextView textoMartes;
    private TextView textoMiercoles;
    private TextView textoJueves;
    private TextView textoViernes;
    private TextView textoSabado;
    private TextView textoDomingo;
    private TextView textFecha;
    private ImageView imageDuracion;
    private TextView textDuracionEntero;
    private TextView textDuracionDecimal;
    private TextView textUnidadEntero;
    private TextView textUnidadDecimal;
    private ImageView idBotonMenos;
    private ImageView idBotonMas;
    private Button botonAceptar;
    private ImageView imageOnOff;

    private ProgramaDispositivoIotOnOff programaIotOnOff;
    private ProgramaDispositivoIotTermostato programaTermostato;
    final double INCREMENTO = 0.1;
    final double DECREMENTO = 0.1;
    final double INCREMENTO_LARGO = 0.5;
    final double DECREMENTO_LARGO = 0.5;
    private long INCREMENTO_DURACION = 1;
    private long DECREMENTO_DURACION = 1;
    private long INCREMENTO_DURACION_LARGO = 10;
    private long DECREMENTO_DURACION_LARGO = 10;
    private Handler repetidor;
    boolean autoIncremento = false;
    boolean autoDecremento = false;
    private int duracionPrograma;
    private COMANDO_IOT tipoOperacion;
    private TIPO_DISPOSITIVO_IOT tipo;
    private final int DURACION_MAXIMA = 16;

    private String [] diasSemana = {"No repetir", "Repetir"};
    private boolean[] checkDias = new boolean[]{true, true,true,true,true,true,true};

    private void registrarControles() {

        textHoraPrograma = (TextView) findViewById(R.id.textHoraPrograma);
        textHoraPrograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anotarHoraPrograma(textHoraPrograma);
            }
        });
        textRepetir = (TextView) findViewById(R.id.textRepetir);
        switchRepetir = (SwitchCompat) findViewById(R.id.switchRepetir);
        switchRepetir.setOnClickListener(this);
        panelRepetir = (ConstraintLayout) findViewById(R.id.panelRepetir);
        textoLunes = (TextView) findViewById(R.id.textoLunes);
        textoLunes.setOnClickListener(this);
        textoMartes = (TextView) findViewById(R.id.textoMartes);
        textoMartes.setOnClickListener(this);
        textoMiercoles = (TextView) findViewById(R.id.textoMiercoles);
        textoMiercoles.setOnClickListener(this);
        textoJueves = (TextView) findViewById(R.id.textoJueves);
        textoJueves.setOnClickListener(this);
        textoViernes = (TextView) findViewById(R.id.textoViernes);
        textoViernes.setOnClickListener(this);
        textoSabado = (TextView) findViewById(R.id.textoSabado);
        textoSabado.setOnClickListener(this);
        textoDomingo = (TextView) findViewById(R.id.textoDomingo);
        textoDomingo.setOnClickListener(this);
        textFecha = (TextView) findViewById(R.id.textFecha);
        textFecha.setOnClickListener(this);
        imageDuracion = (ImageView) findViewById(R.id.imageIconoTemperatura);
        textDuracionEntero = (TextView) findViewById(R.id.textDuracionEnteroTemperatura);
        textDuracionDecimal = (TextView) findViewById(R.id.textDuracionDecimalTemperatura);
        textUnidadEntero = (TextView) findViewById(R.id.textUnidadEnteroTemperatura);
        textUnidadDecimal = (TextView) findViewById(R.id.textUnidadDecimalTemperatura);
        idBotonMenos = (ImageButton) findViewById(R.id.idBotonMasTemperatura);
        idBotonMenos.setOnClickListener(this);
        idBotonMenos.setOnLongClickListener(this);
        idBotonMenos.setOnTouchListener(this);
        idBotonMas = (ImageButton) findViewById(R.id.idBotonMenosTemperatura);
        idBotonMas.setOnClickListener(this);
        idBotonMas.setOnLongClickListener(this);
        idBotonMas.setOnTouchListener(this);
        botonAceptar = (Button) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        imageOnOff = (ImageView) findViewById(R.id.imageOnOff);
        imageOnOff.setOnClickListener(this);




    }

    void calculoIncrementoDecremento() {


        if (duracionPrograma <= 120) {
            INCREMENTO_DURACION = 1;
            DECREMENTO_DURACION = 1;
            INCREMENTO_DURACION_LARGO = 5;
            DECREMENTO_DURACION_LARGO = 5;
        }

        if ( (duracionPrograma > 120) && (duracionPrograma <= 3600*2)) {
            INCREMENTO_DURACION = 60;
            DECREMENTO_DURACION = 60;
            INCREMENTO_DURACION_LARGO = 300;
            DECREMENTO_DURACION_LARGO = 300;
        }

        if (duracionPrograma > 3600*2) {
            INCREMENTO_DURACION = 60;
            DECREMENTO_DURACION = 60;
            INCREMENTO_DURACION_LARGO = 3000;
            DECREMENTO_DURACION_LARGO = 3000;

        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programa_interruptor);

        registrarControles();
        inicializarActivity();
    }


    private void presentarDuracion(int duracion, boolean incremento) {

        double duracionPrograma = 0;
        int horas, minutos, segundos;
        segundos = duracion;
        this.duracionPrograma = duracion;
        //Representacion de 0 segundos
        if (duracion ==0) {
            segundos = duracion;
            textUnidadEntero.setVisibility(View.INVISIBLE);
            textDuracionDecimal.setVisibility(View.INVISIBLE);
            textUnidadDecimal.setVisibility(View.INVISIBLE);
            textDuracionEntero.setText(String.valueOf(segundos));

        }
        //Representacion en segundos
        if ((duracion > 0) &&(duracion < 120)) {
            duracionPrograma = duracion;

            textDuracionEntero.setVisibility(View.VISIBLE);
            textUnidadEntero.setVisibility(View.VISIBLE);
            textDuracionDecimal.setVisibility(View.INVISIBLE);
            textUnidadDecimal.setVisibility(View.INVISIBLE);
            textUnidadEntero.setText("seg");
            textDuracionEntero.setText(String.valueOf(segundos));

        }

        //Representacion en minutos
        if ((duracion >= 120) && (duracion < 3600*2)) {
            textDuracionEntero.setVisibility(View.VISIBLE);
            textUnidadEntero.setVisibility(View.VISIBLE);
            textDuracionDecimal.setVisibility(View.INVISIBLE);
            textUnidadDecimal.setVisibility(View.INVISIBLE);
            minutos = duracion/60;
            textUnidadEntero.setText("min");
            textDuracionEntero.setText(String.valueOf(minutos));
        }

        //Representacion en horas y minutos
        if (duracion >= 3600*2) {
            int max;
            textDuracionEntero.setVisibility(View.VISIBLE);
            textUnidadEntero.setVisibility(View.VISIBLE);
            textDuracionDecimal.setVisibility(View.VISIBLE);
            textUnidadDecimal.setVisibility(View.VISIBLE);
            max = Integer.valueOf(textDuracionEntero.getText().toString());
            if (((max <= DURACION_MAXIMA) && (incremento == true)) || (incremento == false)){
                horas = (int) duracion / 3600;
                minutos = (int) (duracion % 3600)/60;
                duracionPrograma = duracion/3600;
                textDuracionEntero.setText(String.valueOf(horas));
                textUnidadEntero.setText("horas");
                textDuracionDecimal.setText(String.valueOf(minutos));
                textUnidadDecimal.setText("min");
            } else {
                textDuracionEntero.setText(String.valueOf(DURACION_MAXIMA));
                textUnidadDecimal.setText(String.valueOf(0));
            }

        }

    }


    private void presentarProgramaInterruptor(COMANDO_IOT tipoComando) {

        TIPO_PROGRAMA tipoPrograma;
        tipoPrograma = programaIotOnOff.getTipoPrograma();
        int duracion;
        int i;
        if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            if (programaIotOnOff.getEstadoRele() == ESTADO_RELE.ON) {
                imageOnOff.setImageResource(R.drawable.switch_on);
                imageOnOff.setTag(true);
            } else {
                imageOnOff.setImageResource(R.drawable.switch_off);
                imageOnOff.setTag(false);
            }
            switch (tipoPrograma) {

                case PROGRAMA_DESCONOCIDO:
                    break;
                case PROGRAMA_DIARIO:
                    switchRepetir.setChecked(true);
                    panelRepetir.setVisibility(View.VISIBLE);
                    textFecha.setVisibility(View.INVISIBLE);
                    textHoraPrograma.setText(formatearHora(programaIotOnOff.getHora(), programaIotOnOff.getMinuto()));
                    duracion = programaIotOnOff.getDuracion();
                    presentarDuracion(duracion, false);
                    actualizarSemanaCompleta();

                    break;
                case PROGRAMA_SEMANAL:
                    break;
                case PROGRAMA_FECHADO:
                    switchRepetir.setChecked(false);
                    panelRepetir.setVisibility(View.INVISIBLE);
                    textFecha.setVisibility(View.VISIBLE);
                    textHoraPrograma.setText(formatearHora(programaIotOnOff.getHora(), programaIotOnOff.getMinuto()));
                    duracion = programaIotOnOff.getDuracion();
                    presentarDuracion(duracion, false);
                    break;
            }

        }


    }
    private void seleccionarTipoProgramaDispositivo(TIPO_DISPOSITIVO_IOT tipo, Intent intent, COMANDO_IOT tipoComando) {

        switch (tipo) {


            case INTERRUPTOR:

                if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
                    programaIotOnOff = (ProgramaDispositivoIotOnOff) intent.getSerializableExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
                    presentarProgramaInterruptor(COMANDO_IOT.MODIFICAR_PROGRAMACION);

                } else {

                }
                break;
            case TERMOMETRO:

                if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
                    programaTermostato = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
                } else {

                }
                break;
            default:
                break;

        }

    }



    private void recibirDatosActivity() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            tipo = (TIPO_DISPOSITIVO_IOT) bundle.get(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
            tipoOperacion = (COMANDO_IOT) bundle.get(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            programaIotOnOff = (ProgramaDispositivoIotOnOff) intent.getSerializableExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
            //presentarProgramaInterruptor(COMANDO_IOT.MODIFICAR_PROGRAMACION);
            //seleccionarTipoProgramaDispositivo(tipo, intent, tipoOperacion);



        }
    }


    private void inicializarActivity() {
        recibirDatosActivity();
        repetidor = new Handler();
        Calendar fecha = Calendar.getInstance();

        if(tipoOperacion == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            presentarProgramaInterruptor(COMANDO_IOT.MODIFICAR_PROGRAMACION);

        } else {
            programaIotOnOff = new ProgramaDispositivoIotOnOff();
            actualizarDiasSemana(textoLunes, true);
            actualizarDiasSemana(textoMartes, true);
            actualizarDiasSemana(textoMiercoles, true);
            actualizarDiasSemana(textoJueves, true);
            actualizarDiasSemana(textoViernes, true);
            actualizarDiasSemana(textoSabado, true);
            actualizarDiasSemana(textoDomingo, true);
            programaIotOnOff.setDia(fecha.get(Calendar.DAY_OF_MONTH));
            programaIotOnOff.setMes(fecha.get(Calendar.MONTH));
            programaIotOnOff.setAno(fecha.get(Calendar.YEAR));
            programaIotOnOff.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
            programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
            programaIotOnOff.setEstadoRele(ESTADO_RELE.ON);
            programaIotOnOff.setDuracion(120);
            programaIotOnOff.setHora(fecha.get(Calendar.HOUR_OF_DAY));
            programaIotOnOff.setMinuto(fecha.get(Calendar.MINUTE));
            programaIotOnOff.setSegundo(0);
            programaIotOnOff.setMascara(calcularMascara());
            programaIotOnOff.actualizarDiasActivos();
            textFecha.setVisibility(View.INVISIBLE);
            textHoraPrograma.setText(formatearHora(programaIotOnOff.getHora(), programaIotOnOff.getMinuto()));
            presentarDuracion(programaIotOnOff.getDuracion(), true);
            imageOnOff.setTag(true);


        }



    }




    private void anotarHoraPrograma(TextView control) {

        TimePickerDialog timePickerDialog;
        String hora;
        String minuto;
        hora = control.getText().toString();
        hora = hora.substring(0,2);
        minuto = control.getText().toString().substring(3,5);

        timePickerDialog = new TimePickerDialog(this, ActivityProgramaInterruptor.this, Integer.valueOf(hora),Integer.valueOf(minuto), true);
        timePickerDialog.show();

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Log.i(TAG, "hola");

        textHoraPrograma.setText(formatearHora(hourOfDay, minute));
        programaIotOnOff.setHora(hourOfDay);
        programaIotOnOff.setMinuto(minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        programaIotOnOff.setAno(year);
        programaIotOnOff.setMes(month);
        programaIotOnOff.setDia(dayOfMonth);
    }

    private void abrirDialogoDiasSemana() {

        Button boton;

        String dias[] = {
                getResources().getString(R.string.lunes),
                getResources().getString(R.string.martes),
                getResources().getString(R.string.miercoles),
                getResources().getString(R.string.jueves),
                getResources().getString(R.string.viernes),
                getResources().getString(R.string.sabado),
                getResources().getString(R.string.domingo)};


        AlertDialog.Builder ventana = new AlertDialog.Builder(this);
        ventana.setTitle("Repetir programa");
        ventana.setIcon(R.drawable.schedule);
        ventana.setMultiChoiceItems(dias, checkDias, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkDias[which] = isChecked;
                Log.i(TAG, "hola");

            }
        });

        ventana.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ventana.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ventana.show();

    }


    private void abrirDatePicker() {

        DatePickerDialog datePickerDialog;
        Calendar fecha = Calendar.getInstance();
        int ano;
        int mes;
        int dia;

        if (tipoOperacion == COMANDO_IOT.NUEVA_PROGRAMACION) {
            ano = fecha.get(Calendar.YEAR);
            mes = fecha.get(Calendar.MONTH);
            dia = fecha.get(Calendar.DAY_OF_MONTH);
        } else {
            if (programaIotOnOff.getDia() == 0) {
                ano = fecha.get(Calendar.YEAR);
                mes = fecha.get(Calendar.MONTH);
                dia = fecha.get(Calendar.DAY_OF_MONTH);
            } else {
                ano = programaIotOnOff.getAno();
                mes = programaIotOnOff.getMes();
                dia = programaIotOnOff.getDia();
            }

        }
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                programaIotOnOff.setAno(year);
                programaIotOnOff.setMes(month);
                programaIotOnOff.setDia(dayOfMonth);
                textFecha.setText(dayOfMonth + "/"+ (month+1) +"/"+ year);

            }
        }, ano, mes, dia);

        datePickerDialog.show();


    }

    private void procesarBotonImageOnOff() {


        if ((Boolean) imageOnOff.getTag() == true) {
            imageOnOff.setImageResource(R.drawable.switch_off);
            imageOnOff.setTag(false);
            programaIotOnOff.setEstadoRele(ESTADO_RELE.OFF);

        } else {
            imageOnOff.setImageResource(R.drawable.switch_on);
            imageOnOff.setTag(true);
            programaIotOnOff.setEstadoRele(ESTADO_RELE.ON);
        }


    }


    @Override
    public void onClick(View v) {


        Log.w(getLocalClassName(), "pulsado: " + v.getId());
        switch(v.getId()) {

            case R.id.textFecha:
                Log.i(getLocalClassName(), "has pulsado en la fecha");
                abrirDatePicker();
                break;
            case R.id.textHoraPrograma:
                break;
            case R.id.switchRepetir:
                presentarProgramaFechado();
                break;
            case R.id.textoLunes:
            case R.id.textoMartes:
            case R.id.textoMiercoles:
            case R.id.textoJueves:
            case R.id.textoViernes:
            case R.id.textoSabado:
            case R.id.textoDomingo:
                pulsacionDiaSemana((TextView) v);
                break;
            case R.id.idBotonMasTemperatura:
                if (!(textUnidadEntero.getText().toString().equals("horas")) ||
                        (textUnidadEntero.getText().toString().equals("horas") && (Integer.valueOf(textDuracionEntero.getText().toString()) < 16))) {
                    incrementarDecrementarDuracion(true, true);
                    programaIotOnOff.setDuracion(Integer.valueOf(duracionPrograma));
                }

                break;
            case R.id.idBotonMenosTemperatura:

                incrementarDecrementarDuracion(false, true);
                programaIotOnOff.setDuracion(Integer.valueOf(duracionPrograma));
                break;
            case R.id.botonCancelar:
                finish();
                break;
            case R.id.botonAceptar:
                procesarBotonAceptar();
                break;
            case R.id.imageOnOff:
                procesarBotonImageOnOff();
        }

    }


    private void pulsacionDiaSemana(TextView idTexto) {

        Drawable a;

        if ((boolean) idTexto.getTag() == true) {
            //idTexto.setBackgroundResource(R.drawable.texto_redondeado_desactivado);
            //idTexto.setTag((boolean) false);
            actualizarDiasSemana(idTexto, false);
        } else {
            //idTexto.setBackgroundResource(R.drawable.texto_redondeado);
            //idTexto.setTag((boolean) true);
            actualizarDiasSemana(idTexto, true);
        }
    }


    void incrementarDecrementarDuracion(boolean incrementar, boolean corto) {

        int duracion;
        int valor;

        //String temp = textDuracion.getText().toString();
        //duracion = Integer.valueOf(temp);
        calculoIncrementoDecremento();
        duracion = duracionPrograma;
        if (incrementar == true) {
            if (corto == true) {
                duracion += INCREMENTO_DURACION;
            } else {
                duracion += INCREMENTO_DURACION_LARGO;

            }

        } else {
            if (corto == true) {
                duracion -= DECREMENTO_DURACION;

            } else {
                duracion -= DECREMENTO_DURACION_LARGO;

            }

        }
        if (duracion < 0) duracion = 0;
        //temperatura = presentarDecimales(temperatura, 1);
        presentarDuracion(duracion, incrementar);
        //textDuracion.setText(String.valueOf(duracion));
        //programa.setDuracion

        Log.i(getLocalClassName(), "Duracion: " + duracion);


    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {

            /*
            case R.id.idBotonMas:

                autoIncremento = true;
                repetidor.post(new RepetirLong());
                break;

            case R.id.idbotonMenostemperatura:
                autoDecremento = true;
                repetidor.post(new RepetirLong());

                break;
*/
            case R.id.idBotonMasTemperatura:
                autoIncremento = true;
                repetidor.post(new RepetirDuracion());
                break;
            case R.id.idBotonMenosTemperatura:
                autoDecremento = true;
                repetidor.post(new RepetirDuracion());
                break;
            case R.id.textoLunes:
            case R.id.textoMartes:
            case R.id.textoMiercoles:
            case R.id.textoJueves:
            case R.id.textoViernes:
            case R.id.textoSabado:
            case R.id.textoDomingo:
                pulsacionDiaSemana((TextView) v);

            default:
                break;

        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            //case R.id.idbotonMasTemperatura:
            case R.id.idBotonMasTemperatura:
                Log.i(TAG, "antes");
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    Log.i(TAG, "despues");
                    autoIncremento = false;
                    programaIotOnOff.setDuracion(Integer.valueOf(duracionPrograma));
                }
                break;
            //case R.id.idbotonMenostemperatura:
            case R.id.idBotonMenosTemperatura:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoDecremento = false;
                    programaIotOnOff.setDuracion(Integer.valueOf(duracionPrograma));
                }
                break;

        }

        return false;

    }

    class RepetirDuracion implements Runnable {

        public void run() {
            if (autoIncremento) {
                repetidor.postDelayed(new RepetirDuracion(), 200);
                if (!(textUnidadEntero.getText().toString().equals("horas")) ||
                        (textUnidadEntero.getText().toString().equals("horas") && (Integer.valueOf(textDuracionEntero.getText().toString()) < 16))) {
                    incrementarDecrementarDuracion(true, false);
                    Log.i(getLocalClassName(), "Incrementando");
                }

            } else if (autoDecremento){
                repetidor.postDelayed(new RepetirDuracion(), 200);
                incrementarDecrementarDuracion(false, false);

            }

        }
    }


    private String ponerFechaDeHoy() {
        Calendar calendario;
        String hoy;
        long tiempo;

        calendario = Calendar.getInstance();
        tiempo = calendario.getTime().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        hoy = formatter.format(tiempo);
        return hoy;


    }

    private void presentarProgramaFechado() {

        Calendar calendario;
        long fecha;
        if (!switchRepetir.isChecked()) {
            panelRepetir.setVisibility(View.INVISIBLE);
            textFecha.setVisibility(View.VISIBLE);
            if (tipoOperacion == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
                if (programaIotOnOff.getDia() == 0) {
                    textFecha.setText(ponerFechaDeHoy());
                } else {
                    textFecha.setText(programaIotOnOff.getDia() + "/"+programaIotOnOff.getMes() + "/" +programaIotOnOff.getAno());

                }
            } else {
                textFecha.setText(ponerFechaDeHoy());


            }
        } else {
            panelRepetir.setVisibility(View.VISIBLE);
            textFecha.setVisibility(View.INVISIBLE);
        }


    }

    private void leerMascara() {

        ProgramaDispositivoIot prog = null;
        switch (tipo) {
            case INTERRUPTOR:
                prog = programaIotOnOff;
                break;
            case CRONOTERMOSTATO:
                //prog = programaCronotermostato;
                break;
        }

        if (prog.getDiaActivo(0) == true) actualizarDiasSemana(textoDomingo, true); else actualizarDiasSemana(textoDomingo, false);
        if (prog.getDiaActivo(1) == true) actualizarDiasSemana(textoLunes, true); else actualizarDiasSemana(textoLunes, false);
        if (prog.getDiaActivo(2) == true) actualizarDiasSemana(textoMartes, true); else actualizarDiasSemana(textoMartes, false);
        if (prog.getDiaActivo(3) == true) actualizarDiasSemana(textoMiercoles, true); else actualizarDiasSemana(textoMiercoles, false);
        if (prog.getDiaActivo(4) == true) actualizarDiasSemana(textoJueves, true); else actualizarDiasSemana(textoJueves, false);
        if (prog.getDiaActivo(5) == true) actualizarDiasSemana(textoViernes, true); else actualizarDiasSemana(textoViernes, false);
        if (prog.getDiaActivo(6) == true) actualizarDiasSemana(textoSabado, true); else actualizarDiasSemana(textoSabado, false);


    }

    private void actualizarDiasSemana(TextView idTexto, boolean activo) {

        if (activo == true) {
            idTexto.setBackgroundResource(R.drawable.texto_redondeado);
            idTexto.setTag((boolean) true);
        } else {
            idTexto.setBackgroundResource(R.drawable.texto_redondeado_desactivado);
            idTexto.setTag((boolean) false);
        }


    }


    private int calcularMascara () {

        int mascara = 0;

        if ((Boolean) textoDomingo.getTag() == true) mascara = mascara | 1;
        if ((Boolean) textoLunes.getTag() == true) mascara = mascara | 2;
        if ((Boolean) textoMartes.getTag() == true) mascara = mascara | 4;
        if ((Boolean) textoMiercoles.getTag() == true) mascara = mascara | 8;
        if ((Boolean) textoJueves.getTag() == true) mascara = mascara | 16;
        if ((Boolean) textoViernes.getTag() == true) mascara = mascara | 32;
        if ((Boolean) textoSabado.getTag() == true) mascara = mascara | 64;

        Log.i(getLocalClassName(), "La mascara es: " + mascara);

        return mascara;

    }

    private void procesarBotonAceptar() {

        dialogoIot dialogo;
        String textoComando;
        dialogo = new dialogoIot();
        programaIotOnOff.setMascara(calcularMascara());
        if (tipoOperacion == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            textoComando = dialogo.comandoModificarPrograma(programaIotOnOff);
            Intent datosDevueltos = new Intent();
            datosDevueltos.setData(Uri.parse(textoComando));
            setResult(RESULT_OK, datosDevueltos);
        } else {
            programaIotOnOff.setEstadoRele(ESTADO_RELE.ON);
            textoComando = dialogo.comandoNuevoPrograma(programaIotOnOff);
            Intent datosDevueltos = new Intent();
            datosDevueltos.setData(Uri.parse(textoComando));
            setResult(RESULT_OK, datosDevueltos);

        }

        finish();
    }
    private void actualizarSemanaCompleta() {

        actualizarDiasSemana(textoDomingo, programaIotOnOff.getDiaActivo(0));
        actualizarDiasSemana(textoLunes, programaIotOnOff.getDiaActivo(1));
        actualizarDiasSemana(textoMartes, programaIotOnOff.getDiaActivo(2));
        actualizarDiasSemana(textoMiercoles, programaIotOnOff.getDiaActivo(3));
        actualizarDiasSemana(textoJueves, programaIotOnOff.getDiaActivo(4));
        actualizarDiasSemana(textoViernes, programaIotOnOff.getDiaActivo(5));
        actualizarDiasSemana(textoSabado, programaIotOnOff.getDiaActivo(6));


    }


    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }




}