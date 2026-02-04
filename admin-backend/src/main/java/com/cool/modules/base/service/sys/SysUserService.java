package com.cool.modules.base.service.sys;

import cn.hutool.core.lang.Dict;
import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.SysUserEntity;

/**
 * 系统用户 Service
 */
public interface SysUserService extends BaseService<SysUserEntity> {

    /**
     * 移动用户到指定部门
     *
     * @param departmentId 部门ID
     * @param userIds      用户ID数组
     */
    void move(Long departmentId, Long[] userIds);

    /**
     * 修改个人信息
     *
     * @param userId 用户ID
     * @param body   修改内容
     */
    void personUpdate(Long userId, Dict body);
}
