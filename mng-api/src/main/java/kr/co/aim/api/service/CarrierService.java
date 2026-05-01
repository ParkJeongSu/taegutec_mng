package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.vo.carrier.CarrierDispatchRequestVo;
import kr.co.aim.api.vo.carrier.CarrierSelectionResult;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.payload.MaterialDeassignFromCarrier;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.CarrierDeassignCommand;
import kr.co.aim.domain.command.CleanJobEndedCommand;
import kr.co.aim.domain.command.CleanJobStartedCommand;
import kr.co.aim.domain.command.LocationChangedCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.CarrierHistoryEntity;
import kr.co.aim.infra.persistence.entity.IfEventQueueEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.springdatajpa.IfEventQueueJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class CarrierService {
    private final ObjectMapper objectMapper;
    private final CarrierDefRepository carrierDefRepository;
    private final CarrierRepository carrierRepository;

    @Transactional(value = "mssqlTransactionManager")
    public Optional<Carrier> findByCarrierName(String carrierName){
        return carrierRepository.findByCarrierName(carrierName);
    }

    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public List<Carrier> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber){
        return carrierRepository.findByOrderIdAndOrderLineNumber(orderId,orderLineNumber);
    }

    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public List<CarrierHistory> findByOrderIdAndOrderLineNumberAndEventName(String orderId, String orderLineNumber,String eventName){
        return carrierRepository.findByOrderIdAndOrderLineNumberAndEventName(orderId,orderLineNumber,eventName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Carrier save(Carrier carrier){
        return carrierRepository.save(carrier);
    }


    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllCarriersByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        carrierRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(value = "mssqlTransactionManager")
    public List<CarrierSelectionResult> selectCarrierByInputPort(CarrierDispatchRequestVo vo) {

        // TODO : Input Port
        // Input Port :
        // (1) 설비에서 Production Order Select
        // (2) 존재하면, 해당 order Select
        // (3) 존재하지 않으면, 설비명으로 신규 Production Order Select
        // (4) Order 에서 가장 우선순위가 높은 Carrier Select

        // TODO: ProductionOrderService 는 순환참조되기 때문에 따로 CarrierDispatchServer 생성
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
        List<Carrier> carriers = carrierRepository.findCarriersForEmptyContainer(
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