package net.jajica.libiot;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    ConjuntoDispositivosIot conf;
    Context appContext;

    public ExampleInstrumentedTest() {


        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new ConjuntoDispositivosIot(appContext);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("net.jajica.libiot.test", appContext.getPackageName());
    }

    @Test

    public void pruebaInsertarDispositivo() {

        IotDevice dispositivo;
        IOT_DEVICE_STATE a;
        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        dispositivo = new IotDevice();
        dispositivo.setDeviceName("mmajGH");
        dispositivo.setDeviceId("mmddgGG");
        dispositivo.setPublishTopic("kkk");
        dispositivo.setSubscriptionTopic("pppp");
        dispositivo.setDeviceType(IOT_DEVICE_TYPE.DESCONOCIDO);
        assertNotNull(dispositivo);
        //conf = new ConjuntoDispositivosIot(appContext);
        conf.cargarDispositivos();
        assertEquals( OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO, op = conf.insertarDispositivo(dispositivo));
        assertEquals( OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_EXISTENTE, op = conf.insertarDispositivo(dispositivo));
        assertEquals( OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_NULO, op = conf.insertarDispositivo(null));

    }

    @Test
    public void cargaConfiguracion() {

        ConjuntoDispositivosIot conf = null;
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new ConjuntoDispositivosIot(appContext);
        assertEquals(OPERACION_CONFIGURACION_DISPOSITIVOS.NINGUN_DISPOSITIVO, conf.cargarDispositivos());
        pruebaInsertarDispositivo();
        assertEquals(OPERACION_CONFIGURACION_DISPOSITIVOS.CONFIGURACION_OK, conf.cargarDispositivos());
    }

    @Test
    public void crearDispositivo() {

        IotDevice dispositivo;
        dispositivo = new IotDevice();
        dispositivo.setDeviceName("mmajGH");
        dispositivo.setDeviceId("mmddgGG");
        dispositivo.setPublishTopic("kkk");
        dispositivo.setSubscriptionTopic("pppp");
        dispositivo.setDeviceType(IOT_DEVICE_TYPE.DESCONOCIDO);
        assertNotNull(dispositivo);



    }



}