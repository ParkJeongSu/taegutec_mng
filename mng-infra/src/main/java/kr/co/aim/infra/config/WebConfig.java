package kr.co.aim.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 복잡하게 나눌 필요 없이, static 폴더 전체를 정적 자원 루트로 단일 매핑합니다.
        // 이 설정이 들어가면 /wcs-web/assets/xxx.js 요청이 들어왔을 때
        // 내부적으로 static/assets/xxx.js 를 정확하게 찾아갑니다.
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // [추가] 사용자가 /wcs-web 또는 /wcs-web/ 로 진입했을 때 index.html을 열어줍니다.
        registry.addViewController("/")
                .setViewName("forward:/index.html");

        // 1단계 경로 대응 (예: /wcs-web/login, /wcs-web/dashboard)
        registry.addViewController("/{p1:[^\\.]*}")
                .setViewName("forward:/index.html");

        // 2단계 경로 대응 (예: /wcs-web/order/list)
        registry.addViewController("/{p1:[^\\.]*}/{p2:[^\\.]*}")
                .setViewName("forward:/index.html");

        // 3단계 경로 대응 (예: /wcs-web/order/detail/1)
        registry.addViewController("/{p1:[^\\.]*}/{p2:[^\\.]*}/{p3:[^\\.]*}")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();

        // 1페이지부터 시작하도록 설정 (0-indexed를 1-indexed로 변경)
        resolver.setOneIndexedParameters(true);

        // 페이지 사이즈 제한 등 추가 설정이 필요하다면 여기서 가능합니다.
        // resolver.setMaxPageSize(2000);

        resolvers.add(resolver);
    }

}