package com.simple.core.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 用于封装所有 API 接口的返回结果
 *
 * @param <T> 响应数据类型
 */
@ApiModel(description = "响应数据结构")
@Data
public class RestResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码：1000 表示成功，其他值表示失败
     */
    @ApiModelProperty(value = "编码：1000表示成功，其他值表示失败")
    private int code = 1000;

    /**
     * 响应消息
     */
    @ApiModelProperty(value = "消息内容")
    private String message = "success";

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T data;

    public RestResult() {
    }

    public RestResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回错误结果（默认错误码和消息）
     */
    public static RestResult error() {
        return error(1001, "请求方式不正确或服务出现异常");
    }

    /**
     * 返回错误结果（自定义消息）
     */
    public static RestResult error(String msg) {
        return error(1001, msg);
    }

    /**
     * 返回错误结果（自定义错误码和消息）
     */
    public static RestResult error(int code, String msg) {
        RestResult restResult = new RestResult();
        restResult.code = code;
        restResult.message = msg;
        return restResult;
    }

    /**
     * 返回成功结果（自定义消息）
     */
    public static RestResult okMsg(String msg) {
        RestResult restResult = new RestResult();
        restResult.message = msg;
        return restResult;
    }

    /**
     * 返回成功结果（无数据）
     */
    public static RestResult ok() {
        return new RestResult();
    }

    /**
     * 返回成功结果（带数据）
     */
    public static <B> RestResult<B> ok(B data) {
        return new RestResult<B>(1000, "success", data);
    }

    /**
     * 设置属性值（兼容旧版本调用方式）
     */
    public RestResult<T> put(String key, Object value) {
        if ("code".equals(key)) {
            this.code = (int) value;
        } else if ("message".equals(key)) {
            this.message = (String) value;
        } else if ("data".equals(key)) {
            this.data = (T) value;
        }
        return this;
    }
}