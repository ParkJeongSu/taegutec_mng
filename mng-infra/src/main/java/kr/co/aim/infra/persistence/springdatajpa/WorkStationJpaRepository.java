package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WorkStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkStationJpaRepository extends JpaRepository<WorkStationEntity, Long> {
    Optional<WorkStationEntity> findByWorkStationName(String workStationName);
}
