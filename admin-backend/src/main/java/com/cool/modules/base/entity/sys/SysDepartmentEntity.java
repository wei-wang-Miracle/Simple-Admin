package com.cool.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cool.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统部门实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_department")
@ApiModel(description = "系统部门")
public class SysDepartmentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String name;

    /**
     * 上级部门ID
     */
    @TableField("parent_id")
    @ApiModelProperty(value = "上级部门ID")
    private Long parentId;

    /**
     * 排序
     */
    @TableField("order_num")
    @ApiModelProperty(value = "排序")
    private Integer orderNum;
}
