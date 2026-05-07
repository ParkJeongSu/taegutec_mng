package kr.co.aim.api.service;

import kr.co.aim.api.vo.carrier.CarrierDispatchRequestVo;
import kr.co.aim.api.vo.carrier.CarrierSelectionResult;
import kr.co.aim.common.enums.CarrierCleanState;
import kr.co.aim.common.enums.CarrierTransportState;
import kr.co.aim.common.enums.CarrierUseState;
import kr.co.aim.common.enums.ContainerType;
import kr.co.aim.domain.model.Carrier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
@Profile({"pex","tex","scheduler"})
public class CarrierSelectionService {

    private final CarrierService carrierService;
    private final ProductionOrderService productionOrderService;

    @Transactional(value = "mssqlTransactionManager")
    public List<CarrierSelectionResult> selectCarrierByInputPort(CarrierDispatchRequestVo vo) {

        // TODO : Input Port
        // Input Port :
        // (1) 설비에서 Production Order Select
        // (2) 존재하면, 해당 order Select
        // (3) 존재하지 않으면, 설비명으로 신규 Production Order Select
        // (4) Order 에서 가장 우선순위가 높은 Carrier Select

        return null;
    }

    @Transactional(value = "mssqlTransactionManager")
    public List<CarrierSelectionResult> selectCarrierByOutputPort(CarrierDispatchRequestVo vo) {
        // TODO : Output Port
        // (1) EquipmentDef 에서 ContainerType을 Select
        // (2) ContainerType None 이거나 위에서 찾은 type으로 가장 우선 순위가 높은 Carrier 찾기
        List<String> containerTypes = new ArrayList<>();
        containerTypes.add(ContainerType.NONE.getValue());
        containerTypes.add(vo.getEquipmentDef().getContainerType());
        List<Carrier> carriers = carrierService.findCarriersForEmptyContainer(
                CarrierCleanState.CLEAN.getValue(),
                CarrierTransportState.IN_WAREHOUSE.getValue(),
                "",
                CarrierUseState.AVAILABLE.getValue(),
                0,
                containerTypes
        );

        // 리스트가 비어있을 수 있으므로 방어 로직 추가
        if (carriers == null || carriers.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarrierSelectionResult> carrierSelectionResultList = new ArrayList<>();
        for(Carrier carrier : carriers) {
            CarrierSelectionResult
                    .builder()
                    .carrier(carrier)
                    .build();
        }

        return carrierSelectionResultList;

    }



}