package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.WorkStation;
import kr.co.aim.domain.repository.condition.WorkStationSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WorkStationRepository {

    List<WorkStation> findAll();

    Optional<WorkStation> findById(Long id);

    Optional<WorkStation> findByWorkStationName(String workStationName);

    WorkStation save(WorkStation workStation);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<WorkStation> findWorkStationsWithConditions(WorkStationSearchCondition condition, Pageable pageable);
}
