package com.iotcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;





import java.util.ArrayList;

public class DispositivosAdapter extends ArrayAdapter<dispositivoIot> {

    Context contexto;
    int layout;
    ArrayList<dispositivoIot> dispositivos;
    conexionMqtt cnx;
    dialogoIot dialogo;


    DispositivosAdapter(Context contexto, int layout, ArrayList<dispositivoIot> dispositivos, conexionMqtt cnx, dialogoIot dialogo) {
        super(contexto, layout, dispositivos);

        this.contexto = contexto;
        this.layout = layout;
        this.dispositivos = dispositivos;
        this.cnx = cnx;
        this.dialogo = dialogo;
    }


    private void rellenarControlesCronotermostatoSinEstado(DispositivosAdapterHolder holder, dispositivoIot dispositivo) {


        dispositivoIotTermostato dispo;
        int valorIcono = -1;
        double dato = -1000;

        dispo = (dispositivoIotTermostato) dispositivo;

        switch (dispo.estadoRele) {

            case ON:
                valorIcono = R.drawable.heating;
                break;
            case OFF:
                valorIcono = R.drawable.noheating;
                break;
            case INDETERMINADO:
                valorIcono = R.drawable.nodeterminado;
                break;
        }

        holder.iconoCronotermostato.setImageResource(valorIcono);
        holder.iconoCronotermostato.setTag(Integer.valueOf(valorIcono));
        Log.i(getClass().toString(), "estado: " + dispositivo.getEstadoConexion().toString());
        holder.textoCronotermostato.setText(dispo.nombreDispositivo);

        switch (dispo.estadoConexion) {
            case CONECTADO:
                valorIcono = R.drawable.bkconectado;
                holder.progresoCronotermostato.setVisibility(View.INVISIBLE);
                break;
            case DESCONECTADO:
                valorIcono = R.drawable.bkconectadooff;
                holder.progresoCronotermostato.setVisibility(View.INVISIBLE);
                break;

            case ESPERANDO_RESPUESTA:
                valorIcono = R.drawable.nodeterminado;
                holder.progresoCronotermostato.setVisibility(View.VISIBLE);
                break;

            default:
                valorIcono = R.drawable.nodeterminado;
                holder.progresoCronotermostato.setVisibility(View.INVISIBLE);
                break;

        }
        holder.iconoEstadoConexionCronotermostato.setImageResource(valorIcono);
        dato = dispo.redondearDatos(dispo.temperatura, 1);
        if (dato == -1000) {
            holder.textoTemperatura.setText("--.-");
        } else {

            holder.textoTemperatura.setText(String.valueOf(dato));
        }

        dato = dispo.redondearDatos(dispo.humedad, 1);
        if (dato == -1000) {
            holder.textoHumedad.setText("--.-");
        } else {

            holder.textoHumedad.setText(String.valueOf(dato));
        }
        dato = dispo.redondearDatos(dispo.umbralTemperatura, 1);
        if (dato == -1000) {
            holder.textoUmbralTemperatura.setText("--.-");
        } else {

            holder.textoUmbralTemperatura.setText(String.valueOf(dato));
        }

        Log.i(getClass().toString(), "Fin de rellenar Cronotermostato");

    }

    private void rellenarControlesInterruptorSinEstado(DispositivosAdapterHolder holder, dispositivoIot dispositivo) {

        dispositivoIotOnOff dispo;
        int valorIcono = -1;

        dispo = (dispositivoIotOnOff) dispositivo;

        switch (dispo.estadoRele) {

            case ON:
                valorIcono = R.drawable.interruptor_encendido;
                break;
            case OFF:
                valorIcono = R.drawable.interruptor_apagado;
                break;
            case INDETERMINADO:
                valorIcono = R.drawable.nodeterminado;
                break;
        }



        holder.iconoInterruptor.setImageResource(valorIcono);
        holder.iconoInterruptor.setTag(Integer.valueOf(valorIcono));
        holder.textoInterruptor.setText(dispo.nombreDispositivo);


        switch (dispo.estadoConexion) {
            case CONECTADO:
                valorIcono = R.drawable.bkconectado;
                holder.progresoInterruptor.setVisibility(View.INVISIBLE);
                break;
            case DESCONECTADO:
                valorIcono = R.drawable.bkconectadooff;
                holder.progresoInterruptor.setVisibility(View.INVISIBLE);
                break;
            case ESPERANDO_RESPUESTA:
                valorIcono = R.drawable.nodeterminado;
                holder.progresoInterruptor.setVisibility(View.VISIBLE);
                break;

                default:
                    valorIcono = R.drawable.nodeterminado;
                    holder.progresoInterruptor.setVisibility(View.INVISIBLE);
                    break;

        }
        holder.iconoEstadoConexionInterruptor.setImageResource(valorIcono);


    }



