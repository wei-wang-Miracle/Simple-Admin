package com.simple.modules.base.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * 根据角色ID列表获取菜单ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT menu_id FROM base_sys_role_menu " +
            "WHERE role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectMenuIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
