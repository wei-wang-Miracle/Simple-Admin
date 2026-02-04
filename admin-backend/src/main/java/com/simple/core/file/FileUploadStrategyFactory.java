package com.simple.core.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * 文件上传策略工厂
 */
@Component
public class FileUploadStrategyFactory {

	/**
	 * 上传文件
	 */
	public Object upload(MultipartFile[] files, HttpServletRequest request) {
		// 简化实现，实际项目可以扩展
		return Collections.emptyList();
	}

	/**
	 * 获取上传模式
	 */
	public String getMode() {
		return "local";
	}
}