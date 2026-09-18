package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.DepartmentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentHistoryJpaRepository extends JpaRepository<DepartmentHistoryEntity, Long> {
}
