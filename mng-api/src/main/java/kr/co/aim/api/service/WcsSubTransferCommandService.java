package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsSubTransferCommandCreateRequestDto;
import kr.co.aim.api.dto.WcsSubTransferCommandResponse;
import kr.co.aim.api.dto.WcsSubTransferCommandUpdateRequestDto;
import kr.co.aim.common.condition.WcsSubTransferCommandSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsSubTransferCommandCreateCommand;
import kr.co.aim.domain.command.WcsSubTransferCommandUpdateCommand;
import kr.co.aim.domain.model.WcsSubTransferCommand;
import kr.co.aim.domain.repository.WcsSubTransferCommandRepository;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsSubTransferCommandMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsSubTransferCommandHistoryJpaRepository;
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
public class WcsSubTransferCommandService {

    private final WcsSubTransferCommandRepository wcsSubTransferCommandRepository;
    private final WcsSubTransferCommandHistoryJpaRepository wcsSubTransferCommandHistoryJpaRepository;
    private final WcsSubTransferCommandMapper wcsSubTransferCommandMapper;

    /**
     * 조건 및 페이징 기반 WCS 하위 반송 명령 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsSubTransferCommandResponse> findSubTransferCommands(WcsSubTransferCommandSearchCondition condition, Pageable pageable) {
        Page<WcsSubTransferCommand> pageResult = wcsSubTransferCommandRepository.findSubTransferCommands(condition, pageable);
        List<WcsSubTransferCommandResponse> content = new ArrayList<>();

        for (WcsSubTransferCommand command : pageResult.getContent()) {
            if (command != null) {
                content.add(WcsSubTransferCommandResponse.fromDomain(command));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 복합키 기반 WCS 하위 반송 명령 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsSubTransferCommandResponse findById(String transferCommandName, Integer jobNo) {
        if (!StringUtils.hasText(transferCommandName) || jobNo == null) {
            throw new IllegalArgumentException("조회할 하위 반송 명령 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsSubTransferCommand> optionalCommand = wcsSubTransferCommandRepository.findById(
                transferCommandName.trim(), jobNo);
        if (optionalCommand.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 하위 반송 명령이 존재하지 않습니다. [transferCommandName: " + transferCommandName + ", jobNo: " + jobNo + "]");
        }

        return WcsSubTransferCommandResponse.fromDomain(optionalCommand.get());
    }

    /**
     * 상위 반송 명령 명(transferCommandName)에 속한 하위 반송 명령 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<WcsSubTransferCommandResponse> findByTransferCommandName(String transferCommandName) {
        if (!StringUtils.hasText(transferCommandName)) {
            return new ArrayList<>();
        }

        List<WcsSubTransferCommand> list = wcsSubTransferCommandRepository.findByTransferCommandName(transferCommandName.trim());
        List<WcsSubTransferCommandResponse> result = new ArrayList<>();

        for (WcsSubTransferCommand command : list) {
            if (command != null) {
                result.add(WcsSubTransferCommandResponse.fromDomain(command));
            }
        }

        return result;
    }

    /**
     * 신규 WCS 하위 반송 명령 등록 (CREATE)
     * - 2개 복합키 중복 검증
     * - 도메인 내부 WcsSubTransferCommand.create(command) 호출
     * - SUBTRANSFERCOMMAND 저장 및 W_SUB_TRANSFER_COMMAND_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsSubTransferCommandResponse createSubTransferCommand(WcsSubTransferCommandCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("하위 반송 명령 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getTransferCommandName())) {
            throw new IllegalArgumentException("반송 명령 명은 필수 입력 항목입니다.");
        }
        if (dto.getJobNo() == null) {
            throw new IllegalArgumentException("작업 번호는 필수 입력 항목입니다.");
        }

        String transferCommandName = dto.getTransferCommandName().trim();
        Integer jobNo = dto.getJobNo();

        // 1. 복합키 중복 검증
        if (wcsSubTransferCommandRepository.existsById(transferCommandName, jobNo)) {
            throw new IllegalArgumentException("이미 등록된 WCS 하위 반송 명령입니다. [transferCommandName: " + transferCommandName + ", jobNo: " + jobNo + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsSubTransferCommandCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs sub transfer command created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsSubTransferCommandCreateCommand command = WcsSubTransferCommandCreateCommand.builder()
                .transactionInfo(tx)
                .transferCommandName(transferCommandName)
                .jobNo(jobNo)
                .createTime(dto.getCreateTime())
                .currentCommandData(dto.getCurrentCommandData())
                .jobCompleteState(dto.getJobCompleteState())
                .localNo(dto.getLocalNo())
                .sourcePositionColumn(dto.getSourcePositionColumn())
                .sourcePositionName(dto.getSourcePositionName())
                .sourcePositionPortNo(dto.getSourcePositionPortNo())
                .sourcePositionRow(dto.getSourcePositionRow())
                .sourcePositionStage(dto.getSourcePositionStage())
                .sourcePositionBin(dto.getSourcePositionBin())
                .subCommandStatus(dto.getSubCommandStatus())
                .targetPositionColumn(dto.getTargetPositionColumn())
                .targetPositionName(dto.getTargetPositionName())
                .targetPositionPortNo(dto.getTargetPositionPortNo())
                .targetPositionRow(dto.getTargetPositionRow())
                .targetPositionStage(dto.getTargetPositionStage())
                .targetPositionBin(dto.getTargetPositionBin())
                .transferEquipmentName(dto.getTransferEquipmentName())
                .transferType(dto.getTransferType())
                .transferUnitName(dto.getTransferUnitName())
                .transferUnitNumber(dto.getTransferUnitNumber())
                .factoryName(dto.getFactoryName())
                .endTime(dto.getEndTime())
                .startTime(dto.getStartTime())
                .transferSpeed(dto.getTransferSpeed())
                .loadType(dto.getLoadType())
                .carrierName(dto.getCarrierName())
                .build();

        WcsSubTransferCommand subTransferCommand = WcsSubTransferCommand.create(command);

        // 4. SUBTRANSFERCOMMAND 테이블 저장
        WcsSubTransferCommand savedCommand = wcsSubTransferCommandRepository.save(subTransferCommand);

        // 5. W_SUB_TRANSFER_COMMAND_HISTORY 테이블 이력 적재
        WcsSubTransferCommandHistoryEntity historyEntity = wcsSubTransferCommandMapper.toHistoryEntity(savedCommand);
        wcsSubTransferCommandHistoryJpaRepository.save(historyEntity);

        log.info("WcsSubTransferCommand created successfully: [transferCommandName={}, jobNo={}]", transferCommandName, jobNo);

        return WcsSubTransferCommandResponse.fromDomain(savedCommand);
    }

    /**
     * 복합키 기반 WCS 하위 반송 명령 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 subTransferCommand.update(command) 호출
     * - SUBTRANSFERCOMMAND 저장 및 W_SUB_TRANSFER_COMMAND_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsSubTransferCommandResponse updateSubTransferCommand(String transferCommandName, Integer jobNo, WcsSubTransferCommandUpdateRequestDto dto) {
        if (!StringUtils.hasText(transferCommandName) || jobNo == null) {
            throw new IllegalArgumentException("수정할 대상 하위 반송 명령 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 하위 반송 명령 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsSubTransferCommand> optionalCommand = wcsSubTransferCommandRepository.findById(
                transferCommandName.trim(), jobNo);
        if (optionalCommand.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 하위 반송 명령이 존재하지 않습니다. [transferCommandName: " + transferCommandName + ", jobNo: " + jobNo + "]");
        }

        WcsSubTransferCommand commandDomain = optionalCommand.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsSubTransferCommandModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs sub transfer command modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsSubTransferCommandUpdateCommand command = WcsSubTransferCommandUpdateCommand.builder()
                .transactionInfo(tx)
                .createTime(dto.getCreateTime())
                .currentCommandData(dto.getCurrentCommandData())
                .jobCompleteState(dto.getJobCompleteState())
                .localNo(dto.getLocalNo())
                .sourcePositionColumn(dto.getSourcePositionColumn())
                .sourcePositionName(dto.getSourcePositionName())
                .sourcePositionPortNo(dto.getSourcePositionPortNo())
                .sourcePositionRow(dto.getSourcePositionRow())
                .sourcePositionStage(dto.getSourcePositionStage())
                .sourcePositionBin(dto.getSourcePositionBin())
                .subCommandStatus(dto.getSubCommandStatus())
                .targetPositionColumn(dto.getTargetPositionColumn())
                .targetPositionName(dto.getTargetPositionName())
                .targetPositionPortNo(dto.getTargetPositionPortNo())
                .targetPositionRow(dto.getTargetPositionRow())
                .targetPositionStage(dto.getTargetPositionStage())
                .targetPositionBin(dto.getTargetPositionBin())
                .transferEquipmentName(dto.getTransferEquipmentName())
                .transferType(dto.getTransferType())
                .transferUnitName(dto.getTransferUnitName())
                .transferUnitNumber(dto.getTransferUnitNumber())
                .factoryName(dto.getFactoryName())
                .endTime(dto.getEndTime())
                .startTime(dto.getStartTime())
                .transferSpeed(dto.getTransferSpeed())
                .loadType(dto.getLoadType())
                .carrierName(dto.getCarrierName())
                .build();

        commandDomain.update(command);

        // 4. SUBTRANSFERCOMMAND 테이블 저장
        WcsSubTransferCommand updatedCommand = wcsSubTransferCommandRepository.save(commandDomain);

        // 5. W_SUB_TRANSFER_COMMAND_HISTORY 테이블 이력 적재
        WcsSubTransferCommandHistoryEntity historyEntity = wcsSubTransferCommandMapper.toHistoryEntity(updatedCommand);
        wcsSubTransferCommandHistoryJpaRepository.save(historyEntity);

        log.info("WcsSubTransferCommand updated successfully: [transferCommandName={}, jobNo={}]", transferCommandName, jobNo);

        return WcsSubTransferCommandResponse.fromDomain(updatedCommand);
    }

    /**
     * 복합키 기반 WCS 하위 반송 명령 삭제 (DELETE)
     * - 대상 조회
     * - W_SUB_TRANSFER_COMMAND_HISTORY 테이블에 삭제 이력 적재
     * - SUBTRANSFERCOMMAND 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteSubTransferCommand(String transferCommandName, Integer jobNo, String eventUser, String eventComment) {
        if (!StringUtils.hasText(transferCommandName) || jobNo == null) {
            throw new IllegalArgumentException("삭제할 대상 하위 반송 명령 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsSubTransferCommand> optionalCommand = wcsSubTransferCommandRepository.findById(
                transferCommandName.trim(), jobNo);
        if (optionalCommand.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 하위 반송 명령이 존재하지 않습니다. [transferCommandName: " + transferCommandName + ", jobNo: " + jobNo + "]");
        }

        WcsSubTransferCommand commandDomain = optionalCommand.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        commandDomain.setLastEventName("WcsSubTransferCommandDeleted");
        commandDomain.setLastEventTime(now);
        commandDomain.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        commandDomain.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs sub transfer command deleted");

        // 2. 삭제 이력 적재
        WcsSubTransferCommandHistoryEntity historyEntity = wcsSubTransferCommandMapper.toHistoryEntity(commandDomain);
        wcsSubTransferCommandHistoryJpaRepository.save(historyEntity);

        // 3. 하위 반송 명령 삭제
        wcsSubTransferCommandRepository.deleteById(transferCommandName.trim(), jobNo);

        log.info("WcsSubTransferCommand deleted successfully: [transferCommandName={}, jobNo={}]", transferCommandName, jobNo);
    }
}
