package kr.co.aim.api.strategy.impl.select;

import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.api.strategy.SelectStrategy;
import kr.co.aim.common.enums.ProductionOrderType;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SelectProductionIssueTypeStrategy implements SelectStrategy {

    private final LotCarrierMappingRepository lotCarrierMappingRepository;

    @Override
    public boolean supports(ProductionOrder productionOrder) {
        return StringUtils.equals(ProductionOrderType.PRODUCTION_ISSUE.getValue(), productionOrder.getProductionOrderType());
    }

    @Override
    public List<LotCarrierMapping> selectAvailableCarrier(ProductionOrder productionOrder) {
        return lotCarrierMappingRepository.findByLotName(productionOrder.getMaterialLotName());
    }
}