package com.cool.modules.base.service.sys;

import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.SysLogEntity;

/**
 * 系统日志 Service
 */
public interface SysLogService extends BaseService<SysLogEntity> {

    /**
     * 记录日志
     *
     * @param log 日志实体
     */
    void record(SysLogEntity log);

    /**
     * 清理日志
     *
     * @param isAll 是否清理全部
     */
    void clear(Boolean isAll);
}
