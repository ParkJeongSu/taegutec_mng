package kr.co.aim.api.service;

import kr.co.aim.api.dto.MaterialCreateRequestDto;
import kr.co.aim.api.dto.MaterialResponse;
import kr.co.aim.api.dto.MaterialUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.MaterialCreateCommand;
import kr.co.aim.domain.command.MaterialUpdateCommand;
import kr.co.aim.domain.model.Material;
import kr.co.aim.domain.repository.MaterialRepository;
import kr.co.aim.domain.repository.condition.MaterialSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialHistoryEntity;
import kr.co.aim.infra.persistence.mapper.MaterialMapper;
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
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final HistoryService historyService;
    private final MaterialMapper materialMapper;

    /**
     * 조건 및 페이징 기반 자재(MATERIAL) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MaterialResponse> findMaterials(MaterialSearchCondition condition, Pageable pageable) {
        Page<Material> pageResult = materialRepository.findMaterialsWithConditions(condition, pageable);
        List<MaterialResponse> content = new ArrayList<>();
        for (Material item : pageResult.getContent()) {
            content.add(MaterialResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자재(MATERIAL) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public MaterialResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자재(MATERIAL) ID가 유효하지 않습니다.");
        }
        Optional<Material> optional = materialRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 자재(MATERIAL)이(가) 존재하지 않습니다. ID: " + id);
        }
        return MaterialResponse.fromDomain(optional.get());
    }

    /**
     * 신규 자재(MATERIAL) 등록 (CREATE) 및 MATERIAL_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialResponse createMaterial(MaterialCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자재(MATERIAL) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getMaterialId() == null || dto.getMaterialId().trim().isEmpty()) {
            throw new IllegalArgumentException("자재 ID은(는) 필수 입력 항목입니다.");
        }

        String materialIdValue = dto.getMaterialId().trim();
        Optional<Material> existing = materialRepository.findByMaterialId(materialIdValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 자재(MATERIAL)입니다: " + materialIdValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialCreated";
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
            eventComment = "Material created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialId = null;
        if (dto.getMaterialId() != null) {
            materialId = dto.getMaterialId().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        String carrierName = null;
        if (dto.getCarrierName() != null) {
            carrierName = dto.getCarrierName().trim();
        }

        MaterialCreateCommand command = MaterialCreateCommand.builder()
                .transactionInfo(tx)
                .materialId(materialId)
                .description(description)
                .materialClassId(dto.getMaterialClassId())
                .materialGroupId(dto.getMaterialGroupId())
                .carrierName(carrierName)
                .quantity(dto.getQuantity())
                .build();

        Material material = Material.create(command);
        Material saved = materialRepository.save(material);

        MaterialHistoryEntity historyEntity = materialMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("Material created successfully: [id={}]", saved.getId());

        return MaterialResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자재(MATERIAL) 수정 (UPDATE) 및 MATERIAL_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialResponse updateMaterial(Long id, MaterialUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자재(MATERIAL) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자재(MATERIAL) 정보가 유효하지 않습니다.");
        }

        Optional<Material> optional = materialRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자재(MATERIAL)이(가) 존재하지 않습니다. ID: " + id);
        }

        Material material = optional.get();

        if (dto.getMaterialId() != null && !dto.getMaterialId().trim().isEmpty()) {
            String targetName = dto.getMaterialId().trim();
            if (!targetName.equals(material.getMaterialId())) {
                Optional<Material> duplicate = materialRepository.findByMaterialId(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 자재(MATERIAL)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialModified";
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
            eventComment = "Material modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialId = null;
        if (dto.getMaterialId() != null) {
            materialId = dto.getMaterialId().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        String carrierName = null;
        if (dto.getCarrierName() != null) {
            carrierName = dto.getCarrierName().trim();
        }

        MaterialUpdateCommand command = MaterialUpdateCommand.builder()
                .transactionInfo(tx)
                .materialId(materialId)
                .description(description)
                .materialClassId(dto.getMaterialClassId())
                .materialGroupId(dto.getMaterialGroupId())
                .carrierName(carrierName)
                .quantity(dto.getQuantity())
                .build();

        material.update(command);
        Material updated = materialRepository.save(material);

        MaterialHistoryEntity historyEntity = materialMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("Material updated successfully: [id={}]", updated.getId());

        return MaterialResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 자재(MATERIAL) 단건 삭제 (DELETE) 및 MATERIAL_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterial(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Material> optional = materialRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자재(MATERIAL)이(가) 존재하지 않습니다. ID: " + id);
        }

        Material material = optional.get();

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
            comment = "Material deleted";
        }

        material.setEventName("MaterialDeleted");
        material.setEventTime(now);
        material.setEventUser(user);
        material.setEventComment(comment);

        MaterialHistoryEntity historyEntity = materialMapper.toHistoryEntity(material);
        historyService.saveHistory(historyEntity);

        materialRepository.deleteById(id);

        log.info("Material deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 자재(MATERIAL) 벌크 삭제 (DELETE batch) 및 MATERIAL_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterials(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch Materials deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Material> optional = materialRepository.findById(id);
            if (optional.isPresent()) {
                Material material = optional.get();
                material.setEventName("MaterialDeleted");
                material.setEventTime(now);
                material.setEventUser(user);
                material.setEventComment(comment);

                MaterialHistoryEntity historyEntity = materialMapper.toHistoryEntity(material);
                historyService.saveHistory(historyEntity);
            }
        }

        materialRepository.deleteAllByIdInBatch(ids);
        log.info("Materials batch deleted successfully: [count={}]", ids.size());
    }
}
