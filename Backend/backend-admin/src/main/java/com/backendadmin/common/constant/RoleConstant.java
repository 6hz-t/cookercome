package com.admin.backendadmin.common.constant;

/**
 * 用户角色常量（与 t_user.role 字段对应）
 */
public class RoleConstant {

    // 客户
    public static final int ROLE_CUSTOMER = 0;

    // 厨师
    public static final int ROLE_CHEF = 1;

    // 管理员（管理端登录角色）
    public static final int ROLE_ADMIN = 2;

    private RoleConstant() {
        // 禁止实例化
    }
}