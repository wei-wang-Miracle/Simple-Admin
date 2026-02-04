package com.cool.core.exception;

/**
 * 自定义业务异常
 */
public class CoolException extends RuntimeException {

    private int code = 1001;

    public CoolException(String message) {
        super(message);
    }

    public CoolException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
