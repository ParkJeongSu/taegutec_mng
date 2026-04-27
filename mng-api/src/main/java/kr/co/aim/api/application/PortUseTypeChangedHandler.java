package kr.co.aim.api.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.MessageExecuteService;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.PortUseTypeChangedBody;
import kr.co.aim.common.format.request.BaseMessage;
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
public class PortUseTypeChangedHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MessageExecuteService messageExecuteService;

    @Override
    public String getSupportedMessageName() {
        return MessageList.PORT_USE_TYPE_CHANGED.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        
        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<PortUseTypeChangedBody>> typeRef = new TypeReference<>() {};
        BaseMessage<PortUseTypeChangedBody> request = objectMapper.readValue(message, typeRef);

        // 2. 해당 비즈니스 로직 호출
        // 서비스 호출
        messageExecuteService.portUseTypeChanged(request);

        return null;
    }
}