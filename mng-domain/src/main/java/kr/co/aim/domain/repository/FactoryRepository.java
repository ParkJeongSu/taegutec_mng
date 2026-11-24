package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Factory;
import kr.co.aim.domain.repository.condition.FactorySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FactoryRepository {

    List<Factory> findAll();

    Optional<Factory> findById(Long id);

    Optional<Factory> findByFactoryName(String factoryName);

    Factory save(Factory factory);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<Factory> findFactorysWithConditions(FactorySearchCondition condition, Pageable pageable);
}
