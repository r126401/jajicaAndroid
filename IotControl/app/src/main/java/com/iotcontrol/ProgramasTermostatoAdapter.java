package com.iotcontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ProgramasTermostatoAdapter extends ArrayAdapter<ProgramaDispositivoIotTermostato> {

    Context contexto;
    int indiceLayout;
    ArrayList<ProgramaDispositivoIotTermostato> datos;
    conexionMqtt cnx;
    dispositivoIotTermostato dispositivo;

    ProgramasTermostatoAdapter(Context contexto, int indiceLayout, ArrayList<ProgramaDispositivoIotTermostato> datos, conexionMqtt cnx, dispositivoIotTermostato dispositivo) {

        super(contexto, indiceLayout, datos);

        this.contexto = contexto;
        this.indiceLayout = indiceLayout;
        this.cnx = cnx;
        this.datos = datos;
        this.dispositivo = dispositivo;

    }


    @Override
    public View getView(final int position, View fila, ViewGroup parent) {
        //return super.getView(position, fila, parent);

        ListaProgramasHolder holder = null;
        ProgramaDispositivoIotTermostato programa = datos.get(position);
        final ProgramaDispositivoIotTermostato prog = programa;

        if (fila == null) {
            LayoutInflater inflater = ((Activity) contexto).getLayoutInflater();
            fila = inflater.inflate(indiceLayout, parent, false);
            holder = new ListaProgramasHolder();
            holder.indicadorProgramaActivo = (ImageView) fila.findViewById(R.id.indicadorProgramaActivo);
            //holder.botonEstadoPrograma = (ImageView) fila.findViewById(R.id.botonEstadoPrograma);
            //holder.textoTipoPrograma = (TextView) fila.findViewById(R.id.textoTipoPrograma);
            holder.textoFechaPrograma = (TextView) fila.findViewById(R.id.textoFechaPrograma);
            holder.textoHoraPrograma = (TextView) fila.findViewById(R.id.textoHoraPrograma);
            holder.textoUmbralTemperatura = (TextView) fila.findViewById(R.id.textoUmbralTemperatura);
            holder.botonEliminarPrograma = (ImageButton) fila.findViewById(R.id.botonEliminarPrograma);
            holder.layoutDiaSemana = (LinearLayout) fila.findViewById(R.id.layoutDiaSemana);
            holder.textoLunes = (TextView) fila.findViewById(R.id.textoLunes);
            holder.textoMartes = (TextView) fila.findViewById(R.id.textoMartes);
            holder.textoMiercoles = (TextView) fila.findViewById(R.id.textoMiercoles);
            holder.textoJueves = (TextView) fila.findViewById(R.id.textoJueves);
            holder.textoViernes = (TextView) fila.findViewById(R.id.textoViernes);
            holder.textoSadado = (TextView) fila.findViewById(R.id.textoSabado);
            holder.textoDomigo = (TextView) fila.findViewById(R.id.textoDomingo);
            holder.botonInhibirPrograma = (SwitchCompat) fila.findViewById(R.id.botonInhibirPrograma);
            holder.indicadorTipoprograma = (ImageView) fila.findViewById(R.id.imagenTipoPrograma);
            fila.setTag(holder);

            holder.botonEliminarPrograma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarPrograma(cnx, datos.get(position));
                }
            });

            holder.botonInhibirPrograma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ProgramaDispositivoIotTermostato pr = null;
                    pr = datos.get(position);

                    if (isChecked) {
                        pr.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);

                    } else {
                        pr.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_INACTIVO);
                    }

                }
            });

            holder.botonInhibirPrograma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogoIot dialogo;
                    dialogo = new dialogoIot();
                    cnx.publicarTopic(dispositivo.getTopicPublicacion(),dialogo.comandoInhibirProgramacion(datos.get(position)));

                }
            });

        } else {
            holder = (ListaProgramasHolder) fila.getTag();
        }



        //Codigo para rellenar cada fila del listView
        if (programa.getProgramaEnCurso() == true) {
            holder.indicadorProgramaActivo.setVisibility(View.VISIBLE);
        } else {
            holder.indicadorProgramaActivo.setVisibility(View.INVISIBLE);
        }

        switch (programa.getEstadoPrograma()) {

            case PROGRAMA_INACTIVO:
                //holder.botonEstadoPrograma.setImageResource(R.drawable.inhibido);
                holder.botonInhibirPrograma.setChecked(false);
                fila.setBackgroundColor(Color.parseColor("#DEDEE0"));
                break;
            case PROGRAMA_ACTIVO:
                //holder.botonEstadoPrograma.setImageResource(R.drawable.schedule);
                holder.botonInhibirPrograma.setChecked(true);
                fila.setBackgroundColor(Color.parseColor("#E6E8F0"));

                break;
            case PROGRAMA_DESCONOCIDO:
                fila.setBackgroundColor(Color.parseColor("#E6E8F0"));
                //holder.botonEstadoPrograma.setImageResource(R.drawable.acerca_de);
                break;
            default:
                Log.e(getClass().toString(), "Error al volcar el estado del programa");
                break;

        }

        holder.textoHoraPrograma.setText(programa.getHora() + ":" + programa.getMinuto());
        //dispositivo.redondearDatos(programa.getUmbralTemperatura(),1);
        holder.textoUmbralTemperatura.setText(String.valueOf(dispositivo.redondearDatos(programa.getUmbralTemperatura(),1)) + " ");

        switch (programa.getTipoPrograma()) {

            case PROGRAMA_DIARIO:
                presentarProgramaDiario(holder, programa);
                break;
            case PROGRAMA_SEMANAL:
                presentarProgramaSemanal(holder, programa);
                break;
            case PROGRAMA_FECHADO:
                presentarProgramaFechado(holder, programa);
                break;
            case PROGRAMA_DESCONOCIDO:
                //holder.textoTipoPrograma.setText(TIPO_PROGRAMA.PROGRAMA_DESCONOCIDO.toString());
                holder.textoFechaPrograma.setVisibility(View.INVISIBLE);

                break;
                default:
                    Log.e(getClass().toString(), "Error al leer el tipo de programa");
                    break;

        }

        //return super.getView(position, fila, parent);
        return fila;

    }

    private void presentarProgramaDiario(ListaProgramasHolder holder, ProgramaDispositivoIotTermostato programa) {

        int i;
        //holder.textoTipoPrograma.setText(TIPO_PROGRAMA.PROGRAMA_DIARIO.toString());
        holder.indicadorTipoprograma.setImageResource(R.drawable.icono_repetir);
        holder.textoFechaPrograma.setVisibility(View.INVISIBLE);
        holder.layoutDiaSemana.setVisibility(View.VISIBLE);
        if (programa.getDiaActivo(1) == true) holder.textoLunes.setVisibility(View.VISIBLE); else holder.textoLunes.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(2) == true) holder.textoMartes.setVisibility(View.VISIBLE); else holder.textoMartes.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(3) == true) holder.textoMiercoles.setVisibility(View.VISIBLE); else holder.textoMiercoles.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(4) == true) holder.textoJueves.setVisibility(View.VISIBLE); else holder.textoJueves.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(5) == true) holder.textoViernes.setVisibility(View.VISIBLE); else holder.textoViernes.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(6) == true) holder.textoSadado.setVisibility(View.VISIBLE); else holder.textoSadado.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(0) == true) holder.textoDomigo.setVisibility(View.VISIBLE); else holder.textoDomigo.setVisibility(View.INVISIBLE);

    }

    private void presentarProgramaSemanal(ListaProgramasHolder holder, ProgramaDispositivoIotTermostato programa) {

        //holder.textoTipoPrograma.setText(TIPO_PROGRAMA.PROGRAMA_SEMANAL.toString());
        holder.indicadorTipoprograma.setImageResource(R.drawable.icono_repetir);
        holder.textoFechaPrograma.setVisibility(View.INVISIBLE);
        holder.layoutDiaSemana.setVisibility(View.VISIBLE);
        if (programa.getDiaActivo(0) == true) holder.textoLunes.setVisibility(View.VISIBLE); else holder.textoLunes.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(1) == true) holder.textoMartes.setVisibility(View.VISIBLE); else holder.textoMartes.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(2) == true) holder.textoMiercoles.setVisibility(View.VISIBLE); else holder.textoMiercoles.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(3) == true) holder.textoJueves.setVisibility(View.VISIBLE); else holder.textoJueves.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(4) == true) holder.textoViernes.setVisibility(View.VISIBLE); else holder.textoViernes.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(5) == true) holder.textoSadado.setVisibility(View.VISIBLE); else holder.textoSadado.setVisibility(View.INVISIBLE);
        if (programa.getDiaActivo(6) == true) holder.textoDomigo.setVisibility(View.VISIBLE); else holder.textoDomigo.setVisibility(View.INVISIBLE);

    }

    private void presentarProgramaFechado(ListaProgramasHolder holder, ProgramaDispositivoIotTermostato programa) {

        //holder.textoTipoPrograma.setText(TIPO_PROGRAMA.PROGRAMA_FECHADO.toString());
        holder.indicadorTipoprograma.setImageResource(R.drawable.icono_repetir);
        holder.textoFechaPrograma.setVisibility(View.VISIBLE);
        holder.textoFechaPrograma.setText(programa.getDia() + "/" + (programa.getMes() + 1) + "/" + ((programa.getAno() + 1900)) + " ");
        holder.layoutDiaSemana.setVisibility(View.INVISIBLE);

    }


    void eliminarPrograma(conexionMqtt cnx, ProgramaDispositivoIotTermostato programa) {

        dialogoIot dialogo;
        dialogo = new dialogoIot();
        cnx.publicarTopic(dispositivo.getTopicPublicacion(),dialogo.comandoEliminarProgramacion(programa.getIdProgramacion()));


    }

    static class ListaProgramasHolder {

        ImageView indicadorProgramaActivo;
        //ImageView botonEstadoPrograma;
        //TextView textoTipoPrograma;
        TextView textoFechaPrograma;
        TextView textoHoraPrograma;
        TextView textoUmbralTemperatura;
        ImageButton botonEliminarPrograma;
        LinearLayout layoutDiaSemana;
        TextView textoLunes;
        TextView textoMartes;
        TextView textoMiercoles;
        TextView textoJueves;
        TextView textoViernes;
        TextView textoSadado;
        TextView textoDomigo;
        SwitchCompat botonInhibirPrograma;
        ImageView indicadorTipoprograma;

    }
}
