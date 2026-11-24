package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneJpaRepository extends JpaRepository<ZoneEntity, Long> {
    Optional<ZoneEntity> findByZoneName(String zoneName);
}
