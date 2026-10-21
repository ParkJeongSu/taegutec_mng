package kr.co.aim.domain.repository;

import kr.co.aim.domain.repository.condition.InsertAutoTestSearchCondition;
import kr.co.aim.domain.model.InsertAutoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InsertAutoTestRepository {

    List<InsertAutoTest> findAll();

    Optional<InsertAutoTest> findById(Long id);

    List<InsertAutoTest> findByWorkStationId(String workStationId);

    List<InsertAutoTest> findByCarrierName(String carrierName);

    InsertAutoTest save(InsertAutoTest insertAutoTest);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<InsertAutoTest> findAutoTestsWithConditions(InsertAutoTestSearchCondition condition, Pageable pageable);
}
