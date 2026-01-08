package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TransportJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransportJobJpaRepository extends JpaRepository<TransportJobEntity, Long> {
    Optional<TransportJobEntity> findByTransportJobName(String transportJobName);
}
