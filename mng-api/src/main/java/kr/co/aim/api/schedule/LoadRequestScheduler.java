package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.common.format.CarrierDispatchRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.Port;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler","tex"})
public class LoadRequestScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final PortService portService;

    /*
     * 1) readyToLoad 인 PortList 조회
     *
     * 2) PEX로 LoadRequest 메시지 전송
     *
     * */
    @Scheduled(fixedDelay = 60000) // 60초마다 실행
    @SchedulerLock(name = "LoadRequest",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void LoadRequest() {

        List<Port> portList = portService.findByTransportState(PortTransportState.READY_TO_LOAD.getValue());
        if(CollectionUtils.isNotEmpty(portList)){
            for(Port port : portList){

                BaseMessage<CarrierDispatchRequestBody> request = new BaseMessage<>();
                CarrierDispatchRequestBody body = CarrierDispatchRequestBody.builder()
                        .equipmentName(port.getEquipmentName())
                        .portName(port.getPortName())
                        .build();
                request.setMessageName(MessageList.LOAD_REQUEST.getMessageName());
                request.setBody(body);

                String jsonPayload = "";
                try {
                    jsonPayload = objectMapper.writeValueAsString(request);
                } catch (Exception e) {
                    log.info("error : writeValueAsString");
                }
                log.info("Sending JSON Payload: {}", jsonPayload);
                if(StringUtils.isNotBlank(jsonPayload) ){
                    rabbitTemplate.convertAndSend(
                            RabbitConfig.EXCHANGE_PEX,
                            RabbitConfig.ROUTING_PEX,
                            request );
                }
            }
        }
    }
}