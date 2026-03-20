package kr.co.aim.api.service;

import kr.co.aim.common.enums.CommunicationState;
import kr.co.aim.common.enums.EquipmentOperationMode;
import kr.co.aim.common.enums.EquipmentState;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.CommunicationStateChangeCommand;
import kr.co.aim.domain.command.EquipmentOperationModeChangeCommand;
import kr.co.aim.domain.command.EquipmentStateChangeCommand;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.domain.repository.EquipmentGroupRepository;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.infra.persistence.entity.EquipmentHistoryEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentGroupRepository equipmentGroupRepository;
    private final EquipmentDefRepository equipmentDefRepository;
    private final EquipmentMapper equipmentMapper;
    private final HistoryService historyService;

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

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }
        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();
        equipment.communicationStateChange(command);

        equipment = equipmentRepository.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);

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

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();

        equipment.communicationStateChange(command);
        equipment = equipmentRepository.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
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

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command =EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipment = equipmentRepository.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);

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

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command =EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipment = equipmentRepository.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
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

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipment = equipmentRepository.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
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

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipment = equipmentRepository.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
    }
}