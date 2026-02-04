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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl
        extends BaseServiceImpl<SysLogMapper, SysLogEntity>
        implements SysLogService {

    private final IPUtils ipUtils;
    private final LogProperties logProperties;
    private final Executor logTaskExecutor;

    @Override
    @Async
    public void record(HttpServletRequest request, JSONObject requestParams) {
        String requestURI = request.getRequestURI();
        String ipAddr = ipUtils.getIpAddr(request);
        // 异步记录日志
        recordAsync(ipAddr, requestURI, requestParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(Boolean isAll) {
        if (isAll) {
            this.remove(QueryWrapper.create().ge(SysLogEntity::getId, 0));
        } else {
            Date beforeDate = DateUtil.offsetDay(new Date(), -365);
            this.remove(QueryWrapper.create().lt(SysLogEntity::getCreateTime, beforeDate));
        }
    }

    public void recordAsync(String ipAddr, String requestURI, JSONObject requestParams) {
        logTaskExecutor.execute(() -> {
            SysLogEntity logEntity = new SysLogEntity();
            logEntity.setIp(ipAddr);
            logEntity.setAction(requestURI);
            logEntity.setUserId(SecurityUtil.getCurrentUserId());
            if (requestParams != null) {
                String paramsStr = requestParams.toString();
                if (paramsStr.length() <= logProperties.getMaxByteLength()) {
                    logEntity.setParams(paramsStr);
                } else {
                    logEntity.setParams("参数过长，不予记录");
                }
            }
            save(logEntity);
        });
    }
}
