package kr.co.aim.api.strategy.impl.dispatch.where;

import kr.co.aim.api.service.EquipmentDefService;
import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.api.service.PortDefService;
import kr.co.aim.api.service.PortService;
import kr.co.aim.api.strategy.WhereDispatchStrategy;
import kr.co.aim.api.context.WhereDispatchContext;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.PortDef;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MagazineEquipmentDispatchStrategy implements WhereDispatchStrategy {

    private final EquipmentDefService equipmentDefService;
    private final EquipmentService equipmentService;
    private final PortDefService portDefService;
    private final PortService portService;


    @Override
    public boolean supports(WhereDispatchContext context) {
        String detailType = context.getSourceEquipmentDef().getDetailEquipmentType();
        return StringUtils.equals(EquipmentDetailType.MAGAZINE.getValue(), detailType);
    }

    @Override
    public void determineDestination(WhereDispatchContext context) {
        PortDef sourcePortDef = context.getSourcePortDef();

        if (StringUtils.equals(PortType.INPUT.getValue(), sourcePortDef.getPortType())) {

        } else if (StringUtils.equals(PortType.OUTPUT.getValue(), sourcePortDef.getPortType())) {
            // TODO: 창고(Warehouse) 반송 로직
            // 해포 설비 input port 에서 빈 팔렛트가 되면 무조건 Magazine 으로 이동
            // Magazine 은 설비 한 대라고 함

            Equipment targetEquipment = null; // < 목적지 창고 설비 명
            String targetZoneName = "";

            context.assignTarget(targetEquipment, null, targetZoneName);
        }
    }
}