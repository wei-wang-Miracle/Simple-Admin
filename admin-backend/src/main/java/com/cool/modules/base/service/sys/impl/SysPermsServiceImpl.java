package com.cool.modules.base.service.sys.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cool.core.cache.CoolCache;
import com.cool.core.security.jwt.JwtUser;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.base.entity.sys.*;
import com.cool.modules.base.mapper.sys.*;
import com.cool.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限菜单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysPermsServiceImpl implements SysPermsService {

    private final CoolCache coolCache;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysRoleDepartmentMapper sysRoleDepartmentMapper;

    @Override
    public Long[] loginDepartmentIds() {
        Long userId = CoolSecurityUtil.getAdminUserId();
        if (userId == null) {
            return new Long[0];
        }
        Long[] roleIds = getRoles(userId);
        return getDepartmentIdsByRoleIds(roleIds);
    }

    @Override
    public Long[] getDepartmentIdsByRoleIds(Long[] roleIds) {
        if (ArrayUtil.isEmpty(roleIds)) {
            return new Long[0];
        }
        LambdaQueryWrapper<SysRoleDepartmentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleDepartmentEntity::getRoleId, Arrays.asList(roleIds));
        List<SysRoleDepartmentEntity> list = sysRoleDepartmentMapper.selectList(wrapper);
        return list.stream()
                .map(SysRoleDepartmentEntity::getDepartmentId)
                .distinct()
                .toArray(Long[]::new);
    }

    @Override
    public Long[] getDepartmentIdsByUserId(Long userId) {
        Long[] roleIds = getRoles(userId);
        return getDepartmentIdsByRoleIds(roleIds);
    }

    @Override
    public String[] getPermsCache(Long userId) {
        String key = "admin:perms:" + userId;
        String[] perms = coolCache.get(key, String[].class);
        if (perms == null) {
            perms = getPerms(userId);
            coolCache.set(key, perms);
        }
        return perms;
    }

    @Override
    public Long[] getRoles(Long userId) {
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getUserId, userId);
        List<SysUserRoleEntity> list = sysUserRoleMapper.selectList(wrapper);
        return list.stream()
                .map(SysUserRoleEntity::getRoleId)
                .toArray(Long[]::new);
    }

    @Override
    public Long[] getRoles(String username) {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserEntity::getUsername, username);
        SysUserEntity user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            return new Long[0];
        }
        return getRoles(user.getId());
    }

    @Override
    public Long[] getRoles(SysUserEntity userEntity) {
        if (userEntity == null) {
            return new Long[0];
        }
        // 如果是 admin 用户，返回所有角色
        if ("admin".equals(userEntity.getUsername())) {
            List<SysRoleEntity> allRoles = sysRoleMapper.selectList(null);
            return allRoles.stream()
                    .map(SysRoleEntity::getId)
                    .toArray(Long[]::new);
        }
        return getRoles(userEntity.getId());
    }

    @Override
    public String[] getPerms(Long userId) {
        Long[] roleIds = getRoles(userId);
        return getPerms(roleIds);
    }

    @Override
    public String[] getPerms(Long[] roleIds) {
        if (ArrayUtil.isEmpty(roleIds)) {
            return new String[0];
        }
        List<SysMenuEntity> menus = getMenus(roleIds);
        return menus.stream()
                .map(SysMenuEntity::getPerms)
                .filter(StrUtil::isNotBlank)
                .flatMap(perms -> Arrays.stream(perms.split(",")))
                .distinct()
                .toArray(String[]::new);
    }

    @Override
    public List<SysMenuEntity> getMenus(Long[] roleIds) {
        if (ArrayUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return sysMenuMapper.selectByRoleIds(Arrays.asList(roleIds));
    }

    @Override
    public List<SysMenuEntity> getMenus(Long userId) {
        Long[] roleIds = getRoles(userId);
        return getMenus(roleIds);
    }

    @Override
    public List<SysMenuEntity> getMenus(String username) {
        Long[] roleIds = getRoles(username);
        return getMenus(roleIds);
    }

    @Override
    public String[] getAllPerms() {
        List<SysMenuEntity> menus = sysMenuMapper.selectList(null);
        return menus.stream()
                .map(SysMenuEntity::getPerms)
                .filter(StrUtil::isNotBlank)
                .flatMap(perms -> Arrays.stream(perms.split(",")))
                .distinct()
                .toArray(String[]::new);
    }

    @Override
    public Dict permmenu(Long adminUserId) {
        SysUserEntity user = sysUserMapper.selectById(adminUserId);
        List<SysMenuEntity> menus;
        String[] perms;

        if (user != null && "admin".equals(user.getUsername())) {
            // admin用户拥有所有权限
            menus = sysMenuMapper.selectList(null);
            perms = getAllPerms();
        } else {
            menus = getMenus(adminUserId);
            perms = getPerms(adminUserId);
        }

        return Dict.create()
                .set("menus", menus)
                .set("perms", perms);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePerms(Long roleId, Long[] menuIdList, Long[] departmentIds) {
        // 删除旧的角色菜单关系
        LambdaQueryWrapper<SysRoleMenuEntity> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(SysRoleMenuEntity::getRoleId, roleId);
        sysRoleMenuMapper.delete(menuWrapper);

        // 添加新的角色菜单关系
        if (ArrayUtil.isNotEmpty(menuIdList)) {
            for (Long menuId : menuIdList) {
                SysRoleMenuEntity roleMenu = new SysRoleMenuEntity();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }

        // 删除旧的角色部门关系
        LambdaQueryWrapper<SysRoleDepartmentEntity> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(SysRoleDepartmentEntity::getRoleId, roleId);
        sysRoleDepartmentMapper.delete(deptWrapper);

        // 添加新的角色部门关系
        if (ArrayUtil.isNotEmpty(departmentIds)) {
            for (Long departmentId : departmentIds) {
                SysRoleDepartmentEntity roleDept = new SysRoleDepartmentEntity();
                roleDept.setRoleId(roleId);
                roleDept.setDepartmentId(departmentId);
                sysRoleDepartmentMapper.insert(roleDept);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(Long userId, Long[] roleIdList) {
        // 删除旧的用户角色关系
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);

        // 添加新的用户角色关系
        if (ArrayUtil.isNotEmpty(roleIdList)) {
            for (Long roleId : roleIdList) {
                SysUserRoleEntity userRole = new SysUserRoleEntity();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }

        // 刷新权限缓存
        refreshPerms(userId);
    }

    @Override
    @Async
    public void refreshPerms(Long userId) {
        String[] perms = getPerms(userId);
        coolCache.set("admin:perms:" + userId, perms);

        // 更新用户详情缓存
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user != null) {
            List<GrantedAuthority> authorities = Arrays.stream(perms)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            // 增加 ADMIN 角色
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            JwtUser jwtUser = new JwtUser(user.getUsername(), user.getPassword(), authorities);
            coolCache.set("admin:userDetails:" + user.getUsername(), jwtUser);
            coolCache.set("admin:passwordVersion:" + userId, user.getPasswordV());
        }
    }

    @Override
    public void refreshPermsByMenuId(Long menuId) {
        // 获取关联此菜单的所有角色
        LambdaQueryWrapper<SysRoleMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuEntity::getMenuId, menuId);
        List<SysRoleMenuEntity> roleMenus = sysRoleMenuMapper.selectList(wrapper);

        for (SysRoleMenuEntity roleMenu : roleMenus) {
            refreshPermsByRoleId(roleMenu.getRoleId());
        }
    }

    @Override
    public void refreshPermsByRoleId(Long roleId) {
        // 获取关联此角色的所有用户
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getRoleId, roleId);
        List<SysUserRoleEntity> userRoles = sysUserRoleMapper.selectList(wrapper);

        for (SysUserRoleEntity userRole : userRoles) {
            refreshPerms(userRole.getUserId());
        }
    }
}
