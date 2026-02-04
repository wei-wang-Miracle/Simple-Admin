package com.simple.modules.base.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.simple.modules.base.entity.sys.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统菜单 Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuEntity> {

    /**
     * 根据角色ID列表获取菜单
     */
    @Select("<script>" +
            "SELECT DISTINCT m.* FROM base_sys_menu m " +
            "INNER JOIN base_sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            " ORDER BY m.order_num ASC" +
            "</script>")
    List<SysMenuEntity> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
