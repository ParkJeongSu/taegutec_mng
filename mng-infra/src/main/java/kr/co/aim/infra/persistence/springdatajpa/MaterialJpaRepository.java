package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialJpaRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByMaterialId(String materialId);
}
