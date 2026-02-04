package com.cool.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cool.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色部门关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_role_department")
@ApiModel(description = "系统角色部门")
public class SysRoleDepartmentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableField("role_id")
    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    /**
     * 部门ID
     */
    @TableField("department_id")
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;
}
