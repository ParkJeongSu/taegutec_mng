package kr.co.aim.domain.repository;

import kr.co.aim.common.dto.PortsResponseDto;
import kr.co.aim.common.dto.PortsSearchConditionDto;
import kr.co.aim.domain.model.Ports;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface PortsRepository {

    /**
     * Port를 저장하거나 업데이트합니다.
     * @param ports 저장할 사용자 도메인 객체
     * @return 저장된 Port 도메인 객체 (ID 포함)
     */
    Ports save(Ports ports);

    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 CarrierDef 도메인 객체 리스트
     */
    List<Ports> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrierDef ID
     * @return Optional<CarrierDef>
     */
    Optional<Ports> findById(Long id);

    /**
     * equipmentName로 설비를 찾습니다.
     * @param equipmentName equipmentName
     * @param portName portName
     * @return Optional<Equipments>
     */
    Optional<Ports> findByEquipmentNameAndPortName(String equipmentName,String portName);

    void deleteAllByIdInBatch(List<Long>ids);

    Page<PortsResponseDto> findPortsWithConditions(PortsSearchConditionDto condition, Pageable pageable);
}
