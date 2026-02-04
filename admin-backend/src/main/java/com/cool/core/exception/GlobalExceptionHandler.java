package com.cool.core.exception;

import com.cool.core.request.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(CoolException.class)
    public R handleCoolException(CoolException e) {
        log.error("业务异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public R handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证失败: {}", e.getMessage());
        return R.error(1001, "用户名或密码错误");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error("系统异常: ", e);
        return R.error("系统异常，请稍后重试");
    }
}
