package com.cool.modules.base.controller.admin;

import cn.hutool.core.lang.Dict;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.file.FileUploadStrategyFactory;
import com.cool.core.request.R;
import com.cool.modules.base.entity.sys.SysMenuEntity;
import com.cool.modules.base.entity.sys.SysUserEntity;
import com.cool.modules.base.service.sys.SysLoginService;
import com.cool.modules.base.service.sys.SysPermsService;
import com.cool.modules.base.service.sys.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public R person(@RequestAttribute Long adminUserId) {
        SysUserEntity user = baseSysUserService.getById(adminUserId);
        if (user != null) {
            user.setPassword(null);
            user.setPasswordV(null);
        }
        return R.ok(user);
    }

    @ApiOperation("修改个人信息")
    @PostMapping("/personUpdate")
    public R personUpdate(@RequestAttribute Long adminUserId, @RequestBody Dict body) {
        baseSysUserService.personUpdate(adminUserId, body);
        return R.ok();
    }

    @ApiOperation("权限与菜单")
    @GetMapping("/permmenu")
    public R permmenu(@RequestAttribute Long adminUserId) {
        Dict permmenu = baseSysPermsService.permmenu(adminUserId);
        return R.ok(permmenu);
    }

    @ApiOperation("文件上传")
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.ALL_VALUE })
    public R upload(@RequestPart(value = "file", required = false) MultipartFile[] files,
            HttpServletRequest request) {
        return R.ok(fileUploadStrategyFactory.upload(files, request));
    }

    @ApiOperation("文件上传模式")
    @GetMapping("/uploadMode")
    public R uploadMode() {
        return R.ok(fileUploadStrategyFactory.getMode());
    }

    @ApiOperation("退出")
    @PostMapping("/logout")
    public R logout(@RequestAttribute Long adminUserId, @RequestAttribute String adminUsername) {
        baseSysLoginService.logout(adminUserId, adminUsername);
        return R.ok();
    }

    @TokenIgnore
    @ApiOperation("编程")
    @GetMapping("/program")
    public R program() {
        return R.ok("Java");
    }
}
