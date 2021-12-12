package com.iotcontrol;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.math.BigDecimal;

import static com.iotcontrol.COMANDO_IOT.FACTORY_RESET;
import static com.iotcontrol.COMANDO_IOT.RESET;

public class Temporizador extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    ImageView botonOk;
    ImageView botonCancelar;
    TimePicker timePickerHora;
    Button botonMenos;
    Button botonMas;
    EditText textoTemperatura;
    TextView textoFecha;
    TextView textoHora;
    SwitchCompat switchTipoPrograma;
    LinearLayout linearLayoutTermostato;
    LinearLayout linearLayoutInterruptor;
    LinearLayout linearLayoutDiasSemana;
    CheckBox lunes;
    CheckBox martes;
    CheckBox miercoles;
    CheckBox jueves;
    CheckBox viernes;
    CheckBox sabado;
    CheckBox domingo;
    //Spinner spinnerAno;
    DatePicker datePickerFecha;
    //ProgramaDispositivoIot programa;
    ProgramaDispositivoIotTermostato programa = null;
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

    ImageButton botonRele;
    TextView textoDuracion;
    Button botonMenosDuracion;
    Button botonMasDuracion;






    void definicionControles() {

    botonOk = (ImageView) findViewById(R.id.botonAceptar);
    botonOk.setOnClickListener(this);
    botonCancelar = (ImageView) findViewById(R.id.botonCancelar);
    botonCancelar.setOnClickListener(this);
    timePickerHora = (TimePicker) findViewById(R.id.timePickerHora);
    timePickerHora.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker timePicker, int i, int i1) {
            textoHora.setText(i + ":" + i1);

        }
    });
    botonMenos = (Button) findViewById(R.id.botonMenos);
    botonMenos.setOnClickListener(this);
    botonMenos.setOnLongClickListener(this);
    botonMenos.setOnTouchListener(this);
    botonMas = (Button) findViewById(R.id.botonMas);
    botonMas.setOnClickListener(this);
    botonMas.setOnLongClickListener(this);
    botonMas.setOnTouchListener(this);
    textoTemperatura = (EditText) findViewById(R.id.textoTemperatura);
    textoFecha = (TextView) findViewById(R.id.textoFecha);
    textoHora = (TextView) findViewById(R.id.textoHora);
    switchTipoPrograma = (SwitchCompat) findViewById(R.id.switchTipoPrograma);
    switchTipoPrograma.setOnClickListener(this);
    switchTipoPrograma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                buttonView.setText("Programa no repetido");

            } else {
                buttonView.setText("Programa repetido");
            }
        }
    });
    linearLayoutInterruptor = (LinearLayout)findViewById(R.id.linearInterruptor);
    linearLayoutTermostato = (LinearLayout) findViewById(R.id.linearTemperatura);
    linearLayoutDiasSemana = (LinearLayout) findViewById(R.id.linearLayoutDiasSemana);
    lunes = (CheckBox) findViewById(R.id.checkboxLunes);
    lunes.setTag((int) 1);
    lunes.setOnClickListener(this);
    martes = (CheckBox) findViewById(R.id.checkboxMartes);
    martes.setTag((int) 2);
    martes.setOnClickListener(this);
    miercoles = (CheckBox) findViewById(R.id.checkboxMiercoles);
    miercoles.setTag((int) 3);
    miercoles.setOnClickListener(this);
    jueves = (CheckBox) findViewById(R.id.checkboxJueves);
    jueves.setTag((int) 4);
    jueves.setOnClickListener(this);
    viernes = (CheckBox) findViewById(R.id.checkboxViernes);
    viernes.setTag((int) 5);
    viernes.setOnClickListener(this);
    sabado = (CheckBox) findViewById(R.id.checkboxSabado);
    sabado.setTag((int) 6);
    sabado.setOnClickListener(this);
    domingo = (CheckBox) findViewById(R.id.checkboxDomingo);
    domingo.setTag((int) 0);
    domingo.setOnClickListener(this);
    //spinnerAno = (Spinner) findViewById(R.id.spinnerAno);
    datePickerFecha = (DatePicker) findViewById(R.id.datePickerFecha);
    datePickerFecha.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            actualizarTextoFecha(datePickerFecha.getDayOfMonth(), datePickerFecha.getMonth(), datePickerFecha.getYear());
