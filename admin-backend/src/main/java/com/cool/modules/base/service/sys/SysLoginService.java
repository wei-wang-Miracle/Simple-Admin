package com.cool.modules.base.service.sys;

import com.cool.modules.base.dto.sys.SysLoginDto;

/**
 * 系统登录 Service
 */
public interface SysLoginService {

    /**
     * 登录
     *
     * @param sysLoginDto 登录信息
     * @return token信息
     */
    Object login(SysLoginDto sysLoginDto);

    /**
     * 退出登录
     *
     * @param adminUserId 用户ID
     * @param username    用户名
     */
    void logout(Long adminUserId, String username);

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新token
     */
    Object refreshToken(String refreshToken);
}
