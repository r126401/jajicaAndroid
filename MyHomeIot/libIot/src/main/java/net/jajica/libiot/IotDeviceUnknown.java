package net.jajica.libiot;

import java.io.Serializable;

public class IotDeviceUnknown extends IotDevice implements Serializable {


    public IotDeviceSwitch unknown2Switch() {

        IotDeviceSwitch device;
        device = new IotDeviceSwitch();
        device.setDeviceId(this.getDeviceName());
        device.setCnx(this.getCnx());
        device.setDeviceId(this.getDeviceId());
        device.setSubscriptionTopic(this.getSubscriptionTopic());
        device.setPublishTopic(this.getPublishTopic());
        device.setDeviceState(this.getDeviceState());
        return device;
    }

}