/*
            switch (tipo) {
                case INTERRUPTOR:
                    programaIotOnOff.setDia(datePickerFecha.getDayOfMonth());
                    programaIotOnOff.setMes(datePickerFecha.getMonth());
                    programaIotOnOff.setAno(datePickerFecha.getYear());
                    break;
                case CRONOTERMOSTATO:
                    programa.setDia(datePickerFecha.getDayOfMonth());
                    programa.setMes(datePickerFecha.getMonth());
                    programa.setAno(datePickerFecha.getYear());

                    break;
                default:
                    Log.e(getLocalClassName(), "Error en el tipo de dispositivo");
                    break;
            }*/
;
        }
    });

    botonRele = (ImageButton) findViewById(R.id.botonRele);
    botonRele.setOnClickListener(this);
    botonRele.setTag(Integer.valueOf(R.drawable.on));
    textoDuracion = (TextView) findViewById(R.id.textoDuracion);
    botonMenosDuracion = (Button) findViewById(R.id.botonMenosDuracion);
    botonMenosDuracion.setOnClickListener(this);
    botonMenosDuracion.setOnLongClickListener(this);
    botonMenosDuracion.setOnTouchListener(this);
    botonMasDuracion = (Button) findViewById(R.id.botonMasDuracion);
    botonMasDuracion.setOnClickListener(this);
    botonMasDuracion.setOnLongClickListener(this);
    botonMasDuracion.setOnTouchListener(this);



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporizador);


        definicionControles();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        repetidor = new Handler();

        //programa = new ProgramaDispositivoIotTermostato();
        tipo = (TIPO_DISPOSITIVO_IOT) bundle.get("TIPO_DISPOSITIVO");
        //Elegimos los controles a enseñar
        seleccionarTipoDispositivo(tipo, intent);

                //programa = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
