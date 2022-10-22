package net.jajica.libiot;


public class IotDeviceSwitch extends IotDevice {

    private IOT_SWITCH_RELAY Relay;

    public IOT_SWITCH_RELAY getRelay() {
        return Relay;
    }

    public void setRelay(IOT_SWITCH_RELAY relay) {
        this.Relay = relay;
    }

    public IotDeviceSwitch(MqttConnection cnx) {
        super(cnx);
    }

    @Override
    protected RESULT_CODE processStatus(String respuesta) {

        int res;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        IOT_SWITCH_RELAY estado = IOT_SWITCH_RELAY.UNKNOWN;
        res = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        setRelay(estado.fromId(res));
        return super.processStatus(respuesta);
    }



}
