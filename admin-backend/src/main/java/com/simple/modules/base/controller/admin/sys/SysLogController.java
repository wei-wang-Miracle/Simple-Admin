package com.simple.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
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

		QueryWrapper wrapper = QueryWrapper.create();
		// 关键词搜索
		if (params.get("keyWord") != null) {
			String keyWord = params.get("keyWord").toString();
			wrapper.where(SysLogEntity::getAction).like(keyWord);
		}
		wrapper.orderBy(SysLogEntity::getCreateTime).desc();

		Page<SysLogEntity> pageResult = baseSysLogService.page(new Page<>(page, size), wrapper);

		return RestResult.ok(Dict.create()
				.set("list", pageResult.getRecords())
				.set("pagination", Dict.create()
						.set("page", pageResult.getPageNumber())
						.set("size", pageResult.getPageSize())
						.set("total", pageResult.getTotalRow())));
	}

	@ApiOperation("清理日志")
	@PostMapping("/clear")
	public RestResult clear(@RequestBody(required = false) Map<String, Object> params) {
		Boolean isAll = params != null && Boolean.TRUE.equals(params.get("isAll"));
		baseSysLogService.clear(isAll);
		return RestResult.ok();
	}
}
