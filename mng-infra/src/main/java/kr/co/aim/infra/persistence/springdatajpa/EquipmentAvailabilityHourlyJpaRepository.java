package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentAvailabilityHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentAvailabilityHourlyJpaRepository extends JpaRepository<EquipmentAvailabilityHourlyEntity, Long> {
}
