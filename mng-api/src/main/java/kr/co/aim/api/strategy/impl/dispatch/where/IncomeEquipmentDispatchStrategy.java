package kr.co.aim.api.strategy.impl.dispatch.where;

import kr.co.aim.api.strategy.WhereDispatchStrategy;
import kr.co.aim.api.context.WhereDispatchContext;
import kr.co.aim.common.enums.EquipmentDetailType;
import kr.co.aim.common.enums.PortType;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.domain.repository.PortRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IncomeEquipmentDispatchStrategy implements WhereDispatchStrategy {

    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentRepository equipmentRepository;
    private final PortRepository portRepository;


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
            // 해포 설비 input port 에서 빈 팔렛트가 되면 무조건 Magazine 으로 이동
            // Magazine 은 설비 한 대라고 함

            List<EquipmentDef> equipmentDefList = equipmentDefRepository.findByDetailEquipmentType(EquipmentDetailType.MAGAZINE.getValue());

            if(CollectionUtils.isEmpty(equipmentDefList)){
                throw new RuntimeException("equipmentDefList is empty");
            }
            EquipmentDef equipmentDef = equipmentDefList.get(0);

            Optional<Equipment> optionalEquipment = equipmentRepository.findByEquipmentName(equipmentDef.getEquipmentName());
            if(optionalEquipment.isEmpty()){
                // 여기서도 에러
                throw  new RuntimeException("equipment not found");
            }
            Equipment targetEquipment = optionalEquipment.get();

            List<Port> portList = portRepository.findByEquipmentNameAndPortType(targetEquipment.getEquipmentName(),PortType.INPUT.getValue());

            if(CollectionUtils.isEmpty(portList)){
                throw new RuntimeException("portList is empty");
            }
            Port targetPort = portList.get(0);

            context.assignTarget(targetEquipment, targetPort, StringUtils.EMPTY);
        } else if (StringUtils.equals(PortType.OUTPUT.getValue(), sourcePortDef.getPortType())) {
            // TODO: 창고(Warehouse) 반송 로직
            // 4개의 창고중 어디로 가야하는지 확인하기
        }
    }
}