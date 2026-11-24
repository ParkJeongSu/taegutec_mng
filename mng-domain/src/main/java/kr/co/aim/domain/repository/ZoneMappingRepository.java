package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.ZoneMapping;
import kr.co.aim.domain.repository.condition.ZoneMappingSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ZoneMappingRepository {

    List<ZoneMapping> findAll();

    Optional<ZoneMapping> findById(Long id);

    ZoneMapping save(ZoneMapping zoneMapping);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<ZoneMapping> findZoneMappingsWithConditions(ZoneMappingSearchCondition condition, Pageable pageable);
}
