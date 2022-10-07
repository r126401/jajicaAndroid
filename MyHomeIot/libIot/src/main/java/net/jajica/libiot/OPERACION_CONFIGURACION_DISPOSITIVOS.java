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

public enum OPERACION_CONFIGURACION_DISPOSITIVOS {

    DISPOSITIVO_INSERTADO,
    DISPOSITIVO_MODIFICADO,
    DISPOSITIVO_ELIMINADO,
    DISPOSITIVO_ENCONTRADO,
    DISPOSITIVO_NO_ENCONTRADO,
    DISPOSITIVO_EXISTENTE,
    DISPOSITIVO_GUARDADO,
    DISPOSITIVO_NULO,
    NINGUN_DISPOSITIVO, // No hay ningún dispositivo en la configuración
    CONFIGURACION_CORRUPTA, // El fichero de configuración es inválido
    CONFIGURACION_OK,
    CONFIGURACION_NO_JSON
}
