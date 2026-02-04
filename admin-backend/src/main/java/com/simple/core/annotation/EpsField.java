package com.simple.core.annotation;

import java.lang.annotation.*;

/**
 * Eps 字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EpsField {
    String value() default "";
    String component() default "";
}
