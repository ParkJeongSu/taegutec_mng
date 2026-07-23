package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.common.condition.TransportOrderSearchCondition;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QTransportOrderEntity.transportOrderEntity;

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
    @Override
    public Page<TransportOrder> findTransportOrderWithConditions(TransportOrderSearchCondition condition, Pageable pageable) {
        JPAQuery<TransportOrderEntity> query = queryFactory
                .selectFrom(transportOrderEntity)
                .where(
                        transportOrderIdContains(condition.getTransportOrderId()),
                        idocIdEq(condition.getIdocId()),
                        descriptionContains(condition.getDescription()),
                        carrierNameContains(condition.getCarrierName()),
                        virtualCarrierNameContains(condition.getVirtualCarrierName()),
                        transportTypeContains(condition.getTransportType()),
                        transportStatusContains(condition.getTransportStatus()),
                        lastTransactionCodeContains(condition.getLastTransactionCode()),
                        carrierTypeContains(condition.getCarrierType()),
                        priorityEq(condition.getPriority()),
                        galIdContains(condition.getGalId()),
                        galWarehouseContains(condition.getGalWarehouse()),
                        locationIdContains(condition.getLocationId()),
                        workStationIdContains(condition.getWorkStationId()),
                        sourceZoneNameContains(condition.getSourceZoneName()),
                        destinationZoneNameContains(condition.getDestinationZoneName()),
                        errorTextContains(condition.getErrorText()),
                        actualWeightContains(condition.getActualWeight()),
                        requestedZoneNameContains(condition.getRequestedZoneName()),
                        actualZoneNameContains(condition.getActualZoneName()),
                        actualLocationIdContains(condition.getActualLocationId()),
                        travelProfileContains(condition.getTravelProfile()),
                        createTimeEq(condition.getCreateTime()),
                        releaseTimeEq(condition.getReleaseTime()),
                        completeTimeEq(condition.getCompleteTime()),
                        retrievalTimeEq(condition.getRetrievalTime()),
                        createUserContains(condition.getCreateUser()),
                        releaseUserContains(condition.getReleaseUser()),
                        completeUserContains(condition.getCompleteUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<TransportOrderEntity> content = query.fetch();
        List<TransportOrder> converted = content.stream().map(transportOrderMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(transportOrderEntity.count())
                    .from(transportOrderEntity)
                    .where(
                            transportOrderIdContains(condition.getTransportOrderId()),
                            idocIdEq(condition.getIdocId()),
                            descriptionContains(condition.getDescription()),
                            carrierNameContains(condition.getCarrierName()),
                            virtualCarrierNameContains(condition.getVirtualCarrierName()),
                            transportTypeContains(condition.getTransportType()),
                            transportStatusContains(condition.getTransportStatus()),
                            lastTransactionCodeContains(condition.getLastTransactionCode()),
                            carrierTypeContains(condition.getCarrierType()),
                            priorityEq(condition.getPriority()),
                            galIdContains(condition.getGalId()),
                            galWarehouseContains(condition.getGalWarehouse()),
                            locationIdContains(condition.getLocationId()),
                            workStationIdContains(condition.getWorkStationId()),
                            sourceZoneNameContains(condition.getSourceZoneName()),
                            destinationZoneNameContains(condition.getDestinationZoneName()),
                            errorTextContains(condition.getErrorText()),
                            actualWeightContains(condition.getActualWeight()),
                            requestedZoneNameContains(condition.getRequestedZoneName()),
                            actualZoneNameContains(condition.getActualZoneName()),
                            actualLocationIdContains(condition.getActualLocationId()),
                            travelProfileContains(condition.getTravelProfile()),
                            createTimeEq(condition.getCreateTime()),
                            releaseTimeEq(condition.getReleaseTime()),
                            completeTimeEq(condition.getCompleteTime()),
                            retrievalTimeEq(condition.getRetrievalTime()),
                            createUserContains(condition.getCreateUser()),
                            releaseUserContains(condition.getReleaseUser()),
                            completeUserContains(condition.getCompleteUser())
                    )
                    .fetchOne();

            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    PathBuilder pathBuilder = new PathBuilder<>(transportOrderEntity.getType(), transportOrderEntity.getMetadata());
                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, transportOrderEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression transportOrderIdContains(String transportOrderId) {
        return StringUtils.hasText(transportOrderId) ? transportOrderEntity.transportOrderId.contains(transportOrderId) : null;
    }

    private BooleanExpression idocIdEq(Long idocId) {
        return idocId != null ? transportOrderEntity.idocId.eq(idocId) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? transportOrderEntity.description.contains(description) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? transportOrderEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression virtualCarrierNameContains(String virtualCarrierName) {
        return StringUtils.hasText(virtualCarrierName) ? transportOrderEntity.virtualCarrierName.contains(virtualCarrierName) : null;
    }

    private BooleanExpression transportTypeContains(String transportType) {
        return StringUtils.hasText(transportType) ? transportOrderEntity.transportType.contains(transportType) : null;
    }

    private BooleanExpression transportStatusContains(String transportStatus) {
        return StringUtils.hasText(transportStatus) ? transportOrderEntity.transportStatus.contains(transportStatus) : null;
    }

    private BooleanExpression lastTransactionCodeContains(String lastTransactionCode) {
        return StringUtils.hasText(lastTransactionCode) ? transportOrderEntity.lastTransactionCode.contains(lastTransactionCode) : null;
    }

    private BooleanExpression carrierTypeContains(String carrierType) {
        return StringUtils.hasText(carrierType) ? transportOrderEntity.carrierType.contains(carrierType) : null;
    }

    private BooleanExpression priorityEq(Integer priority) {
        return priority != null ? transportOrderEntity.priority.eq(priority) : null;
    }

    private BooleanExpression galIdContains(String galId) {
        return StringUtils.hasText(galId) ? transportOrderEntity.galId.contains(galId) : null;
    }

    private BooleanExpression galWarehouseContains(String galWarehouse) {
        return StringUtils.hasText(galWarehouse) ? transportOrderEntity.galWarehouse.contains(galWarehouse) : null;
    }

    private BooleanExpression locationIdContains(String locationId) {
        return StringUtils.hasText(locationId) ? transportOrderEntity.locationId.contains(locationId) : null;
    }

    private BooleanExpression workStationIdContains(String workStationId) {
        return StringUtils.hasText(workStationId) ? transportOrderEntity.workStationId.contains(workStationId) : null;
    }

    private BooleanExpression sourceZoneNameContains(String sourceZoneName) {
        return StringUtils.hasText(sourceZoneName) ? transportOrderEntity.sourceZoneName.contains(sourceZoneName) : null;
    }

    private BooleanExpression destinationZoneNameContains(String destinationZoneName) {
        return StringUtils.hasText(destinationZoneName) ? transportOrderEntity.destinationZoneName.contains(destinationZoneName) : null;
    }

    private BooleanExpression errorTextContains(String errorText) {
        return StringUtils.hasText(errorText) ? transportOrderEntity.errorText.contains(errorText) : null;
    }

    private BooleanExpression actualWeightContains(String actualWeight) {
        return StringUtils.hasText(actualWeight) ? transportOrderEntity.actualWeight.contains(actualWeight) : null;
    }

    private BooleanExpression requestedZoneNameContains(String requestedZoneName) {
        return StringUtils.hasText(requestedZoneName) ? transportOrderEntity.requestedZoneName.contains(requestedZoneName) : null;
    }

    private BooleanExpression actualZoneNameContains(String actualZoneName) {
        return StringUtils.hasText(actualZoneName) ? transportOrderEntity.actualZoneName.contains(actualZoneName) : null;
    }

    private BooleanExpression actualLocationIdContains(String actualLocationId) {
        return StringUtils.hasText(actualLocationId) ? transportOrderEntity.actualLocationId.contains(actualLocationId) : null;
    }

    private BooleanExpression travelProfileContains(String travelProfile) {
        return StringUtils.hasText(travelProfile) ? transportOrderEntity.travelProfile.contains(travelProfile) : null;
    }

    private BooleanExpression createTimeEq(LocalDateTime createTime) {
        return createTime != null ? transportOrderEntity.createTime.eq(createTime) : null;
    }

    private BooleanExpression releaseTimeEq(LocalDateTime releaseTime) {
        return releaseTime != null ? transportOrderEntity.releaseTime.eq(releaseTime) : null;
    }

    private BooleanExpression completeTimeEq(LocalDateTime completeTime) {
        return completeTime != null ? transportOrderEntity.completeTime.eq(completeTime) : null;
    }

    private BooleanExpression retrievalTimeEq(LocalDateTime retrievalTime) {
        return retrievalTime != null ? transportOrderEntity.retrievalTime.eq(retrievalTime) : null;
    }

    private BooleanExpression createUserContains(String createUser) {
        return StringUtils.hasText(createUser) ? transportOrderEntity.createUser.contains(createUser) : null;
    }

    private BooleanExpression releaseUserContains(String releaseUser) {
        return StringUtils.hasText(releaseUser) ? transportOrderEntity.releaseUser.contains(releaseUser) : null;
    }

    private BooleanExpression completeUserContains(String completeUser) {
        return StringUtils.hasText(completeUser) ? transportOrderEntity.completeUser.contains(completeUser) : null;
    }

}
