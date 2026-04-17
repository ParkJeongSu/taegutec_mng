package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.TransportOrderService;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.TransportOrderRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.TransportOrder;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler","tex"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertTransportOrderScheduler {

    private final TransportOrderService transportOrderService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 10000) // 5초마다 실행
    @SchedulerLock(name = "insertTransportOrderRequest",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void insertTransportOrderRequest() {
        // 1 단계 : TransportOrder 에서 Inbound,relocation order Created 상태 조회

        // 2단계 메시지 전송
        List<String> transportTypes = new ArrayList<>();
        transportTypes.add(TransportOrderType.INBOUND.getValue());
        transportTypes.add(TransportOrderType.RELOCATION.getValue());
        List<TransportOrder> transportOrders =
                transportOrderService.findByTransportTypeInAndTransportStatus(transportTypes,TransportOrderStatus.CREATED.getValue());

        if(CollectionUtils.isNotEmpty(transportOrders)){
            for(TransportOrder transportOrder : transportOrders){
                try {
                    // 메시지 전송
                    BaseMessage<TransportOrderRequestBody> request = new BaseMessage<>();
                    request.setMessageName(MessageList.TRANSPORT_ORDER_REQUEST.getMessageName());
                    TransportOrderRequestBody body =
                            TransportOrderRequestBody
                                    .builder()
                                    .id(transportOrder.getId())
                                    .build();

                    request.setBody(body);
                    // 1. 현재 시간 가져오기 (2026년 기준)
                    LocalDateTime now = LocalDateTime.now();
                    // 2. 18자리 포맷 정의 (연4, 월2, 일2, 시2, 분2, 초2, 소수점4)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS");
                    // 3. 포맷 적용 및 출력
                    String timestamp = now.format(formatter);
                    request.setTransactionId(timestamp);
                    String jsonPayload = objectMapper.writeValueAsString(request);
                    log.info("Sending JSON Payload: {}", jsonPayload);

                    // 5. String 으로 변환된 메시지 reply
                    rabbitTemplate.convertAndSend( RabbitConfig.EXCHANGE_TEX,RabbitConfig.ROUTING_TEX, request );
                    log.info("Send Completed");
                } catch (Exception e) {
                    log.error("transportOrder id error {}",transportOrder.getId());
                }

            }
        }
    }
}