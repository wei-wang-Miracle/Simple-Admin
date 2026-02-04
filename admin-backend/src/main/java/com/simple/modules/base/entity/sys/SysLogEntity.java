package com.simple.modules.base.entity.sys;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.simple.core.annotation.ColumnDefine;
import com.simple.core.base.BaseEntity;
import com.simple.core.handler.Fastjson2TypeHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统日志实体类
 */
@Getter
@Setter
@Table(value = "base_sys_log", comment = "系统日志表")
public class SysLogEntity extends BaseEntity<SysLogEntity> {

    @ColumnDefine(comment = "用户ID", type = "bigint")
    private Long userId;

    @ColumnDefine(comment = "行为", length = 1000)
    private String action;

    @ColumnDefine(comment = "IP", length = 50)
    private String ip;

    @ColumnDefine(comment = "参数", type = "json")
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Object params;

    // 用户名称
    @Column(ignore = true)
    private String name;
}
