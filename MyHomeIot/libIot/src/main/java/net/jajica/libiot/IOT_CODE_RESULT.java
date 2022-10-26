package net.jajica.libiot;

public enum IOT_CODE_RESULT {
    RESUT_CODE_OK(200),
    RESULT_CODE_NOK(400),
    RESULT_CODE_ERROR(500),
    RESULT_CODE_TIMEOUT(501);

    private int resultCode;

    IOT_CODE_RESULT(int code) {

        this.resultCode = code;
    }

    public int getResultCode() {

        return this.resultCode;
    }

    public IOT_CODE_RESULT fromId(int id) {

        for (IOT_CODE_RESULT result : values()) {

            if (result.getResultCode() == id) return result;
        }
        return null;
    }

}
