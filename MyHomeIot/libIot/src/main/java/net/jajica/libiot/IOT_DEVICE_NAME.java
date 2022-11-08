package net.jajica.libiot;

public enum IOT_DEVICE_NAME {
    DESCONOCIDO("UNKNOWN"),
    INTERRUPTOR("INTERRUPTOR"),
    TERMOMETRO("TERMOMETRO"),
    CRONOTERMOSTATO("CRONOTERMOSTATO"),
    OTA_SERVER("OTA_SERVER");

    private String nameDevice;

    IOT_DEVICE_NAME(String nameDevice) {

        this.nameDevice = nameDevice;

    }

    public String getValorTextoJson() {

        return this.nameDevice;
    }

}
