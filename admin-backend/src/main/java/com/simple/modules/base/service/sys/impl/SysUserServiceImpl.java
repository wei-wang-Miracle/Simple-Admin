package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.modules.base.entity.sys.SysUserEntity;
import com.simple.modules.base.mapper.sys.SysDepartmentMapper;
import com.simple.modules.base.mapper.sys.SysUserMapper;
import com.simple.modules.base.service.sys.SysPermsService;
import com.simple.modules.base.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 系统用户 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl
        extends BaseServiceImpl<SysUserMapper, SysUserEntity>
        implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final SysPermsService sysPermsService;
    final private SysDepartmentMapper baseSysDepartmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long departmentId, Long[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return;
        }
        LambdaUpdateWrapper<SysUserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysUserEntity::getId, Arrays.asList(userIds))
                .set(SysUserEntity::getDepartmentId, departmentId);
        this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void personUpdate(Long userId, Dict body) {
        SysUserEntity user = this.getById(userId);
        if (user == null) {
            return;
        }
        // 只允许修改部分字段
        if (body.containsKey("nickName")) {
            user.setNickName(body.getStr("nickName"));
        }
        if (body.containsKey("headImg")) {
            user.setHeadImg(body.getStr("headImg"));
        }
        if (body.containsKey("phone")) {
            user.setPhone(body.getStr("phone"));
        }
        if (body.containsKey("email")) {
            user.setEmail(body.getStr("email"));
        }
        // 如果修改密码
        if (body.containsKey("password") && StrUtil.isNotBlank(body.getStr("password"))) {
            user.setPassword(passwordEncoder.encode(body.getStr("password")));
            user.setPasswordV(user.getPasswordV() == null ? 1 : user.getPasswordV() + 1);
        }
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SysUserEntity entity) {
        // 密码加密
        if (StrUtil.isNotBlank(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        boolean result = super.save(entity);
        // 保存用户角色关系
        if (entity.getRoleIdList() != null && !entity.getRoleIdList().isEmpty()) {
            sysPermsService.updateUserRole(entity.getId(),
                    entity.getRoleIdList().toArray(new Long[0]));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SysUserEntity entity) {
        // 如果密码不为空且被修改，则加密
        if (StrUtil.isNotBlank(entity.getPassword())) {
            SysUserEntity oldUser = this.getById(entity.getId());
            if (oldUser != null && !entity.getPassword().equals(oldUser.getPassword())) {
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
                entity.setPasswordV(oldUser.getPasswordV() == null ? 1 : oldUser.getPasswordV() + 1);
            }
        }
        boolean result = super.updateById(entity);
        // 更新用户角色关系
        if (entity.getRoleIdList() != null) {
            sysPermsService.updateUserRole(entity.getId(),
                    entity.getRoleIdList().toArray(new Long[0]));
        }
        return result;
    }
}
