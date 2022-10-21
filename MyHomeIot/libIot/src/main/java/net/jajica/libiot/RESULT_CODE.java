package net.jajica.libiot;

public enum RESULT_CODE {
    RESUT_CODE_OK(200),
    RESULT_CODE_NOK(400),
    RESULT_CODE_ERROR(500),
    RESULT_CODE_TIMEOUT(501);

    private int resultCode;

    RESULT_CODE(int code) {

        this.resultCode = code;
    }

    public int getResultCode() {

        return this.resultCode;
    }

    public RESULT_CODE fromId(int id) {

        for (RESULT_CODE result : values()) {

            if (result.getResultCode() == id) return result;
        }
        return null;
    }

}
