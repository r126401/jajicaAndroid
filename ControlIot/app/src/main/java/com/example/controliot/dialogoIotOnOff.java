package com.example.controliot;

import android.content.Context;
import android.icu.util.CopticCalendar;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.OptionalInt;







public class dialogoIotOnOff extends dialogoIot{



    private onRecibirMensajesListenerCompleto listenerCompleto;
    private onRecibirMensajesListenerBasico listenerBasico;
    private final String TAG = "dialogoIotOnOff";


    public interface onRecibirMensajesListenerCompleto {

        void espontaneoArranqueAplicacion(dispositivoIot dispositivo);


    }

    public interface onRecibirMensajesListenerBasico {
        void espontaneoArranqueAplicacion(dispositivoIot dispositivo);
    }


    private COMANDO_IOT_ONOFF descubrirComandoIotOnOff(String texto) {

        int idComando = -100;
        JSONObject objetoJson = null;

        try {
            objetoJson = new JSONObject(texto);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            idComando = objetoJson.getInt(TEXTOS_DIALOGO_IOT.DLG_COMANDO.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            return COMANDO_IOT_ONOFF.ESPONTANEO;
        }
        return COMANDO_IOT_ONOFF.ESPONTANEO.fromId(idComando);

    }

    private ESPONTANEO_IOT_ONOFF descubrirTipoInformeEspontaneoIotOnOff(String texto) {

        int tipoInforme;
        tipoInforme = extraerDatoJsonInt(texto, TEXTOS_DIALOGO_IOT.TIPO_INFORME_ESPONTANEO.getValorTextoJson());


        return ESPONTANEO_IOT_ONOFF.ESPONTANEO_DESCONOCIDO.fromId(tipoInforme);
    }

    public void setOnRecibirMensajesListener(onRecibirMensajesListenerCompleto listener) {

        this.listenerCompleto = listener;
    }

    public void setOnRecibirMensajesListenerBasico(onRecibirMensajesListenerBasico listener) {
        this.listenerBasico = listener;
    }


    private dispositivoIotOnOff procesarEstadoDispositivo(String topic, String texto, Context contexto) {

        JSONObject mensaje;
        String idDispositivo;
        configuracionDispositivos confDisp;
        try {
            mensaje = new JSONObject(texto);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "El mensaje no es json");
            return null;

        }

        dispositivoIot dispositivo;
        idDispositivo = mensaje.optString(TEXTOS_DIALOGO_IOT.ID_DISPOSITIVO.getValorTextoJson());
        confDisp = new configuracionDispositivos(contexto);
        dispositivo = confDisp.getDispositivoPorId(idDispositivo);
        dispositivoIotOnOff dispIotOnOff;
        dispIotOnOff = new dispositivoIotOnOff(dispositivo);
        ESTADO_RELE estado = getEstadoRele(TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        dispIotOnOff.setEstadoRele(estado);

        return dispIotOnOff;
    }



    void procesarMensajeEspontaneo(String topic, MqttMessage message, Context contexto) {

        ESPONTANEO_IOT_ONOFF tipoInformeEspontaneo;
        String texto = new String(message.getPayload());

        tipoInformeEspontaneo = descubrirTipoInformeEspontaneoIotOnOff(texto);
        switch (tipoInformeEspontaneo) {

            case ARRANQUE_APLICACION:
            case ACTUACION_RELE_LOCAL:
            case ACTUACION_RELE_REMOTO:
                dispositivoIotOnOff dispositivo;
                dispositivo = procesarEstadoDispositivo(topic, texto, contexto);
                break;
            case UPGRADE_FIRMWARE_FOTA:
                break;
            case CAMBIO_DE_PROGRAMA:
                break;
            case ESPONTANEO_DESCONOCIDO:
                break;
        }

    }

    void procesarRespuestaComando(String topic, MqttMessage message, Context contexto) {

        COMANDO_IOT_ONOFF idComando;
        String texto = new String(message.getPayload());
        idComando = descubrirComandoIotOnOff(texto);
        switch (idComando) {

            case CONSULTAR_CONF_APP:
                break;
            case ACTUAR_RELE:
                break;
            case ESTADO:
                dispositivoIotOnOff dispositivo;
                dispositivo = procesarEstadoDispositivo(topic, texto, contexto);
                listenerCompleto.espontaneoArranqueAplicacion(dispositivo);
                listenerBasico.espontaneoArranqueAplicacion(dispositivo);
                break;
            case CONSULTAR_PROGRAMACION:
                break;
            case NUEVA_PROGRAMACION:
                break;
            case ELIMINAR_PROGRAMACION:
                break;
            case MODIFICAR_PROGRAMACION:
                break;
            case MODIFICAR_APP:
                break;
            case RESET:
                break;
            case FACTORY_RESET:
                break;
            case MODIFY_CLOCK:
                break;
            case UPGRADE_FIRMWARE:
                break;
            case ESPONTANEO:
                break;
            case VERSION_OTA:
                break;
            case ERROR_RESPUESTA:
                break;
        }

    }



    public void procesarMensajes(String topic, MqttMessage message, Context contexto) {

        COMANDO_IOT_ONOFF idComando;
        String texto = new String(message.getPayload());;
        idComando = descubrirComandoIotOnOff(texto);

        if (idComando == COMANDO_IOT_ONOFF.ESPONTANEO) {

            procesarMensajeEspontaneo(topic, message, contexto);
        } else {
            procesarRespuestaComando(topic, message, contexto);
        }






    }
}
