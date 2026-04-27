package kr.co.aim.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.enums.MessageList;
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
public class OPCallSendHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public String getSupportedMessageName() {
        return MessageList.OP_CALL_SEND.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {

        return null;
    }
}