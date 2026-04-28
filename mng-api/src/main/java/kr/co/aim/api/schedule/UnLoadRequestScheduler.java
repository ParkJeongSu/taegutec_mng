package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.LoadRequestBody;
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
@Profile({"scheduler"})
public class UnLoadRequestScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final PortService portService;
    private final JsonUtils jsonUtils;

    /*
     * 1) readyToLoad 인 PortList 조회
     *
     * 2) PEX로 LoadRequest 메시지 전송
     *
     * */
    @Scheduled(fixedDelay = 60000) // 60초마다 실행
    @SchedulerLock(name = "unLoadRequest",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void unLoadRequest() {

        List<Port> portList = portService.findByTransportState(PortTransportState.READY_TO_UNLOAD.getValue());
        if(CollectionUtils.isNotEmpty(portList)){
            for(Port port : portList){
                String transactionId = FormatUtils.generateTransactionId();
                BaseMessage<LoadRequestBody> request = new BaseMessage<>();
                request.setTransactionId(transactionId);
                request.setMessageFrom(SystemName.MNG.getValue());
                request.setMessageOwner(SystemName.MNG.getValue());
                request.setMessageTo(SystemName.MNG.getValue());
                request.setEventTime(transactionId);
                request.setResultMessage("");
                request.setResultCode(ResultCode.OK.getValue());
                request.setMessageName(MessageList.UNLOAD_REQUEST.getMessageName());
                LoadRequestBody body = LoadRequestBody.builder()
                        .equipmentName(port.getEquipmentName())
                        .portName(port.getPortName())
                        .build();
                request.setBody(body);

                jsonUtils.writePrettyJson(request);
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_PEX,
                        RabbitConfig.ROUTING_PEX,
                        request );
            }
        }
    }
}