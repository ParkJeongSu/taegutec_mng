package kr.co.aim.api.service;

import kr.co.aim.common.dto.EquipmentGroupDefSaveRequestDto;
import kr.co.aim.common.dto.EquipmentGroupDefSearchConditionDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.EquipmentGroupDefCreateCommand;
import kr.co.aim.domain.command.EquipmentGroupDefUpdateCommand;
import kr.co.aim.domain.model.EquipmentGroupDef;
import kr.co.aim.domain.repository.EquipmentGroupDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentGroupDefService {
    private final EquipmentGroupDefRepository equipmentGroupDefRepository;

    @Transactional
    public EquipmentGroupDef createEquipmentGroupDef(EquipmentGroupDefSaveRequestDto dto) {
        Optional<EquipmentGroupDef> existing = equipmentGroupDefRepository.findByEquipmentGroupName(dto.getEquipmentGroupName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 Equipment Group Name입니다: " + dto.getEquipmentGroupName());
        }
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        EquipmentGroupDefCreateCommand command = EquipmentGroupDefCreateCommand.builder()
                .transactionInfo(tx)
                .equipmentGroupName(dto.getEquipmentGroupName())
                .description(dto.getDescription())
                .checkOutState(dto.getCheckOutState())
                .checkOutTime(dto.getCheckOutTime())
                .checkOutUser(dto.getCheckOutUser())
                .dataState(dto.getDataState())
                .build();

        return equipmentGroupDefRepository.save(EquipmentGroupDef.create(command));
    }

    public Page<EquipmentGroupDef> findEquipmentGroupDefWithConditions(EquipmentGroupDefSearchConditionDto condition, Pageable pageable) {
        return equipmentGroupDefRepository.findEquipmentGroupDefWithConditions(condition, pageable);
    }

    public EquipmentGroupDef findById(Long id) {
        Optional<EquipmentGroupDef> optional = equipmentGroupDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 설비그룹 기준정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional
    public EquipmentGroupDef updateEquipmentGroupDef(EquipmentGroupDefSaveRequestDto dto) {
        Optional<EquipmentGroupDef> optional = equipmentGroupDefRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 설비그룹 기준정보가 없습니다. ID: " + dto.getId());
        }

        EquipmentGroupDef groupDef = optional.get();
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        EquipmentGroupDefUpdateCommand command = EquipmentGroupDefUpdateCommand.builder()
                .transactionInfo(tx)
                .description(dto.getDescription())
                .checkOutState(dto.getCheckOutState())
                .checkOutTime(dto.getCheckOutTime())
                .checkOutUser(dto.getCheckOutUser())
                .dataState(dto.getDataState())
                .build();

        groupDef.update(command);
        return equipmentGroupDefRepository.save(groupDef);
    }

    @Transactional
    public void deleteEquipmentGroupDefs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        equipmentGroupDefRepository.deleteAllByIdInBatch(ids);
    }
}