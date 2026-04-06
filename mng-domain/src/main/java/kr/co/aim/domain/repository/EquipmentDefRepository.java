package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.EquipmentDef;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface EquipmentDefRepository {
    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 CarrierDef 도메인 객체 리스트
     */
    List<EquipmentDef> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id EquipmentDef ID
     * @return Optional<EquipmentDef>
     */
    Optional<EquipmentDef> findById(Long id);

    /**
     * carrierDefName로 사용자를 찾습니다.
     * @param equipmentDefName equipmentDefName
     * @return Optional<EquipmentDef>
     */
    Optional<EquipmentDef> findByEquipmentName(String equipmentDefName);

    EquipmentDef save(EquipmentDef equipmentDef);

//    Page<EquipmentDefResponseDto> findEquipmentDefWithConditions(EquipmentDefSearchConditionDto condition, Pageable pageable);

    void deleteAllByIdInBatch(List<Long>ids);
}
