package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.TransportJobDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransportJobDetailJpaRepository extends JpaRepository<TransportJobDetailEntity, Long> {
    List<TransportJobDetailEntity> findByTransportJobId(Long transportJobId);
    Optional<TransportJobDetailEntity> findByTransportJobDetailName(String transportJobDetailName);
}
