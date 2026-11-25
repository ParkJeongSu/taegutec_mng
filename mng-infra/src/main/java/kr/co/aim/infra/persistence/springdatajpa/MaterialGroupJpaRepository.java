package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MaterialGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialGroupJpaRepository extends JpaRepository<MaterialGroupEntity, Long> {
    Optional<MaterialGroupEntity> findByMaterialGroupName(String materialGroupDefName);
}
