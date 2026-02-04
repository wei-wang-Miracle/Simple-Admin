package com.cool.core.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略 URL 配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cool.security.ignore")
public class IgnoredUrlsProperties {

    /**
     * Admin 忽略鉴权的 URL 列表
     */
    private List<String> adminAuthUrls = new ArrayList<>();

    /**
     * App 忽略鉴权的 URL 列表
     */
    private List<String> appAuthUrls = new ArrayList<>();
}
