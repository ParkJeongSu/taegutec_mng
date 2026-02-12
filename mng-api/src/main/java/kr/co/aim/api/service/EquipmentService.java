package kr.co.aim.api.service;

import kr.co.aim.common.dto.*;
import kr.co.aim.common.enums.CommunicationState;
import kr.co.aim.common.enums.EquipmentOperationMode;
import kr.co.aim.common.enums.EquipmentState;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.domain.model.Equipments;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.domain.repository.EquipmentGroupRepository;
import kr.co.aim.domain.repository.EquipmentsRepository;
import kr.co.aim.infra.persistence.mapper.EquipmentGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class EquipmentService {
    private final EquipmentsRepository equipmentsRepository;
    private final EquipmentGroupRepository equipmentGroupRepository;
    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentGroupMapper equipmentGroupMapper;

    /**
     * 설비들의 communicationState 를 보고 받음 
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void communicationStateReport(BaseMessage<CommunicationStateReportBody> message) {

        String communicationState = message.getBody().getCommunicationState();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();

        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }
        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipments equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();
        equipment.communicationStateChange(command);

        equipmentsRepository.save(equipment);

    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void communicationStateChanged(BaseMessage<CommunicationStateChangedBody> message) {
        String communicationState = message.getBody().getCommunicationState();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();

        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipments equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();

        equipment.communicationStateChange(command);
        equipmentsRepository.save(equipment);
    }

    /**
     * equipment의 State 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void equipmentStateChanged(BaseMessage<EquipmentStateChangedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();
        String equipmentState = message.getBody().getEquipmentStateName();

        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipments equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command =EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipmentsRepository.save(equipment);

    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void equipmentStateReport(BaseMessage<EquipmentStateReportBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();
        String equipmentState = message.getBody().getEquipmentStateName();

        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipments equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command =EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipmentsRepository.save(equipment);
    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void operationModeChanged(BaseMessage<OperationModeChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipments equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipmentsRepository.save(equipment);
    }

    /**
     * 설비의 모드 변경 보고
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void operationModeReport(BaseMessage<OperationModeReportBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipments equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipmentsRepository.save(equipment);
    }


    // ============== [EquipmentGroup] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public EquipmentGroup createEquipmentGroup(EquipmentGroupCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<EquipmentGroup> optionalEquipmentGroup = equipmentGroupRepository.findByEquipmentGroupName(requestDto.getEquipmentGroupName());
        if(optionalEquipmentGroup.isPresent()){
            throw new EntityExistException("이미 생성된 설비그룹입니다. ID: " + requestDto.getEquipmentGroupName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        EquipmentGroupCreateCommand command =
                EquipmentGroupCreateCommand.builder()
                        .equipmentGroupName(requestDto.getEquipmentGroupName())
                        .description(requestDto.getDescription())
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();

        EquipmentGroup equipmentGroup = EquipmentGroup.create(command);

        return equipmentGroupRepository.save(equipmentGroup);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentGroupResponseDto> findEquipmentGroups(EquipmentGroupSearchCondtionDto condition, Pageable pageable) {
        Page<EquipmentGroupResponseDto> page = equipmentGroupRepository.findEquipmentGroupWithConditions(condition,pageable);
        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public EquipmentGroup changeEquipmentGroup(Long id, EquipmentGroupUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        EquipmentGroup equipmentGroup;
        Optional<EquipmentGroup> optionalEquipmentGroup = equipmentGroupRepository.findById(id);
        if(optionalEquipmentGroup.isPresent()){
            equipmentGroup = optionalEquipmentGroup.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설비그룹입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        EquipmentGroupUpdateCommand command =
                EquipmentGroupUpdateCommand.builder()
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();

        equipmentGroup.changeEquipmentGroup(command);

        return equipmentGroupRepository.save(equipmentGroup);
    }


    @Transactional
    public void deleteAllEquipmentGroupByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        equipmentGroupRepository.deleteAllByIdInBatch(ids);
    }


    // ============== [EquipmentGroup] ==============



    // ============== [EquipmentDef] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public EquipmentDef createEquipmentDef(EquipmentDefCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<EquipmentDef> optionalEquipmentDef = equipmentDefRepository.findByEquipmentDefName(requestDto.getEquipmentDefName());
        if(optionalEquipmentDef.isPresent()){
            throw new EntityExistException("이미 생성된 설비정의입니다. ID: " + requestDto.getEquipmentDefName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        EquipmentDefCreateCommand command =
                EquipmentDefCreateCommand.builder()
                        .equipmentDefName(requestDto.getEquipmentDefName())
                        .description(requestDto.getDescription())
                        .equipmentType(requestDto.getEquipmentType())
                        .equipmentGroupId(requestDto.getEquipmentGroupId())
                        .detailEquipmentType(requestDto.getDetailEquipmentType())
                        .stateModel(requestDto.getStateModel())
                        .vendorId(requestDto.getVendorId())
                        .modelId(requestDto.getModelId())
                        .processCapacity(requestDto.getProcessCapacity())
                        .loadingCapacity(requestDto.getLoadingCapacity())
                        .containerType(requestDto.getContainerType())
                        .transactionInfo(tx)
                        .build();

        EquipmentDef equipmentDef = EquipmentDef.create(command);

        return equipmentDefRepository.save(equipmentDef);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentDefResponseDto> findEquipmentDefs(EquipmentDefSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.
        Page<EquipmentDefResponseDto> page = equipmentDefRepository.findEquipmentDefWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public EquipmentDef changeEquipmentDef(Long id, EquipmentDefUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        EquipmentDef equipmentDef;
        Optional<EquipmentDef> optionalEquipmentDef = equipmentDefRepository.findById(id);
        if(optionalEquipmentDef.isPresent()){
            equipmentDef = optionalEquipmentDef.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설비정의입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        EquipmentDefUpdateCommand command =
                EquipmentDefUpdateCommand.builder()
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();

        equipmentDef.changeEquipmentDef(command);

        return equipmentDefRepository.save(equipmentDef);
    }


    @Transactional
    public void deleteAllEquipmentDefByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        equipmentDefRepository.deleteAllByIdInBatch(ids);
    }


    // ============== [EquipmentDef] ==============



    // ============== [Equipments] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Equipments createEquipment(EquipmentsCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<Equipments> optionalEquipments = equipmentsRepository.findByEquipmentName(requestDto.getEquipmentName());
        if(optionalEquipments.isPresent()){
            throw new EntityExistException("이미 생성된 설비입니다. ID: " + requestDto.getEquipmentName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        EquipmentsCreateCommand command =
                EquipmentsCreateCommand.builder()
                        .equipmentName(requestDto.getEquipmentName())
                        .equipmentDefId(requestDto.getEquipmentDefId())
                        .parentEquipmentId(requestDto.getParentEquipmentId())
                        .equipmentLevel(requestDto.getEquipmentLevel())
                        .equipmentState(requestDto.getEquipmentState())
                        .communicationState(requestDto.getCommunicationState())
                        .processCount(requestDto.getProcessCount())
                        .recipeName(requestDto.getRecipeName())
                        .defaultStockerId(requestDto.getDefaultStockerId())
                        .defaultZoneId(requestDto.getDefaultZoneId())
                        .holdState(requestDto.getHoldState())
                        .reasonCode(requestDto.getReasonCode())
                        .resourceState(requestDto.getResourceState())
                        .operationMode(requestDto.getOperationMode())
                        .messageServiceAddress(requestDto.getMessageServiceAddress())
                        .transactionInfo(tx)
                        .build();

        Equipments equipments = Equipments.create(command);

        return equipmentsRepository.save(equipments);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentsResponseDto> findEquipments(EquipmentsSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.
        Page<EquipmentsResponseDto> page = equipmentsRepository.findEquipmentsWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Equipments changeEquipment(Long id, EquipmentsUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Equipments equipments;
        Optional<Equipments> optionalEquipments = equipmentsRepository.findById(id);
        if(optionalEquipments.isPresent()){
            equipments = optionalEquipments.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설비입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        EquipmentsUpdateCommand command =
                EquipmentsUpdateCommand.builder()
                        .equipmentDefId(requestDto.getEquipmentDefId())
                        .parentEquipmentId(requestDto.getParentEquipmentId())
                        .equipmentLevel(requestDto.getEquipmentLevel())
                        .equipmentState(requestDto.getEquipmentState())
                        .communicationState(requestDto.getCommunicationState())
                        .processCount(requestDto.getProcessCount())
                        .recipeName(requestDto.getRecipeName())
                        .defaultStockerId(requestDto.getDefaultStockerId())
                        .defaultZoneId(requestDto.getDefaultZoneId())
                        .holdState(requestDto.getHoldState())
                        .reasonCode(requestDto.getReasonCode())
                        .resourceState(requestDto.getResourceState())
                        .operationMode(requestDto.getOperationMode())
                        .messageServiceAddress(requestDto.getMessageServiceAddress())
                        .transactionInfo(tx)
                        .build();

        equipments.changeEquipment(command);

        return equipmentsRepository.save(equipments);
    }


    @Transactional
    public void deleteAllEquipmentsByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        equipmentsRepository.deleteAllByIdInBatch(ids);
    }
    // ============== [Equipments] ==============
    
}