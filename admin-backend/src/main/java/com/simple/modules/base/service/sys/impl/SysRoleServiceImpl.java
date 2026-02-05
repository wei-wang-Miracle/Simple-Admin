package com.simple.modules.base.service.sys.impl;

import cn.hutool.json.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.core.exception.SimpleException;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.SysRoleDepartmentEntity;
import com.simple.modules.base.entity.sys.SysRoleEntity;
import com.simple.modules.base.entity.sys.SysRoleMenuEntity;
import com.simple.modules.base.mapper.sys.SysRoleDepartmentMapper;
import com.simple.modules.base.mapper.sys.SysRoleMapper;
import com.simple.modules.base.mapper.sys.SysRoleMenuMapper;
import com.simple.modules.base.service.sys.SysPermsService;
import com.simple.modules.base.service.sys.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 系统角色 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl
        extends BaseServiceImpl<SysRoleMapper, SysRoleEntity>
        implements SysRoleService {

    private final SysPermsService sysPermsService;
    final private SysRoleMapper sysRoleMapper;
    final private SysRoleMenuMapper sysRoleMenuMapper;
    final private SysRoleDepartmentMapper sysRoleDepartmentMapper;


    @Override
    public Object add(JSONObject requestParams, SysRoleEntity entity) {
        SysRoleEntity checkLabel = getOne(QueryWrapper.create().eq(SysRoleEntity::getLabel, entity.getLabel()));
        if (checkLabel != null) {
            throw new SimpleException("标识已存在");
        }
        entity.setUserId(SecurityUtil.getCurrentUserId());
        return super.add(requestParams, entity);
    }

    @Override
    public SysRoleEntity info(Long id) {
        SysRoleEntity roleEntity = getById(id);
        Long[] menuIdList = new Long[0];
        Long[] departmentIdList = new Long[0];
        if (roleEntity != null) {
            List<SysRoleMenuEntity> list = sysRoleMenuMapper
                    .selectListByQuery(QueryWrapper.create().eq(SysRoleMenuEntity::getRoleId, id, !id.equals(1L)));
            menuIdList = list.stream().map(SysRoleMenuEntity::getMenuId).toArray(Long[]::new);

            List<SysRoleDepartmentEntity> departmentEntities = sysRoleDepartmentMapper.selectListByQuery(
                    QueryWrapper.create().eq(SysRoleDepartmentEntity::getRoleId, id, !id.equals(1L)));

            departmentIdList = departmentEntities.stream().map(SysRoleDepartmentEntity::getDepartmentId)
                    .toArray(Long[]::new);

            roleEntity.setMenuIdList(Arrays.asList(menuIdList));
            roleEntity.setDepartmentIdList(Arrays.asList(departmentIdList));

        }

        return roleEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SysRoleEntity entity) {
        SysRoleEntity checkLabel = getOne(QueryWrapper.create().eq(SysRoleEntity::getLabel, entity.getLabel()));
        if (checkLabel != null) {
            throw new SimpleException("标识已存在");
        }
        if (entity.getUserId() == null) {
            entity.setUserId(SecurityUtil.getCurrentUserId());
        }
        boolean result = super.save(entity);
        if (result) {
            updateRolePerms(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SysRoleEntity entity) {
        boolean result = super.updateById(entity);
        if (result) {
            updateRolePerms(entity);
        }
        return result;
    }

    private void updateRolePerms(SysRoleEntity entity) {
        if (entity.getMenuIdList() != null || entity.getDepartmentIdList() != null) {
            sysPermsService.updatePerms(entity.getId(),
                    entity.getMenuIdList() == null ? new Long[0] : entity.getMenuIdList().toArray(new Long[0]),
                    entity.getDepartmentIdList() == null ? new Long[0] : entity.getDepartmentIdList().toArray(new Long[0]));
        }
    }
}
