package com.hs.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        logger.info("========== [JWT Filter] 开始处理请求 ========== ");
        logger.info("[1] 请求 URL: {}", request.getRequestURL());
        logger.info("[2] 请求 URI: {}", request.getRequestURI());
        logger.info("[3] Authorization Header: {}", request.getHeader("Authorization"));
        
        try {
            // 从请求中获取 JWT token
            String jwt = getJwtFromRequest(request);
            logger.info("[4] 提取的 JWT Token: {}", jwt != null ? jwt.substring(0, Math.min(50, jwt.length())) + "..." : "null");

            // 验证 token 并设置认证
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                logger.info("[5] ✅ Token 有效，解析出 userId: {}", userId);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
                logger.info("[6] 加载 UserDetails: {}", userDetails.getUsername());
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("[7] ✅ SecurityContext 已设置认证信息");
                logger.info("[8] Authentication 对象：{}", authentication);
                logger.info("[9] Authentication.getName(): {}", authentication.getName());
            } else if (StringUtils.hasText(jwt)) {
                // ✅ Token 存在但无效（被篡改或过期），返回 401
                logger.warn("[❌] Token 存在但无效（被篡改或过期）");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期\"}");
                return;
            } else {
                logger.info("[ℹ️] 请求中没有 Token");
            }
            // 如果没有 token，继续放行（让 Spring Security 处理）
        } catch (Exception ex) {
            logger.error("[❌] Could not set user authentication in security context", ex);
            // Token 验证失败，返回 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token 验证失败\"}");
            return;
        }
        
        logger.info("[10] Filter 处理完成，继续过滤器链");
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取 JWT token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
