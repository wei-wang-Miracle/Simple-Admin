package com.simple.modules.base.entity.sys;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.simple.core.annotation.ColumnDefine;
import com.simple.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 系统用户实体类
 */
@Getter
@Setter
@Table(value = "base_sys_user", comment = "系统用户表")
public class SysUserEntity extends BaseEntity<SysUserEntity> {

    private static final long serialVersionUID = 1L;

    @ColumnDefine(comment = "部门ID", type = "bigint")
    private Long departmentId;

    @ColumnDefine(comment = "姓名")
    private String name;

    @ColumnDefine(comment = "用户名", length = 100, notNull = true)
    private String username;

    @ColumnDefine(comment = "密码", notNull = true)
    private String password;

    @ColumnDefine(comment = "密码版本", defaultValue = "1")
    private Integer passwordV;

    @ColumnDefine(comment = "昵称", notNull = true)
    private String nickName;

    @ColumnDefine(comment = "头像")
    private String headImg;

    @ColumnDefine(comment = "手机号")
    private String phone;

    @ColumnDefine(comment = "邮箱")
    private String email;

    @ColumnDefine(comment = "备注")
    private String remark;

    @ColumnDefine(comment = "状态 0:禁用 1：启用", defaultValue = "1")
    private Integer status;

    // 部门名称
    @Column(ignore = true)
    private String departmentName;

    // 角色名称
    @Column(ignore = true)
    private String roleName;

    @ColumnDefine(comment = "socketId")
    private String socketId;


    @Schema(description = "角色列表")
    private List<Long> roleIdList;
}
