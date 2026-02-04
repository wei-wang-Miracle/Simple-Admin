package com.simple.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simple.core.request.RestResult;
import com.simple.modules.base.entity.sys.SysDepartmentEntity;
import com.simple.modules.base.service.sys.SysDepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统部门 Controller
 */
@Api(tags = "系统部门")
@RestController
@RequestMapping("/admin/base/sys/department")
@RequiredArgsConstructor
public class SysDepartmentController {

    private final SysDepartmentService sysDepartmentService;

    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResult add(@RequestBody SysDepartmentEntity entity) {
        sysDepartmentService.save(entity);
        return RestResult.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public RestResult delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        sysDepartmentService.removeByIds(ids);
        return RestResult.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResult update(@RequestBody SysDepartmentEntity entity) {
        sysDepartmentService.updateById(entity);
        return RestResult.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public RestResult info(@RequestParam Long id) {
        return RestResult.ok(sysDepartmentService.getById(id));
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public RestResult list(@RequestBody(required = false) Map<String, Object> params) {
        List<SysDepartmentEntity> list = sysDepartmentService.list(wrapper);
        return RestResult.ok(list);
    }

    @ApiOperation("排序")
    @PostMapping("/order")
    public RestResult order(@RequestBody List<SysDepartmentEntity> list) {
        sysDepartmentService.order(list);
        return RestResult.ok();
    }

    /**
     * 获取请求参数中的 ids
     */
    private List<Long> getIds(Map<String, Object> params) {
        Object ids = params.get("ids");
        if (ids instanceof List) {
            return ((List<?>) ids).stream()
                    .map(id -> Long.parseLong(id.toString()))
                    .collect(Collectors.toList());
        }
        return Arrays.stream(ids.toString().split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}