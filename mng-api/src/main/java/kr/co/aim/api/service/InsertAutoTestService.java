package kr.co.aim.api.service;

import kr.co.aim.api.dto.InsertAutoTestCreateRequestDto;
import kr.co.aim.api.dto.InsertAutoTestResponse;
import kr.co.aim.api.dto.InsertAutoTestResultCreateRequestDto;
import kr.co.aim.api.dto.InsertAutoTestResultResponse;
import kr.co.aim.api.dto.InsertAutoTestUpdateRequestDto;
import kr.co.aim.common.condition.InsertAutoTestResultSearchCondition;
import kr.co.aim.common.condition.InsertAutoTestSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.InsertAutoTestCreateCommand;
import kr.co.aim.domain.command.InsertAutoTestResultCreateCommand;
import kr.co.aim.domain.command.InsertAutoTestUpdateCommand;
import kr.co.aim.domain.model.InsertAutoTest;
import kr.co.aim.domain.model.InsertAutoTestResult;
import kr.co.aim.domain.repository.InsertAutoTestRepository;
import kr.co.aim.domain.repository.InsertAutoTestResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class InsertAutoTestService {

    private final InsertAutoTestRepository insertAutoTestRepository;
    private final InsertAutoTestResultRepository insertAutoTestResultRepository;

    /**
     * 조건 및 페이징 기반 자동 테스트 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<InsertAutoTestResponse> findAutoTests(InsertAutoTestSearchCondition condition, Pageable pageable) {
        Page<InsertAutoTest> pageResult = insertAutoTestRepository.findAutoTestsWithConditions(condition, pageable);
        List<InsertAutoTestResponse> content = new ArrayList<>();
        for (InsertAutoTest test : pageResult.getContent()) {
            content.add(InsertAutoTestResponse.fromDomain(test));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 자동 테스트 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public InsertAutoTestResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 자동 테스트 ID가 유효하지 않습니다.");
        }
        Optional<InsertAutoTest> optionalTest = insertAutoTestRepository.findById(id);
        if (optionalTest.isEmpty()) {
            throw new IllegalArgumentException("해당 자동 테스트가 존재하지 않습니다. ID: " + id);
        }
        return InsertAutoTestResponse.fromDomain(optionalTest.get());
    }

    /**
     * 신규 자동 테스트 등록 (CREATE)
     */
    @Transactional(value = "mssqlTransactionManager")
    public InsertAutoTestResponse createAutoTest(InsertAutoTestCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("자동 테스트 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getWorkStationId() == null || dto.getWorkStationId().trim().isEmpty()) {
            throw new IllegalArgumentException("작업 스테이션 ID는 필수 입력 항목입니다.");
        }
        if (dto.getCarrierName() == null || dto.getCarrierName().trim().isEmpty()) {
            throw new IllegalArgumentException("캐리어 이름은 필수 입력 항목입니다.");
        }

        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "AutoTestCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Auto test scenario created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        InsertAutoTestCreateCommand command = InsertAutoTestCreateCommand.builder()
                .transactionInfo(tx)
                .wareHouseName(dto.getWareHouseName().trim())
                .workStationId(dto.getWorkStationId().trim())
                .carrierName(dto.getCarrierName().trim())
                .zoneName(dto.getZoneName() != null ? dto.getZoneName().trim() : null)
                .inboundPortName(dto.getInboundPortName().trim())
                .travelProfile(dto.getTravelProfile() != null ? dto.getTravelProfile().trim() : null)
                .build();

        InsertAutoTest insertAutoTest = InsertAutoTest.create(command);
        InsertAutoTest saved = insertAutoTestRepository.save(insertAutoTest);

        log.info("InsertAutoTest created successfully: [id={}, workStationId={}, carrierName={}]", saved.getId(), saved.getWorkStationId(), saved.getCarrierName());

        return InsertAutoTestResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 자동 테스트 수정 (UPDATE)
     */
    @Transactional(value = "mssqlTransactionManager")
    public InsertAutoTestResponse updateAutoTest(Long id, InsertAutoTestUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 자동 테스트 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 자동 테스트 정보가 유효하지 않습니다.");
        }

        Optional<InsertAutoTest> optionalTest = insertAutoTestRepository.findById(id);
        if (optionalTest.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 자동 테스트가 존재하지 않습니다. ID: " + id);
        }

        InsertAutoTest insertAutoTest = optionalTest.get();

        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "AutoTestModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Auto test scenario modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        InsertAutoTestUpdateCommand command = InsertAutoTestUpdateCommand.builder()
                .transactionInfo(tx)
                .wareHouseName(dto.getWareHouseName().trim())
                .workStationId(dto.getWorkStationId())
                .carrierName(dto.getCarrierName())
                .zoneName(dto.getZoneName())
                .inboundPortName(dto.getInboundPortName())
                .travelProfile(dto.getTravelProfile())
                .build();

        insertAutoTest.update(command);
        InsertAutoTest updated = insertAutoTestRepository.save(insertAutoTest);

        log.info("InsertAutoTest updated successfully: [id={}, workStationId={}]", updated.getId(), updated.getWorkStationId());

        return InsertAutoTestResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 자동 테스트 단건 삭제 (DELETE) 및 연관 결과 이력 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAutoTest(Long id) {
        if (id == null) {
            return;
        }

        Optional<InsertAutoTest> optionalTest = insertAutoTestRepository.findById(id);
        if (optionalTest.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 자동 테스트가 존재하지 않습니다. ID: " + id);
        }

        // 연관된 테스트 결과 이력 일괄 삭제
        insertAutoTestResultRepository.deleteByAutoTestId(id);
        // 테스트 마스터 삭제
        insertAutoTestRepository.deleteById(id);

        log.info("InsertAutoTest deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 자동 테스트 벌크 삭제 (DELETE batch)
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteAutoTests(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            if (id != null) {
                insertAutoTestResultRepository.deleteByAutoTestId(id);
            }
        }

        insertAutoTestRepository.deleteAllByIdInBatch(ids);
        log.info("InsertAutoTests batch deleted successfully: [count={}]", ids.size());
    }

    /**
     * 자동 테스트 결과 이력 등록
     */
    @Transactional(value = "mssqlTransactionManager")
    public InsertAutoTestResultResponse addTestResult(Long autoTestId, InsertAutoTestResultCreateRequestDto dto) {
        if (autoTestId == null) {
            throw new IllegalArgumentException("자동 테스트 ID가 유효하지 않습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("테스트 결과 정보가 유효하지 않습니다.");
        }

        Optional<InsertAutoTest> masterOptional = insertAutoTestRepository.findById(autoTestId);
        if (masterOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 자동 테스트 ID입니다: " + autoTestId);
        }

        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "TestResultRecorded";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Test result recorded";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        InsertAutoTestResultCreateCommand command = InsertAutoTestResultCreateCommand.builder()
                .transactionInfo(tx)
                .autoTestId(autoTestId)
                .state(dto.getState())
                .result(dto.getResult())
                .build();

        InsertAutoTestResult testResult = InsertAutoTestResult.create(command);
        InsertAutoTestResult saved = insertAutoTestResultRepository.save(testResult);

        log.info("InsertAutoTestResult created successfully: [id={}, autoTestId={}, state={}, result={}]", saved.getId(), saved.getAutoTestId(), saved.getState(), saved.getResult());

        return InsertAutoTestResultResponse.fromDomain(saved);
    }

    /**
     * 특정 자동 테스트의 결과 이력 목록 조회 (조건 및 페이징)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<InsertAutoTestResultResponse> findTestResults(InsertAutoTestResultSearchCondition condition, Pageable pageable) {
        Page<InsertAutoTestResult> pageResult = insertAutoTestResultRepository.findAutoTestResultsWithConditions(condition, pageable);
        List<InsertAutoTestResultResponse> content = new ArrayList<>();
        for (InsertAutoTestResult result : pageResult.getContent()) {
            content.add(InsertAutoTestResultResponse.fromDomain(result));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 특정 자동 테스트 ID에 대한 결과 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<InsertAutoTestResultResponse> findTestResultsByAutoTestId(Long autoTestId) {
        if (autoTestId == null) {
            return new ArrayList<>();
        }
        List<InsertAutoTestResult> list = insertAutoTestResultRepository.findByAutoTestId(autoTestId);
        List<InsertAutoTestResultResponse> resultList = new ArrayList<>();
        for (InsertAutoTestResult result : list) {
            resultList.add(InsertAutoTestResultResponse.fromDomain(result));
        }
        return resultList;
    }
}
