package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaJpaRepository extends JpaRepository<AreaEntity, Long> {
    Optional<AreaEntity> findByAreaName(String areaName);
}
