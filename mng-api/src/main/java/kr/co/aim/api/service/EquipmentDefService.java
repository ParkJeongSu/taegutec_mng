package kr.co.aim.api.service;

import kr.co.aim.common.dto.EquipmentDefSaveRequestDto;
import kr.co.aim.common.dto.EquipmentDefSearchConditionDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.EquipmentDefCreateCommand;
import kr.co.aim.domain.command.EquipmentDefUpdateCommand;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentDefService {

    private final EquipmentDefRepository equipmentDefRepository;

    @Transactional
    public EquipmentDef createEquipmentDef(EquipmentDefSaveRequestDto dto) {
        Optional<EquipmentDef> existing = equipmentDefRepository.findByEquipmentName(dto.getEquipmentName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 Equipment Name입니다: " + dto.getEquipmentName());
        }
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        EquipmentDefCreateCommand command = EquipmentDefCreateCommand.builder()
                .transactionInfo(tx)
                .equipmentName(dto.getEquipmentName())
                .factoryName(dto.getFactoryName())
                .description(dto.getDescription())
                .equipmentType(dto.getEquipmentType())
                .equipmentGroupName(dto.getEquipmentGroupName())
                .detailEquipmentType(dto.getDetailEquipmentType())
                .vendorId(dto.getVendorId())
                .modelId(dto.getModelId())
                .processCapacity(dto.getProcessCapacity())
                .containerType(dto.getContainerType())
                .plcType(dto.getPlcType())
                .routeKey(dto.getRouteKey())
                .serverName(dto.getServerName())
                .checkOutState(dto.getCheckOutState())
                .checkOutTime(dto.getCheckOutTime())
                .checkOutUser(dto.getCheckOutUser())
                .dataState(dto.getDataState())
                .localNo(dto.getLocalNo())
                .build();

        return equipmentDefRepository.save(EquipmentDef.create(command));
    }

    public Page<EquipmentDef> findEquipmentDefWithConditions(EquipmentDefSearchConditionDto condition, Pageable pageable) {
        return equipmentDefRepository.findEquipmentDefWithConditions(condition, pageable);
    }

    public EquipmentDef findById(Long id) {
        Optional<EquipmentDef> optional = equipmentDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 설비 기준정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional
    public EquipmentDef updateEquipmentDef(EquipmentDefSaveRequestDto dto) {
        Optional<EquipmentDef> optional = equipmentDefRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 설비 기준정보가 없습니다. ID: " + dto.getId());
        }

        EquipmentDef equipmentDef = optional.get();
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        EquipmentDefUpdateCommand command = EquipmentDefUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName())
                .description(dto.getDescription())
                .equipmentType(dto.getEquipmentType())
                .equipmentGroupName(dto.getEquipmentGroupName())
                .detailEquipmentType(dto.getDetailEquipmentType())
                .vendorId(dto.getVendorId())
                .modelId(dto.getModelId())
                .processCapacity(dto.getProcessCapacity())
                .containerType(dto.getContainerType())
                .plcType(dto.getPlcType())
                .routeKey(dto.getRouteKey())
                .serverName(dto.getServerName())
                .checkOutState(dto.getCheckOutState())
                .checkOutTime(dto.getCheckOutTime())
                .checkOutUser(dto.getCheckOutUser())
                .dataState(dto.getDataState())
                .localNo(dto.getLocalNo())
                .build();

        equipmentDef.update(command);
        return equipmentDefRepository.save(equipmentDef);
    }

    @Transactional
    public void deleteEquipmentDefs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        equipmentDefRepository.deleteAllByIdInBatch(ids);
    }
}
