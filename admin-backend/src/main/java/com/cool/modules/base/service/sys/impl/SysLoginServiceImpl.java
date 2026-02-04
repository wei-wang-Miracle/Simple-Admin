package com.cool.modules.base.service.sys.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cool.core.enums.UserTypeEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.security.jwt.JwtTokenUtil;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.base.dto.sys.SysLoginDto;
import com.cool.modules.base.entity.sys.SysUserEntity;
import com.cool.modules.base.mapper.sys.SysUserMapper;
import com.cool.modules.base.service.sys.SysLoginService;
import com.cool.modules.base.service.sys.SysPermsService;
import io.jsonwebtoken.Claims;
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
	public Object login(SysLoginDto sysLoginDto) {
		// 1、执行登录认证
		UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(
				sysLoginDto.getUsername(), sysLoginDto.getPassword());
		Authentication authentication = authenticationManager.authenticate(upToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 2、查询用户信息
		LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SysUserEntity::getUsername, sysLoginDto.getUsername());
		SysUserEntity sysUserEntity = sysUserMapper.selectOne(wrapper);

		CoolPreconditions.check(
				ObjectUtil.isEmpty(sysUserEntity) || sysUserEntity.getStatus() == 0,
				"用户已禁用");

		// 3、生成token
		Long[] roleIds = sysPermsService.getRoles(sysUserEntity);

		return generateToken(roleIds, sysUserEntity, null);
	}

	@Override
	public void logout(Long adminUserId, String username) {
		CoolSecurityUtil.adminLogout(adminUserId, username);
	}

	@Override
	public Object refreshToken(String refreshToken) {
		CoolPreconditions.check(!jwtTokenUtil.validateRefreshToken(refreshToken), "错误的refreshToken");

		Claims claims = jwtTokenUtil.getTokenInfo(refreshToken);
		CoolPreconditions.check(claims == null || !Boolean.TRUE.equals(claims.get("isRefresh")),
				"错误的refreshToken");

		Long userId = Long.valueOf(claims.get("userId").toString());
		SysUserEntity sysUserEntity = sysUserMapper.selectById(userId);
		Long[] roleIds = sysPermsService.getRoles(sysUserEntity);

		return generateToken(roleIds, sysUserEntity, refreshToken);
	}

	private Dict generateToken(Long[] roleIds, SysUserEntity sysUserEntity, String refreshToken) {
		Dict tokenInfo = Dict.create()
				.set("userType", UserTypeEnum.ADMIN.name())
				.set("roleIds", roleIds)
				.set("username", sysUserEntity.getUsername())
				.set("userId", sysUserEntity.getId())
				.set("passwordVersion", sysUserEntity.getPasswordV());

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
