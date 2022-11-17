package net.jajica.libiot;

public enum IOT_LABELS_JSON {

    JSON_COMMAND("comando"),
    KEY_COMMAND("token"),
    JSON_ANSWER("dlgRespuesta"),
    DLG_UPGDRADE_FIRMWARE("upgradeFirmware"),
    END_UPGRADE("finUpgrade"),
    TYPE_SPONTANEOUS_REPORT("tipoReport"),
    RESULT_CODE("dlgResultCode"),
    COMMAND("dlgComando"),
    COMMAND_NAME("nombreComando"),
    DEVICE_ID("idDevice"),
    DEVICE_NAME("nombreDispositivo"),
    DEVICE_TYPE("device"),
    TOPIC_SUBSCRIPTION("topicSubscripcion"),
    TOPIC_PUBLISH("topicPublicacion"),
    DEVICES("dispositivos"),
    STATE_RELAY("estadoRele"),
    TEMPERATURE("temperatura"),
    HUMIDITY("humedad"),
    THRESHOLD_TEMPERATURE("temperaturaUmbral"),
    DEFAULT_THRESHOLD_TEMPERATURE("umbralDefecto"),
    MARGIN_TEMPERATURE("margenTemperatura"),
    READ_INTERVAL("intervaloLectura"),
    RETRY_INTERVAL("intervaloReintentos"),
    READ_NUMBER_RETRY("reintentosLectura"),
    CALIBRATE_VALUE("valorCalibrado"),
    RELAY("rele"),
    ACTION_RELAY("opRele"),
    STATUS_CONNECTION("estadoConexion"),
    STATUS_PROGRAMMER("programmerState"),
    STATUS_SCHEDULE("programState"),
    STATUS_DEVICE("deviceState"),
    OTA_SERVER("otaServer"),
    OTA_PORT("otaPort"),
    OTA_URL("otaUrl"),
    OTA_FILE("otaFile"),
    OTA_VERSION("otaVersion"),
    SCHEDULE("program"),
    SCHEDULE_ID("programId"),
    NEW_SCHEDULE_ID("newProgramId"),
    ACTIVE_SCHEDULE("currentProgramId"),
    MODIFY_APP("modificarApp"),
    CONFIGURE_APP("configApp"),
    TYPE_SCHEDULE("programType"),
    HOUR("hour"),
    MINUTE("minute"),
    SECOND("second"),
    YEAR("year"),
    MONTH("month"),
    DAY("day"),
    MASK_SCHEDULE("programMask"),
    DURATION("durationProgram"),
    WEEK_DAY("weekDay"),
    OTA_CODE("otaCode"),
    TYPE_SENSOR("master"),
    SENSOR_ID("idsensor"),
    TEMPERATURE_SENSOR("sensorTemperatura"),
    FREE_MEM("freeMem"),
    UPTIME("uptime"),
    WIFI_ALARM("alarmaWifi"),
    MQTT_ALARM("alarmaMqtt"),
    NTP_ALARM("alarmaNtp"),
    NVS_ALARM("alarmaNvs"),
    SENSOR_ALARM("alarmaSensor"),
    REMOTE_SENSOR_ALARM("alarmaSensorRemoto"),
    OTA_VERSION_TYPE("otaVersiontype");


    private String dialogo;

    IOT_LABELS_JSON(String respuesta) {

        this.dialogo = respuesta;

    }

    public String getValorTextoJson() {

        return this.dialogo;
    }


}

