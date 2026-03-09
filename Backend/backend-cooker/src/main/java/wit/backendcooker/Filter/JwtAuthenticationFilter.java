package wit.backendcooker.Filter;

/*
 * @author ：jee
 * @date ：2026/3/911:13
 * @version: 1.0
 --------------------------
 */


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import wit.backendcooker.Utils.JwtTokenUtil;

import java.io.IOException;
import java.util.ArrayList;


/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter，确保每个请求只执行一次过滤
 * 负责在请求到达业务逻辑前验证 JWT Token 的有效性
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT 工具类，用于生成和验证 Token
     * 使用 final 确保依赖不可变
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 构造函数注入 JwtTokenUtil
     * Spring 会自动管理依赖关系
     */
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 核心过滤方法，处理每个 HTTP 请求
     * 
     * 执行流程：
     * 1. 从请求头中提取 JWT Token
     * 2. 验证 Token 是否有效（签名 + 有效期）
     * 3. 如果有效，解析用户名并创建 Spring Security 认证对象
     * 4. 将认证信息存入全局安全上下文
     * 5. 继续执行后续过滤器链
     *
     * @param request HTTP 请求对象，用于获取请求头和参数
     * @param response HTTP 响应对象
     * @param filterChain 过滤器链，用于将请求传递给下一个过滤器或 Controller
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 步骤 1: 从 Authorization 请求头中获取 Token
            // 格式应为：Bearer <token>
            String token = getTokenFromRequest(request);
            System.out.println("token:"+token);

            if (StringUtils.hasText(token) && jwtTokenUtil.validateToken(token)) {
                // 步骤 3: Token 有效，从中解析用户名（JWT 的 subject 字段）
                String username = jwtTokenUtil.getUsernameFromToken(token);
                System.out.println("username:"+username);


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());


                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            
            SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(request, response);
    }

    /**
     * 从 HTTP 请求头中提取 JWT Token
     * 遵循 OAuth 2.0 Bearer Token 标准格式
     *
     * @param request HTTP 请求对象
     * @return 提取的 Token 字符串，如果不存在则返回 null
     *
     * 请求头格式示例：
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从 Authorization 请求头获取 Bearer Token
        String bearerToken = request.getHeader("Authorization");

        // 标准的 JWT Token 格式：Bearer <token>
        // StringUtils.hasText() 检查字符串是否非空且包含实际内容
        // startsWith("Bearer ") 确保格式正确（注意 Bearer 后面有空格）
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 截取 "Bearer " 后面的部分（从索引 7 开始）
            // substring(7) 去掉前缀 "Bearer "（7 个字符）
            return bearerToken.substring(7);
        }

        // 如果请求头不存在或格式不正确，返回 null
        return null;
    }
}