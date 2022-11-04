package net.jajica.libiot;

public class IotAlarmDevice {

    protected IOT_ALARM_VALUE wifiAlarm;
    protected IOT_ALARM_VALUE mqttAlarm;
    protected IOT_ALARM_VALUE ntpAlarm;
    protected IOT_ALARM_VALUE nvsAlarm;

    public IOT_ALARM_VALUE getWifiAlarm() {
        return wifiAlarm;
    }

    public void setWifiAlarm(IOT_ALARM_VALUE wifiAlarm) {
        this.wifiAlarm = wifiAlarm;
    }

    public IOT_ALARM_VALUE getMqttAlarm() {
        return mqttAlarm;
    }

    public void setMqttAlarm(IOT_ALARM_VALUE mqttAlarm) {
        this.mqttAlarm = mqttAlarm;
    }

    public IOT_ALARM_VALUE getNtpAlarm() {
        return ntpAlarm;
    }

    public void setNtpAlarm(IOT_ALARM_VALUE ntpAlarm) {
        this.ntpAlarm = ntpAlarm;
    }

    public IOT_ALARM_VALUE getNvsAlarm() {
        return nvsAlarm;
    }

    public void setNvsAlarm(IOT_ALARM_VALUE nvsAlarm) {
        this.nvsAlarm = nvsAlarm;
    }

    public IotAlarmDevice() {

        setWifiAlarm(IOT_ALARM_VALUE.ALARM_OFF);
        setMqttAlarm(IOT_ALARM_VALUE.ALARM_OFF);
        setNtpAlarm(IOT_ALARM_VALUE.ALARM_OFF);
        setNvsAlarm(IOT_ALARM_VALUE.ALARM_OFF);

    }





}
