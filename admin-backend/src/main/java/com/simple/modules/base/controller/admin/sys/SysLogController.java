package com.simple.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simple.core.request.RestResult;
import com.simple.modules.base.entity.sys.SysLogEntity;
import com.simple.modules.base.service.sys.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统日志 Controller
 */
@Api(tags = "系统日志")
@RestController
@RequestMapping("/admin/base/sys/log")
@RequiredArgsConstructor
public class SysLogController {

	private final SysLogService baseSysLogService;

	@ApiOperation("分页")
	@PostMapping("/page")
	public RestResult page(@RequestBody Map<String, Object> params) {
		int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
		int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 15;

		LambdaQueryWrapper<SysLogEntity> wrapper = new LambdaQueryWrapper<>();
		// 关键词搜索
		if (params.get("keyWord") != null) {
			String keyWord = params.get("keyWord").toString();
			wrapper.like(SysLogEntity::getAction, keyWord);
		}
		wrapper.orderByDesc(SysLogEntity::getCreateTime);

		Page<SysLogEntity> pageResult = baseSysLogService.page(new Page<>(page, size), wrapper);

		return RestResult.ok(Dict.create()
				.set("list", pageResult.getRecords())
				.set("pagination", Dict.create()
						.set("page", pageResult.getCurrent())
						.set("size", pageResult.getSize())
						.set("total", pageResult.getTotal())));
	}

	@ApiOperation("清理日志")
	@PostMapping("/clear")
	public RestResult clear(@RequestBody(required = false) Map<String, Object> params) {
		Boolean isAll = params != null && Boolean.TRUE.equals(params.get("isAll"));
		baseSysLogService.clear(isAll);
		return RestResult.ok();
	}
}
