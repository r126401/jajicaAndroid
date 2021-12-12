package com.iotcontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import net.steamcrafted.lineartimepicker.dialog.LinearDatePickerDialog;
import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;
import net.steamcrafted.lineartimepicker.view.LinearTimePickerView;

import org.eclipse.paho.client.mqttv3.internal.ConnectActionListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class programadorCronotermostato extends AppCompatActivity implements Serializable {



    Button botonFecha;
    Button botonHora;
    Switch switchTipoPrograma;
    LinearLayout layoutDiasSemana;
    ToggleButton botonLunes;
    ToggleButton botonMartes;
    ToggleButton botonMiercoles;
    ToggleButton botonJueves;
    ToggleButton botonViernes;
    ToggleButton botonSabado;
    ToggleButton botonDomingo;
    ProgramaDispositivoIotTermostato programa;
    TextView textoProgramaFecha;
    TextView textoProgramaHora;
    Button botonAceptarPrograma;
    Button menosTemperatura;
    Button masTemperatura;
    EditText indicadorTemperatura;
    TextView textoIndicadorTemperatura;
    final double INCREMENTO = 0.1;
    final double DECREMENTO = 0.1;
    final double INCREMENTO_LARGO = 0.5;
    final double DECREMENTO_LARGO = 0.5;


    static int colorActivo = Color.BLACK;
    static int colorNoActivo = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programador_cronotermostato);


        programa = new ProgramaDispositivoIotTermostato();


        Intent intento = getIntent();
        Bundle bundle = intento.getExtras();


        if (bundle != null) {

            programa = (ProgramaDispositivoIotTermostato) intento.getSerializableExtra(COMANDO_IOT.MODIFICAR_PROGRAMACION.toString());
            Log.i(getLocalClassName(), "programa capturado");
        } else {

        }

        capturarControles(bundle);


    }

    void capturarControlesDiaSemana() {


        botonLunes = (ToggleButton) findViewById(R.id.botonLunes);
        botonLunes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonLunes.isChecked()== true) {
                    botonLunes.setBackgroundColor(colorActivo);
                } else {
                    botonLunes.setBackgroundColor(colorNoActivo);
                }
            }
        });


        botonMartes = (ToggleButton) findViewById(R.id.botonMartes);
        botonMartes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonMartes.isChecked()== true) {
                    botonMartes.setBackgroundColor(colorActivo);
                } else {
                    botonMartes.setBackgroundColor(colorNoActivo);
                }
            }



        });
        botonMiercoles = (ToggleButton) findViewById(R.id.botonMiercoles);
        botonMiercoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonMiercoles.isChecked()== true) {
                    botonMiercoles.setBackgroundColor(colorActivo);
                } else {
                    botonMiercoles.setBackgroundColor(colorNoActivo);
                }

            }
        });
        botonJueves = (ToggleButton) findViewById(R.id.botonJueves);
        botonJueves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonJueves.isChecked()== true) {
                    botonJueves.setBackgroundColor(colorActivo);
                } else {
                    botonJueves.setBackgroundColor(colorNoActivo);
                }

            }
        });

        botonViernes = (ToggleButton) findViewById(R.id.botonViernes);
        botonViernes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonViernes.isChecked()== true) {
                    botonViernes.setBackgroundColor(colorActivo);
                } else {
                    botonViernes.setBackgroundColor(colorNoActivo);
                }

            }
        });

        botonSabado = (ToggleButton) findViewById(R.id.botonSabado);
        botonSabado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonSabado.isChecked()== true) {
                    botonSabado.setBackgroundColor(colorActivo);
                } else {
                    botonSabado.setBackgroundColor(colorNoActivo);
                }

            }
        });

        botonDomingo = (ToggleButton) findViewById(R.id.botonDomingo);
        botonDomingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonDomingo.isChecked()== true) {
                    botonDomingo.setBackgroundColor(colorActivo);
                } else {
                    botonDomingo.setBackgroundColor(colorNoActivo);
                }

            }
        });

    }

    void logicaBotonFecha(final Bundle bundle) {


        int ano, mes, dia;

        Calendar date = Calendar.getInstance();
        if (bundle == null) {
            ano = date.get(Calendar.YEAR);
            mes = date.get(Calendar.MONTH) + 1;
            dia = date.get(Calendar.DAY_OF_MONTH);
        } else {
            ano = programa.getAno() + 1900;
            mes = programa.getMes();
            dia = programa.getDia();
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textoProgramaFecha.setText(programa.getDia() + "/" + programa.getMes() + "/" + programa.getAno() + " ");
            }
        }, ano, mes, dia);

        botonFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Date date = new Date();
                datePickerDialog.show();
                DatePicker fecha = datePickerDialog.getDatePicker();
                //fecha.setMinDate(date.getTime());


            }
        });

    }

    void logicaBotonHora() {

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i(getLocalClassName(), "ponemos fecha");
                programa.setHora(hourOfDay);
                programa.setMinuto(minute);
                textoProgramaHora.setText(programa.getHora() + ":" + programa.getMinuto() + " ");
            }
        }, 1, 1, true);
        botonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog.show();

            }
        });
    }

    void actualizarVistaControles() {

        if (switchTipoPrograma.isChecked() == false) {
            layoutDiasSemana.setVisibility(View.VISIBLE);
            botonFecha.setVisibility(View.INVISIBLE);
            botonHora.setVisibility(View.VISIBLE);
            switchTipoPrograma.setText("REPETIDA");
            programa.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_DIARIO);
        }  else {
            layoutDiasSemana.setVisibility(View.INVISIBLE);
            botonFecha.setVisibility(View.VISIBLE);
            botonHora.setVisibility(View.VISIBLE);
            switchTipoPrograma.setText("UNA VEZ...");
            programa.setTipoPrograma(TIPO_PROGRAMA.PROGRAMA_FECHADO);
        }
    }

    void capturarControlesTemperatura() {

        menosTemperatura = (Button) findViewById(R.id.botonMenosTemperatura);
        menosTemperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double temperatura;
                temperatura = programa.getUmbralTemperatura();
                temperatura -= DECREMENTO;

                BigDecimal umbral = new BigDecimal(temperatura);
                umbral = umbral.setScale(1, BigDecimal.ROUND_HALF_UP);
                programa.setUmbralTemperatura(umbral.doubleValue());
                indicadorTemperatura.setText(String.valueOf(umbral.doubleValue()));
                textoIndicadorTemperatura.setText(String.valueOf(umbral.doubleValue()) + " ");



            }
        });

        masTemperatura = (Button) findViewById(R.id.botonMasTemperatura);
        masTemperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double temperatura;
                temperatura = programa.getUmbralTemperatura();
                temperatura += INCREMENTO;

                BigDecimal umbral = new BigDecimal(temperatura);
                umbral = umbral.setScale(1, BigDecimal.ROUND_HALF_UP);
                programa.setUmbralTemperatura(umbral.doubleValue());
                indicadorTemperatura.setText(String.valueOf(umbral.doubleValue()));
                textoIndicadorTemperatura.setText(String.valueOf(umbral.doubleValue()) + " ");


            }
        });

        indicadorTemperatura = (EditText) findViewById(R.id.editIndicadorTemperatura);
        indicadorTemperatura.setText("21.0");
        programa.setUmbralTemperatura(21.0);


    }

    void capturarControles(Bundle bundle) {

        layoutDiasSemana = (LinearLayout) findViewById(R.id.linearLayoutDiasSemana);
        textoIndicadorTemperatura = (TextView) findViewById(R.id.textoIndicadorTemperatura);
        botonFecha = (Button) findViewById(R.id.botonSeleccionFecha);
        botonHora = (Button) findViewById(R.id.botonSeleccionHora);
        logicaBotonFecha(bundle);
        logicaBotonHora();
        capturarControlesDiaSemana();
        textoProgramaFecha = (TextView) findViewById(R.id.textoProgramaFecha);
        textoProgramaHora = (TextView) findViewById(R.id.textoProgramahora);
        switchTipoPrograma = (Switch) findViewById(R.id.switchTipoPrograma);
        actualizarVistaControles();
        switchTipoPrograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarVistaControles();
            }
        });
        capturarControlesTemperatura();
        botonAceptarPrograma = (Button) findViewById(R.id.botonAceptarPrograma);
        botonAceptarPrograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (programa.getTipoPrograma() == TIPO_PROGRAMA.PROGRAMA_DIARIO) {
                    programa.setMascara(calcularMascara());
                }
                programa.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
                String textoComando = null;
                dialogoIot dialogo = new dialogoIot();
                textoComando = dialogo.comandoNuevoPrograma(programa);
                Intent datosDevueltos = new Intent();
                datosDevueltos.setData(Uri.parse(textoComando));
                setResult(COMANDO_IOT.NUEVA_PROGRAMACION.getIdComando(), datosDevueltos);
                finish();


            }
        });

    }


    private int calcularMascara () {

        int mascara = 0;

        if (botonDomingo.isChecked() == true) mascara = mascara | 1;
        if (botonLunes.isChecked() == true) mascara = mascara | 2;
        if (botonMartes.isChecked() == true) mascara = mascara | 4;
        if (botonMiercoles.isChecked() == true) mascara = mascara | 8;
        if (botonJueves.isChecked() == true) mascara = mascara | 16;
        if (botonViernes.isChecked() == true) mascara = mascara | 32;
        if (botonSabado.isChecked() == true) mascara = mascara | 64;

        return mascara;

    }


}
