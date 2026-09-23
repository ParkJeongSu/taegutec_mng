package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsStockerCreateRequestDto;
import kr.co.aim.api.dto.WcsStockerResponse;
import kr.co.aim.api.dto.WcsStockerUpdateRequestDto;
import kr.co.aim.common.condition.WcsStockerSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsStockerCreateCommand;
import kr.co.aim.domain.command.WcsStockerUpdateCommand;
import kr.co.aim.domain.model.WcsStocker;
import kr.co.aim.domain.repository.WcsStockerRepository;
import kr.co.aim.infra.persistence.entity.WcsStockerHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsStockerMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsStockerHistoryJpaRepository;
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
public class WcsStockerService {

    private final WcsStockerRepository wcsStockerRepository;
    private final WcsStockerHistoryJpaRepository wcsStockerHistoryJpaRepository;
    private final WcsStockerMapper wcsStockerMapper;

    /**
     * 조건 및 페이징 기반 WCS 스토커 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsStockerResponse> findStockers(WcsStockerSearchCondition condition, Pageable pageable) {
        Page<WcsStocker> pageResult = wcsStockerRepository.findStockers(condition, pageable);
        List<WcsStockerResponse> content = new ArrayList<>();

        for (WcsStocker stocker : pageResult.getContent()) {
            if (stocker != null) {
                content.add(WcsStockerResponse.fromDomain(stocker));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 복합키(factoryName, stockerName) 기반 WCS 스토커 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsStockerResponse findById(String factoryName, String stockerName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            throw new IllegalArgumentException("조회할 공장 구분 및 스토커 명이 누락되었습니다.");
        }

        Optional<WcsStocker> optionalStocker = wcsStockerRepository.findById(factoryName.trim(), stockerName.trim());
        if (optionalStocker.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 스토커가 존재하지 않습니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ")");
        }

        return WcsStockerResponse.fromDomain(optionalStocker.get());
    }

    /**
     * 신규 WCS 스토커 등록 (CREATE)
     * - 복합키 중복 검증
     * - 도메인 내부 WcsStocker.create(command) 호출
     * - STOCKER 저장 및 STOCKER_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsStockerResponse createStocker(WcsStockerCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("스토커 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getStockerName())) {
            throw new IllegalArgumentException("스토커 명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String stockerName = dto.getStockerName().trim();

        // 1. 중복 검증 (복합키 기준)
        if (wcsStockerRepository.existsById(factoryName, stockerName)) {
            throw new IllegalArgumentException("이미 등록된 WCS 스토커입니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsStockerCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs stocker created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsStockerCreateCommand command = WcsStockerCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .stockerName(stockerName)
                .dispatchingPriority(dto.getDispatchingPriority())
                .onlineControlStatus(dto.getOnlineControlStatus())
                .operationMode(dto.getOperationMode())
                .serverName(dto.getServerName())
                .stockerConnectionStatus(dto.getStockerConnectionStatus())
                .stockerMode(dto.getStockerMode())
                .stockerNumber(dto.getStockerNumber())
                .stockerStatus(dto.getStockerStatus())
                .stockerType(dto.getStockerType())
                .machineTypeName(dto.getMachineTypeName())
                .eqRouteKey(dto.getEqRouteKey())
                .areaName(dto.getAreaName())
                .abnormalShelfCount(dto.getAbnormalShelfCount())
                .emptyShelfCount(dto.getEmptyShelfCount())
                .normalShelfCount(dto.getNormalShelfCount())
                .reservedShelfCount(dto.getReservedShelfCount())
                .totalShelfCount(dto.getTotalShelfCount())
                .useShelfCount(dto.getUseShelfCount())
                .lastStockerArrangeExecuteTime(dto.getLastStockerArrangeExecuteTime())
                .stockerArrangeDailyTime(dto.getStockerArrangeDailyTime())
                .stockerArrangeEnabled(dto.getStockerArrangeEnabled())
                .stockerArrangeExecuteTime(dto.getStockerArrangeExecuteTime())
                .stockerArrangeMaxCommandCount(dto.getStockerArrangeMaxCommandCount())
                .stockerArrangeMode(dto.getStockerArrangeMode())
                .stockerArrangeScheduleType(dto.getStockerArrangeScheduleType())
                .stockerArrangeState(dto.getStockerArrangeState())
                .carrierUseCountThreshold(dto.getCarrierUseCountThreshold())
                .build();

        WcsStocker stocker = WcsStocker.create(command);

        // 4. STOCKER 테이블 저장
        WcsStocker savedStocker = wcsStockerRepository.save(stocker);

        // 5. STOCKER_HISTORY 테이블 이력 적재
        WcsStockerHistoryEntity historyEntity = wcsStockerMapper.toHistoryEntity(savedStocker);
        wcsStockerHistoryJpaRepository.save(historyEntity);

        log.info("WcsStocker created successfully: [factoryName={}, stockerName={}]", factoryName, stockerName);

        return WcsStockerResponse.fromDomain(savedStocker);
    }

    /**
     * 복합키 기반 WCS 스토커 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 stocker.update(command) 호출
     * - STOCKER 저장 및 STOCKER_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsStockerResponse updateStocker(String factoryName, String stockerName, WcsStockerUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            throw new IllegalArgumentException("수정할 대상 공장 구분 및 스토커 명이 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 스토커 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsStocker> optionalStocker = wcsStockerRepository.findById(factoryName.trim(), stockerName.trim());
        if (optionalStocker.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 스토커가 존재하지 않습니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ")");
        }

        WcsStocker stocker = optionalStocker.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsStockerModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs stocker modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsStockerUpdateCommand command = WcsStockerUpdateCommand.builder()
                .transactionInfo(tx)
                .dispatchingPriority(dto.getDispatchingPriority())
                .onlineControlStatus(dto.getOnlineControlStatus())
                .operationMode(dto.getOperationMode())
                .serverName(dto.getServerName())
                .stockerConnectionStatus(dto.getStockerConnectionStatus())
                .stockerMode(dto.getStockerMode())
                .stockerNumber(dto.getStockerNumber())
                .stockerStatus(dto.getStockerStatus())
                .stockerType(dto.getStockerType())
                .machineTypeName(dto.getMachineTypeName())
                .eqRouteKey(dto.getEqRouteKey())
                .areaName(dto.getAreaName())
                .abnormalShelfCount(dto.getAbnormalShelfCount())
                .emptyShelfCount(dto.getEmptyShelfCount())
                .normalShelfCount(dto.getNormalShelfCount())
                .reservedShelfCount(dto.getReservedShelfCount())
                .totalShelfCount(dto.getTotalShelfCount())
                .useShelfCount(dto.getUseShelfCount())
                .lastStockerArrangeExecuteTime(dto.getLastStockerArrangeExecuteTime())
                .stockerArrangeDailyTime(dto.getStockerArrangeDailyTime())
                .stockerArrangeEnabled(dto.getStockerArrangeEnabled())
                .stockerArrangeExecuteTime(dto.getStockerArrangeExecuteTime())
                .stockerArrangeMaxCommandCount(dto.getStockerArrangeMaxCommandCount())
                .stockerArrangeMode(dto.getStockerArrangeMode())
                .stockerArrangeScheduleType(dto.getStockerArrangeScheduleType())
                .stockerArrangeState(dto.getStockerArrangeState())
                .carrierUseCountThreshold(dto.getCarrierUseCountThreshold())
                .build();

        stocker.update(command);

        // 4. STOCKER 테이블 저장
        WcsStocker updatedStocker = wcsStockerRepository.save(stocker);

        // 5. STOCKER_HISTORY 테이블 이력 적재
        WcsStockerHistoryEntity historyEntity = wcsStockerMapper.toHistoryEntity(updatedStocker);
        wcsStockerHistoryJpaRepository.save(historyEntity);

        log.info("WcsStocker updated successfully: [factoryName={}, stockerName={}]", factoryName, stockerName);

        return WcsStockerResponse.fromDomain(updatedStocker);
    }

    /**
     * 복합키 기반 WCS 스토커 삭제 (DELETE)
     * - 대상 조회
     * - STOCKER_HISTORY 테이블에 삭제 이력 적재
     * - STOCKER 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteStocker(String factoryName, String stockerName, String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            throw new IllegalArgumentException("삭제할 대상 공장 구분 및 스토커 명이 누락되었습니다.");
        }

        Optional<WcsStocker> optionalStocker = wcsStockerRepository.findById(factoryName.trim(), stockerName.trim());
        if (optionalStocker.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 스토커가 존재하지 않습니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ")");
        }

        WcsStocker stocker = optionalStocker.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        stocker.setLastEventName("WcsStockerDeleted");
        stocker.setLastEventTime(now);
        stocker.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        stocker.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs stocker deleted");

        // 2. 삭제 이력 적재
        WcsStockerHistoryEntity historyEntity = wcsStockerMapper.toHistoryEntity(stocker);
        wcsStockerHistoryJpaRepository.save(historyEntity);

        // 3. 스토커 삭제
        wcsStockerRepository.deleteById(factoryName.trim(), stockerName.trim());

        log.info("WcsStocker deleted successfully: [factoryName={}, stockerName={}]", factoryName, stockerName);
    }
}
