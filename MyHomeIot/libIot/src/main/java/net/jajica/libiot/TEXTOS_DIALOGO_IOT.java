package net.jajica.libiot;

public enum TEXTOS_DIALOGO_IOT {

    COMANDO("comando"),
    CLAVE("token"),
    DLG_RESPUESTA("dlgRespuesta"),
    DLG_UPGDRADE_FIRMWARE("upgradeFirmware"),
    FIN_UPGRADE("finUpgrade"),
    TIPO_INFORME_ESPONTANEO("tipoReport"),
    CODIGO_RESPUESTA("dlgResultCode"),
    DLG_COMANDO("dlgComando"),
    NOMBRE_COMANDO("nombreComando"),
    ID_DISPOSITIVO("idDevice"),
    NOMBRE_DISPOSITIVO("nombreDispositivo"),
    TIPO_DISPOSITIVO("device"),
    TOPIC_SUBSCRICION("topicSubscripcion"),
    TOPIC_PUBLICACION("topicPublicacion"),
    DISPOSITIVOS("dispositivos"),
    ESTADO_RELE("estadoRele"),
    TEMPERATURA("temperatura"),
    HUMEDAD("humedad"),
    UMBRAL_TEMPERATURA("temperaturaUmbral"),
    UMBRAL_TEMPERATURA_DEFECTO("umbralDefecto"),
    MARGEN_TEMPERATURA("margenTemperatura"),
    INTERVALO_LECTURA("intervaloLectura"),
    INTERVALO_REINTENTOS("intervaloReintentos"),
    REINTENTOS_LECTURA("reintentosLectura"),
    VALOR_CALIBRADO("valorCalibrado"),
    RELE("rele"),
    OP_RELE("opRele"),
    ESTADO_CONEXION("estadoConexion"),
    ESTADO_PROGRAMACION("programmerState"),
    ESTADO_DISPOSITIVO("deviceState"),
    SERVIDOR_OTA("otaServer"),
    PUERTO_OTA("otaPort"),
    URL_OTA("otaUrl"),
    FICHERO_OTA("otaFile"),
    VERSION_OTA("otaVersion"),
    PROGRAMAS("program"),
    ID_PROGRAMA("programId"),
    NUEVO_ID_PROGRAMA("newProgramId"),
    PROGRAMA_ACTIVO("currentProgramId"),
    MODIFICAR_APP("modificarApp"),
    CONFIGURACION_APP("configApp"),
    TIPO_PROGRAMA("programType"),
    HORA("hour"),
    MINUTO("minute"),
    SEGUNDO("second"),
    ANO("year"),
    MES("month"),
    DIA("day"),
    MASCARA_PROGRAMA("programMask"),
    DURACION("durationProgram"),
    DIA_SEMANA("weekDay"),
    CODIGO_OTA("otaCode"),
    TIPO_SENSOR("master"),
    ID_SENSOR("idsensor"),
    SENSOR_TEMPERATURA("sensorTemperatura");


    private String dialogo;

    TEXTOS_DIALOGO_IOT(String respuesta) {

        this.dialogo = respuesta;

    }

    public String getValorTextoJson() {

        return this.dialogo;
    }


}
