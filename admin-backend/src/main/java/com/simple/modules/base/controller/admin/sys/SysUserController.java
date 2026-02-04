package com.simple.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simple.core.request.RestResult;
import com.simple.modules.base.entity.sys.SysUserEntity;
import com.simple.modules.base.service.sys.SysPermsService;
import com.simple.modules.base.service.sys.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统用户 Controller
 */
@Api(tags = "系统用户")
@RestController
@RequestMapping("/admin/base/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService baseSysUserService;
    private final SysPermsService baseSysPermsService;

    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResult add(@RequestBody SysUserEntity entity) {
        baseSysUserService.save(entity);
        return RestResult.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public RestResult delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        baseSysUserService.removeByIds(ids);
        return RestResult.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResult update(@RequestBody SysUserEntity entity) {
        baseSysUserService.updateById(entity);
        return RestResult.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public RestResult info(@RequestParam Long id) {
        SysUserEntity user = baseSysUserService.getById(id);
        if (user != null) {
            // 获取用户角色
            Long[] roleIds = baseSysPermsService.getRoles(user.getId());
            user.setRoleIdList(Arrays.asList(roleIds));
            // 清除敏感信息
            user.setPassword(null);
        }
        return RestResult.ok(user);
    }

    @ApiOperation("分页")
    @PostMapping("/page")
    public RestResult page(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 15;

        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        // 部门过滤
        if (params.get("departmentIds") != null) {
            List<Long> departmentIds = ((List<?>) params.get("departmentIds")).stream()
                    .map(id -> Long.parseLong(id.toString()))
                    .collect(Collectors.toList());
            wrapper.in(SysUserEntity::getDepartmentId, departmentIds);
        }
        // 关键词搜索
        if (params.get("keyWord") != null) {
            String keyWord = params.get("keyWord").toString();
            wrapper.and(w -> w.like(SysUserEntity::getUsername, keyWord)
                    .or().like(SysUserEntity::getName, keyWord)
                    .or().like(SysUserEntity::getPhone, keyWord));
        }
        wrapper.orderByDesc(SysUserEntity::getCreateTime);

        Page<SysUserEntity> pageResult = baseSysUserService.page(new Page<>(page, size), wrapper);
        // 清除敏感信息
        pageResult.getRecords().forEach(u -> u.setPassword(null));

        return RestResult.ok(Dict.create()
                .set("list", pageResult.getRecords())
                .set("pagination", Dict.create()
                        .set("page", pageResult.getCurrent())
                        .set("size", pageResult.getSize())
                        .set("total", pageResult.getTotal())));
    }

    @ApiOperation("移动用户到指定部门")
    @PostMapping("/move")
    public RestResult move(@RequestBody Map<String, Object> params) {
        Long departmentId = Long.parseLong(params.get("departmentId").toString());
        List<Long> userIds = ((List<?>) params.get("userIds")).stream()
                .map(id -> Long.parseLong(id.toString()))
                .collect(Collectors.toList());
        baseSysUserService.move(departmentId, userIds.toArray(new Long[0]));
        return RestResult.ok();
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