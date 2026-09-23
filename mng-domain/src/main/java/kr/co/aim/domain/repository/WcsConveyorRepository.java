package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsConveyorSearchCondition;
import kr.co.aim.domain.model.WcsConveyor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsConveyorRepository {

    List<WcsConveyor> findAll();

    Optional<WcsConveyor> findById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo);

    List<WcsConveyor> findByFactoryNameAndConveyorGroup(String factoryName, String conveyorGroup);

    boolean existsById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo);

    WcsConveyor save(WcsConveyor conveyor);

    void deleteById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo);

    Page<WcsConveyor> findConveyors(WcsConveyorSearchCondition condition, Pageable pageable);
}
