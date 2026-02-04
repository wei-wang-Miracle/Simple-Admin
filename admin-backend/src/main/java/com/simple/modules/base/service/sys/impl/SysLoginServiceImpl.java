package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.enums.UserTypeEnum;
import com.simple.core.exception.SimplePreconditions;
import com.simple.core.security.jwt.JwtTokenUtil;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.dto.sys.SysLoginDto;
import com.simple.modules.base.entity.sys.SysUserEntity;
import com.simple.modules.base.mapper.sys.SysUserMapper;
import com.simple.modules.base.service.sys.SysLoginService;
import com.simple.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 系统登录 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysLoginServiceImpl implements SysLoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final SysUserMapper sysUserMapper;
    private final SysPermsService sysPermsService;

    @Override
    public Object login(SysLoginDto baseSysLoginDto) {
        // 1、检查验证码是否正确 2、执行登录操作
        UsernamePasswordAuthenticationToken upToken =
                new UsernamePasswordAuthenticationToken(
                        baseSysLoginDto.getUsername(), baseSysLoginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 查询用户信息并生成token
        SysUserEntity baseSysUserEntity =
                sysUserMapper.selectOneByQuery(
                        QueryWrapper.create()
                                .eq(SysUserEntity::getUsername, baseSysLoginDto.getUsername()));
        Long[] roleIds = sysPermsService.getRoles(baseSysUserEntity);
        return generateToken(roleIds, baseSysUserEntity, null);
    }

    @Override
    public void logout(Long adminUserId, String username) {
        SecurityUtil.adminLogout(adminUserId, username);
    }

    @Override
    public Object refreshToken(String refreshToken) {
        JWT jwt = jwtTokenUtil.getTokenInfo(refreshToken);
        SimplePreconditions.check(jwt == null || !(Boolean) jwt.getPayload("isRefresh"),
                "错误的refreshToken");
        SysUserEntity baseSysUserEntity =
                sysUserMapper.selectOneById(Convert.toLong(jwt.getPayload("userId")));
        Long[] roleIds = sysPermsService.getRoles(baseSysUserEntity);
        return generateToken(roleIds, baseSysUserEntity, refreshToken);
    }

    private Dict generateToken(Long[] roleIds, SysUserEntity baseSysUserEntity, String refreshToken) {
        Dict tokenInfo =
                Dict.create()
                        .set("userType", UserTypeEnum.ADMIN.name())
                        .set("roleIds", roleIds)
                        .set("username", baseSysUserEntity.getUsername())
                        .set("userId", baseSysUserEntity.getId())
                        .set("passwordVersion", baseSysUserEntity.getPasswordV());
        String token = jwtTokenUtil.generateToken(tokenInfo);
        if (StrUtil.isEmpty(refreshToken)) {
            refreshToken = jwtTokenUtil.generateRefreshToken(tokenInfo);
        }
        return Dict.create()
                .set("token", token)
                .set("expire", jwtTokenUtil.getExpire())
                .set("refreshToken", refreshToken)
                .set("refreshExpire", jwtTokenUtil.getRefreshExpire());
    }
}
