package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentGroupDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentGroupJpaRepository extends JpaRepository<EquipmentGroupDefEntity, Long> {
    Optional<EquipmentGroupDefEntity> findByEquipmentGroupName(String equipmentGroupName);
}
