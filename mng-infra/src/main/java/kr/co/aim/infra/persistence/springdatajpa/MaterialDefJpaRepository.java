package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MaterialDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialDefJpaRepository extends JpaRepository<MaterialDefEntity, Long> {
    Optional<MaterialDefEntity> findByMaterialDefName(String materialDefName);
}
