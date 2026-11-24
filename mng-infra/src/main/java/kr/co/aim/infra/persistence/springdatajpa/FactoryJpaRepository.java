package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.FactoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FactoryJpaRepository extends JpaRepository<FactoryEntity, Long> {
    Optional<FactoryEntity> findByFactoryName(String factoryName);
}
