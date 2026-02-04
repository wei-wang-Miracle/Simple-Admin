package com.simple.modules.base.controller.admin;

import cn.hutool.core.lang.Dict;
import com.simple.core.file.FileUploadStrategyFactory;
import com.simple.core.request.RestResult;
import com.simple.modules.base.entity.sys.SysUserEntity;
import com.simple.modules.base.service.sys.SysLoginService;
import com.simple.modules.base.service.sys.SysPermsService;
import com.simple.modules.base.service.sys.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统通用接口 Controller（需要登录）
 */
@Api(tags = "系统通用")
@RestController
@RequestMapping("/admin/base/comm")
@RequiredArgsConstructor
public class AdminBaseCommController {

    private final SysPermsService baseSysPermsService;
    private final SysUserService baseSysUserService;
    private final SysLoginService baseSysLoginService;
    private final FileUploadStrategyFactory fileUploadStrategyFactory;

    @ApiOperation("个人信息")
    @GetMapping("/person")
    public RestResult person(@RequestAttribute Long adminUserId) {
        SysUserEntity user = baseSysUserService.getById(adminUserId);
        if (user != null) {
            user.setPassword(null);
            user.setPasswordV(null);
        }
        return RestResult.ok(user);
    }

    @ApiOperation("修改个人信息")
    @PostMapping("/personUpdate")
    public RestResult personUpdate(@RequestAttribute Long adminUserId, @RequestBody Dict body) {
        baseSysUserService.personUpdate(adminUserId, body);
        return RestResult.ok();
    }

    @ApiOperation("权限与菜单")
    @GetMapping("/permmenu")
    public RestResult permmenu(@RequestAttribute Long adminUserId) {
        Dict permmenu = baseSysPermsService.permmenu(adminUserId);
        return RestResult.ok(permmenu);
    }

    @ApiOperation("退出")
    @PostMapping("/logout")
    public RestResult logout(@RequestAttribute Long adminUserId, @RequestAttribute String adminUsername) {
        baseSysLoginService.logout(adminUserId, adminUsername);
        return RestResult.ok();
    }

    @ApiOperation("获取上传模式")
    @GetMapping("/uploadMode")
    public RestResult uploadMode() {
        // 获取上传模式：local 为本地上传
        String mode = fileUploadStrategyFactory.getMode();
        return RestResult.ok(Dict.create().set("mode", mode).set("type", mode));
    }

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public RestResult upload(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                             javax.servlet.http.HttpServletRequest request) {
        // 使用文件上传策略工厂处理上传
        Object result = fileUploadStrategyFactory.upload(new org.springframework.web.multipart.MultipartFile[]{file}, request);
        return RestResult.ok(result);
    }
}
