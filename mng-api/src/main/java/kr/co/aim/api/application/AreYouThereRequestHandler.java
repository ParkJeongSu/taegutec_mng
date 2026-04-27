package kr.co.aim.api.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.MessageExecuteService;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.AreYouThereReplyBody;
import kr.co.aim.common.format.AreYouThereRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.MessageHandler;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"pex","tex"})
public class AreYouThereRequestHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MessageExecuteService messageExecuteService;
    private final JsonUtils jsonUtils;

    @Override
    public String getSupportedMessageName() {
        return MessageList.ARE_YOU_THERE_REQUEST.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<AreYouThereRequestBody>> typeRef = new TypeReference<>() {};
        BaseMessage<AreYouThereRequestBody> request = objectMapper.readValue(message, typeRef);

        String fromSystemName = request.getMessageFrom();
        // 2. 해당 비즈니스 로직 호출
        // AreYouThereRequest 는 단순히 로그
        log.info("TransactionId : {}", request.getTransactionId());
        log.info("MessageFrom : {}", fromSystemName);

        // 3. 메시지 송신 객체 생성
        BaseMessage<AreYouThereReplyBody> reply = messageExecuteService.areYouThereRequest(request);

        // pretty Log
        jsonUtils.writePrettyJson(reply);

        // 4. Message Reply
        if(ObjectUtils.isNotEmpty(reply)) {
            if(StringUtils.equals(SystemName.WCS.getValue(),fromSystemName)){
                // 4. Message Reply
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_WCS,
                        RabbitConfig.ROUTING_WCS,
                        reply
                );
            }else if(StringUtils.isBlank(fromSystemName)){
                // 4. Message Reply
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_EAS,
                        RabbitConfig.ROUTING_EAS,
                        reply
                );
            }else if(StringUtils.equals(SystemName.EAS.getValue(),fromSystemName)){
                // 4. Message Reply
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_EAS,
                        RabbitConfig.ROUTING_EAS,
                        reply
                );
            }else{
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_WCS,
                        RabbitConfig.ROUTING_WCS,
                        reply
                );
            }
        }
        
        return null;
    }
}