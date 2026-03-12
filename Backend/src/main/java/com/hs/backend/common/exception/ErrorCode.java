package com.hs.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // 用户相关
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PHONE_ALREADY_EXISTS(1003, "手机号已被注册"),
    INVALID_USERNAME_OR_PASSWORD(1004, "用户名或密码错误"),
    USER_DISABLED(1005, "账号已被禁用"),

    // 厨师相关
    CHEF_NOT_FOUND(2001, "厨师不存在"),
    CHEF_AUDIT_FAILED(2002, "厨师审核失败"),

    // 订单相关
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_CANCELLED(3003, "订单已取消"),

    // 菜品相关
    DISH_NOT_FOUND(4001, "菜品不存在"),

    // 评价相关
    REVIEW_NOT_FOUND(5001, "评价不存在");

    private final Integer code;
    private final String message;
}
