package kr.co.aim.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.format.response.ReplySample;
import kr.co.aim.common.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateUserHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    // private final UserService userService; // 실제 사용할 서비스 주입

    @Override
    public String getSupportedMessageName() {
        return "CreateUser";
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        log.info("✅ Handling CreateUser request: {}", message);
        // 1. 자신에게 맞는 DTO로 역직렬화
        //CreateUserRequest request = objectMapper.readValue(messagePayload, CreateUserRequest.class);

        // 2. 해당 비즈니스 로직 호출
        // userService.createUser(request);
        ReplySample t = ReplySample.builder().messageName("123").messageContent("1234").build();
        return t;
    }
}