package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.IdTransportRouteDaily;
import kr.co.aim.infra.persistence.entity.IdWorkOrderProcessedDaily;
import kr.co.aim.infra.persistence.entity.TransportRouteDailyEntity;
import kr.co.aim.infra.persistence.entity.WorkOrderProcessedDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderProcessedDailyJpaRepository extends JpaRepository<WorkOrderProcessedDailyEntity, IdWorkOrderProcessedDaily> {
}
