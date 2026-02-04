package com.simple.modules.base.entity.sys;

import com.mybatisflex.annotation.Table;
import com.simple.core.annotation.ColumnDefine;
import com.simple.core.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统角色部门关联实体类
 */
@Getter
@Setter
@Table(value = "base_sys_role_department", comment = "系统角色部门")
public class SysRoleDepartmentEntity extends BaseEntity<SysRoleDepartmentEntity> {

    @ColumnDefine(comment = "角色ID", type = "bigint")
    private Long roleId;

    @ColumnDefine(comment = "部门ID", type = "bigint")
    private Long departmentId;
}
