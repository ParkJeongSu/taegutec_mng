package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.*;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.model.ProductionOrderHistory;
import kr.co.aim.domain.model.ProductionOrderSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface ProductionOrderRepository {

    ProductionOrder save(ProductionOrder productionOrder);

    Optional<ProductionOrder> findById(Long id);

    List<ProductionOrder> findAll();

    void deleteAllByIdInBatch(List<Long>ids);

    // (1) orderId 와 orderLineNumber 로 조회
    Optional<ProductionOrder> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);

    // (2) galId 로 조회
    Optional<ProductionOrder> findByGalKey(String galKey);

    Optional<ProductionOrder> findByH2OrderDpLineId(Long h2orderDPLineId);

    // (3) equipmentName, productionOrderState 로 조회
    List<ProductionOrder> findByEquipmentNameAndProductionOrderState(String equipmentName, String productionOrderState);

    // (4) equipmentName, productionOrderType, productionOrderState 로 조회
    List<ProductionOrder> findByEquipmentNameAndProductionOrderTypeAndProductionOrderState(
            String equipmentName,
            String productionOrderType,
            String productionOrderState
    );

    List<ProductionOrder> findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
            String equipmentName,
            List<String> productionOrderState
    );

    Page<ProductionOrderSummary> findProductionOrderSummaryByCondition(ProductionOrderSummarySearchCondition condition, Pageable pageable);

    Page<ProductionOrder> findProductionOrderByCondition(ProductionOrderSearchCondition condition, Pageable pageable);

    Page<ProductionOrderHistory> findProductionOrderHistoryByCondition(ProductionOrderHistorySearchCondition condition, Pageable pageable);

    List<ProductionOrder> findByProductionOrderStateInOrderByCreateTimeAsc(
            List<String> productionOrderState
    );

    // 금일 특정 기간(00:00:00 ~ 23:59:59) 동안 생성된 order 조회
    List<ProductionOrder> findByCreateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 금일 생성된 완료 order 조회용 (기간 + 상태 조건)
    List<ProductionOrder> findByCreateTimeBetweenAndProductionOrderState(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String productionOrderState
    );
}
