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
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.EquipmentDef;
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

    @Transactional(value = "mssqlTransactionManager")
    public Equipment save(Equipment equipment){
        return equipmentRepository.save(equipment);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<Equipment> findEquipmentByEquipmentName(String equipmentName){
        return equipmentRepository.findByEquipmentName(equipmentName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<EquipmentDef> findEquipmentDefByEquipmentName(String equipmentName){
        return equipmentDefRepository.findByEquipmentName(equipmentName);
    }

}