package com.cool.core.security;

import com.alibaba.fastjson2.JSON;
import com.cool.core.request.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未授权处理器
 */
@Slf4j
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().println(JSON.toJSONString(R.error(401, "未授权")));
        response.getWriter().flush();
    }
}