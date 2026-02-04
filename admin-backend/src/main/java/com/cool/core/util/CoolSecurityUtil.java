package com.cool.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 */
public class CoolSecurityUtil {

    /**
     * 获取当前登录的 Admin 用户ID
     */
    public static Long getAdminUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object userId = request.getAttribute("adminUserId");
            if (userId != null) {
                return Long.parseLong(userId.toString());
            }
        }
        return null;
    }

    /**
     * 获取当前登录的 Admin 用户名
     */
    public static String getAdminUsername() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object username = request.getAttribute("adminUsername");
            if (username != null) {
                return username.toString();
            }
        }
        return null;
    }

    /**
     * Admin 退出登录
     */
    public static void adminLogout(Long userId, String username) {
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}
