package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ProductionOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderJpaRepository extends JpaRepository<ProductionOrderEntity, Long> {
    // (1) orderId 와 orderLineNumber 로 조회
    Optional<ProductionOrderEntity> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);

    // (2) galId 로 조회
    Optional<ProductionOrderEntity> findByGalId(String galId);

    // (3) equipmentName, productionOrderState 로 조회
    List<ProductionOrderEntity> findByEquipmentNameAndProductionOrderState(String equipmentName, String productionOrderState);

    // (4) equipmentName, productionOrderType, productionOrderState 로 조회
    List<ProductionOrderEntity> findByEquipmentNameAndProductionOrderTypeAndProductionOrderState(
            String equipmentName,
            String productionOrderType,
            String productionOrderState
    );

    List<ProductionOrderEntity> findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
            String equipmentName,
            List<String> productionOrderState
    );

    List<ProductionOrderEntity> findByProductionOrderStateInOrderByCreateTimeAsc(
            List<String> productionOrderState
    );

}
