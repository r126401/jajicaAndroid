package com.example.controliot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Formatter;

public class listaProgramasInterruptorAdapter extends ArrayAdapter<ProgramaDispositivoIotOnOff> {

    private Context contexto;
    private int idLayout;
    ArrayList<ProgramaDispositivoIotOnOff> listaProgramas;
    conexionMqtt cnx;
    dispositivoIotOnOff dispositivo;

    listaProgramasInterruptorAdapter(Context contexto, int idLayout, ArrayList<ProgramaDispositivoIotOnOff> listaProgramas, conexionMqtt cnx, dispositivoIotOnOff dispositivo) {
        super(contexto, idLayout, listaProgramas);

        this.contexto = contexto;
        this.idLayout = idLayout;
        this.listaProgramas = listaProgramas;
        this.cnx = cnx;
        this.dispositivo = dispositivo;
        dispositivo.programas = listaProgramas;

    }

    private void eliminarPrograma(ProgramaDispositivoIotOnOff programa) {
        dialogoIot dialogo;
        dialogo = new dialogoIot(cnx);
        dialogo.enviarComando(dispositivo, dialogo.comandoEliminarProgramacion(programa.idProgramacion));
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ListaProgramasInterruptorAdapterHolder holder;
        ProgramaDispositivoIotOnOff programa = listaProgramas.get(position);
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) contexto).getLayoutInflater();
            convertView = inflater.inflate(idLayout, parent, false);
            holder = new ListaProgramasInterruptorAdapterHolder();
            holder.imageInterruptor = (ImageView) convertView.findViewById(R.id.imageInterruptor);
            holder.switchProgramaActivo = (SwitchCompat) convertView.findViewById(R.id.switchProgramaActivo);
            holder.textHoraPrograma = (TextView) convertView.findViewById(R.id.textHoraPrograma);
            holder.textoLunes = (TextView) convertView.findViewById(R.id.textoLunes);
            holder.textoMartes = (TextView) convertView.findViewById(R.id.textoMartes);
            holder.textoMiercoles = (TextView) convertView.findViewById(R.id.textoMiercoles);
            holder.textoJueves = (TextView) convertView.findViewById(R.id.textoJueves);
            holder.textoViernes = (TextView) convertView.findViewById(R.id.textoViernes);
            holder.textoSabado = (TextView) convertView.findViewById(R.id.textoSabado);
            holder.textoDomingo = (TextView) convertView.findViewById(R.id.textoDomingo);
            holder.panelDiasSemana = (ConstraintLayout) convertView.findViewById(R.id.panelDiasSemana);
            holder.textDurante = (TextView) convertView.findViewById(R.id.textDuranteTemperatura);
            holder.textDuracionPrograma = (TextView) convertView.findViewById(R.id.textDuracionPrograma);
            holder.imageBorrarPrograma = (ImageView) convertView.findViewById(R.id.imageBorrarPrograma);
            holder.imageProgramaActivado = (ImageView) convertView.findViewById(R.id.imageProgramaActivado);
            holder.textunidadTiempo = (TextView) convertView.findViewById(R.id.textUnidadTiempo);
            holder.imageBorrarPrograma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarPrograma(listaProgramas.get(position));


                }
            });

            holder.switchProgramaActivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getClass().toString(), "hola");

                }
            });
            holder.switchProgramaActivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(getClass().toString(), "hola");

                }
            });

            convertView.setTag(holder);

        } else {
            holder = (ListaProgramasInterruptorAdapterHolder) convertView.getTag();
        }

        switch (programa.getTipoPrograma()) {

            case PROGRAMA_DESCONOCIDO:
                break;
            case PROGRAMA_DIARIO:
                presentarProgramaDiario(holder, programa);
                break;
            case PROGRAMA_SEMANAL:
                break;
            case PROGRAMA_FECHADO:
                break;
        }

        if (programa.getProgramaEnCurso() == true) {
            holder.imageProgramaActivado.setVisibility(View.VISIBLE);
        } else {
            holder.imageProgramaActivado.setVisibility(View.INVISIBLE);
        }


        return convertView;


    }

    private void actualizarPanelDiaSemana(TextView dia, boolean activado) {

        if (activado == false) {
            dia.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xcc));
            dia.setTag(false);
        } else {
            dia.setBackgroundColor(Color.rgb(0x34, 0x98, 0xdb));
            dia.setTag(true);
        }

    }


    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }


    private void presentarProgramaDiario(ListaProgramasInterruptorAdapterHolder holder, ProgramaDispositivoIotOnOff programa) {

        int i;
        String textoRepeticion = "";
        holder.panelDiasSemana.setVisibility(View.VISIBLE);
        long duracion;
        switch (programa.getTipoPrograma()) {

            case PROGRAMA_DESCONOCIDO:
                break;
            case PROGRAMA_DIARIO:
                if (programa.getDiaActivo(1) == true) {
                    actualizarPanelDiaSemana(holder.textoLunes, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoLunes, false);
                }
                if (programa.getDiaActivo(2) == true) {
                    actualizarPanelDiaSemana(holder.textoMartes, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoMartes, false);
                }
                if (programa.getDiaActivo(3) == true) {
                    actualizarPanelDiaSemana(holder.textoMiercoles, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoMiercoles, false);
                }
                if (programa.getDiaActivo(4) == true) {
                    actualizarPanelDiaSemana(holder.textoJueves, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoJueves, false);
                }
                if (programa.getDiaActivo(5) == true) {
                    actualizarPanelDiaSemana(holder.textoViernes, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoViernes, false);
                }
                if (programa.getDiaActivo(6) == true) {
                    actualizarPanelDiaSemana(holder.textoSabado, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoSabado, false);
                }
                if (programa.getDiaActivo(0) == true) {
                    actualizarPanelDiaSemana(holder.textoDomingo, true);
                } else {
                    actualizarPanelDiaSemana(holder.textoDomingo, false);
                }
                holder.textHoraPrograma.setText(formatearHora(programa.getHora(), programa.getMinuto()));
                duracion = programa.getDuracion();
                if (duracion < 60) holder.textunidadTiempo.setText("sg");
                if ((duracion >= 60) && (duracion < 3600)) {
                    duracion = duracion/60;
                    holder.textDuracionPrograma.setText(String.valueOf(duracion));
                    holder.textunidadTiempo.setText("min");
                }
                if (duracion > 3600) {
                    duracion = duracion/3600;
                    holder.textDuracionPrograma.setText(String.valueOf(duracion));
                    holder.textunidadTiempo.setText("h");
                }

                break;
            case PROGRAMA_SEMANAL:
                break;
            case PROGRAMA_FECHADO:
                holder.panelDiasSemana.setVisibility(View.INVISIBLE);
                break;
        }

        switch (programa.getEstadoPrograma()) {

            case PROGRAMA_DESCONOCIDO:
                break;
            case PROGRAMA_INACTIVO:
                holder.switchProgramaActivo.setChecked(false);
                break;
            case PROGRAMA_ACTIVO:
                holder.switchProgramaActivo.setChecked(true);
                break;
        }




    }


    static class ListaProgramasInterruptorAdapterHolder {

        ImageView imageInterruptor;
        SwitchCompat switchProgramaActivo;
        TextView textHoraPrograma;
        TextView textDuracionPrograma;
        TextView textDurante;
        ImageView imageBorrarPrograma;
        ImageView imageProgramaActivado;
        TextView textunidadTiempo;
        TextView textoLunes;
        TextView textoMartes;
        TextView textoMiercoles;
        TextView textoJueves;
        TextView textoViernes;
        TextView textoSabado;
        TextView textoDomingo;
        ConstraintLayout panelDiasSemana;

    }


}
