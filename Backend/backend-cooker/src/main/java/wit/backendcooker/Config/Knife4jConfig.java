package wit.backendcooker.Config;

/*
 * @author ：jee
 * @date ：2026/3/813:19
 * @version: 1.0
 --------------------------
 */


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    /**
     * 配置 OpenAPI 文档基础信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API 文档标题
                .info(new Info()
                        .title("CookerCome厨师端API文档")
                        // API 文档版本
                        .version("1.0.0")
                        // API 文档描述
                        .description("这是基于 Knife4j 构建的 RESTful API 文档")
                        // 联系人信息
                        .contact(new Contact().name("开发团队").email("dev@example.com")));
    }
}