package kr.co.aim.api.strategy.impl.dispatch.where;

import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.api.service.PortService;
import kr.co.aim.api.strategy.WhereDispatchStrategy;
import kr.co.aim.api.vo.powder.ops.WhereDispatchContext;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.domain.model.PortDef;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncomeEquipmentDispatchStrategy implements WhereDispatchStrategy {

    private final EquipmentService equipmentService;
    private final PortService portService;

    @Override
    public boolean supports(WhereDispatchContext context) {
        String detailType = context.getSourceEquipmentDef().getDetailEquipmentType();
        return StringUtils.equals(EquipmentDetailType.INCOME.getValue(), detailType);
    }

    @Override
    public void determineDestination(WhereDispatchContext context) {
        PortDef sourcePortDef = context.getSourcePortDef();

        if (StringUtils.equals(PortType.INPUT.getValue(), sourcePortDef.getPortType())) {
            // TODO: Magazine Input Port 조회 로직
            // Equipment targetEquip = ...
            // Port targetPort = ...
            // context.assignTarget(targetEquip, targetPort, targetZone);
        } else if (StringUtils.equals(PortType.OUTPUT.getValue(), sourcePortDef.getPortType())) {
            // TODO: 창고(Warehouse) 반송 로직
        }
    }
}