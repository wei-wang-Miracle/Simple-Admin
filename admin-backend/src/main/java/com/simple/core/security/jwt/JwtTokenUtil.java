package com.simple.core.security.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
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
    private String secret;

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
        Map<String, Object> claimsMap = new HashMap<>(claims);
        return Jwts.builder()
                .setClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成刷新 token
     */
    public String generateRefreshToken(Dict claims) {
        Map<String, Object> claimsMap = new HashMap<>(claims);
        claimsMap.put("isRefresh", true);
        return Jwts.builder()
                .setClaims(claimsMap)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpire * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从 token 中获取信息
     *//*
    public Claims getTokenInfo(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }*/

    /**
     * 获得token信息
     *
     * @param token 令牌
     * @return token信息
     */
    public JWT getTokenInfo(String token) {
        return JWT.of(token);
    }

    /**
     * 验证 token
     */
    public boolean validateToken(String token) {
        try {
            JWTValidator.of(token).validateDate(DateUtil.date());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证 token 并校验用户名
     */
    public boolean validateToken(String token, String username) {
        if (ObjectUtil.isEmpty(token)) {
            return false;
        }
        String tokenUsername = getUsernameFromToken(token);
        String secret = getTokenSecret();
        boolean isValidSignature = JWTUtil.verify(token, secret.getBytes());
        return (tokenUsername.equals(username) && !isTokenExpired(token) && isValidSignature);
    }

    /**
     * 验证刷新 token
     */
    public boolean validateRefreshToken(String token) {
        if (ObjectUtil.isEmpty(token)) {
            return false;
        }
        String secret = getRefreshTokenSecret();
        boolean isValidSignature = JWTUtil.verify(token, secret.getBytes());
        return (!isTokenExpired(token) && isValidSignature);
    }

    /**
     * 判断 token 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        JWT jwt = JWT.of(token);
        return jwt.getPayload("username").toString();
    }
    public String getTokenSecret() {
        String secret = baseSysConfService.getValueWithCache(tokenKey);
        if (StrUtil.isBlank(secret)) {
            secret = StrUtil.uuid().replaceAll("-", "");
            baseSysConfService.setValue(tokenKey, secret);
        }
        return secret;
    }
}
