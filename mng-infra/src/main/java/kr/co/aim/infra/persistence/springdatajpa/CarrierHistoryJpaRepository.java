package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.CarrierHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarrierHistoryJpaRepository extends JpaRepository<CarrierHistoryEntity, Long> {
}
