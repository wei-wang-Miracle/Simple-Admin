package com.cool.modules.base.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.base.entity.sys.SysLogEntity;
import com.cool.modules.base.mapper.sys.SysLogMapper;
import com.cool.modules.base.service.sys.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * 系统日志 Service 实现类
 */
@Slf4j
@Service
public class SysLogServiceImpl
		extends BaseServiceImpl<SysLogMapper, SysLogEntity>
		implements SysLogService {

	@Override
	@Async
	@Transactional(rollbackFor = Exception.class)
	public void record(SysLogEntity logEntity) {
		try {
			logEntity.setCreateTime(new Date());
			this.save(logEntity);
		} catch (Exception e) {
			log.error("记录日志失败", e);
		}
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
}
