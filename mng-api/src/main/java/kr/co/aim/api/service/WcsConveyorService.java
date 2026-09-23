package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsConveyorCreateRequestDto;
import kr.co.aim.api.dto.WcsConveyorResponse;
import kr.co.aim.api.dto.WcsConveyorUpdateRequestDto;
import kr.co.aim.common.condition.WcsConveyorSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsConveyorCreateCommand;
import kr.co.aim.domain.command.WcsConveyorUpdateCommand;
import kr.co.aim.domain.model.WcsConveyor;
import kr.co.aim.domain.repository.WcsConveyorRepository;
import kr.co.aim.infra.persistence.entity.WcsConveyorHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsConveyorMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsConveyorHistoryJpaRepository;
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
public class WcsConveyorService {

    private final WcsConveyorRepository wcsConveyorRepository;
    private final WcsConveyorHistoryJpaRepository wcsConveyorHistoryJpaRepository;
    private final WcsConveyorMapper wcsConveyorMapper;

    /**
     * 조건 및 페이징 기반 WCS 컨베이어 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsConveyorResponse> findConveyors(WcsConveyorSearchCondition condition, Pageable pageable) {
        Page<WcsConveyor> pageResult = wcsConveyorRepository.findConveyors(condition, pageable);
        List<WcsConveyorResponse> content = new ArrayList<>();

        for (WcsConveyor conveyor : pageResult.getContent()) {
            if (conveyor != null) {
                content.add(WcsConveyorResponse.fromDomain(conveyor));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 5개 복합키 기반 WCS 컨베이어 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsConveyorResponse findById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(conveyorGroup) || !StringUtils.hasText(conveyorName)
                || conveyorNumber == null || localNo == null) {
            throw new IllegalArgumentException("조회할 컨베이어 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsConveyor> optionalConveyor = wcsConveyorRepository.findById(
                factoryName.trim(), conveyorGroup.trim(), conveyorName.trim(), conveyorNumber, localNo);
        if (optionalConveyor.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 컨베이어가 존재하지 않습니다. [factoryName: " + factoryName
                    + ", conveyorGroup: " + conveyorGroup + ", conveyorName: " + conveyorName
                    + ", conveyorNumber: " + conveyorNumber + ", localNo: " + localNo + "]");
        }

        return WcsConveyorResponse.fromDomain(optionalConveyor.get());
    }

    /**
     * 신규 WCS 컨베이어 등록 (CREATE)
     * - 5개 복합키 중복 검증
     * - 도메인 내부 WcsConveyor.create(command) 호출
     * - CONVEYOR 저장 및 W_CONVEYOR_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsConveyorResponse createConveyor(WcsConveyorCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("컨베이어 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getConveyorGroup())) {
            throw new IllegalArgumentException("컨베이어 그룹은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getConveyorName())) {
            throw new IllegalArgumentException("컨베이어 명은 필수 입력 항목입니다.");
        }
        if (dto.getConveyorNumber() == null) {
            throw new IllegalArgumentException("컨베이어 번호는 필수 입력 항목입니다.");
        }
        if (dto.getLocalNo() == null) {
            throw new IllegalArgumentException("로컬 번호는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String conveyorGroup = dto.getConveyorGroup().trim();
        String conveyorName = dto.getConveyorName().trim();
        Integer conveyorNumber = dto.getConveyorNumber();
        Integer localNo = dto.getLocalNo();

        // 1. 복합키 중복 검증
        if (wcsConveyorRepository.existsById(factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo)) {
            throw new IllegalArgumentException("이미 등록된 WCS 컨베이어입니다. [factoryName: " + factoryName
                    + ", conveyorGroup: " + conveyorGroup + ", conveyorName: " + conveyorName
                    + ", conveyorNumber: " + conveyorNumber + ", localNo: " + localNo + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsConveyorCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs conveyor created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsConveyorCreateCommand command = WcsConveyorCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .conveyorGroup(conveyorGroup)
                .conveyorName(conveyorName)
                .conveyorNumber(conveyorNumber)
                .localNo(localNo)
                .autoRunStatus(dto.getAutoRunStatus())
                .carrierExist(dto.getCarrierExist())
                .carrierName(dto.getCarrierName())
                .conveyorGroupNumber(dto.getConveyorGroupNumber())
                .conveyorType(dto.getConveyorType())
                .currentCmdData(dto.getCurrentCmdData())
                .direction(dto.getDirection())
                .dispatchingPriority(dto.getDispatchingPriority())
                .errorHappen(dto.getErrorHappen())
                .jobCompleteState(dto.getJobCompleteState())
                .onlineControlStatus(dto.getOnlineControlStatus())
                .operationMode(dto.getOperationMode())
                .preStatus(dto.getPreStatus())
                .rtvNumber(dto.getRtvNumber())
                .status(dto.getStatus())
                .touchPanelNumber(dto.getTouchPanelNumber())
                .serverName(dto.getServerName())
                .mode(dto.getMode())
                .downConveyorCount(dto.getDownConveyorCount())
                .onCarrierCount(dto.getOnCarrierCount())
                .totalConveyorCount(dto.getTotalConveyorCount())
                .runConveyorCount(dto.getRunConveyorCount())
                .machineTypeName(dto.getMachineTypeName())
                .readingEnableMode(dto.getReadingEnableMode())
                .rfidEnableMode(dto.getRfidEnableMode())
                .eqRouteKey(dto.getEqRouteKey())
                .conveyorConnectionStatus(dto.getConveyorConnectionStatus())
                .areaName(dto.getAreaName())
                .build();

        WcsConveyor conveyor = WcsConveyor.create(command);

        // 4. CONVEYOR 테이블 저장
        WcsConveyor savedConveyor = wcsConveyorRepository.save(conveyor);

        // 5. W_CONVEYOR_HISTORY 테이블 이력 적재
        WcsConveyorHistoryEntity historyEntity = wcsConveyorMapper.toHistoryEntity(savedConveyor);
        wcsConveyorHistoryJpaRepository.save(historyEntity);

        log.info("WcsConveyor created successfully: [factoryName={}, conveyorGroup={}, conveyorName={}, conveyorNumber={}, localNo={}]",
                factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo);

        return WcsConveyorResponse.fromDomain(savedConveyor);
    }

    /**
     * 5개 복합키 기반 WCS 컨베이어 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 conveyor.update(command) 호출
     * - CONVEYOR 저장 및 W_CONVEYOR_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsConveyorResponse updateConveyor(String factoryName, String conveyorGroup, String conveyorName,
                                             Integer conveyorNumber, Integer localNo, WcsConveyorUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(conveyorGroup) || !StringUtils.hasText(conveyorName)
                || conveyorNumber == null || localNo == null) {
            throw new IllegalArgumentException("수정할 대상 컨베이어 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 컨베이어 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsConveyor> optionalConveyor = wcsConveyorRepository.findById(
                factoryName.trim(), conveyorGroup.trim(), conveyorName.trim(), conveyorNumber, localNo);
        if (optionalConveyor.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 컨베이어가 존재하지 않습니다. [factoryName: " + factoryName
                    + ", conveyorGroup: " + conveyorGroup + ", conveyorName: " + conveyorName
                    + ", conveyorNumber: " + conveyorNumber + ", localNo: " + localNo + "]");
        }

        WcsConveyor conveyor = optionalConveyor.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsConveyorModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs conveyor modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsConveyorUpdateCommand command = WcsConveyorUpdateCommand.builder()
                .transactionInfo(tx)
                .autoRunStatus(dto.getAutoRunStatus())
                .carrierExist(dto.getCarrierExist())
                .carrierName(dto.getCarrierName())
                .conveyorGroupNumber(dto.getConveyorGroupNumber())
                .conveyorType(dto.getConveyorType())
                .currentCmdData(dto.getCurrentCmdData())
                .direction(dto.getDirection())
                .dispatchingPriority(dto.getDispatchingPriority())
                .errorHappen(dto.getErrorHappen())
                .jobCompleteState(dto.getJobCompleteState())
                .onlineControlStatus(dto.getOnlineControlStatus())
                .operationMode(dto.getOperationMode())
                .preStatus(dto.getPreStatus())
                .rtvNumber(dto.getRtvNumber())
                .status(dto.getStatus())
                .touchPanelNumber(dto.getTouchPanelNumber())
                .serverName(dto.getServerName())
                .mode(dto.getMode())
                .downConveyorCount(dto.getDownConveyorCount())
                .onCarrierCount(dto.getOnCarrierCount())
                .totalConveyorCount(dto.getTotalConveyorCount())
                .runConveyorCount(dto.getRunConveyorCount())
                .machineTypeName(dto.getMachineTypeName())
                .readingEnableMode(dto.getReadingEnableMode())
                .rfidEnableMode(dto.getRfidEnableMode())
                .eqRouteKey(dto.getEqRouteKey())
                .conveyorConnectionStatus(dto.getConveyorConnectionStatus())
                .areaName(dto.getAreaName())
                .build();

        conveyor.update(command);

        // 4. CONVEYOR 테이블 저장
        WcsConveyor updatedConveyor = wcsConveyorRepository.save(conveyor);

        // 5. W_CONVEYOR_HISTORY 테이블 이력 적재
        WcsConveyorHistoryEntity historyEntity = wcsConveyorMapper.toHistoryEntity(updatedConveyor);
        wcsConveyorHistoryJpaRepository.save(historyEntity);

        log.info("WcsConveyor updated successfully: [factoryName={}, conveyorGroup={}, conveyorName={}, conveyorNumber={}, localNo={}]",
                factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo);

        return WcsConveyorResponse.fromDomain(updatedConveyor);
    }

    /**
     * 5개 복합키 기반 WCS 컨베이어 삭제 (DELETE)
     * - 대상 조회
     * - W_CONVEYOR_HISTORY 테이블에 삭제 이력 적재
     * - CONVEYOR 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteConveyor(String factoryName, String conveyorGroup, String conveyorName,
                               Integer conveyorNumber, Integer localNo, String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(conveyorGroup) || !StringUtils.hasText(conveyorName)
                || conveyorNumber == null || localNo == null) {
            throw new IllegalArgumentException("삭제할 대상 컨베이어 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsConveyor> optionalConveyor = wcsConveyorRepository.findById(
                factoryName.trim(), conveyorGroup.trim(), conveyorName.trim(), conveyorNumber, localNo);
        if (optionalConveyor.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 컨베이어가 존재하지 않습니다. [factoryName: " + factoryName
                    + ", conveyorGroup: " + conveyorGroup + ", conveyorName: " + conveyorName
                    + ", conveyorNumber: " + conveyorNumber + ", localNo: " + localNo + "]");
        }

        WcsConveyor conveyor = optionalConveyor.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        conveyor.setLastEventName("WcsConveyorDeleted");
        conveyor.setLastEventTime(now);
        conveyor.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        conveyor.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs conveyor deleted");

        // 2. 삭제 이력 적재
        WcsConveyorHistoryEntity historyEntity = wcsConveyorMapper.toHistoryEntity(conveyor);
        wcsConveyorHistoryJpaRepository.save(historyEntity);

        // 3. 컨베이어 삭제
        wcsConveyorRepository.deleteById(factoryName.trim(), conveyorGroup.trim(), conveyorName.trim(), conveyorNumber, localNo);

        log.info("WcsConveyor deleted successfully: [factoryName={}, conveyorGroup={}, conveyorName={}, conveyorNumber={}, localNo={}]",
                factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo);
    }
}
