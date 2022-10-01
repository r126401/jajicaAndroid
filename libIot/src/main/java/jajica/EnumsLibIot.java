package jajica;

import java.io.Serializable;

enum TIPO_DISPOSITIVO_IOT implements Serializable {
    DESCONOCIDO(-1),
    INTERRUPTOR(0),
    TERMOMETRO(2),
    CRONOTERMOSTATO(1),
    SERVIDOR_OTA (100);

    private int tipo;

    TIPO_DISPOSITIVO_IOT(int tipoDispositivo) {

        this.tipo = tipoDispositivo;
    }

    public int getValorTipoDispositivo() {

        return this.tipo;
    }


    public TIPO_DISPOSITIVO_IOT fromId(int id) {


        for (TIPO_DISPOSITIVO_IOT orden : values() ) {

            if (orden.getValorTipoDispositivo() == id) {

                return orden;
            }

        }
        return null;

    }



}

enum ESTADO_RELE implements Serializable{
    OFF(0),
    ON(1),
    INDETERMINADO(-1);

    private int estadoRele;

    ESTADO_RELE(int estadoRele) {

        this.estadoRele = estadoRele;

    }

    public int getEstadoRele() {

        return this.estadoRele;
    }

    public ESTADO_RELE fromId(int id) {

        for (ESTADO_RELE tipo : values()) {

            if (tipo.getEstadoRele() == id) return tipo;
        }
        return null;
    }



}

enum ESTADO_DISPOSITIVO implements Serializable{
    NORMAL_AUTO(0),
    NORMAL_AUTOMAN(1),
    NORMAL_MANUAL(2),
    NORMAL_ARRANCANDO(3),
    NORMAL_SIN_PROGRAMACION(4),
    INDETERMINADO(-1);

    private int estadoDispositivo;

    ESTADO_DISPOSITIVO(int estado) {
        this.estadoDispositivo = estado;
    }

    public int getEstadoDispositivo() {

        return this.estadoDispositivo;
    }

    public ESTADO_DISPOSITIVO fromId(int id) {

        for (ESTADO_DISPOSITIVO tipo : values()) {

            if (tipo.getEstadoDispositivo() == id) return tipo;
        }
        return null;
    }



}

enum ESTADO_PROGRAMACION implements Serializable{
    INVALIDO(0),
    VALIDO(1),
    INHIBIDO(2),
    INDETERMINADO(-1);

    private int estadoProgramacion;

    ESTADO_PROGRAMACION(int estado) {
        this.estadoProgramacion = estado;
    }

    public int getEstadoProgramacion() {
        return this.estadoProgramacion;
    }

    public ESTADO_PROGRAMACION fromId(int id) {

        for (ESTADO_PROGRAMACION tipo : values()) {

            if (tipo.getEstadoProgramacion() == id) return tipo;
        }
        return null;
    }


}

enum ESTADO_CONEXION_IOT implements Serializable{
    INDETERMINADO,
    CONECTADO,
    DESCONECTADO,
    ESPERANDO_RESPUESTA;
}

enum ESTADO_PROGRAMA {
    PROGRAMA_DESCONOCIDO(-1),
    PROGRAMA_INACTIVO(0),
    PROGRAMA_ACTIVO(1);

    private int estadoPrograma;

    ESTADO_PROGRAMA (int estadoPrograma) { this.estadoPrograma = estadoPrograma;}

    public int getEstadoPrograma() {
        return this.estadoPrograma;
    }

