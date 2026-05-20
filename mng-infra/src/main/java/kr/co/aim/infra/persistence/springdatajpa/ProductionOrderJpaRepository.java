package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.ProductionOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductionOrderJpaRepository extends JpaRepository<ProductionOrderEntity, Long> {
    // (1) orderId 와 orderLineNumber 로 조회
    Optional<ProductionOrderEntity> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);

    // (2) galId 로 조회
    Optional<ProductionOrderEntity> findByGalKey(String galId);

    Optional<ProductionOrderEntity> findByH2OrderDpLineId(Long h2orderDPLineId);

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

    // 금일 특정 기간(00:00:00 ~ 23:59:59) 동안 생성된 order 조회
    List<ProductionOrderEntity> findByCreateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 금일 생성된 완료 order 조회용 (기간 + 상태 조건)
    List<ProductionOrderEntity> findByCreateTimeBetweenAndProductionOrderState(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String productionOrderState
    );

}
