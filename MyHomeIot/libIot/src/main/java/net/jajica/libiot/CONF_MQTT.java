package net.jajica.libiot;

/**
 * Enum en el que aparecen los valores json a utilizar para el fichero de configuracion mqtt
 */
public enum CONF_MQTT {
    MQTT("mqtt"),
    BROKER("broker"),
    PUERTO("puerto"),
    USUARIO("usuario"),
    PASSWORD("password"),
    TLS("tls"),
    AUTOMATIC_RECONNECT("automaticReconnect"),
    CLEAN_SESSION("cleanSession"),
    CONNECTION_TIMEOUT("connectionTimeout"),
    MQTT_VERSION("mqttVersion"),
    HOSTNAME_VERIFICATION("httpsHostnameVerification");

    private String confMqtt;

    CONF_MQTT(String dato) {
        this.confMqtt = dato;


    }

    String getValorTextoJson() {

        return this.confMqtt;
    }

}
