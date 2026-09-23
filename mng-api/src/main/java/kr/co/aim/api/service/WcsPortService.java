package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsPortCreateRequestDto;
import kr.co.aim.api.dto.WcsPortResponse;
import kr.co.aim.api.dto.WcsPortUpdateRequestDto;
import kr.co.aim.common.condition.WcsPortSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsPortCreateCommand;
import kr.co.aim.domain.command.WcsPortUpdateCommand;
import kr.co.aim.domain.model.WcsPort;
import kr.co.aim.domain.repository.WcsPortRepository;
import kr.co.aim.infra.persistence.entity.WcsPortHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsPortMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsPortHistoryJpaRepository;
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
public class WcsPortService {

    private final WcsPortRepository wcsPortRepository;
    private final WcsPortHistoryJpaRepository wcsPortHistoryJpaRepository;
    private final WcsPortMapper wcsPortMapper;

    /**
     * 조건 및 페이징 기반 WCS 포트 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsPortResponse> findPorts(WcsPortSearchCondition condition, Pageable pageable) {
        Page<WcsPort> pageResult = wcsPortRepository.findPorts(condition, pageable);
        List<WcsPortResponse> content = new ArrayList<>();

        for (WcsPort port : pageResult.getContent()) {
            if (port != null) {
                content.add(WcsPortResponse.fromDomain(port));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 4개 복합키 기반 WCS 포트 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsPortResponse findById(String factoryName, String equipmentName, Integer localNo, Integer portNumber) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || localNo == null || portNumber == null) {
            throw new IllegalArgumentException("조회할 포트 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsPort> optionalPort = wcsPortRepository.findById(
                factoryName.trim(), equipmentName.trim(), localNo, portNumber);
        if (optionalPort.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 포트가 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", localNo: " + localNo + ", portNumber: " + portNumber + "]");
        }

        return WcsPortResponse.fromDomain(optionalPort.get());
    }

    /**
     * 신규 WCS 포트 등록 (CREATE)
     * - 4개 복합키 중복 검증
     * - 도메인 내부 WcsPort.create(command) 호출
     * - PORT 저장 및 W_PORT_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsPortResponse createPort(WcsPortCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("포트 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getEquipmentName())) {
            throw new IllegalArgumentException("설비 명은 필수 입력 항목입니다.");
        }
        if (dto.getLocalNo() == null) {
            throw new IllegalArgumentException("로컬 번호는 필수 입력 항목입니다.");
        }
        if (dto.getPortNumber() == null) {
            throw new IllegalArgumentException("포트 번호는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String equipmentName = dto.getEquipmentName().trim();
        Integer localNo = dto.getLocalNo();
        Integer portNumber = dto.getPortNumber();

        // 1. 복합키 중복 검증
        if (wcsPortRepository.existsById(factoryName, equipmentName, localNo, portNumber)) {
            throw new IllegalArgumentException("이미 등록된 WCS 포트입니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", localNo: " + localNo + ", portNumber: " + portNumber + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsPortCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs port created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsPortCreateCommand command = WcsPortCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .localNo(localNo)
                .portNumber(portNumber)
                .carrierName(dto.getCarrierName())
                .errorHappen(dto.getErrorHappen())
                .portContainStatus(dto.getPortContainStatus())
                .portEnableMode(dto.getPortEnableMode())
                .portName(dto.getPortName())
                .portStatus(dto.getPortStatus())
                .portTransferStatus(dto.getPortTransferStatus())
                .portType(dto.getPortType())
                .touchPanelNumber(dto.getTouchPanelNumber())
                .zoneName(dto.getZoneName())
                .bin(dto.getBin())
                .row(dto.getRow())
                .stage(dto.getStage())
                .col(dto.getCol())
                .linkEquipmentName(dto.getLinkEquipmentName())
                .linkPortName(dto.getLinkPortName())
                .linkPortType(dto.getLinkPortType())
                .portTransferMode(dto.getPortTransferMode())
                .portReadingEnableMode(dto.getPortReadingEnableMode())
                .portDetailType(dto.getPortDetailType())
                .rejectEquipmentName(dto.getRejectEquipmentName())
                .rejectPortName(dto.getRejectPortName())
                .useWorkerFlag(dto.getUseWorkerFlag())
                .portUseType(dto.getPortUseType())
                .portMode(dto.getPortMode())
                .portOperationMode(dto.getPortOperationMode())
                .portRfidEnableMode(dto.getPortRfidEnableMode())
                .build();

        WcsPort port = WcsPort.create(command);

        // 4. PORT 테이블 저장
        WcsPort savedPort = wcsPortRepository.save(port);

        // 5. W_PORT_HISTORY 테이블 이력 적재
        WcsPortHistoryEntity historyEntity = wcsPortMapper.toHistoryEntity(savedPort);
        wcsPortHistoryJpaRepository.save(historyEntity);

        log.info("WcsPort created successfully: [factoryName={}, equipmentName={}, localNo={}, portNumber={}]",
                factoryName, equipmentName, localNo, portNumber);

        return WcsPortResponse.fromDomain(savedPort);
    }

    /**
     * 4개 복합키 기반 WCS 포트 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 port.update(command) 호출
     * - PORT 저장 및 W_PORT_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsPortResponse updatePort(String factoryName, String equipmentName, Integer localNo, Integer portNumber,
                                      WcsPortUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || localNo == null || portNumber == null) {
            throw new IllegalArgumentException("수정할 대상 포트 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 포트 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsPort> optionalPort = wcsPortRepository.findById(
                factoryName.trim(), equipmentName.trim(), localNo, portNumber);
        if (optionalPort.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 포트가 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", localNo: " + localNo + ", portNumber: " + portNumber + "]");
        }

        WcsPort port = optionalPort.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsPortModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs port modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsPortUpdateCommand command = WcsPortUpdateCommand.builder()
                .transactionInfo(tx)
                .carrierName(dto.getCarrierName())
                .errorHappen(dto.getErrorHappen())
                .portContainStatus(dto.getPortContainStatus())
                .portEnableMode(dto.getPortEnableMode())
                .portName(dto.getPortName())
                .portStatus(dto.getPortStatus())
                .portTransferStatus(dto.getPortTransferStatus())
                .portType(dto.getPortType())
                .touchPanelNumber(dto.getTouchPanelNumber())
                .zoneName(dto.getZoneName())
                .bin(dto.getBin())
                .row(dto.getRow())
                .stage(dto.getStage())
                .col(dto.getCol())
                .linkEquipmentName(dto.getLinkEquipmentName())
                .linkPortName(dto.getLinkPortName())
                .linkPortType(dto.getLinkPortType())
                .portTransferMode(dto.getPortTransferMode())
                .portReadingEnableMode(dto.getPortReadingEnableMode())
                .portDetailType(dto.getPortDetailType())
                .rejectEquipmentName(dto.getRejectEquipmentName())
                .rejectPortName(dto.getRejectPortName())
                .useWorkerFlag(dto.getUseWorkerFlag())
                .portUseType(dto.getPortUseType())
                .portMode(dto.getPortMode())
                .portOperationMode(dto.getPortOperationMode())
                .portRfidEnableMode(dto.getPortRfidEnableMode())
                .build();

        port.update(command);

        // 4. PORT 테이블 저장
        WcsPort updatedPort = wcsPortRepository.save(port);

        // 5. W_PORT_HISTORY 테이블 이력 적재
        WcsPortHistoryEntity historyEntity = wcsPortMapper.toHistoryEntity(updatedPort);
        wcsPortHistoryJpaRepository.save(historyEntity);

        log.info("WcsPort updated successfully: [factoryName={}, equipmentName={}, localNo={}, portNumber={}]",
                factoryName, equipmentName, localNo, portNumber);

        return WcsPortResponse.fromDomain(updatedPort);
    }

    /**
     * 4개 복합키 기반 WCS 포트 삭제 (DELETE)
     * - 대상 조회
     * - W_PORT_HISTORY 테이블에 삭제 이력 적재
     * - PORT 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deletePort(String factoryName, String equipmentName, Integer localNo, Integer portNumber,
                           String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || localNo == null || portNumber == null) {
            throw new IllegalArgumentException("삭제할 대상 포트 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsPort> optionalPort = wcsPortRepository.findById(
                factoryName.trim(), equipmentName.trim(), localNo, portNumber);
        if (optionalPort.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 포트가 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", localNo: " + localNo + ", portNumber: " + portNumber + "]");
        }

        WcsPort port = optionalPort.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        port.setLastEventName("WcsPortDeleted");
        port.setLastEventTime(now);
        port.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        port.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs port deleted");

        // 2. 삭제 이력 적재
        WcsPortHistoryEntity historyEntity = wcsPortMapper.toHistoryEntity(port);
        wcsPortHistoryJpaRepository.save(historyEntity);

        // 3. 포트 삭제
        wcsPortRepository.deleteById(factoryName.trim(), equipmentName.trim(), localNo, portNumber);

        log.info("WcsPort deleted successfully: [factoryName={}, equipmentName={}, localNo={}, portNumber={}]",
                factoryName, equipmentName, localNo, portNumber);
    }
}
