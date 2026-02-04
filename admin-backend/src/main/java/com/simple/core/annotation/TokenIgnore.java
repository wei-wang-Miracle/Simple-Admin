package com.simple.core.annotation;

import java.lang.annotation.*;

/**
 * 标记接口忽略 Token 验证
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenIgnore {
}
