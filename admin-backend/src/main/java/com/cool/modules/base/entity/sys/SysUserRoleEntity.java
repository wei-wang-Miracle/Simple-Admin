package com.cool.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cool.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户角色关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_user_role")
@ApiModel(description = "系统用户角色")
public class SysUserRoleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    @ApiModelProperty(value = "角色ID")
    private Long roleId;
}
