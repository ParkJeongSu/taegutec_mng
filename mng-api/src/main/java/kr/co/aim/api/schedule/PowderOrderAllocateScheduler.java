package kr.co.aim.api.schedule;

import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.OrderAllocateRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.ProductionOrder;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderOrderAllocateScheduler {

    private final ProductionOrderService productionOrderService;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    @SchedulerLock(
            name = "powderOrderAllocateRequest",
            lockAtMostFor = "PT2M",
            lockAtLeastFor = "PT5S"
    )
    @Transactional
    public void powderOrderAllocateRequest() {
        // 1. CREATED 상태의 ProductionOrder 조회
        List<String> productionOrderState = new ArrayList<>();
        productionOrderState.add(ProductionOrderState.CREATED.getValue());
        List<ProductionOrder> createdOrders = productionOrderService.findByProductionOrderStateInOrderByCreateTimeAsc(
                productionOrderState
        );

        if (CollectionUtils.isEmpty(createdOrders)) {
            return;
        }

        TransactionInfo tx = TransactionInfo.now(EventName.ALLOCATE_REQUEST.getValue(), SystemName.MNG.getValue(), "");
        for (ProductionOrder order : createdOrders) {
            try {
                // 2. 선점 처리를 위해 상태를 ALLOCATE_REQUEST로 변경
                Optional<ProductionOrder> optionalProductionOrder = productionOrderService.updateOrderState(tx,order.getId(), ProductionOrderState.ALLOCATE_REQUEST.getValue());

                if(optionalProductionOrder.isPresent()){
                    // 3. MQ 메시지 생성 및 발송
                    String transactionId = FormatUtils.generateTransactionId();
                    BaseMessage<OrderAllocateRequestBody> request = new BaseMessage<>();
                    request.setMessageName(MessageList.ORDER_ALLOCATE_REQUEST.getMessageName());
                    request.setMessageFrom(SystemName.MNG.getValue());
                    request.setMessageOwner(SystemName.MNG.getValue());
                    request.setMessageTo(SystemName.MNG.getValue());
                    request.setResultCode(ResultCode.OK.getValue());
                    request.setTransactionId(transactionId);

                    OrderAllocateRequestBody body = OrderAllocateRequestBody.builder()
                            .id(order.getId())
                            .orderId(order.getOrderId())
                            .orderLineNumber(order.getOrderLineNumber())
                            .build();

                    request.setBody(body);

                    rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_PEX, RabbitConfig.ROUTING_PEX, request);
                    log.info("Allocation request sent for OrderId: {}, LineNo: {}", order.getOrderId(), order.getOrderLineNumber());
                }
                else{
                    throw new RuntimeException("update Error");
                }

            } catch (Exception e) {
                log.error("Failed to process allocate request for ProductionOrder ID: {}", order.getId(), e);
            }
        }
    }
}