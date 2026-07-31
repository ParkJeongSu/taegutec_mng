package kr.co.aim.api.service;

import kr.co.aim.api.dto.EquipmentDataDashboard;
import kr.co.aim.api.dto.EquipmentGroupDashboard;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.EquipmentSearchCondition;
import kr.co.aim.common.enums.ProductionOrderState;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.domain.repository.EquipmentGroupDefRepository;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.infra.persistence.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentGroupDefRepository equipmentGroupDefRepository;
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



    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public Page<Equipment> findEquipmentByCondition(EquipmentSearchCondition condition, Pageable pageable){
        return equipmentRepository.findEquipmentByCondition(condition,pageable);
    }

    @Transactional(value = "mssqlTransactionManager",readOnly = true)
    public List<EquipmentHistory> findEquipmentHistoryByPeriod(LocalDateTime start, LocalDateTime end){
        return equipmentRepository.findEquipmentHistoryByPeriod(start, end);
    }

}