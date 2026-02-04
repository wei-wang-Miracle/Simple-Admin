package com.simple.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnDefine {

    /**
     * 字段类型：varchar、int、bigint、json、text 等
     */
    String type() default "";

    /**
     * 字段长度，默认 -1 表示不限制
     */
    int length() default -1;

    /**
     * 小数点长度，默认 -1 表示不限制
     */
    int decimalLength() default -1;

    /**
     * 是否不能为 null
     */
    boolean notNull() default false;

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 字段注释说明（EPS 文档的核心字段）
     */
    String comment() default "";
}