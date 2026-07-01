package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ProductionOrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionOrderDetailJpaRepository extends JpaRepository<ProductionOrderDetailEntity, Long> {
    List<ProductionOrderDetailEntity> findByProductionOrderIdOrderBySeqAsc(Long productionOrderId);
    List<ProductionOrderDetailEntity> findByOrderIdAndOrderLineNumberOrderBySeqAsc(String orderId, String orderLineNumber);
}