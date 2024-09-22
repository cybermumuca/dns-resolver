package dns.enums;

public enum ResultCode {
    NO_ERROR(0),
    FORM_ERROR(1),
    SERVER_FAILURE(2),
    NAME_ERROR(3),
    NOT_IMPLEMENTED(4),
    REFUSED(5);

    private final int code;

    ResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Invalid result code: " + code);
    }
}