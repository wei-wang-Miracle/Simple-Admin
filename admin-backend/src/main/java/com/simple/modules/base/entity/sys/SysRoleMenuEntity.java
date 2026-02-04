package com.simple.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.simple.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色菜单关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_role_menu")
@ApiModel(description = "系统角色菜单")
public class SysRoleMenuEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableField("menu_id")
    @ApiModelProperty(value = "菜单ID")
    private Long menuId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    @ApiModelProperty(value = "角色ID")
    private Long roleId;
}
