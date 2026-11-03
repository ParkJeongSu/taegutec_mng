package kr.co.aim.api.service;

import kr.co.aim.api.strategy.SchedulerProcessStrategy;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.error.PreconditionNotMetException;
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

        } catch (PreconditionNotMetException pe) {
            // 110 선행 데이터 미존재로 인한 대기: 최대 10회까지 재시도 허용
            log.warn("[id={}] 선행 데이터 대기: {}", ifEventQueue.getId(), pe.getMessage());
            try {
                ifEventQueueService.increaseRetryCnt(ifEventQueue.getId(), 10);
            } catch (Exception ex) {
                log.error("대기 처리 중 에러 발생", ex);
            }

        } catch (Exception e) {
            // DB 장애, 파싱 에러 등 일반 시스템 에러: 3회 초과 시 FAIL 처리
            log.error("reportFail id {} ", ifEventQueue.getId(), e);
            try {
                ifEventQueueService.increaseRetryCnt(ifEventQueue.getId(), 3);
            } catch (Exception e1) {
                log.error("시스템 에러 후 retry 처리 실패 id {} ", ifEventQueue.getId(), e1);
            }
        }
    }
}
