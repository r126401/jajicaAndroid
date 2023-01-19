package net.jajica.libiot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IotRoomsDevices {

    protected String nameRoom;
    protected int idRoom;
    protected ArrayList<IotDevice> deviceList;
    protected JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }


    public ArrayList<IotDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<IotDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public IotRoomsDevices() {

    }

    public IOT_DEVICE_USERS_RESULT object2json() {

        JSONObject deviceJson;
        JSONArray jsonArray = null;
        int i;
        try {
            if (jsonObject == null) jsonObject = new JSONObject();
            jsonObject.put(IOT_LABELS_JSON.NAME_ROOM.getValorTextoJson(), nameRoom);
            jsonObject.put(IOT_LABELS_JSON.ID_ROOM.getValorTextoJson(), idRoom);
            if(deviceList != null) {
                for (i=0;i<deviceList.size();i++) {
                    if (i==0) jsonArray = new JSONArray();
                    deviceList.get(i).object2Json();
                    deviceJson = deviceList.get(i).getDispositivoJson();
                    jsonArray.put(deviceJson);


                }
                jsonObject.put(IOT_LABELS_JSON.DEVICES.getValorTextoJson(), jsonArray);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_USERS_RESULT.RESULT_NOK;

        }


        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }

    public IOT_DEVICE_USERS_RESULT insertDeviceForRoom(IotDevice device) {

        int i;
        if ((i=searchDevice(device.getDeviceId())) >= 0) {
            return IOT_DEVICE_USERS_RESULT.DEVICE_EXITS;
        }
        if (deviceList == null) deviceList = new ArrayList<>();
        deviceList.add(device);
        return IOT_DEVICE_USERS_RESULT.RESULT_OK;

    }

    public IOT_DEVICE_USERS_RESULT deleteDeviceFromRoom(String idDevice) {

        int i;
        if ((i = searchDevice(idDevice)) < 0) {
            return IOT_DEVICE_USERS_RESULT.DEVICE_NOT_EXITS;
        }
        deviceList.remove(i);
        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }

    protected int searchDevice(String idDevice) {

        int i;
        if (deviceList == null) {
            return -2;
        }
        for (i=0;i<deviceList.size();i++) {
            if(idDevice.equals(deviceList.get(i).getDeviceId())) {
                return i;
            }
        }
        return -1;
    }

    public IOT_DEVICE_USERS_RESULT modifyDeviceForRoom(IotDevice device, int nDevice) {


        if (!deviceList.get(nDevice).getDeviceId().equals(device.getDeviceId())) {
            return IOT_DEVICE_USERS_RESULT.DEVICE_NOT_EXITS;

        }
        deviceList.remove(nDevice);
        deviceList.add(device);

        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }

    protected IOT_JSON_RESULT json2Object(JSONObject object) {

        JSONObject jsonDevice;
        IotDevice device = null;
        JSONArray array;
        IOT_DEVICE_TYPE type;
        IOT_JSON_RESULT result;
        int i;
        try {
            setIdRoom(object.getInt(IOT_LABELS_JSON.ID_ROOM.getValorTextoJson()));
            setNameRoom(object.getString(IOT_LABELS_JSON.NAME_ROOM.getValorTextoJson()));
            array = object.getJSONArray(IOT_LABELS_JSON.DEVICES.getValorTextoJson());
            if (array != null) {
                for (i = 0; i < array.length(); i++) {
                    jsonDevice = array.getJSONObject(i);
                    type = getDeviceTypeFromReport(jsonDevice);
                    switch (type) {

                        case UNKNOWN:
                            device = new IotDeviceUnknown();
                            break;
                        case INTERRUPTOR:
                            device = new IotDeviceSwitch();
                            break;
                        case THERMOMETER:
                            device = new IotDeviceThermometer();
                            break;
                        case CRONOTERMOSTATO:
                            device = new IotDeviceThermostat();
                            break;
                        case OTA_SERVER:
                            break;
                    }
                    device.json2Object(jsonDevice);
                    if (deviceList == null) deviceList = new ArrayList<>();
                    deviceList.add(device);


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_JSON_RESULT.JSON_ERROR;
        }


        return IOT_JSON_RESULT.JSON_OK;



    }

    protected IOT_DEVICE_TYPE getDeviceTypeFromReport(JSONObject object) {

        IOT_DEVICE_TYPE type = IOT_DEVICE_TYPE.UNKNOWN;
        IotTools api;
        int i;
        api = new IotTools();
        i = api.getFieldIntFromReport(object.toString(), IOT_LABELS_JSON.DEVICE_TYPE);
        if ( i < -1 ) {
            return IOT_DEVICE_TYPE.UNKNOWN;
        }

        return type.fromId(i);

    }

    public ArrayList<IotDevice> getDeviceListFotDeviceType(IOT_DEVICE_TYPE type) {
        int i;
        ArrayList<IotDevice> deviceList;
        if (getDeviceList() == null ) {
            return null;
        }
        deviceList = new ArrayList<>();
        for (i=0;i<getDeviceList().size();i++) {
            if (getDeviceList().get(i).getDeviceType() == type) {
                deviceList.add(getDeviceList().get(i));
            }
        }
        return deviceList;

    }

    public IotDevice searchDeviceObject(String idDevice) {

        int index;
        IotDevice device;
        index = searchDevice(idDevice);
        return getDeviceList().get(index);

    }

    public IOT_DEVICE_USERS_RESULT deleteDevice(String idDevice) {

        int index;
        index = searchDevice(idDevice);
        if (index >= 0) {
            deviceList.remove(index);
            return IOT_DEVICE_USERS_RESULT.RESULT_OK;
        }

        return IOT_DEVICE_USERS_RESULT.RESULT_NOK;

    }

}
