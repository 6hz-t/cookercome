package com.hs.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有接口跨域
        registry.addMapping("/**")
                // 允许前端域名跨域（*代表所有，开发环境可用）
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带Cookie/Token
                .allowCredentials(true)
                // 预检请求有效期（秒）
                .maxAge(3600);
    }

}
