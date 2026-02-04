package com.simple.modules.base.controller.admin;

import com.simple.core.annotation.TokenIgnore;
import com.simple.core.request.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 字典信息 Controller（兼容前端，返回空数据）
 * 原 dict 模块已移除
 */
@Api(tags = "字典信息（兼容接口）")
@RestController
@RequestMapping("/admin/dict/info")
@RequiredArgsConstructor
public class AdminDictInfoController {

    @TokenIgnore
    @ApiOperation("获取字典类型列表")
    @GetMapping("/types")
    public RestResult types() {
        // dict 模块已移除，返回空数组以兼容前端
        return RestResult.ok(new Object[0]);
    }
}
