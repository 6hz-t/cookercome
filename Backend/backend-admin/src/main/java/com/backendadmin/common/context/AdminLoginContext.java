package com.admin.backendadmin.common.context;


import com.admin.backendadmin.common.constant.RoleConstant;
import lombok.Data;

/**
 * 管理员登录上下文
 * 使用 ThreadLocal 存储当前线程的登录信息，保证线程安全
 */
public class AdminLoginContext {

    // 线程本地存储，每个线程独立持有自己的上下文
    private static final ThreadLocal<AdminInfo> ADMIN_INFO_HOLDER = new ThreadLocal<>();

    // ========== 内部静态类：存储管理员信息 ==========
    @Data
    public static class AdminInfo {
        private Long adminId;    // 管理员ID（t_user.id）
        private String username; // 管理员用户名
        private Integer role;    // 角色（固定为 ROLE_ADMIN = 2）
    }

    // ========== 上下文操作方法 ==========

    /**
     * 设置当前登录管理员信息（登录拦截器中调用）
     */
    public static void setAdminInfo(Long adminId, String username) {
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setAdminId(adminId);
        adminInfo.setUsername(username);
        adminInfo.setRole(RoleConstant.ROLE_ADMIN); // 管理端固定为管理员角色
        ADMIN_INFO_HOLDER.set(adminInfo);
    }

    /**
     * 获取当前登录管理员信息
     */
    public static AdminInfo getAdminInfo() {
        return ADMIN_INFO_HOLDER.get();
    }

    /**
     * 获取当前登录管理员ID（常用）
     */
    public static Long getAdminId() {
        AdminInfo adminInfo = getAdminInfo();
        return adminInfo != null ? adminInfo.getAdminId() : null;
    }

    /**
     * 获取当前登录管理员用户名
     */
    public static String getUsername() {
        AdminInfo adminInfo = getAdminInfo();
        return adminInfo != null ? adminInfo.getUsername() : null;
    }

    /**
     * 清除当前线程的上下文信息（请求结束时调用，避免内存泄漏）
     */
    public static void clear() {
        ADMIN_INFO_HOLDER.remove();
    }
}
