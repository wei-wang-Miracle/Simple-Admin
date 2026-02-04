package com.cool.modules.base.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cool.modules.base.entity.sys.SysLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志 Mapper
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLogEntity> {
}
