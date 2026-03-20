package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentEntity, Long> {
    Optional<EquipmentEntity> findByEquipmentName(String equipmentName);
}
