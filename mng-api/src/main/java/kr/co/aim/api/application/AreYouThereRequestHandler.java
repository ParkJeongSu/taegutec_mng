package kr.co.aim.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.format.request.BaseMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.AreYouThereBody;
import kr.co.aim.common.format.Header;
import kr.co.aim.common.format.LoadCompletedBody;
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
public class AreYouThereRequestHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public String getSupportedMessageName() {
        return MessageList.ARE_YOU_THERE_REQUEST.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        log.info("✅ Handling CreateUser request: {}", message);
        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<AreYouThereBody>> typeRef = new TypeReference<>() {};
        BaseMessage<AreYouThereBody> request = objectMapper.readValue(message, typeRef);

        // 2. 해당 비즈니스 로직 호출
        // AreYouThereRequest 는 단순히 로그
        log.info("equipment : {}", request.getBody().getEquipmentName());
        
        // 3. 메시지 송신 객체 생성
        BaseMessage<AreYouThereBody> reply = new BaseMessage<>();
        AreYouThereBody body = AreYouThereBody.
                builder()
                .equipmentName(request.getBody().getEquipmentName())
                .build();
        
        reply.setEventTime(request.getEventTime());
        reply.setMessageFrom("MNG");
        reply.setMessageName(MessageList.ARE_YOU_THERE_REPLY.getMessageName());
        reply.setMessageOwner("MNG");
        reply.setMessageTo(request.getMessageFrom());
        reply.setResultCode("0");
        reply.setResultMessage("0");
        reply.setTransactionId(request.getTransactionId());
        reply.setBody(body);

        // 3. DTO 객체를 JSON 문자열로 직접 변환합니다.
        String jsonPayload = objectMapper.writeValueAsString(reply);
        log.info("Sending JSON Payload: " + jsonPayload);

        // 4. Message Reply
//        rabbitTemplate.convertAndSend(
//                "dead.exchange",
//                "DEAD.request.queue",
//                reply
//        );
        
        // test
        BaseMessage<LoadCompletedBody> t = new BaseMessage<>();
        t.setMessageName(MessageList.LOAD_COMPLETE.getMessageName());
        t.setTransactionId("1234566");
        // [수정] 바디 객체를 직접 만들어서 넣어주세요
        LoadCompletedBody b =LoadCompletedBody.builder().equipmentName("aaa").build();
        t.setBody(b); // 세팅!
        // 4. Message Reply
        
        // 임시 테스트용 코드
        rabbitTemplate.setMandatory(true); // 갈 데 없으면 반송해라!
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("반송된 메시지! 이유: {}", returned.getReplyText());
            log.error("Exchange: {}, RoutingKey: {}", returned.getExchange(), returned.getRoutingKey());
        });
        
        rabbitTemplate.convertAndSend(
                "rpc.exchange",
                "PEX.key",
                t
        );
        return null;
    }
}