package kr.co.aim.api.config;

import kr.co.aim.api.dto.ResponseMessage;
import kr.co.aim.common.annotation.ResponseAnnotation;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(annotations = ResponseAnnotation.class)
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 이미 ResponseMessage 타입이면 중복으로 감싸지 않음
        return !returnType.getParameterType().equals(ResponseMessage.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 컨트롤러에서 반환한 body를 data 필드에 넣어서 반환
        return new ResponseMessage<>("SUCCESS", "정상 처리되었습니다.", body);
    }
}