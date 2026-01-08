package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.EquipmentsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentsHistoryJpaRepository extends JpaRepository<EquipmentsHistoryEntity, Long> {
}
