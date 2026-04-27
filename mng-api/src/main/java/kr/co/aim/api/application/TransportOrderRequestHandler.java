package kr.co.aim.api.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.aim.api.service.MessageExecuteService;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.TransportJobRequestListBody;
import kr.co.aim.common.format.TransportOrderRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.MessageHandler;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"tex"})
public class TransportOrderRequestHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MessageExecuteService messageExecuteService;
    private final JsonUtils jsonUtils;

    @Override
    public String getSupportedMessageName() {
        return MessageList.TRANSPORT_ORDER_REQUEST.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        
        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<TransportOrderRequestBody>> typeRef = new TypeReference<>() {};
        BaseMessage<TransportOrderRequestBody> requestMessage = objectMapper.readValue(message, typeRef);

        // 2. 해당 비즈니스 로직 호출 & reply 메시지 생성
        // 서비스 호출
        BaseMessage<TransportJobRequestBody> transportJobRequestBodyBaseMessage = messageExecuteService.transportOrderRequest(requestMessage);

        jsonUtils.writePrettyJson(transportJobRequestBodyBaseMessage);

        if(ObjectUtils.isEmpty(transportJobRequestBodyBaseMessage)){
            log.info("transportJobRequestBodyBaseMessage is null");
        }
        else if(ObjectUtils.isEmpty(transportJobRequestBodyBaseMessage)){
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_WCS,
                    RabbitConfig.ROUTING_WCS,
                    transportJobRequestBodyBaseMessage );
        }

        return null;
    }
}