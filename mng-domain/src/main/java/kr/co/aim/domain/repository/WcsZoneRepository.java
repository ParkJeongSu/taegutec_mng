package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsZoneSearchCondition;
import kr.co.aim.domain.model.WcsZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsZoneRepository {

    List<WcsZone> findAll();

    Optional<WcsZone> findById(String factoryName, String zoneName);

    List<WcsZone> findByFactoryName(String factoryName);

    boolean existsById(String factoryName, String zoneName);

    WcsZone save(WcsZone zone);

    void deleteById(String factoryName, String zoneName);

    Page<WcsZone> findZones(WcsZoneSearchCondition condition, Pageable pageable);
}
