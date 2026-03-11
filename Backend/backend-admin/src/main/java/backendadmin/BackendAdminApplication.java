package backendadmin;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@MapperScan("backendadmin.server.mapper")
@SpringBootApplication
public class BackendAdminApplication   implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(BackendAdminApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置Knife4j资源映射
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath*:/META-INF/resources/doc.html");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath*:/META-INF/resources/webjars/");
        // 添加Knife4j UI资源路径
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath*:/META-INF/resources/webjars/swagger-ui/");
    }
}