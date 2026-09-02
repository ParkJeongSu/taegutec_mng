package kr.co.aim.api.service;

import kr.co.aim.common.dto.PortDefSaveRequestDto;
import kr.co.aim.common.condition.PortDefSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.PortDefCreateCommand;
import kr.co.aim.domain.command.PortDefUpdateCommand;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.PortDefRepository;
import kr.co.aim.infra.persistence.entity.PortDefHistoryEntity;
import kr.co.aim.infra.persistence.mapper.PortDefMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class PortDefService {

    private final PortDefRepository portDefRepository;
    private final PortDefMapper portDefMapper;
    private final HistoryService historyService;

    @Transactional(value = "mssqlTransactionManager")
    public PortDef save(PortDef portDef) {
        return portDefRepository.save(portDef);
    }

    @Transactional(value = "mssqlTransactionManager")
    public PortDef createPortDef(PortDefSaveRequestDto dto) {
        Optional<PortDef> existing = portDefRepository.findByEquipmentNameAndPortName(dto.getEquipmentName(), dto.getPortName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("해당 설비에 이미 존재하는 Port Name입니다: " + dto.getEquipmentName() + " - " + dto.getPortName());
        }
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        PortDefCreateCommand command = PortDefCreateCommand.builder()
                .transactionInfo(tx)
                .equipmentName(dto.getEquipmentName())
                .portName(dto.getPortName())
                .factoryName(dto.getFactoryName())
                .portNumber(dto.getPortNumber())
                .description(dto.getDescription())
                .transportMode(dto.getTransportMode())
                .portType(dto.getPortType())
                .detailPortType(dto.getDetailPortType())
                .portUseType(dto.getPortUseType())
                .portRoleType(dto.getPortRoleType())
                .workCenterName(dto.getWorkCenterName())
                .locationId(dto.getLocationId())
                .connectedEquipmentName(dto.getConnectedEquipmentName())
                .connectedPortName(dto.getConnectedPortName())
                .checkOutState(dto.getCheckOutState())
                .checkOutTime(dto.getCheckOutTime())
                .checkOutUser(dto.getCheckOutUser())
                .dataState(dto.getDataState())
                .build();
        PortDef portDef = portDefRepository.save(PortDef.create(command));
        PortDefHistoryEntity historyEntity = portDefMapper.toHistoryEntity(portDef);
        historyService.saveHistory(historyEntity);
        return portDef;
    }

    @Transactional(value = "mssqlTransactionManager")
    public Page<PortDef> findPortDefWithConditions(PortDefSearchCondition condition, Pageable pageable) {
        return portDefRepository.findPortDefWithConditions(condition, pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public PortDef findById(Long id) {
        Optional<PortDef> optional = portDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 포트 기준정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional(value = "mssqlTransactionManager")
    public PortDef updatePortDef(PortDefSaveRequestDto dto) {
        Optional<PortDef> optional = portDefRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 포트 기준정보가 없습니다. ID: " + dto.getId());
        }

        PortDef portDef = optional.get();
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        PortDefUpdateCommand command = PortDefUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName())
                .portNumber(dto.getPortNumber())
                .description(dto.getDescription())
                .transportMode(dto.getTransportMode())
                .portType(dto.getPortType())
                .detailPortType(dto.getDetailPortType())
                .portUseType(dto.getPortUseType())
                .portRoleType(dto.getPortRoleType())
                .workCenterName(dto.getWorkCenterName())
                .locationId(dto.getLocationId())
                .connectedEquipmentName(dto.getConnectedEquipmentName())
                .connectedPortName(dto.getConnectedPortName())
                .checkOutState(dto.getCheckOutState())
                .checkOutTime(dto.getCheckOutTime())
                .checkOutUser(dto.getCheckOutUser())
                .dataState(dto.getDataState())
                .build();

        portDef.update(command);
        portDef = portDefRepository.save(portDef);
        PortDefHistoryEntity historyEntity = portDefMapper.toHistoryEntity(portDef);
        historyService.saveHistory(historyEntity);
        return portDef;
    }

    @Transactional(value = "mssqlTransactionManager")
    public void deletePortDefs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        portDefRepository.deleteAllByIdInBatch(ids);
    }

}
