package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.core.config.LogProperties;
import com.simple.core.util.IPUtils;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.SysLogEntity;
import com.simple.modules.base.mapper.sys.SysLogMapper;
import com.simple.modules.base.service.sys.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.Executor;

/**
 * 系统日志 Service 实现类
 * 
 * 功能：实现系统操作日志的异步记录和清理
 * 使用线程池异步执行，避免影响主请求响应时间
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl
        extends BaseServiceImpl<SysLogMapper, SysLogEntity>
        implements SysLogService {

    // IP工具类，用于获取客户端真实IP
    private final IPUtils ipUtils;
    // 日志配置属性
    private final LogProperties logProperties;
    // 日志任务专用线程池
    private final Executor logTaskExecutor;

    /**
     * 记录日志（简化版本，向后兼容）
     * 
     * @param request 请求对象
     * @param requestParams 请求参数
     */
    @Override
    @Async
    public void record(HttpServletRequest request, JSONObject requestParams) {
        // 调用完整版方法，使用默认值
        record(request, request.getMethod(), requestParams, 200, 0L);
    }
    
    /**
     * 记录日志（完整版本）
     * 
     * @param request 请求对象
     * @param method 请求方法（GET/POST等）
     * @param params 请求参数
     * @param statusCode 响应状态码
     * @param duration 请求耗时（毫秒）
     */
    @Override
    @Async
    public void record(HttpServletRequest request, String method, JSONObject params, 
                       int statusCode, long duration) {
        String requestURI = request.getRequestURI();
        String ipAddr = ipUtils.getIpAddr(request);
        
        // 异步记录日志到数据库
        recordAsync(ipAddr, requestURI, method, params, statusCode, duration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(Boolean isAll) {
        if (isAll) {
            // 清理全部日志
            this.remove(QueryWrapper.create().ge(SysLogEntity::getId, 0));
        } else {
            // 清理365天前的日志
            Date beforeDate = DateUtil.offsetDay(new Date(), -365);
            this.remove(QueryWrapper.create().lt(SysLogEntity::getCreateTime, beforeDate));
        }
    }

    /**
     * 异步记录日志到数据库
     * 
     * @param ipAddr 客户端IP地址
     * @param requestURI 请求URI
     * @param method 请求方法
     * @param requestParams 请求参数
     * @param statusCode 响应状态码
     * @param duration 请求耗时
     */
    private void recordAsync(String ipAddr, String requestURI, String method,
                             JSONObject requestParams, int statusCode, long duration) {
        logTaskExecutor.execute(() -> {
            try {
                // 第一步：创建日志实体
                SysLogEntity logEntity = new SysLogEntity();
                logEntity.setIp(ipAddr);
                logEntity.setAction(requestURI);
                logEntity.setMethod(method);
                logEntity.setStatusCode(statusCode);
                logEntity.setDuration(duration);
                
                // 第二步：获取当前用户ID（可能为空，如未登录请求）
                logEntity.setUserId(SecurityUtil.getCurrentUserId());
                
                // 第三步：处理请求参数，避免参数过长
                if (requestParams != null && !requestParams.isEmpty()) {
                    String paramsStr = requestParams.toString();
                    if (paramsStr.length() <= logProperties.getMaxByteLength()) {
                        logEntity.setParams(paramsStr);
                    } else {
                        // 参数过长时，截断并标记
                        logEntity.setParams(paramsStr.substring(0, logProperties.getMaxByteLength()) 
                            + "...[已截断]");
                    }
                }
                
                // 第四步：保存到数据库
                save(logEntity);
                log.debug("日志记录成功: {} {} -> {}", method, requestURI, statusCode);
            } catch (Exception e) {
                // 日志记录失败不应影响主流程，仅记录警告
                log.warn("日志记录失败: {} {} - 错误: {}", method, requestURI, e.getMessage());
            }
        });
    }
}

