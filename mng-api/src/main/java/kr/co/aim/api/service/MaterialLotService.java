package kr.co.aim.api.service;

import kr.co.aim.api.dto.MaterialLotCreateRequestDto;
import kr.co.aim.api.dto.MaterialLotResponse;
import kr.co.aim.api.dto.MaterialLotUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.MaterialLotCreateCommand;
import kr.co.aim.domain.command.MaterialLotUpdateCommand;
import kr.co.aim.domain.model.MaterialLot;
import kr.co.aim.domain.repository.MaterialLotRepository;
import kr.co.aim.domain.repository.condition.MaterialLotSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialLotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.MaterialLotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialLotService {

    private final MaterialLotRepository materialLotRepository;
    private final HistoryService historyService;
    private final MaterialLotMapper materialLotMapper;

    /**
     * 조건 및 페이징 기반 자재 로트(MATERIAL_LOT) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MaterialLotResponse> findMaterialLots(MaterialLotSearchCondition condition, Pageable pageable) {
        Page<MaterialLot> pageResult = materialLotRepository.findMaterialLotsWithConditions(condition, pageable);
        List<MaterialLotResponse> content = new ArrayList<>();
        for (MaterialLot item : pageResult.getContent()) {
            content.add(MaterialLotResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자재 로트(MATERIAL_LOT) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public MaterialLotResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자재 로트(MATERIAL_LOT) ID가 유효하지 않습니다.");
        }
        Optional<MaterialLot> optional = materialLotRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 자재 로트(MATERIAL_LOT)이(가) 존재하지 않습니다. ID: " + id);
        }
        return MaterialLotResponse.fromDomain(optional.get());
    }

    /**
     * 신규 자재 로트(MATERIAL_LOT) 등록 (CREATE) 및 MATERIAL_LOT_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialLotResponse createMaterialLot(MaterialLotCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자재 로트(MATERIAL_LOT) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getMaterialLotId() == null || dto.getMaterialLotId().trim().isEmpty()) {
            throw new IllegalArgumentException("자재 로트 ID은(는) 필수 입력 항목입니다.");
        }

        String materialLotIdValue = dto.getMaterialLotId().trim();
        Optional<MaterialLot> existing = materialLotRepository.findByMaterialLotId(materialLotIdValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 자재 로트(MATERIAL_LOT)입니다: " + materialLotIdValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialLotCreated";
        }

        String eventUser;
        if (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) {
            eventUser = dto.getEventUser().trim();
        } else {
            eventUser = "SYSTEM";
        }

        String eventComment;
        if (dto.getEventComment() != null) {
            eventComment = dto.getEventComment().trim();
        } else {
            eventComment = "MaterialLot created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialLotId = null;
        if (dto.getMaterialLotId() != null) {
            materialLotId = dto.getMaterialLotId().trim();
        }

        String carrierName = null;
        if (dto.getCarrierName() != null) {
            carrierName = dto.getCarrierName().trim();
        }

        String state = null;
        if (dto.getState() != null) {
            state = dto.getState().trim();
        }

        MaterialLotCreateCommand command = MaterialLotCreateCommand.builder()
                .transactionInfo(tx)
                .materialLotId(materialLotId)
                .materialDefId(dto.getMaterialDefId())
                .parentMaterialLotId(dto.getParentMaterialLotId())
                .carrierName(carrierName)
                .quantity(dto.getQuantity())
                .state(state)
                .build();

        MaterialLot materialLot = MaterialLot.create(command);
        MaterialLot saved = materialLotRepository.save(materialLot);

        MaterialLotHistoryEntity historyEntity = materialLotMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("MaterialLot created successfully: [id={}]", saved.getId());

        return MaterialLotResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자재 로트(MATERIAL_LOT) 수정 (UPDATE) 및 MATERIAL_LOT_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialLotResponse updateMaterialLot(Long id, MaterialLotUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자재 로트(MATERIAL_LOT) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자재 로트(MATERIAL_LOT) 정보가 유효하지 않습니다.");
        }

        Optional<MaterialLot> optional = materialLotRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자재 로트(MATERIAL_LOT)이(가) 존재하지 않습니다. ID: " + id);
        }

        MaterialLot materialLot = optional.get();

        if (dto.getMaterialLotId() != null && !dto.getMaterialLotId().trim().isEmpty()) {
            String targetName = dto.getMaterialLotId().trim();
            if (!targetName.equals(materialLot.getMaterialLotId())) {
                Optional<MaterialLot> duplicate = materialLotRepository.findByMaterialLotId(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 자재 로트(MATERIAL_LOT)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialLotModified";
        }

        String eventUser;
        if (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) {
            eventUser = dto.getEventUser().trim();
        } else {
            eventUser = "SYSTEM";
        }

        String eventComment;
        if (dto.getEventComment() != null) {
            eventComment = dto.getEventComment().trim();
        } else {
            eventComment = "MaterialLot modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialLotId = null;
        if (dto.getMaterialLotId() != null) {
            materialLotId = dto.getMaterialLotId().trim();
        }

        String carrierName = null;
        if (dto.getCarrierName() != null) {
            carrierName = dto.getCarrierName().trim();
        }

        String state = null;
        if (dto.getState() != null) {
            state = dto.getState().trim();
        }

        MaterialLotUpdateCommand command = MaterialLotUpdateCommand.builder()
                .transactionInfo(tx)
                .materialLotId(materialLotId)
                .materialDefId(dto.getMaterialDefId())
                .parentMaterialLotId(dto.getParentMaterialLotId())
                .carrierName(carrierName)
                .quantity(dto.getQuantity())
                .state(state)
                .build();

        materialLot.update(command);
        MaterialLot updated = materialLotRepository.save(materialLot);

        MaterialLotHistoryEntity historyEntity = materialLotMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("MaterialLot updated successfully: [id={}]", updated.getId());

        return MaterialLotResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 자재 로트(MATERIAL_LOT) 단건 삭제 (DELETE) 및 MATERIAL_LOT_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialLot(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<MaterialLot> optional = materialLotRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자재 로트(MATERIAL_LOT)이(가) 존재하지 않습니다. ID: " + id);
        }

        MaterialLot materialLot = optional.get();

        LocalDateTime now = LocalDateTime.now();
        String user;
        if (eventUser != null && !eventUser.trim().isEmpty()) {
            user = eventUser.trim();
        } else {
            user = "SYSTEM";
        }

        String comment;
        if (eventComment != null && !eventComment.trim().isEmpty()) {
            comment = eventComment.trim();
        } else {
            comment = "MaterialLot deleted";
        }

        materialLot.setEventName("MaterialLotDeleted");
        materialLot.setEventTime(now);
        materialLot.setEventUser(user);
        materialLot.setEventComment(comment);

        MaterialLotHistoryEntity historyEntity = materialLotMapper.toHistoryEntity(materialLot);
        historyService.saveHistory(historyEntity);

        materialLotRepository.deleteById(id);

        log.info("MaterialLot deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 자재 로트(MATERIAL_LOT) 벌크 삭제 (DELETE batch) 및 MATERIAL_LOT_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialLots(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user;
        if (eventUser != null && !eventUser.trim().isEmpty()) {
            user = eventUser.trim();
        } else {
            user = "SYSTEM";
        }

        String comment;
        if (eventComment != null && !eventComment.trim().isEmpty()) {
            comment = eventComment.trim();
        } else {
            comment = "Batch MaterialLots deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<MaterialLot> optional = materialLotRepository.findById(id);
            if (optional.isPresent()) {
                MaterialLot materialLot = optional.get();
                materialLot.setEventName("MaterialLotDeleted");
                materialLot.setEventTime(now);
                materialLot.setEventUser(user);
                materialLot.setEventComment(comment);

                MaterialLotHistoryEntity historyEntity = materialLotMapper.toHistoryEntity(materialLot);
                historyService.saveHistory(historyEntity);
            }
        }

        materialLotRepository.deleteAllByIdInBatch(ids);
        log.info("MaterialLots batch deleted successfully: [count={}]", ids.size());
    }
}
