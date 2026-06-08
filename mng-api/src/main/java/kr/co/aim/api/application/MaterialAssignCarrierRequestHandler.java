package kr.co.aim.api.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.MessageExecuteService;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.MaterialAssignedCarrierReplyByWMSBody;
import kr.co.aim.common.format.MaterialAssignedCarrierRequestByWMSBody;
import kr.co.aim.common.format.ZoneRequestBody;
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
@Profile({"pex","tex"})
public class MaterialAssignCarrierRequestHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MessageExecuteService messageExecuteService;
    private final JsonUtils jsonUtils;

    @Override
    public String getSupportedMessageName() {
        return MessageList.MATERIAL_ASSIGN_CARRIER_REQUEST.getMessageName();
    }

    @Override
    @SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {

        // 1. 자신에게 맞는 DTO로 역직렬화
        TypeReference<BaseMessage<MaterialAssignedCarrierRequestByWMSBody>> typeRef = new TypeReference<>() {};
        BaseMessage<MaterialAssignedCarrierRequestByWMSBody> request = objectMapper.readValue(message, typeRef);
        jsonUtils.writePrettyJson(request);


        BaseMessage<MaterialAssignedCarrierReplyByWMSBody> reply = new BaseMessage<>();
        MaterialAssignedCarrierReplyByWMSBody body = new MaterialAssignedCarrierReplyByWMSBody();

        reply.setEventTime(request.getEventTime());
        reply.setMessageFrom("MNG");
        reply.setMessageName(MessageList.MATERIAL_ASSIGN_CARRIER_REPLY.getMessageName());
        reply.setMessageOwner("MNG");
        reply.setMessageTo(request.getMessageFrom());
        reply.setResultCode("0");
        reply.setResultMessage("0");
        reply.setTransactionId(request.getTransactionId());
        reply.setBody(body);
        jsonUtils.writePrettyJson(reply);

        return reply;
    }
}