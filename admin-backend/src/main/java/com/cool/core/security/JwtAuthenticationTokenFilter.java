package com.cool.core.security;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cool.core.cache.CoolCache;
import com.cool.core.enums.UserTypeEnum;
import com.cool.core.security.jwt.JwtTokenUtil;
import com.cool.core.security.jwt.JwtUser;
import com.cool.core.util.PathUtils;
import io.jsonwebtoken.Claims;
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
    private final CoolCache coolCache;
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
            Claims claims = jwtTokenUtil.getTokenInfo(authToken);
            if (claims != null) {
                Object userType = claims.get("userType");
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
    private void handleAppRequest(HttpServletRequest request, Claims claims, String authToken) {
        String userId = claims.get("userId").toString();
        if (ObjectUtil.isNotEmpty(userId)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = coolCache.get("app:userDetails:" + userId, JwtUser.class);
            if (jwtTokenUtil.validateToken(authToken) && userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", claims.get("userId"));
                request.setAttribute("tokenInfo", claims);
            }
        }
    }

    /**
     * 处理 Admin 请求
     */
    private void handleAdminRequest(HttpServletRequest request, Claims claims, String authToken) {
        String username = claims.get("username").toString();
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = coolCache.get("admin:userDetails:" + username, JwtUser.class);
            Integer passwordV = Convert.toInt(claims.get("passwordVersion"));
            Integer rv = coolCache.get("admin:passwordVersion:" + claims.get("userId"), Integer.class);

            if (jwtTokenUtil.validateToken(authToken, username)
                    && Objects.equals(passwordV, rv)
                    && userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("adminUsername", claims.get("username"));
                request.setAttribute("adminUserId", claims.get("userId"));
                request.setAttribute("tokenInfo", claims);
            }
        }
    }
}
