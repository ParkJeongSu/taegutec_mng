package kr.co.aim.api.strategy.impl.order.process;

import kr.co.aim.api.service.*;
import kr.co.aim.api.strategy.ProductionOrderProcessStrategy;
import kr.co.aim.api.context.ProductionOrderProcessContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.NextRRNReplyCommand;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessRRNReplyTypeStrategy implements ProductionOrderProcessStrategy {

    private final LotCarrierMappingService lotCarrierMappingService;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final HistoryService historyService;

    @Override
    public boolean supports(ProductionOrderProcessContext context) {
        return context.getProductionOrder().getProductionOrderType().equals(ProductionOrderType.RRN_REPLY.getValue());
    }

    @Override
    public void productionOrderProcess(ProductionOrderProcessContext context) {
        // 1. ProductionOrder 조회
        ProductionOrder productionOrder = context.getProductionOrder();
        TransactionInfo tx = context.getTx();
        List<LotCarrierMapping> lotCarrierMappingList = lotCarrierMappingService.findByMngKey(productionOrder.getMngKey());
        NextRRNReplyCommand command =
                NextRRNReplyCommand
                        .builder()
                        .transactionInfo(tx)
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .productionOrderId(productionOrder.getId())
                        .nextEquipmentName(productionOrder.getEquipmentName())
                        .build();
        for(LotCarrierMapping lotCarrierMapping : lotCarrierMappingList) {
            lotCarrierMapping.nextRRNReply(command);
            lotCarrierMapping = lotCarrierMappingService.save(lotCarrierMapping);
            LotCarrierMappingHistoryEntity lotCarrierMappingHistoryEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
            historyService.saveHistory(lotCarrierMappingHistoryEntity);
        }

    }
}