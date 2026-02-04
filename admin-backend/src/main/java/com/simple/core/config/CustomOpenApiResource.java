package com.simple.core.config;

import org.springframework.stereotype.Component;

/**
 * 自定义 OpenAPI 资源配置（占位符类）
 */
@Component
public class CustomOpenApiResource {
    public byte[] getOpenApiJson() {
        return "{}".getBytes();
    }
}
