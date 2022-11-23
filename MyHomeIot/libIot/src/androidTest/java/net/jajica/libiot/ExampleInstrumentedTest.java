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

    IotConfigurationDevices conf;
    Context appContext;

    public ExampleInstrumentedTest() {


        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new IotConfigurationDevices(appContext);
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
        conf.loadIotDevices();
        assertEquals( IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_INSERTED, op = conf.insertIotDevice(dispositivo));
        assertEquals( IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_EXITS, op = conf.insertIotDevice(dispositivo));
        assertEquals( IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NULL, op = conf.insertIotDevice(null));

    }

    @Test
    public void cargaConfiguracion() {

        IotConfigurationDevices conf = null;
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        conf = new IotConfigurationDevices(appContext);
        assertEquals(IOT_OPERATION_CONFIGURATION_DEVICES.DEVICE_NONE, conf.loadIotDevices());
        pruebaInsertarDispositivo();
        assertEquals(IOT_OPERATION_CONFIGURATION_DEVICES.OK_CONFIGURATION, conf.loadIotDevices());
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