package com.cool.modules.base.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cool.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_sys_log")
@ApiModel(description = "系统日志")
public class SysLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 行为
     */
    @ApiModelProperty(value = "行为")
    private String action;

    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP")
    private String ip;

    /**
     * 参数（JSON格式）
     */
    @ApiModelProperty(value = "参数")
    private String params;
}
