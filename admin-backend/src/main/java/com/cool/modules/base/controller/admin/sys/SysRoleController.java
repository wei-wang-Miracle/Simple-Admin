package com.cool.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.base.entity.sys.SysRoleEntity;
import com.cool.modules.base.entity.sys.SysUserEntity;
import com.cool.modules.base.service.sys.SysRoleService;
import com.cool.modules.base.service.sys.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统角色 Controller
 */
@Api(tags = "系统角色")
@RestController
@RequestMapping("/admin/base/sys/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService baseSysRoleService;
    private final SysUserService baseSysUserService;

    @ApiOperation("新增")
    @PostMapping("/add")
    public R add(@RequestBody SysRoleEntity entity) {
        // 设置创建者ID
        Long userId = CoolSecurityUtil.getAdminUserId();
        entity.setUserId(userId);
        baseSysRoleService.save(entity);
        return R.ok(Dict.create().set("id", entity.getId()));
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        List<Long> ids = getIds(params);
        baseSysRoleService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R update(@RequestBody SysRoleEntity entity) {
        baseSysRoleService.updateById(entity);
        return R.ok();
    }

    @ApiOperation("详情")
    @GetMapping("/info")
    public R info(@RequestParam Long id) {
        return R.ok(baseSysRoleService.getById(id));
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public R list(@RequestBody(required = false) Map<String, Object> params) {
        LambdaQueryWrapper<SysRoleEntity> wrapper = new LambdaQueryWrapper<>();
        // 非 admin 用户只能看到自己创建的角色
        Long adminUserId = CoolSecurityUtil.getAdminUserId();
        SysUserEntity user = baseSysUserService.getById(adminUserId);
        if (user != null && !"admin".equals(user.getUsername())) {
            wrapper.eq(SysRoleEntity::getUserId, adminUserId);
        }
        wrapper.orderByDesc(SysRoleEntity::getCreateTime);
        return R.ok(baseSysRoleService.list(wrapper));
    }

    @ApiOperation("分页")
    @PostMapping("/page")
    public R page(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 15;

        LambdaQueryWrapper<SysRoleEntity> wrapper = new LambdaQueryWrapper<>();
        // 非 admin 用户只能看到自己创建的角色
        Long adminUserId = CoolSecurityUtil.getAdminUserId();
        SysUserEntity user = baseSysUserService.getById(adminUserId);
        if (user != null && !"admin".equals(user.getUsername())) {
            wrapper.eq(SysRoleEntity::getUserId, adminUserId);
        }
        // 关键词搜索
        if (params.get("keyWord") != null) {
            String keyWord = params.get("keyWord").toString();
            wrapper.like(SysRoleEntity::getName, keyWord);
        }
        wrapper.orderByDesc(SysRoleEntity::getCreateTime);

        Page<SysRoleEntity> pageResult = baseSysRoleService.page(new Page<>(page, size), wrapper);

        return R.ok(Dict.create()
                .set("list", pageResult.getRecords())
                .set("pagination", Dict.create()
                        .set("page", pageResult.getCurrent())
                        .set("size", pageResult.getSize())
                        .set("total", pageResult.getTotal())));
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