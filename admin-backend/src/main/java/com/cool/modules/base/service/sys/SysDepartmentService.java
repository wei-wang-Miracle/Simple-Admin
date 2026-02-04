package com.cool.modules.base.service.sys;

import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.SysDepartmentEntity;

import java.util.List;

/**
 * 系统部门 Service
 */
public interface SysDepartmentService extends BaseService<SysDepartmentEntity> {

    /**
     * 排序
     *
     * @param list 部门列表
     */
    void order(List<SysDepartmentEntity> list);
}
