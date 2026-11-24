package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MaterialClassDefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialClassDefJpaRepository extends JpaRepository<MaterialClassDefEntity, Long> {

    Optional<MaterialClassDefEntity> findByMaterialClassDefName(String materialClassDefName);

    List<MaterialClassDefEntity> findByParentClassId(Long parentClassId);
}
