package com.simple.core.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.simple.modules.base.service.sys.SysLogService;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

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
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("BaseLogFilter 开始处理请求: {}, Method: {}", request.getRequestURI(), request.getMethod());

        // 包装请求以支持多次读取请求体
        ContentCachingRequestWrapper wrappedRequest = request instanceof ContentCachingRequestWrapper
            ? (ContentCachingRequestWrapper) request
            : new ContentCachingRequestWrapper(request);

        // 获取URL参数
        JSONObject params = new JSONObject();
        Enumeration<String> parameterNames = wrappedRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = wrappedRequest.getParameter(paramName);
            params.putOnce(paramName, paramValue);
        }
        // 先执行后续过滤器链,让请求体被读取
        filterChain.doFilter(wrappedRequest, servletResponse);

        // 过滤器链执行完成后,读取缓存的请求体
        byte[] buf = wrappedRequest.getContentAsByteArray();
        if (buf.length > 0) {
            String requestBody = new String(buf, StandardCharsets.UTF_8);
            log.info("请求体: {}", requestBody);
            try {
                JSONObject bodyParams = JSONUtil.parseObj(requestBody);
                params.putAll(bodyParams);
            } catch (Exception e) {
                log.warn("解析请求体JSON失败: {}", e.getMessage());
            }
        }
        
        log.info("BaseLogFilter 提取到的参数: {}", params);
        
        // 异步记录日志
        sysLogService.record(wrappedRequest, params);
    }

}
