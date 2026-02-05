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
 * 
 * 功能：记录系统中所有HTTP请求的日志信息
 * 包括：请求路径、方法、参数、用户、IP、响应状态、耗时等
 */
@Getter
@Setter
@Table(value = "base_sys_log", comment = "系统日志表")
public class SysLogEntity extends BaseEntity<SysLogEntity> {

    @ColumnDefine(comment = "用户ID", type = "bigint")
    private Long userId;

    @ColumnDefine(comment = "请求路径", length = 1000)
    private String action;
    
    @ColumnDefine(comment = "请求方法", length = 10)
    private String method;

    @ColumnDefine(comment = "IP地址", length = 50)
    private String ip;

    @ColumnDefine(comment = "请求参数", type = "json")
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Object params;
    
    @ColumnDefine(comment = "响应状态码", type = "int")
    private Integer statusCode;
    
    @ColumnDefine(comment = "请求耗时(ms)", type = "bigint")
    private Long duration;

    // 用户名称（非数据库字段，用于查询展示）
    @Column(ignore = true)
    private String name;
}

