package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentGroupJpaRepository extends JpaRepository<EquipmentGroupEntity, Long> {
    Optional<EquipmentGroupEntity> findByEquipmentGroupName(String equipmentGroupName);
}
