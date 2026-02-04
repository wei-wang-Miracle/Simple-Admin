package com.simple.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.simple.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_param")
@ApiModel(description = "系统参数配置")
public class SysParamEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 键
     */
    @TableField("key_name")
    @ApiModelProperty(value = "键")
    private String keyName;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 数据
     */
    @ApiModelProperty(value = "数据")
    private String data;

    /**
     * 数据类型 0:字符串 1:数组 2:键值对
     */
    @TableField("data_type")
    @ApiModelProperty(value = "数据类型 0:字符串 1:数组 2:键值对")
    private Integer dataType;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
