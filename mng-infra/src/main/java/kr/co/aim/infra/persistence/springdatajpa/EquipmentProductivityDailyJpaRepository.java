package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentAvailabilityHourlyEntity;
import kr.co.aim.infra.persistence.entity.EquipmentProductivityDailyEntity;
import kr.co.aim.infra.persistence.entity.IdAvailabilityHourly;
import kr.co.aim.infra.persistence.entity.IdProductivityDaily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentProductivityDailyJpaRepository extends JpaRepository<EquipmentProductivityDailyEntity, IdProductivityDaily> {
}
