package kr.co.aim.api.service;

import kr.co.aim.api.dto.MaterialClassDefCreateRequestDto;
import kr.co.aim.api.dto.MaterialClassDefResponse;
import kr.co.aim.api.dto.MaterialClassDefUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.MaterialClassDefCreateCommand;
import kr.co.aim.domain.command.MaterialClassDefUpdateCommand;
import kr.co.aim.domain.model.MaterialClassDef;
import kr.co.aim.domain.repository.MaterialClassDefRepository;
import kr.co.aim.domain.repository.condition.MaterialClassDefSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialClassDefHistoryEntity;
import kr.co.aim.infra.persistence.mapper.MaterialClassDefMapper;
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
public class MaterialClassDefService {

    private final MaterialClassDefRepository materialClassDefRepository;
    private final HistoryService historyService;
    private final MaterialClassDefMapper materialClassDefMapper;

    /**
     * 조건 및 페이징 기반 자재 분류 정의 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MaterialClassDefResponse> findMaterialClassDefs(MaterialClassDefSearchCondition condition, Pageable pageable) {
        Page<MaterialClassDef> pageResult = materialClassDefRepository.findMaterialClassDefsWithConditions(condition, pageable);
        List<MaterialClassDefResponse> content = new ArrayList<>();
        for (MaterialClassDef materialClassDef : pageResult.getContent()) {
            content.add(MaterialClassDefResponse.fromDomain(materialClassDef));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자재 분류 정의 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public MaterialClassDefResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자재 분류 정의 ID가 유효하지 않습니다.");
        }
        Optional<MaterialClassDef> optionalDef = materialClassDefRepository.findById(id);
        if (optionalDef.isEmpty()) {
            throw new IllegalArgumentException("해당 자재 분류 정의가 존재하지 않습니다. ID: " + id);
        }
        return MaterialClassDefResponse.fromDomain(optionalDef.get());
    }

    /**
     * 신규 자재 분류 정의 등록 (CREATE)
     * - MATERIAL_CLASS_DEF_NAME 중복 검증
     * - MATERIAL_CLASS_DEF_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialClassDefResponse createMaterialClassDef(MaterialClassDefCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자재 분류 정의 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getMaterialClassDefName() == null || dto.getMaterialClassDefName().trim().isEmpty()) {
            throw new IllegalArgumentException("자재 분류 정의명은 필수 입력 항목입니다.");
        }

        String materialClassDefName = dto.getMaterialClassDefName().trim();

        Optional<MaterialClassDef> existing = materialClassDefRepository.findByMaterialClassDefName(materialClassDefName);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 자재 분류 정의명입니다: " + materialClassDefName);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialClassDefCreated";
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
            eventComment = "Material class def created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialClassType = null;
        if (dto.getMaterialClassType() != null) {
            materialClassType = dto.getMaterialClassType().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        MaterialClassDefCreateCommand command = MaterialClassDefCreateCommand.builder()
                .transactionInfo(tx)
                .materialClassDefName(materialClassDefName)
                .materialClassType(materialClassType)
                .parentClassId(dto.getParentClassId())
                .classLevel(dto.getClassLevel())
                .description(description)
                .build();

        MaterialClassDef materialClassDef = MaterialClassDef.create(command);
        MaterialClassDef saved = materialClassDefRepository.save(materialClassDef);

        MaterialClassDefHistoryEntity historyEntity = materialClassDefMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("MaterialClassDef created successfully: [id={}, materialClassDefName={}]", saved.getId(), saved.getMaterialClassDefName());

        return MaterialClassDefResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자재 분류 정의 수정 (UPDATE)
     * - MATERIAL_CLASS_DEF_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialClassDefResponse updateMaterialClassDef(Long id, MaterialClassDefUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자재 분류 정의 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자재 분류 정의 정보가 유효하지 않습니다.");
        }

        Optional<MaterialClassDef> optionalDef = materialClassDefRepository.findById(id);
        if (optionalDef.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자재 분류 정의가 존재하지 않습니다. ID: " + id);
        }

        MaterialClassDef materialClassDef = optionalDef.get();

        String targetDefName = null;
        if (dto.getMaterialClassDefName() != null && !dto.getMaterialClassDefName().trim().isEmpty()) {
            targetDefName = dto.getMaterialClassDefName().trim();
            if (!targetDefName.equals(materialClassDef.getMaterialClassDefName())) {
                Optional<MaterialClassDef> duplicate = materialClassDefRepository.findByMaterialClassDefName(targetDefName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 자재 분류 정의명입니다: " + targetDefName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialClassDefModified";
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
            eventComment = "Material class def modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialClassType = null;
        if (dto.getMaterialClassType() != null) {
            materialClassType = dto.getMaterialClassType().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        MaterialClassDefUpdateCommand command = MaterialClassDefUpdateCommand.builder()
                .transactionInfo(tx)
                .materialClassDefName(targetDefName)
                .materialClassType(materialClassType)
                .parentClassId(dto.getParentClassId())
                .classLevel(dto.getClassLevel())
                .description(description)
                .build();

        materialClassDef.update(command);
        MaterialClassDef updated = materialClassDefRepository.save(materialClassDef);

        MaterialClassDefHistoryEntity historyEntity = materialClassDefMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("MaterialClassDef updated successfully: [id={}, materialClassDefName={}]", updated.getId(), updated.getMaterialClassDefName());

        return MaterialClassDefResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 단건 자재 분류 정의 삭제 (DELETE)
     * - MATERIAL_CLASS_DEF_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialClassDef(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<MaterialClassDef> optionalDef = materialClassDefRepository.findById(id);
        if (optionalDef.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자재 분류 정의가 존재하지 않습니다. ID: " + id);
        }

        MaterialClassDef materialClassDef = optionalDef.get();

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
            comment = "Material class def deleted";
        }

        materialClassDef.setEventName("MaterialClassDefDeleted");
        materialClassDef.setEventTime(now);
        materialClassDef.setEventUser(user);
        materialClassDef.setEventComment(comment);

        MaterialClassDefHistoryEntity historyEntity = materialClassDefMapper.toHistoryEntity(materialClassDef);
        historyService.saveHistory(historyEntity);

        materialClassDefRepository.deleteById(id);

        log.info("MaterialClassDef deleted successfully: [id={}, materialClassDefName={}]", id, materialClassDef.getMaterialClassDefName());
    }

    /**
     * TSID(ID) 목록 기반 복수 자재 분류 정의 벌크 삭제 (DELETE batch)
     * - 각 대상에 대한 삭제 이력 적재 (명시적 for 루프 사용)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialClassDefs(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch material class defs deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<MaterialClassDef> optionalDef = materialClassDefRepository.findById(id);
            if (optionalDef.isPresent()) {
                MaterialClassDef materialClassDef = optionalDef.get();
                materialClassDef.setEventName("MaterialClassDefDeleted");
                materialClassDef.setEventTime(now);
                materialClassDef.setEventUser(user);
                materialClassDef.setEventComment(comment);

                MaterialClassDefHistoryEntity historyEntity = materialClassDefMapper.toHistoryEntity(materialClassDef);
                historyService.saveHistory(historyEntity);
            }
        }

        materialClassDefRepository.deleteAllByIdInBatch(ids);
        log.info("MaterialClassDefs batch deleted successfully: [count={}]", ids.size());
    }
}
