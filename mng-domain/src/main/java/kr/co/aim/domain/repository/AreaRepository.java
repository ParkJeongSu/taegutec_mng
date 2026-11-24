package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Area;
import kr.co.aim.domain.repository.condition.AreaSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AreaRepository {

    List<Area> findAll();

    Optional<Area> findById(Long id);

    Optional<Area> findByAreaName(String areaName);

    Area save(Area area);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<Area> findAreasWithConditions(AreaSearchCondition condition, Pageable pageable);
}
