package kr.co.aim.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.ResponseMessage;
import kr.co.aim.common.annotation.ResponseAnnotation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(annotations = ResponseAnnotation.class)
@RequiredArgsConstructor
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 이미 ResponseMessage 타입이면 중복으로 감싸지 않음
        return !returnType.getParameterType().equals(ResponseMessage.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        ResponseMessage<?> responseMessage = new ResponseMessage<>("SUCCESS", "정상 처리되었습니다.", body);

        // StringHttpMessageConverter가 사용될 때 ClassCastException 방지
        if (selectedConverterType.equals(StringHttpMessageConverter.class) || body instanceof String) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(responseMessage);
            } catch (JsonProcessingException e) {
                log.error("JSON serialization failed in GlobalResponseHandler", e);
                return responseMessage;
            }
        }

        // 컨트롤러에서 반환한 body를 data 필드에 넣어서 반환
        return responseMessage;
    }
}