package wit.backendcooker.Config;

/*
 * @author ：jee
 * @date ：2026/3/820:49
 * @version: 1.0
 --------------------------
 */


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig {

    // 方式 1.1：通过 CorsFilter 配置（更灵活，推荐）
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许的前端域名（* 表示允许所有，生产环境建议指定具体域名，如 http://localhost:5173）
        config.addAllowedOriginPattern("*"); // 替代 addAllowedOrigin，解决通配符+带凭证的问题

        // 2. 允许携带 Cookie/Token（前端请求需配 withCredentials: true）
        config.setAllowCredentials(true);

        // 3. 允许的请求方法（GET/POST/PUT/DELETE 等）
        config.addAllowedMethod("*");

        // 4. 允许的请求头（包括 Authorization 等自定义头）
        config.addAllowedHeader("*");

        // 5. 暴露的响应头（前端能获取的额外头）
        config.addExposedHeader("Authorization");

        // 6. 预检请求有效期（秒），避免频繁发送 OPTIONS 请求
        config.setMaxAge(3600L);

        // 配置生效的接口路径（/** 表示所有接口）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    // 方式 1.2：通过 WebMvcConfigurer 配置（简洁版）
    /*
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 所有接口
                        .allowedOriginPatterns("*") // 允许的域名
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                        .allowedHeaders("*") // 允许的头
                        .allowCredentials(true) // 允许携带凭证
                        .maxAge(3600); // 预检请求有效期
            }
        };
    }
    */
}