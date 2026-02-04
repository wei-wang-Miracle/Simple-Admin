package com.simple.core.filter;

import cn.hutool.json.JSONObject;
import com.simple.modules.base.service.sys.SysLogService;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class BaseLogFilter implements Filter {

    private final SysLogService sysLogService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain)
        throws IOException, ServletException {
        // 记录日志
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("BaseLogFilter 开始处理请求: {}, Method: {}", request.getRequestURI(), request.getMethod());

        JSONObject params = (JSONObject) request.getAttribute("requestParams");
        if (params == null) {
            params = new JSONObject(request.getParameterMap());
        }
        log.info("BaseLogFilter 提取到的参数: {}", params);

        sysLogService.record(request, params);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
