package com.cool.modules.base.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cool.modules.base.entity.sys.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
}
