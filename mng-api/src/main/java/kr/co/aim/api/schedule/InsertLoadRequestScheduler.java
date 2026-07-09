package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.LoadRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.Port;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertLoadRequestScheduler {

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
    @Scheduled(fixedDelay = 5000) // 60초마다 실행
    @SchedulerLock(name = "insertLoadRequestScheduler",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void insertLoadRequestScheduler() {

        List<Port> portList = portService.findByTransportState(PortTransportState.READY_TO_LOAD.getValue());
        //TODO: 워크센터별로 하나씩 READY_TO_LOAD 인 PORT 조회해서 보내기 아래의 detail port Type도 확인해보기
        //List<Port> portList = portService.findEarliestPortPerWorkCenter(PortTransportState.READY_TO_LOAD.getValue());

        //List<String> detailPortTypes = new ArrayList<>();
        //detailPortTypes.add(DetailPortType.CRANE_OUT_PND.getValue());
        //detailPortTypes.add(DetailPortType.CRANE_BOTH_PND.getValue());
        //List<Port> portList = portService.findByTransportStateAndDetailPortTypeIn(PortTransportState.READY_TO_LOAD.getValue(),detailPortTypes);
        if(CollectionUtils.isNotEmpty(portList)){
            for(Port port : portList){
                String transactionId = FormatUtils.generateTransactionId();
                BaseMessage<LoadRequestBody> request = new BaseMessage<>();

                request.setMessageName(MessageList.LOAD_REQUEST.getMessageName());
                request.setTransactionId(transactionId);
                request.setMessageFrom(SystemName.MNG.getValue());
                request.setMessageOwner(SystemName.MNG.getValue());
                request.setMessageTo(SystemName.MNG.getValue());
                request.setEventTime(transactionId);
                request.setResultMessage("");
                request.setResultCode(ResultCode.OK.getValue());
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