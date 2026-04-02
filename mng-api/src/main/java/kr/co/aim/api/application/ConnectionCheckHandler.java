package kr.co.aim.api.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.AreYouThereReplyBody;
import kr.co.aim.common.format.AreYouThereRequestBody;
import kr.co.aim.common.format.ConnectionBody;
import kr.co.aim.common.format.ConnectionCheckBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.MessageHandler;
import kr.co.aim.infra.config.RabbitConfig;
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
public class ConnectionCheckHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public String getSupportedMessageName() {
        return MessageList.CONNECTION_CHECK.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        log.info("message: {}", message);
        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<ConnectionCheckBody>> typeRef = new TypeReference<>() {};
        BaseMessage<ConnectionCheckBody> request = objectMapper.readValue(message, typeRef);

        // 2. 해당 비즈니스 로직 호출
        // ConnectionCheckHandler 는 단순히 로그
        log.info("transactionId : {}", request.getTransactionId());
        
        // 3. 메시지 송신 객체 생성
        BaseMessage<ConnectionBody> reply = new BaseMessage<>();
        ConnectionBody body = new  ConnectionBody();
        
        reply.setEventTime(request.getEventTime());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageName(MessageList.CONNECTION.getMessageName());
        reply.setMessageOwner(request.getMessageOwner());
        reply.setMessageTo(request.getMessageFrom());
        reply.setResultCode(ResultCode.OK.getValue());
        reply.setResultMessage("");
        reply.setTransactionId(request.getTransactionId());
        reply.setBody(body);

        // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
        String jsonPayload = objectMapper.writeValueAsString(reply);
        log.info("Sending JSON Payload : {}",jsonPayload);

        // 4. Message Reply
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_DEAD,
                RabbitConfig.ROUTING_DEAD,
                reply
        );

        return null;
    }
}