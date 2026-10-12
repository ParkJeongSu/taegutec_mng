package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.InsertAutoTestResultSearchCondition;
import kr.co.aim.domain.model.InsertAutoTestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InsertAutoTestResultRepository {

    List<InsertAutoTestResult> findAll();

    Optional<InsertAutoTestResult> findById(Long id);

    List<InsertAutoTestResult> findByAutoTestId(Long autoTestId);

    InsertAutoTestResult save(InsertAutoTestResult insertAutoTestResult);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    void deleteByAutoTestId(Long autoTestId);

    Page<InsertAutoTestResult> findAutoTestResultsWithConditions(InsertAutoTestResultSearchCondition condition, Pageable pageable);
}
