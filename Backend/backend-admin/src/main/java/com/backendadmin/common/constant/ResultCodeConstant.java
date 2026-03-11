package com.admin.backendadmin.common.constant;

/**
 * 统一响应状态码常量
 */
public class ResultCodeConstant {

    // 成功
    public static final int SUCCESS = 200;

    // 系统错误
    public static final int ERROR = 500;

    // 参数错误
    public static final int PARAM_ERROR = 400;

    // 未登录 / Token 无效
    public static final int UNAUTHORIZED = 401;

    // 无权限访问
    public static final int FORBIDDEN = 403;

    // 资源不存在
    public static final int NOT_FOUND = 404;

    private ResultCodeConstant() {
        // 禁止实例化
    }
}

