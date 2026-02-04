package com.simple.modules.base.service.sys.impl;

import com.simple.core.base.BaseServiceImpl;
import com.simple.modules.base.entity.sys.SysMenuEntity;
import com.simple.modules.base.mapper.sys.SysMenuMapper;
import com.simple.modules.base.service.sys.SysMenuService;
import com.simple.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 系统菜单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl
        extends BaseServiceImpl<SysMenuMapper, SysMenuEntity>
        implements SysMenuService {

    private final SysPermsService sysPermsService;

    @Override
    public void create(Map<String, Object> params) {
        // 代码生成功能暂不实现
    }

    @Override
    public List<SysMenuEntity> export(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return this.listByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importMenu(List<SysMenuEntity> menus) {
        if (menus == null || menus.isEmpty()) {
            return false;
        }
        // 清除ID，作为新菜单导入
        Map<Long, Long> idMap = new HashMap<>();
        for (SysMenuEntity menu : menus) {
            Long oldId = menu.getId();
            menu.setId(null);
            menu.setCreateTime(new Date());
            menu.setUpdateTime(new Date());
            this.save(menu);
            idMap.put(oldId, menu.getId());
        }
        // 更新父ID
        for (SysMenuEntity menu : menus) {
            if (menu.getParentId() != null && idMap.containsKey(menu.getParentId())) {
                menu.setParentId(idMap.get(menu.getParentId()));
                this.updateById(menu);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SysMenuEntity entity) {
        boolean result = super.save(entity);
        // 刷新权限缓存
        sysPermsService.refreshPermsByMenuId(entity.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SysMenuEntity entity) {
        boolean result = super.updateById(entity);
        // 刷新权限缓存
        sysPermsService.refreshPermsByMenuId(entity.getId());
        return result;
    }
}
