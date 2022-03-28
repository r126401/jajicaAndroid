package com.example.controliot;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class ListaDispositivosAdapter extends ArrayAdapter<dispositivoIot> {

    private Context contexto;
    public ArrayList<dispositivoIot> listaDispositivos;
    private conexionMqtt cnx;
    private dialogoIot dialogo;
    private int idLayout;
    final String TAG = "ListaDispositivosAdapter";

    ListaDispositivosAdapter(Context contexto, int idLayout, ArrayList<dispositivoIot> listaDispositivos, conexionMqtt cnx, dialogoIot dialogo) {
        super(contexto, idLayout, listaDispositivos);

        this.contexto = contexto;
        this.idLayout = idLayout;
        this.listaDispositivos = listaDispositivos;
        this.cnx = cnx;
        this.dialogo = dialogo;

    }

    private void inicializarControles(ListaDispositivosAdapterHolder holder) {

        holder.estadoConexionInterruptor.setTag(Integer.valueOf(R.drawable.switch_indeterminado));
        holder.estadoConexionInterruptor.setImageResource(R.drawable.switch_indeterminado);


    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View fila, @NonNull ViewGroup parent) {

        ListaDispositivosAdapterHolder holder;
        dispositivoIot dispositivo;
        dispositivo = listaDispositivos.get(position);


        if (fila == null) {
            LayoutInflater inflater = ((Activity) contexto).getLayoutInflater();
            fila = inflater.inflate(idLayout, parent, false);
            holder = new ListaDispositivosAdapterHolder();
            holder.imageOnOff = (ImageView) fila.findViewById(R.id.imageOnOff);
            holder.textoInterruptor = (TextView) fila.findViewById(R.id.textoInterruptor);
            holder.estadoConexionInterruptor = (ImageView) fila.findViewById(R.id.estadoConexionInterruptor);
            holder.imageHeatingOnOff = (ImageView) fila.findViewById(R.id.imageHeatingOnOff);
            holder.textoTemperatura = (TextView) fila.findViewById(R.id.textoTemperatura);
            holder.textoHumedad = (TextView) fila.findViewById(R.id.textoHumedad);
            holder.textoUmbralTemperatura = (TextView) fila.findViewById(R.id.textoUmbralTemperatura);
            holder.estadoConexionTermostato = (ImageView) fila.findViewById(R.id.estadoConexionTermostato);
            holder.vista_interruptor = (ConstraintLayout) fila.findViewById(R.id.layoutVistaInterruptor);
            holder.vista_termostato = (ConstraintLayout) fila.findViewById(R.id.layoutVistaTermostato);
            holder.barraProgresoInterruptor = (ProgressBar) fila.findViewById(R.id.progresoOperacionInterruptor) ;
            holder.barraProgresoTermostato = (ProgressBar) fila.findViewById(R.id.progresoOperacionTermostato);
            holder.textNombreTermostato = (TextView) fila.findViewById(R.id.textNombreTermostato);
            holder.imageIconoUmbralTemperatura = (ImageView) fila.findViewById(R.id.imageIconoUmbralTemperatura);
            holder.imageIconoHumedad = (ImageView) fila.findViewById(R.id.imageIconoHumedad);
            holder.imageEliminarSwitch = (ImageView) fila.findViewById(R.id.imageEliminarSwitch);
            holder.imageEliminarTermostato = (ImageView) fila.findViewById(R.id.imageEliminarTermostato);



            holder.imageEliminarTermostato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarDispositivo(dispositivo, position);
                    Log.i(TAG, "he pulsado el boton de eliminar termometro");
                }
            });

            holder.imageEliminarSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarDispositivo(dispositivo, position);
                }
            });

            holder.imageOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getClass().toString(), "Hemos pulsado el interruptor");
                    int valorIcono;
                    valorIcono = (Integer) holder.imageOnOff.getTag();
                    switch (valorIcono) {

                        case R.drawable.switchon:
                            dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                            dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
                            holder.barraProgresoInterruptor.setVisibility(View.VISIBLE);
                            holder.imageOnOff.setTag(R.drawable.switchon);
                            //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                            break;
                        case R.drawable.switchoff:
                            dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.ON));
                            dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
                            holder.barraProgresoInterruptor.setVisibility(View.VISIBLE);
                            holder.imageOnOff.setTag(R.drawable.switchoff);
                            //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoActuarRele(ESTADO_RELE.ON));
                            break;


                        default:
                            Log.w(getClass().toString(), "No se puede actuar porque el estado del rele es indeterminado: ");
                            break;

                    }


                    Log.i(TAG, "He pulsado el boton del interruptor");
                }
            });

            holder.textoInterruptor.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(TAG, "Pulsacion larga para cambiar el nombre del interruptor");
                    return false;
                }
            });

            holder.textNombreTermostato.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(TAG, "Pulsacion larga para cambiar el nombre del termostato");
                    return false;
                }
            });

            // Inicializamos los valores
            inicializarControles(holder);
            fila.setTag(holder);
        } else {
            holder = (ListaDispositivosAdapterHolder) fila.getTag();


        }

        /**
         * Preparamos los eventos de los controles
         */


        Log.d(TAG, "Entramos en la logica del adapter");
        switch (dispositivo.getTipoDispositivo()) {

            case DESCONOCIDO:
                holder.vista_interruptor.setVisibility((View.VISIBLE));
                holder.vista_termostato.setVisibility(View.INVISIBLE);
                rellenarControlesDesconocidoSinEstado(holder, (dispositivoIotDesconocido) dispositivo);
                break;
            case INTERRUPTOR:
                holder.vista_interruptor.setVisibility(View.VISIBLE);
                holder.vista_termostato.setVisibility(View.GONE);
                rellenarControlesInterruptorSinEstado(holder, (dispositivoIotOnOff) dispositivo);
                break;
            case CRONOTERMOSTATO:
                holder.vista_interruptor.setVisibility(View.GONE);
                holder.vista_termostato.setVisibility(View.VISIBLE);
                rellenarControlesTermostatoSinEstado(holder, (dispositivoIotTermostato) dispositivo);
                break;
            case TERMOMETRO:
                holder.vista_interruptor.setVisibility(View.GONE);
                holder.vista_termostato.setVisibility(View.VISIBLE);
                rellenarControlesTermometroSinEstado(holder, (dispositivoIotTermostato) dispositivo);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dispositivo.getTipoDispositivo());
        }


        return fila;
    }


    static class ListaDispositivosAdapterHolder {

        ImageView imageOnOff;
        TextView textoInterruptor;
        ImageView estadoConexionInterruptor;
        ImageView imageHeatingOnOff;
        TextView textoTemperatura;
        TextView textoHumedad;
        TextView textoUmbralTemperatura;
        ImageView estadoConexionTermostato;
        TextView textNombreTermostato;
        ImageView imageIconoUmbralTemperatura;
        ConstraintLayout vista_interruptor;
        ConstraintLayout vista_termostato;
        ProgressBar barraProgresoInterruptor;
        ProgressBar barraProgresoTermostato;
        ImageView imageIconoHumedad;
        ImageView imageEliminarTermostato;
        ImageView imageEliminarSwitch;



    }

