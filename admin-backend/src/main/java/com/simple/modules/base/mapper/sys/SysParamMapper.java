package com.simple.modules.base.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simple.modules.base.entity.sys.SysParamEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统参数配置 Mapper
 */
@Mapper
public interface SysParamMapper extends BaseMapper<SysParamEntity> {
}
