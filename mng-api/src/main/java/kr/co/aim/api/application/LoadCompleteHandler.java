package kr.co.aim.api.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.LoadCompletedBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"pex","tex","scheduler"})
public class LoadCompleteHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final PortService portService;
    private final FactoryProcessStrategy factoryProcessStrategy;

    @Override
    public String getSupportedMessageName() {
        return MessageList.LOAD_COMPLETE.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        log.info("✅ Handling Message request: {}", message);
        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<LoadCompletedBody>> typeRef = new TypeReference<>() {};
        BaseMessage<LoadCompletedBody> request = objectMapper.readValue(message, typeRef);

        // 2. 해당 비즈니스 로직 호출
        // 서비스 호출
        factoryProcessStrategy.loadCompleted(request);
        // 3. 만일 서비스 호출 후 메시지 송신해야하면 이 부분에서 reply 메시지 생성
        // reply 객체 정의

        // 4. DTO 객체를 JSON 문자열로 직접 변환합니다.
        //String jsonPayload = objectMapper.writeValueAsString(reply);
        //log.info("Sending JSON Payload: {}", jsonPayload);

        // 5. String 으로 변환된 메시지 reply
        //rabbitTemplate.convertAndSend( "demo-queue", jsonPayload );
        return null;
    }
}