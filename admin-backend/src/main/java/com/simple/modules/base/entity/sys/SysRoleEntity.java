package com.simple.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.simple.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_role")
@ApiModel(description = "系统角色")
public class SysRoleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 角色标签
     */
    @ApiModelProperty(value = "角色标签")
    private String label;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 数据权限是否关联上下级
     */
    @ApiModelProperty(value = "数据权限是否关联上下级")
    private Integer relevance;

    /**
     * 菜单权限（JSON格式）
     */
    @TableField("menu_id_list")
    @ApiModelProperty(value = "菜单权限")
    private String menuIdList;

    /**
     * 部门权限（JSON格式）
     */
    @TableField("department_id_list")
    @ApiModelProperty(value = "部门权限")
    private String departmentIdList;

    /**
     * 菜单ID列表（非数据库字段，用于接收前端参数）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "菜单ID列表")
    private List<Long> menuIdListArr;

    /**
     * 部门ID列表（非数据库字段，用于接收前端参数）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "部门ID列表")
    private List<Long> departmentIdListArr;
}
