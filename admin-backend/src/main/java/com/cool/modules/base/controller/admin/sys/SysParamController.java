package com.cool.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cool.core.request.R;
import com.cool.modules.base.entity.sys.SysParamEntity;
import com.cool.modules.base.service.sys.SysParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统参数配置 Controller
 */
@Api(tags = "系统参数配置")
@RestController
@RequestMapping("/admin/base/sys/param")
@RequiredArgsConstructor
public class SysParamController {

    private final SysParamService baseSysParamService;

    @ApiOperation("新增")
    @PostMapping("/add")
    public R add(@RequestBody SysParamEntity entity) {
        baseSysParamService.save(entity);
        return R.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        baseSysParamService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R update(@RequestBody SysParamEntity entity) {
        baseSysParamService.updateById(entity);
        return R.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public R info(@RequestParam Long id) {
        return R.ok(baseSysParamService.getById(id));
    }

    @ApiOperation("分页")
    @PostMapping("/page")
    public R page(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 15;

        LambdaQueryWrapper<SysParamEntity> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索
        if (params.get("keyWord") != null) {
            String keyWord = params.get("keyWord").toString();
            wrapper.like(SysParamEntity::getName, keyWord)
                    .or().like(SysParamEntity::getKeyName, keyWord);
        }
        wrapper.orderByDesc(SysParamEntity::getCreateTime);

        Page<SysParamEntity> pageResult = baseSysParamService.page(new Page<>(page, size), wrapper);

        return R.ok(Dict.create()
                .set("list", pageResult.getRecords())
                .set("pagination", Dict.create()
                        .set("page", pageResult.getCurrent())
                        .set("size", pageResult.getSize())
                        .set("total", pageResult.getTotal())));
    }

    @ApiOperation("根据key获取数据")
    @GetMapping("/html")
    public R html(@RequestParam String key) {
        return R.ok(baseSysParamService.htmlByKey(key));
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