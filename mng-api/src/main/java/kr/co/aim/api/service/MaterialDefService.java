package kr.co.aim.api.service;

import kr.co.aim.api.dto.MaterialDefCreateRequestDto;
import kr.co.aim.api.dto.MaterialDefResponse;
import kr.co.aim.api.dto.MaterialDefUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.MaterialDefCreateCommand;
import kr.co.aim.domain.command.MaterialDefUpdateCommand;
import kr.co.aim.domain.model.MaterialDef;
import kr.co.aim.domain.repository.MaterialDefRepository;
import kr.co.aim.domain.repository.condition.MaterialDefSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialDefHistoryEntity;
import kr.co.aim.infra.persistence.mapper.MaterialDefMapper;
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
public class MaterialDefService {

    private final MaterialDefRepository materialDefRepository;
    private final HistoryService historyService;
    private final MaterialDefMapper materialDefMapper;

    /**
     * 조건 및 페이징 기반 자재 정의(MATERIAL_DEF) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MaterialDefResponse> findMaterialDefs(MaterialDefSearchCondition condition, Pageable pageable) {
        Page<MaterialDef> pageResult = materialDefRepository.findMaterialDefsWithConditions(condition, pageable);
        List<MaterialDefResponse> content = new ArrayList<>();
        for (MaterialDef item : pageResult.getContent()) {
            content.add(MaterialDefResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자재 정의(MATERIAL_DEF) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public MaterialDefResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자재 정의(MATERIAL_DEF) ID가 유효하지 않습니다.");
        }
        Optional<MaterialDef> optional = materialDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 자재 정의(MATERIAL_DEF)이(가) 존재하지 않습니다. ID: " + id);
        }
        return MaterialDefResponse.fromDomain(optional.get());
    }

    /**
     * 신규 자재 정의(MATERIAL_DEF) 등록 (CREATE) 및 MATERIAL_DEF_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialDefResponse createMaterialDef(MaterialDefCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자재 정의(MATERIAL_DEF) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getMaterialDefName() == null || dto.getMaterialDefName().trim().isEmpty()) {
            throw new IllegalArgumentException("자재 정의명은(는) 필수 입력 항목입니다.");
        }

        String materialDefNameValue = dto.getMaterialDefName().trim();
        Optional<MaterialDef> existing = materialDefRepository.findByMaterialDefName(materialDefNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 자재 정의(MATERIAL_DEF)입니다: " + materialDefNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialDefCreated";
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
            eventComment = "MaterialDef created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialDefName = null;
        if (dto.getMaterialDefName() != null) {
            materialDefName = dto.getMaterialDefName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        String unit = null;
        if (dto.getUnit() != null) {
            unit = dto.getUnit().trim();
        }

        String isSerialTracked = null;
        if (dto.getIsSerialTracked() != null) {
            isSerialTracked = dto.getIsSerialTracked().trim();
        }

        MaterialDefCreateCommand command = MaterialDefCreateCommand.builder()
                .transactionInfo(tx)
                .materialDefName(materialDefName)
                .description(description)
                .materialClassId(dto.getMaterialClassId())
                .materialGroupId(dto.getMaterialGroupId())
                .unit(unit)
                .isSerialTracked(isSerialTracked)
                .build();

        MaterialDef materialDef = MaterialDef.create(command);
        MaterialDef saved = materialDefRepository.save(materialDef);

        MaterialDefHistoryEntity historyEntity = materialDefMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("MaterialDef created successfully: [id={}]", saved.getId());

        return MaterialDefResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자재 정의(MATERIAL_DEF) 수정 (UPDATE) 및 MATERIAL_DEF_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialDefResponse updateMaterialDef(Long id, MaterialDefUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자재 정의(MATERIAL_DEF) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자재 정의(MATERIAL_DEF) 정보가 유효하지 않습니다.");
        }

        Optional<MaterialDef> optional = materialDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자재 정의(MATERIAL_DEF)이(가) 존재하지 않습니다. ID: " + id);
        }

        MaterialDef materialDef = optional.get();

        if (dto.getMaterialDefName() != null && !dto.getMaterialDefName().trim().isEmpty()) {
            String targetName = dto.getMaterialDefName().trim();
            if (!targetName.equals(materialDef.getMaterialDefName())) {
                Optional<MaterialDef> duplicate = materialDefRepository.findByMaterialDefName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 자재 정의(MATERIAL_DEF)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialDefModified";
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
            eventComment = "MaterialDef modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialDefName = null;
        if (dto.getMaterialDefName() != null) {
            materialDefName = dto.getMaterialDefName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        String unit = null;
        if (dto.getUnit() != null) {
            unit = dto.getUnit().trim();
        }

        String isSerialTracked = null;
        if (dto.getIsSerialTracked() != null) {
            isSerialTracked = dto.getIsSerialTracked().trim();
        }

        MaterialDefUpdateCommand command = MaterialDefUpdateCommand.builder()
                .transactionInfo(tx)
                .materialDefName(materialDefName)
                .description(description)
                .materialClassId(dto.getMaterialClassId())
                .materialGroupId(dto.getMaterialGroupId())
                .unit(unit)
                .isSerialTracked(isSerialTracked)
                .build();

        materialDef.update(command);
        MaterialDef updated = materialDefRepository.save(materialDef);

        MaterialDefHistoryEntity historyEntity = materialDefMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("MaterialDef updated successfully: [id={}]", updated.getId());

        return MaterialDefResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 자재 정의(MATERIAL_DEF) 단건 삭제 (DELETE) 및 MATERIAL_DEF_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialDef(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<MaterialDef> optional = materialDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자재 정의(MATERIAL_DEF)이(가) 존재하지 않습니다. ID: " + id);
        }

        MaterialDef materialDef = optional.get();

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
            comment = "MaterialDef deleted";
        }

        materialDef.setEventName("MaterialDefDeleted");
        materialDef.setEventTime(now);
        materialDef.setEventUser(user);
        materialDef.setEventComment(comment);

        MaterialDefHistoryEntity historyEntity = materialDefMapper.toHistoryEntity(materialDef);
        historyService.saveHistory(historyEntity);

        materialDefRepository.deleteById(id);

        log.info("MaterialDef deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 자재 정의(MATERIAL_DEF) 벌크 삭제 (DELETE batch) 및 MATERIAL_DEF_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialDefs(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch MaterialDefs deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<MaterialDef> optional = materialDefRepository.findById(id);
            if (optional.isPresent()) {
                MaterialDef materialDef = optional.get();
                materialDef.setEventName("MaterialDefDeleted");
                materialDef.setEventTime(now);
                materialDef.setEventUser(user);
                materialDef.setEventComment(comment);

                MaterialDefHistoryEntity historyEntity = materialDefMapper.toHistoryEntity(materialDef);
                historyService.saveHistory(historyEntity);
            }
        }

        materialDefRepository.deleteAllByIdInBatch(ids);
        log.info("MaterialDefs batch deleted successfully: [count={}]", ids.size());
    }
}
