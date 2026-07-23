package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.PortDefSearchCondition;
import kr.co.aim.domain.model.PortDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface PortDefRepository {

    List<PortDef> findAll();

    Optional<PortDef> findById(Long id);

    Optional<PortDef> findByEquipmentNameAndPortName(String equipmentName,String portName);

    PortDef save(PortDef portDef);

    Optional<PortDef> findByLocationId(String locationId);

    Page<PortDef> findPortDefWithConditions(PortDefSearchCondition condition, Pageable pageable);

    void deleteAllByIdInBatch(List<Long> ids);

    Optional<PortDef> findWithLockByEquipmentNameAndPortName(String equipmentName, String portName);

    List<PortDef> findByWorkCenterNameAndDetailPortTypeInAndPortTypeIn(
            String workCenterName,
            List<String> detailPortTypes,
            List<String> portTypes
    );
}
