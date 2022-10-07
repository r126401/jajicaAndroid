package net.jajica.libiot;

import org.json.*;





public class OtaVersion {

    protected String otaServer;
    protected int otaPort;
    protected String otaUrl;
    protected String otaFile;
    protected String otaVersionAvailable;

    public String getOtaServer() {

        return this.otaServer;
    }

    public int getOtaPort() {
        return this.otaPort;
    }

    public String getOtaUrl() {
        return this.otaUrl;
    }

    public String getOtaFile() {
        return this.otaFile;
    }

    public String getOtaVersionAvailable() {
        return this.otaVersionAvailable;
    }

    public void setOtaServer(String otaServer) {
        this.otaServer = otaServer;
    }

    public void setOtaPort(int otaPort) {
        this.otaPort = otaPort;
    }

    public void setOtaUrl(String otaUrl) {
        this.otaUrl = otaUrl;
    }

    public void setOtaFile(String otaFile) {
        this.otaFile = otaFile;
    }

    public void setOtaVersion(String otaVersion) {
        this.otaVersionAvailable = otaVersion;
    }




    OtaVersion() {


    }

    /**
     * Introduce en el dispositivo, los valores recibidos desde el servidor de OTA
     * @param jsonOtaVersion
     * @return
     */
    /*
    public boolean setDatosOtaDispositivo(String jsonOtaVersion) {

        JSONObject objeto;
        JSONObject respuesta;

        try {
            respuesta = new JSONObject(jsonOtaVersion);
            //respuesta = objeto.getJSONObject(TEXTOS_DIALOGO_IOT.DLG_RESPUESTA.getValorTextoJson());
            if (respuesta.getInt(TEXTOS_DIALOGO_IOT.CODIGO_RESPUESTA.getValorTextoJson()) == 200) {
                setOtaServer(respuesta.getString(TEXTOS_DIALOGO_IOT.SERVIDOR_OTA.getValorTextoJson()));
                setOtaPort(respuesta.getInt(TEXTOS_DIALOGO_IOT.PUERTO_OTA.getValorTextoJson()));
                setOtaUrl(respuesta.getString(TEXTOS_DIALOGO_IOT.URL_OTA.getValorTextoJson()));
                setOtaFile(respuesta.getString(TEXTOS_DIALOGO_IOT.FICHERO_OTA.getValorTextoJson()));
                setOtaVersion(respuesta.getString(TEXTOS_DIALOGO_IOT.VERSION_OTA.getValorTextoJson()));

            } else {
                Log.e(getClass().toString(), "Respuesta erronea del servidor");
                return false;
            }



        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        Log.i(getClass().toString(), "Cargados los valores OTA del dispositivo");


        return true;
    }
*/
}

enum OTA_IOT {
    OTA_ERROR(0),
    OTA_DESCARGA_FIRMWARE(1),
    OTA_BORRANDO_SECTORES(2),
    OTA_COPIANDO_SECTORES(3),
    OTA_UPGRADE_FINALIZADO(4),
    OTA_FALLO_CONEXION(5),
    OTA_DATOS_CORRUPTOS(6),
    OTA_PAQUETES_ERRONEOS(7),
    OTA_CRC_ERRONEO(8),
    OTA_ERROR_MEMORIA(9);


    private int idTipoInformeOta;

    OTA_IOT(int tipoInforme) {
        this.idTipoInformeOta = tipoInforme;
    }

    public int getIdInforme() {
        return this.idTipoInformeOta;
    }
    public OTA_IOT fromId(int id) {


        for (OTA_IOT orden : values() ) {

            if (orden.getIdInforme() == id) {

                return orden;
            }

        }
        return null;

    }


}


