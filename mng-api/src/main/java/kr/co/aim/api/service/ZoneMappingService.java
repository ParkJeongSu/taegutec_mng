package kr.co.aim.api.service;

import kr.co.aim.api.dto.ZoneMappingCreateRequestDto;
import kr.co.aim.api.dto.ZoneMappingResponse;
import kr.co.aim.api.dto.ZoneMappingUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ZoneMappingCreateCommand;
import kr.co.aim.domain.command.ZoneMappingUpdateCommand;
import kr.co.aim.domain.model.ZoneMapping;
import kr.co.aim.domain.repository.ZoneMappingRepository;
import kr.co.aim.domain.repository.condition.ZoneMappingSearchCondition;
import kr.co.aim.infra.persistence.entity.ZoneMappingHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ZoneMappingMapper;
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
public class ZoneMappingService {

    private final ZoneMappingRepository zoneMappingRepository;
    private final HistoryService historyService;
    private final ZoneMappingMapper zoneMappingMapper;

    /**
     * 조건 및 페이징 기반 존 매핑(ZONE_MAPPING) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<ZoneMappingResponse> findZoneMappings(ZoneMappingSearchCondition condition, Pageable pageable) {
        Page<ZoneMapping> pageResult = zoneMappingRepository.findZoneMappingsWithConditions(condition, pageable);
        List<ZoneMappingResponse> content = new ArrayList<>();
        for (ZoneMapping item : pageResult.getContent()) {
            content.add(ZoneMappingResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 존 매핑(ZONE_MAPPING) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public ZoneMappingResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 존 매핑(ZONE_MAPPING) ID가 유효하지 않습니다.");
        }
        Optional<ZoneMapping> optional = zoneMappingRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 존 매핑(ZONE_MAPPING)이(가) 존재하지 않습니다. ID: " + id);
        }
        return ZoneMappingResponse.fromDomain(optional.get());
    }

    /**
     * 신규 존 매핑(ZONE_MAPPING) 등록 (CREATE) 및 ZONE_MAPPING_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ZoneMappingResponse createZoneMapping(ZoneMappingCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("존 매핑(ZONE_MAPPING) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getZoneId() == null) {
            throw new IllegalArgumentException("존 ID은(는) 필수 입력 항목입니다.");
        }
        if (dto.getOrderLineNumber() == null || dto.getOrderLineNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("오더 라인 번호은(는) 필수 입력 항목입니다.");
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ZoneMappingCreated";
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
            eventComment = "ZoneMapping created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String orderLineNumber = null;
        if (dto.getOrderLineNumber() != null) {
            orderLineNumber = dto.getOrderLineNumber().trim();
        }

        ZoneMappingCreateCommand command = ZoneMappingCreateCommand.builder()
                .transactionInfo(tx)
                .zoneId(dto.getZoneId())
                .orderLineNumber(orderLineNumber)
                .build();

        ZoneMapping zoneMapping = ZoneMapping.create(command);
        ZoneMapping saved = zoneMappingRepository.save(zoneMapping);

        ZoneMappingHistoryEntity historyEntity = zoneMappingMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("ZoneMapping created successfully: [id={}]", saved.getId());

        return ZoneMappingResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 존 매핑(ZONE_MAPPING) 수정 (UPDATE) 및 ZONE_MAPPING_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ZoneMappingResponse updateZoneMapping(Long id, ZoneMappingUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 존 매핑(ZONE_MAPPING) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 존 매핑(ZONE_MAPPING) 정보가 유효하지 않습니다.");
        }

        Optional<ZoneMapping> optional = zoneMappingRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 존 매핑(ZONE_MAPPING)이(가) 존재하지 않습니다. ID: " + id);
        }

        ZoneMapping zoneMapping = optional.get();

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ZoneMappingModified";
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
            eventComment = "ZoneMapping modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String orderLineNumber = null;
        if (dto.getOrderLineNumber() != null) {
            orderLineNumber = dto.getOrderLineNumber().trim();
        }

        ZoneMappingUpdateCommand command = ZoneMappingUpdateCommand.builder()
                .transactionInfo(tx)
                .zoneId(dto.getZoneId())
                .orderLineNumber(orderLineNumber)
                .build();

        zoneMapping.update(command);
        ZoneMapping updated = zoneMappingRepository.save(zoneMapping);

        ZoneMappingHistoryEntity historyEntity = zoneMappingMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("ZoneMapping updated successfully: [id={}]", updated.getId());

        return ZoneMappingResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 존 매핑(ZONE_MAPPING) 단건 삭제 (DELETE) 및 ZONE_MAPPING_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZoneMapping(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<ZoneMapping> optional = zoneMappingRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 존 매핑(ZONE_MAPPING)이(가) 존재하지 않습니다. ID: " + id);
        }

        ZoneMapping zoneMapping = optional.get();

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
            comment = "ZoneMapping deleted";
        }

        zoneMapping.setEventName("ZoneMappingDeleted");
        zoneMapping.setEventTime(now);
        zoneMapping.setEventUser(user);
        zoneMapping.setEventComment(comment);

        ZoneMappingHistoryEntity historyEntity = zoneMappingMapper.toHistoryEntity(zoneMapping);
        historyService.saveHistory(historyEntity);

        zoneMappingRepository.deleteById(id);

        log.info("ZoneMapping deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 존 매핑(ZONE_MAPPING) 벌크 삭제 (DELETE batch) 및 ZONE_MAPPING_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZoneMappings(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch ZoneMappings deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<ZoneMapping> optional = zoneMappingRepository.findById(id);
            if (optional.isPresent()) {
                ZoneMapping zoneMapping = optional.get();
                zoneMapping.setEventName("ZoneMappingDeleted");
                zoneMapping.setEventTime(now);
                zoneMapping.setEventUser(user);
                zoneMapping.setEventComment(comment);

                ZoneMappingHistoryEntity historyEntity = zoneMappingMapper.toHistoryEntity(zoneMapping);
                historyService.saveHistory(historyEntity);
            }
        }

        zoneMappingRepository.deleteAllByIdInBatch(ids);
        log.info("ZoneMappings batch deleted successfully: [count={}]", ids.size());
    }
}
