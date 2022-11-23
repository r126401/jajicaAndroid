package net.jajica.libiot;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PruebaConfiguracionMqtt {

    Context appContext;
    IotMqttConfiguration conf;

    public PruebaConfiguracionMqtt() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new IotMqttConfiguration(appContext);

    }

    @Test
    public void textCargaConfiguracion() {

        CONFIGURACION_CONEXION_MQTT res;
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, res = conf.cargarConfiguracion());


    }

    @Test
    public void modificarParametros() {

        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setBrokerId("jajicaiot.ddns.net"));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setPuerto("8883"));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setUsuario(""));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setPassword(""));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setTls(true));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setAutoConnect(true));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setCleanSession(true));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setConnectionTimeout(30));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setMqttVersion(3));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setHostnameVerification(false));

    }

    @Test
    public void cargarUsers() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        IotUsersDevices users;
        users = new IotUsersDevices(appContext);
        users.loadConfiguration();

        users.json2Object();
        users.setJsonObject(null);
        users.object2json();
        Log.i("jhh", "hjhkj");



    }


    @Test
    public void testUsers() {

        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        IotDeviceSwitch device1;
        device1 = new IotDeviceSwitch();
        device1.setDeviceId("id1");
        device1.setDeviceName("name1");
        device1.object2Json();

        IotDeviceThermometer device2;
        device2 = new IotDeviceThermometer();
        device2.setDeviceId("id2");
        device2.setDeviceName("name2");
        device2.object2Json();

        IotDeviceThermometer device3;
        device3 = new IotDeviceThermometer();
        device3.setDeviceId("id3");
        device3.setDeviceName("name3");
        device3.object2Json();

        IotDeviceThermostat device4;
        device4 = new IotDeviceThermostat();
        device4.setDeviceId("id4");
        device4.setDeviceName("name4");
        device4.object2Json();


        IotDeviceThermostat device5;
        device5 = new IotDeviceThermostat();
        device5.setDeviceId("id5");
        device5.setDeviceName("name5");
        device5.object2Json();

        IotDeviceThermometer device6;
        device6 = new IotDeviceThermometer();
        device6.setDeviceId("id6");
        device6.setDeviceName("name6");
        device6.object2Json();

        IotDeviceThermostat device7;
        device7 = new IotDeviceThermostat();
        device7.setDeviceId("id7");
        device7.setDeviceName("name7");
        device7.object2Json();

        IotDeviceSwitch device8;
        device8 = new IotDeviceSwitch();
        device8.setDeviceId("id8");
        device8.setDeviceName("name8");
        device8.object2Json();



        IotRoomsDevices room1;
        room1 = new IotRoomsDevices();
        room1.setIdRoom(1);
        room1.setNameRoom("Salon");
        room1.insertDeviceForRoom(device1);
        room1.insertDeviceForRoom(device2);
        room1.object2json();
        IotRoomsDevices room2;
        room2 = new IotRoomsDevices();
        room2.setIdRoom(2);
        room2.setNameRoom("Patio");
        room2.insertDeviceForRoom(device3);
        room2.insertDeviceForRoom(device4);
        room2.object2json();
        IotRoomsDevices room3;
        room3 = new IotRoomsDevices();
        room3.setIdRoom(3);
        room3.setNameRoom("Trastero");
        room3.insertDeviceForRoom(device5);
        room3.insertDeviceForRoom(device6);
        room3.object2json();
        IotRoomsDevices room4;
        room4 = new IotRoomsDevices();
        room4.setIdRoom(4);
        room4.setNameRoom("Dormitorio1");
        room4.insertDeviceForRoom(device7);
        room4.insertDeviceForRoom(device8);
        room4.object2json();




        IotSitesDevices site;
        site = new IotSitesDevices("Navalcarnero");
        site.setCity("Navalcarnero");
        site.setCountry("España");
        site.setStreet("Avenida De Los Cinco Siglos");
        site.setStreetNumber(46);
        site.setLongitude(45);
        site.setLatitude(43);
        site.object2json();

        IotSitesDevices site2;
        site2 = new IotSitesDevices("Alcorcon");
        site2.setCity("Alcorcon");
        site2.setCountry("España");
        site2.setStreet("Polvoranca");
        site2.setStreetNumber(40);
        site2.setLongitude(-23.00043);
        site2.setLatitude(43.4334);
        site2.object2json();


        IotUsersDevices users;
        users = new IotUsersDevices();
        users.setUser("Manolo");
        users.setDni("08038287V");
        users.setPassword("pepeillo");
        users.setMail("r126401@gmail.com");
        users.setTelephone("618097200");
        users.insertSiteForUser(site);

        //Añadimos los sites
        users.insertSiteForUser(site);
        users.insertSiteForUser(site2);

        //Añadimos las rooms
        site.insertRoomForSite(room1);
        site.insertRoomForSite(room2);
        site2.insertRoomForSite(room3);
        site2.insertRoomForSite(room4);
         users.object2json();
        users.saveConfiguration(appContext);
        Log.i("hh", "jj");


    }



}
