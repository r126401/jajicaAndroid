package net.jajica.libiot;


public class IotOtaVersionAvailable {

    protected String otaServer;
    protected int otaPort;
    protected String otaUrl;
    protected String otaFile;
    protected String otaVersionAvailable;
    protected String topicSubscripcion;
    protected String topicPublicacion;


    public String getOtaServer() {
        return otaServer;
    }

    public void setOtaServer(String otaServer) {
        this.otaServer = otaServer;
    }

    public int getOtaPort() {
        return otaPort;
    }

    public void setOtaPort(int otaPort) {
        this.otaPort = otaPort;
    }

    public String getOtaUrl() {
        return otaUrl;
    }

    public void setOtaUrl(String otaUrl) {
        this.otaUrl = otaUrl;
    }

    public String getOtaFile() {
        return otaFile;
    }

    public void setOtaFile(String otaFile) {
        this.otaFile = otaFile;
    }

    public String getOtaVersionAvailable() {
        return otaVersionAvailable;
    }

    public void setOtaVersionAvailable(String otaVersionAvailable) {
        this.otaVersionAvailable = otaVersionAvailable;
    }

    public String getTopicSubscripcion() {
        return topicSubscripcion;
    }

    public void setTopicSubscripcion(String topicSubscripcion) {
        this.topicSubscripcion = topicSubscripcion;
    }

    public String getTopicPublicacion() {
        return topicPublicacion;
    }

    public void setTopicPublicacion(String topicPublicacion) {
        this.topicPublicacion = topicPublicacion;
    }



    IotOtaVersionAvailable(String topicPublicacion, String topicSubscripcion) {
        setOtaServer(null);
        setOtaPort(0);
        setOtaUrl(null);
        setOtaFile(null);
        setOtaVersionAvailable(null);
        setTopicPublicacion(topicPublicacion);
        setTopicSubscripcion(topicSubscripcion);

    }


    public IOT_CODE_RESULT setOtaServerFromReport(String message) {

        IotTools api;
        api = new IotTools();
        String dato;
        if ((dato = api.getFieldStringFromReport(message, IOT_LABELS_JSON.OTA_SERVER)) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setOtaServer(dato);


        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    public IOT_CODE_RESULT setOtaPortFromReport(String message) {

        IotTools api;
        api = new IotTools();
        int dato;
        if ((dato = api.getFieldIntFromReport(message, IOT_LABELS_JSON.OTA_PORT)) <= 0) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setOtaPort(dato);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    public IOT_CODE_RESULT setOtaUrlFromReport(String message) {

        IotTools api;
        api = new IotTools();
        String dato;
        if ((dato = api.getFieldStringFromReport(message, IOT_LABELS_JSON.OTA_URL)) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setOtaUrl(dato);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    public IOT_CODE_RESULT setOtaFileFromReport(String message) {

        IotTools api;
        api = new IotTools();
        String dato;
        if ((dato = api.getFieldStringFromReport(message, IOT_LABELS_JSON.OTA_FILE)) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setOtaFile(dato);

        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    public IOT_CODE_RESULT setOtaVersionAvailableFromReport(String message) {

        IotTools api;
        api = new IotTools();
        String dato;
        if ((dato = api.getFieldStringFromReport(message, IOT_LABELS_JSON.OTA_VERSION)) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        setOtaVersionAvailable(dato);
        return IOT_CODE_RESULT.RESUT_CODE_OK;
    }

    protected IOT_CODE_RESULT setDataOtaFromReport(String message) {

        if(setOtaServerFromReport(message) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        if(setOtaPortFromReport(message) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        if(setOtaUrlFromReport(message) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        if(setOtaFileFromReport(message) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }
        if(setOtaVersionAvailableFromReport(message) == null) {
            return IOT_CODE_RESULT.RESULT_CODE_ERROR;
        }

        return IOT_CODE_RESULT.RESUT_CODE_OK;

    }

    public Boolean isOtaVersionAvailable(String currentVersion) {
        Double versionAvaliable;
        Double version;
        if ((getOtaVersionAvailable() == null) || (getOtaVersionAvailable().isEmpty())){
            return false;
        }
        versionAvaliable = Double.valueOf(getOtaVersionAvailable());
        version = Double.valueOf(currentVersion);
        return versionAvaliable > version;


    }
    public int getOtaVersionIntAvailable() {
        int versionAvaliable;
        versionAvaliable = Integer.parseInt(getOtaVersionAvailable());


        return 0;
    }




}


