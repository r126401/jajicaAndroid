package net.jajica.libiot;

public enum IOT_LABELS_JSON {

    JSON_COMMAND("comando", false),
    KEY_COMMAND("token", false),
    JSON_ANSWER("dlgRespuesta", false),
    DLG_UPGDRADE_FIRMWARE("upgradeFirmware", false),
    END_UPGRADE("finUpgrade", false),
    TYPE_SPONTANEOUS_REPORT("tipoReport", false),
    RESULT_CODE("dlgResultCode", false),
    COMMAND("dlgComando", false),
    COMMAND_NAME("nombreComando", false),
    DEVICE_ID("idDevice", false),
    DEVICE_NAME("nombreDispositivo", true),
    DEVICE_TYPE("device", false),
    TOPIC_SUBSCRIPTION("topicSubscripcion", false),
    TOPIC_PUBLISH("topicPublicacion", false),
    DEVICES("dispositivos", false),
    STATE_RELAY("estadoRele", false),
    TEMPERATURE("temperatura", false),
    HUMIDITY("humedad", false),
    THRESHOLD_TEMPERATURE("temperaturaUmbral", false),
    DEFAULT_THRESHOLD_TEMPERATURE("umbralDefecto", true),
    MARGIN_TEMPERATURE("margenTemperatura", true),
    READ_INTERVAL("intervaloLectura", true),
    RETRY_INTERVAL("intervaloReintentos", true),
    READ_NUMBER_RETRY("reintentosLectura", true),
    CALIBRATE_VALUE("valorCalibrado", true),
    RELAY("rele", false),
    ACTION_RELAY("opRele", false),
    STATUS_CONNECTION("estadoConexion", false),
    STATUS_PROGRAMMER("programmerState", false),

    NUMBER_PROGRAMS("programNumbers", false),
    STATUS_SCHEDULE("programState", false),
    STATUS_DEVICE("deviceState", false),
    OTA_SERVER("otaServer", false),
    OTA_PORT("otaPort", false),
    OTA_URL("otaUrl", false),
    OTA_FILE("otaFile", false),
    OTA_VERSION("otaVersion", false),
    SCHEDULE("program", false),
    SCHEDULE_ID("programId", false),
    NEW_SCHEDULE_ID("newProgramId", false),
    ACTIVE_SCHEDULE("currentProgramId", false),
    MODIFY_APP("modificarApp", false),
    CONFIGURE_APP("configApp", false),
    TYPE_SCHEDULE("programType", false),
    HOUR("hour", false),
    MINUTE("minute", false),
    SECOND("second", false),
    YEAR("year", false),
    MONTH("month", false),
    DAY("day", false),
    MASK_SCHEDULE("programMask", false),
    DURATION("durationProgram", false),
    WEEK_DAY("weekDay", false),
    OTA_CODE("otaCode", false),
    TYPE_SENSOR("master", true),
    SENSOR_ID("idsensor", false),
    TEMPERATURE_SENSOR("sensorTemperatura", false),
    FREE_MEM("freeMem", false),
    UPTIME("uptime", false),
    WIFI_ALARM("alarmaWifi", false),
    MQTT_ALARM("alarmaMqtt", false),
    NTP_ALARM("alarmaNtp", false),
    NVS_ALARM("alarmaNvs", false),
    SENSOR_ALARM("alarmaSensor", false),
    REMOTE_SENSOR_ALARM("alarmaSensorRemoto", false),
    OTA_VERSION_TYPE("otaVersiontype", false),
    SITE("site", false),
    ROOM("room", false),
    NAME_SITE("nameSite", false),
    ID_SITE("idSite", false),
    DNI("dni", false),
    USER("user", false),
    ID_USER("idUser", false),
    PASSWORD("password", false),
    MAIL("mail", false),
    TELEPHONE("Telephone", false),
    STREET("street", false),
    STREET_NUMBER("streetNumber", false),
    PO_BOX("poBox", false),
    CITY("city", false),
    COUNTRY("country", false),
    LONGITUDE("longitude", false),
    LATITUDE("latitude", false),
    NAME_ROOM("nameRoom", false),
    ID_ROOM("idRoom", false),
    PROVINCE("province", false),
    CURRENT_SITE("currentSite", false),
    NEW_DEVICE("newDevice", false);


    private String dialogo;
    private Boolean configurable;

    IOT_LABELS_JSON(String respuesta, boolean b) {

        this.dialogo = respuesta;
        this.configurable = b;


    }

    public String getValorTextoJson() {

        return this.dialogo;
    }

    public Boolean isConfigurable() {
        return configurable;
    }


    public IOT_LABELS_JSON fromlabel(String id) {

        for (IOT_LABELS_JSON label : values()) {

            if (label.getValorTextoJson().equals(id)) return label;
        }
        return null;
    }
}

