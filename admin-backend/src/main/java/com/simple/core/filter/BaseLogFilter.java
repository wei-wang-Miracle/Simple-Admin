package com.simple.core.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.simple.modules.base.service.sys.SysLogService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 基础日志过滤器
 * 
 * 功能：拦截所有HTTP请求，提取请求参数并记录日志
 * 支持的请求类型：
 * - GET：获取URL查询参数
 * - POST/PUT/DELETE：获取URL查询参数 + 请求体参数（JSON/表单）
 * - Multipart：获取URL查询参数 + 表单字段（不记录文件内容）
 */
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class BaseLogFilter implements Filter {

    // 注入日志记录服务
    private final SysLogService sysLogService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain)
        throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // 记录请求开始时间，用于计算请求耗时
        long startTime = System.currentTimeMillis();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String contentType = request.getContentType();
        
        log.info("========== 请求开始 ==========");
        log.info("请求URI: {} | 方法: {} | Content-Type: {}", requestURI, method, contentType);

        // 第一步：包装请求和响应，支持多次读取
        ContentCachingRequestWrapper wrappedRequest = wrapRequest(request);
        ContentCachingResponseWrapper wrappedResponse = wrapResponse(response);
        
        // 第二步：提取URL查询参数（适用于所有请求类型）
        JSONObject urlParams = extractUrlParameters(wrappedRequest);
        log.info("URL查询参数: {}", urlParams.isEmpty() ? "无" : urlParams);
        
        // 第三步：执行后续过滤器链（请求体在此过程中被读取和缓存）
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // 第四步：提取请求体参数（仅对有请求体的请求类型有效）
            JSONObject bodyParams = extractBodyParameters(wrappedRequest, contentType);
            
            // 第五步：合并所有参数
            JSONObject allParams = new JSONObject();
            // URL参数放在 queryParams 字段下
            if (!urlParams.isEmpty()) {
                allParams.set("queryParams", urlParams);
            }
            // 请求体参数放在 bodyParams 字段下
            if (!bodyParams.isEmpty()) {
                allParams.set("bodyParams", bodyParams);
            }
            
            // 第六步：计算请求耗时
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = wrappedResponse.getStatus();
            
            log.info("请求体参数: {}", bodyParams.isEmpty() ? "无" : bodyParams);
            log.info("响应状态: {} | 耗时: {}ms", statusCode, duration);
            log.info("========== 请求结束 ==========\n");
            
            // 第七步：异步记录日志到数据库
            sysLogService.record(wrappedRequest, method, allParams, statusCode, duration);
            
            // 必须复制响应内容到原始响应，否则客户端收不到响应
            wrappedResponse.copyBodyToResponse();
        }
    }
    
    /**
     * 包装HttpServletRequest以支持多次读取请求体
     * 
     * @param request 原始请求对象
     * @return 包装后的请求对象
     */
    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        }
        return new ContentCachingRequestWrapper(request);
    }
    
    /**
     * 包装HttpServletResponse以支持读取响应状态和内容
     * 
     * @param response 原始响应对象
     * @return 包装后的响应对象
     */
    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        }
        return new ContentCachingResponseWrapper(response);
    }
    
    /**
     * 提取URL查询参数
     * 适用于所有HTTP请求方法（GET/POST/PUT/DELETE等）
     * 
     * @param request 请求对象
     * @return 包含所有URL查询参数的JSON对象
     */
    private JSONObject extractUrlParameters(HttpServletRequest request) {
        JSONObject params = new JSONObject();
        Enumeration<String> parameterNames = request.getParameterNames();
        
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            
            if (paramValues != null) {
                if (paramValues.length == 1) {
                    // 单值参数直接放入
                    params.set(paramName, paramValues[0]);
                } else {
                    // 多值参数存为数组
                    params.set(paramName, paramValues);
                }
            }
        }
        return params;
    }
    
    /**
     * 提取请求体参数
     * 支持JSON格式和表单格式的请求体
     * 
     * @param request 包装后的请求对象
     * @param contentType 请求的Content-Type
     * @return 包含请求体参数的JSON对象
     */
    private JSONObject extractBodyParameters(ContentCachingRequestWrapper request, String contentType) {
        JSONObject bodyParams = new JSONObject();
        
        // 检查是否有请求体内容
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return bodyParams;
        }
        
        String requestBody = new String(content, StandardCharsets.UTF_8);
        
        // 根据Content-Type处理不同格式的请求体
        if (contentType != null) {
            if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                // 处理JSON格式请求体
                bodyParams = parseJsonBody(requestBody);
            } else if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                // 处理表单格式请求体（已通过getParameter获取，这里记录原始内容）
                bodyParams.set("formData", requestBody);
            } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                // 处理文件上传请求（仅记录标识，不记录文件内容）
                bodyParams.set("multipart", "文件上传请求，详细内容不予记录");
            } else {
                // 其他类型，记录原始内容（限制长度）
                bodyParams.set("rawBody", truncateContent(requestBody, 500));
            }
        }
        
        return bodyParams;
    }
    
    /**
     * 解析JSON格式的请求体
     * 
     * @param jsonBody JSON字符串
     * @return 解析后的JSON对象
     */
    private JSONObject parseJsonBody(String jsonBody) {
        try {
            return JSONUtil.parseObj(jsonBody);
        } catch (Exception e) {
            log.warn("解析请求体JSON失败: {}", e.getMessage());
            JSONObject fallback = new JSONObject();
            fallback.set("rawJson", truncateContent(jsonBody, 500));
            fallback.set("parseError", e.getMessage());
            return fallback;
        }
    }
    
    /**
     * 截断过长的内容，避免日志过大
     * 
     * @param content 原始内容
     * @param maxLength 最大长度
     * @return 截断后的内容
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...[内容已截断，总长度: " + content.length() + "]";
    }
}

