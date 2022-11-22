package net.jajica.libiot;

/**
 * Esta clase gestiona la lista de dispositivos configurados en una aplicación que gestione los dispositivos
 * conectados a un mismo usuario.
 * El formato de la lista de dispositivos es la siguiente:
 * {"dispositivos":[{"nombreDispositivo":"Interruptor","idDevice":"4417931D0126","topicPublication":"\/sub_4417931D0126","topicSubscripcion":"\/pub_4417931D0126","device":0}]}
 * <p>
 * La clase provee los metodos para listar, modificar, crear y eliminar dispositivos. Los guarda sobre un fichero standard
 * llamado datosDispositivos.conf
 */

public enum IOT_OPERATION_CONFIGURATION_DEVICES {

    DEVICE_INSERTED,
    DEVICE_MODIFIED,
    DEVICE_REMOVED,
    DEVICE_FOUND,
    DEVICE_NOT_FOUND,
    DEVICE_EXITS,
    DEVICE_STORED,
    DEVICE_NULL,
    DEVICE_NONE, // No hay ningún dispositivo en la configuración
    CORRUPTED_CONFIGURATION, // El fichero de configuración es inválido
    OK_CONFIGURATION,
    NO_JSON_CONFIGURATION
}
