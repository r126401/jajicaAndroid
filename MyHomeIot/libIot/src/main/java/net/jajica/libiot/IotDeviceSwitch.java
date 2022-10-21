package net.jajica.libiot;


public class IotDeviceSwitch extends IotDevice {

    private ESTADO_RELE estadoRele;

    public ESTADO_RELE getEstadoRele() {
        return estadoRele;
    }

    public void setEstadoRele(ESTADO_RELE estadoRele) {
        this.estadoRele = estadoRele;
    }

    public IotDeviceSwitch(ConexionMqtt cnx) {
        super(cnx);
    }





    @Override
    protected RESULT_CODE procesarStatus(String respuesta) {

        int res;
        ApiDispositivoIot api;
        api = new ApiDispositivoIot();
        ESTADO_RELE estado = ESTADO_RELE.INDETERMINADO;
        res = api.getJsonInt(respuesta, TEXTOS_DIALOGO_IOT.ESTADO_RELE.getValorTextoJson());
        setEstadoRele(estado.fromId(res));
        return super.procesarStatus(respuesta);
    }
}
