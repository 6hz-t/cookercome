package com.admin.backendadmin.common.interceptor;

import com.admin.backendadmin.common.context.AdminLoginContext;
import com.admin.backendadmin.common.exception.BusinessException;
import com.admin.backendadmin.common.utils.JwtUtil;
import com.admin.backendadmin.common.utils.StringUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器：校验Token有效性，设置登录上下文
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取Token（前端需传：Authorization: Bearer xxx）
        String token = request.getHeader("Authorization");
        if (StringUtil.isEmpty(token) || !token.startsWith("Bearer ")) {
            throw BusinessException.unAuthorized("请先登录");
        }
        token = token.substring(7); // 去掉"Bearer "前缀

        // 2. 解析Token并校验
        try {
            // 校验Token是否过期
            if (jwtUtil.isExpired(token)) {
                throw BusinessException.unAuthorized("登录已过期，请重新登录");
            }
            // 解析Token载荷
            Claims claims = jwtUtil.parseToken(token);
            Long adminId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            // 3. 设置登录上下文（供后续业务使用）
            AdminLoginContext.setAdminInfo(adminId, username);
            return true; // 放行请求
        } catch (Exception e) {
            // Token解析失败（无效/篡改）
            throw BusinessException.unAuthorized("Token无效，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束，清除上下文（避免ThreadLocal内存泄漏）
        AdminLoginContext.clear();
    }
}