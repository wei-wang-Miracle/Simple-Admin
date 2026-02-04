package com.cool.modules.base.service.sys;

import cn.hutool.core.lang.Dict;
import com.cool.modules.base.entity.sys.SysMenuEntity;
import com.cool.modules.base.entity.sys.SysUserEntity;

import java.util.List;

/**
 * 权限菜单 Service
 */
public interface SysPermsService {

    /**
     * 获得权限缓存
     *
     * @param userId 用户ID
     * @return 权限数组
     */
    String[] getPermsCache(Long userId);

    /**
     * 获得权限
     *
     * @param userId 用户ID
     * @return 权限数组
     */
    String[] getPerms(Long userId);

    /**
     * 获得权限
     *
     * @param roleIds 角色ID数组
     * @return 权限数组
     */
    String[] getPerms(Long[] roleIds);

    /**
     * 获得菜单
     *
     * @param roleIds 角色ID数组
     * @return 菜单列表
     */
    List<SysMenuEntity> getMenus(Long[] roleIds);

    /**
     * 获得菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuEntity> getMenus(Long userId);

    /**
     * 获得菜单
     *
     * @param username 用户名
     * @return 菜单列表
     */
    List<SysMenuEntity> getMenus(String username);

    /**
     * 获得角色数组
     *
     * @param userId 用户ID
     * @return 角色ID数组
     */
    Long[] getRoles(Long userId);

    /**
     * 获得角色数组
     *
     * @param username 用户名
     * @return 角色ID数组
     */
    Long[] getRoles(String username);

    /**
     * 获得角色数组
     *
     * @param userEntity 用户实体
     * @return 角色ID数组
     */
    Long[] getRoles(SysUserEntity userEntity);

    /**
     * 获得登录用户的部门权限
     *
     * @return 部门ID数组
     */
    Long[] loginDepartmentIds();

    /**
     * 根据角色获得部门ID
     *
     * @param roleIds 角色ID数组
     * @return 部门ID数组
     */
    Long[] getDepartmentIdsByRoleIds(Long[] roleIds);

    /**
     * 根据用户ID获得部门ID
     *
     * @param userId 用户ID
     * @return 部门ID数组
     */
    Long[] getDepartmentIdsByUserId(Long userId);

    /**
     * 所有的操作权限
     *
     * @return 权限数组
     */
    String[] getAllPerms();

    /**
     * 用户的权限菜单
     *
     * @param adminUserId 用户ID
     * @return 权限菜单
     */
    Dict permmenu(Long adminUserId);

    /**
     * 更新角色权限
     *
     * @param roleId        角色ID
     * @param menuIdList    菜单ID数组
     * @param departmentIds 部门ID数组
     */
    void updatePerms(Long roleId, Long[] menuIdList, Long[] departmentIds);

    /**
     * 更新用户角色
     *
     * @param userId     用户ID
     * @param roleIdList 角色ID数组
     */
    void updateUserRole(Long userId, Long[] roleIdList);

    /**
     * 刷新权限
     *
     * @param userId 用户ID
     */
    void refreshPerms(Long userId);

    /**
     * 刷新权限
     *
     * @param menuId 菜单ID
     */
    void refreshPermsByMenuId(Long menuId);

    /**
     * 刷新权限
     *
     * @param roleId 角色ID
     */
    void refreshPermsByRoleId(Long roleId);
}
