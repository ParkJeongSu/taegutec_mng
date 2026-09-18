package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentJpaRepository extends JpaRepository<DepartmentEntity, Long> {

    Optional<DepartmentEntity> findByFactoryNameAndDepartmentName(String factoryName, String departmentName);

    List<DepartmentEntity> findByFactoryName(String factoryName);
}
