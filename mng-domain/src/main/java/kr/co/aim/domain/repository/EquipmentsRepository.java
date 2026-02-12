package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Equipments;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface EquipmentsRepository {
    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 CarrierDef 도메인 객체 리스트
     */
    List<Equipments> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrierDef ID
     * @return Optional<CarrierDef>
     */
    Optional<Equipments> findById(Long id);

    /**
     * equipmentName로 설비를 찾습니다.
     * @param equipmentName equipmentName
     * @return Optional<Equipments>
     */
    Optional<Equipments> findByEquipmentName(String equipmentName);

    /**
     * equipmentName로 설비를 찾습니다.
     * @param equipments equipments
     * @return Equipments
     */
    Equipments save(Equipments equipments);

//    Page<EquipmentsResponseDto> findEquipmentsWithConditions(EquipmentsSearchConditionDto condition, Pageable pageable);

    void deleteAllByIdInBatch(List<Long>ids);


}
