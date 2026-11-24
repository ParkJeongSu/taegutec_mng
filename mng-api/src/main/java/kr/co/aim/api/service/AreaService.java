package kr.co.aim.api.service;

import kr.co.aim.api.dto.AreaCreateRequestDto;
import kr.co.aim.api.dto.AreaResponse;
import kr.co.aim.api.dto.AreaUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.AreaCreateCommand;
import kr.co.aim.domain.command.AreaUpdateCommand;
import kr.co.aim.domain.model.Area;
import kr.co.aim.domain.repository.AreaRepository;
import kr.co.aim.domain.repository.condition.AreaSearchCondition;
import kr.co.aim.infra.persistence.entity.AreaHistoryEntity;
import kr.co.aim.infra.persistence.mapper.AreaMapper;
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
public class AreaService {

    private final AreaRepository areaRepository;
    private final HistoryService historyService;
    private final AreaMapper areaMapper;

    /**
     * 조건 및 페이징 기반 구역(AREA) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<AreaResponse> findAreas(AreaSearchCondition condition, Pageable pageable) {
        Page<Area> pageResult = areaRepository.findAreasWithConditions(condition, pageable);
        List<AreaResponse> content = new ArrayList<>();
        for (Area item : pageResult.getContent()) {
            content.add(AreaResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 구역(AREA) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public AreaResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 구역(AREA) ID가 유효하지 않습니다.");
        }
        Optional<Area> optional = areaRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 구역(AREA)이(가) 존재하지 않습니다. ID: " + id);
        }
        return AreaResponse.fromDomain(optional.get());
    }

    /**
     * 신규 구역(AREA) 등록 (CREATE) 및 AREA_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public AreaResponse createArea(AreaCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("구역(AREA) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getAreaName() == null || dto.getAreaName().trim().isEmpty()) {
            throw new IllegalArgumentException("구역명은(는) 필수 입력 항목입니다.");
        }

        String areaNameValue = dto.getAreaName().trim();
        Optional<Area> existing = areaRepository.findByAreaName(areaNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 구역(AREA)입니다: " + areaNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "AreaCreated";
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
            eventComment = "Area created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String areaName = null;
        if (dto.getAreaName() != null) {
            areaName = dto.getAreaName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        AreaCreateCommand command = AreaCreateCommand.builder()
                .transactionInfo(tx)
                .areaName(areaName)
                .description(description)
                .build();

        Area area = Area.create(command);
        Area saved = areaRepository.save(area);

        AreaHistoryEntity historyEntity = areaMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("Area created successfully: [id={}]", saved.getId());

        return AreaResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 구역(AREA) 수정 (UPDATE) 및 AREA_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public AreaResponse updateArea(Long id, AreaUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 구역(AREA) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 구역(AREA) 정보가 유효하지 않습니다.");
        }

        Optional<Area> optional = areaRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 구역(AREA)이(가) 존재하지 않습니다. ID: " + id);
        }

        Area area = optional.get();

        if (dto.getAreaName() != null && !dto.getAreaName().trim().isEmpty()) {
            String targetName = dto.getAreaName().trim();
            if (!targetName.equals(area.getAreaName())) {
                Optional<Area> duplicate = areaRepository.findByAreaName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 구역(AREA)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "AreaModified";
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
            eventComment = "Area modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String areaName = null;
        if (dto.getAreaName() != null) {
            areaName = dto.getAreaName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        AreaUpdateCommand command = AreaUpdateCommand.builder()
                .transactionInfo(tx)
                .areaName(areaName)
                .description(description)
                .build();

        area.update(command);
        Area updated = areaRepository.save(area);

        AreaHistoryEntity historyEntity = areaMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("Area updated successfully: [id={}]", updated.getId());

        return AreaResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 구역(AREA) 단건 삭제 (DELETE) 및 AREA_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteArea(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Area> optional = areaRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 구역(AREA)이(가) 존재하지 않습니다. ID: " + id);
        }

        Area area = optional.get();

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
            comment = "Area deleted";
        }

        area.setEventName("AreaDeleted");
        area.setEventTime(now);
        area.setEventUser(user);
        area.setEventComment(comment);

        AreaHistoryEntity historyEntity = areaMapper.toHistoryEntity(area);
        historyService.saveHistory(historyEntity);

        areaRepository.deleteById(id);

        log.info("Area deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 구역(AREA) 벌크 삭제 (DELETE batch) 및 AREA_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAreas(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch Areas deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Area> optional = areaRepository.findById(id);
            if (optional.isPresent()) {
                Area area = optional.get();
                area.setEventName("AreaDeleted");
                area.setEventTime(now);
                area.setEventUser(user);
                area.setEventComment(comment);

                AreaHistoryEntity historyEntity = areaMapper.toHistoryEntity(area);
                historyService.saveHistory(historyEntity);
            }
        }

        areaRepository.deleteAllByIdInBatch(ids);
        log.info("Areas batch deleted successfully: [count={}]", ids.size());
    }
}
