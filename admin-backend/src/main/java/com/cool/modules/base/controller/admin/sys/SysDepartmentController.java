package com.cool.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cool.core.request.R;
import com.cool.modules.base.entity.sys.SysDepartmentEntity;
import com.cool.modules.base.service.sys.SysDepartmentService;
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
    public R add(@RequestBody SysDepartmentEntity entity) {
        sysDepartmentService.save(entity);
        return R.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        sysDepartmentService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R update(@RequestBody SysDepartmentEntity entity) {
        sysDepartmentService.updateById(entity);
        return R.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public R info(@RequestParam Long id) {
        return R.ok(sysDepartmentService.getById(id));
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public R list(@RequestBody(required = false) Map<String, Object> params) {
        LambdaQueryWrapper<SysDepartmentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDepartmentEntity::getOrderNum);
        List<SysDepartmentEntity> list = sysDepartmentService.list(wrapper);
        return R.ok(list);
    }

    @ApiOperation("排序")
    @PostMapping("/order")
    public R order(@RequestBody List<SysDepartmentEntity> list) {
        sysDepartmentService.order(list);
        return R.ok();
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