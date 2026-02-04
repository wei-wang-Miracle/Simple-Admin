package com.simple.core.security.jwt;

import cn.hutool.core.lang.Dict;
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
     */
    public Claims getTokenInfo(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 token
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getTokenInfo(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证 token 并校验用户名
     */
    public boolean validateToken(String token, String username) {
        try {
            Claims claims = getTokenInfo(token);
            if (claims == null) {
                return false;
            }
            String tokenUsername = (String) claims.get("username");
            return username.equals(tokenUsername) && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证刷新 token
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getTokenInfo(token);
            if (claims == null) {
                return false;
            }
            Boolean isRefresh = (Boolean) claims.get("isRefresh");
            return Boolean.TRUE.equals(isRefresh) && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断 token 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
