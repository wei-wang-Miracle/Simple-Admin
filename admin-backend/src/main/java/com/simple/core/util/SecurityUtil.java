package com.simple.core.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.simple.core.cache.CacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 安全工具类
 */
public class SecurityUtil {
    private static final CacheUtil coolCache = SpringUtil.getBean(CacheUtil.class);
    /**
     * 获取当前登录的用户
     *
     * @return UserDetails
     */
    public static UserDetails getCurrentUser() {
        UserDetailsService userDetailsService = SpringUtil.getBean(UserDetailsService.class);
        return userDetailsService.loadUserByUsername(getCurrentUsername());
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }

    public static JSONObject getAdminUserInfo(JSONObject requestParams) {
        JSONObject tokenInfo = requestParams.getJSONObject("tokenInfo");
        return tokenInfo;
    }

    /**
     * 获取系统用户ID
     *
     * @return 系统用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        try {
            UserDetails userDetails = getCurrentUser();
            if (userDetails == null) {
                return null;
            }
            // 将 Java 对象转换为 JSONObject 对象
            JSONObject jsonObject = (JSONObject) JSON.toJSON(userDetails);
            return jsonObject.getJSONObject("user").getLong("id");
        } catch (Exception e) {
            return null;
        }
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
