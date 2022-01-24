package com.iotcontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

public class programador extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    protected LinearLayout idLayoutDiasSemana;
    protected LinearLayout idLayoutFecha;
    protected LinearLayout idLayoutHora;
    protected LinearLayout idLayoutInterruptor;
    protected LinearLayout idLayoutCronotermostato;

    protected SwitchCompat idSwitchTipoPrograma;

    protected ImageView botonCancelar;
    protected ImageView botonAceptar;
    protected TextView idTituloBotones;

    protected TextView textoLunes;
    protected TextView textoMartes;
    protected TextView textoMiercoles;
    protected TextView textoJueves;
    protected TextView textoViernes;
    protected TextView textoSabado;
    protected TextView textoDomingo;

    protected TextView textoFecha;
    protected TextView textoHora;

    // Duracion para el interruptor
    protected ImageButton idBotonMenosDuracion;
    protected ImageButton idBotonMasDuracion;
    protected TextView idTextoDuracion;

    protected SwitchCompat idSwitchEstadoRele;

    // Duracion para el interruptor
    protected ImageButton idBotonMenosTemperatura;
    protected ImageButton idBotonMasTemperatura;
    protected TextView idTextoTemperatura;

    ////
    ProgramaDispositivoIotTermostato programaCronotermostato = null;
    ProgramaDispositivoIotOnOff programaIotOnOff = null;
    final double INCREMENTO = 0.1;
    final double DECREMENTO = 0.1;
    final double INCREMENTO_LARGO = 0.5;
    final double DECREMENTO_LARGO = 0.5;
    final int INCREMENTO_DURACION = 1;
    final int DECREMENTO_DURACION = 1;
    final int INCREMENTO_DURACION_LARGO = 10;
    final int DECREMENTO_DURACION_LARGO = 10;
    private Handler repetidor;
    boolean autoIncremento = false;
    boolean autoDecremento = false;
    TIPO_DISPOSITIVO_IOT tipo;
    boolean accionNuevoPrograma;



    private void capturarControles() {

        idLayoutDiasSemana = (LinearLayout) findViewById(R.id.idLayoutDiasSemana);
        idLayoutFecha = (LinearLayout) findViewById(R.id.idLayoutFecha);
        idLayoutFecha.setOnClickListener(this);
        idLayoutHora = (LinearLayout) findViewById(R.id.idLayoutHora);
        idLayoutHora.setOnClickListener(this);
        idLayoutInterruptor = (LinearLayout) findViewById(R.id.idLayoutInterruptor);
        idLayoutCronotermostato = (LinearLayout) findViewById(R.id.idLayoutCronotermostato);

        idSwitchTipoPrograma = (SwitchCompat) findViewById(R.id.idSwitchTipoPrograma);
        idSwitchTipoPrograma.setOnClickListener(this);

        botonCancelar = (ImageView) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);
        botonAceptar = (ImageView) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        idTituloBotones = (TextView) findViewById(R.id.idTituloBotones);

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

        textoFecha = (TextView) findViewById(R.id.textoFecha);
        textoHora = (TextView) findViewById(R.id.idTextoHora);
        textoHora.setOnClickListener(this);

        idBotonMenosDuracion = (ImageButton) findViewById(R.id.idBotonMenosDuracion);
        idBotonMenosDuracion.setOnClickListener(this);
        idBotonMenosDuracion.setOnLongClickListener(this);
        idBotonMenosDuracion.setOnTouchListener(this);
        idBotonMasDuracion = (ImageButton) findViewById(R.id.idBotonMasDuracion);
        idBotonMasDuracion.setOnClickListener(this);
        idBotonMasDuracion.setOnLongClickListener(this);
        idBotonMasDuracion.setOnTouchListener(this);
        idTextoDuracion = (TextView) findViewById(R.id.idtextoDuracion);

        idSwitchEstadoRele = (SwitchCompat) findViewById(R.id.idSwitchEstadoRele);
        idSwitchEstadoRele.setOnClickListener(this);

        idBotonMenosTemperatura = (ImageButton) findViewById(R.id.idbotonMenostemperatura);
        idBotonMenosTemperatura.setOnClickListener(this);
        idBotonMenosTemperatura.setOnLongClickListener(this);
        idBotonMenosTemperatura.setOnTouchListener(this);
        idBotonMasTemperatura = (ImageButton) findViewById(R.id.idbotonMasTemperatura);
        idBotonMasTemperatura.setOnClickListener(this);
        idBotonMasTemperatura.setOnLongClickListener(this);
        idBotonMasTemperatura.setOnTouchListener(this);
        idTextoTemperatura = (TextView) findViewById(R.id.idTextoTemperatura);


    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programador);

        capturarControles();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        repetidor = new Handler();

        //programa = new ProgramaDispositivoIotTermostato();
        tipo = (TIPO_DISPOSITIVO_IOT) bundle.get("TIPO_DISPOSITIVO");
        //Elegimos los controles a enseñar
        seleccionarTipoDispositivo(tipo, intent);



    }

    @Override
    public void onClick(View v) {

        Log.w(getLocalClassName(), "pulsado: " + v.getId());
        switch(v.getId()) {

            case R.id.idLayoutFecha:
                Log.i(getLocalClassName(), "has pulsado en la fecha");
                abrirDialogoFecha();
                break;
            case R.id.idLayoutHora:
                abrirDialogoHora();
                break;
            case R.id.idTextoHora:
                Log.i(getLocalClassName(), "has pulsado en la hora " + v.getId());
                abrirDialogoHora();
                break;
            case R.id.idSwitchTipoPrograma:
                eleccionPrograma();
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
            case R.id.idBotonMasDuracion:
                incrementarDecrementarDuracion(true, true);
                break;
            case R.id.idBotonMenosDuracion:
                incrementarDecrementarDuracion(false, true);
                break;
            case R.id.botonCancelar:
                finish();
                break;
            case R.id.botonAceptar:
                validarPrograma();
                break;
            case R.id.idSwitchEstadoRele:
                actualizarSwitchRele();
                break;
            case R.id.idbotonMenostemperatura:
                incrementarDecrementarTemperatura(false, true);
                break;
            case R.id.idbotonMasTemperatura:
                incrementarDecrementarTemperatura(true, true);
                break;
        }

    }

    private void eleccionPrograma() {

        if (idSwitchTipoPrograma.isChecked()) {
            ensenarProgramaDiario();
            esconderProgramaFechado();
        } else {
            ensenarProgramaFechado();
            esconderProgramaDiario();
        }
    }

    private void ensenarProgramaDiario() {
        idLayoutDiasSemana.setVisibility(View.VISIBLE);

    }

    private void esconderProgramaDiario() {
        idLayoutDiasSemana.setVisibility(View.GONE);

    }

    private void esconderProgramaFechado() {
        idLayoutFecha.setVisibility(View.GONE);

    }

    private void ensenarProgramaFechado() {
        idLayoutFecha.setVisibility(View.VISIBLE);

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

    private void seleccionarTipoDispositivo(TIPO_DISPOSITIVO_IOT tipo, Intent intent) {

        switch (tipo) {
            case INTERRUPTOR:
                idLayoutInterruptor.setVisibility(View.VISIBLE);
                idLayoutCronotermostato.setVisibility(View.GONE);
                programaIotOnOff = (ProgramaDispositivoIotOnOff) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
                if (programaIotOnOff == null) {
                    crearTipoDispositivo(tipo);
                    escenarioNuevoPrograma(tipo);


                } else {
                    escenarioModificarPrograma(tipo);

                }
                eleccionPrograma();

                break;
            case CRONOTERMOSTATO:
                idLayoutInterruptor.setVisibility(View.GONE);
                idLayoutCronotermostato.setVisibility(View.VISIBLE);
                programaCronotermostato = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
                if (programaCronotermostato == null) {
                    crearTipoDispositivo(tipo);
                    escenarioNuevoPrograma(tipo);
                } else {

                }

                break;
        }


    }

    private void crearTipoDispositivo(TIPO_DISPOSITIVO_IOT tipo) {


        if (programaIotOnOff == null) {
            // Creamos el objeto programa
            programaIotOnOff = new ProgramaDispositivoIotOnOff();
            //programaIotOnOff.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
        }

        if (programaCronotermostato == null) {
            // Creamos el objeto programa
            programaCronotermostato = new ProgramaDispositivoIotTermostato();
            //programa.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
        }



    }


    private void escenarioNuevoPrograma(TIPO_DISPOSITIVO_IOT tipo) {

        accionNuevoPrograma = true;
        textoLunes.setTag((boolean) true);
        textoMartes.setTag((boolean) true);
        textoMiercoles.setTag((boolean) true);
        textoJueves.setTag((boolean) true);
        textoViernes.setTag((boolean) true);
        textoSabado.setTag((boolean) true);
        textoDomingo.setTag((boolean) true);
        idSwitchTipoPrograma.setChecked(true);
        idTextoDuracion.setText("0");

    }

    void incrementarDecrementarTemperatura(boolean incrementar, boolean corto) {

        double temperatura;
        int valor;

        String temp = idTextoTemperatura.getText().toString();
        temperatura = Double.valueOf(temp);
        if (incrementar == true) {
            if (corto == true) {
                temperatura += INCREMENTO;
            } else {
                temperatura += INCREMENTO_LARGO;

            }

        } else {
            if (corto == true) {
                temperatura -= DECREMENTO;
            } else {
                temperatura -= DECREMENTO_LARGO;
            }

        }
        temperatura = presentarDecimales(temperatura, 1);
        idTextoTemperatura.setText(String.valueOf(temperatura));
        //programa.setUmbralTemperatura(temperatura);
        Log.i(getLocalClassName(), "Umbral: " + temperatura);


    }

    void incrementarDecrementarDuracion(boolean incrementar, boolean corto) {

        int duracion;
        int valor;

        String temp = idTextoDuracion.getText().toString();
        duracion = Integer.valueOf(temp);
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
        idTextoDuracion.setText(String.valueOf(duracion));
        //programa.setDuracion

        Log.i(getLocalClassName(), "Duracion: " + duracion);


    }

    double presentarDecimales(double dato, int precision) {

        BigDecimal umbral = new BigDecimal(dato);
        umbral = umbral.setScale(precision, BigDecimal.ROUND_HALF_UP);
        return umbral.doubleValue();
    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            case R.id.idbotonMasTemperatura:

                autoIncremento = true;
                repetidor.post(new RepetirLong());
                break;
            case R.id.idbotonMenostemperatura:
                autoDecremento = true;
                repetidor.post(new RepetirLong());

                break;

            case R.id.idBotonMasDuracion:
                autoIncremento = true;
                repetidor.post(new RepetirDuracion());
                break;
            case R.id.idBotonMenosDuracion:
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

            case R.id.idbotonMasTemperatura:
            case R.id.idBotonMasDuracion:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoIncremento = false;
                }
                break;
            case R.id.idbotonMenostemperatura:
            case R.id.idBotonMenosDuracion:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoDecremento = false;
                }
                break;

        }
        return false;
    }



    class RepetirLong implements Runnable {

        public void run() {
            if (autoIncremento) {
                repetidor.postDelayed(new RepetirLong(), 200);
                incrementarDecrementarTemperatura(true, false);
                Log.i(getLocalClassName(), "Incrementando");
            } else if (autoDecremento){
                repetidor.postDelayed(new RepetirLong(), 200);
                incrementarDecrementarTemperatura(false, false);

            }

        }
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

    private void abrirDialogoFecha() {

        DatePickerDialog datePickerDialog;
        Calendar fecha = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, programador.this,fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH));
        long tiempo, tiempo2;
        tiempo = fecha.getTime().getTime();
        fecha.set(fecha.get(Calendar.YEAR) + 1,fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH));
        tiempo2 = fecha.getTime().getTime();
        datePickerDialog.getDatePicker().setMinDate(tiempo);
        datePickerDialog.getDatePicker().setMaxDate(tiempo2);
        datePickerDialog.show();

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {



        Calendar fecha = Calendar.getInstance();
        long tiempo;
        fecha.set(year, month, dayOfMonth);
        tiempo = fecha.getTime().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String hoy = formatter.format(tiempo);
        textoFecha.setText(hoy);
        switch (tipo) {
            case INTERRUPTOR:
                programaIotOnOff.setDia(dayOfMonth);
                programaIotOnOff.setMes(month);
                programaIotOnOff.setAno(year - 1900);
                break;
            case CRONOTERMOSTATO:
                programaCronotermostato.setDia(dayOfMonth);
                programaCronotermostato.setMes(month);
                programaCronotermostato.setAno(year - 1900);
                break;

        }


    }

    private void abrirDialogoHora() {

        TimePickerDialog timePickerDialog;
        String hora;
        String minuto;
        hora = textoHora.getText().toString();
        hora = hora.substring(0,2);
        minuto = textoHora.getText().toString().substring(3,5);

        timePickerDialog = new TimePickerDialog(this, programador.this, Integer.valueOf(hora),Integer.valueOf(minuto), true);
        timePickerDialog.show();



    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Formatter formato;
        formato = new Formatter();
        formato.format("%02d:%02d", hourOfDay, minute);
        textoHora.setText(formato.toString());
        switch (tipo) {
            case INTERRUPTOR:
                programaIotOnOff.setHora(hourOfDay);
                programaIotOnOff.setMinuto(minute);
                programaIotOnOff.setSegundo(0);
                break;
            case CRONOTERMOSTATO:
                programaCronotermostato.setHora(hourOfDay);
                programaCronotermostato.setMinuto(minute);
                programaCronotermostato.setSegundo(0);
                break;
        }

    }

    private void escenarioModificarPrograma(TIPO_DISPOSITIVO_IOT tipo) {

        accionNuevoPrograma = false;
        switch (tipo) {
            case INTERRUPTOR:
                switch (programaIotOnOff.getTipoPrograma()) {
                    case PROGRAMA_DIARIO:
                        idSwitchTipoPrograma.setChecked(true);
                        leerMascara();
                        break;
                    case PROGRAMA_FECHADO:
                        idSwitchTipoPrograma.setChecked(false);
                        Formatter formato;
                        formato = new Formatter();
                        formato.format("%02d/%02d/%04d", programaIotOnOff.getDia(), programaIotOnOff.getMes() + 1, programaIotOnOff.getAno() + 1900);
                        textoFecha.setText(formato.toString());
                        break;
                }
                eleccionPrograma();
                Formatter formato;
                formato = new Formatter();
                formato.format("%02d:%02d", programaIotOnOff.getHora(), programaIotOnOff.getMinuto());
                textoHora.setText(formato.toString());
                idTextoDuracion.setText(String.valueOf(programaIotOnOff.getDuracion()/60));
                if (programaIotOnOff.getEstadoRele() == ESTADO_RELE.ON) {
                    idSwitchEstadoRele.setChecked(true);
                } else {
                    idSwitchEstadoRele.setChecked(false);
                }


                break;
            case CRONOTERMOSTATO:
                break;
            case DESCONOCIDO:
                break;
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

    private void leerMascara() {

        ProgramaDispositivoIot prog = null;
        switch (tipo) {
            case INTERRUPTOR:
                prog = programaIotOnOff;
                break;
            case CRONOTERMOSTATO:
                prog = programaCronotermostato;
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

    private void validarPrograma() {

        Log.i(getLocalClassName(), "Lanzamos programa");
        String textoComando = null;
        dialogoIot dialogo = new dialogoIot();
        switch(tipo) {

            case INTERRUPTOR:
                // Tipo de programa
                if (!idSwitchTipoPrograma.isChecked()) {
                    programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
                }
                else {
                    programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
                    programaIotOnOff.setMascara(calcularMascara());
                }

                //Estado Rele
                if (idSwitchEstadoRele.isChecked()) {
                    programaIotOnOff.setEstadoRele(ESTADO_RELE.ON);
                } else {
                    programaIotOnOff.setEstadoRele(ESTADO_RELE.OFF);
                }

                //Duracion
                programaIotOnOff.setDuracion(Integer.valueOf(idTextoDuracion.getText().toString())*60);
                //Estado del programa
                programaIotOnOff.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);




                if (accionNuevoPrograma == true) {

                    textoComando = dialogo.comandoNuevoPrograma(programaIotOnOff);
                    Intent datosDevueltos = new Intent();
                    datosDevueltos.setData(Uri.parse(textoComando));
                    setResult(COMANDO_IOT.NUEVA_PROGRAMACION.getIdComando(), datosDevueltos);
                } else {
                    textoComando = dialogo.comandoModificarPrograma(programaIotOnOff);
                    Intent datosDevueltos = new Intent();
                    datosDevueltos.setData(Uri.parse(textoComando));
                    setResult(COMANDO_IOT.MODIFICAR_PROGRAMACION.getIdComando(), datosDevueltos);
                }

                break;
            case CRONOTERMOSTATO:
                // Tipo de programa
                if (!idSwitchTipoPrograma.isChecked()) {

                }
                else {
                    programaCronotermostato.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
                    programaCronotermostato.setMascara(calcularMascara());
                }

                programaCronotermostato.setEstadoRele(ESTADO_RELE.ON);

                //Estado del programa
                programaCronotermostato.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);

                // umbral de temperatura
                double dato;
                dato = Double.valueOf(idTextoTemperatura.getText().toString());
                programaCronotermostato.setUmbralTemperatura(presentarDecimales(dato, 1));


                if (accionNuevoPrograma == true) {
                    textoComando = dialogo.comandoNuevoPrograma(programaCronotermostato);
                    Intent datosDevueltos = new Intent();
                    datosDevueltos.setData(Uri.parse(textoComando));
                    setResult(COMANDO_IOT.NUEVA_PROGRAMACION.getIdComando(), datosDevueltos);
                } else {
                    textoComando = dialogo.comandoModificarPrograma(programaCronotermostato);
                    Intent datosDevueltos = new Intent();
                    datosDevueltos.setData(Uri.parse(textoComando));
                    setResult(COMANDO_IOT.MODIFICAR_PROGRAMACION.getIdComando(), datosDevueltos);
                }

                break;
            default:
                Log.e(getLocalClassName(), "Error no contemplado");
                break;
        }



        finish();

    }

    private void actualizarSwitchRele() {

    }


}