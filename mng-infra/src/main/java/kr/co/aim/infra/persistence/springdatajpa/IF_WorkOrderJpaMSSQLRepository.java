package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.IF_WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IF_WorkOrderJpaMSSQLRepository extends JpaRepository<IF_WorkOrderEntity, Long> {
}
