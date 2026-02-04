package com.cool.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cool.core.request.R;
import com.cool.modules.base.entity.sys.SysMenuEntity;
import com.cool.modules.base.service.sys.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统菜单 Controller
 */
@Api(tags = "系统菜单")
@RestController
@RequestMapping("/admin/base/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService baseSysMenuService;

    @ApiOperation("新增")
    @PostMapping("/add")
    public R add(@RequestBody SysMenuEntity entity) {
        baseSysMenuService.save(entity);
        return R.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        baseSysMenuService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R update(@RequestBody SysMenuEntity entity) {
        baseSysMenuService.updateById(entity);
        return R.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public R info(@RequestParam Long id) {
        return R.ok(baseSysMenuService.getById(id));
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public R list(@RequestBody(required = false) Map<String, Object> params) {
        LambdaQueryWrapper<SysMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenuEntity::getOrderNum);
        return R.ok(baseSysMenuService.list(wrapper));
    }

    @ApiOperation("分页")
    @PostMapping("/page")
    public R page(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 15;

        LambdaQueryWrapper<SysMenuEntity> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索
        if (params.get("keyWord") != null) {
            String keyWord = params.get("keyWord").toString();
            wrapper.like(SysMenuEntity::getName, keyWord);
        }
        wrapper.orderByAsc(SysMenuEntity::getOrderNum);

        Page<SysMenuEntity> pageResult = baseSysMenuService.page(new Page<>(page, size), wrapper);

        return R.ok(Dict.create()
                .set("list", pageResult.getRecords())
                .set("pagination", Dict.create()
                        .set("page", pageResult.getCurrent())
                        .set("size", pageResult.getSize())
                        .set("total", pageResult.getTotal())));
    }

    @ApiOperation("导出")
    @PostMapping("/export")
    public R export(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        return R.ok(baseSysMenuService.export(ids));
    }

    @ApiOperation("导入")
    @PostMapping("/import")
    public R importMenu(@RequestBody List<SysMenuEntity> menus) {
        baseSysMenuService.importMenu(menus);
        return R.ok();
    }

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