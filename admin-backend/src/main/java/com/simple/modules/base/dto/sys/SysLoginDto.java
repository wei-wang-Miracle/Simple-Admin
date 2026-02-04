package com.simple.modules.base.dto.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录 DTO
 */
@Data
@Schema(description = "登录参数")
public class SysLoginDto {

    @Schema(description = "用户名")
    @NotBlank
    private String username;

    @Schema(description = "密码")
    @NotBlank
    private String password;
}
