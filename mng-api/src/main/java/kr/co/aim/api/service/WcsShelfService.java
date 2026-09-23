package kr.co.aim.api.service;

import kr.co.aim.api.dto.WcsShelfCreateRequestDto;
import kr.co.aim.api.dto.WcsShelfResponse;
import kr.co.aim.api.dto.WcsShelfUpdateRequestDto;
import kr.co.aim.common.condition.WcsShelfSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.WcsShelfCreateCommand;
import kr.co.aim.domain.command.WcsShelfUpdateCommand;
import kr.co.aim.domain.model.WcsShelf;
import kr.co.aim.domain.repository.WcsShelfRepository;
import kr.co.aim.infra.persistence.entity.WcsShelfHistoryEntity;
import kr.co.aim.infra.persistence.mapper.WcsShelfMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsShelfHistoryJpaRepository;
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
public class WcsShelfService {

    private final WcsShelfRepository wcsShelfRepository;
    private final WcsShelfHistoryJpaRepository wcsShelfHistoryJpaRepository;
    private final WcsShelfMapper wcsShelfMapper;

    /**
     * 조건 및 페이징 기반 WCS 셸프 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<WcsShelfResponse> findShelves(WcsShelfSearchCondition condition, Pageable pageable) {
        Page<WcsShelf> pageResult = wcsShelfRepository.findShelves(condition, pageable);
        List<WcsShelfResponse> content = new ArrayList<>();

        for (WcsShelf shelf : pageResult.getContent()) {
            if (shelf != null) {
                content.add(WcsShelfResponse.fromDomain(shelf));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 복합키(factoryName, stockerName, shelfName) 기반 WCS 셸프 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public WcsShelfResponse findById(String factoryName, String stockerName, String shelfName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(shelfName)) {
            throw new IllegalArgumentException("조회할 공장 구분, 스토커 명 및 셸프 명이 누락되었습니다.");
        }

        Optional<WcsShelf> optionalShelf = wcsShelfRepository.findById(factoryName.trim(), stockerName.trim(), shelfName.trim());
        if (optionalShelf.isEmpty()) {
            throw new IllegalArgumentException("해당 WCS 셸프가 존재하지 않습니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ", shelfName: " + shelfName + ")");
        }

        return WcsShelfResponse.fromDomain(optionalShelf.get());
    }

    /**
     * 신규 WCS 셸프 등록 (CREATE)
     * - 복합키(factoryName, stockerName, shelfName) 중복 검증
     * - 도메인 내부 WcsShelf.create(command) 호출
     * - SHELF 저장 및 SHELF_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsShelfResponse createShelf(WcsShelfCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("셸프 등록 정보가 유효하지 않습니다.");
        }
        if (!StringUtils.hasText(dto.getFactoryName())) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getStockerName())) {
            throw new IllegalArgumentException("스토커 명은 필수 입력 항목입니다.");
        }
        if (!StringUtils.hasText(dto.getShelfName())) {
            throw new IllegalArgumentException("셸프 명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String stockerName = dto.getStockerName().trim();
        String shelfName = dto.getShelfName().trim();

        // 1. 복합키 중복 검증
        if (wcsShelfRepository.existsById(factoryName, stockerName, shelfName)) {
            throw new IllegalArgumentException("이미 등록된 WCS 셸프입니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ", shelfName: " + shelfName + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsShelfCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs shelf created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 생성
        WcsShelfCreateCommand command = WcsShelfCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .stockerName(stockerName)
                .shelfName(shelfName)
                .abnormalStageNumber(dto.getAbnormalStageNumber())
                .bin(dto.getBin())
                .carrierName(dto.getCarrierName())
                .col(dto.getCol())
                .lastTransferCmdName(dto.getLastTransferCmdName())
                .numberOfUses(dto.getNumberOfUses())
                .row(dto.getRow())
                .shelfEnableMode(dto.getShelfEnableMode())
                .shelfStatus(dto.getShelfStatus())
                .shelfTransferStatus(dto.getShelfTransferStatus())
                .shelfType(dto.getShelfType())
                .stage(dto.getStage())
                .zoneName(dto.getZoneName())
                .build();

        WcsShelf shelf = WcsShelf.create(command);

        // 4. SHELF 테이블 저장
        WcsShelf savedShelf = wcsShelfRepository.save(shelf);

        // 5. SHELF_HISTORY 테이블 이력 적재
        WcsShelfHistoryEntity historyEntity = wcsShelfMapper.toHistoryEntity(savedShelf);
        wcsShelfHistoryJpaRepository.save(historyEntity);

        log.info("WcsShelf created successfully: [factoryName={}, stockerName={}, shelfName={}]", factoryName, stockerName, shelfName);

        return WcsShelfResponse.fromDomain(savedShelf);
    }

    /**
     * 복합키 기반 WCS 셸프 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 shelf.update(command) 호출
     * - SHELF 저장 및 SHELF_HISTORY 이력 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public WcsShelfResponse updateShelf(String factoryName, String stockerName, String shelfName, WcsShelfUpdateRequestDto dto) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(shelfName)) {
            throw new IllegalArgumentException("수정할 대상 공장 구분, 스토커 명 및 셸프 명이 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 셸프 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<WcsShelf> optionalShelf = wcsShelfRepository.findById(factoryName.trim(), stockerName.trim(), shelfName.trim());
        if (optionalShelf.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 WCS 셸프가 존재하지 않습니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ", shelfName: " + shelfName + ")");
        }

        WcsShelf shelf = optionalShelf.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "WcsShelfModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Wcs shelf modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 커맨드 생성 및 도메인 객체 수정
        WcsShelfUpdateCommand command = WcsShelfUpdateCommand.builder()
                .transactionInfo(tx)
                .abnormalStageNumber(dto.getAbnormalStageNumber())
                .bin(dto.getBin())
                .carrierName(dto.getCarrierName())
                .col(dto.getCol())
                .lastTransferCmdName(dto.getLastTransferCmdName())
                .numberOfUses(dto.getNumberOfUses())
                .row(dto.getRow())
                .shelfEnableMode(dto.getShelfEnableMode())
                .shelfStatus(dto.getShelfStatus())
                .shelfTransferStatus(dto.getShelfTransferStatus())
                .shelfType(dto.getShelfType())
                .stage(dto.getStage())
                .zoneName(dto.getZoneName())
                .build();

        shelf.update(command);

        // 4. SHELF 테이블 저장
        WcsShelf updatedShelf = wcsShelfRepository.save(shelf);

        // 5. SHELF_HISTORY 테이블 이력 적재
        WcsShelfHistoryEntity historyEntity = wcsShelfMapper.toHistoryEntity(updatedShelf);
        wcsShelfHistoryJpaRepository.save(historyEntity);

        log.info("WcsShelf updated successfully: [factoryName={}, stockerName={}, shelfName={}]", factoryName, stockerName, shelfName);

        return WcsShelfResponse.fromDomain(updatedShelf);
    }

    /**
     * 복합키 기반 WCS 셸프 삭제 (DELETE)
     * - 대상 조회
     * - SHELF_HISTORY 테이블에 삭제 이력 적재
     * - SHELF 테이블에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteShelf(String factoryName, String stockerName, String shelfName, String eventUser, String eventComment) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(shelfName)) {
            throw new IllegalArgumentException("삭제할 대상 공장 구분, 스토커 명 및 셸프 명이 누락되었습니다.");
        }

        Optional<WcsShelf> optionalShelf = wcsShelfRepository.findById(factoryName.trim(), stockerName.trim(), shelfName.trim());
        if (optionalShelf.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 WCS 셸프가 존재하지 않습니다. (factoryName: " + factoryName + ", stockerName: " + stockerName + ", shelfName: " + shelfName + ")");
        }

        WcsShelf shelf = optionalShelf.get();

        // 1. 삭제 이벤트 정보 설정
        LocalDateTime now = LocalDateTime.now();
        shelf.setLastEventName("WcsShelfDeleted");
        shelf.setLastEventTime(now);
        shelf.setLastEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        shelf.setLastEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Wcs shelf deleted");

        // 2. 삭제 이력 적재
        WcsShelfHistoryEntity historyEntity = wcsShelfMapper.toHistoryEntity(shelf);
        wcsShelfHistoryJpaRepository.save(historyEntity);

        // 3. 셸프 삭제
        wcsShelfRepository.deleteById(factoryName.trim(), stockerName.trim(), shelfName.trim());

        log.info("WcsShelf deleted successfully: [factoryName={}, stockerName={}, shelfName={}]", factoryName, stockerName, shelfName);
    }
}
