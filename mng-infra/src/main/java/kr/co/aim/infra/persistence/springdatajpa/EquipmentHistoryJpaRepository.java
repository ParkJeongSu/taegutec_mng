package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentHistoryJpaRepository extends JpaRepository<EquipmentHistoryEntity, Long> {
}
