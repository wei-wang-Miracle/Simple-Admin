package com.simple.core.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

/**
 * 数据库方言工具类
 */
public class DatabaseDialectUtils {

    /**
     * 判断是否为 PostgreSQL
     */
    public static boolean isPostgresql() {
        Environment env = SpringUtil.getBean(Environment.class);
        String url = env.getProperty("spring.datasource.url");
        return url != null && url.contains("postgresql");
    }

    /**
     * 判断是否为 MySQL
     */
    public static boolean isMysql() {
        Environment env = SpringUtil.getBean(Environment.class);
        String url = env.getProperty("spring.datasource.url");
        return url != null && url.contains("mysql");
    }
}
