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
public class ProcessMaterialInboundTypeStrategy implements ProductionOrderProcessStrategy {

    private final LotCarrierMappingService lotCarrierMappingService;
    private final ProductDefService productDefService;
    private final List<SelectStrategy> selectStrategyList;
    private final CarrierSelectionService carrierSelectionService;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final HistoryService historyService;
    private final ProductionOrderService productionOrderService;

    @Override
    public boolean supports(ProductionOrderProcessContext context) {
        return context.getProductionOrder().getProductionOrderType().equals(ProductionOrderType.MATERIAL_INBOUND.getValue());
    }

    @Override
    public void productionOrderProcess(ProductionOrderProcessContext context) {
        // 1. ProductionOrder 조회
        ProductionOrder productionOrder = context.getProductionOrder();

        // 자재 인바운드 로직은 처리할 로직이 없음
    }
}