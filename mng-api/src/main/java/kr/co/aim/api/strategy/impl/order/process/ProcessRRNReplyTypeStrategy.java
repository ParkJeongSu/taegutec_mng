package kr.co.aim.api.strategy.impl.order.process;

import kr.co.aim.api.service.*;
import kr.co.aim.api.strategy.ProductionOrderProcessStrategy;
import kr.co.aim.api.strategy.SelectStrategy;
import kr.co.aim.api.vo.powder.ops.ProductionOrderProcessContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.AllocatedCommand;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessRRNReplyTypeStrategy implements ProductionOrderProcessStrategy {

    private final LotCarrierMappingService lotCarrierMappingService;
    private final ProductDefService productDefService;
    private final List<SelectStrategy> selectStrategyList;
    private final CarrierSelectionService carrierSelectionService;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final HistoryService historyService;
    private final ProductionOrderService productionOrderService;

    @Override
    public boolean supports(ProductionOrderProcessContext context) {
        return context.getProductionOrder().getProductionOrderType().equals(ProductionOrderType.RRN_REPLY.getValue());
    }

    @Override
    public void productionOrderProcess(ProductionOrderProcessContext context) {
        // 1. ProductionOrder 조회
        ProductionOrder productionOrder = context.getProductionOrder();

        // 2. ProductDef 조회 (ITEM_NAME으로 TOLERANCE_VAL 가져옴)
        Optional<ProductDef> optionalProductDef = productDefService.findByProductDefName(productionOrder.getItemName());
        BigDecimal toleranceVal = BigDecimal.ZERO;
        if (optionalProductDef.isPresent()) {
            ProductDef productDef = optionalProductDef.get();
            if (productDef.getToleranceVal() != null) {
                toleranceVal = productDef.getToleranceVal();
            }
        }

        SelectStrategy targetStrategy = null;
        for (SelectStrategy strategy : selectStrategyList) {
            if (strategy.supports(productionOrder)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No dispatch strategy found for context");
        }

        // 3. 할당 가능한 LotCarrierMapping 목록 조회 (Inbound 시간순/생성순 정렬 데이터)
        List<LotCarrierMapping> availableMappings = targetStrategy.selectAvailableCarrier(productionOrder);

        if (CollectionUtils.isEmpty(availableMappings)) {
            log.warn("No available LotCarrierMappings found for OrderId: {}, LineNo: {}", productionOrder.getOrderId(), productionOrder.getOrderLineNumber());
            return;
        }

        // 4. DP 서비스를 통한 캐리어 최적 조합 선택
        List<LotCarrierMapping> selectedMappings = carrierSelectionService.selectBestCarriers(
                availableMappings,
                productionOrder.getPlanQuantity(),
                toleranceVal
        );

        // 5. 선택된 캐리어 및 오더 상태 변경
        if (CollectionUtils.isNotEmpty(selectedMappings)) {
            TransactionInfo transactionInfo = TransactionInfo.now(EventName.ALLOCATE.getValue(), SystemName.MNG.getValue(), "Carrier Allocated by DP Knapsack");
            int seq = 1;
            for (LotCarrierMapping mapping : selectedMappings) {
                AllocatedCommand command =
                        AllocatedCommand
                                .builder()
                                .orderId(productionOrder.getOrderId())
                                .orderLineNumber(productionOrder.getOrderLineNumber())
                                .productionOrderId(productionOrder.getId())
                                .seq(seq++)
                                .productionStatus(ProductionStatus.ALLOCATED.getValue())
                                .build();
                mapping.allocated(command);
                mapping = lotCarrierMappingService.save(mapping);
                LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(mapping);
                historyService.saveHistory(historyEntity);
            }

            // ProductionOrder 상태를 ALLOCATED로 변경
            Optional<ProductionOrder> optionalProductionOrder = productionOrderService.updateOrderState(transactionInfo,productionOrder.getId(), ProductionOrderState.PROCESS_COMPLETED.getValue());

            log.info("Successfully allocated {} carriers for ProductionOrder ID: {}", selectedMappings.size(), productionOrder.getId());
        } else {
            log.warn("Failed to allocate carriers for ProductionOrder ID: {}. Holding allocation.", productionOrder.getId());
            // 조합 실패 시 오더 상태 원복 또는 별도 에러 처리 진행
        }
    }
}