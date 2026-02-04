package com.simple.modules.base.entity.sys;

import com.mybatisflex.annotation.Table;
import com.simple.core.annotation.ColumnDefine;
import com.simple.core.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统角色菜单关联实体类
 */
@Getter
@Setter
@Table(value = "base_sys_role_menu", comment = "系统角色菜单表")
public class SysRoleMenuEntity extends BaseEntity<SysRoleMenuEntity> {
    @ColumnDefine(comment = "菜单", type = "bigint")
    private Long menuId;

    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;
}
