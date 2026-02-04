package com.simple.modules.base.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.simple.modules.base.entity.sys.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
}
