package kr.co.aim.domain.repository;

import kr.co.aim.common.dto.EquipmentDefResponseDto;
import kr.co.aim.common.dto.EquipmentDefSearchConditionDto;
import kr.co.aim.common.dto.EquipmentGroupResponseDto;
import kr.co.aim.common.dto.EquipmentGroupSearchCondtionDto;
import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.domain.model.Equipments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface EquipmentGroupRepository {
    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 CarrierDef 도메인 객체 리스트
     */
    List<EquipmentGroup> findAll();

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrierDef ID
     * @return Optional<CarrierDef>
     */
    Optional<EquipmentGroup> findById(Long id);

    /**
     * equipmentName로 설비를 찾습니다.
     * @param equipmentGroupName EquipmentGroup EquipmentGroupName
     * @return Optional<Equipments>
     */
    Optional<EquipmentGroup> findByEquipmentGroupName(String equipmentGroupName);

    /**
     * equipmentName로 설비를 찾습니다.
     * @param equipmentGroup EquipmentGroup
     * @return Equipments
     */
    EquipmentGroup save(EquipmentGroup equipmentGroup);

    Page<EquipmentGroupResponseDto> findEquipmentGroupWithConditions(EquipmentGroupSearchCondtionDto condition, Pageable pageable);


    void deleteAllByIdInBatch(List<Long>ids);


}
