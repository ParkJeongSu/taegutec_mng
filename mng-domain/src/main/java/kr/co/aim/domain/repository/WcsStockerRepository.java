package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsStockerSearchCondition;
import kr.co.aim.domain.model.WcsStocker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsStockerRepository {

    List<WcsStocker> findAll();

    Optional<WcsStocker> findById(String factoryName, String stockerName);

    List<WcsStocker> findByFactoryName(String factoryName);

    boolean existsById(String factoryName, String stockerName);

    WcsStocker save(WcsStocker stocker);

    void deleteById(String factoryName, String stockerName);

    Page<WcsStocker> findStockers(WcsStockerSearchCondition condition, Pageable pageable);
}
