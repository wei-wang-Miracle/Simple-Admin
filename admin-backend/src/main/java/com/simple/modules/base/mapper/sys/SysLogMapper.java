package com.simple.modules.base.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.simple.modules.base.entity.sys.SysLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志 Mapper
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLogEntity> {
}
