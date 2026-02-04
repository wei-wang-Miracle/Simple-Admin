package com.simple.modules.base.service.sys.impl;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.core.config.LogProperties;
import com.simple.core.util.IPUtils;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.SysLogEntity;
import com.simple.modules.base.mapper.sys.SysLogMapper;
import com.simple.modules.base.mapper.sys.SysUserMapper;
import com.simple.modules.base.service.sys.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
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
		if (Boolean.TRUE.equals(isAll)) {
			// 清理全部
			this.remove(new LambdaQueryWrapper<>());
		} else {
			// 清理30天前的日志
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -30);
			LambdaQueryWrapper<SysLogEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.lt(SysLogEntity::getCreateTime, calendar.getTime());
			this.remove(wrapper);
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
