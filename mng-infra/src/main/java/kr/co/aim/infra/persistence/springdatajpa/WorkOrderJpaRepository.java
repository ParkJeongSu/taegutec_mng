package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.domain.model.WorkOrder;
import kr.co.aim.infra.persistence.entity.LotsEntity;
import kr.co.aim.infra.persistence.entity.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderEntity, Long> {
    Optional<WorkOrderEntity> findByWorkOrderName(String workOrderName);
    Optional<WorkOrderEntity> findByWorkOrderState(String workOrderState);
}
