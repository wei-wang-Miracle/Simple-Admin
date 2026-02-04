package com.simple.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.request.RestResult;
import com.simple.modules.base.entity.sys.SysMenuEntity;
import com.simple.modules.base.service.sys.SysMenuService;
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
    public RestResult add(@RequestBody SysMenuEntity entity) {
        baseSysMenuService.save(entity);
        return RestResult.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public RestResult delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        baseSysMenuService.removeByIds(ids);
        return RestResult.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResult update(@RequestBody SysMenuEntity entity) {
        baseSysMenuService.updateById(entity);
        return RestResult.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public RestResult info(@RequestParam Long id) {
        return RestResult.ok(baseSysMenuService.getById(id));
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public RestResult list(@RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(SysMenuEntity::getOrderNum, true);
        return RestResult.ok(baseSysMenuService.list(wrapper));
    }

    @ApiOperation("分页")
    @PostMapping("/page")
    public RestResult page(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 15;

        QueryWrapper wrapper = QueryWrapper.create();
        // 关键词搜索
        if (params.get("keyWord") != null) {
            String keyWord = params.get("keyWord").toString();
            wrapper.where(SysMenuEntity::getName).like(keyWord);
        }
        wrapper.orderBy(SysMenuEntity::getOrderNum, true);

        Page<SysMenuEntity> pageResult = baseSysMenuService.page(new Page<>(page, size), wrapper);

        return RestResult.ok(Dict.create()
                .set("list", pageResult.getRecords())
                .set("pagination", Dict.create()
                        .set("page", pageResult.getPageNumber())
                        .set("size", pageResult.getPageSize())
                        .set("total", pageResult.getTotalRow())));
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