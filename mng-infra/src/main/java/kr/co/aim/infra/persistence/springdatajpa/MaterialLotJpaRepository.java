package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MaterialLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialLotJpaRepository extends JpaRepository<MaterialLotEntity, Long> {
    Optional<MaterialLotEntity> findByMaterialLotId(String materialLotId);
}
