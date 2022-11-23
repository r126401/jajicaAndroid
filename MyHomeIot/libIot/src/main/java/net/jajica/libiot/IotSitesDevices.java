package net.jajica.libiot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IotSitesDevices {

    protected String siteName;
    protected String street;
    protected int streetNumber;
    protected int poBox;
    protected String city;
    protected String country;
    protected double longitude;
    protected double latitude;
    protected ArrayList<IotRoomsDevices> roomList;
    protected JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public int getPoBox() {
        return poBox;
    }

    public void setPoBox(int poBox) {
        this.poBox = poBox;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public ArrayList<IotRoomsDevices> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<IotRoomsDevices> roomList) {
        this.roomList = roomList;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }


    public IotSitesDevices(String siteName) {

        this.siteName = siteName;

    }

    public IotSitesDevices() {

    }

    protected IOT_DEVICE_USERS_RESULT object2json() {

        JSONArray array = null;
        JSONObject roomJson;
        int i;

        try {
            if (jsonObject == null) jsonObject = new JSONObject();
            jsonObject.put(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson(), siteName);
            jsonObject.put(IOT_LABELS_JSON.STREET.getValorTextoJson(), street);
            jsonObject.put(IOT_LABELS_JSON.STREET_NUMBER.getValorTextoJson(), streetNumber);
            jsonObject.put(IOT_LABELS_JSON.PO_BOX.getValorTextoJson(), poBox);
            jsonObject.put(IOT_LABELS_JSON.CITY.getValorTextoJson(), city);
            jsonObject.put(IOT_LABELS_JSON.COUNTRY.getValorTextoJson(),country);
            jsonObject.put(IOT_LABELS_JSON.LATITUDE.getValorTextoJson(), latitude);
            jsonObject.put(IOT_LABELS_JSON.LONGITUDE.getValorTextoJson(), longitude);
            if (roomList != null) {
                for (i=0;i<roomList.size();i++) {
                    if (i== 0) array = new JSONArray();
                    roomList.get(i).object2json();
                    roomJson = roomList.get(i).getJsonObject();
                    array.put(roomJson);
                }
                jsonObject.put(IOT_LABELS_JSON.ROOM.getValorTextoJson(), array);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            return IOT_DEVICE_USERS_RESULT.RESULT_NOK;

        }


        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }

    public IOT_DEVICE_USERS_RESULT insertRoomForSite(IotRoomsDevices room) {

        int i;
        if ((i = searchRoom(room.getNameRoom())) >= 0) {
            return IOT_DEVICE_USERS_RESULT.ROOM_EXITS;
        }
        if (roomList == null) roomList = new ArrayList<>();
        roomList.add(room);
        return IOT_DEVICE_USERS_RESULT.RESULT_OK;
    }

    public IOT_DEVICE_USERS_RESULT deleteRoomForSite(String nameRoom) {

        int i;
        if ((i = searchRoom(nameRoom)) < 0) {
            return IOT_DEVICE_USERS_RESULT.ROOM_NOT_EXITS;
        }
        // Si el site tiene rooms no se puede borrar.
        if (roomList.get(i).deviceList.size() > 0) {
            return IOT_DEVICE_USERS_RESULT.ROOM_WITH_DEVICES;
        }
        roomList.remove(i);
        return IOT_DEVICE_USERS_RESULT.RESULT_OK;

    }

    protected int searchRoom(String nameRoom) {

        IOT_DEVICE_USERS_RESULT res;
        int i;
        if (roomList == null) {
            return -2;
        }
        for (i=0;i<roomList.size();i++) {
            if (nameRoom.equals(roomList.get(i).getNameRoom())) {
                return i;
            }
        }
        return -1;
    }


    protected IOT_JSON_RESULT json2object(JSONObject object) {

        JSONArray array;
        JSONObject roomJson;
        IotRoomsDevices room;
        int i;
        try {
            setSiteName(object.getString(IOT_LABELS_JSON.NAME_SITE.getValorTextoJson()));
            setLatitude(object.optDouble(IOT_LABELS_JSON.LATITUDE.getValorTextoJson()));
            setLongitude(object.optDouble(IOT_LABELS_JSON.LONGITUDE.getValorTextoJson()));
            setStreet(object.optString(IOT_LABELS_JSON.STREET.getValorTextoJson()));
            setStreetNumber(object.optInt(IOT_LABELS_JSON.STREET_NUMBER.getValorTextoJson()));
            setCity(object.optString(IOT_LABELS_JSON.CITY.getValorTextoJson()));
            setCountry(object.optString(IOT_LABELS_JSON.COUNTRY.getValorTextoJson()));
            setPoBox(object.optInt(IOT_LABELS_JSON.PO_BOX.getValorTextoJson()));
            array = object.getJSONArray(IOT_LABELS_JSON.ROOM.getValorTextoJson());
            if (array != null) {
                for (i=0;i<array.length();i++) {
                    roomJson = array.getJSONObject(i);
                    room = new IotRoomsDevices();
                    room.json2Object(roomJson);
                    if (roomList == null) roomList = new ArrayList<>();
                    roomList.add(room);
                }


            } else {
                roomList = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (roomList != null) {

        }

        return IOT_JSON_RESULT.JSON_OK;
    }


}
