package com.simple.core.exception;

/**
 * 自定义业务异常
 */
public class SimpleException extends RuntimeException {

    private int code = 1001;

    public SimpleException(String message) {
        super(message);
    }

    public SimpleException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
