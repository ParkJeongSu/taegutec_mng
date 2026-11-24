package kr.co.aim.api.service;

import kr.co.aim.api.dto.FactoryCreateRequestDto;
import kr.co.aim.api.dto.FactoryResponse;
import kr.co.aim.api.dto.FactoryUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.FactoryCreateCommand;
import kr.co.aim.domain.command.FactoryUpdateCommand;
import kr.co.aim.domain.model.Factory;
import kr.co.aim.domain.repository.FactoryRepository;
import kr.co.aim.domain.repository.condition.FactorySearchCondition;
import kr.co.aim.infra.persistence.entity.FactoryHistoryEntity;
import kr.co.aim.infra.persistence.mapper.FactoryMapper;
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
public class FactoryService {

    private final FactoryRepository factoryRepository;
    private final HistoryService historyService;
    private final FactoryMapper factoryMapper;

    /**
     * 조건 및 페이징 기반 공장(FACTORY) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<FactoryResponse> findFactorys(FactorySearchCondition condition, Pageable pageable) {
        Page<Factory> pageResult = factoryRepository.findFactorysWithConditions(condition, pageable);
        List<FactoryResponse> content = new ArrayList<>();
        for (Factory item : pageResult.getContent()) {
            content.add(FactoryResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 공장(FACTORY) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public FactoryResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 공장(FACTORY) ID가 유효하지 않습니다.");
        }
        Optional<Factory> optional = factoryRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 공장(FACTORY)이(가) 존재하지 않습니다. ID: " + id);
        }
        return FactoryResponse.fromDomain(optional.get());
    }

    /**
     * 신규 공장(FACTORY) 등록 (CREATE) 및 FACTORY_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public FactoryResponse createFactory(FactoryCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("공장(FACTORY) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장명은(는) 필수 입력 항목입니다.");
        }

        String factoryNameValue = dto.getFactoryName().trim();
        Optional<Factory> existing = factoryRepository.findByFactoryName(factoryNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 공장(FACTORY)입니다: " + factoryNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "FactoryCreated";
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
            eventComment = "Factory created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String factoryName = null;
        if (dto.getFactoryName() != null) {
            factoryName = dto.getFactoryName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        FactoryCreateCommand command = FactoryCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .description(description)
                .build();

        Factory factory = Factory.create(command);
        Factory saved = factoryRepository.save(factory);

        FactoryHistoryEntity historyEntity = factoryMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("Factory created successfully: [id={}]", saved.getId());

        return FactoryResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 공장(FACTORY) 수정 (UPDATE) 및 FACTORY_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public FactoryResponse updateFactory(Long id, FactoryUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 공장(FACTORY) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 공장(FACTORY) 정보가 유효하지 않습니다.");
        }

        Optional<Factory> optional = factoryRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 공장(FACTORY)이(가) 존재하지 않습니다. ID: " + id);
        }

        Factory factory = optional.get();

        if (dto.getFactoryName() != null && !dto.getFactoryName().trim().isEmpty()) {
            String targetName = dto.getFactoryName().trim();
            if (!targetName.equals(factory.getFactoryName())) {
                Optional<Factory> duplicate = factoryRepository.findByFactoryName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 공장(FACTORY)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "FactoryModified";
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
            eventComment = "Factory modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String factoryName = null;
        if (dto.getFactoryName() != null) {
            factoryName = dto.getFactoryName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        FactoryUpdateCommand command = FactoryUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .description(description)
                .build();

        factory.update(command);
        Factory updated = factoryRepository.save(factory);

        FactoryHistoryEntity historyEntity = factoryMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("Factory updated successfully: [id={}]", updated.getId());

        return FactoryResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 공장(FACTORY) 단건 삭제 (DELETE) 및 FACTORY_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteFactory(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Factory> optional = factoryRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 공장(FACTORY)이(가) 존재하지 않습니다. ID: " + id);
        }

        Factory factory = optional.get();

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
            comment = "Factory deleted";
        }

        factory.setEventName("FactoryDeleted");
        factory.setEventTime(now);
        factory.setEventUser(user);
        factory.setEventComment(comment);

        FactoryHistoryEntity historyEntity = factoryMapper.toHistoryEntity(factory);
        historyService.saveHistory(historyEntity);

        factoryRepository.deleteById(id);

        log.info("Factory deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 공장(FACTORY) 벌크 삭제 (DELETE batch) 및 FACTORY_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteFactorys(List<Long> ids, String eventUser, String eventComment) {
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
            comment = "Batch Factorys deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Factory> optional = factoryRepository.findById(id);
            if (optional.isPresent()) {
                Factory factory = optional.get();
                factory.setEventName("FactoryDeleted");
                factory.setEventTime(now);
                factory.setEventUser(user);
                factory.setEventComment(comment);

                FactoryHistoryEntity historyEntity = factoryMapper.toHistoryEntity(factory);
                historyService.saveHistory(historyEntity);
            }
        }

        factoryRepository.deleteAllByIdInBatch(ids);
        log.info("Factorys batch deleted successfully: [count={}]", ids.size());
    }
}
