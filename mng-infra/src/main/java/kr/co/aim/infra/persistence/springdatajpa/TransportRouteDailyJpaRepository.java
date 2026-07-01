package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TransportRouteDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportRouteDailyJpaRepository extends JpaRepository<TransportRouteDailyEntity, Long> {
}
