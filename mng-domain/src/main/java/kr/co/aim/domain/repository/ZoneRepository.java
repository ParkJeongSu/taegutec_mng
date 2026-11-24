package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Zone;
import kr.co.aim.domain.repository.condition.ZoneSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository {

    List<Zone> findAll();

    Optional<Zone> findById(Long id);

    Optional<Zone> findByZoneName(String zoneName);

    Zone save(Zone zone);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<Zone> findZonesWithConditions(ZoneSearchCondition condition, Pageable pageable);
}