    public ESTADO_PROGRAMA fromId(int id) {

        for (ESTADO_PROGRAMA orden : values()) {
            if (orden.getEstadoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }

}

enum TIPO_PROGRAMA {
    PROGRAMA_DESCONOCIDO(-1),
    PROGRAMA_DIARIO(0),
    PROGRAMA_SEMANAL(1),
    PROGRAMA_FECHADO(2);

    private int estadoPrograma;

    TIPO_PROGRAMA (int tipoPrograma) { this.estadoPrograma = tipoPrograma;}

    public int getTipoPrograma() {
        return this.estadoPrograma;
    }

    public TIPO_PROGRAMA fromId(int id) {

        for (TIPO_PROGRAMA orden : values()) {
            if (orden.getTipoPrograma() == id) {
                return orden;
            }
        }
        return null;
    }

}


enum TEXTOS_DIALOGO_IOT {

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
    ESTADO_PROGRAMACION("programState"),
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
enum TIPO_INFORME {
    RESULTADO_COMANDO,
    INFORME_ESPONTANEO
}
enum ESPONTANEO_IOT {

    ARRANQUE_APLICACION(0),
    ACTUACION_RELE_LOCAL(1),
    ACTUACION_RELE_REMOTO(2),
    UPGRADE_FIRMWARE_FOTA(3),
    CAMBIO_DE_PROGRAMA(4),
    COMANDO_APLICACION(5),
    CAMBIO_TEMPERATURA(6),
    CAMBIO_ESTADO(7),
    RELE_TEMPORIZADO(8),
    INFORME_ALARMA(9),
    CAMBIO_UMBRAL_TEMPERATURA(10),
    CAMBIO_ESTADO_APLICACION(11),
    ERROR(12),

    ESPONTANEO_DESCONOCIDO(-1);


    private int idTipoInforme;

    ESPONTANEO_IOT(int tipoInforme) {
        this.idTipoInforme = tipoInforme;
    }

    public int getIdInforme() {
        return this.idTipoInforme;
    }
    public ESPONTANEO_IOT fromId(int id) {


        for (ESPONTANEO_IOT orden : values() ) {

            if (orden.getIdInforme() == id) {

                return orden;
            }

        }
        return null;

    }


}
enum COMANDO_IOT {
    CONSULTAR_CONF_APP(0),
    ACTUAR_RELE(50),
    ESTADO(51),
    CONSULTAR_PROGRAMACION(6),
    NUEVA_PROGRAMACION(7),
    ELIMINAR_PROGRAMACION(9),
    MODIFICAR_PROGRAMACION(8),
    MODIFICAR_APP(12),
    RESET(10),
    FACTORY_RESET(11),
    MODIFY_CLOCK(15),
    UPGRADE_FIRMWARE(26),
    MODIFICAR_UMBRAL_TEMPERATURA(52),
    SELECCIONAR_SENSOR_TEMPERATURA(53),
    ESPONTANEO(-1),
    VERSION_OTA(100),
    ERROR_RESPUESTA(-100);

    private int idComando;


    COMANDO_IOT(int idComando) {
        this.idComando = idComando;
    }

    public int getIdComando() {

        return this.idComando;
    }

    public COMANDO_IOT fromId(int id) {


        for (COMANDO_IOT orden : values() ) {

            if (orden.getIdComando() == id) {

                return orden;
            }

        }
        return null;

    }




}

enum OPERACION_JSON {

    JSON_ERROR,
    JSON_OK,
    JSON_CORRUPTO
}

enum OPERACION_DISPOSITIVO {

    DISPOSITIVO_OK,
    NO_EXISTE_DISPOSITIVO,
    EXISTE_DISPOSITIVO

}

enum OPERACION_CONFIGURACION_DISPOSITIVOS {

    DISPOSITIVO_INSERTADO,
    DISPOSITIVO_MODIFICADO,
    DISPOSITIVO_ELIMINADO,
    DISPOSITIVO_ENCONTRADO,
    DISPOSITIVO_NO_ENCONTRADO,
    DISPOSITIVO_EXISTENTE,
    DISPOSITIVO_GUARDADO,
    NINGUN_DISPOSITIVO, // No hay ningún dispositivo en la configuración
    CONFIGURACION_CORRUPTA, // El fichero de configuración es inválido
    CONFIGURACION_OK,
    CONFIGURACION_NO_JSON
}