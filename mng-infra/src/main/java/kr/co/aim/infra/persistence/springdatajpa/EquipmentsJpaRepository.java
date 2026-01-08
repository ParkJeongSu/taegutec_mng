package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentsJpaRepository extends JpaRepository<EquipmentsEntity, Long> {
    Optional<EquipmentsEntity> findByEquipmentName(String equipmentName);
}
