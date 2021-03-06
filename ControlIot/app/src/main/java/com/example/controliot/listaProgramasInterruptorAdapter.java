package com.example.controliot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Calendar;
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

    private void inhibirPrograma(ProgramaDispositivoIotOnOff programa,ListaProgramasInterruptorAdapterHolder holder) {
        dialogoIot dialogo;
        dialogo = new dialogoIot(cnx);
        if (holder.switchProgramaActivo.isChecked()) {
            programa.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_ACTIVO);
        } else {
            programa.setEstadoPrograma(ESTADO_PROGRAMA.PROGRAMA_INACTIVO);
        }

        dialogo.enviarComando(dispositivo, dialogo.comandoInhibirProgramacion(programa));


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
            holder.imageInterruptor = (ImageView) convertView.findViewById(R.id.imageHeating);
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
            holder.textDurante = (TextView) convertView.findViewById(R.id.textTemperatura);
            holder.textDuracionPrograma = (TextView) convertView.findViewById(R.id.textoUmbral);
            holder.imageBorrarPrograma = (ImageView) convertView.findViewById(R.id.imageBorrarPrograma);
            holder.imageProgramaActivado = (ImageView) convertView.findViewById(R.id.imageProgramaActivado);
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
                    View a = parent.getRootView();
                    ProgressBar b = a.findViewById(R.id.barraProgreso);
                    b.setVisibility(View.VISIBLE);
                    TextView textoConsola = a.findViewById(R.id.textConsolaMensajes);
                    textoConsola.setText("Inhibiendo programa...");
                    inhibirPrograma(listaProgramas.get(position), holder);
                    if (holder.switchProgramaActivo.isChecked()) {
                        holder.switchProgramaActivo.setChecked(false);
                    } else {
                        holder.switchProgramaActivo.setChecked(true);
                    }


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

        if (programa.getEstadoRele() == ESTADO_RELE.ON) {
            holder.imageInterruptor.setImageResource(R.drawable.switch_on);
            holder.imageInterruptor.setTag(true);
        } else {
            holder.imageInterruptor.setImageResource(R.drawable.switch_off);
            holder.imageInterruptor.setTag(false);
        }


        return convertView;


    }

    private void actualizarPanelDiaSemana(TextView dia, boolean activado) {

        if (activado == false) {
            //dia.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xcc));
            dia.setBackgroundResource(R.drawable.texto_redondeado_desactivado);
            dia.setTag(false);
        } else {
            dia.setBackgroundResource(R.drawable.redondeado);
            //dia.setBackgroundColor(Color.rgb(0x34, 0x98, 0xdb));
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
                if (programa.getDuracion() == 0) {
                    holder.textDurante.setVisibility(View.GONE);
                    holder.textDuracionPrograma.setText(" Permanente");
                } else {
                    holder.textDurante.setVisibility(View.VISIBLE);
                    holder.textDurante.setText(" hasta las ");
                    holder.textDuracionPrograma.setText(convertirDuracion(programa.getHora(), programa.getMinuto(), programa.getDuracion()));

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
        TextView textoLunes;
        TextView textoMartes;
        TextView textoMiercoles;
        TextView textoJueves;
        TextView textoViernes;
        TextView textoSabado;
        TextView textoDomingo;
        ConstraintLayout panelDiasSemana;

    }

    private String convertirDuracion(int hora, int minuto, int duracion) {

        Calendar fecha;
        String horafinal;
        if (duracion == 0) {
            horafinal = "siempre";
        } else {
            fecha = Calendar.getInstance();
            fecha.set(Calendar.HOUR_OF_DAY, hora);
            fecha.set(Calendar.MINUTE, minuto);
            fecha.set(Calendar.SECOND, duracion);


            horafinal = formatearHora(fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));

        }
        return horafinal;


    }


}
