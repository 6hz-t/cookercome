package com.admin.backendadmin.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j接口文档配置（Swagger增强版）
 */
@Configuration
@EnableKnife4j // 开启Knife4j增强功能
public class Knife4jConfig {

    /**
     * 配置OpenAPI文档信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("厨师上门服务-管理端API文档")
                        .version("v1.0")
                        .description("管理端核心业务接口（登录、厨师审核、订单管理等）")
                        .contact(new Contact().name("hs").email("3190284709@qq.com")));
    }
}
