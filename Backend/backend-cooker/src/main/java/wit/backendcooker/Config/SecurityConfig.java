package wit.backendcooker.Config;

/*
 * @author ：jee
 * @date ：2026/3/813:13
 * @version: 1.0
 --------------------------
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 禁用表单登录
                .formLogin(form -> form.disable())
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // /api/** 公开 doc swagger-ui.html
                        .requestMatchers("/api/**").permitAll()
                        // Swagger / SpringDoc 相关路径全部公开
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs/swagger-config",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/doc.html"
                        ).permitAll()

                        .requestMatchers("/*.ico").permitAll()
                        // 其他请求需要认证
                       .anyRequest().authenticated()
                )

                // 禁用 Session（无状态）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 禁用 HTTP Basic 认证
                .httpBasic(basic -> basic.disable());

        return http.build();
    }


















}
