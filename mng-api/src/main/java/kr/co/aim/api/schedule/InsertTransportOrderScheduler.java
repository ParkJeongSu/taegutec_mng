package kr.co.aim.api.schedule;

import kr.co.aim.api.service.TransportOrderDispatcherService;
import kr.co.aim.common.enums.TransportOrderStatus;
import kr.co.aim.common.enums.TransportOrderType;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertTransportOrderScheduler {

    private final TransportOrderRepository transportOrderRepository;
    private final TransportOrderDispatcherService transportOrderDispatcherService;

    private static final int MAX_RETRY_COUNT = 3;
    private static final int BATCH_SIZE = 50;

    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(
            name = "insertTransportOrderRequest",
            lockAtMostFor = "PT1M",
            lockAtLeastFor = "PT0.9S"
    )
    public void insertTransportOrderRequest() {
        List<String> transportTypes = List.of(
                TransportOrderType.INBOUND.getValue(),
                TransportOrderType.OUTBOUND.getValue(),
                TransportOrderType.RELOCATION.getValue()
        );

        // 1. PK 오름차순(id ASC)으로 정렬하여 최대 50건 조회
        List<TransportOrder> transportOrders = transportOrderRepository.findPendingOrders(
                transportTypes,
                TransportOrderStatus.CREATED.getValue(),
                MAX_RETRY_COUNT,
                PageRequest.of(0, BATCH_SIZE)
        );

        if (CollectionUtils.isEmpty(transportOrders)) {
            return;
        }

        // 2. 단일 루프 순차 처리
        for (TransportOrder transportOrder : transportOrders) {
            try {
                transportOrderDispatcherService.processAndSendOrder(transportOrder.getId());
                log.info("TransportOrder WCS 발송 성공 : OrderId={}", transportOrder.getTransportOrderId());

            } catch (Exception e) {
                // 실패 건 카운트 증가 처리 (별도 트랜잭션)
                transportOrderDispatcherService.handleFailure(transportOrder.getId(), MAX_RETRY_COUNT, e);

                // 순서 보장을 위해 후속 작업 즉시 중단 (다음 주기에서 재시도)
                log.error("❌ [발송 중단] OrderId={} 오류로 후속 발송을 중단합니다.", transportOrder.getTransportOrderId());
                break;
            }
        }
    }
}