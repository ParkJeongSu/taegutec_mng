package kr.co.aim.api.service;

import kr.co.aim.api.dto.ShelfCreateRequestDto;
import kr.co.aim.api.dto.ShelfResponse;
import kr.co.aim.api.dto.ShelfUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ShelfCreateCommand;
import kr.co.aim.domain.command.ShelfUpdateCommand;
import kr.co.aim.domain.model.Shelf;
import kr.co.aim.domain.repository.ShelfRepository;
import kr.co.aim.domain.repository.condition.ShelfSearchCondition;
import kr.co.aim.infra.persistence.entity.ShelfHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ShelfMapper;
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
public class ShelfService {

    private final ShelfRepository shelfRepository;
    private final HistoryService historyService;
    private final ShelfMapper shelfMapper;

    /**
     * 조건 및 페이징 기반 선반(SHELF) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<ShelfResponse> findShelfs(ShelfSearchCondition condition, Pageable pageable) {
        Page<Shelf> pageResult = shelfRepository.findShelfsWithConditions(condition, pageable);
        List<ShelfResponse> content = new ArrayList<>();
        for (Shelf item : pageResult.getContent()) {
            content.add(ShelfResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 선반(SHELF) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public ShelfResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 선반(SHELF) ID가 유효하지 않습니다.");
        }
        Optional<Shelf> optional = shelfRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 선반(SHELF)이(가) 존재하지 않습니다. ID: " + id);
        }
        return ShelfResponse.fromDomain(optional.get());
    }

    /**
     * 신규 선반(SHELF) 등록 (CREATE) 및 SHELF_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ShelfResponse createShelf(ShelfCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("선반(SHELF) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getLocationId() == null || dto.getLocationId().trim().isEmpty()) {
            throw new IllegalArgumentException("위치 ID은(는) 필수 입력 항목입니다.");
        }

        String locationIdValue = dto.getLocationId().trim();
        Optional<Shelf> existing = shelfRepository.findByLocationId(locationIdValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 선반(SHELF)입니다: " + locationIdValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ShelfCreated";
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
            eventComment = "Shelf created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String locationId = null;
        if (dto.getLocationId() != null) {
            locationId = dto.getLocationId().trim();
        }

        String equipmentName = null;
        if (dto.getEquipmentName() != null) {
            equipmentName = dto.getEquipmentName().trim();
        }

        String shelfState = null;
        if (dto.getShelfState() != null) {
            shelfState = dto.getShelfState().trim();
        }

        String communicationState = null;
        if (dto.getCommunicationState() != null) {
            communicationState = dto.getCommunicationState().trim();
        }

        String holdState = null;
        if (dto.getHoldState() != null) {
            holdState = dto.getHoldState().trim();
        }

        ShelfCreateCommand command = ShelfCreateCommand.builder()
                .transactionInfo(tx)
                .locationId(locationId)
                .equipmentName(equipmentName)
                .zoneId(dto.getZoneId())
                .areaId(dto.getAreaId())
                .capacity(dto.getCapacity())
                .shelfState(shelfState)
                .communicationState(communicationState)
                .holdState(holdState)
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .build();

        Shelf shelf = Shelf.create(command);
        Shelf saved = shelfRepository.save(shelf);

        ShelfHistoryEntity historyEntity = shelfMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("Shelf created successfully: [id={}]", saved.getId());

        return ShelfResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 선반(SHELF) 수정 (UPDATE) 및 SHELF_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public ShelfResponse updateShelf(Long id, ShelfUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 선반(SHELF) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 선반(SHELF) 정보가 유효하지 않습니다.");
        }

        Optional<Shelf> optional = shelfRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 선반(SHELF)이(가) 존재하지 않습니다. ID: " + id);
        }

        Shelf shelf = optional.get();

        if (dto.getLocationId() != null && !dto.getLocationId().trim().isEmpty()) {
            String targetName = dto.getLocationId().trim();
            if (!targetName.equals(shelf.getLocationId())) {
                Optional<Shelf> duplicate = shelfRepository.findByLocationId(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 선반(SHELF)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "ShelfModified";
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
            eventComment = "Shelf modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String locationId = null;
        if (dto.getLocationId() != null) {
            locationId = dto.getLocationId().trim();
        }

        String equipmentName = null;
        if (dto.getEquipmentName() != null) {
            equipmentName = dto.getEquipmentName().trim();
        }

        String shelfState = null;
        if (dto.getShelfState() != null) {
            shelfState = dto.getShelfState().trim();
        }

        String communicationState = null;
        if (dto.getCommunicationState() != null) {
            communicationState = dto.getCommunicationState().trim();
        }

        String holdState = null;
        if (dto.getHoldState() != null) {
            holdState = dto.getHoldState().trim();
        }

        ShelfUpdateCommand command = ShelfUpdateCommand.builder()
                .transactionInfo(tx)
                .locationId(locationId)
                .equipmentName(equipmentName)
                .zoneId(dto.getZoneId())
                .areaId(dto.getAreaId())
                .capacity(dto.getCapacity())
                .shelfState(shelfState)
                .communicationState(communicationState)
                .holdState(holdState)
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .build();

        shelf.update(command);
        Shelf updated = shelfRepository.save(shelf);

        ShelfHistoryEntity historyEntity = shelfMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("Shelf updated successfully: [id={}]", updated.getId());

        return ShelfResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 선반(SHELF) 단건 삭제 (DELETE) 및 SHELF_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteShelf(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Shelf> optional = shelfRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 선반(SHELF)이(가) 존재하지 않습니다. ID: " + id);
        }

        Shelf shelf = optional.get();

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
            comment = "Shelf deleted";
        }

        shelf.setEventName("ShelfDeleted");
        shelf.setEventTime(now);
        shelf.setEventUser(user);
        shelf.setEventComment(comment);

        ShelfHistoryEntity historyEntity = shelfMapper.toHistoryEntity(shelf);
        historyService.saveHistory(historyEntity);

        shelfRepository.deleteById(id);

        log.info("Shelf deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 선반(SHELF) 벌크 삭제 (DELETE batch) 및 SHELF_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteShelfs(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch Shelfs deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Shelf> optional = shelfRepository.findById(id);
            if (optional.isPresent()) {
                Shelf shelf = optional.get();
                shelf.setEventName("ShelfDeleted");
                shelf.setEventTime(now);
                shelf.setEventUser(user);
                shelf.setEventComment(comment);

                ShelfHistoryEntity historyEntity = shelfMapper.toHistoryEntity(shelf);
                historyService.saveHistory(historyEntity);
            }
        }

        shelfRepository.deleteAllByIdInBatch(ids);
        log.info("Shelfs batch deleted successfully: [count={}]", ids.size());
    }
}
