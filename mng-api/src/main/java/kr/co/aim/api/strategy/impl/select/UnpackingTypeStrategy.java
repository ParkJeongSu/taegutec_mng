package kr.co.aim.api.strategy.impl.select;

import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.strategy.SelectStrategy;
import kr.co.aim.common.enums.CarrierType;
import kr.co.aim.common.enums.ProductionOrderType;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.ProductionOrder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnpackingTypeStrategy implements SelectStrategy {

    private final LotCarrierMappingService lotCarrierMappingService;

    @Override
    public boolean supports(ProductionOrder productionOrder) {
        return StringUtils.equals(ProductionOrderType.UNPACKING.getValue(), productionOrder.getProductionOrderType());
    }

    @Override
    public List<LotCarrierMapping> selectAvailableCarrier(ProductionOrder productionOrder) {
        return lotCarrierMappingService.findLotCarrierMappingForUnpacking(
                productionOrder.getLotName(),
                CarrierType.PALLET.getValue()
        );
    }
}