package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.infra.persistence.entity.TransportJobEntity;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class TransportOrderRepositoryImpl implements TransportOrderRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final TransportOrderJpaRepository transportOrderJpaRepository;
    private final TransportOrderMapper transportOrderMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public TransportOrder save(TransportOrder transportOrder) {
        // 1. Domain -> Entity 변환
        TransportOrderEntity entity = transportOrderMapper.toEntity(transportOrder);
        // 2. JPA 리포지토리를 통해 DB에 저장
        TransportOrderEntity savedEntity = transportOrderJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return transportOrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TransportOrder> findById(Long id) {
        // 1. JPA 리포지토리를 통해 ID로 Entity 조회
        Optional<TransportOrderEntity> entityOptional = transportOrderJpaRepository.findById(id);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(transportOrderMapper::toDomain);
    }

    @Override
    public List<TransportOrder> findByTransportTypeInAndTransportStatus(List<String> types, String status) {
        return transportOrderJpaRepository.findByTransportTypeInAndTransportStatus(types,status).stream().map(transportOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<TransportOrder> findWithLockById(Long id) {
        return transportOrderJpaRepository.findWithLockById(id).map(transportOrderMapper::toDomain);
    }

    @Override
    public Optional<TransportOrder> findByTransportOrderId(String transportOrderId) {
        return transportOrderJpaRepository.findByTransportOrderId(transportOrderId).map(transportOrderMapper::toDomain);
    }

    @Override
    public List<TransportOrder> findTransportOrderByCondition(String carrierName, String transportType, List<String> transportStatus) {
        return transportOrderJpaRepository.findTransportOrderByCondition(
                carrierName,
                transportType,
                transportStatus
        ).stream().map(transportOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TransportOrder> findOutboundOrderForTransportRequest(String transportType, String transportStatus, String workStationId) {
        return transportOrderJpaRepository.findOutboundOrderForTransportRequest(
                transportType,
                transportStatus,
                workStationId
        ).stream().map(transportOrderMapper::toDomain).collect(Collectors.toList());
    }
}
