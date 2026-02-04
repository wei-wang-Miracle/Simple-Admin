package com.simple.core.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 基础 Service 实现类
 * 继承 MyBatis-Plus 的 ServiceImpl，提供通用的 CRUD 实现
 *
 * @param <M> Mapper 类型
 * @param <T> 实体类型
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T>
        extends ServiceImpl<M, T> implements BaseService<T> {
    // 可根据需要添加通用业务方法实现
}
