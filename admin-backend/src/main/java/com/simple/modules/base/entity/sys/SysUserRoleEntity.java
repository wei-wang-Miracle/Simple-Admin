package com.simple.modules.base.entity.sys;

import com.mybatisflex.annotation.Table;
import com.simple.core.annotation.ColumnDefine;
import com.simple.core.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统用户角色关联实体类
 */
@Getter
@Setter
@Table(value = "base_sys_user_role", comment = "系统用户角色表")
public class SysUserRoleEntity extends BaseEntity<SysUserRoleEntity> {

    private static final long serialVersionUID = 1L;

    @ColumnDefine(comment = "用户ID", type = "bigint")
    private Long userId;

    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;
}
