package com.admin.backendadmin.common.interceptor;

import com.admin.backendadmin.common.constant.RoleConstant;
import com.admin.backendadmin.common.context.AdminLoginContext;
import com.admin.backendadmin.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员权限拦截器：校验登录用户是否为管理员角色
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从上下文获取登录信息
        AdminLoginContext.AdminInfo adminInfo = AdminLoginContext.getAdminInfo();
        if (adminInfo == null) {
            throw BusinessException.unAuthorized("未登录");
        }

        // 2. 校验角色是否为管理员（role=2）
        if (!RoleConstant.ROLE_ADMIN.equals(adminInfo.getRole())) {
            throw BusinessException.forbidden("无权限访问管理端");
        }

        return true; // 权限校验通过，放行
    }
}