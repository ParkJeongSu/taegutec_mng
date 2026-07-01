package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.ProductionOrderDetail;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderDetailRepository {
    List<ProductionOrderDetail> findAll();
    Optional<ProductionOrderDetail> findById(Long id);
    List<ProductionOrderDetail> findByProductionOrderId(Long productionOrderId);
    List<ProductionOrderDetail> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);
    ProductionOrderDetail save(ProductionOrderDetail detail);
    void deleteAllByIdInBatch(List<Long> ids);
}