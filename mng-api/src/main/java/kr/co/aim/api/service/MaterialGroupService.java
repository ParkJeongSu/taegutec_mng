package kr.co.aim.api.service;

import kr.co.aim.api.dto.MaterialGroupCreateRequestDto;
import kr.co.aim.api.dto.MaterialGroupResponse;
import kr.co.aim.api.dto.MaterialGroupUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.MaterialGroupCreateCommand;
import kr.co.aim.domain.command.MaterialGroupUpdateCommand;
import kr.co.aim.domain.model.MaterialGroup;
import kr.co.aim.domain.repository.MaterialGroupRepository;
import kr.co.aim.domain.repository.condition.MaterialGroupSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialGroupHistoryEntity;
import kr.co.aim.infra.persistence.mapper.MaterialGroupMapper;
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
public class MaterialGroupService {

    private final MaterialGroupRepository materialGroupRepository;
    private final HistoryService historyService;
    private final MaterialGroupMapper materialGroupMapper;

    /**
     * 조건 및 페이징 기반 자재 그룹(MATERIAL_GROUP) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MaterialGroupResponse> findMaterialGroups(MaterialGroupSearchCondition condition, Pageable pageable) {
        Page<MaterialGroup> pageResult = materialGroupRepository.findMaterialGroupsWithConditions(condition, pageable);
        List<MaterialGroupResponse> content = new ArrayList<>();
        for (MaterialGroup item : pageResult.getContent()) {
            content.add(MaterialGroupResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자재 그룹(MATERIAL_GROUP) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public MaterialGroupResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자재 그룹(MATERIAL_GROUP) ID가 유효하지 않습니다.");
        }
        Optional<MaterialGroup> optional = materialGroupRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 자재 그룹(MATERIAL_GROUP)이(가) 존재하지 않습니다. ID: " + id);
        }
        return MaterialGroupResponse.fromDomain(optional.get());
    }

    /**
     * 신규 자재 그룹(MATERIAL_GROUP) 등록 (CREATE) 및 MATERIAL_GROUP_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialGroupResponse createMaterialGroup(MaterialGroupCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자재 그룹(MATERIAL_GROUP) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getMaterialGroupDefName() == null || dto.getMaterialGroupDefName().trim().isEmpty()) {
            throw new IllegalArgumentException("자재 그룹 정의명은(는) 필수 입력 항목입니다.");
        }

        String materialGroupDefNameValue = dto.getMaterialGroupDefName().trim();
        Optional<MaterialGroup> existing = materialGroupRepository.findByMaterialGroupDefName(materialGroupDefNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 자재 그룹(MATERIAL_GROUP)입니다: " + materialGroupDefNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialGroupCreated";
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
            eventComment = "MaterialGroup created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialGroupDefName = null;
        if (dto.getMaterialGroupDefName() != null) {
            materialGroupDefName = dto.getMaterialGroupDefName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        MaterialGroupCreateCommand command = MaterialGroupCreateCommand.builder()
                .transactionInfo(tx)
                .materialGroupDefName(materialGroupDefName)
                .description(description)
                .build();

        MaterialGroup materialGroup = MaterialGroup.create(command);
        MaterialGroup saved = materialGroupRepository.save(materialGroup);

        MaterialGroupHistoryEntity historyEntity = materialGroupMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("MaterialGroup created successfully: [id={}]", saved.getId());

        return MaterialGroupResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자재 그룹(MATERIAL_GROUP) 수정 (UPDATE) 및 MATERIAL_GROUP_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MaterialGroupResponse updateMaterialGroup(Long id, MaterialGroupUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자재 그룹(MATERIAL_GROUP) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자재 그룹(MATERIAL_GROUP) 정보가 유효하지 않습니다.");
        }

        Optional<MaterialGroup> optional = materialGroupRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자재 그룹(MATERIAL_GROUP)이(가) 존재하지 않습니다. ID: " + id);
        }

        MaterialGroup materialGroup = optional.get();

        if (dto.getMaterialGroupDefName() != null && !dto.getMaterialGroupDefName().trim().isEmpty()) {
            String targetName = dto.getMaterialGroupDefName().trim();
            if (!targetName.equals(materialGroup.getMaterialGroupDefName())) {
                Optional<MaterialGroup> duplicate = materialGroupRepository.findByMaterialGroupDefName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 자재 그룹(MATERIAL_GROUP)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "MaterialGroupModified";
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
            eventComment = "MaterialGroup modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String materialGroupDefName = null;
        if (dto.getMaterialGroupDefName() != null) {
            materialGroupDefName = dto.getMaterialGroupDefName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        MaterialGroupUpdateCommand command = MaterialGroupUpdateCommand.builder()
                .transactionInfo(tx)
                .materialGroupDefName(materialGroupDefName)
                .description(description)
                .build();

        materialGroup.update(command);
        MaterialGroup updated = materialGroupRepository.save(materialGroup);

        MaterialGroupHistoryEntity historyEntity = materialGroupMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("MaterialGroup updated successfully: [id={}]", updated.getId());

        return MaterialGroupResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 자재 그룹(MATERIAL_GROUP) 단건 삭제 (DELETE) 및 MATERIAL_GROUP_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialGroup(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<MaterialGroup> optional = materialGroupRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자재 그룹(MATERIAL_GROUP)이(가) 존재하지 않습니다. ID: " + id);
        }

        MaterialGroup materialGroup = optional.get();

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
            comment = "MaterialGroup deleted";
        }

        materialGroup.setEventName("MaterialGroupDeleted");
        materialGroup.setEventTime(now);
        materialGroup.setEventUser(user);
        materialGroup.setEventComment(comment);

        MaterialGroupHistoryEntity historyEntity = materialGroupMapper.toHistoryEntity(materialGroup);
        historyService.saveHistory(historyEntity);

        materialGroupRepository.deleteById(id);

        log.info("MaterialGroup deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 자재 그룹(MATERIAL_GROUP) 벌크 삭제 (DELETE batch) 및 MATERIAL_GROUP_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMaterialGroups(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch MaterialGroups deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<MaterialGroup> optional = materialGroupRepository.findById(id);
            if (optional.isPresent()) {
                MaterialGroup materialGroup = optional.get();
                materialGroup.setEventName("MaterialGroupDeleted");
                materialGroup.setEventTime(now);
                materialGroup.setEventUser(user);
                materialGroup.setEventComment(comment);

                MaterialGroupHistoryEntity historyEntity = materialGroupMapper.toHistoryEntity(materialGroup);
                historyService.saveHistory(historyEntity);
            }
        }

        materialGroupRepository.deleteAllByIdInBatch(ids);
        log.info("MaterialGroups batch deleted successfully: [count={}]", ids.size());
    }
}
