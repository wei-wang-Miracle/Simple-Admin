package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.simple.core.cache.CacheUtil;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.*;
import com.simple.modules.base.mapper.sys.*;
import com.simple.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * 权限菜单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysPermsServiceImpl implements SysPermsService {

    final private CacheUtil cacheUtil;

    final private SysUserMapper baseSysUserMapper;

    final private SysUserRoleMapper baseSysUserRoleMapper;

    final private SysMenuMapper baseSysMenuMapper;

    final private SysRoleMenuMapper baseSysRoleMenuMapper;

    final private SysRoleDepartmentMapper baseSysRoleDepartmentMapper;

    final private SysDepartmentMapper baseSysDepartmentMapper;

    final private ExecutorService cachedThreadPool;

    @Override
    public Long[] loginDepartmentIds() {
        String username = SecurityUtil.getCurrentUsername();
        if ("admin".equals(username)) {
            return baseSysDepartmentMapper.selectAll().stream().map(SysDepartmentEntity::getId)
                    .toArray(Long[]::new);
        } else {
            Long[] roleIds = getRoles(username);
            return baseSysRoleDepartmentMapper
                    .selectListByQuery(
                            QueryWrapper.create().where(SysRoleDepartmentEntity::getRoleId).in(Arrays.asList(roleIds)))
                    .stream().map(SysRoleDepartmentEntity::getDepartmentId).toArray(Long[]::new);
        }
    }

    @Override
    public Long[] getDepartmentIdsByRoleIds(Long[] roleIds) {
        return getLongs(roleIds);
    }

    private Long[] getLongs(Long[] roleIds) {
        if (ObjectUtil.isEmpty(roleIds)) {
            return new Long[]{};
        }
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!Arrays.asList(roleIds).contains(1L)) {
            queryWrapper.where(SysRoleDepartmentEntity::getRoleId).in(Arrays.asList(roleIds));
        }
        return baseSysRoleDepartmentMapper
                .selectListByQuery(queryWrapper)
                .stream().map(SysRoleDepartmentEntity::getDepartmentId).toArray(Long[]::new);
    }

    @Override
    public Long[] getDepartmentIdsByRoleIds(Long userId) {
        Long[] roleIds = getRoles(userId);
        return getLongs(roleIds);
    }

    @Override
    public Long[] getDepartmentIdsByUserId(Long userId) {
        return cacheUtil.get("admin:department:" + userId, Long[].class);
    }

    @Override
    public String[] getPermsCache(Long userId) {
        String[] result = cacheUtil.get("admin:perms:" + userId, String[].class);
        if (ObjectUtil.isNotEmpty(result)) {
            return result;
        }
        return getPerms(userId);
    }

    @Override
    public Long[] getRoles(Long userId) {
        return getRoles(baseSysUserMapper.selectOneById(userId));
    }

    @Override
    public Long[] getRoles(String username) {
        return getRoles(
                baseSysUserMapper.selectOneByQuery(QueryWrapper.create().where(SysUserEntity::getUsername).eq(username)));
    }

    @Override
    public Long[] getRoles(SysUserEntity userEntity) {
        if (userEntity == null) {
            return new Long[0];
        }
        // admin 账号默认拥有超级管理员角色 (ID: 1)
        if ("admin".equals(userEntity.getUsername())) {
            return new Long[]{1L};
        }
        List<SysUserRoleEntity> list = baseSysUserRoleMapper
                .selectListByQuery(QueryWrapper.create().where(SysUserRoleEntity::getUserId).eq(userEntity.getId()));
        return list.stream().map(SysUserRoleEntity::getRoleId).toArray(Long[]::new);
    }

    @Override
    public String[] getPerms(Long userId) {
        return getPerms(getRoles(userId));
    }

    @Override
    public String[] getPerms(Long[] roleIds) {
        List<SysMenuEntity> menus = getMenus(roleIds);
        Set<String> perms = new HashSet<>();
        String[] permsData = menus.stream().map(SysMenuEntity::getPerms)
                .filter(itemPerms -> !StrUtil.isEmpty(itemPerms)).toArray(String[]::new);
        for (String permData : permsData) {
            perms.addAll(Arrays.asList(permData.split(",")));
        }
        return perms.toArray(new String[0]);
    }

    @Override
    public List<SysMenuEntity> getMenus(Long[] roleIds) {
        if (roleIds != null && Arrays.asList(roleIds).contains(1L)) {
            roleIds = null;
        }

        QueryWrapper queryWrapper = QueryWrapper.create();
        if (roleIds == null) {
            // 超级管理员，获取所有菜单
        } else if (roleIds.length == 0) {
            return new ArrayList<>();
        } else {
            queryWrapper.select("a.*").from("base_sys_menu").as("a")
                    .leftJoin("base_sys_role_menu").as("b").on("a.id = b.menu_id")
                    .where(QueryMethods.column("b.role_id").in(Arrays.asList(roleIds)))
                    .groupBy("a.id");
        }
        queryWrapper.orderBy(SysMenuEntity::getOrderNum, true);
        return baseSysMenuMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public List<SysMenuEntity> getMenus(Long userId) {
        return getMenus(getRoles(userId));
    }

    @Override
    public List<SysMenuEntity> getMenus(String username) {
        SysUserEntity sysUserEntity = baseSysUserMapper
                .selectOneByQuery(QueryWrapper.create().where(SysUserEntity::getUsername).eq(username));
        return getMenus(sysUserEntity.getId());
    }

    @Override
    public String[] getAllPerms() {
        return getPerms((Long[]) null);
    }

    @Override
    public Dict permmenu(Long adminUserId) {
        Long[] roleIds = getRoles(adminUserId);
        return Dict.create().set("menus", getMenus(roleIds)).set("perms", getPerms(roleIds));
    }

    @Override
    public void updatePerms(Long roleId, Long[] menuIdList, Long[] departmentIds) {
        // 更新菜单权限
        baseSysRoleMenuMapper.deleteByQuery(QueryWrapper.create().where(SysRoleMenuEntity::getRoleId).eq(roleId));
        List<SysRoleMenuEntity> batchRoleMenuList = new ArrayList<>();
        for (Long menuId : menuIdList) {
            SysRoleMenuEntity roleMenuEntity = new SysRoleMenuEntity();
            roleMenuEntity.setRoleId(roleId);
            roleMenuEntity.setMenuId(menuId);
            batchRoleMenuList.add(roleMenuEntity);
        }
        if (ObjectUtil.isNotEmpty(batchRoleMenuList)) {
            baseSysRoleMenuMapper.insertBatch(batchRoleMenuList);
        }
        // 更新部门权限
        baseSysRoleDepartmentMapper
                .deleteByQuery(QueryWrapper.create().where(SysRoleDepartmentEntity::getRoleId).eq(roleId));
        List<SysRoleDepartmentEntity> batchRoleDepartmentList = new ArrayList<>();
        for (Long departmentId : departmentIds) {
            SysRoleDepartmentEntity roleDepartmentEntity = new SysRoleDepartmentEntity();
            roleDepartmentEntity.setRoleId(roleId);
            roleDepartmentEntity.setDepartmentId(departmentId);
            batchRoleDepartmentList.add(roleDepartmentEntity);
        }
        if (ObjectUtil.isNotEmpty(batchRoleDepartmentList)) {
            baseSysRoleDepartmentMapper.insertBatch(batchRoleDepartmentList);
        }
        cachedThreadPool.submit(() -> {
            // 刷新对应角色用户的权限
            List<SysUserRoleEntity> userRoles = baseSysUserRoleMapper
                    .selectListByQuery(QueryWrapper.create().where(SysUserRoleEntity::getRoleId).eq(roleId));
            for (SysUserRoleEntity userRole : userRoles) {
                refreshPerms(userRole.getUserId());
            }
        });
    }

    @Override
    public void updateUserRole(Long userId, Long[] roleIdList) {
        baseSysUserRoleMapper.deleteByQuery(QueryWrapper.create().where(SysUserRoleEntity::getUserId).eq(userId));
        if (roleIdList == null) {
            roleIdList = new Long[0];
        }
        for (Long roleId : roleIdList) {
            SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
            sysUserRoleEntity.setRoleId(roleId);
            sysUserRoleEntity.setUserId(userId);
            baseSysUserRoleMapper.insert(sysUserRoleEntity);
        }
        refreshPerms(userId);
    }

    @Override
    public void refreshPerms(Long userId) {
        SysUserEntity baseSysUserEntity = baseSysUserMapper.selectOneById(userId);
        if (baseSysUserEntity != null && baseSysUserEntity.getStatus() != 0) {
            SpringUtil.getBean(UserDetailsService.class).loadUserByUsername(baseSysUserEntity.getUsername());
        }
        if (baseSysUserEntity != null && baseSysUserEntity.getStatus() == 0) {
            SecurityUtil.adminLogout(baseSysUserEntity.getId(), baseSysUserEntity.getUsername());
        }
    }

    @Async
    @Override
    public void refreshPermsByMenuId(Long menuId) {
        // 刷新超管权限、 找出这个菜单的所有用户、 刷新用户权限
        SysUserEntity admin = baseSysUserMapper
                .selectOneByQuery(QueryWrapper.create().where(SysUserEntity::getUsername).eq("admin"));
        if (admin != null) {
            refreshPerms(admin.getId());
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("c.user_id")
                .from("base_sys_role_menu").as("b")
                .leftJoin("base_sys_user_role").as("c").on("b.role_id = c.role_id")
                .where(com.mybatisflex.core.query.QueryMethods.column("b.menu_id").eq(menuId, menuId != null))
                .groupBy("c.user_id");
        
        List<Row> list = baseSysRoleMenuMapper.selectRowsByQuery(queryWrapper);
        for (Row row : list) {
            refreshPerms(row.getLong("userId"));
        }
    }

    @Override
    public void refreshPermsByRoleId(Long roleId) {
        // 找出角色对应的所有用户
        List<SysUserRoleEntity> list = baseSysUserRoleMapper
                .selectListByQuery(QueryWrapper.create().where(SysUserRoleEntity::getRoleId).eq(roleId));
        list.forEach(e -> {
            refreshPerms(e.getUserId());
        });
    }
}
