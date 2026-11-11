package kr.co.aim.api.schedule;

import kr.co.aim.api.service.IfEventQueueService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.EventQueueListReportBody;
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

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertReportScheduler {

    private final IfEventQueueService ifEventQueueService;
    private final RabbitTemplate rabbitTemplate;
    private final JsonUtils jsonUtils;

    @Scheduled(fixedRate = 1000)
    @SchedulerLock(name = "insertReportH2Trans",
            lockAtMostFor = "PT10S",
            lockAtLeastFor = "PT0.9S")
    public void insertReportH2Trans() {

        // 1단계 : EventQueue 조회 후 PROCESSING 상태로 변경
        List<IfEventQueue> ifEventQueues = ifEventQueueService.findByIfStatusOrderByCreateTimeAscAndToProcessing(IfEventQueueState.READY.getValue());

        if (CollectionUtils.isEmpty(ifEventQueues)) {
            return;
        }

        // 2단계 : groupId 유무에 따라 분기 처리
        // 순서 보존을 위해 LinkedHashMap 사용 (먼저 인입된 그룹이 먼저 전송되도록)
        Map<Long, List<IfEventQueue>> groupMap = new LinkedHashMap<>();

        for (IfEventQueue ifEventQueue : ifEventQueues) {
            Long groupId = ifEventQueue.getGroupId();

            if (groupId == null) {
                // groupId가 없는 단건 데이터는 즉시 전송
                sendMessageToScheduler(ifEventQueue);
                //sleepQuietly(50L);
            } else {
                // groupId가 있는 데이터는 그룹별 버킷에 수집
                List<IfEventQueue> groupList = groupMap.get(groupId);
                if (groupList == null) {
                    groupList = new ArrayList<>();
                    groupMap.put(groupId, groupList);
                }
                groupList.add(ifEventQueue);
            }
        }

        // 3단계 : 그룹별 데이터 전송
        for (Map.Entry<Long, List<IfEventQueue>> entry : groupMap.entrySet()) {
            List<IfEventQueue> groupList = entry.getValue();

            // 그룹 내부 순서(groupSeq) 보장을 위한 오름차순 정렬
            sortEventQueueListByGroupSeq(groupList);

            sendListMessageToScheduler(groupList);
            //sleepQuietly(50L);
        }
    }

    // --- [그룹 내부 정렬 메서드 (람다 미사용)] ---
    private void sortEventQueueListByGroupSeq(List<IfEventQueue> list) {
        Collections.sort(list, new Comparator<IfEventQueue>() {
            @Override
            public int compare(IfEventQueue o1, IfEventQueue o2) {
                Integer seq1 = o1.getGroupSeq() != null ? o1.getGroupSeq() : 1;
                Integer seq2 = o2.getGroupSeq() != null ? o2.getGroupSeq() : 1;
                return seq1.compareTo(seq2);
            }
        });
    }

    // --- [단건 메시지 발행] ---
    private void sendMessageToScheduler(IfEventQueue ifEventQueue) {
        String transactionId = FormatUtils.generateTransactionId();
        BaseMessage<EventQueueReportBody> request = new BaseMessage<>();

        request.setMessageName(MessageList.INSERT_EVENT_QUEUE_REPORT.getMessageName());
        request.setTransactionId(transactionId);
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MNG.getValue());
        request.setEventTime(transactionId);
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());

        EventQueueReportBody body = convertToReportBody(ifEventQueue);
        request.setBody(body);

        jsonUtils.writePrettyJson(request);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_SCHEDULER,
                RabbitConfig.ROUTING_SCHEDULER,
                request
        );
    }

    // --- [리스트 묶음 메시지 발행] ---
    private void sendListMessageToScheduler(List<IfEventQueue> ifEventQueueList) {
        if (CollectionUtils.isEmpty(ifEventQueueList)) {
            return;
        }

        String transactionId = FormatUtils.generateTransactionId();
        BaseMessage<EventQueueListReportBody> request = new BaseMessage<>();

        request.setMessageName(MessageList.INSERT_EVENT_QUEUE_LIST_REPORT.getMessageName());
        request.setTransactionId(transactionId);
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MNG.getValue());
        request.setEventTime(transactionId);
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());

        List<EventQueueReportBody> bodyList = new ArrayList<>();
        for (IfEventQueue ifEventQueue : ifEventQueueList) {
            bodyList.add(convertToReportBody(ifEventQueue));
        }

        EventQueueListReportBody listBody = EventQueueListReportBody.builder()
                .list(bodyList)
                .build();

        request.setBody(listBody);

        jsonUtils.writePrettyJson(request);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_SCHEDULER,
                RabbitConfig.ROUTING_SCHEDULER,
                request
        );
    }

    // --- [DTO 변환 헬퍼 메서드] ---
    private EventQueueReportBody convertToReportBody(IfEventQueue ifEventQueue) {
        return EventQueueReportBody.builder()
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
                .groupId(ifEventQueue.getGroupId())
                .groupSeq(ifEventQueue.getGroupSeq())
                .build();
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread sleep interrupted: {}", e.getMessage());
        }
    }
}