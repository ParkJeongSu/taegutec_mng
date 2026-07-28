package kr.co.aim.infra.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                // 1. Long 및 long 타입을 문자열로 직렬화 (기존 WebConfig 설정)
                jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
                jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);

                // 2. 빈 문자열("") 입력 시 null로 역직렬화 (추가 설정)
                jacksonObjectMapperBuilder.featuresToEnable(
                        DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
                );
            }
        };
    }
}