/*
        if (programa == null) {
            accionNuevoPrograma = true;
            escenarioNuevoPrograma(tipo);

        } else {

            accionNuevoPrograma = false;
            escencarioModificarPrograma(tipo);
        }

*/

    }

    private void actualizarTodosDiasSemana(boolean accion) {

        actualizarDiasSemana(domingo, accion);
        actualizarDiasSemana(lunes, accion);
        actualizarDiasSemana(martes, accion);
        actualizarDiasSemana(miercoles, accion);
        actualizarDiasSemana(jueves, accion);
        actualizarDiasSemana(viernes, accion);
        actualizarDiasSemana(sabado, accion);
    }



    private void actualizarDiasSemana(CheckBox check, boolean accion) {

        int etiqueta;

        etiqueta = (int) check.getTag();

        switch (etiqueta) {
            case 0:
                Log.i(getLocalClassName(), "Domingo");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.domingooff));
                } else {
                    check.setForeground(getDrawable(R.drawable.domingoon));
                }

                break;
            case 1:
                Log.i(getLocalClassName(), "Lunes");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.lunesoff));
                } else {
                    check.setForeground(getDrawable(R.drawable.luneson));
                }

                break;
            case 2:
                Log.i(getLocalClassName(), "Martes");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.martesoff));
                } else {
                    check.setForeground(getDrawable(R.drawable.marteson));
                }

                break;
            case 3:
                Log.i(getLocalClassName(), "Miercoles");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.miercolesoff));
                } else {
                    check.setForeground(getDrawable(R.drawable.miercoleson));
                }

                break;
            case 4:
                Log.i(getLocalClassName(), "Jueves");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.juevesoff));
                } else {
                    check.setForeground(getDrawable(R.drawable.jueveson));
                }

                break;
            case 5:
                Log.i(getLocalClassName(), "Viernes");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.viernesoff));
                } else {
                    check.setForeground(getDrawable(R.drawable.vierneson));
                }

                break;
            case 6:
                Log.i(getLocalClassName(), "Sabado");
                if (accion == false) {
                    check.setForeground(getDrawable(R.drawable.sabadooff));
                } else {
                    check.setForeground(getDrawable(R.drawable.sabadoson));
                }

                break;
        }
       check.setChecked(accion);
        //programa.setMascara(calcularMascara());

    }


    private void escenarioNuevoPrograma(TIPO_DISPOSITIVO_IOT tipo) {

        //crearTipoDispositivo(tipo);
        // disponemos los controles
        accionNuevoPrograma = true;
        timePickerHora.setIs24HourView(true);
        switchTipoPrograma.setChecked(false);
        switchTipoPrograma.setText("Programa repetido");
        datePickerFecha.setVisibility(View.GONE);
        actualizarTodosDiasSemana(true);
        switch (tipo) {
            case INTERRUPTOR:
                //programaIotOnOff.setHora(timePickerHora.getHour());
                //programaIotOnOff.setMinuto(timePickerHora.getMinute());
                actualizarTextoHora(timePickerHora.getHour(), timePickerHora.getMinute());
                break;
            case CRONOTERMOSTATO:

                //programa.setHora(timePickerHora.getHour());
                //programa.setMinuto(timePickerHora.getMinute());
                //double dato;
                //dato = Double.valueOf(textoTemperatura.getText().toString());
                //programa.setUmbralTemperatura(presentarDecimales(dato, 1));
                escenarioProgramaRepetido();
                actualizarTextoHora(timePickerHora.getHour(), timePickerHora.getMinute());

                break;
            default:
                Log.e(getLocalClassName(), "Error en el tipo de dispositivo");
                break;
        }




    }

    private int calcularMascara () {

        int mascara = 0;

        if (domingo.isChecked() == true) mascara = mascara | 1;
        if (lunes.isChecked() == true) mascara = mascara | 2;
        if (martes.isChecked() == true) mascara = mascara | 4;
        if (miercoles.isChecked() == true) mascara = mascara | 8;
        if (jueves.isChecked() == true) mascara = mascara | 16;
        if (viernes.isChecked() == true) mascara = mascara | 32;
        if (sabado.isChecked() == true) mascara = mascara | 64;

        Log.i(getLocalClassName(), "La mascara es: " + mascara);

        return mascara;

    }

    private void leerMascara() {

        if (programa.getDiaActivo(0) == true) actualizarDiasSemana(domingo, true); else actualizarDiasSemana(domingo, false);
        if (programa.getDiaActivo(1) == true) actualizarDiasSemana(lunes, true); else actualizarDiasSemana(lunes, false);
        if (programa.getDiaActivo(2) == true) actualizarDiasSemana(martes, true); else actualizarDiasSemana(martes, false);
        if (programa.getDiaActivo(3) == true) actualizarDiasSemana(miercoles, true); else actualizarDiasSemana(miercoles, false);
        if (programa.getDiaActivo(4) == true) actualizarDiasSemana(jueves, true); else actualizarDiasSemana(jueves, false);
        if (programa.getDiaActivo(5) == true) actualizarDiasSemana(viernes, true); else actualizarDiasSemana(viernes, false);
        if (programa.getDiaActivo(6) == true) actualizarDiasSemana(sabado, true); else actualizarDiasSemana(sabado, false);


    }

    private void actualizarTemperatura() {

        double temperatura;
        temperatura = presentarDecimales(programa.getUmbralTemperatura(), 1);
        textoTemperatura.setText(String.valueOf(temperatura));
    }


    private void actualizarTextoHora(int hora, int minuto) {
        textoHora.setText(hora + ":" + minuto);
    }

    private void actualizarTextoFecha(int dia, int mes, int ano) {
        textoFecha.setText(dia + "/" + mes + "/" + ano);

    }

    private void escenarioModificarPrograma(TIPO_DISPOSITIVO_IOT tipo) {


        //crearTipoDispositivo(tipo);
        switch (tipo) {
            case INTERRUPTOR:
                if (programaIotOnOff.getTipoPrograma() == TIPO_PROGRAMA.PROGRAMA_DIARIO) {
                    escenarioProgramaRepetido();
                    timePickerHora.setIs24HourView(true);
                    timePickerHora.setHour(programaIotOnOff.getHora());
                    timePickerHora.setMinute(programaIotOnOff.getMinuto());


                } else {
                    escenarioProgramaNoRepetido();
                    timePickerHora.setIs24HourView(true);
                    timePickerHora.setHour(programaIotOnOff.getHora());
                    timePickerHora.setMinute(programaIotOnOff.getMinuto());
                    actualizarTextoHora(timePickerHora.getHour(), timePickerHora.getMinute());

                }

                textoDuracion.setText(String.valueOf(programaIotOnOff.getDuracion()));


                break;
            case CRONOTERMOSTATO:
                if (programa.tipoPrograma == TIPO_PROGRAMA.PROGRAMA_DIARIO) {
                    escenarioProgramaRepetido();
                    timePickerHora.setIs24HourView(true);
                    int a = programa.getHora();
                    timePickerHora.setHour(programa.getHora());
                    timePickerHora.setMinute(programa.getMinuto());
                    switchTipoPrograma.setChecked(false);

                    leerMascara();
                } else {
                    escenarioProgramaNoRepetido();
                    timePickerHora.setIs24HourView(true);
                    timePickerHora.setHour(programa.getHora());
                    timePickerHora.setMinute(programa.getMinuto());
                    actualizarTextoHora(timePickerHora.getHour(), timePickerHora.getMinute());
                    switchTipoPrograma.setChecked(true);

                }
                actualizarTemperatura();
                break;
                default:
                    break;


        }


        Log.i(getLocalClassName(), "datos capturados");


    }



    void incrementarDecrementarTemperatura(boolean incrementar, boolean corto) {

        double temperatura;
        int valor;

        String temp = textoTemperatura.getText().toString();
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
        textoTemperatura.setText(String.valueOf(temperatura));
        //programa.setUmbralTemperatura(temperatura);
        Log.i(getLocalClassName(), "Umbral: " + temperatura);


    }


    double presentarDecimales(double dato, int precision) {

        BigDecimal umbral = new BigDecimal(dato);
        umbral = umbral.setScale(precision, BigDecimal.ROUND_HALF_UP);
        return umbral.doubleValue();
    }

    private void cambiarTipoPrograma() {

                if (switchTipoPrograma.isChecked() == true) {
                    //programa.tipoPrograma = TIPO_PROGRAMA.PROGRAMA_FECHADO;
                    //programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
                    escenarioProgramaNoRepetido();
                } else {
                    //programa.tipoPrograma = TIPO_PROGRAMA.PROGRAMA_DIARIO;
                    //programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
                    escenarioProgramaRepetido();

                }

    }

    private void validarPrograma() {

        Log.i(getLocalClassName(), "Lanzamos programa");
        String textoComando = null;
        dialogoIot dialogo = new dialogoIot();
        switch(tipo) {

            case INTERRUPTOR:
                // Tipo de programa
                if (switchTipoPrograma.isChecked() == true) {
                    programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
                    programaIotOnOff.setDia(datePickerFecha.getDayOfMonth());
                    programaIotOnOff.setMes(datePickerFecha.getMonth());
                    programaIotOnOff.setAno(datePickerFecha.getYear()- 1900);
                }
                else {
                    programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
                    programaIotOnOff.setMascara(calcularMascara());
                }
                programaIotOnOff.setHora(timePickerHora.getHour());
                programaIotOnOff.setMinuto(timePickerHora.getMinute());
                //Estado Rele
                if ((Integer) botonRele.getTag() == R.drawable.on) {
                    programaIotOnOff.setEstadoRele(ESTADO_RELE.ON);
                } else {
                    programaIotOnOff.setEstadoRele(ESTADO_RELE.OFF);
                }

                //Duracion
                programaIotOnOff.setDuracion(Integer.valueOf(textoDuracion.getText().toString()));
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
                if (switchTipoPrograma.isChecked() == true) {
                    programa.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
                    programa.setDia(datePickerFecha.getDayOfMonth());
                    programa.setMes(datePickerFecha.getMonth());
                    programa.setAno(datePickerFecha.getYear() - 1900);
                }
                else {
                    programa.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
                    programa.setMascara(calcularMascara());
                }
                programa.setHora(timePickerHora.getHour());
                programa.setMinuto(timePickerHora.getMinute());
                //Estado Rele
                programa.setEstadoRele(ESTADO_RELE.ON);

                //Estado del programa
                programa.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);

                // umbral de temperatura
                double dato;
                dato = Double.valueOf(textoTemperatura.getText().toString());
                programa.setUmbralTemperatura(presentarDecimales(dato, 1));


                if (accionNuevoPrograma == true) {
                    textoComando = dialogo.comandoNuevoPrograma(programa);
                    Intent datosDevueltos = new Intent();
                    datosDevueltos.setData(Uri.parse(textoComando));
                    setResult(COMANDO_IOT.NUEVA_PROGRAMACION.getIdComando(), datosDevueltos);
                } else {
                    textoComando = dialogo.comandoModificarPrograma(programa);
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
    @Override
    public void onClick(View v) {
        Log.i(getLocalClassName(), "He pulsado en algun sitio");

        switch (v.getId()) {

            case R.id.checkboxDomingo:
                Log.i(getLocalClassName(), "domingo");
                actualizarDiasSemana(domingo, domingo.isChecked());
                break;
            case R.id.checkboxLunes:
                actualizarDiasSemana(lunes, lunes.isChecked());
                break;
            case R.id.checkboxMartes:
                actualizarDiasSemana(martes, martes.isChecked());
                break;
            case R.id.checkboxMiercoles:
                actualizarDiasSemana(miercoles, miercoles.isChecked());
                break;
            case R.id.checkboxJueves:
                actualizarDiasSemana(jueves, jueves.isChecked());
                break;
            case R.id.checkboxViernes:
                actualizarDiasSemana(viernes, viernes.isChecked());
                break;
            case R.id.checkboxSabado:
                actualizarDiasSemana(sabado, sabado.isChecked());
                break;
            case R.id.botonMenos:
                incrementarDecrementarTemperatura(false, true);
                break;
            case R.id.botonMas:
                incrementarDecrementarTemperatura(true, true);
                break;
            case R.id.switchTipoPrograma:
                cambiarTipoPrograma();
                break;
            case R.id.botonAceptar:
                validarPrograma();
                break;
            case R.id.botonCancelar:
                finish();
                break;
            case R.id.botonRele:
                seleccionarPosicionRele();
                break;

            case R.id.botonMenosDuracion:
                incrementarDecrementarDuracion(false, true);
                break;
            case R.id.botonMasDuracion:
                incrementarDecrementarDuracion(true, true);
                break;




                default:
                    break;



        }

    }

    private void escenarioProgramaRepetido() {

        switchTipoPrograma.setText(R.string.repetir);
        switchTipoPrograma.setChecked(false);
        //programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
        //programa.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
        linearLayoutDiasSemana.setVisibility(View.VISIBLE);
        datePickerFecha.setVisibility(View.GONE);
        textoFecha.setVisibility(View.GONE);
        actualizarTodosDiasSemana(true);
        //programaIotOnOff.setMascara(calcularMascara());
        //programa.setMascara(calcularMascara());



    }

    private void escenarioProgramaNoRepetido() {

        linearLayoutDiasSemana.setVisibility(View.GONE);
       // spinnerAno.setVisibility(View.VISIBLE);
        switchTipoPrograma.setText(R.string.norepetir);
        switchTipoPrograma.setChecked(true);
        //programa.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
        //programaIotOnOff.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
        datePickerFecha.setVisibility(View.VISIBLE);
        datePickerFecha.setMinDate(System.currentTimeMillis());
        textoFecha.setVisibility(View.VISIBLE);
        actualizarTextoFecha(datePickerFecha.getDayOfMonth(), datePickerFecha.getMonth(), datePickerFecha.getYear());
        //textoFecha.setText(datePickerFecha.getDayOfMonth() + "/" + (datePickerFecha.getMonth()+1) + "/" + datePickerFecha.getYear());
        //programa.setAno(datePickerFecha.getYear());
        //programa.setMes(datePickerFecha.getMonth());
        //programa.setDia(datePickerFecha.getDayOfMonth());
        //programaIotOnOff.setAno(datePickerFecha.getYear());
        //programaIotOnOff.setMes(datePickerFecha.getMonth());
        //programaIotOnOff.setDia(datePickerFecha.getDayOfMonth());

    }

    private void crearTipoDispositivo(TIPO_DISPOSITIVO_IOT tipo) {


        if (programaIotOnOff == null) {
            // Creamos el objeto programa
            programaIotOnOff = new ProgramaDispositivoIotOnOff();
            //programaIotOnOff.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
        }

        if (programa == null) {
            // Creamos el objeto programa
            programa = new ProgramaDispositivoIotTermostato();
            //programa.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
        }



    }

    private void seleccionarTipoDispositivo(TIPO_DISPOSITIVO_IOT tipo, Intent intent) {



        switch (tipo) {
            case INTERRUPTOR:
                programaIotOnOff = (ProgramaDispositivoIotOnOff) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
                if (programaIotOnOff == null) {
                    crearTipoDispositivo(tipo);
                    escenarioNuevoPrograma(tipo);
                } else {
                    escenarioModificarPrograma(tipo);

                }

                linearLayoutInterruptor.setVisibility(View.VISIBLE);
                linearLayoutTermostato.setVisibility(View.GONE);
                break;
            case CRONOTERMOSTATO:
                programa = (ProgramaDispositivoIotTermostato) intent.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
                if (programa == null) {
                    crearTipoDispositivo(tipo);
                    escenarioNuevoPrograma(tipo);
                } else {
                    escenarioModificarPrograma(tipo);
                }
                linearLayoutInterruptor.setVisibility(View.GONE);
                linearLayoutTermostato.setVisibility(View.VISIBLE);
                break;
        }

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

            case R.id.botonMasDuracion:
                autoIncremento = true;
                repetidor.post(new RepetirDuracion());
                break;
            case R.id.botonMenosDuracion:
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

            case R.id.botonMas:
            case R.id.botonMasDuracion:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)) {
                    autoIncremento = false;
                }
                break;
                case R.id.botonMenos:
            case R.id.botonMenosDuracion:
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

    private void seleccionarPosicionRele() {

        int boton;

        boton = (int) botonRele.getTag();

        switch (boton) {

            case R.drawable.on:
                botonRele.setImageResource(R.drawable.off);
                botonRele.setTag(Integer.valueOf(R.drawable.off));
                //programaIotOnOff.setEstadoRele(ESTADO_RELE.OFF);
                //programa.setEstadoRele(ESTADO_RELE.OFF);
                break;
            case R.drawable.off:
                botonRele.setImageResource(R.drawable.on);
                botonRele.setTag(Integer.valueOf(R.drawable.on));
                //programaIotOnOff.setEstadoRele(ESTADO_RELE.ON);
                //programa.setEstadoRele(ESTADO_RELE.ON);
                break;
                default:
                    Log.e(getLocalClassName(), "Error en el tag del rele");
                    break;
        }


    }


    void incrementarDecrementarDuracion(boolean incrementar, boolean corto) {

        int duracion;
        int valor;

        String temp = textoDuracion.getText().toString();
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
        //temperatura = presentarDecimales(temperatura, 1);
        textoDuracion.setText(String.valueOf(duracion));
        //programa.setDuracion

        Log.i(getLocalClassName(), "Duracion: " + duracion);


    }


}

