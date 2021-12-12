package com.iotcontrol;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class PanelNotificacion {

    protected boolean versionComprobada;
    private ImageView iconoNotificacion;
    private TextView textoNotificacion;

    public void setIconoNotificacion(ImageView icono) {
        this.iconoNotificacion = icono;
    }

    public void setTextoNotificacion(TextView textoNotificacion) {
        this.textoNotificacion = textoNotificacion;
    }

    public ImageView getIconoNotificacion() {
        return this.iconoNotificacion;
    }

    public TextView getTextoNotificacion() {
        return this.textoNotificacion;
    }

    PanelNotificacion() {

        this.versionComprobada = false;
    }

    private boolean comprobarSiHayNuevaVersionDisponible(dispositivoIot dispositivo) {

        if (dispositivo.getOtaVersion() > 0) {
            if (dispositivo.datosOta.getOtaVersionAvailable() > dispositivo.getOtaVersion()) {
                Log.i(getClass().toString(), "Hay una nueva version disponible en el servidor OTA");
                versionComprobada = true;
            } else {
                Log.i(getClass().toString(), "No hay nueva version disponible en el servidor OTA");
                versionComprobada = false;
            }

        } else {
            versionComprobada = false;
        }

        return versionComprobada;

    }


    public  void hayNuevaVersionDisponible( dispositivoIot dispositivo) {

        if (comprobarSiHayNuevaVersionDisponible(dispositivo) == true) {

            iconoNotificacion.setImageResource(R.drawable.upgrade);
            textoNotificacion.setText("Hay una nueva version disponible: " + dispositivo.datosOta.getOtaVersionAvailable());
        } else {
            iconoNotificacion.setImageResource(R.drawable.ready);
            textoNotificacion.setText("dispositivo disponible");
        }


    }

    public void notificarEstadoConexionBroker(ImageView icono, TextView texto, ESTADO_CONEXION_IOT estado) {

        switch (estado) {

            case INDETERMINADO:
                icono.setImageResource(R.drawable.warning);
                break;

            case CONECTADO:
                icono.setImageResource(R.drawable.bkconectado);
                texto.setText("Broker conectado...");
                break;

            case DESCONECTADO:
                icono.setImageResource(R.drawable.bkconectadooff);
                texto.setText("Broker desconectado...");
                break;

        }

    }


    /**
     * Esta funcion escribira en el panel la accion que este ocurriendo en ese momento
     * @param icono
     * @param mensaje
     */
    public void escribirMensajePanel(int icono, String mensaje) {

        iconoNotificacion.setImageResource(icono);
        textoNotificacion.setText(mensaje);






    }






}
