package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentDefJpaRepository extends JpaRepository<EquipmentDefEntity, Long> {

    Optional<EquipmentDefEntity> findByEquipmentDefName(String equipmentDefName);
}
