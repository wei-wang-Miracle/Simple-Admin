package com.simple.modules.base.service.sys;

import cn.hutool.json.JSONObject;
import com.simple.core.base.BaseService;
import com.simple.modules.base.entity.sys.SysLogEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志 Service
 */
public interface SysLogService extends BaseService<SysLogEntity> {

    /**
     * 记录日志
     *
     * @param request 请求对象
     * @param params 请求参数
     */
    void record(HttpServletRequest request, JSONObject params);

    /**
     * 清理日志
     *
     * @param isAll 是否清理全部
     */
    void clear(Boolean isAll);
}
