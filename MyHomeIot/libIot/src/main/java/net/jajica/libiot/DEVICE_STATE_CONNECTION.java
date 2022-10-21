package net.jajica.libiot;

import java.io.Serializable;

public enum DEVICE_STATE_CONNECTION implements Serializable {
    UNKNOWN,
    DEVICE_CONNECTED,
    DEVICE_DISCONNECTED,
    DEVICE_WAITING_RESPONSE,
    DEVICE_ERROR_COMMUNICATION,
    DEVICE_ERROR_SUBSCRIPTION;
}
