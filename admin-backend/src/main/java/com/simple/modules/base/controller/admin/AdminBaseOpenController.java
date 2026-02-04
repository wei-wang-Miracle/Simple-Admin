package com.simple.modules.base.controller.admin;

import com.simple.core.annotation.TokenIgnore;
import com.simple.core.request.RestResult;
import com.simple.modules.base.dto.sys.SysLoginDto;
import com.simple.modules.base.service.sys.SysLoginService;
import com.simple.modules.base.service.sys.SysParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统开放接口 Controller（无需登录）
 */
@Api(tags = "系统开放接口")
@RestController
@RequestMapping("/admin/base/open")
@RequiredArgsConstructor
public class AdminBaseOpenController {

    private final SysLoginService baseSysLoginService;
    private final SysParamService baseSysParamService;

    @TokenIgnore
    @ApiOperation("登录")
    @PostMapping("/login")
    public RestResult login(@RequestBody SysLoginDto baseSysLoginDto) {
        return RestResult.ok(baseSysLoginService.login(baseSysLoginDto));
    }

    @TokenIgnore
    @ApiOperation("刷新token")
    @PostMapping("/refreshToken")
    public RestResult refreshToken(@RequestBody Map<String, String> params) {
        String refreshToken = params.get("refreshToken");
        return RestResult.ok(baseSysLoginService.refreshToken(refreshToken));
    }

    @TokenIgnore
    @ApiOperation("获取参数HTML")
    @GetMapping("/html")
    public RestResult html(@RequestParam String key) {
        return RestResult.ok(baseSysParamService.htmlByKey(key));
    }

    @TokenIgnore
    @ApiOperation("编程语言")
    @GetMapping("/program")
    public RestResult program() {
        return RestResult.ok("Java");
    }

    @TokenIgnore
    @ApiOperation("EPS接口（已简化，返回空数据）")
    @GetMapping("/eps")
    public RestResult eps() {
        // EPS 功能已简化移除，返回空数组以兼容前端
        return RestResult.ok(new Object[0]);
    }
}
