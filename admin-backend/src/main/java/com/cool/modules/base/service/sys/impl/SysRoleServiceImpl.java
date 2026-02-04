package com.cool.modules.base.service.sys.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.base.entity.sys.SysRoleEntity;
import com.cool.modules.base.mapper.sys.SysRoleMapper;
import com.cool.modules.base.service.sys.SysPermsService;
import com.cool.modules.base.service.sys.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统角色 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl
        extends BaseServiceImpl<SysRoleMapper, SysRoleEntity>
        implements SysRoleService {

    private final SysPermsService sysPermsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SysRoleEntity entity) {
        boolean result = super.save(entity);
        // 保存角色权限
        if (entity.getMenuIdListArr() != null) {
            sysPermsService.updatePerms(entity.getId(),
                    entity.getMenuIdListArr().toArray(new Long[0]),
                    entity.getDepartmentIdListArr() != null ? entity.getDepartmentIdListArr().toArray(new Long[0])
                            : null);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SysRoleEntity entity) {
        boolean result = super.updateById(entity);
        // 更新角色权限
        if (entity.getMenuIdListArr() != null) {
            sysPermsService.updatePerms(entity.getId(),
                    entity.getMenuIdListArr().toArray(new Long[0]),
                    entity.getDepartmentIdListArr() != null ? entity.getDepartmentIdListArr().toArray(new Long[0])
                            : null);
            // 刷新权限缓存
            sysPermsService.refreshPermsByRoleId(entity.getId());
        }
        return result;
    }
}
