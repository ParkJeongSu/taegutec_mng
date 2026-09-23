package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsAlternativeStorageZoneCreateRequestDto;
import kr.co.aim.api.dto.WcsAlternativeStorageZoneResponse;
import kr.co.aim.api.dto.WcsAlternativeStorageZoneUpdateRequestDto;
import kr.co.aim.common.condition.WcsAlternativeStorageZoneSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsAlternativeStorageZoneCreateCommand;
import kr.co.aim.domain.command.WcsAlternativeStorageZoneUpdateCommand;
import kr.co.aim.domain.model.WcsAlternativeStorageZone;
import kr.co.aim.domain.repository.WcsAlternativeStorageZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcsAlternativeStorageZoneService {

    private final WcsAlternativeStorageZoneRepository wcsAlternativeStorageZoneRepository;

    /**
     * 조건 및 페이징 기반 WCS 대체 보관 존 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsAlternativeStorageZoneResponse> findAlternativeStorageZones(WcsAlternativeStorageZoneSearchCondition condition, Pageable pageable) {
        Page<WcsAlternativeStorageZone> pageResult = wcsAlternativeStorageZoneRepository.findAlternativeStorageZones(condition, pageable);
        List<WcsAlternativeStorageZoneResponse> content = new ArrayList<>();

        for (WcsAlternativeStorageZone zone : pageResult.getContent()) {
            if (zone != null) {
                content.add(WcsAlternativeStorageZoneResponse.fromDomain(zone));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 4개 복합키 기반 WCS 대체 보관 존 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsAlternativeStorageZoneResponse findById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)
                || !StringUtils.hasText(alternativeZoneName) || priority == null) {
            throw new IllegalArgumentException("조회할 대체 보관 존 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsAlternativeStorageZone> optionalZone = wcsAlternativeStorageZoneRepository.findById(
                factoryName.trim(), sourceZoneName.trim(), alternativeZoneName.trim(), priority);
        if (optionalZone.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 대체 보관 존이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", sourceZoneName: " + sourceZoneName + ", alternativeZoneName: " + alternativeZoneName + ", priority: " + priority + "]");
        }

        return WcsAlternativeStorageZoneResponse.fromDomain(optionalZone.get());
    }

    /**
     * 공장 및 원본 존 별 대체 보관 존 목록 조회 (우선순위 순)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<WcsAlternativeStorageZoneResponse> findByFactoryNameAndSourceZoneName(String factoryName, String sourceZoneName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)) {
            return new ArrayList<>();
        }

        List<WcsAlternativeStorageZone> list = wcsAlternativeStorageZoneRepository.findByFactoryNameAndSourceZoneName(
                factoryName.trim(), sourceZoneName.trim());
        List<WcsAlternativeStorageZoneResponse> result = new ArrayList<>();

        for (WcsAlternativeStorageZone zone : list) {
            if (zone != null) {
                result.add(WcsAlternativeStorageZoneResponse.fromDomain(zone));
            }
        }

        return result;
    }

    /**
     * 신규 WCS 대체 보관 존 등록 (CREATE)
     * - 4개 복합키 중복 검증
     * - 도메인 내부 WcsAlternativeStorageZone.create(command) 호출
     * - ALTERNATIVESTORAGEZONE 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsAlternativeStorageZoneResponse createAlternativeStorageZone(WcsAlternativeStorageZoneCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("대체 보관 존 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getSourceZoneName())) {
            throw new IllegalArgumentException("원본 보관 존 명은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getAlternativeZoneName())) {
            throw new IllegalArgumentException("대체 보관 존 명은 필수 입력 항목입니다.");
        }
        if (dto.getPriority() == null) {
            throw new IllegalArgumentException("우선순위는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String sourceZoneName = dto.getSourceZoneName().trim();
        String alternativeZoneName = dto.getAlternativeZoneName().trim();
        Integer priority = dto.getPriority();

        // 1. 복합키 중복 검증
        if (wcsAlternativeStorageZoneRepository.existsById(factoryName, sourceZoneName, alternativeZoneName, priority)) {
            throw new IllegalArgumentException("이미 등록된 WCS 대체 보관 존입니다. [factoryName: " + factoryName
                    + ", sourceZoneName: " + sourceZoneName + ", alternativeZoneName: " + alternativeZoneName + ", priority: " + priority + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsAlternativeStorageZoneCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs alternative storage zone created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsAlternativeStorageZoneCreateCommand command = WcsAlternativeStorageZoneCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .sourceZoneName(sourceZoneName)
                .alternativeZoneName(alternativeZoneName)
                .priority(priority)
                .description(dto.getDescription())
                .useYn(dto.getUseYn())
                .build();

        WcsAlternativeStorageZone zone = WcsAlternativeStorageZone.create(command);

        // 4. ALTERNATIVESTORAGEZONE 테이블 저장
        WcsAlternativeStorageZone savedZone = wcsAlternativeStorageZoneRepository.save(zone);

        log.info("WcsAlternativeStorageZone created successfully: [factoryName={}, sourceZoneName={}, alternativeZoneName={}, priority={}]",
                factoryName, sourceZoneName, alternativeZoneName, priority);

        return WcsAlternativeStorageZoneResponse.fromDomain(savedZone);
    }

    /**
     * 4개 복합키 기반 WCS 대체 보관 존 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 zone.update(command) 호출
     * - ALTERNATIVESTORAGEZONE 저장 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsAlternativeStorageZoneResponse updateAlternativeStorageZone(
            String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority,
            WcsAlternativeStorageZoneUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)
                || !StringUtils.hasText(alternativeZoneName) || priority == null) {
            throw new IllegalArgumentException("수정할 대상 대체 보관 존 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 대체 보관 존 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsAlternativeStorageZone> optionalZone = wcsAlternativeStorageZoneRepository.findById(
                factoryName.trim(), sourceZoneName.trim(), alternativeZoneName.trim(), priority);
        if (optionalZone.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 대체 보관 존이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", sourceZoneName: " + sourceZoneName + ", alternativeZoneName: " + alternativeZoneName + ", priority: " + priority + "]");
        }

        WcsAlternativeStorageZone zoneDomain = optionalZone.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsAlternativeStorageZoneModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs alternative storage zone modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsAlternativeStorageZoneUpdateCommand command = WcsAlternativeStorageZoneUpdateCommand.builder()
                .transactionInfo(tx)
                .description(dto.getDescription())
                .useYn(dto.getUseYn())
                .build();

        zoneDomain.update(command);

        // 4. ALTERNATIVESTORAGEZONE 테이블 저장
        WcsAlternativeStorageZone updatedZone = wcsAlternativeStorageZoneRepository.save(zoneDomain);

        log.info("WcsAlternativeStorageZone updated successfully: [factoryName={}, sourceZoneName={}, alternativeZoneName={}, priority={}]",
                factoryName, sourceZoneName, alternativeZoneName, priority);

        return WcsAlternativeStorageZoneResponse.fromDomain(updatedZone);
    }

    /**
     * 4개 복합키 기반 WCS 대체 보관 존 삭제 (DELETE)
     * - 대상 존재 확인
     * - ALTERNATIVESTORAGEZONE 테이블에서 삭제 (이력 테이블 부재)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAlternativeStorageZone(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)
                || !StringUtils.hasText(alternativeZoneName) || priority == null) {
            throw new IllegalArgumentException("삭제할 대상 대체 보관 존 복합키 식별자 정보가 누락되었습니다.");
        }

        if (!wcsAlternativeStorageZoneRepository.existsById(factoryName.trim(), sourceZoneName.trim(), alternativeZoneName.trim(), priority)) {
            throw new IllegalArgumentException("삭제할 대상 WCS 대체 보관 존이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", sourceZoneName: " + sourceZoneName + ", alternativeZoneName: " + alternativeZoneName + ", priority: " + priority + "]");
        }

        // ALTERNATIVESTORAGEZONE 테이블에서 삭제
        wcsAlternativeStorageZoneRepository.deleteById(factoryName.trim(), sourceZoneName.trim(), alternativeZoneName.trim(), priority);

        log.info("WcsAlternativeStorageZone deleted successfully: [factoryName={}, sourceZoneName={}, alternativeZoneName={}, priority={}]",
                factoryName, sourceZoneName, alternativeZoneName, priority);
    }
}
