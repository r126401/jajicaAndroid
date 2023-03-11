package net.jajica.libiot;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class IotUsersDevices {


    protected String dni;
    protected String user;
    protected String password;
    protected String mail;
    protected String telephone;
    protected ArrayList<IotSitesDevices> siteList;
    protected JSONObject jsonObject;
    protected Context context;
    protected String configurationFile;
    protected String currentSite;

    public String getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(String currentSite) {
        this.currentSite = currentSite;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ArrayList<IotSitesDevices> getSiteList() {
        return siteList;
    }

    public void setSiteList(ArrayList<IotSitesDevices> siteList) {
        this.siteList = siteList;
    }

    private void initValues() {
        setCurrentSite(null);
        setTelephone(null);
        setDni(null);
        setMail(null);
        setPassword(null);
        setUser(null);
        setJsonObject(null);
        setSiteList(null);
    }

    public IotUsersDevices(Context context) {
        setConfigurationFile("configuration.conf");
        this.context = context;
    }


    public IOT_DEVICE_USERS_RESULT object2json() {

        int i;
        JSONObject siteJson;
        JSONArray array;
        try {
            if (jsonObject == null) jsonObject = new JSONObject();
            jsonObject.put(IOT_LABELS_JSON.DNI.getValorTextoJson(), dni);
            jsonObject.put(IOT_LABELS_JSON.USER.getValorTextoJson(), user);
            jsonObject.put(IOT_LABELS_JSON.PASSWORD.getValorTextoJson(), password);
            jsonObject.put(IOT_LABELS_JSON.MAIL.getValorTextoJson(), mail);
            jsonObject.put(IOT_LABELS_JSON.TELEPHONE.getValorTextoJson(),telephone);
            jsonObject.put(IOT_LABELS_JSON.CURRENT_SITE.getValorTextoJson(), currentSite);
            if (siteList != null) {
                array = new JSONArray();
                for (i=0;i<siteList.size();i++) {
                    siteList.get(i).object2json();
                    siteJson = siteList.get(i).getJsonObject();
                    array.put(siteJson );
                }

                jsonObject.put(IOT_LABELS_JSON.SITE.getValorTextoJson(), array);
                Log.i("jj", "kkk");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_USERS_RESULT.RESULT_NOK;

        }


        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }
    public IOT_JSON_RESULT json2Object() {

        int i;
        JSONObject jsonSite;
        JSONArray array;
        IotSitesDevices site;
        IOT_JSON_RESULT res;
        //if (getUser() == null) return IOT_JSON_RESULT.JSON_ERROR;
        if (jsonObject == null) {
            initValues();
            return IOT_JSON_RESULT.JSON_EMPTY;
        }

        setUser(jsonObject.optString(IOT_LABELS_JSON.USER.getValorTextoJson()));
        setDni(jsonObject.optString(IOT_LABELS_JSON.DNI.getValorTextoJson()));
        setPassword(jsonObject.optString(IOT_LABELS_JSON.PASSWORD.getValorTextoJson()));
        setMail(jsonObject.optString(IOT_LABELS_JSON.MAIL.getValorTextoJson()));
        setTelephone(jsonObject.optString(IOT_LABELS_JSON.TELEPHONE.getValorTextoJson()));
        setCurrentSite(jsonObject.optString(IOT_LABELS_JSON.CURRENT_SITE.getValorTextoJson()));
        try {
            array = jsonObject.getJSONArray(IOT_LABELS_JSON.SITE.getValorTextoJson());
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_JSON_RESULT.JSON_ERROR;
        }
        for (i=0;i<array.length();i++) {
            try {
                jsonSite = array.getJSONObject(i);
                site = new IotSitesDevices();
                if ((res = site.json2object(jsonSite)) != IOT_JSON_RESULT.JSON_OK) {
                    return res;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return IOT_JSON_RESULT.JSON_ERROR;
            }
            if (siteList == null) siteList = new ArrayList<>();
            siteList.add(site);
        }




        return IOT_JSON_RESULT.JSON_OK;
    }

    public IOT_DEVICE_USERS_RESULT insertSiteForUser(IotSitesDevices site) {

        IOT_DEVICE_USERS_RESULT res;
        int index;

        if ((index = searchSiteOfUser(site.getSiteName())) >= 0) {
            return IOT_DEVICE_USERS_RESULT.SITE_EXITS;
        }

        if (siteList == null) {
            siteList = new ArrayList<IotSitesDevices>();

        }
        siteList.add(site);
        if (siteList.size() == 1) {
            setCurrentSite(site.getSiteName());
        }

        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }

    public IOT_DEVICE_USERS_RESULT deleteSiteForUser(String nameSite, Boolean mandatory) {

        int i;
        if ((i = searchSiteOfUser(nameSite)) < 0) {
            return IOT_DEVICE_USERS_RESULT.SITE_NOT_EXITS;
        }
        //Si el site tiene rooms no se puede borrar.
        if (siteList.get(i).roomList != null) {
            if(siteList.get(i).roomList.size() > 0) {
                if (mandatory) siteList.remove(i);
                return IOT_DEVICE_USERS_RESULT.SITE_WITH_ROOMS;
            }
        }

        siteList.remove(i);
        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }


    public int searchSiteOfUser(String name) {

        int i;

        if (siteList == null) {
            return -1;
        }
        for (i=0;i<siteList.size();i++) {
            if (name.equals(siteList.get(i).getSiteName())) {
                return i;
            }
        }

        return -1;

    }

    public IotSitesDevices searchSiteObject(String name) {

        int index;
        index = searchSiteOfUser(name);
        return this.getSiteList().get(index);

    }



    public IOT_OPERATION_CONFIGURATION_DEVICES saveConfiguration(Context context) {

        Ficheros file;
        ESTADO_FICHEROS estado;
        file = new Ficheros();
        if (object2json() == IOT_DEVICE_USERS_RESULT.RESULT_OK) {
            if ((estado = file.escribirFichero(this.getConfigurationFile(), getJsonObject().toString(), context)) != ESTADO_FICHEROS.FICHERO_OK) {

                return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
            }
        }

        return IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION;


    }

    public IOT_OPERATION_CONFIGURATION_DEVICES loadConfiguration() {
        Ficheros file;
        file = new Ficheros();
        IOT_JSON_RESULT result;
        String texto;
        ESTADO_FICHEROS estado;
        if ((estado = file.leerFichero(context, getConfigurationFile())) != ESTADO_FICHEROS.FICHERO_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.NO_CONFIGURATION;
        }
        try {
            jsonObject = new JSONObject(file.getTextoFichero());
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }
        if ((result = json2Object()) != IOT_JSON_RESULT.JSON_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION;

    }


    public IOT_OPERATION_CONFIGURATION_DEVICES insertIotDevice(IotDevice device, String siteName, String roomName) {

        int nSite, nRoom, nDevice;
        IotSitesDevices site;
        IotRoomsDevices room;
        if ((device.getDeviceId() == null) || (device.getDeviceName() == null)) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }

        if ((nSite = searchSite(siteName)) < 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }
        site = getSiteList().get(nSite);
        if ((nRoom = site.searchRoom(roomName)) < 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }
        room = site.getRoomList().get(nRoom);

        if((nDevice = room.searchDevice(device.getDeviceId())) > 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_EXITS;
        }

        room.insertDeviceForRoom(device);
        object2json();
        saveConfiguration(context);
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED;

    }


    public IOT_OPERATION_CONFIGURATION_DEVICES deleteIotDevice(String idDevice, String siteName, String roomName) {
        int nSite, nRoom, nDevice;
        IotSitesDevices site;
        IotRoomsDevices room;


        if ((nSite = searchSite(siteName)) < 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }
        site = getSiteList().get(nSite);
        if ((nRoom = site.searchRoom(roomName)) < 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }
        room = site.getRoomList().get(nRoom);

        if ((room.deleteDeviceFromRoom(idDevice) != IOT_DEVICE_USERS_RESULT.RESULT_OK)) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }

        object2json();
        saveConfiguration(context);
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_REMOVED;

    }




    protected int searchSite(String siteName) {

        int i;
        if (siteList == null) {
            return -1;
        }
        for (i=0;i<siteList.size();i++) {
            if (siteList.get(i).getSiteName().equals(siteName)) {
                return i;
            }
        }
        return -1;

    }

    public IOT_OPERATION_CONFIGURATION_DEVICES modifyIotDevice(IotDevice device, String siteName, String roomName) {
        int nSite, nRoom, nDevice;
        IotSitesDevices site;
        IotRoomsDevices room;
        IOT_DEVICE_USERS_RESULT result;
        if ((device.getDeviceId() == null) || (device.getDeviceName() == null)) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }

        if ((nSite = searchSite(siteName)) < 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }
        site = getSiteList().get(nSite);
        if ((nRoom = site.searchRoom(roomName)) < 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NOT_FOUND;
        }
        room = site.getRoomList().get(nRoom);

        if((nDevice = room.searchDevice(device.getDeviceId())) > 0) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_EXITS;
        }


        if ((result = room.modifyDeviceForRoom(device, nDevice)) != IOT_DEVICE_USERS_RESULT.RESULT_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NULL;
        }
        if ((result =object2json()) != IOT_DEVICE_USERS_RESULT.RESULT_OK) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_ERROR;

        }
        if (saveConfiguration(context) != IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION) {
            return IOT_OPERATION_CONFIGURATION_DEVICES.CORRUPTED_CONFIGURATION;
        }
        return IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_MODIFIED;
    }

    public IOT_OPERATION_CONFIGURATION_DEVICES reloadConfiguration() {

        jsonObject = null;
        initValues();
        return loadConfiguration();

    }

    public ArrayList<IotDevice> getAllDevices() {
        ArrayList<IotDevice> deviceList;
        int indexSites;
        int indexRooms;

        deviceList = new ArrayList<>();
        for (indexSites = 0; indexSites < this.getSiteList().size(); indexSites++) {

            for (indexRooms = 0; indexRooms < this.getSiteList().get(indexSites).getRoomList().size(); indexRooms++) {
                if (this.getSiteList().get(indexSites).getRoomList().get(indexRooms).getDeviceList() != null){
                    deviceList.addAll(this.getSiteList().get(indexSites).getRoomList().get(indexRooms).getDeviceList());
                }

            }
        }

        return deviceList;

    }

    public ArrayList<IotDevice> getDeviceListFotDeviceType(IOT_DEVICE_TYPE type) {

        ArrayList<IotDevice> deviceList;
        int indexSites;
        int indexRooms;

        deviceList = new ArrayList<>();
        for (indexSites = 0; indexSites < this.getSiteList().size(); indexSites++) {
            for (indexRooms = 0; indexRooms < this.getSiteList().get(indexSites).getRoomList().size(); indexRooms++) {
                deviceList.addAll(this.getSiteList().get(indexSites).getRoomList().get(indexRooms).getDeviceListFotDeviceType(type));
            }
        }
        return deviceList;
    }

    public IotDevice getIotDeviceObject(String siteName, String roomName, String idDevice) {

        IotSitesDevices site;
        IotRoomsDevices room;
        site = searchSiteObject(siteName);
        room = site.searchRoomObject(roomName);
        return room.searchDeviceObject(idDevice);

    }

    public IotDevice searchDeviceObject(String deviceId) {

        int i;
        int j;
        IotDevice device;
        ArrayList<IotRoomsDevices> roomList;
        for (i=0;i<getSiteList().size();i++) {
            roomList = getSiteList().get(i).getRoomList();
            for (j=0;j<roomList.size(); j++) {
                if ((device = roomList.get(j).searchDeviceObject(deviceId)) != null) {
                    return device;
                }
            }
        }
         return null;
    }



}
