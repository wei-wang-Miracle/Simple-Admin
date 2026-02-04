package com.cool.core.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cool.core.cache.CoolCache;
import com.cool.core.security.jwt.JwtUser;
import com.cool.modules.base.entity.sys.SysUserEntity;
import com.cool.modules.base.mapper.sys.SysUserMapper;
import com.cool.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现
 */
@Service
@RequiredArgsConstructor
public class CoolUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysPermsService sysPermsService;
    private final CoolCache coolCache;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserEntity::getUsername, username);
        SysUserEntity user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已禁用");
        }

        // 获取用户权限
        String[] perms = sysPermsService.getPerms(user.getId());
        List<GrantedAuthority> authorities = Arrays.stream(perms)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 增加 ADMIN 角色
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        JwtUser jwtUser = new JwtUser(user.getUsername(), user.getPassword(), authorities);

        // 缓存用户信息
        coolCache.set("admin:userDetails:" + username, jwtUser);
        coolCache.set("admin:passwordVersion:" + user.getId(), user.getPasswordV());

        return jwtUser;
    }
}
