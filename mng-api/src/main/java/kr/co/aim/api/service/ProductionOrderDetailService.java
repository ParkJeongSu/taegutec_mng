package kr.co.aim.api.service;

import kr.co.aim.domain.model.ProductionOrderDetail;
import kr.co.aim.domain.repository.ProductionOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderDetailService {

    private final ProductionOrderDetailRepository productionOrderDetailRepository;

    @Transactional(value = "mssqlTransactionManager")
    public ProductionOrderDetail save(ProductionOrderDetail detail) {
        return productionOrderDetailRepository.save(detail);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<ProductionOrderDetail> findAll() {
        return productionOrderDetailRepository.findAll();
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Optional<ProductionOrderDetail> findById(Long id) {
        return productionOrderDetailRepository.findById(id);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<ProductionOrderDetail> findByProductionOrderId(Long productionOrderId) {
        return productionOrderDetailRepository.findByProductionOrderId(productionOrderId);
    }

    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<ProductionOrderDetail> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber) {
        return productionOrderDetailRepository.findByOrderIdAndOrderLineNumber(orderId, orderLineNumber);
    }

    @Transactional(value = "mssqlTransactionManager")
    public void deleteAllByIdInBatch(List<Long> ids) {
        productionOrderDetailRepository.deleteAllByIdInBatch(ids);
    }
}