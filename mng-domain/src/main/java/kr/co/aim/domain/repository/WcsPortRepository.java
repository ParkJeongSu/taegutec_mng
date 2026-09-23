package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsPortSearchCondition;
import kr.co.aim.domain.model.WcsPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsPortRepository {

    List<WcsPort> findAll();

    Optional<WcsPort> findById(String factoryName, String equipmentName, Integer localNo, Integer portNumber);

    List<WcsPort> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName);

    boolean existsById(String factoryName, String equipmentName, Integer localNo, Integer portNumber);

    WcsPort save(WcsPort port);

    void deleteById(String factoryName, String equipmentName, Integer localNo, Integer portNumber);

    Page<WcsPort> findPorts(WcsPortSearchCondition condition, Pageable pageable);
}
