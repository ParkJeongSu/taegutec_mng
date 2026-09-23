package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsCraneSearchCondition;
import kr.co.aim.domain.model.WcsCrane;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsCraneRepository {

    List<WcsCrane> findAll();

    Optional<WcsCrane> findById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo);

    List<WcsCrane> findByFactoryNameAndStockerName(String factoryName, String stockerName);

    boolean existsById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo);

    WcsCrane save(WcsCrane crane);

    void deleteById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo);

    Page<WcsCrane> findCranes(WcsCraneSearchCondition condition, Pageable pageable);
}
