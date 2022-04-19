package com.example.controliot;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

public class ActivityProgramaTermostato extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

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
    private TextView textUmbralTemperatura;
    private TextView textUnidadEntero;
    private ImageView idBotonMenos;
    private ImageView idBotonMas;
    private Button botonAceptar;
    private ImageView imageOnOff;
    private TextView textHasta;
    private TextView textoHoraHasta;

    private ProgramaDispositivoIotTermostato programaIotTermostato;
    //private ProgramaDispositivoIotTermostato programaTermostato;
    final double INCREMENTO = 0.5;
    final double DECREMENTO = 0.5;
    final double INCREMENTO_LARGO = 1;
    final double DECREMENTO_LARGO = 1;
    private double INCREMENTO_UMBRAL = 0.5;
    private double DECREMENTO_DURACION = 0.5;
    private double INCREMENTO_UMBRAL_LARGO = 1;
    private double DECREMENTO_UMBRAL_LARGO = 1;
    private Handler repetidor;
    boolean autoIncremento = false;
    boolean autoDecremento = false;
    private COMANDO_IOT tipoOperacion;
    private TIPO_DISPOSITIVO_IOT tipo;
    private final int DURACION_MAXIMA = 16;

    private String [] diasSemana = {"No repetir", "Repetir"};
    private boolean[] checkDias = new boolean[]{true, true,true,true,true,true,true};

    private void registrarControles() {

        textHasta = (TextView) findViewById(R.id.textHasta);
        textoHoraHasta = (TextView) findViewById(R.id.textoHoraHasta);
        textoHoraHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anotarHoraPrograma(textoHoraHasta);
            }
        });
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
        textUmbralTemperatura = (TextView) findViewById(R.id.textDuracionEnteroTemperatura);
        textUnidadEntero = (TextView) findViewById(R.id.textUnidadEnteroTemperatura);
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programa_termostato);

        registrarControles();
        inicializarActivity();
    }





    private void presentarProgramaTermostato(COMANDO_IOT tipoComando) {

        TIPO_PROGRAMA tipoPrograma;
        tipoPrograma = programaIotTermostato.getTipoPrograma();
        int i;
        if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            switch (tipoPrograma) {

                case PROGRAMA_DESCONOCIDO:
                    break;
                case PROGRAMA_DIARIO:
                    switchRepetir.setChecked(true);
                    panelRepetir.setVisibility(View.VISIBLE);
                    textFecha.setVisibility(View.INVISIBLE);
                    textHoraPrograma.setText(formatearHora(programaIotTermostato.getHora(), programaIotTermostato.getMinuto()));
                    textoHoraHasta.setText(duracionAfecha(textHoraPrograma.getText().toString(), programaIotTermostato.getDuracion()));
                    actualizarSemanaCompleta();

                    break;
                case PROGRAMA_SEMANAL:
                    break;
                case PROGRAMA_FECHADO:
                    switchRepetir.setChecked(false);
                    panelRepetir.setVisibility(View.INVISIBLE);
                    textFecha.setVisibility(View.VISIBLE);
                    textHoraPrograma.setText(formatearHora(programaIotTermostato.getHora(), programaIotTermostato.getMinuto()));
                    break;
            }

        }


    }
    private void seleccionarTipoProgramaDispositivo(TIPO_DISPOSITIVO_IOT tipo, Intent intent, COMANDO_IOT tipoComando) {

        switch (tipo) {


            case INTERRUPTOR:

                if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
                    programaIotTermostato = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
                    presentarProgramaTermostato(COMANDO_IOT.MODIFICAR_PROGRAMACION);

                } else {

                }
                break;
            case TERMOMETRO:

                if (tipoComando == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
                    programaIotTermostato = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
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
            programaIotTermostato = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson());
            //presentarProgramaInterruptor(COMANDO_IOT.MODIFICAR_PROGRAMACION);
            //seleccionarTipoProgramaDispositivo(tipo, intent, tipoOperacion);



        }
    }


    private void inicializarActivity() {
        recibirDatosActivity();
        repetidor = new Handler();
        Calendar fecha = Calendar.getInstance();

        if(tipoOperacion == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            presentarProgramaTermostato(COMANDO_IOT.MODIFICAR_PROGRAMACION);

        } else {
            programaIotTermostato = new ProgramaDispositivoIotTermostato();
            actualizarDiasSemana(textoLunes, true);
            actualizarDiasSemana(textoMartes, true);
            actualizarDiasSemana(textoMiercoles, true);
            actualizarDiasSemana(textoJueves, true);
            actualizarDiasSemana(textoViernes, true);
            actualizarDiasSemana(textoSabado, true);
            actualizarDiasSemana(textoDomingo, true);
            programaIotTermostato.setDia(fecha.get(Calendar.DAY_OF_MONTH));
            programaIotTermostato.setMes(fecha.get(Calendar.MONTH));
            programaIotTermostato.setAno(fecha.get(Calendar.YEAR));
            programaIotTermostato.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
            programaIotTermostato.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
            programaIotTermostato.setEstadoRele(ESTADO_RELE.ON);
            programaIotTermostato.setHora(fecha.get(Calendar.HOUR_OF_DAY));
            programaIotTermostato.setMinuto(fecha.get(Calendar.MINUTE));
            programaIotTermostato.setSegundo(0);
            programaIotTermostato.setMascara(calcularMascara());
            programaIotTermostato.actualizarDiasActivos();
            programaIotTermostato.setUmbralTemperatura(19.0);
            textFecha.setVisibility(View.INVISIBLE);
            textHoraPrograma.setText(formatearHora(programaIotTermostato.getHora(), programaIotTermostato.getMinuto()));
            textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));
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

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                control.setText(formatearHora(hourOfDay, minute));
                if (control == textHoraPrograma) {
                    programaIotTermostato.setHora(hourOfDay);
                    programaIotTermostato.setMinuto(minute);
                } else {

                    programaIotTermostato.setDuracion(restarFechas());


                }
            }
        }, Integer.valueOf(hora), Integer.valueOf(minuto), true);
        timePickerDialog.show();

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Log.i(TAG, "hola");

        textHoraPrograma.setText(formatearHora(hourOfDay, minute));
        programaIotTermostato.setHora(hourOfDay);
        programaIotTermostato.setMinuto(minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        programaIotTermostato.setAno(year);
        programaIotTermostato.setMes(month);
        programaIotTermostato.setDia(dayOfMonth);
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
            if (programaIotTermostato.getDia() == 0) {
                ano = fecha.get(Calendar.YEAR);
                mes = fecha.get(Calendar.MONTH);
                dia = fecha.get(Calendar.DAY_OF_MONTH);
            } else {
                ano = programaIotTermostato.getAno();
                mes = programaIotTermostato.getMes();
                dia = programaIotTermostato.getDia();
            }

        }
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                programaIotTermostato.setAno(year);
                programaIotTermostato.setMes(month);
                programaIotTermostato.setDia(dayOfMonth);
                textFecha.setText(dayOfMonth + "/"+ (month+1) +"/"+ year);

            }
        }, ano, mes, dia);

        datePickerDialog.show();


    }

    private void procesarBotonImageOnOff() {


        if ((Boolean) imageOnOff.getTag() == true) {
            imageOnOff.setImageResource(R.drawable.switchoff);
            imageOnOff.setTag(false);
            programaIotTermostato.setEstadoRele(ESTADO_RELE.OFF);

        } else {
            imageOnOff.setImageResource(R.drawable.switchon);
            imageOnOff.setTag(true);
            programaIotTermostato.setEstadoRele(ESTADO_RELE.ON);
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
                incrementarDecrementarDuracion(true, true);
                textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));

                break;
            case R.id.idBotonMenosTemperatura:

                incrementarDecrementarDuracion(false, true);
                textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));
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

        double umbral;
        int valor;

        umbral = programaIotTermostato.getUmbralTemperatura();
        if (incrementar == true) {
            if (corto == true) {


                umbral += INCREMENTO_UMBRAL;
            } else {
                umbral += INCREMENTO_UMBRAL_LARGO;

            }

        } else {
            if (corto == true) {
                umbral -= DECREMENTO_DURACION;

            } else {
                umbral -= DECREMENTO_UMBRAL_LARGO;

            }

        }


        programaIotTermostato.setUmbralTemperatura(umbral);
        Log.i(getLocalClassName(), "Duracion: " + umbral);


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
                repetidor.post(new IncrementoLargoUmbral());
                break;
            case R.id.idBotonMenosTemperatura:
                autoDecremento = true;
                repetidor.post(new IncrementoLargoUmbral());
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

                }
                break;
            //case R.id.idbotonMenostemperatura:
            case R.id.idBotonMenosTemperatura:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoDecremento = false;

                }
                break;

        }

        return false;

    }

    class IncrementoLargoUmbral implements Runnable {

        public void run() {
            if (autoIncremento) {
                repetidor.postDelayed(new IncrementoLargoUmbral(), 200);
                incrementarDecrementarDuracion(true, false);

            } else if (autoDecremento){
                repetidor.postDelayed(new IncrementoLargoUmbral(), 200);
                incrementarDecrementarDuracion(false, false);


            }
            textUmbralTemperatura.setText(String.valueOf(programaIotTermostato.getUmbralTemperatura()));

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
                if (programaIotTermostato.getDia() == 0) {
                    textFecha.setText(ponerFechaDeHoy());
                } else {
                    textFecha.setText(programaIotTermostato.getDia() + "/"+programaIotTermostato.getMes() + "/" +programaIotTermostato.getAno());

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
                prog = programaIotTermostato;
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
        programaIotTermostato.setMascara(calcularMascara());
        programaIotTermostato.setDuracion(restarFechas());
        if (tipoOperacion == COMANDO_IOT.MODIFICAR_PROGRAMACION) {
            textoComando = dialogo.comandoModificarPrograma(programaIotTermostato);
            Intent datosDevueltos = new Intent();
            datosDevueltos.setData(Uri.parse(textoComando));
            setResult(RESULT_OK, datosDevueltos);
        } else {
            programaIotTermostato.setEstadoRele(ESTADO_RELE.ON);
            textoComando = dialogo.comandoNuevoPrograma(programaIotTermostato);
            Intent datosDevueltos = new Intent();
            datosDevueltos.setData(Uri.parse(textoComando));
            setResult(RESULT_OK, datosDevueltos);

        }

        finish();
    }
    private void actualizarSemanaCompleta() {

        actualizarDiasSemana(textoDomingo, programaIotTermostato.getDiaActivo(0));
        actualizarDiasSemana(textoLunes, programaIotTermostato.getDiaActivo(1));
        actualizarDiasSemana(textoMartes, programaIotTermostato.getDiaActivo(2));
        actualizarDiasSemana(textoMiercoles, programaIotTermostato.getDiaActivo(3));
        actualizarDiasSemana(textoJueves, programaIotTermostato.getDiaActivo(4));
        actualizarDiasSemana(textoViernes, programaIotTermostato.getDiaActivo(5));
        actualizarDiasSemana(textoSabado, programaIotTermostato.getDiaActivo(6));


    }


    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }

    private int restarFechas() {

        Calendar fecha1;
        Calendar fecha2;
        int hora;
        int minuto;


        hora = Integer.valueOf(textoHoraHasta.getText().toString().substring(0,2));
        minuto = Integer.valueOf(textoHoraHasta.getText().toString().substring(3,5));
        fecha1 = Calendar.getInstance();
        fecha2 = Calendar.getInstance();
        fecha1.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), programaIotTermostato.getHora(), programaIotTermostato.getMinuto());
        fecha2.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), hora, minuto);
        return (int) ((fecha2.getTime().getTime() - fecha1.getTime().getTime())/1000);
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


}