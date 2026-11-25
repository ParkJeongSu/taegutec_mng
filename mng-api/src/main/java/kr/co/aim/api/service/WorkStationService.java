package kr.co.aim.api.service;

import kr.co.aim.api.dto.WorkStationCreateRequestDto;
import kr.co.aim.api.dto.WorkStationResponse;
import kr.co.aim.api.dto.WorkStationUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WorkStationCreateCommand;
import kr.co.aim.domain.command.WorkStationUpdateCommand;
import kr.co.aim.domain.model.WorkStation;
import kr.co.aim.domain.repository.WorkStationRepository;
import kr.co.aim.domain.repository.condition.WorkStationSearchCondition;
import kr.co.aim.infra.persistence.entity.WorkStationHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WorkStationMapper;
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
public class WorkStationService {

    private final WorkStationRepository workStationRepository;
    private final HistoryService historyService;
    private final WorkStationMapper workStationMapper;

    /**
     * 조건 및 페이징 기반 작업 스테이션(WORK_STATION) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WorkStationResponse> findWorkStations(WorkStationSearchCondition condition, Pageable pageable) {
        Page<WorkStation> pageResult = workStationRepository.findWorkStationsWithConditions(condition, pageable);
        List<WorkStationResponse> content = new ArrayList<>();
        for (WorkStation item : pageResult.getContent()) {
            content.add(WorkStationResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 작업 스테이션(WORK_STATION) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WorkStationResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 작업 스테이션(WORK_STATION) ID가 유효하지 않습니다.");
        }
        Optional<WorkStation> optional = workStationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 작업 스테이션(WORK_STATION)이(가) 존재하지 않습니다. ID: " + id);
        }
        return WorkStationResponse.fromDomain(optional.get());
    }

    /**
     * 신규 작업 스테이션(WORK_STATION) 등록 (CREATE) 및 WORK_STATION_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WorkStationResponse createWorkStation(WorkStationCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("작업 스테이션(WORK_STATION) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getWorkStationName() == null || dto.getWorkStationName().trim().isEmpty()) {
            throw new IllegalArgumentException("작업 스테이션명은(는) 필수 입력 항목입니다.");
        }

        String workStationNameValue = dto.getWorkStationName().trim();
        Optional<WorkStation> existing = workStationRepository.findByWorkStationName(workStationNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 작업 스테이션(WORK_STATION)입니다: " + workStationNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "WorkStationCreated";
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
            eventComment = "WorkStation created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String workStationName = null;
        if (dto.getWorkStationName() != null) {
            workStationName = dto.getWorkStationName().trim();
        }

        String equipmentName = null;
        if (dto.getEquipmentName() != null) {
            equipmentName = dto.getEquipmentName().trim();
        }

        WorkStationCreateCommand command = WorkStationCreateCommand.builder()
                .transactionInfo(tx)
                .workStationName(workStationName)
                .equipmentName(equipmentName)
                .build();

        WorkStation workStation = WorkStation.create(command);
        WorkStation saved = workStationRepository.save(workStation);

        WorkStationHistoryEntity historyEntity = workStationMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("WorkStation created successfully: [id={}]", saved.getId());

        return WorkStationResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 작업 스테이션(WORK_STATION) 수정 (UPDATE) 및 WORK_STATION_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WorkStationResponse updateWorkStation(Long id, WorkStationUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 작업 스테이션(WORK_STATION) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 작업 스테이션(WORK_STATION) 정보가 유효하지 않습니다.");
        }

        Optional<WorkStation> optional = workStationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 작업 스테이션(WORK_STATION)이(가) 존재하지 않습니다. ID: " + id);
        }

        WorkStation workStation = optional.get();

        if (dto.getWorkStationName() != null && !dto.getWorkStationName().trim().isEmpty()) {
            String targetName = dto.getWorkStationName().trim();
            if (!targetName.equals(workStation.getWorkStationName())) {
                Optional<WorkStation> duplicate = workStationRepository.findByWorkStationName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 작업 스테이션(WORK_STATION)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "WorkStationModified";
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
            eventComment = "WorkStation modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String workStationName = null;
        if (dto.getWorkStationName() != null) {
            workStationName = dto.getWorkStationName().trim();
        }

        String equipmentName = null;
        if (dto.getEquipmentName() != null) {
            equipmentName = dto.getEquipmentName().trim();
        }

        WorkStationUpdateCommand command = WorkStationUpdateCommand.builder()
                .transactionInfo(tx)
                .workStationName(workStationName)
                .equipmentName(equipmentName)
                .build();

        workStation.update(command);
        WorkStation updated = workStationRepository.save(workStation);

        WorkStationHistoryEntity historyEntity = workStationMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("WorkStation updated successfully: [id={}]", updated.getId());

        return WorkStationResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 작업 스테이션(WORK_STATION) 단건 삭제 (DELETE) 및 WORK_STATION_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteWorkStation(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<WorkStation> optional = workStationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 작업 스테이션(WORK_STATION)이(가) 존재하지 않습니다. ID: " + id);
        }

        WorkStation workStation = optional.get();

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
            comment = "WorkStation deleted";
        }

        workStation.setEventName("WorkStationDeleted");
        workStation.setEventTime(now);
        workStation.setEventUser(user);
        workStation.setEventComment(comment);

        WorkStationHistoryEntity historyEntity = workStationMapper.toHistoryEntity(workStation);
        historyService.saveHistory(historyEntity);

        workStationRepository.deleteById(id);

        log.info("WorkStation deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 작업 스테이션(WORK_STATION) 벌크 삭제 (DELETE batch) 및 WORK_STATION_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteWorkStations(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch WorkStations deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<WorkStation> optional = workStationRepository.findById(id);
            if (optional.isPresent()) {
                WorkStation workStation = optional.get();
                workStation.setEventName("WorkStationDeleted");
                workStation.setEventTime(now);
                workStation.setEventUser(user);
                workStation.setEventComment(comment);

                WorkStationHistoryEntity historyEntity = workStationMapper.toHistoryEntity(workStation);
                historyService.saveHistory(historyEntity);
            }
        }

        workStationRepository.deleteAllByIdInBatch(ids);
        log.info("WorkStations batch deleted successfully: [count={}]", ids.size());
    }
}
