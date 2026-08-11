package kr.co.aim.api.strategy.impl.dispatch.what;

import kr.co.aim.api.service.CarrierDefService;
import kr.co.aim.api.service.CarrierService;
import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.api.service.PortService;
import kr.co.aim.api.strategy.WhatDispatchStrategy;
import kr.co.aim.api.vo.powder.ops.WhatDispatchContext;
import kr.co.aim.common.enums.CarrierType;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.domain.model.PortDef;
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

    private final CarrierService carrierService;
    private final CarrierDefService carrierDefService;

    @Override
    public boolean supports(WhatDispatchContext context) {
        String detailType = context.getEquipmentDef().getDetailEquipmentType();
        return StringUtils.equals(EquipmentDetailType.DISPENSER.getValue(), detailType);
    }

    @Override
    public void determineDestination(WhatDispatchContext context) {
        PortDef portDef = context.getPortDef();

        if (StringUtils.equals(PortType.INPUT.getValue(), portDef.getPortType())) {
            // TODO: Magazine Input Port 조회 로직
            // Equipment targetEquip = ...
            // Port targetPort = ...
            // context.assignTarget(targetEquip, targetPort, targetZone);

            List<Carrier> targetCarrierList = carrierService.findByQuantityAndCarrierType(new BigDecimal(8), CarrierType.PALLET.getValue());
            if(CollectionUtils.isEmpty(targetCarrierList)){
                throw new RuntimeException("carrier not found");
            }
            Carrier targetCarrier = targetCarrierList.get(0);
            Optional<CarrierDef> optionalCarrierDef = carrierDefService.findByCarrierDefName(targetCarrier.getCarrierDefName());
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