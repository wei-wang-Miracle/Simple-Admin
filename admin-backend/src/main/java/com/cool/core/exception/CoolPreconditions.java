package com.cool.core.exception;

/**
 * 前置条件检查工具类
 */
public class CoolPreconditions {

    /**
     * 检查条件，如果条件为 true 则抛出异常
     */
    public static void check(boolean condition, String message) {
        if (condition) {
            throw new CoolException(message);
        }
    }

    /**
     * 总是抛出异常
     */
    public static void alwaysThrow(String message) {
        throw new CoolException(message);
    }
}
