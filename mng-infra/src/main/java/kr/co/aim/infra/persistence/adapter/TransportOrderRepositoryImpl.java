package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.domain.repository.dto.RecentTransportOrderResponse;
import kr.co.aim.domain.repository.dto.insert.DailyTransportSummaryResponse;
import kr.co.aim.domain.repository.dto.insert.TransportOrderStatisticsResponse;
import kr.co.aim.domain.repository.dto.insert.WarehouseStationTransportCountResponse;
import kr.co.aim.domain.repository.dto.insert.WorkStationTransportCountResponse;
import kr.co.aim.common.enums.TransportOrderStatus;
import kr.co.aim.common.enums.TransportOrderType;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.domain.repository.condition.TransportOrderSearchCondition;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kr.co.aim.infra.persistence.entity.QTransportJobEntity.transportJobEntity;
import static kr.co.aim.infra.persistence.entity.QTransportOrderEntity.transportOrderEntity;

@Repository
@RequiredArgsConstructor
public class TransportOrderRepositoryImpl implements TransportOrderRepository {

    private final TransportOrderJpaRepository transportOrderJpaRepository;
    private final TransportOrderMapper transportOrderMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public TransportOrder save(TransportOrder transportOrder) {
        TransportOrderEntity entity = transportOrderMapper.toEntity(transportOrder);
        TransportOrderEntity savedEntity = transportOrderJpaRepository.save(entity);
        return transportOrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TransportOrder> findById(Long id) {
        Optional<TransportOrderEntity> entityOptional = transportOrderJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.of(transportOrderMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<TransportOrder> findByTransportTypeInAndTransportStatus(List<String> types, String status) {
        List<TransportOrderEntity> entities = transportOrderJpaRepository.findByTransportTypeInAndTransportStatus(types, status);
        List<TransportOrder> domainList = new ArrayList<>();
        for (TransportOrderEntity entity : entities) {
            domainList.add(transportOrderMapper.toDomain(entity));
        }
        return domainList;
    }

    @Override
    public Optional<TransportOrder> findWithLockById(Long id) {
        Optional<TransportOrderEntity> entityOptional = transportOrderJpaRepository.findWithLockById(id);
        if (entityOptional.isPresent()) {
            return Optional.of(transportOrderMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<TransportOrder> findByTransportOrderId(String transportOrderId) {
        Optional<TransportOrderEntity> entityOptional = transportOrderJpaRepository.findByTransportOrderId(transportOrderId);
        if (entityOptional.isPresent()) {
            return Optional.of(transportOrderMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<TransportOrder> findTransportOrderByCondition(String carrierName, String transportType, List<String> transportStatus) {
        List<TransportOrderEntity> entities = transportOrderJpaRepository.findTransportOrderByCondition(carrierName, transportType, transportStatus);
        List<TransportOrder> domainList = new ArrayList<>();
        for (TransportOrderEntity entity : entities) {
            domainList.add(transportOrderMapper.toDomain(entity));
        }
        return domainList;
    }

    @Override
    public List<TransportOrder> findOutboundOrderForTransportRequest(String transportType, String transportStatus, String workStationId) {
        List<TransportOrderEntity> entities = transportOrderJpaRepository.findOutboundOrderForTransportRequest(transportType, transportStatus, workStationId);
        List<TransportOrder> domainList = new ArrayList<>();
        for (TransportOrderEntity entity : entities) {
            domainList.add(transportOrderMapper.toDomain(entity));
        }
        return domainList;
    }

    @Override
    public List<TransportOrder> findOutboundOrderForTransportRequest(String transportType, List<String> transportStatus, String workStationId) {
        List<TransportOrderEntity> entities = transportOrderJpaRepository.findOutboundOrderForTransportRequest(transportType, transportStatus, workStationId);
        List<TransportOrder> domainList = new ArrayList<>();
        for (TransportOrderEntity entity : entities) {
            domainList.add(transportOrderMapper.toDomain(entity));
        }
        return domainList;
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
        List<TransportOrder> converted = new ArrayList<>();
        for (TransportOrderEntity entity : content) {
            converted.add(transportOrderMapper.toDomain(entity));
        }

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

        NumberExpression<Long> completedCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportStatus.eq(TransportOrderStatus.COMPLETED.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        NumberExpression<Long> inboundProgressExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.INBOUND.getValue())
                        .and(transportOrderEntity.transportStatus.in(inProgressStatuses)))
                .then(1L)
                .otherwise(0L)
                .sum();

        NumberExpression<Long> outboundProgressExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.OUTBOUND.getValue())
                        .and(transportOrderEntity.transportStatus.in(inProgressStatuses)))
                .then(1L)
                .otherwise(0L)
                .sum();

        TransportOrderStatisticsResponse response = queryFactory
                .select(Projections.fields(TransportOrderStatisticsResponse.class,
                        transportOrderEntity.workStationId.as("workStationId"),
                        completedCountExpr.coalesce(0L).as("completedCount"),
                        inboundProgressExpr.coalesce(0L).as("inboundProgressCount"),
                        outboundProgressExpr.coalesce(0L).as("outboundProgressCount")
                ))
                .from(transportOrderEntity)
                .where(
                        workStationIdEq(workStationId),
                        transportOrderEntity.createTime.between(startOfDay, endOfDay)
                )
                .groupBy(transportOrderEntity.workStationId)
                .fetchOne();

        if (response == null) {
            TransportOrderStatisticsResponse defaultResponse = new TransportOrderStatisticsResponse();
            defaultResponse.setWorkStationId(workStationId);
            defaultResponse.setCompletedCount(0L);
            defaultResponse.setInboundProgressCount(0L);
            defaultResponse.setOutboundProgressCount(0L);
            defaultResponse.setTotalProgressCount(0L);
            return defaultResponse;
        }

        long inbound = response.getInboundProgressCount() != null ? response.getInboundProgressCount() : 0L;
        long outbound = response.getOutboundProgressCount() != null ? response.getOutboundProgressCount() : 0L;
        response.setTotalProgressCount(inbound + outbound);

        return response;
    }

    @Override
    public Page<RecentTransportOrderResponse> findRecentTransportOrders(String workStationId, String transportType, int limit) {
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

        Pageable pageable = PageRequest.of(0, limit);

        List<RecentTransportOrderResponse> content = queryFactory
                .select(Projections.fields(RecentTransportOrderResponse.class,
                        transportOrderEntity.id.as("id"),
                        transportOrderEntity.transportOrderId.as("transportOrderId"),
                        transportOrderEntity.idocId.as("idocId"),
                        transportOrderEntity.description.as("description"),
                        transportOrderEntity.carrierName.as("carrierName"),
                        transportOrderEntity.virtualCarrierName.as("virtualCarrierName"),
                        transportOrderEntity.transportType.as("transportType"),
                        transportOrderEntity.transportStatus.as("transportStatus"),
                        transportOrderEntity.lastTransactionCode.as("lastTransactionCode"),
                        transportOrderEntity.carrierType.as("carrierType"),
                        transportOrderEntity.priority.as("priority"),
                        transportOrderEntity.galId.as("galId"),
                        transportOrderEntity.galWarehouse.as("galWarehouse"),
                        transportOrderEntity.locationId.as("locationId"),
                        transportOrderEntity.workStationId.as("workStationId"),
                        transportOrderEntity.sourceZoneName.as("sourceZoneName"),
                        transportOrderEntity.destinationZoneName.as("destinationZoneName"),
                        transportOrderEntity.errorText.as("errorText"),
                        transportOrderEntity.actualWeight.as("actualWeight"),
                        transportOrderEntity.requestedZoneName.as("requestedZoneName"),
                        transportOrderEntity.actualZoneName.as("actualZoneName"),
                        transportOrderEntity.actualLocationId.as("actualLocationId"),
                        transportOrderEntity.travelProfile.as("travelProfile"),
                        transportOrderEntity.createTime.as("createTime"),
                        transportOrderEntity.releaseTime.as("releaseTime"),
                        transportOrderEntity.completeTime.as("completeTime"),
                        transportOrderEntity.retrievalTime.as("retrievalTime"),
                        transportOrderEntity.createUser.as("createUser"),
                        transportOrderEntity.releaseUser.as("releaseUser"),
                        transportOrderEntity.completeUser.as("completeUser"),
                        transportOrderEntity.eventName.as("eventName"),
                        transportOrderEntity.eventTime.as("eventTime"),
                        transportOrderEntity.eventUser.as("eventUser"),
                        transportOrderEntity.eventComment.as("eventComment"),
                        transportJobEntity.transportJobName.as("transportJobName")
                ))
                .from(transportOrderEntity)
                .leftJoin(transportJobEntity).on(transportJobEntity.orderId.eq(transportOrderEntity.transportOrderId))
                .where(
                        workStationIdEq(workStationId),
                        transportTypeEq(transportType),
                        transportStatusIn(targetStatuses)
                )
                .orderBy(
                        transportOrderEntity.createTime.desc(),
                        transportOrderEntity.id.desc()
                )
                .limit(limit)
                .fetch();

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

        NumberExpression<Long> inboundCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.INBOUND.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        NumberExpression<Long> outboundCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.OUTBOUND.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        // inbound + outbound 합산 표현식
        NumberExpression<Long> totalCountExpr = inboundCountExpr.add(outboundCountExpr);

        JPAQuery<WorkStationTransportCountResponse> query = queryFactory
                .select(Projections.fields(WorkStationTransportCountResponse.class,
                        transportOrderEntity.workStationId.as("workStationId"),
                        inboundCountExpr.coalesce(0L).as("inboundCount"),
                        outboundCountExpr.coalesce(0L).as("outboundCount"),
                        totalCountExpr.coalesce(0L).as("totalCount")
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

    @Override
    public DailyTransportSummaryResponse getDailyTransportSummary(LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        BooleanExpression dateCondition = transportOrderEntity.createTime.goe(startOfDay)
                .and(transportOrderEntity.createTime.lt(startOfNextDay));

        NumberExpression<Long> inboundCase = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.INBOUND.getValue()))
                .then(1L)
                .otherwise(0L);

        NumberExpression<Long> outboundCase = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.OUTBOUND.getValue()))
                .then(1L)
                .otherwise(0L);

        NumberExpression<Long> relocationCase = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.RELOCATION.getValue()))
                .then(1L)
                .otherwise(0L);

        Tuple tuple = queryFactory
                .select(
                        transportOrderEntity.count(),
                        inboundCase.sum().coalesce(0L),
                        outboundCase.sum().coalesce(0L),
                        relocationCase.sum().coalesce(0L)
                )
                .from(transportOrderEntity)
                .where(dateCondition)
                .fetchOne();

        if (tuple == null) {
            return DailyTransportSummaryResponse.builder()
                    .totalCount(0L)
                    .inboundCount(0L)
                    .outboundCount(0L)
                    .relocationCount(0L)
                    .build();
        }

        Long totalCount = tuple.get(0, Long.class);
        Long inboundCount = tuple.get(1, Long.class);
        Long outboundCount = tuple.get(2, Long.class);
        Long relocationCount = tuple.get(3, Long.class);

        return DailyTransportSummaryResponse.builder()
                .totalCount(totalCount != null ? totalCount : 0L)
                .inboundCount(inboundCount != null ? inboundCount : 0L)
                .outboundCount(outboundCount != null ? outboundCount : 0L)
                .relocationCount(relocationCount != null ? relocationCount : 0L)
                .build();
    }

    @Override
    public Page<WarehouseStationTransportCountResponse> getWarehouseStationTransportCounts(LocalDate targetDate, Pageable pageable) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        BooleanExpression whereCondition = transportOrderEntity.galWarehouse.isNotNull()
                .and(transportOrderEntity.workStationId.isNotNull())
                .and(transportOrderEntity.createTime.goe(startOfDay))
                .and(transportOrderEntity.createTime.lt(startOfNextDay));

        NumberExpression<Long> inboundCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.INBOUND.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        NumberExpression<Long> outboundCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.OUTBOUND.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        NumberExpression<Long> relocationCountExpr = new CaseBuilder()
                .when(transportOrderEntity.transportType.eq(TransportOrderType.RELOCATION.getValue()))
                .then(1L)
                .otherwise(0L)
                .sum();

        JPAQuery<WarehouseStationTransportCountResponse> query = queryFactory
                .select(Projections.fields(WarehouseStationTransportCountResponse.class,
                        transportOrderEntity.galWarehouse.as("galWarehouse"),
                        transportOrderEntity.workStationId.as("workStationId"),
                        inboundCountExpr.coalesce(0L).as("inboundCount"),
                        outboundCountExpr.coalesce(0L).as("outboundCount"),
                        relocationCountExpr.coalesce(0L).as("relocationCount")
                ))
                .from(transportOrderEntity)
                .where(whereCondition)
                .groupBy(transportOrderEntity.galWarehouse, transportOrderEntity.workStationId)
                .orderBy(
                        transportOrderEntity.galWarehouse.asc(),
                        transportOrderEntity.workStationId.asc()
                );

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WarehouseStationTransportCountResponse> content = query.fetch();

        long total;
        if (pageable.isPaged()) {
            List<Tuple> countList = queryFactory
                    .select(transportOrderEntity.galWarehouse, transportOrderEntity.workStationId)
                    .from(transportOrderEntity)
                    .where(whereCondition)
                    .groupBy(transportOrderEntity.galWarehouse, transportOrderEntity.workStationId)
                    .fetch();
            total = countList.size();
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

    private BooleanExpression transportStatusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return transportOrderEntity.transportStatus.in(statuses);
    }
}