private void rellenarControlesDesconocidoSinEstado(ListaDispositivosAdapterHolder holder, dispositivoIotDesconocido dispositivo) {

        holder.imageOnOff.setVisibility(View.INVISIBLE);
        int a;
        a = (Integer) holder.estadoConexionInterruptor.getTag();
        if (a == R.drawable.bk_no_conectado) {
            holder.estadoConexionInterruptor.setImageResource(R.drawable.switch_indeterminado);

        }
        holder.textoInterruptor.setText(dispositivo.getNombreDispositivo().toUpperCase());

}






    private void rellenarControlesInterruptorSinEstado(ListaDispositivosAdapterHolder holder, dispositivoIotOnOff dispositivo) {


        int valor;

        Log.i(TAG, "delante");
        switch (dispositivo.getEstadoRele()) {

            case OFF:
                valor = R.drawable.switchoff;
                break;
            case ON:
                valor = R.drawable.switchon;
                break;
            case INDETERMINADO:
                valor = R.drawable.switch_indeterminado;
                break;
            default:
                valor = R.drawable.switch_indeterminado;
                break;
        }
        holder.imageOnOff.setImageResource(valor);
        holder.imageOnOff.setTag(Integer.valueOf(valor));
        holder.textoInterruptor.setText(dispositivo.getNombreDispositivo());

        switch (dispositivo.getEstadoConexion()) {


            case INDETERMINADO:
            case ESPERANDO_RESPUESTA:
                valor = R.drawable.switch_indeterminado;
                holder.barraProgresoInterruptor.setVisibility(View.VISIBLE);
                break;
            case CONECTADO:
                valor = R.drawable.bk_conectado;
                holder.barraProgresoInterruptor.setVisibility(View.INVISIBLE);
                break;
            case DESCONECTADO:
                valor = R.drawable.bk_no_conectado;
                holder.barraProgresoInterruptor.setVisibility(View.INVISIBLE);
                break;
        }

        holder.estadoConexionInterruptor.setImageResource(valor);
        holder.estadoConexionInterruptor.setTag(Integer.valueOf(valor));




    }

    private void rellenarControlesTermostatoSinEstado(ListaDispositivosAdapterHolder holder, dispositivoIotTermostato dispositivo) {

        double dato;
        int valor;
        rellenarControlesTermometroSinEstado(holder, (dispositivoIotTermostato) dispositivo);
        // Actualizamos el estado del rele del termostato
        holder.imageIconoUmbralTemperatura.setVisibility(View.INVISIBLE);
        holder.imageHeatingOnOff.setVisibility(View.VISIBLE);
        switch (dispositivo.getEstadoRele()) {


            case OFF:
                valor = R.drawable.heating_off;
                holder.barraProgresoTermostato.setVisibility(View.INVISIBLE);
                holder.imageHeatingOnOff.setImageResource(R.drawable.heating_off);

                break;
            case ON:
                valor = R.drawable.heating_on;
                holder.barraProgresoTermostato.setVisibility(View.INVISIBLE);
                holder.imageHeatingOnOff.setImageResource(R.drawable.heating_on);
                break;
            case INDETERMINADO:
            default:
                valor = R.drawable.switch_indeterminado;
                holder.imageHeatingOnOff.setImageResource(R.drawable.switch_indeterminado);
                break;
        }

        holder.imageHeatingOnOff.setImageResource(valor);
        holder.imageHeatingOnOff.setTag(Integer.valueOf(valor));
        dato = dispositivo.redondearDatos(dispositivo.getUmbralTemperatura(), 1);
        if (dispositivo.getEstadoConexion() == ESTADO_CONEXION_IOT.CONECTADO) {
            if (dato == -1000) {
                holder.textoUmbralTemperatura.setText("--.- ºC");
            } else {
                holder.textoUmbralTemperatura.setText(String.valueOf(dato) + " ºC");
            }
        }
        holder.imageIconoUmbralTemperatura.setVisibility(View.VISIBLE);




    }

    private void rellenarControlesTermometroSinEstado(ListaDispositivosAdapterHolder holder, dispositivoIotTermostato dispositivo) {

        int valor;
        double dato;


        holder.textNombreTermostato.setText(dispositivo.nombreDispositivo);
        holder.imageHeatingOnOff.setVisibility(View.INVISIBLE);
        holder.imageIconoUmbralTemperatura.setVisibility(View.INVISIBLE);
        holder.textoHumedad.setVisibility(View.INVISIBLE);
        holder.imageIconoHumedad.setVisibility(View.INVISIBLE);


        //Actualizamos el valor de la conexion
        switch (dispositivo.getEstadoConexion()) {


            case INDETERMINADO:
            case ESPERANDO_RESPUESTA:
            default:
                valor = R.drawable.switch_indeterminado;
                break;
            case CONECTADO:
                valor = R.drawable.bk_conectado;
                holder.barraProgresoTermostato.setVisibility(View.INVISIBLE);
                break;
            case DESCONECTADO:
                valor = R.drawable.bk_no_conectado;
                holder.barraProgresoTermostato.setVisibility(View.INVISIBLE);
                break;
        }
        holder.estadoConexionTermostato.setImageResource(valor);
        holder.estadoConexionTermostato.setTag(Integer.valueOf(valor));
        //Actualizamos el valor de la tempertaura y umbral.
        if (dispositivo.getEstadoConexion() == ESTADO_CONEXION_IOT.CONECTADO) {

            dato = dispositivo.redondearDatos(dispositivo.getTemperatura(), 1);
            if (dato == -1000) {
                holder.textoTemperatura.setText("--.- ºC");
            }
            holder.textoTemperatura.setText(String.valueOf(dato) + "ºC");

            dato = dispositivo.redondearDatos(dispositivo.getHumedad(), 1);
            if(dato == -1000) {
                holder.textoHumedad.setText("--.- %");
            } else {
                holder.textoHumedad.setText(String.valueOf(dato));
            }



        } else {
            Log.d(TAG, "No procede actualizar valores porque no esta conectado.");

        }

    }

    private void accionarBotonEliminarInterruptor(dispositivoIot dispositivo, int position) {


        Log.i(TAG, "he pulsado el boton eliminar switch");
        borrarDispositivo(dispositivo, position);



    }





    boolean borrarDispositivo(dispositivoIot dispositivo, int position) {

        final configuracionDispositivos lista;
        DialogosAplicacion dial;


        lista = new configuracionDispositivos();
        dial = new DialogosAplicacion();

        Resources cadena;
        cadena = contexto.getResources();


        if (lista.leerDispositivos(contexto) == true) {
            dial.ventanaDialogo(contexto, COMANDO_IOT.ESTADO, cadena.getString(R.string.eliminarDispositivo), cadena.getString(R.string.avisoEliminarDispositivo), R.drawable.ic_delete).show();
            dial.setOnDialogosAplicacion(new DialogosAplicacion.OnDialogosAplicacion() {
                @Override
                public void OnAceptarListener(COMANDO_IOT idComando) {
                    lista.eliminarDispositivo(dispositivo, contexto);
                    listaDispositivos.remove(position);
                    remove(dispositivo);
                    notifyDataSetChanged();

                }

                @Override
                public void OnCancelarListener() {

                }
            });

            return true;

        } else {
            return false;
        }

    }



}
