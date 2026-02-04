package com.simple.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.simple.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_menu")
@ApiModel(description = "系统菜单")
public class SysMenuEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    @ApiModelProperty(value = "父菜单ID")
    private Long parentId;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    private String name;

    /**
     * 权限
     */
    @ApiModelProperty(value = "权限")
    private String perms;

    /**
     * 类型 0：目录 1：菜单 2：按钮
     */
    @ApiModelProperty(value = "类型 0：目录 1：菜单 2：按钮")
    private Integer type;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;

    /**
     * 排序
     */
    @TableField("order_num")
    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    /**
     * 菜单地址
     */
    @ApiModelProperty(value = "菜单地址")
    private String router;

    /**
     * 视图地址
     */
    @TableField("view_path")
    @ApiModelProperty(value = "视图地址")
    private String viewPath;

    /**
     * 路由缓存
     */
    @TableField("keep_alive")
    @ApiModelProperty(value = "路由缓存")
    private Boolean keepAlive;

    /**
     * 是否显示
     */
    @TableField("is_show")
    @ApiModelProperty(value = "是否显示")
    private Boolean isShow;
}
