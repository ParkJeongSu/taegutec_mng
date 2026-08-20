package kr.co.aim.api.service;

import kr.co.aim.api.strategy.SchedulerProcessStrategy;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
@Profile({"scheduler"})
public class InsertSchedulerProcessService implements SchedulerProcessStrategy {

    private final IfEventQueueService ifEventQueueService;
    private final InsertExternalInterfaceService insertExternalInterfaceService;

    @Override
    public void eventQueueReport(BaseMessage<EventQueueReportBody> message) {

        EventQueueReportBody body = message.getBody();

        IfEventQueue ifEventQueue =
                IfEventQueue
                        .builder()
                        .id(body.getId())
                        .eventType(body.getEventType())
                        .payload(body.getPayload())
                        .ifStatus(body.getIfStatus())
                        .carrierName(body.getCarrierName())
                        .idocId(body.getIdocId())
                        .orderId(body.getOrderId())
                        .orderLineNumber(body.getOrderLineNumber())
                        .retryCNT(body.getRetryCNT())
                        .errMSG(body.getErrMSG())
                        .createTime(body.getCreateTime())
                        .updateTime(body.getUpdateTime())
                        .build();

        try {
            // DB2 H2transReport
            insertExternalInterfaceService.reportH2trans(ifEventQueue);
            // ifEventQueue 상태를 Success 로 변경
            ifEventQueueService.reportCompleted(ifEventQueue.getId());

        } catch (Exception e) {
            // retry cnt ++
            // 만일 3초과면, ready -> fail 로 데이터 변경
            log.error("reportFail id {} ",ifEventQueue.getId());
            try {
                Optional<IfEventQueue> optionalIfEventQueue
                        = ifEventQueueService.increaseRetryCnt(ifEventQueue.getId());
                if(optionalIfEventQueue.isPresent()){
                    if(optionalIfEventQueue.get().getRetryCNT() > 3){
                        ifEventQueueService.reportFailed(ifEventQueue.getId());
                    }
                }
            } catch (Exception e1){
                log.error("final report error", e1);
                log.error("increase & reportFail id {} ",ifEventQueue.getId());
            }
        }
    }

}
