package com.cool.core.util;

import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * 路径工具类
 */
public class PathUtils {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 判断路径是否匹配
     */
    public static boolean isMatch(List<String> patterns, String path) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        for (String pattern : patterns) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        return path.startsWith("/") || path.matches("^[a-zA-Z]:\\\\.*");
    }

    /**
     * 获取用户目录
     */
    public static String getUserDir() {
        return System.getProperty("user.dir");
    }
}
