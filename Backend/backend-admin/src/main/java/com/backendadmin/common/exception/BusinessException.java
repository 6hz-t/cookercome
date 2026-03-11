package com.admin.backendadmin.common.exception;

import com.admin.backendadmin.common.constant.ResultCodeConstant;
import lombok.Getter;

/**
 * 自定义业务异常
 * 用于处理业务逻辑中的预期异常（如：用户名不存在、厨师已审核、订单状态错误等）
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 异常状态码（默认500，可自定义）
     */
    private final Integer code;

    // ========== 构造方法 ==========

    /**
     * 仅传入异常消息（默认状态码：500）
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCodeConstant.ERROR;
    }

    /**
     * 传入状态码 + 异常消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 传入状态码 + 异常消息 + 根因异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    // ========== 快捷创建方法（语义优化，保持原有功能） ==========
    /**
     * 快捷创建：参数错误异常（状态码400）
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ResultCodeConstant.PARAM_ERROR, message);
    }

    /**
     * 快捷创建：未登录异常（状态码401）
     */
    public static BusinessException unAuthorized(String message) {
        return new BusinessException(ResultCodeConstant.UNAUTHORIZED, message);
    }

    /**
     * 快捷创建：无权限异常（状态码403）
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(ResultCodeConstant.FORBIDDEN, message);
    }

    /**
     * 快捷创建：资源不存在异常（状态码404）
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(ResultCodeConstant.NOT_FOUND, message);
    }
}