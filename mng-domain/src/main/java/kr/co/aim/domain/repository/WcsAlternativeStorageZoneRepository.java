package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsAlternativeStorageZoneSearchCondition;
import kr.co.aim.domain.model.WcsAlternativeStorageZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsAlternativeStorageZoneRepository {

    List<WcsAlternativeStorageZone> findAll();

    Optional<WcsAlternativeStorageZone> findById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority);

    List<WcsAlternativeStorageZone> findByFactoryName(String factoryName);

    List<WcsAlternativeStorageZone> findByFactoryNameAndSourceZoneName(String factoryName, String sourceZoneName);

    boolean existsById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority);

    WcsAlternativeStorageZone save(WcsAlternativeStorageZone alternativeStorageZone);

    void deleteById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority);

    Page<WcsAlternativeStorageZone> findAlternativeStorageZones(WcsAlternativeStorageZoneSearchCondition condition, Pageable pageable);
}
