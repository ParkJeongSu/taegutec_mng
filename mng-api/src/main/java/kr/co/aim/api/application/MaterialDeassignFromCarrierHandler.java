package kr.co.aim.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.CarrierService;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.MaterialDeassignedFromCarrierBody;
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
public class MaterialDeassignFromCarrierHandler implements MessageHandler<String> {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final CarrierService carrierService;

    @Override
    public String getSupportedMessageName() {
        return MessageList.MATERIAL_DEASSIGNED_FROM_CARRIER.getMessageName();
    }

    @Override
    //@SneakyThrows // objectMapper의 예외 처리를 간소화
    public Object handle(String message) {
        log.info("✅ Handling Message request: {}", message);
        try {
            // 1. 자신에게 맞는 DTO로 역직렬화
            TypeReference<BaseMessage<MaterialDeassignedFromCarrierBody>> typeRef = new TypeReference<>() {};
            BaseMessage<MaterialDeassignedFromCarrierBody> requestMessage = objectMapper.readValue(message, typeRef);

            // 2. 해당 비즈니스 로직 호출
            // 서비스 호출
            carrierService.materialDeassignedFromCarrier(requestMessage);
        }
        catch (JsonProcessingException ex) {
            log.error("❌ JSON 처리 중 오류 발생: {}", ex.getMessage());
            throw new RuntimeException("메시지 형식이 올바르지 않습니다.",ex);
        }
        catch (Exception e){
            log.error("❌ 시스템 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("서비스 처리 중 예외가 발생했습니다.",e);
        }
        return null;
    }
}