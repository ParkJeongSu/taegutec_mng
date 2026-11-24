package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ZoneFallbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneFallbackJpaRepository extends JpaRepository<ZoneFallbackEntity, Long> {}
