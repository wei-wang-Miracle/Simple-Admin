package com.simple.core.base;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 基础 Service 接口
 * 继承 MyBatis-Plus 的 IService，提供通用的 CRUD 操作
 *
 * @param <T> 实体类型
 */
public interface BaseService<T> extends IService<T> {
    // 可根据需要添加通用业务方法
}
