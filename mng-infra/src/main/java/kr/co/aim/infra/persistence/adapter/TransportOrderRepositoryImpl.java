package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.common.condition.TransportOrderSearchCondition;
import kr.co.aim.common.dto.insert.QTransportOrderStatisticsResponse;
import kr.co.aim.common.dto.insert.QWorkStationTransportCountResponse;
import kr.co.aim.common.dto.insert.TransportOrderStatisticsResponse;
import kr.co.aim.common.dto.insert.WorkStationTransportCountResponse;
import kr.co.aim.common.enums.TransportOrderStatus;
import kr.co.aim.common.enums.TransportOrderType;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
    public List<TransportOrder> findOutboundOrderForTransportRequest(String transportType, List<String> transportStatus, String workStationId) {
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

    @Override
    public Long findMaxOrderId() {
        return transportOrderJpaRepository.findMaxOrderId();
    }

    @Override
    public TransportOrderStatisticsResponse getWorkStationStatistics(String workStationId, LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<String> inProgressStatuses = Arrays.asList(
                TransportOrderStatus.CREATED.getValue(),
                TransportOrderStatus.REQUESTED.getValue(),
                TransportOrderStatus.ACCEPTED.getValue(),
                TransportOrderStatus.STARTED.getValue()
        );

        // 1) 완료 수량 집계
        NumberExpression<Long> completedCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportStatus.eq(TransportOrderStatus.COMPLETED.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        // 2) 진행 중 Inbound 집계
        NumberExpression<Long> inboundProgressExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.INBOUND.getValue())
                        .and(transportOrderEntity.transportStatus.in(inProgressStatuses)))
                .then(1L)
                .otherwise(0L)
                .sum();

        // 3) 진행 중 Outbound 집계
        NumberExpression<Long> outboundProgressExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.OUTBOUND.getValue())
                        .and(transportOrderEntity.transportStatus.in(inProgressStatuses)))
                .then(1L)
                .otherwise(0L)
                .sum();

        TransportOrderStatisticsResponse response = queryFactory
                .select(new QTransportOrderStatisticsResponse(
                        transportOrderEntity.workStationId,
                        completedCountExpr,
                        inboundProgressExpr,
                        outboundProgressExpr
                ))
                .from(transportOrderEntity)
                .where(
                        workStationIdEq(workStationId),
                        transportOrderEntity.createTime.between(startOfDay, endOfDay)
                )
                .groupBy(transportOrderEntity.workStationId)
                .fetchOne();

        // 데이터가 없는 경우 0으로 초기화된 기본 객체 반환
        if (response == null) {
            return new TransportOrderStatisticsResponse(workStationId, 0L, 0L, 0L);
        }

        return response;
    }

    @Override
    public Page<TransportOrder> findRecentTransportOrders(String workStationId, String transportType, int limit) {
        // 1. transportType에 따른 status 조건 목록 분기
        List<String> targetStatuses;

        if (TransportOrderType.INBOUND.getValue().equalsIgnoreCase(transportType)) {
            targetStatuses = Arrays.asList(
                    TransportOrderStatus.ACCEPTED.getValue(),
                    TransportOrderStatus.STARTED.getValue(),
                    TransportOrderStatus.COMPLETED.getValue()
            );
        } else if (TransportOrderType.OUTBOUND.getValue().equalsIgnoreCase(transportType)) {
            targetStatuses = Arrays.asList(
                    TransportOrderStatus.CREATED.getValue(),
                    TransportOrderStatus.REQUESTED.getValue(),
                    TransportOrderStatus.ACCEPTED.getValue(),
                    TransportOrderStatus.STARTED.getValue()
            );
        } else {
            targetStatuses = Collections.emptyList();
        }

        // 2. 기본 쿼리 생성
        JPAQuery<TransportOrderEntity> query = queryFactory
                .selectFrom(transportOrderEntity)
                .where(
                        workStationIdEq(workStationId),
                        transportTypeEq(transportType),
                        transportStatusIn(targetStatuses)
                )
                .orderBy(
                        transportOrderEntity.createTime.desc(),
                        transportOrderEntity.id.desc()
                );

        // 3. limit 적용 분기: "I"인 경우에만 limit 설정
        Pageable pageable;
        if (TransportOrderType.INBOUND.getValue().equalsIgnoreCase(transportType)) {
            query.limit(limit);
            pageable = PageRequest.of(0, limit);
        } else {
            pageable = Pageable.unpaged();
        }

        // 4. 데이터 조회 및 Domain 변환 (람다 미사용)
        List<TransportOrderEntity> entities = query.fetch();
        List<TransportOrder> content = new ArrayList<>();
        for (TransportOrderEntity entity : entities) {
            content.add(transportOrderMapper.toDomain(entity));
        }

        // 5. Total Count 조회
        long total;
        if (TransportOrderType.INBOUND.getValue().equalsIgnoreCase(transportType)) {
            Long count = queryFactory
                    .select(transportOrderEntity.count())
                    .from(transportOrderEntity)
                    .where(
                            workStationIdEq(workStationId),
                            transportTypeEq(transportType),
                            transportStatusIn(targetStatuses)
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<WorkStationTransportCountResponse> getWorkStationTransportCounts(LocalDate targetDate, Pageable pageable) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        // 1. Inbound 집계 표현식
        NumberExpression<Long> inboundCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.INBOUND.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        // 2. Outbound 집계 표현식
        NumberExpression<Long> outboundCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.OUTBOUND.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        // 3. 페이징 데이터 쿼리
        JPAQuery<WorkStationTransportCountResponse> query = queryFactory
                .select(new QWorkStationTransportCountResponse(
                        transportOrderEntity.workStationId,
                        inboundCountExpr,
                        outboundCountExpr
                ))
                .from(transportOrderEntity)
                .where(
                        transportOrderEntity.workStationId.isNotNull(),
                        transportOrderEntity.createTime.goe(startOfDay),
                        transportOrderEntity.createTime.lt(startOfNextDay)
                )
                .groupBy(transportOrderEntity.workStationId)
                .orderBy(transportOrderEntity.workStationId.asc());

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WorkStationTransportCountResponse> content = query.fetch();

        // 4. 총 그룹 개수(distinct workStationId) 조회
        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(transportOrderEntity.workStationId.countDistinct())
                    .from(transportOrderEntity)
                    .where(
                            transportOrderEntity.workStationId.isNotNull(),
                            transportOrderEntity.createTime.goe(startOfDay),
                            transportOrderEntity.createTime.lt(startOfNextDay)
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable, total);
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

    private BooleanExpression workStationIdEq(String workStationId) {
        return StringUtils.hasText(workStationId) ? transportOrderEntity.workStationId.eq(workStationId) : null;
    }

    private BooleanExpression transportTypeEq(String transportType) {
        return StringUtils.hasText(transportType) ? transportOrderEntity.transportType.eq(transportType) : null;
    }

    // status IN 절 동적 쿼리 헬퍼 메소드
    private BooleanExpression transportStatusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return transportOrderEntity.transportStatus.in(statuses);
    }

}
