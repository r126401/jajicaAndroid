package net.jajica.libiot;

public enum IOT_ALARM_VALUE {

    ALARM_UNKNOWN(-1),
    ALARM_OFF(0),
    ALARM_ON(1),
    ALARM_INHIBIT(2);

    private int alarmValue;

    IOT_ALARM_VALUE(int alarmValue) {
        this.alarmValue = alarmValue;
    }

    public int getAlarmType() {
        return this.alarmValue;
    }

    public IOT_ALARM_VALUE fromId(int id) {

        for (IOT_ALARM_VALUE orden : values()) {
            if (orden.getAlarmType() == id) {
                return orden;
            }
        }
        return null;
    }


}
