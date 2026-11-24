package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ZoneMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneMappingJpaRepository extends JpaRepository<ZoneMappingEntity, Long> {}
