package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsCraneCreateRequestDto;
import kr.co.aim.api.dto.WcsCraneResponse;
import kr.co.aim.api.dto.WcsCraneUpdateRequestDto;
import kr.co.aim.common.condition.WcsCraneSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsCraneCreateCommand;
import kr.co.aim.domain.command.WcsCraneUpdateCommand;
import kr.co.aim.domain.model.WcsCrane;
import kr.co.aim.domain.repository.WcsCraneRepository;
import kr.co.aim.infra.persistence.entity.WcsCraneHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsCraneMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsCraneHistoryJpaRepository;
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
public class WcsCraneService {

    private final WcsCraneRepository wcsCraneRepository;
    private final WcsCraneHistoryJpaRepository wcsCraneHistoryJpaRepository;
    private final WcsCraneMapper wcsCraneMapper;

    /**
     * 조건 및 페이징 기반 WCS 크레인 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsCraneResponse> findCranes(WcsCraneSearchCondition condition, Pageable pageable) {
        Page<WcsCrane> pageResult = wcsCraneRepository.findCranes(condition, pageable);
        List<WcsCraneResponse> content = new ArrayList<>();

        for (WcsCrane crane : pageResult.getContent()) {
            if (crane != null) {
                content.add(WcsCraneResponse.fromDomain(crane));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 5개 복합키 기반 WCS 크레인 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsCraneResponse findById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(craneName)
                || craneNumber == null || localNo == null) {
            throw new IllegalArgumentException("조회할 크레인 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsCrane> optionalCrane = wcsCraneRepository.findById(
                factoryName.trim(), stockerName.trim(), craneName.trim(), craneNumber, localNo);
        if (optionalCrane.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 크레인이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", stockerName: " + stockerName + ", craneName: " + craneName
                    + ", craneNumber: " + craneNumber + ", localNo: " + localNo + "]");
        }

        return WcsCraneResponse.fromDomain(optionalCrane.get());
    }

    /**
     * 신규 WCS 크레인 등록 (CREATE)
     * - 5개 복합키 중복 검증
     * - 도메인 내부 WcsCrane.create(command) 호출
     * - CRANE 저장 및 W_CRANE_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsCraneResponse createCrane(WcsCraneCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("크레인 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getStockerName())) {
            throw new IllegalArgumentException("스토커 명은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getCraneName())) {
            throw new IllegalArgumentException("크레인 명은 필수 입력 항목입니다.");
        }
        if (dto.getCraneNumber() == null) {
            throw new IllegalArgumentException("크레인 번호는 필수 입력 항목입니다.");
        }
        if (dto.getLocalNo() == null) {
            throw new IllegalArgumentException("로컬 번호는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String stockerName = dto.getStockerName().trim();
        String craneName = dto.getCraneName().trim();
        Integer craneNumber = dto.getCraneNumber();
        Integer localNo = dto.getLocalNo();

        // 1. 복합키 중복 검증
        if (wcsCraneRepository.existsById(factoryName, stockerName, craneName, craneNumber, localNo)) {
            throw new IllegalArgumentException("이미 등록된 WCS 크레인입니다. [factoryName: " + factoryName
                    + ", stockerName: " + stockerName + ", craneName: " + craneName
                    + ", craneNumber: " + craneNumber + ", localNo: " + localNo + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsCraneCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs crane created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsCraneCreateCommand command = WcsCraneCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .stockerName(stockerName)
                .craneName(craneName)
                .craneNumber(craneNumber)
                .localNo(localNo)
                .autoRunStatus(dto.getAutoRunStatus())
                .carrierExist(dto.getCarrierExist())
                .carrierName(dto.getCarrierName())
                .columnPosition(dto.getColumnPosition())
                .currentCmdData(dto.getCurrentCmdData())
                .errorHappen(dto.getErrorHappen())
                .forkPreStatus(dto.getForkPreStatus())
                .forkStatus(dto.getForkStatus())
                .jobCompleteState(dto.getJobCompleteState())
                .mode(dto.getMode())
                .positionType(dto.getPositionType())
                .preStatus(dto.getPreStatus())
                .rowPosition(dto.getRowPosition())
                .stagePosition(dto.getStagePosition())
                .status(dto.getStatus())
                .touchPanelNumber(dto.getTouchPanelNumber())
                .zoneName(dto.getZoneName())
                .craneReadingEnableMode(dto.getCraneReadingEnableMode())
                .craneRfidEnableMode(dto.getCraneRfidEnableMode())
                .portNoPosition(dto.getPortNoPosition())
                .permissionRetryCount(dto.getPermissionRetryCount())
                .permissionRetryTime(dto.getPermissionRetryTime())
                .opportunisticEnabled(dto.getOpportunisticEnabled())
                .build();

        WcsCrane crane = WcsCrane.create(command);

        // 4. CRANE 테이블 저장
        WcsCrane savedCrane = wcsCraneRepository.save(crane);

        // 5. W_CRANE_HISTORY 테이블 이력 적재
        WcsCraneHistoryEntity historyEntity = wcsCraneMapper.toHistoryEntity(savedCrane);
        wcsCraneHistoryJpaRepository.save(historyEntity);

        log.info("WcsCrane created successfully: [factoryName={}, stockerName={}, craneName={}, craneNumber={}, localNo={}]",
                factoryName, stockerName, craneName, craneNumber, localNo);

        return WcsCraneResponse.fromDomain(savedCrane);
    }

    /**
     * 5개 복합키 기반 WCS 크레인 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 crane.update(command) 호출
     * - CRANE 저장 및 W_CRANE_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsCraneResponse updateCrane(String factoryName, String stockerName, String craneName,
                                        Integer craneNumber, Integer localNo, WcsCraneUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(craneName)
                || craneNumber == null || localNo == null) {
            throw new IllegalArgumentException("수정할 대상 크레인 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 크레인 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsCrane> optionalCrane = wcsCraneRepository.findById(
                factoryName.trim(), stockerName.trim(), craneName.trim(), craneNumber, localNo);
        if (optionalCrane.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 크레인이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", stockerName: " + stockerName + ", craneName: " + craneName
                    + ", craneNumber: " + craneNumber + ", localNo: " + localNo + "]");
        }

        WcsCrane crane = optionalCrane.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsCraneModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs crane modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsCraneUpdateCommand command = WcsCraneUpdateCommand.builder()
                .transactionInfo(tx)
                .autoRunStatus(dto.getAutoRunStatus())
                .carrierExist(dto.getCarrierExist())
                .carrierName(dto.getCarrierName())
                .columnPosition(dto.getColumnPosition())
                .currentCmdData(dto.getCurrentCmdData())
                .errorHappen(dto.getErrorHappen())
                .forkPreStatus(dto.getForkPreStatus())
                .forkStatus(dto.getForkStatus())
                .jobCompleteState(dto.getJobCompleteState())
                .mode(dto.getMode())
                .positionType(dto.getPositionType())
                .preStatus(dto.getPreStatus())
                .rowPosition(dto.getRowPosition())
                .stagePosition(dto.getStagePosition())
                .status(dto.getStatus())
                .touchPanelNumber(dto.getTouchPanelNumber())
                .zoneName(dto.getZoneName())
                .craneReadingEnableMode(dto.getCraneReadingEnableMode())
                .craneRfidEnableMode(dto.getCraneRfidEnableMode())
                .portNoPosition(dto.getPortNoPosition())
                .permissionRetryCount(dto.getPermissionRetryCount())
                .permissionRetryTime(dto.getPermissionRetryTime())
                .opportunisticEnabled(dto.getOpportunisticEnabled())
                .build();

        crane.update(command);

        // 4. CRANE 테이블 저장
        WcsCrane updatedCrane = wcsCraneRepository.save(crane);

        // 5. W_CRANE_HISTORY 테이블 이력 적재
        WcsCraneHistoryEntity historyEntity = wcsCraneMapper.toHistoryEntity(updatedCrane);
        wcsCraneHistoryJpaRepository.save(historyEntity);

        log.info("WcsCrane updated successfully: [factoryName={}, stockerName={}, craneName={}, craneNumber={}, localNo={}]",
                factoryName, stockerName, craneName, craneNumber, localNo);

        return WcsCraneResponse.fromDomain(updatedCrane);
    }

    /**
     * 5개 복합키 기반 WCS 크레인 삭제 (DELETE)
     * - 대상 조회
     * - W_CRANE_HISTORY 테이블에 삭제 이력 적재
     * - CRANE 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteCrane(String factoryName, String stockerName, String craneName,
                            Integer craneNumber, Integer localNo, String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(craneName)
                || craneNumber == null || localNo == null) {
            throw new IllegalArgumentException("삭제할 대상 크레인 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsCrane> optionalCrane = wcsCraneRepository.findById(
                factoryName.trim(), stockerName.trim(), craneName.trim(), craneNumber, localNo);
        if (optionalCrane.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 크레인이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", stockerName: " + stockerName + ", craneName: " + craneName
                    + ", craneNumber: " + craneNumber + ", localNo: " + localNo + "]");
        }

        WcsCrane crane = optionalCrane.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        crane.setLastEventName("WcsCraneDeleted");
        crane.setLastEventTime(now);
        crane.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        crane.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs crane deleted");

        // 2. 삭제 이력 적재
        WcsCraneHistoryEntity historyEntity = wcsCraneMapper.toHistoryEntity(crane);
        wcsCraneHistoryJpaRepository.save(historyEntity);

        // 3. 크레인 삭제
        wcsCraneRepository.deleteById(factoryName.trim(), stockerName.trim(), craneName.trim(), craneNumber, localNo);

        log.info("WcsCrane deleted successfully: [factoryName={}, stockerName={}, craneName={}, craneNumber={}, localNo={}]",
                factoryName, stockerName, craneName, craneNumber, localNo);
    }
}
