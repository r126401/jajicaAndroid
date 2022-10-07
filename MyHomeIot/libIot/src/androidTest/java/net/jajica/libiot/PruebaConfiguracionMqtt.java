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
    ConfiguracionConexionMqtt conf;

    public PruebaConfiguracionMqtt() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new ConfiguracionConexionMqtt(appContext);

    }

    @Test
    public void textCargaConfiguracion() {

        CONFIGURACION_CONEXION_MQTT res;
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_INEXISTENTE, res = conf.cargarConfiguracion());
        if (res != CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK) {
            assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.escribirConfiguracionMqtt());
        }


    }

    @Test
    public void modificarParametros() {

        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setBrokerId("miBroker"));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setPuerto("5555"));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setUsuario("miUsuario"));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setPassword("miPassword"));
        assertEquals(CONFIGURACION_CONEXION_MQTT.CONFIGURACION_MQTT_OK, conf.setTls(false));

    }



}
