package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsTransferCommandCreateRequestDto;
import kr.co.aim.api.dto.WcsTransferCommandResponse;
import kr.co.aim.api.dto.WcsTransferCommandUpdateRequestDto;
import kr.co.aim.common.condition.WcsTransferCommandSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsTransferCommandCreateCommand;
import kr.co.aim.domain.command.WcsTransferCommandUpdateCommand;
import kr.co.aim.domain.model.WcsTransferCommand;
import kr.co.aim.domain.repository.WcsTransferCommandRepository;
import kr.co.aim.infra.persistence.entity.WcsTransferCommandHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsTransferCommandMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsTransferCommandHistoryJpaRepository;
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
public class WcsTransferCommandService {

    private final WcsTransferCommandRepository wcsTransferCommandRepository;
    private final WcsTransferCommandHistoryJpaRepository wcsTransferCommandHistoryJpaRepository;
    private final WcsTransferCommandMapper wcsTransferCommandMapper;

    /**
     * 조건 및 페이징 기반 WCS 반송 명령 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsTransferCommandResponse> findTransferCommands(WcsTransferCommandSearchCondition condition, Pageable pageable) {
        Page<WcsTransferCommand> pageResult = wcsTransferCommandRepository.findTransferCommands(condition, pageable);
        List<WcsTransferCommandResponse> content = new ArrayList<>();

        for (WcsTransferCommand command : pageResult.getContent()) {
            if (command != null) {
                content.add(WcsTransferCommandResponse.fromDomain(command));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 단일 PK 기반 WCS 반송 명령 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsTransferCommandResponse findById(String transferCommandName) {
        if (!StringUtils.hasText(transferCommandName)) {
            throw new IllegalArgumentException("조회할 반송 명령 명이 누락되었습니다.");
        }

        Optional<WcsTransferCommand> optionalCommand = wcsTransferCommandRepository.findById(transferCommandName.trim());
        if (optionalCommand.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 반송 명령이 존재하지 않습니다. [transferCommandName: " + transferCommandName + "]");
        }

        return WcsTransferCommandResponse.fromDomain(optionalCommand.get());
    }

    /**
     * 신규 WCS 반송 명령 등록 (CREATE)
     * - 단일 PK 중복 검증
     * - 도메인 내부 WcsTransferCommand.create(command) 호출
     * - TRANSFERCOMMAND 저장 및 W_TRANSFER_COMMAND_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsTransferCommandResponse createTransferCommand(WcsTransferCommandCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("반송 명령 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getTransferCommandName())) {
            throw new IllegalArgumentException("반송 명령 명은 필수 입력 항목입니다.");
        }

        String transferCommandName = dto.getTransferCommandName().trim();

        // 1. 단일 PK 중복 검증
        if (wcsTransferCommandRepository.existsById(transferCommandName)) {
            throw new IllegalArgumentException("이미 등록된 WCS 반송 명령입니다. [transferCommandName: " + transferCommandName + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsTransferCommandCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs transfer command created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsTransferCommandCreateCommand command = WcsTransferCommandCreateCommand.builder()
                .transactionInfo(tx)
                .transferCommandName(transferCommandName)
                .carrierName(dto.getCarrierName())
                .commandStatus(dto.getCommandStatus())
                .createTime(dto.getCreateTime())
                .currentEquipmentName(dto.getCurrentEquipmentName())
                .hotLot(dto.getHotLot())
                .jobCompletedTime(dto.getJobCompletedTime())
                .jobReceiveTime(dto.getJobReceiveTime())
                .jobStartTime(dto.getJobStartTime())
                .lotName(dto.getLotName())
                .owner(dto.getOwner())
                .priority(dto.getPriority())
                .productQuantity(dto.getProductQuantity())
                .source(dto.getSource())
                .target(dto.getTarget())
                .targetEquipmentName(dto.getTargetEquipmentName())
                .factoryName(dto.getFactoryName())
                .currentSource(dto.getCurrentSource())
                .orderType(dto.getOrderType())
                .sourceEquipmentName(dto.getSourceEquipmentName())
                .sourceTransferType(dto.getSourceTransferType())
                .targetTransferType(dto.getTargetTransferType())
                .subCommandJobNo(dto.getSubCommandJobNo())
                .subCommandStatus(dto.getSubCommandStatus())
                .transferSpeed(dto.getTransferSpeed())
                .processType(dto.getProcessType())
                .startReportFlag(dto.getStartReportFlag())
                .build();

        WcsTransferCommand transferCommand = WcsTransferCommand.create(command);

        // 4. TRANSFERCOMMAND 테이블 저장
        WcsTransferCommand savedCommand = wcsTransferCommandRepository.save(transferCommand);

        // 5. W_TRANSFER_COMMAND_HISTORY 테이블 이력 적재
        WcsTransferCommandHistoryEntity historyEntity = wcsTransferCommandMapper.toHistoryEntity(savedCommand);
        wcsTransferCommandHistoryJpaRepository.save(historyEntity);

        log.info("WcsTransferCommand created successfully: [transferCommandName={}]", transferCommandName);

        return WcsTransferCommandResponse.fromDomain(savedCommand);
    }

    /**
     * 단일 PK 기반 WCS 반송 명령 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 transferCommand.update(command) 호출
     * - TRANSFERCOMMAND 저장 및 W_TRANSFER_COMMAND_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsTransferCommandResponse updateTransferCommand(String transferCommandName, WcsTransferCommandUpdateRequestDto dto) {
        if (!StringUtils.hasText(transferCommandName)) {
            throw new IllegalArgumentException("수정할 대상 반송 명령 명이 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 반송 명령 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsTransferCommand> optionalCommand = wcsTransferCommandRepository.findById(transferCommandName.trim());
        if (optionalCommand.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 반송 명령이 존재하지 않습니다. [transferCommandName: " + transferCommandName + "]");
        }

        WcsTransferCommand commandDomain = optionalCommand.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsTransferCommandModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs transfer command modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsTransferCommandUpdateCommand command = WcsTransferCommandUpdateCommand.builder()
                .transactionInfo(tx)
                .carrierName(dto.getCarrierName())
                .commandStatus(dto.getCommandStatus())
                .createTime(dto.getCreateTime())
                .currentEquipmentName(dto.getCurrentEquipmentName())
                .hotLot(dto.getHotLot())
                .jobCompletedTime(dto.getJobCompletedTime())
                .jobReceiveTime(dto.getJobReceiveTime())
                .jobStartTime(dto.getJobStartTime())
                .lotName(dto.getLotName())
                .owner(dto.getOwner())
                .priority(dto.getPriority())
                .productQuantity(dto.getProductQuantity())
                .source(dto.getSource())
                .target(dto.getTarget())
                .targetEquipmentName(dto.getTargetEquipmentName())
                .factoryName(dto.getFactoryName())
                .currentSource(dto.getCurrentSource())
                .orderType(dto.getOrderType())
                .sourceEquipmentName(dto.getSourceEquipmentName())
                .sourceTransferType(dto.getSourceTransferType())
                .targetTransferType(dto.getTargetTransferType())
                .subCommandJobNo(dto.getSubCommandJobNo())
                .subCommandStatus(dto.getSubCommandStatus())
                .transferSpeed(dto.getTransferSpeed())
                .processType(dto.getProcessType())
                .startReportFlag(dto.getStartReportFlag())
                .build();

        commandDomain.update(command);

        // 4. TRANSFERCOMMAND 테이블 저장
        WcsTransferCommand updatedCommand = wcsTransferCommandRepository.save(commandDomain);

        // 5. W_TRANSFER_COMMAND_HISTORY 테이블 이력 적재
        WcsTransferCommandHistoryEntity historyEntity = wcsTransferCommandMapper.toHistoryEntity(updatedCommand);
        wcsTransferCommandHistoryJpaRepository.save(historyEntity);

        log.info("WcsTransferCommand updated successfully: [transferCommandName={}]", transferCommandName);

        return WcsTransferCommandResponse.fromDomain(updatedCommand);
    }

    /**
     * 단일 PK 기반 WCS 반송 명령 삭제 (DELETE)
     * - 대상 조회
     * - W_TRANSFER_COMMAND_HISTORY 테이블에 삭제 이력 적재
     * - TRANSFERCOMMAND 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteTransferCommand(String transferCommandName, String eventUser, String eventComment) {
        if (!StringUtils.hasText(transferCommandName)) {
            throw new IllegalArgumentException("삭제할 대상 반송 명령 명이 누락되었습니다.");
        }

        Optional<WcsTransferCommand> optionalCommand = wcsTransferCommandRepository.findById(transferCommandName.trim());
        if (optionalCommand.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 반송 명령이 존재하지 않습니다. [transferCommandName: " + transferCommandName + "]");
        }

        WcsTransferCommand commandDomain = optionalCommand.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        commandDomain.setLastEventName("WcsTransferCommandDeleted");
        commandDomain.setLastEventTime(now);
        commandDomain.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        commandDomain.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs transfer command deleted");

        // 2. 삭제 이력 적재
        WcsTransferCommandHistoryEntity historyEntity = wcsTransferCommandMapper.toHistoryEntity(commandDomain);
        wcsTransferCommandHistoryJpaRepository.save(historyEntity);

        // 3. 반송 명령 삭제
        wcsTransferCommandRepository.deleteById(transferCommandName.trim());

        log.info("WcsTransferCommand deleted successfully: [transferCommandName={}]", transferCommandName);
    }
}
