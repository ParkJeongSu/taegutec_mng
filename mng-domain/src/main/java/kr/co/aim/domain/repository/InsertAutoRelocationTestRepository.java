package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.InsertAutoRelocationTest;
import kr.co.aim.domain.repository.condition.InsertAutoRelocationTestSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InsertAutoRelocationTestRepository {

    List<InsertAutoRelocationTest> findAll();

    Optional<InsertAutoRelocationTest> findById(Long id);

    List<InsertAutoRelocationTest> findByCarrierName(String carrierName);

    InsertAutoRelocationTest save(InsertAutoRelocationTest insertAutoRelocationTest);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<InsertAutoRelocationTest> findAutoRelocationTestsWithConditions(InsertAutoRelocationTestSearchCondition condition, Pageable pageable);
}
