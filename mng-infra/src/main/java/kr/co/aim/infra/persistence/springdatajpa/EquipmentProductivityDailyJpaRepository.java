package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentProductivityDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentProductivityDailyJpaRepository extends JpaRepository<EquipmentProductivityDailyEntity, Long> {
}
