package net.jajica.libiot;


import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class TestConexionMqtt {

    Context appContext;
    ConexionMqtt cnx;

    public TestConexionMqtt() {

        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();


    }

    @Test
    public void conectarMqtt() {

        ConfiguracionConexionMqtt conf;
        conf = new ConfiguracionConexionMqtt(appContext);
        conf.setTls(true);
        conf.setPuerto("8883");
        cnx = new ConexionMqtt(appContext);

        assertEquals(ESTADO_CONEXION_MQTT.CONEXION_MQTT_COMPLETA, cnx.connect());


    }

}
