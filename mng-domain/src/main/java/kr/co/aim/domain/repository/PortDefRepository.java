package kr.co.aim.domain.repository;

import kr.co.aim.common.dto.PortDefSearchConditionDto;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface PortDefRepository {
    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 CarrierDef 도메인 객체 리스트
     */
    List<PortDef> findAll();

    Optional<PortDef> findById(Long id);

    Optional<PortDef> findByEquipmentNameAndPortName(String equipmentName,String portName);

    PortDef save(PortDef portDef);

    Optional<PortDef> findByLocationId(String locationId);

    Page<PortDef> findPortDefWithConditions(PortDefSearchConditionDto condition, Pageable pageable);
    void deleteAllByIdInBatch(List<Long> ids);

    Optional<PortDef> findWithLockByEquipmentNameAndPortName(String equipmentName, String portName);
}
