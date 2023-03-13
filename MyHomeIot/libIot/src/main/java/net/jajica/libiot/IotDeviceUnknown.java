package net.jajica.libiot;

import java.io.Serializable;

public class IotDeviceUnknown extends IotDevice implements Serializable {



    public IotDeviceSwitch unknown2Switch() {

        IotDeviceSwitch device;
        device = new IotDeviceSwitch();
        device.setDeviceName(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceStatus(this.getDeviceStatus());
        device.setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
        device.object2Json();
        return device;
    }

    public IotDeviceThermometer unknown2Thermometer() {

        IotDeviceThermometer device;
        device = new IotDeviceThermometer();
        device.setDeviceName(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceStatus(this.getDeviceStatus());
        device.setDeviceType(IOT_DEVICE_TYPE.THERMOMETER);
        device.object2Json();
        return device;
    }

    public IotDeviceThermostat unknown2Thermostat() {
        IotDeviceThermostat device;
        device = new IotDeviceThermostat();
        device.setDeviceName(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceStatus(this.getDeviceStatus());
        device.setDeviceType(IOT_DEVICE_TYPE.CRONOTERMOSTATO);
        device.object2Json();
        return device;
    }

}
