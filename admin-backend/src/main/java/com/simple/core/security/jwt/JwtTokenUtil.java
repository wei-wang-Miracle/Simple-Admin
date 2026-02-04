package com.simple.core.security.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * JWT Token 工具类
 */
@Data
@Component
@ConfigurationProperties(prefix = "simple.jwt")
public class JwtTokenUtil {

    /**
     * 密钥
     */
    private String secret = "simple-admin-secret-key-for-jwt-and-other-security-purposes";

    /**
     * token 过期时间（秒）
     */
    private Long expire = 7200L;

    /**
     * refreshToken 过期时间（秒）
     */
    private Long refreshExpire = 604800L;

    /**
     * 生成 token
     */
    public String generateToken(Dict claims) {
        return JWT.create()
                .addPayloads(claims)
                .setKey(secret.getBytes())
                .setExpiresAt(new Date(System.currentTimeMillis() + expire * 1000))
                .sign();
    }

    /**
     * 生成刷新 token
     */
    public String generateRefreshToken(Dict claims) {
        return JWT.create()
                .addPayloads(claims)
                .setPayload("isRefresh", true)
                .setKey(secret.getBytes())
                .setExpiresAt(new Date(System.currentTimeMillis() + refreshExpire * 1000))
                .sign();
    }

    /**
     * 获取 token 信息
     */
    public JWT getTokenInfo(String token) {
        try {
            return JWT.of(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 token
     */
    public boolean validateToken(String token) {
        try {
            if (StrUtil.isBlank(token)) return false;
            return JWTUtil.verify(token, secret.getBytes()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证 token 并校验用户名
     */
    public boolean validateToken(String token, String username) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && validateToken(token));
    }

    /**
     * 验证刷新 token
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    /**
     * 判断 token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            JWTValidator.of(token).validateDate(DateUtil.date());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            JWT jwt = JWT.of(token);
            Object username = jwt.getPayload("username");
            return username != null ? username.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取密钥
     */
    public String getTokenSecret() {
        return secret;
    }
}
