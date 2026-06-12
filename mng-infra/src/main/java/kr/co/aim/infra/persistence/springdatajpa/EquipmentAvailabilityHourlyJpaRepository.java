package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentAvailabilityHourlyEntity;
import kr.co.aim.infra.persistence.entity.IdAvailabilityHourly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentAvailabilityHourlyJpaRepository extends JpaRepository<EquipmentAvailabilityHourlyEntity, IdAvailabilityHourly> {
}
