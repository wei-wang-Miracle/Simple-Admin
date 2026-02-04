package com.simple.modules.base.service.sys;

import cn.hutool.core.lang.Dict;
import com.simple.core.base.BaseService;
import com.simple.modules.base.entity.sys.SysUserEntity;

/**
 * 系统用户 Service
 */
public interface SysUserService extends BaseService<SysUserEntity> {

    /**
     * 修改用户信息
     *
     * @param body 用户信息
     */
    void personUpdate(Long userId, Dict body);

    /**
     * 移动部门
     *
     * @param departmentId 部门ID
     * @param userIds      用户ID集合
     */
    void move(Long departmentId, Long[] userIds);
}
