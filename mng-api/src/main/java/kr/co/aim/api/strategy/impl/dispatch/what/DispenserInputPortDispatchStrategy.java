package kr.co.aim.api.strategy.impl.dispatch.what;

import kr.co.aim.api.service.*;
import kr.co.aim.api.strategy.WhatDispatchStrategy;
import kr.co.aim.api.context.WhatDispatchContext;
import kr.co.aim.common.enums.CarrierType;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.domain.repository.CarrierRepository;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DispenserInputPortDispatchStrategy implements WhatDispatchStrategy {

    private final CarrierRepository carrierRepository;
    private final CarrierDefRepository carrierDefRepository;
    private final LotCarrierMappingRepository lotCarrierMappingRepository;

    @Override
    public boolean supports(WhatDispatchContext context) {
        String detailType = context.getEquipmentDef().getDetailEquipmentType();
        return StringUtils.equals(EquipmentDetailType.DISPENSER.getValue(), detailType);
    }

    @Override
    public void determineDestination(WhatDispatchContext context) {
        PortDef portDef = context.getPortDef();

        if (StringUtils.equals(PortType.INPUT.getValue(), portDef.getPortType())) {
            // Dispenser input 설비는 창고에 있는 8단이 쌓여있는 Empty pallet을 가져오는 로직임
            List<Carrier> targetCarrierList = carrierRepository.findByQuantityAndCarrierType(new BigDecimal(8), CarrierType.PALLET.getValue());
            if(CollectionUtils.isEmpty(targetCarrierList)){
                throw new RuntimeException("carrier not found");
            }
            Carrier targetCarrier = targetCarrierList.get(0);

            Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingRepository.findByCarrierName(targetCarrier.getCarrierName());
            if(optionalLotCarrierMapping.isPresent()){
                // Lot이 존재하면 안됨 존재하면 Error
                throw new RuntimeException("Lot found");
            }

            Optional<CarrierDef> optionalCarrierDef = carrierDefRepository.findByCarrierDefName(targetCarrier.getCarrierDefName());
            if(optionalCarrierDef.isEmpty()){
                throw new RuntimeException("carrier Def not found");
            }
            CarrierDef targetCarrierDef = optionalCarrierDef.get();
            context.assignCarrier(targetCarrier,targetCarrierDef);
        } else if (StringUtils.equals(PortType.OUTPUT.getValue(), portDef.getPortType())) {
            // Dispenser 는 output port에서 loadRequest 하는 경우가 없음
        }
    }
}