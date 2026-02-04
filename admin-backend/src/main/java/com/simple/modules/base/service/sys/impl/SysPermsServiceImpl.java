package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
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
        if (username.equals("admin")) {
            return baseSysDepartmentMapper.selectAll().stream().map(SysDepartmentEntity::getId)
                    .toArray(Long[]::new);
        } else {
            Long[] roleIds = getRoles(username);
            return baseSysRoleDepartmentMapper
                    .selectListByQuery(
                            QueryWrapper.create().in(SysRoleDepartmentEntity::getRoleId, (Object) roleIds))
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
        if (!CollUtil.toList(roleIds).contains(1L)) {
            queryWrapper.in(SysRoleDepartmentEntity::getRoleId, (Object) roleIds);
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
        return (Long[]) cacheUtil.get("admin:department:" + userId);
    }

    @Override
    public String[] getPermsCache(Long userId) {
        Object result = cacheUtil.get("admin:perms:" + userId);
        if (ObjectUtil.isNotEmpty(result)) {
            return Convert.toStrArray(result);
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
                baseSysUserMapper.selectOneByQuery(QueryWrapper.create().eq(SysUserEntity::getUsername, username)));
    }

    @Override
    public Long[] getRoles(SysUserEntity userEntity) {
        Long[] roleIds = null;
        if (!userEntity.getUsername().equals("admin")) {
            List<SysUserRoleEntity> list = baseSysUserRoleMapper
                    .selectListByQuery(QueryWrapper.create().eq(SysUserRoleEntity::getUserId, userEntity.getId()));
            roleIds = list.stream().map(SysUserRoleEntity::getRoleId).toArray(Long[]::new);
            if (Arrays.asList(roleIds).contains(1L)) {
                roleIds = null;
            }
        }
        return roleIds;
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
        return ArrayUtil.toArray(perms, String.class);
    }

    @Override
    public List<SysMenuEntity> getMenus(Long[] roleIds) {
        if (CollUtil.toList(roleIds).contains(1L)) {
            roleIds = null;
        }
        if (roleIds != null && roleIds.length == 0) {
            return new ArrayList<>();
        }

        QueryWrapper queryWrapper = QueryWrapper.create().select(BASE_SYS_MENU_ENTITY.ALL_COLUMNS).from(BASE_SYS_MENU_ENTITY);
        if (ObjectUtil.isNotEmpty(roleIds)) {
            queryWrapper.leftJoin(BASE_SYS_ROLE_MENU_ENTITY).on(BASE_SYS_MENU_ENTITY.ID.eq(BASE_SYS_ROLE_MENU_ENTITY.MENU_ID)).and(BASE_SYS_ROLE_MENU_ENTITY.ROLE_ID.in((Object) roleIds));
        }
        return baseSysMenuMapper.selectListByQuery(queryWrapper.groupBy(BASE_SYS_MENU_ENTITY.ID).orderBy(BASE_SYS_MENU_ENTITY.ORDER_NUM, false));
    }

    @Override
    public List<SysMenuEntity> getMenus(Long userId) {
        return getMenus(getRoles(userId));
    }

    @Override
    public List<SysMenuEntity> getMenus(String username) {
        SysUserEntity sysUserEntity = baseSysUserMapper
                .selectOneByQuery(QueryWrapper.create().eq(SysUserEntity::getUsername, username));
        return getMenus(sysUserEntity.getId());
    }

    @Override
    public String[] getAllPerms() {
        return getPerms((Long[]) null);
    }

    @Override
    public Dict permmenu(Long adminUserId) {
        return Dict.create().set("menus", getMenus(adminUserId)).set("perms", getPerms(adminUserId));
    }

    @Override
    public void updatePerms(Long roleId, Long[] menuIdList, Long[] departmentIds) {
        // 更新菜单权限
        baseSysRoleMenuMapper.deleteByQuery(QueryWrapper.create().eq(SysRoleMenuEntity::getRoleId, roleId));
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
                .deleteByQuery(QueryWrapper.create().eq(SysRoleDepartmentEntity::getRoleId, roleId));
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
                    .selectListByQuery(QueryWrapper.create().eq(SysUserRoleEntity::getRoleId, roleId));
            for (SysUserRoleEntity userRole : userRoles) {
                refreshPerms(userRole.getUserId());
            }
        });
    }

    @Override
    public void updateUserRole(Long userId, Long[] roleIdList) {
        baseSysUserRoleMapper.deleteByQuery(QueryWrapper.create().eq(SysUserRoleEntity::getUserId, userId));
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
            SpringContextUtils.getBean(UserDetailsService.class).loadUserByUsername(baseSysUserEntity.getUsername());
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
                .selectOneByQuery(QueryWrapper.create().eq(SysUserEntity::getUsername, "admin"));
        refreshPerms(admin.getId());
        List<Row> list = baseSysRoleMenuMapper.selectRowsByQuery(QueryWrapper.create().select(BASE_SYS_USER_ROLE_ENTITY.USER_ID)
                .from(BASE_SYS_ROLE_MENU_ENTITY).leftJoin(BASE_SYS_USER_ROLE_ENTITY)
                .on(BASE_SYS_ROLE_MENU_ENTITY.ROLE_ID.eq(BASE_SYS_USER_ROLE_ENTITY.ROLE_ID)).and(BASE_SYS_ROLE_MENU_ENTITY.MENU_ID.eq(menuId, ObjectUtil.isNotEmpty(menuId))).groupBy(BASE_SYS_USER_ROLE_ENTITY.USER_ID));
        for (Row row : list) {
            refreshPerms(row.getLong("userId"));
        }
    }

    @Override
    public void refreshPermsByRoleId(Long roleId) {
        // 找出角色对应的所有用户
        List<SysUserRoleEntity> list = baseSysUserRoleMapper
                .selectListByQuery(QueryWrapper.create().eq(SysUserRoleEntity::getRoleId, roleId));
        list.forEach(e -> {
            refreshPerms(e.getUserId());
        });
    }
}
