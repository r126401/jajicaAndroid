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
        device.setDeviceState(this.getDeviceState());
        device.setDeviceType(IOT_DEVICE_TYPE.INTERRUPTOR);
        return device;
    }

    public IotDeviceThermometer unknown2Thermometer() {

        IotDeviceThermometer device;
        device = new IotDeviceThermometer();
        device.setDeviceId(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceState(this.getDeviceState());
        return device;
    }

    public IotDeviceThermostat unknown2Thermostat() {
        IotDeviceThermostat device;
        device = new IotDeviceThermostat();
        device.setDeviceId(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceState(this.getDeviceState());
        return device;
    }

}
