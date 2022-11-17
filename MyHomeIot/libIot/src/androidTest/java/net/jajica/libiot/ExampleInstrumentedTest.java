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

    IotSetDevices conf;
    Context appContext;

    public ExampleInstrumentedTest() {


        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new IotSetDevices(appContext);
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
        IOT_OPERATION_CONFIGURATION_DEVICES op;
        dispositivo = new IotDeviceUnknown();
        dispositivo.setDeviceName("mmajGH");
        dispositivo.setDeviceId("mmddgGG");
        dispositivo.setPublishTopic("kkk");
        dispositivo.setSubscriptionTopic("pppp");
        dispositivo.setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        assertNotNull(dispositivo);
        //conf = new IotSetDevices(appContext);
        conf.cargarDispositivos();
        assertEquals( IOT_OPERATION_CONFIGURATION_DEVICES.DISPOSITIVO_INSERTADO, op = conf.insertarDispositivo(dispositivo));
        assertEquals( IOT_OPERATION_CONFIGURATION_DEVICES.DISPOSITIVO_EXISTENTE, op = conf.insertarDispositivo(dispositivo));
        assertEquals( IOT_OPERATION_CONFIGURATION_DEVICES.DISPOSITIVO_NULO, op = conf.insertarDispositivo(null));

    }

    @Test
    public void cargaConfiguracion() {

        IotSetDevices conf = null;
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new IotSetDevices(appContext);
        assertEquals(IOT_OPERATION_CONFIGURATION_DEVICES.NINGUN_DISPOSITIVO, conf.cargarDispositivos());
        pruebaInsertarDispositivo();
        assertEquals(IOT_OPERATION_CONFIGURATION_DEVICES.CONFIGURACION_OK, conf.cargarDispositivos());
    }

    @Test
    public void crearDispositivo() {

        IotDevice dispositivo;
        dispositivo = new IotDeviceUnknown();
        dispositivo.setDeviceName("mmajGH");
        dispositivo.setDeviceId("mmddgGG");
        dispositivo.setPublishTopic("kkk");
        dispositivo.setSubscriptionTopic("pppp");
        dispositivo.setDeviceType(IOT_DEVICE_TYPE.UNKNOWN);
        assertNotNull(dispositivo);



    }



}