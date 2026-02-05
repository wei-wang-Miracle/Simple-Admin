package com.simple.modules.base.service.sys;

import cn.hutool.json.JSONObject;
import com.simple.core.base.BaseService;
import com.simple.modules.base.entity.sys.SysLogEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志 Service
 * 
 * 功能：提供系统操作日志的记录和管理功能
 * 包括：请求日志记录、日志清理等
 */
public interface SysLogService extends BaseService<SysLogEntity> {

    /**
     * 记录日志（简化版本，向后兼容）
     *
     * @param request 请求对象
     * @param params 请求参数
     */
    void record(HttpServletRequest request, JSONObject params);
    
    /**
     * 记录日志（完整版本，包含更多信息）
     * 
     * @param request 请求对象
     * @param method 请求方法（GET/POST/PUT/DELETE等）
     * @param params 请求参数（包含queryParams和bodyParams）
     * @param statusCode 响应状态码
     * @param duration 请求耗时（毫秒）
     */
    void record(HttpServletRequest request, String method, JSONObject params, int statusCode, long duration);

    /**
     * 清理日志
     *
     * @param isAll 是否清理全部，true清理全部，false清理一年前的日志
     */
    void clear(Boolean isAll);
}
