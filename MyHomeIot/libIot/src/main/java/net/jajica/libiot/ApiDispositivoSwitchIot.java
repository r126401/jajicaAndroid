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

    public ApiDispositivoSwitchIot(MqttConnection cnx) {
        super(cnx);
    }

    public interface OnMessagesReceived {

        public void recibirRespuestaComando(IotDeviceSwitch dispositivo);
        public void recibirMensajeEspontaneo(IotDeviceSwitch dispositivo);
    }



    @Override
    protected void procesarComando(String topic, MqttMessage message) {
        super.procesarComando(topic, message);
        IOT_COMMANDS idComando;

        String mensaje = new String(message.getPayload());
        idComando = getCommandId(mensaje);

        switch (idComando) {

            case INFO_DEVICE:
                break;
            case SET_RELAY:
                break;
            case STATUS_DEVICE:
                break;
            case GET_SCHEDULE:
                break;
            case NEW_SCHEDULE:
                break;
            case REMOVE_SCHEDULE:
                break;
            case MODIFY_SCHEDULE:
                break;
            case MODIFY_PARAMETER_DEVICE:
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
            case ERROR_REPORT:
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
