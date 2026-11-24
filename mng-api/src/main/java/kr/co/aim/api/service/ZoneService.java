package kr.co.aim.api.service;

import kr.co.aim.api.dto.ZoneCreateRequestDto;
import kr.co.aim.api.dto.ZoneResponse;
import kr.co.aim.api.dto.ZoneUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ZoneCreateCommand;
import kr.co.aim.domain.command.ZoneUpdateCommand;
import kr.co.aim.domain.model.Zone;
import kr.co.aim.domain.repository.ZoneRepository;
import kr.co.aim.domain.repository.condition.ZoneSearchCondition;
import kr.co.aim.infra.persistence.entity.ZoneHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ZoneMapper;
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
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final HistoryService historyService;
    private final ZoneMapper zoneMapper;

    /**
     * 조건 및 페이징 기반 존(ZONE) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<ZoneResponse> findZones(ZoneSearchCondition condition, Pageable pageable) {
        Page<Zone> pageResult = zoneRepository.findZonesWithConditions(condition, pageable);
        List<ZoneResponse> content = new ArrayList<>();
        for (Zone item : pageResult.getContent()) {
            content.add(ZoneResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 존(ZONE) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public ZoneResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 존(ZONE) ID가 유효하지 않습니다.");
        }
        Optional<Zone> optional = zoneRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 존(ZONE)이(가) 존재하지 않습니다. ID: " + id);
        }
        return ZoneResponse.fromDomain(optional.get());
    }

    /**
     * 신규 존(ZONE) 등록 (CREATE) 및 ZONE_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ZoneResponse createZone(ZoneCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("존(ZONE) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getZoneName() == null || dto.getZoneName().trim().isEmpty()) {
            throw new IllegalArgumentException("존 이름은(는) 필수 입력 항목입니다.");
        }

        String zoneNameValue = dto.getZoneName().trim();
        Optional<Zone> existing = zoneRepository.findByZoneName(zoneNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 존(ZONE)입니다: " + zoneNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ZoneCreated";
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
            eventComment = "Zone created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String zoneName = null;
        if (dto.getZoneName() != null) {
            zoneName = dto.getZoneName().trim();
        }

        String zoneType = null;
        if (dto.getZoneType() != null) {
            zoneType = dto.getZoneType().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        String zoneColor = null;
        if (dto.getZoneColor() != null) {
            zoneColor = dto.getZoneColor().trim();
        }

        ZoneCreateCommand command = ZoneCreateCommand.builder()
                .transactionInfo(tx)
                .zoneName(zoneName)
                .zoneType(zoneType)
                .description(description)
                .zoneColor(zoneColor)
                .build();

        Zone zone = Zone.create(command);
        Zone saved = zoneRepository.save(zone);

        ZoneHistoryEntity historyEntity = zoneMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("Zone created successfully: [id={}]", saved.getId());

        return ZoneResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 존(ZONE) 수정 (UPDATE) 및 ZONE_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ZoneResponse updateZone(Long id, ZoneUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 존(ZONE) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 존(ZONE) 정보가 유효하지 않습니다.");
        }

        Optional<Zone> optional = zoneRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 존(ZONE)이(가) 존재하지 않습니다. ID: " + id);
        }

        Zone zone = optional.get();

        if (dto.getZoneName() != null && !dto.getZoneName().trim().isEmpty()) {
            String targetName = dto.getZoneName().trim();
            if (!targetName.equals(zone.getZoneName())) {
                Optional<Zone> duplicate = zoneRepository.findByZoneName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 존(ZONE)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ZoneModified";
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
            eventComment = "Zone modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String zoneName = null;
        if (dto.getZoneName() != null) {
            zoneName = dto.getZoneName().trim();
        }

        String zoneType = null;
        if (dto.getZoneType() != null) {
            zoneType = dto.getZoneType().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        String zoneColor = null;
        if (dto.getZoneColor() != null) {
            zoneColor = dto.getZoneColor().trim();
        }

        ZoneUpdateCommand command = ZoneUpdateCommand.builder()
                .transactionInfo(tx)
                .zoneName(zoneName)
                .zoneType(zoneType)
                .description(description)
                .zoneColor(zoneColor)
                .build();

        zone.update(command);
        Zone updated = zoneRepository.save(zone);

        ZoneHistoryEntity historyEntity = zoneMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("Zone updated successfully: [id={}]", updated.getId());

        return ZoneResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 존(ZONE) 단건 삭제 (DELETE) 및 ZONE_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZone(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Zone> optional = zoneRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 존(ZONE)이(가) 존재하지 않습니다. ID: " + id);
        }

        Zone zone = optional.get();

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
            comment = "Zone deleted";
        }

        zone.setEventName("ZoneDeleted");
        zone.setEventTime(now);
        zone.setEventUser(user);
        zone.setEventComment(comment);

        ZoneHistoryEntity historyEntity = zoneMapper.toHistoryEntity(zone);
        historyService.saveHistory(historyEntity);

        zoneRepository.deleteById(id);

        log.info("Zone deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 존(ZONE) 벌크 삭제 (DELETE batch) 및 ZONE_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZones(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch Zones deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Zone> optional = zoneRepository.findById(id);
            if (optional.isPresent()) {
                Zone zone = optional.get();
                zone.setEventName("ZoneDeleted");
                zone.setEventTime(now);
                zone.setEventUser(user);
                zone.setEventComment(comment);

                ZoneHistoryEntity historyEntity = zoneMapper.toHistoryEntity(zone);
                historyService.saveHistory(historyEntity);
            }
        }

        zoneRepository.deleteAllByIdInBatch(ids);
        log.info("Zones batch deleted successfully: [count={}]", ids.size());
    }
}
