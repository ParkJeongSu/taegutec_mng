package kr.co.aim.api.strategy.impl.order.process;

import kr.co.aim.api.service.*;
import kr.co.aim.api.strategy.ProductionOrderProcessStrategy;
import kr.co.aim.api.context.ProductionOrderProcessContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.NextRRNReplyCommand;
import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessRRNReplyTypeStrategy implements ProductionOrderProcessStrategy {

    private final LotRepository lotRepository;
    private final LotMapper lotMapper;
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
        List<Lot> lotList = lotRepository.findByMngKey(productionOrder.getMngKey());
        NextRRNReplyCommand command =
                NextRRNReplyCommand
                        .builder()
                        .transactionInfo(tx)
                        .orderId(productionOrder.getOrderId())
                        .orderLineNumber(productionOrder.getOrderLineNumber())
                        .productionOrderId(productionOrder.getId())
                        .nextEquipmentName(productionOrder.getEquipmentName())
                        .build();
        for(Lot lot : lotList) {
            lot.nextRRNReply(command);
            lot = lotRepository.save(lot);
            LotHistoryEntity lotHistoryEntity = lotMapper.toHistoryEntity(lot);
            historyService.saveHistory(lotHistoryEntity);
        }

    }
}