package kr.co.aim.api.schedule;

import kr.co.aim.api.service.ProductionOrderService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.ProductionOrderProcessRequestBody;
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
public class PowderOrderProcessScheduler {

    private final ProductionOrderService productionOrderService;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    @SchedulerLock(
            name = "powderOrderProcessRequest",
            lockAtMostFor = "PT2M",
            lockAtLeastFor = "PT5S"
    )
    @Transactional
    public void powderOrderProcessRequest() {

        // 1. CREATED 상태의 ProductionOrder 조회
        //List<String> productionOrderState = new ArrayList<>();
        //productionOrderState.add(ProductionOrderState.CREATED.getValue());
        //List<ProductionOrder> productionOrderList = productionOrderService.findByProductionOrderStateInOrderByCreateTimeAsc(productionOrderState);

        List<String> productionOrderType = new ArrayList<>();
        productionOrderType.add(ProductionOrderType.PRODUCTION.getValue());
        productionOrderType.add(ProductionOrderType.UNPACKING.getValue());
        productionOrderType.add(ProductionOrderType.PRODUCTION_ISSUE.getValue());
        productionOrderType.add(ProductionOrderType.RRN_REPLY.getValue());
        List<ProductionOrder> productionOrderList = productionOrderService.findByProductionOrderStateAndProductionOrderTypeInOrderByCreateTimeAsc(
                ProductionOrderState.ACCEPTED.getValue(),
                productionOrderType
        );

        if (CollectionUtils.isEmpty(productionOrderList)) {
            return;
        }

        TransactionInfo tx = TransactionInfo.now(EventName.PROCESS_REQUEST.getValue(), SystemName.MNG.getValue(), "");
        for (ProductionOrder order : productionOrderList) {
            try {
                // 2. 선점 처리를 위해 상태를 PROCESS_REQUEST로 변경
                Optional<ProductionOrder> optionalProductionOrder = productionOrderService.updateOrderState(tx,order.getId(), ProductionOrderState.PROCESS_REQUEST.getValue());

                if(optionalProductionOrder.isPresent()){
                    // 3. MQ 메시지 생성 및 발송
                    String transactionId = FormatUtils.generateTransactionId();
                    BaseMessage<ProductionOrderProcessRequestBody> request = new BaseMessage<>();
                    request.setMessageName(MessageList.PRODUCTION_ORDER_PROCESS_REQUEST.getMessageName());
                    request.setMessageFrom(SystemName.MNG.getValue());
                    request.setMessageOwner(SystemName.MNG.getValue());
                    request.setMessageTo(SystemName.MNG.getValue());
                    request.setResultCode(ResultCode.OK.getValue());
                    request.setTransactionId(transactionId);

                    ProductionOrderProcessRequestBody body = ProductionOrderProcessRequestBody.builder()
                            .id(order.getId())
                            .orderId(order.getOrderId())
                            .orderLineNumber(order.getOrderLineNumber())
                            .build();

                    request.setBody(body);

                    rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_PEX, RabbitConfig.ROUTING_PEX, request);
                    log.info("Process request sent for OrderId: {}, LineNo: {}", order.getOrderId(), order.getOrderLineNumber());
                }
                else{
                    throw new RuntimeException("update Error");
                }

            } catch (Exception e) {
                log.error("Failed to process request for ProductionOrder ID: {}", order.getId(), e);
            }
        }
    }
}