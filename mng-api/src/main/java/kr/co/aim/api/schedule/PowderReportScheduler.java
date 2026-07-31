package kr.co.aim.api.schedule;

import kr.co.aim.api.service.IfEventQueueService;
import kr.co.aim.api.service.PowderExternalInterfaceService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.IfEventQueueState;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.EventQueueReportBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.IfEventQueue;
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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderReportScheduler {

    private final PowderExternalInterfaceService powderExternalInterfaceService;
    private final IfEventQueueService ifEventQueueService;

    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "powderReportH2Trans",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void powderReportH2Trans() {

        // 1단계 : EventLog 조회 MSSQL 트랜잭션 Ready 상태 조회후 PROCESSING 상태로 변경

        // 2단계 : try catch for문
        // tex로 rabbitMQ 메시지 전송

        // 1단계 EventQueue 조회
        // 조회 후 바로 Processing 상태로 변경
        List<IfEventQueue> ifEventQueues = ifEventQueueService.findByIfStatusOrderByCreateTimeAscAndToProcessing(IfEventQueueState.READY.getValue());

        if(CollectionUtils.isNotEmpty(ifEventQueues)){
            for(IfEventQueue ifEventQueue : ifEventQueues){
                sendMessageToPEX(ifEventQueue);
            }
        }
    }

    private void sendMessageToPEX(IfEventQueue ifEventQueue){
        String transactionId = FormatUtils.generateTransactionId();
        BaseMessage<EventQueueReportBody> request = new BaseMessage<>();

        request.setMessageName(MessageList.POWDER_EVENT_QUEUE_REPORT.getMessageName());
        request.setTransactionId(transactionId);
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MNG.getValue());
        request.setEventTime(transactionId);
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());

        EventQueueReportBody body =
                EventQueueReportBody
                        .builder()
                        .id(ifEventQueue.getId())
                        .eventType(ifEventQueue.getEventType())
                        .payload(ifEventQueue.getPayload())
                        .ifStatus(ifEventQueue.getIfStatus())
                        .carrierName(ifEventQueue.getCarrierName())
                        .idocId(ifEventQueue.getIdocId())
                        .orderId(ifEventQueue.getOrderId())
                        .orderLineNumber(ifEventQueue.getOrderLineNumber())
                        .retryCNT(ifEventQueue.getRetryCNT())
                        .errMSG(ifEventQueue.getErrMSG())
                        .createTime(ifEventQueue.getCreateTime())
                        .updateTime(ifEventQueue.getUpdateTime())
                        .build();
        request.setBody(body);

        jsonUtils.writePrettyJson(request);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_TEX,
                RabbitConfig.ROUTING_TEX,
                request );
    }

    /*
    * try {
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
    *
    * */


}