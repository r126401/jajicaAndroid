package net.jajica.libiot;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ApiDispositivoSwitchIot extends ApiDispositivoIot{


    private OnMessagesReceived listenerMessagesReceived;

    public OnMessagesReceived getListenerMessagesReceived() {
        return listenerMessagesReceived;
    }

    public void setListenerMessagesReceived(OnMessagesReceived listenerMessagesReceived) {
        this.listenerMessagesReceived = listenerMessagesReceived;
    }

    public ApiDispositivoSwitchIot(ConexionMqtt cnx) {
        super(cnx);
    }

    public interface OnMessagesReceived {

        public void recibirRespuestaComando(IotDeviceSwitch dispositivo);
        public void recibirMensajeEspontaneo(IotDeviceSwitch dispositivo);
    }



    @Override
    protected void procesarComando(String topic, MqttMessage message) {
        super.procesarComando(topic, message);
        COMANDO_IOT idComando;

        String mensaje = new String(message.getPayload());
        idComando = getCommandId(mensaje);

        switch (idComando) {

            case CONSULTAR_CONF_APP:
                break;
            case ACTUAR_RELE:
                break;
            case ESTADO:
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
            case VERSION_OTA:
                break;
            case ERROR_RESPUESTA:
                break;
            default:
                break;
        }

        IotDeviceSwitch dispositivo = null;
        listenerMessagesReceived.recibirRespuestaComando(dispositivo);
    }



    @Override
    protected void procesarEspontaneo(String topic, MqttMessage message) {
        super.procesarEspontaneo(topic, message);
        IotDeviceSwitch dispositivo = null;
        listenerMessagesReceived.recibirMensajeEspontaneo(dispositivo);
    }



}
