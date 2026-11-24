package kr.co.aim.api.service;

import kr.co.aim.api.dto.ZoneFallbackCreateRequestDto;
import kr.co.aim.api.dto.ZoneFallbackResponse;
import kr.co.aim.api.dto.ZoneFallbackUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ZoneFallbackCreateCommand;
import kr.co.aim.domain.command.ZoneFallbackUpdateCommand;
import kr.co.aim.domain.model.ZoneFallback;
import kr.co.aim.domain.repository.ZoneFallbackRepository;
import kr.co.aim.domain.repository.condition.ZoneFallbackSearchCondition;
import kr.co.aim.infra.persistence.entity.ZoneFallbackHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ZoneFallbackMapper;
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
public class ZoneFallbackService {

    private final ZoneFallbackRepository zoneFallbackRepository;
    private final HistoryService historyService;
    private final ZoneFallbackMapper zoneFallbackMapper;

    /**
     * 조건 및 페이징 기반 존 폴백(ZONE_FALLBACK) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<ZoneFallbackResponse> findZoneFallbacks(ZoneFallbackSearchCondition condition, Pageable pageable) {
        Page<ZoneFallback> pageResult = zoneFallbackRepository.findZoneFallbacksWithConditions(condition, pageable);
        List<ZoneFallbackResponse> content = new ArrayList<>();
        for (ZoneFallback item : pageResult.getContent()) {
            content.add(ZoneFallbackResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 존 폴백(ZONE_FALLBACK) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public ZoneFallbackResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 존 폴백(ZONE_FALLBACK) ID가 유효하지 않습니다.");
        }
        Optional<ZoneFallback> optional = zoneFallbackRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 존 폴백(ZONE_FALLBACK)이(가) 존재하지 않습니다. ID: " + id);
        }
        return ZoneFallbackResponse.fromDomain(optional.get());
    }

    /**
     * 신규 존 폴백(ZONE_FALLBACK) 등록 (CREATE) 및 ZONE_FALLBACK_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ZoneFallbackResponse createZoneFallback(ZoneFallbackCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("존 폴백(ZONE_FALLBACK) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getSourceZoneId() == null) {
            throw new IllegalArgumentException("원본 존 ID은(는) 필수 입력 항목입니다.");
        }
        if (dto.getTargetZoneId() == null) {
            throw new IllegalArgumentException("대상 존 ID은(는) 필수 입력 항목입니다.");
        }
        if (dto.getPriority() == null) {
            throw new IllegalArgumentException("우선순위은(는) 필수 입력 항목입니다.");
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ZoneFallbackCreated";
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
            eventComment = "ZoneFallback created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);



        ZoneFallbackCreateCommand command = ZoneFallbackCreateCommand.builder()
                .transactionInfo(tx)
                .sourceZoneId(dto.getSourceZoneId())
                .targetZoneId(dto.getTargetZoneId())
                .priority(dto.getPriority())
                .build();

        ZoneFallback zoneFallback = ZoneFallback.create(command);
        ZoneFallback saved = zoneFallbackRepository.save(zoneFallback);

        ZoneFallbackHistoryEntity historyEntity = zoneFallbackMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("ZoneFallback created successfully: [id={}]", saved.getId());

        return ZoneFallbackResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 존 폴백(ZONE_FALLBACK) 수정 (UPDATE) 및 ZONE_FALLBACK_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ZoneFallbackResponse updateZoneFallback(Long id, ZoneFallbackUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 존 폴백(ZONE_FALLBACK) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 존 폴백(ZONE_FALLBACK) 정보가 유효하지 않습니다.");
        }

        Optional<ZoneFallback> optional = zoneFallbackRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 존 폴백(ZONE_FALLBACK)이(가) 존재하지 않습니다. ID: " + id);
        }

        ZoneFallback zoneFallback = optional.get();

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ZoneFallbackModified";
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
            eventComment = "ZoneFallback modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);



        ZoneFallbackUpdateCommand command = ZoneFallbackUpdateCommand.builder()
                .transactionInfo(tx)
                .sourceZoneId(dto.getSourceZoneId())
                .targetZoneId(dto.getTargetZoneId())
                .priority(dto.getPriority())
                .build();

        zoneFallback.update(command);
        ZoneFallback updated = zoneFallbackRepository.save(zoneFallback);

        ZoneFallbackHistoryEntity historyEntity = zoneFallbackMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("ZoneFallback updated successfully: [id={}]", updated.getId());

        return ZoneFallbackResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 존 폴백(ZONE_FALLBACK) 단건 삭제 (DELETE) 및 ZONE_FALLBACK_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZoneFallback(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<ZoneFallback> optional = zoneFallbackRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 존 폴백(ZONE_FALLBACK)이(가) 존재하지 않습니다. ID: " + id);
        }

        ZoneFallback zoneFallback = optional.get();

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
            comment = "ZoneFallback deleted";
        }

        zoneFallback.setEventName("ZoneFallbackDeleted");
        zoneFallback.setEventTime(now);
        zoneFallback.setEventUser(user);
        zoneFallback.setEventComment(comment);

        ZoneFallbackHistoryEntity historyEntity = zoneFallbackMapper.toHistoryEntity(zoneFallback);
        historyService.saveHistory(historyEntity);

        zoneFallbackRepository.deleteById(id);

        log.info("ZoneFallback deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 존 폴백(ZONE_FALLBACK) 벌크 삭제 (DELETE batch) 및 ZONE_FALLBACK_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZoneFallbacks(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch ZoneFallbacks deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<ZoneFallback> optional = zoneFallbackRepository.findById(id);
            if (optional.isPresent()) {
                ZoneFallback zoneFallback = optional.get();
                zoneFallback.setEventName("ZoneFallbackDeleted");
                zoneFallback.setEventTime(now);
                zoneFallback.setEventUser(user);
                zoneFallback.setEventComment(comment);

                ZoneFallbackHistoryEntity historyEntity = zoneFallbackMapper.toHistoryEntity(zoneFallback);
                historyService.saveHistory(historyEntity);
            }
        }

        zoneFallbackRepository.deleteAllByIdInBatch(ids);
        log.info("ZoneFallbacks batch deleted successfully: [count={}]", ids.size());
    }
}
