package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.ProductionOrderDetail;
import kr.co.aim.domain.repository.ProductionOrderDetailRepository;
import kr.co.aim.infra.persistence.entity.ProductionOrderDetailEntity;
import kr.co.aim.infra.persistence.mapper.ProductionOrderDetailMapper;
import kr.co.aim.infra.persistence.springdatajpa.ProductionOrderDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductionOrderDetailImpl implements ProductionOrderDetailRepository {

    private final ProductionOrderDetailJpaRepository jpaRepository;
    private final ProductionOrderDetailMapper mapper;

    @Override
    public List<ProductionOrderDetail> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductionOrderDetail> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductionOrderDetail> findByProductionOrderId(Long productionOrderId) {
        return jpaRepository.findByProductionOrderIdOrderBySeqAsc(productionOrderId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrderDetail> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber) {
        return jpaRepository.findByOrderIdAndOrderLineNumberOrderBySeqAsc(orderId, orderLineNumber).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ProductionOrderDetail save(ProductionOrderDetail detail) {
        ProductionOrderDetailEntity entity = mapper.toEntity(detail);
        ProductionOrderDetailEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        jpaRepository.deleteAllByIdInBatch(ids);
    }
}