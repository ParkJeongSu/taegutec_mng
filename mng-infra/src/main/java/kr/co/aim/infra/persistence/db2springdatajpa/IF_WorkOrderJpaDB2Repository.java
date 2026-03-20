package kr.co.aim.infra.persistence.db2springdatajpa;

import kr.co.aim.infra.persistence.db2entity.IF_WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IF_WorkOrderJpaDB2Repository extends JpaRepository<IF_WorkOrderEntity, Long> {
    List<IF_WorkOrderEntity> findByifState(String ifState);
}
