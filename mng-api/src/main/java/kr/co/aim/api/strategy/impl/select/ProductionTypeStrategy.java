package kr.co.aim.api.strategy.impl.select;

import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.service.PortService;
import kr.co.aim.api.strategy.SelectStrategy;
import kr.co.aim.api.strategy.WhatDispatchStrategy;
import kr.co.aim.api.vo.powder.ops.WhatDispatchContext;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.common.enums.ProductionOrderType;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.model.ProductionOrder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductionTypeStrategy implements SelectStrategy {

    private final LotCarrierMappingService lotCarrierMappingService;

    @Override
    public boolean supports(ProductionOrder productionOrder) {
        return StringUtils.equals(ProductionOrderType.PRODUCTION.getValue(), productionOrder.getProductionOrderType());
    }

    @Override
    public List<LotCarrierMapping> selectAvailableCarrier(ProductionOrder productionOrder) {
        return lotCarrierMappingService.findByOrderIdAndOrderLineNumber(
                productionOrder.getOrderId(),
                productionOrder.getOrderLineNumber()
        );
    }
}