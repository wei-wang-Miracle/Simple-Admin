package com.simple.core.security;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.simple.core.cache.CacheUtil;
import com.simple.core.enums.UserTypeEnum;
import com.simple.core.security.jwt.JwtTokenUtil;
import com.simple.core.security.jwt.JwtUser;
import com.simple.core.util.PathUtils;
import cn.hutool.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JWT Token 过滤器
 * 处理每个请求中的 JWT token 验证
 */
@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CacheUtil cacheUtil;
    private final IgnoredUrlsProperties ignoredUrlsProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 请求路径在忽略鉴权列表中，直接放行
        if (PathUtils.isMatch(ignoredUrlsProperties.getAdminAuthUrls(), requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String authToken = request.getHeader("Authorization");
        if (StrUtil.isNotEmpty(authToken)) {
            JWT claims = jwtTokenUtil.getTokenInfo(authToken);
            if (claims != null) {
                Object userType = claims.getPayload("userType");
                if (Objects.equals(userType, UserTypeEnum.APP.name())) {
                    // APP 用户
                    handleAppRequest(request, claims, authToken);
                } else {
                    // Admin 用户
                    handleAdminRequest(request, claims, authToken);
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 处理 APP 请求
     */
    private void handleAppRequest(HttpServletRequest request, JWT claims, String authToken) {
        Object userIdObj = claims.getPayload("userId");
        String userId = userIdObj != null ? userIdObj.toString() : null;
        if (ObjectUtil.isNotEmpty(userId)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = cacheUtil.get("app:userDetails:" + userId, JwtUser.class);
            if (jwtTokenUtil.validateToken(authToken) && userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", claims.getPayload("userId"));
                request.setAttribute("tokenInfo", claims);
            }
        }
    }

    /**
     * 处理 Admin 请求
     */
    private void handleAdminRequest(HttpServletRequest request, JWT claims, String authToken) {
        Object usernameObj = claims.getPayload("username");
        String username = usernameObj != null ? usernameObj.toString() : null;
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = cacheUtil.get("admin:userDetails:" + username, JwtUser.class);
            Integer passwordV = Convert.toInt(claims.getPayload("passwordVersion"));
            Integer rv = cacheUtil.get("admin:passwordVersion:" + claims.getPayload("userId"), Integer.class);

            if (jwtTokenUtil.validateToken(authToken, username)
                    && Objects.equals(passwordV, rv)
                    && userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("adminUsername", claims.getPayload("username"));
                request.setAttribute("adminUserId", claims.getPayload("userId"));
                request.setAttribute("tokenInfo", claims);
            }
        }
    }
}
