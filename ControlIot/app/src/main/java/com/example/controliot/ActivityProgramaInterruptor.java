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

    private String [] diasSemana = {"No repetir", "Repetir"};
    private boolean[] checkDias = new boolean[]{true, true,true,true,true,true,true};

    private void registrarControles() {

        textHoraPrograma = (TextView) findViewById(R.id.textHoraPrograma);
        textHoraPrograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anotarHoraPrograma();
            }
        });
        textRepetir = (TextView) findViewById(R.id.textRepetir);
        switchRepetir = (SwitchCompat) findViewById(R.id.switchRepetir);
        switchRepetir.setOnClickListener(this);
        panelRepetir = (ConstraintLayout) findViewById(R.id.panelRepetir);
        textoLunes = (TextView) findViewById(R.id.textoLunes);
        textoMartes = (TextView) findViewById(R.id.textoMartes);
        textoMiercoles = (TextView) findViewById(R.id.textoMiercoles);
        textoJueves = (TextView) findViewById(R.id.textoJueves);
        textoViernes = (TextView) findViewById(R.id.textoViernes);
        textoSabado = (TextView) findViewById(R.id.textoSabado);
        textoDomingo = (TextView) findViewById(R.id.textoDomingo);
        textFecha = (TextView) findViewById(R.id.textFecha);
        imageDuracion = (ImageView) findViewById(R.id.imageDuracion);
        textDuracionEntero = (TextView) findViewById(R.id.textDuracionEntero);
        textDuracionDecimal = (TextView) findViewById(R.id.textDuracionDecimal);
        textUnidadEntero = (TextView) findViewById(R.id.textUnidadEntero);
        textUnidadDecimal = (TextView) findViewById(R.id.textUnidadDecimal);
        idBotonMenos = (ImageButton) findViewById(R.id.idBotonMas);
        idBotonMenos.setOnClickListener(this);
        idBotonMenos.setOnLongClickListener(this);
        idBotonMenos.setOnTouchListener(this);
        idBotonMas = (ImageButton) findViewById(R.id.idBotonMenos);
        idBotonMas.setOnClickListener(this);
        idBotonMas.setOnLongClickListener(this);
        idBotonMas.setOnTouchListener(this);




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


    private void presentarDuracion(int duracion) {

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
            textDuracionEntero.setVisibility(View.VISIBLE);
            textUnidadEntero.setVisibility(View.VISIBLE);
            textDuracionDecimal.setVisibility(View.VISIBLE);
            textUnidadDecimal.setVisibility(View.VISIBLE);
            horas = (int) duracion / 3600;
            minutos = (int) (duracion % 3600)/60;
            duracionPrograma = duracion/3600;
            textDuracionEntero.setText(String.valueOf(horas));
            textUnidadEntero.setText("h");
            textDuracionDecimal.setText(String.valueOf(minutos));
            textUnidadDecimal.setText("min");
        }

    }


    private void presentarProgramaInterruptor(COMANDO_IOT tipoComando) {

        TIPO_PROGRAMA tipoPrograma;
        tipoPrograma = programaIotOnOff.getTipoPrograma();
        int duracion;
        if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            switch (tipoPrograma) {

                case PROGRAMA_DESCONOCIDO:
                    break;
                case PROGRAMA_DIARIO:
                    switchRepetir.setChecked(true);
                    panelRepetir.setVisibility(View.VISIBLE);
                    textFecha.setVisibility(View.INVISIBLE);
                    textHoraPrograma.setText(programaIotOnOff.getHora() + ":" + programaIotOnOff.getMinuto());
                    duracion = programaIotOnOff.getDuracion();

                    presentarDuracion(duracion);
                    break;
                case PROGRAMA_SEMANAL:
                    break;
                case PROGRAMA_FECHADO:
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
        TIPO_DISPOSITIVO_IOT tipo;
        COMANDO_IOT tipoOperacion;
        if (bundle != null) {
            tipo = (TIPO_DISPOSITIVO_IOT) bundle.get(TEXTOS_DIALOGO_IOT.TIPO_DISPOSITIVO.getValorTextoJson());
            tipoOperacion = (COMANDO_IOT) bundle.get(TEXTOS_DIALOGO_IOT.COMANDO.getValorTextoJson());
            seleccionarTipoProgramaDispositivo(tipo, intent, tipoOperacion);



        }
    }


    private void inicializarActivity() {
        recibirDatosActivity();
        repetidor = new Handler();
        textoLunes.setTag(true);
        textoMartes.setTag(true);
        textoMiercoles.setTag(true);
        textoJueves.setTag(true);
        textoViernes.setTag(true);
        textoSabado.setTag(true);
        textoDomingo.setTag(true);


    }




    private void anotarHoraPrograma() {

        TimePickerDialog timePickerDialog;
        String hora;
        String minuto;
        hora = textHoraPrograma.getText().toString();
        hora = hora.substring(0,2);
        minuto = textHoraPrograma.getText().toString().substring(3,5);

        timePickerDialog = new TimePickerDialog(this, ActivityProgramaInterruptor.this, Integer.valueOf(hora),Integer.valueOf(minuto), true);
        timePickerDialog.show();

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Log.i(TAG, "hola");
        textHoraPrograma.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

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


    @Override
    public void onClick(View v) {


        Log.w(getLocalClassName(), "pulsado: " + v.getId());
        switch(v.getId()) {

            case R.id.textFecha:
                Log.i(getLocalClassName(), "has pulsado en la fecha");
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
                pulsacionDiaSemana(v);
                break;
            case R.id.idBotonMas:
                incrementarDecrementarDuracion(true, true);
                break;
            case R.id.idBotonMenos:
                incrementarDecrementarDuracion(false, true);
                break;
            case R.id.botonCancelar:
                finish();
                break;
            case R.id.botonAceptar:
                break;
        }

    }


    private void pulsacionDiaSemana(View idTexto) {

        Drawable a;

        if ((boolean) idTexto.getTag() == true) {
            idTexto.setBackgroundResource(R.drawable.texto_redondeado_desactivado);
            idTexto.setTag((boolean) false);
        } else {
            idTexto.setBackgroundResource(R.drawable.texto_redondeado);
            idTexto.setTag((boolean) true);
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
        presentarDuracion(duracion);
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
            case R.id.idBotonMas:
                autoIncremento = true;
                repetidor.post(new RepetirDuracion());
                break;
            case R.id.idBotonMenos:
                autoDecremento = true;
                repetidor.post(new RepetirDuracion());
                break;

            default:
                break;

        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            //case R.id.idbotonMasTemperatura:
            case R.id.idBotonMas:
                Log.i(TAG, "antes");
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    Log.i(TAG, "despues");
                    autoIncremento = false;
                }
                break;
            //case R.id.idbotonMenostemperatura:
            case R.id.idBotonMenos:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoDecremento = false;
                }
                break;

        }
        return false;

    }

    class RepetirDuracion implements Runnable {

        public void run() {
            if (autoIncremento) {
                repetidor.postDelayed(new RepetirDuracion(), 200);
                incrementarDecrementarDuracion(true, false);
                Log.i(getLocalClassName(), "Incrementando");
            } else if (autoDecremento){
                repetidor.postDelayed(new RepetirDuracion(), 200);
                incrementarDecrementarDuracion(false, false);

            }

        }
    }


    private void presentarProgramaFechado() {

        if (switchRepetir.isChecked() == false) {
            panelRepetir.setVisibility(View.INVISIBLE);
            textFecha.setVisibility(View.VISIBLE);
        } else {
            panelRepetir.setVisibility(View.VISIBLE);
            textFecha.setVisibility(View.INVISIBLE);
        }


    }


}