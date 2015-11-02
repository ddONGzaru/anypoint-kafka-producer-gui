package tv.anypoint.utils;

/**
 * Created by twjang on 2015-09-13.
 */
public enum Result {

    SUCCESS     ("Success", "RESULT_SUCCESS"),
    FAIL        ("Fail",    "RESULT_FAIL"),
    TRUE        ("True",    "RESULT_TRUE"),
    FALSE       ("False",   "RESULT_FALSE"),
    EMPTY       ("Empty",   "RESULT_EMPTY");

    private String code;

    private String message;

    private Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