    public View getView(int posicion, View vista, ViewGroup parent) {


        DispositivosAdapterHolder holder = null;

        final dispositivoIot dispositivo = dispositivos.get(posicion);

        if (vista == null) {
            LayoutInflater inflater = ((Activity) contexto).getLayoutInflater();
            vista = inflater.inflate(layout, parent, false);
            holder = new DispositivosAdapterHolder();
            holder.iconoInterruptor = (ImageView) vista.findViewById(R.id.iconoInterruptor);
            holder.textoInterruptor = (TextView) vista.findViewById(R.id.nombreInterruptor);
            holder.iconoEstadoConexionInterruptor = (ImageView) vista.findViewById(R.id.iconoEstadoConexionInterruptor);
            holder.iconoCronotermostato = (ImageView) vista.findViewById(R.id.iconoCronotermostato);
            holder.textoCronotermostato = (TextView) vista.findViewById(R.id.nombreCronotermostato);
            holder.iconoEstadoConexionCronotermostato = (ImageView) vista.findViewById(R.id.iconoEstadoConexionCronotermostato);
            holder.layoutInterruptor = (LinearLayout) vista.findViewById(R.id.layoutInterruptor);
            holder.layoutCronotermostato = (LinearLayout) vista.findViewById(R.id.layoutCronotermostato);
            holder.textoTemperatura = (TextView) vista.findViewById(R.id.textoTemperatura);
            holder.textoHumedad = (TextView) vista.findViewById(R.id.textoHumedad);
            holder.textoUmbralTemperatura = (TextView) vista.findViewById(R.id.textoUmbralTemperatura);
            holder.iconoBorrarCronotermostato = (ImageView) vista.findViewById(R.id.iconoBorrarCronotermostato);
            holder.iconoBorrarInterruptor = (ImageView) vista.findViewById(R.id.iconoBorrarInterruptor);
            holder.progresoInterruptor = (ProgressBar) vista.findViewById(R.id.progressInterruptor);
            holder.progresoCronotermostato = (ProgressBar) vista.findViewById(R.id.progresoCronotermostato);
            vista.setTag(holder);
            Log.i(getClass().toString(), "rellenando el adapter en la posicion" + posicion);


        } else {

            holder = (DispositivosAdapterHolder) vista.getTag();
        }

        Log.i("IotControl", "entramos en el adapter general");
        switch (dispositivo.tipoDispositivo) {
            case INTERRUPTOR:
                Log.i(getClass().toString(), "Rellenando Interruptor en pasada " + posicion);
                holder.layoutInterruptor.setVisibility(View.VISIBLE);
                holder.layoutCronotermostato.setVisibility(View.GONE);
                rellenarControlesInterruptorSinEstado(holder, dispositivo );
                break;
            case CRONOTERMOSTATO:
                Log.i(getClass().toString(), "Rellenando Cronotermostato en pasada " + posicion);
                holder.layoutInterruptor.setVisibility(View.GONE);
                holder.layoutCronotermostato.setVisibility(View.VISIBLE);
                rellenarControlesCronotermostatoSinEstado(holder, dispositivo);

                break;
            default:
                Log.i(getClass().toString(), "pasada: " + posicion);
                holder.layoutInterruptor.setVisibility(View.VISIBLE);
                holder.layoutCronotermostato.setVisibility(View.GONE);
                holder.iconoInterruptor.setImageResource(R.drawable.nodeterminado);
                holder.iconoInterruptor.setTag(Integer.valueOf(R.drawable.nodeterminado));
                //holder.iconoCronotermostato.setTag(Integer.valueOf(R.drawable.nodeterminado));
                holder.textoInterruptor.setText(dispositivo.nombreDispositivo);
                holder.iconoEstadoConexionInterruptor.setImageResource(R.drawable.nodeterminado);
                break;


        }

        final DispositivosAdapterHolder finalHolder = holder;
        holder.iconoInterruptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().toString(), "Hemos pulsado el interruptor");
                int valorIcono;
                valorIcono = (Integer) finalHolder.iconoInterruptor.getTag();
                switch (valorIcono) {

                    case R.drawable.interruptor_encendido:
                        dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
                        finalHolder.progresoInterruptor.setVisibility(View.VISIBLE);
                        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                    break;
                    case R.drawable.interruptor_apagado:
                        dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.ON));
                        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
                        finalHolder.progresoInterruptor.setVisibility(View.VISIBLE);

                        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoActuarRele(ESTADO_RELE.ON));
                        break;


                        default:
                            Log.w(getClass().toString(), "No se puede actuar porque el estado del rele es indeterminado: ");
                            break;

                }


            }


        });

        holder.iconoEstadoConexionInterruptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.enviarComando(dispositivo, dialogo.comandoEstadoDispositivo());
                finalHolder.progresoInterruptor.setVisibility(View.VISIBLE);
            }
        });


        final dispositivoIot finalDispositivo = dispositivo;
        holder.iconoCronotermostato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().toString(), "Hemos pulsado el Cronotermostato");
                int valorIcono;
                valorIcono = (Integer) finalHolder.iconoCronotermostato.getTag();
                switch (valorIcono) {

                    case R.drawable.heating:
                        dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
                        finalHolder.progresoCronotermostato.setVisibility(View.VISIBLE);

                        //cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoActuarRele(ESTADO_RELE.OFF));
                        break;
                    case R.drawable.noheating:
                        dialogo.enviarComando(dispositivo, dialogo.comandoActuarRele(ESTADO_RELE.ON));
                        dispositivo.setEstadoConexion(ESTADO_CONEXION_IOT.ESPERANDO_RESPUESTA);
                        finalHolder.progresoCronotermostato.setVisibility(View.VISIBLE);

//                        cnx.publicarTopic(dispositivo.getTopicPublicacion(), dialogo.comandoActuarRele(ESTADO_RELE.ON));
                        break;

                    default:
                        Log.w(getClass().toString(), "No se puede actuar porque el estado del rele es indeterminado: ");
                        break;

                }

            }
        });

        holder.iconoEstadoConexionCronotermostato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.enviarComando(dispositivo, dialogo.comandoEstadoDispositivo());
                finalHolder.progresoCronotermostato.setVisibility(View.VISIBLE);
            }
        });

        holder.iconoBorrarInterruptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().toString(),"Borrando interruptor");
                borrarDispositivo(dispositivo);

            }
        });

        holder.iconoBorrarCronotermostato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().toString(), "Borrando cronotermostato");
                borrarDispositivo(dispositivo);

            }
        });

        Log.i(getClass().toString(), "Posicion: " + posicion + " Finalizando el pintado de " + dispositivo.getTipoDispositivo().toString());

        return vista;
    }



    static class DispositivosAdapterHolder {

        ImageView iconoInterruptor;
        TextView textoInterruptor;
        ImageView iconoEstadoConexionInterruptor;
        ImageView iconoCronotermostato;
        TextView textoCronotermostato;
        ImageView iconoEstadoConexionCronotermostato;
        LinearLayout layoutInterruptor;
        LinearLayout layoutCronotermostato;
        TextView textoTemperatura;
        TextView textoHumedad;
        TextView textoUmbralTemperatura;
        ImageView iconoBorrarCronotermostato;
        ImageView iconoBorrarInterruptor;
        ProgressBar progresoInterruptor;
        ProgressBar progresoCronotermostato;




    }

    boolean borrarDispositivo(final dispositivoIot dispositivo) {

        final configuracionDispositivos lista;
        DialogosAplicacion dial;


        lista = new configuracionDispositivos();
        dial = new DialogosAplicacion();

        Resources cadena;
        String pepe= null;
        cadena = contexto.getResources();
        pepe= cadena.getString(R.string.idDispositivo);

        if (lista.leerDispositivos(contexto) == true) {
            dial.ventanaDialogo(contexto, COMANDO_IOT.ESTADO, cadena.getString(R.string.eliminarDispositivo), cadena.getString(R.string.avisoEliminarDispositivo), R.drawable.delete).show();
            dial.setOnDialogosAplicacion(new DialogosAplicacion.OnDialogosAplicacion() {
                @Override
                public void OnAceptarListener(COMANDO_IOT idComando) {
                    remove(dispositivo);
                    lista.eliminarDispositivo(dispositivo, contexto);
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


