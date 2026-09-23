package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsZoneCreateRequestDto;
import kr.co.aim.api.dto.WcsZoneResponse;
import kr.co.aim.api.dto.WcsZoneUpdateRequestDto;
import kr.co.aim.common.condition.WcsZoneSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsZoneCreateCommand;
import kr.co.aim.domain.command.WcsZoneUpdateCommand;
import kr.co.aim.domain.model.WcsZone;
import kr.co.aim.domain.repository.WcsZoneRepository;
import kr.co.aim.infra.persistence.entity.WcsZoneHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsZoneMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsZoneHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcsZoneService {

    private final WcsZoneRepository wcsZoneRepository;
    private final WcsZoneHistoryJpaRepository wcsZoneHistoryJpaRepository;
    private final WcsZoneMapper wcsZoneMapper;

    /**
     * 조건 및 페이징 기반 WCS 보관 존 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsZoneResponse> findZones(WcsZoneSearchCondition condition, Pageable pageable) {
        Page<WcsZone> pageResult = wcsZoneRepository.findZones(condition, pageable);
        List<WcsZoneResponse> content = new ArrayList<>();

        for (WcsZone zone : pageResult.getContent()) {
            if (zone != null) {
                content.add(WcsZoneResponse.fromDomain(zone));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 복합키(factoryName, zoneName) 기반 WCS 보관 존 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsZoneResponse findById(String factoryName, String zoneName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(zoneName)) {
            throw new IllegalArgumentException("조회할 공장 구분 및 존 명이 누락되었습니다.");
        }

        Optional<WcsZone> optionalZone = wcsZoneRepository.findById(factoryName.trim(), zoneName.trim());
        if (optionalZone.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 보관 존이 존재하지 않습니다. (factoryName: " + factoryName + ", zoneName: " + zoneName + ")");
        }

        return WcsZoneResponse.fromDomain(optionalZone.get());
    }

    /**
     * 신규 WCS 보관 존 등록 (CREATE)
     * - 복합키(factoryName, zoneName) 중복 검증
     * - 도메인 내부 WcsZone.create(command) 호출
     * - ZONE 저장 및 W_ZONE_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsZoneResponse createZone(WcsZoneCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("존 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getZoneName())) {
            throw new IllegalArgumentException("존 명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String zoneName = dto.getZoneName().trim();

        // 1. 복합키 중복 검증
        if (wcsZoneRepository.existsById(factoryName, zoneName)) {
            throw new IllegalArgumentException("이미 등록된 WCS 보관 존입니다. (factoryName: " + factoryName + ", zoneName: " + zoneName + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsZoneCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs zone created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsZoneCreateCommand command = WcsZoneCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .zoneName(zoneName)
                .deepFirstFlag(dto.getDeepFirstFlag())
                .frontRowInterval(dto.getFrontRowInterval())
                .loadType(dto.getLoadType())
                .maxCapacityPercent(dto.getMaxCapacityPercent())
                .zoneCapacity(dto.getZoneCapacity())
                .zoneColor(dto.getZoneColor())
                .zoneSize(dto.getZoneSize())
                .zoneType(dto.getZoneType())
                .shelfSelectMode(dto.getShelfSelectMode())
                .useCapacityPercent(dto.getUseCapacityPercent())
                .waitingAreaFlag(dto.getWaitingAreaFlag())
                .build();

        WcsZone zone = WcsZone.create(command);

        // 4. ZONE 테이블 저장
        WcsZone savedZone = wcsZoneRepository.save(zone);

        // 5. W_ZONE_HISTORY 테이블 이력 적재
        WcsZoneHistoryEntity historyEntity = wcsZoneMapper.toHistoryEntity(savedZone);
        wcsZoneHistoryJpaRepository.save(historyEntity);

        log.info("WcsZone created successfully: [factoryName={}, zoneName={}]", factoryName, zoneName);

        return WcsZoneResponse.fromDomain(savedZone);
    }

    /**
     * 복합키 기반 WCS 보관 존 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 zone.update(command) 호출
     * - ZONE 저장 및 W_ZONE_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsZoneResponse updateZone(String factoryName, String zoneName, WcsZoneUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(zoneName)) {
            throw new IllegalArgumentException("수정할 대상 공장 구분 및 존 명이 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 존 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsZone> optionalZone = wcsZoneRepository.findById(factoryName.trim(), zoneName.trim());
        if (optionalZone.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 보관 존이 존재하지 않습니다. (factoryName: " + factoryName + ", zoneName: " + zoneName + ")");
        }

        WcsZone zone = optionalZone.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsZoneModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs zone modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsZoneUpdateCommand command = WcsZoneUpdateCommand.builder()
                .transactionInfo(tx)
                .deepFirstFlag(dto.getDeepFirstFlag())
                .frontRowInterval(dto.getFrontRowInterval())
                .loadType(dto.getLoadType())
                .maxCapacityPercent(dto.getMaxCapacityPercent())
                .zoneCapacity(dto.getZoneCapacity())
                .zoneColor(dto.getZoneColor())
                .zoneSize(dto.getZoneSize())
                .zoneType(dto.getZoneType())
                .shelfSelectMode(dto.getShelfSelectMode())
                .useCapacityPercent(dto.getUseCapacityPercent())
                .waitingAreaFlag(dto.getWaitingAreaFlag())
                .build();

        zone.update(command);

        // 4. ZONE 테이블 저장
        WcsZone updatedZone = wcsZoneRepository.save(zone);

        // 5. W_ZONE_HISTORY 테이블 이력 적재
        WcsZoneHistoryEntity historyEntity = wcsZoneMapper.toHistoryEntity(updatedZone);
        wcsZoneHistoryJpaRepository.save(historyEntity);

        log.info("WcsZone updated successfully: [factoryName={}, zoneName={}]", factoryName, zoneName);

        return WcsZoneResponse.fromDomain(updatedZone);
    }

    /**
     * 복합키 기반 WCS 보관 존 삭제 (DELETE)
     * - 대상 조회
     * - W_ZONE_HISTORY 테이블에 삭제 이력 적재
     * - ZONE 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteZone(String factoryName, String zoneName, String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(zoneName)) {
            throw new IllegalArgumentException("삭제할 대상 공장 구분 및 존 명이 누락되었습니다.");
        }

        Optional<WcsZone> optionalZone = wcsZoneRepository.findById(factoryName.trim(), zoneName.trim());
        if (optionalZone.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 보관 존이 존재하지 않습니다. (factoryName: " + factoryName + ", zoneName: " + zoneName + ")");
        }

        WcsZone zone = optionalZone.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        zone.setLastEventName("WcsZoneDeleted");
        zone.setLastEventTime(now);
        zone.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        zone.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs zone deleted");

        // 2. 삭제 이력 적재
        WcsZoneHistoryEntity historyEntity = wcsZoneMapper.toHistoryEntity(zone);
        wcsZoneHistoryJpaRepository.save(historyEntity);

        // 3. 존 삭제
        wcsZoneRepository.deleteById(factoryName.trim(), zoneName.trim());

        log.info("WcsZone deleted successfully: [factoryName={}, zoneName={}]", factoryName, zoneName);
    }
}
