package com.hs.backend.config;

import com.hs.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 从 Spring 容器中获取 JwtAuthenticationFilter（避免循环依赖）
        JwtAuthenticationFilter jwtAuthenticationFilter = http.getSharedObject(ApplicationContext.class).getBean(JwtAuthenticationFilter.class);
        
        http
            // 禁用 CSRF（前后端分离使用 Token）
            .csrf(csrf -> csrf.disable())
            
            // 配置 CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 添加 JWT 过滤器（在 UsernamePasswordAuthenticationFilter 之前）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问的接口
                .requestMatchers(
                    "/api/auth/**",      // 认证相关接口（登录、注册、刷新 Token）
                    "/error",            // 错误页面
                    "/favicon.ico",      // 网站图标
                        "/doc.html",    // swagger
                        "/webjars/**",  // swagger-ui 静态资源
                        "/swagger-resources/**",// swagger
                        "/v3/api-docs/**"

                ).permitAll()
                
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            
            // 禁用 Session（使用 JWT Token）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 禁用 HTTP Basic 认证
            .httpBasic(basic -> basic.disable())
            
            // 禁用表单登录
            .formLogin(form -> form.disable())
           
           // 🔥 配置异常处理器：未认证时返回 401 而不是 403
           .exceptionHandling(exception -> exception
               .authenticationEntryPoint((request, response, authException) -> {
                   // 未认证时返回 401
                  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                  response.setContentType("application/json;charset=UTF-8");
                  response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
               })
               // 权限不足时返回 403
               .accessDeniedHandler((request, response, accessDeniedException) -> {
                  response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                  response.setContentType("application/json;charset=UTF-8");
                  response.getWriter().write("{\"code\":403,\"message\":\"权限不足\"}");
               })
           );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许所有来源跨域（生产环境可根据需要限制为特定域名）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // 允许携带凭证（Cookie、Authorization 头等）
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
