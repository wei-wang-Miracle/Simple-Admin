package com.simple.modules.base.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.simple.modules.base.entity.sys.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色菜单 Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenuEntity> {
    /**
     * 跟菜单关联的所有用户
     *
     * @param menuId 菜单
     * @return 所有用户ID
     */
    Long[] userIds(@Param("menuId") Long menuId);
}
