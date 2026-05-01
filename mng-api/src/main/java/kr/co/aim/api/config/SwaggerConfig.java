package kr.co.aim.api.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info();
        info.setTitle("MNG 명세서");
        info.setDescription("Spring Boot를 이용한 API 서비스입니다.");
        info.setVersion("v1.0.0");

        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);

        return openAPI;
    }
}
