package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsAlarmCreateRequestDto;
import kr.co.aim.api.dto.WcsAlarmResponse;
import kr.co.aim.api.dto.WcsAlarmUpdateRequestDto;
import kr.co.aim.common.condition.WcsAlarmSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsAlarmCreateCommand;
import kr.co.aim.domain.command.WcsAlarmUpdateCommand;
import kr.co.aim.domain.model.WcsAlarm;
import kr.co.aim.domain.repository.WcsAlarmRepository;
import kr.co.aim.infra.persistence.entity.WcsAlarmHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsAlarmMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsAlarmHistoryJpaRepository;
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
public class WcsAlarmService {

    private final WcsAlarmRepository wcsAlarmRepository;
    private final WcsAlarmHistoryJpaRepository wcsAlarmHistoryJpaRepository;
    private final WcsAlarmMapper wcsAlarmMapper;

    /**
     * 조건 및 페이징 기반 WCS 알람 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsAlarmResponse> findAlarms(WcsAlarmSearchCondition condition, Pageable pageable) {
        Page<WcsAlarm> pageResult = wcsAlarmRepository.findAlarms(condition, pageable);
        List<WcsAlarmResponse> content = new ArrayList<>();

        for (WcsAlarm alarm : pageResult.getContent()) {
            if (alarm != null) {
                content.add(WcsAlarmResponse.fromDomain(alarm));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 5개 복합키 기반 WCS 알람 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsAlarmResponse findById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName) || !StringUtils.hasText(alarmId)
                || layerNumber == null || !StringUtils.hasText(layerType)) {
            throw new IllegalArgumentException("조회할 알람 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsAlarm> optionalAlarm = wcsAlarmRepository.findById(
                factoryName.trim(), equipmentName.trim(), alarmId.trim(), layerNumber, layerType.trim());
        if (optionalAlarm.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 알람이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", alarmId: " + alarmId
                    + ", layerNumber: " + layerNumber + ", layerType: " + layerType + "]");
        }

        return WcsAlarmResponse.fromDomain(optionalAlarm.get());
    }

    /**
     * 신규 WCS 알람 등록 (CREATE)
     * - 5개 복합키 중복 검증
     * - 도메인 내부 WcsAlarm.create(command) 호출
     * - ALARM 저장 및 W_ALARM_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsAlarmResponse createAlarm(WcsAlarmCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("알람 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getEquipmentName())) {
            throw new IllegalArgumentException("설비 명은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getAlarmId())) {
            throw new IllegalArgumentException("알람 ID는 필수 입력 항목입니다.");
        }
        if (dto.getLayerNumber() == null) {
            throw new IllegalArgumentException("레이어 번호는 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getLayerType())) {
            throw new IllegalArgumentException("레이어 구분은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String equipmentName = dto.getEquipmentName().trim();
        String alarmId = dto.getAlarmId().trim();
        Integer layerNumber = dto.getLayerNumber();
        String layerType = dto.getLayerType().trim();

        // 1. 복합키 중복 검증
        if (wcsAlarmRepository.existsById(factoryName, equipmentName, alarmId, layerNumber, layerType)) {
            throw new IllegalArgumentException("이미 등록된 WCS 알람입니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", alarmId: " + alarmId
                    + ", layerNumber: " + layerNumber + ", layerType: " + layerType + "]");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsAlarmCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs alarm created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsAlarmCreateCommand command = WcsAlarmCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .alarmId(alarmId)
                .layerNumber(layerNumber)
                .layerType(layerType)
                .alarmLevel(dto.getAlarmLevel())
                .alarmRecoveryOptions(dto.getAlarmRecoveryOptions())
                .alarmText(dto.getAlarmText())
                .layerName(dto.getLayerName())
                .systemAlarmFlag(dto.getSystemAlarmFlag())
                .build();

        WcsAlarm alarm = WcsAlarm.create(command);

        // 4. ALARM 테이블 저장
        WcsAlarm savedAlarm = wcsAlarmRepository.save(alarm);

        // 5. W_ALARM_HISTORY 테이블 이력 적재
        WcsAlarmHistoryEntity historyEntity = wcsAlarmMapper.toHistoryEntity(savedAlarm);
        wcsAlarmHistoryJpaRepository.save(historyEntity);

        log.info("WcsAlarm created successfully: [factoryName={}, equipmentName={}, alarmId={}, layerNumber={}, layerType={}]",
                factoryName, equipmentName, alarmId, layerNumber, layerType);

        return WcsAlarmResponse.fromDomain(savedAlarm);
    }

    /**
     * 5개 복합키 기반 WCS 알람 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 alarm.update(command) 호출
     * - ALARM 저장 및 W_ALARM_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsAlarmResponse updateAlarm(String factoryName, String equipmentName, String alarmId,
                                        Integer layerNumber, String layerType, WcsAlarmUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName) || !StringUtils.hasText(alarmId)
                || layerNumber == null || !StringUtils.hasText(layerType)) {
            throw new IllegalArgumentException("수정할 대상 알람 복합키 식별자 정보가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 알람 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsAlarm> optionalAlarm = wcsAlarmRepository.findById(
                factoryName.trim(), equipmentName.trim(), alarmId.trim(), layerNumber, layerType.trim());
        if (optionalAlarm.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 알람이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", alarmId: " + alarmId
                    + ", layerNumber: " + layerNumber + ", layerType: " + layerType + "]");
        }

        WcsAlarm alarm = optionalAlarm.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsAlarmModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs alarm modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsAlarmUpdateCommand command = WcsAlarmUpdateCommand.builder()
                .transactionInfo(tx)
                .alarmLevel(dto.getAlarmLevel())
                .alarmRecoveryOptions(dto.getAlarmRecoveryOptions())
                .alarmText(dto.getAlarmText())
                .layerName(dto.getLayerName())
                .systemAlarmFlag(dto.getSystemAlarmFlag())
                .build();

        alarm.update(command);

        // 4. ALARM 테이블 저장
        WcsAlarm updatedAlarm = wcsAlarmRepository.save(alarm);

        // 5. W_ALARM_HISTORY 테이블 이력 적재
        WcsAlarmHistoryEntity historyEntity = wcsAlarmMapper.toHistoryEntity(updatedAlarm);
        wcsAlarmHistoryJpaRepository.save(historyEntity);

        log.info("WcsAlarm updated successfully: [factoryName={}, equipmentName={}, alarmId={}, layerNumber={}, layerType={}]",
                factoryName, equipmentName, alarmId, layerNumber, layerType);

        return WcsAlarmResponse.fromDomain(updatedAlarm);
    }

    /**
     * 5개 복합키 기반 WCS 알람 삭제 (DELETE)
     * - 대상 조회
     * - W_ALARM_HISTORY 테이블에 삭제 이력 적재
     * - ALARM 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAlarm(String factoryName, String equipmentName, String alarmId,
                            Integer layerNumber, String layerType, String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName) || !StringUtils.hasText(alarmId)
                || layerNumber == null || !StringUtils.hasText(layerType)) {
            throw new IllegalArgumentException("삭제할 대상 알람 복합키 식별자 정보가 누락되었습니다.");
        }

        Optional<WcsAlarm> optionalAlarm = wcsAlarmRepository.findById(
                factoryName.trim(), equipmentName.trim(), alarmId.trim(), layerNumber, layerType.trim());
        if (optionalAlarm.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 알람이 존재하지 않습니다. [factoryName: " + factoryName
                    + ", equipmentName: " + equipmentName + ", alarmId: " + alarmId
                    + ", layerNumber: " + layerNumber + ", layerType: " + layerType + "]");
        }

        WcsAlarm alarm = optionalAlarm.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        alarm.setLastEventName("WcsAlarmDeleted");
        alarm.setLastEventTime(now);
        alarm.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        alarm.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs alarm deleted");

        // 2. 삭제 이력 적재
        WcsAlarmHistoryEntity historyEntity = wcsAlarmMapper.toHistoryEntity(alarm);
        wcsAlarmHistoryJpaRepository.save(historyEntity);

        // 3. 알람 삭제
        wcsAlarmRepository.deleteById(factoryName.trim(), equipmentName.trim(), alarmId.trim(), layerNumber, layerType.trim());

        log.info("WcsAlarm deleted successfully: [factoryName={}, equipmentName={}, alarmId={}, layerNumber={}, layerType={}]",
                factoryName, equipmentName, alarmId, layerNumber, layerType);
    }
}
