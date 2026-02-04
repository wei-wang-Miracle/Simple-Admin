package com.simple.modules.base.service.sys.impl;

import cn.hutool.json.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.simple.core.base.BaseServiceImpl;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.SysDepartmentEntity;
import com.simple.modules.base.entity.sys.SysUserEntity;
import com.simple.modules.base.mapper.sys.SysDepartmentMapper;
import com.simple.modules.base.mapper.sys.SysUserMapper;
import com.simple.modules.base.service.sys.SysDepartmentService;
import com.simple.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统部门 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysDepartmentServiceImpl
        extends BaseServiceImpl<SysDepartmentMapper, SysDepartmentEntity>
        implements SysDepartmentService {

    final private SysUserMapper sysUserMapper;

    final private SysPermsService baseSysPermsService;

    @Override
    public void order(List<SysDepartmentEntity> list) {
        list.forEach(baseSysDepartmentEntity -> {
            UpdateChain.of(SysDepartmentEntity.class)
                    .set(SysDepartmentEntity::getOrderNum, baseSysDepartmentEntity.getOrderNum())
                    .set(SysDepartmentEntity::getParentId, baseSysDepartmentEntity.getParentId())
                    .eq(SysDepartmentEntity::getId, baseSysDepartmentEntity.getId()).update();
        });
    }

    @Override
    public List<SysDepartmentEntity> list(JSONObject requestParams, QueryWrapper queryWrapper) {
        String username = SecurityUtil.getCurrentUsername();
        Long[] loginDepartmentIds = baseSysPermsService.loginDepartmentIds();
        if (loginDepartmentIds != null && loginDepartmentIds.length == 0) {
            return new ArrayList<>();
        }
        List<SysDepartmentEntity> list = this.list(
                QueryWrapper.create()
                        .in(SysDepartmentEntity::getId, loginDepartmentIds, !username.equals("admin"))
                        .orderBy(SysDepartmentEntity::getOrderNum, false));
        list.forEach(e -> {
            List<SysDepartmentEntity> parentDepartment = list.stream()
                    .filter(sysDepartmentEntity -> e.getParentId() != null
                                                   && e.getParentId().equals(sysDepartmentEntity.getId())).collect(
                            Collectors.toList());
            if (!parentDepartment.isEmpty()) {
                e.setParentName(parentDepartment.get(0).getName());
            }
        });
        return list;
    }

    @Override
    public boolean delete(JSONObject requestParams, Long... ids) {
        super.delete(ids);
        // 是否删除对应用户 否则移动到顶层部门
        if (requestParams.getBool("deleteUser")) {
            return sysUserMapper
                           .deleteByQuery(
                                   QueryWrapper.create().in(SysUserEntity::getDepartmentId, (Object) ids)) > 0;
        } else {
            SysDepartmentEntity topDepartment = getOne(
                    QueryWrapper.create().isNull(SysDepartmentEntity::getParentId));
            if (topDepartment != null) {
                UpdateChain.of(SysUserEntity.class)
                        .set(SysUserEntity::getDepartmentId, topDepartment.getId())
                        .in(SysUserEntity::getDepartmentId, (Object) ids).update();
            }
        }
        return false;
    }
}
