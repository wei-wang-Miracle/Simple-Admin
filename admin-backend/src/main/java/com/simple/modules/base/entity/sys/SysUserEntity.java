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
 * 系统用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_user")
@ApiModel(description = "系统用户")
public class SysUserEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableField("department_id")
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 密码版本
     */
    @TableField("password_v")
    @ApiModelProperty(value = "密码版本")
    private Integer passwordV;

    /**
     * 昵称
     */
    @TableField("nick_name")
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 头像
     */
    @TableField("head_img")
    @ApiModelProperty(value = "头像")
    private String headImg;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 状态 0:禁用 1：启用
     */
    @ApiModelProperty(value = "状态 0:禁用 1：启用")
    private Integer status;

    /**
     * socketId
     */
    @TableField("socket_id")
    @ApiModelProperty(value = "socketId")
    private String socketId;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /**
     * 角色名称（非数据库字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 角色ID列表（非数据库字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "角色列表")
    private List<Long> roleIdList;
}
