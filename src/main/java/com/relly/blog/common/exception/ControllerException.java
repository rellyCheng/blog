package com.relly.blog.common.exception;

/**
 * @author Kartist 2018/9/18 08:35
 */
public class ControllerException extends RuntimeException {

    private String errorCode;

    private String message;

    public ControllerException(String errorCode, String message) {
        super();
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
