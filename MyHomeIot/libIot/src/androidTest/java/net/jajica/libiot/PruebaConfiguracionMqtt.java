package net.jajica.libiot;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PruebaConfiguracionMqtt {

    Context appContext;
    ConfigurationMqtt conf;

    public PruebaConfiguracionMqtt() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new ConfigurationMqtt(appContext);

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



}
