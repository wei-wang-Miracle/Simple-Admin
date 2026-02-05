package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.SysMenuEntity;
import com.simple.modules.base.entity.sys.SysRoleMenuEntity;
import com.simple.modules.base.mapper.sys.SysMenuMapper;
import com.simple.modules.base.mapper.sys.SysRoleMenuMapper;
import com.simple.modules.base.service.sys.SysMenuService;
import com.simple.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl
        extends BaseServiceImpl<SysMenuMapper, SysMenuEntity>
        implements SysMenuService {

    private final SysPermsService sysPermsService;
    private final SysRoleMenuMapper baseSysRoleMenuMapper;

    @Override
    public Object list(JSONObject requestParams, QueryWrapper queryWrapper) {
        List<SysMenuEntity> list = sysPermsService.getMenus(SecurityUtil.getCurrentUsername());
        list.forEach(e -> {
            List<SysMenuEntity> parent = list.stream()
                    .filter(sysMenuEntity -> e.getParentId() != null && e.getParentId()
                            .equals(sysMenuEntity.getId())).collect(Collectors.toList());
            if (!parent.isEmpty()) {
                e.setParentName(parent.get(0).getName());
            }
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long... ids) {
        return this.delete(null, ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(JSONObject requestParams, Long... ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return true;
        }
        System.out.println(">>> [DEBUG] Cascade Delete triggered for IDs: " + java.util.Arrays.toString(ids));
        
        List<Long> allIds = new ArrayList<>();
        for (Long id : ids) {
            allIds.add(id);
            collectChildIds(id, allIds);
        }
        System.out.println(">>> [DEBUG] Total IDs to delete (including descendants): " + allIds);

        if (CollectionUtil.isNotEmpty(allIds)) {
            // 1. 删除角色菜单关联
            baseSysRoleMenuMapper.deleteByQuery(QueryWrapper.create()
                    .where(SysRoleMenuEntity::getMenuId).in(allIds));
            
            // 2. 调用父类方法执行物理删除
            super.delete(allIds.toArray(new Long[0]));
            
            // 3. 刷新受影响的权限缓存
            for (Long id : allIds) {
                sysPermsService.refreshPermsByMenuId(id);
            }
            System.out.println(">>> [DEBUG] Cascade Delete completed successfully.");
        }
        return true;
    }

    /**
     * 递归收集子节点 ID
     */
    private void collectChildIds(Long parentId, List<Long> allIds) {
        List<SysMenuEntity> children = super.list(QueryWrapper.create()
                .where(SysMenuEntity::getParentId).eq(parentId));
        if (CollectionUtil.isNotEmpty(children)) {
            for (SysMenuEntity child : children) {
                System.out.println("    - Found child: " + child.getName() + " [ID: " + child.getId() + "]");
                allIds.add(child.getId());
                collectChildIds(child.getId(), allIds);
            }
        }
    }
}
