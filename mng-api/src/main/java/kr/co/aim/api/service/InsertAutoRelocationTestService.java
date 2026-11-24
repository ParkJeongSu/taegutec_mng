package kr.co.aim.api.service;

import kr.co.aim.api.dto.InsertAutoRelocationTestCreateRequestDto;
import kr.co.aim.api.dto.InsertAutoRelocationTestResponse;
import kr.co.aim.api.dto.InsertAutoRelocationTestUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.InsertAutoRelocationTestCreateCommand;
import kr.co.aim.domain.command.InsertAutoRelocationTestUpdateCommand;
import kr.co.aim.domain.model.InsertAutoRelocationTest;
import kr.co.aim.domain.repository.InsertAutoRelocationTestRepository;
import kr.co.aim.domain.repository.condition.InsertAutoRelocationTestSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertAutoRelocationTestService {

    private final InsertAutoRelocationTestRepository insertAutoRelocationTestRepository;

    /**
     * 조건 및 페이징 기반 자동 재배치 테스트 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<InsertAutoRelocationTestResponse> findAutoRelocationTests(InsertAutoRelocationTestSearchCondition condition, Pageable pageable) {
        Page<InsertAutoRelocationTest> pageResult = insertAutoRelocationTestRepository.findAutoRelocationTestsWithConditions(condition, pageable);
        List<InsertAutoRelocationTestResponse> content = new ArrayList<>();
        for (InsertAutoRelocationTest test : pageResult.getContent()) {
            content.add(InsertAutoRelocationTestResponse.fromDomain(test));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자동 재배치 테스트 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public InsertAutoRelocationTestResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자동 재배치 테스트 ID가 유효하지 않습니다.");
        }
        Optional<InsertAutoRelocationTest> optionalTest = insertAutoRelocationTestRepository.findById(id);
        if (optionalTest.isEmpty()) {
            throw new IllegalArgumentException("해당 자동 재배치 테스트가 존재하지 않습니다. ID: " + id);
        }
        return InsertAutoRelocationTestResponse.fromDomain(optionalTest.get());
    }

    /**
     * 신규 자동 재배치 테스트 등록 (CREATE)
     */
    @Transactional(value = "mssqlTransactionManager")
    public InsertAutoRelocationTestResponse createAutoRelocationTest(InsertAutoRelocationTestCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자동 재배치 테스트 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getCarrierName() == null || dto.getCarrierName().trim().isEmpty()) {
            throw new IllegalArgumentException("캐리어 이름은 필수 입력 항목입니다.");
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "AutoRelocationTestCreated";
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
            eventComment = "Auto relocation test scenario created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String wareHouseName1 = null;
        if (dto.getWareHouseName1() != null) {
            wareHouseName1 = dto.getWareHouseName1().trim();
        }

        String wareHouseName2 = null;
        if (dto.getWareHouseName2() != null) {
            wareHouseName2 = dto.getWareHouseName2().trim();
        }

        String zoneName1 = null;
        if (dto.getZoneName1() != null) {
            zoneName1 = dto.getZoneName1().trim();
        }

        String zoneName2 = null;
        if (dto.getZoneName2() != null) {
            zoneName2 = dto.getZoneName2().trim();
        }

        String state = null;
        if (dto.getState() != null) {
            state = dto.getState().trim();
        }

        InsertAutoRelocationTestCreateCommand command = InsertAutoRelocationTestCreateCommand.builder()
                .transactionInfo(tx)
                .wareHouseName1(wareHouseName1)
                .wareHouseName2(wareHouseName2)
                .zoneName1(zoneName1)
                .zoneName2(zoneName2)
                .carrierName(dto.getCarrierName().trim())
                .state(state)
                .build();

        InsertAutoRelocationTest insertAutoRelocationTest = InsertAutoRelocationTest.create(command);
        InsertAutoRelocationTest saved = insertAutoRelocationTestRepository.save(insertAutoRelocationTest);

        log.info("InsertAutoRelocationTest created successfully: [id={}, wareHouseName1={}, wareHouseName2={}, carrierName={}]",
                saved.getId(), saved.getWareHouseName1(), saved.getWareHouseName2(), saved.getCarrierName());

        return InsertAutoRelocationTestResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자동 재배치 테스트 수정 (UPDATE)
     */
    @Transactional(value = "mssqlTransactionManager")
    public InsertAutoRelocationTestResponse updateAutoRelocationTest(Long id, InsertAutoRelocationTestUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자동 재배치 테스트 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자동 재배치 테스트 정보가 유효하지 않습니다.");
        }

        Optional<InsertAutoRelocationTest> optionalTest = insertAutoRelocationTestRepository.findById(id);
        if (optionalTest.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자동 재배치 테스트가 존재하지 않습니다. ID: " + id);
        }

        InsertAutoRelocationTest insertAutoRelocationTest = optionalTest.get();

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "AutoRelocationTestModified";
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
            eventComment = "Auto relocation test scenario modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String wareHouseName1 = null;
        if (dto.getWareHouseName1() != null) {
            wareHouseName1 = dto.getWareHouseName1().trim();
        }

        String wareHouseName2 = null;
        if (dto.getWareHouseName2() != null) {
            wareHouseName2 = dto.getWareHouseName2().trim();
        }

        InsertAutoRelocationTestUpdateCommand command = InsertAutoRelocationTestUpdateCommand.builder()
                .transactionInfo(tx)
                .wareHouseName1(wareHouseName1)
                .wareHouseName2(wareHouseName2)
                .zoneName1(dto.getZoneName1())
                .zoneName2(dto.getZoneName2())
                .carrierName(dto.getCarrierName())
                .state(dto.getState())
                .build();

        insertAutoRelocationTest.update(command);
        InsertAutoRelocationTest updated = insertAutoRelocationTestRepository.save(insertAutoRelocationTest);

        log.info("InsertAutoRelocationTest updated successfully: [id={}, carrierName={}]", updated.getId(), updated.getCarrierName());

        return InsertAutoRelocationTestResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 자동 재배치 테스트 단건 삭제 (DELETE)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAutoRelocationTest(Long id) {
        if (id == null) {
            return;
        }

        Optional<InsertAutoRelocationTest> optionalTest = insertAutoRelocationTestRepository.findById(id);
        if (optionalTest.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자동 재배치 테스트가 존재하지 않습니다. ID: " + id);
        }

        insertAutoRelocationTestRepository.deleteById(id);

        log.info("InsertAutoRelocationTest deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 자동 재배치 테스트 벌크 삭제 (DELETE batch)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAutoRelocationTests(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        insertAutoRelocationTestRepository.deleteAllByIdInBatch(ids);
        log.info("InsertAutoRelocationTests batch deleted successfully: [count={}]", ids.size());
    }
}
