package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.common.Utils.StatTimeUtils;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.EquipmentAvailabilityHourlySearchCondition;
import kr.co.aim.common.condition.EquipmentProductivityDailySearchCondition;
import kr.co.aim.common.condition.TransportRouteDailySearchCondition;
import kr.co.aim.common.condition.WorkOrderProcessedDailySearchCondition;
import kr.co.aim.domain.model.EquipmentAvailabilityHourly;
import kr.co.aim.domain.model.EquipmentProductivityDaily;
import kr.co.aim.domain.model.TransportRouteDaily;
import kr.co.aim.domain.model.WorkOrderProcessedDaily;
import kr.co.aim.domain.repository.StatRepository;
import kr.co.aim.infra.persistence.entity.*;
import kr.co.aim.infra.persistence.mapper.*;
import kr.co.aim.infra.persistence.springdatajpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QEquipmentAvailabilityHourlyEntity.equipmentAvailabilityHourlyEntity;
import static kr.co.aim.infra.persistence.entity.QEquipmentProductivityDailyEntity.equipmentProductivityDailyEntity;
import static kr.co.aim.infra.persistence.entity.QProductionOrderHistoryEntity.productionOrderHistoryEntity;
import static kr.co.aim.infra.persistence.entity.QTransportJobHistoryEntity.transportJobHistoryEntity;
import static kr.co.aim.infra.persistence.entity.QTransportRouteDailyEntity.transportRouteDailyEntity;
import static kr.co.aim.infra.persistence.entity.QWorkOrderProcessedDailyEntity.workOrderProcessedDailyEntity;

@Repository
@RequiredArgsConstructor
public class StatRepositoryImpl implements StatRepository {

    private final EquipmentAvailabilityHourlyJpaRepository equipmentAvailabilityHourlyJpaRepository;
    private final EquipmentProductivityDailyJpaRepository equipmentProductivityDailyJpaRepository;
    private final TransportRouteDailyJpaRepository transportRouteDailyJpaRepository;
    private final WorkOrderProcessedDailyJpaRepository workOrderProcessedDailyJpaRepository;

    private final EquipmentAvailabilityHourlyMapper equipmentAvailabilityHourlyMapper;
    private final EquipmentProductivityDailyMapper equipmentProductivityDailyMapper;
    private final TransportRouteDailyMapper transportRouteDailyMapper;
    private final WorkOrderProcessedDailyMapper workOrderProcessedDailyMapper;

    private final JPAQueryFactory queryFactory;

    @Override
    public void saveAvailabilityAll(List<EquipmentAvailabilityHourly> list) {
        List<EquipmentAvailabilityHourlyEntity> entities = new ArrayList<>();
        for (EquipmentAvailabilityHourly domain : list) {
            entities.add(equipmentAvailabilityHourlyMapper.toEntity(domain));
        }
        equipmentAvailabilityHourlyJpaRepository.saveAll(entities);
    }

    @Override
    public void calculateAndSaveProductivity(String statDate) {
        LocalDateTime startOfDay = StatTimeUtils.toStartDateTime(statDate);
        LocalDateTime endOfDay = StatTimeUtils.toEndDateTime(statDate);

        com.querydsl.core.types.dsl.NumberPath<java.math.BigDecimal> totalProcessedQtyAlias =
                com.querydsl.core.types.dsl.Expressions.numberPath(java.math.BigDecimal.class, "totalProcessedQty");
        com.querydsl.core.types.dsl.NumberPath<java.math.BigDecimal> scrappedQtyAlias =
                com.querydsl.core.types.dsl.Expressions.numberPath(java.math.BigDecimal.class, "scrappedQty");

        List<Tuple> results = queryFactory
                .select(
                        productionOrderHistoryEntity.equipmentName,
                        productionOrderHistoryEntity.count(),
                        productionOrderHistoryEntity.endedQuantity.sum().as(totalProcessedQtyAlias),
                        productionOrderHistoryEntity.scrappedQuantity.sum().as(scrappedQtyAlias)
                )
                .from(productionOrderHistoryEntity)
                .where(productionOrderHistoryEntity.completeTime.between(startOfDay, endOfDay))
                .groupBy(productionOrderHistoryEntity.equipmentName)
                .fetch();

        List<EquipmentProductivityDailyEntity> entities = new ArrayList<>();
        for (Tuple tuple : results) {
            String eqpName = tuple.get(productionOrderHistoryEntity.equipmentName);
            Long totalCount = tuple.get(productionOrderHistoryEntity.count());
            java.math.BigDecimal totalProcessedQty = tuple.get(totalProcessedQtyAlias);
            java.math.BigDecimal scrappedQty = tuple.get(scrappedQtyAlias);

            EquipmentProductivityDailyEntity entity = new EquipmentProductivityDailyEntity(
                    TsidUtils.nextId(),
                    statDate,
                    eqpName,
                    totalCount != null ? totalCount.intValue() : 0,
                    totalProcessedQty, totalProcessedQty, scrappedQty, 0
            );
            entities.add(entity);
        }
        equipmentProductivityDailyJpaRepository.saveAll(entities);
    }

    @Override
    public void calculateAndSaveTransportRoute(String statDate) {
        LocalDateTime startOfDay = StatTimeUtils.toStartDateTime(statDate);
        LocalDateTime endOfDay = StatTimeUtils.toEndDateTime(statDate);

        List<Tuple> results = queryFactory
                .select(
                        transportJobHistoryEntity.sourceEquipmentName,
                        transportJobHistoryEntity.destinationEquipmentName,
                        transportJobHistoryEntity.count()
                )
                .from(transportJobHistoryEntity)
                .where(transportJobHistoryEntity.eventTime.between(startOfDay, endOfDay))
                .groupBy(transportJobHistoryEntity.sourceEquipmentName, transportJobHistoryEntity.destinationEquipmentName)
                .fetch();

        List<TransportRouteDailyEntity> entities = new ArrayList<>();
        for (Tuple tuple : results) {
            String srcName = tuple.get(transportJobHistoryEntity.sourceEquipmentName);
            String destName = tuple.get(transportJobHistoryEntity.destinationEquipmentName);
            Long totalCount = tuple.get(transportJobHistoryEntity.count());

            if (srcName != null && destName != null) {
                TransportRouteDailyEntity entity = new TransportRouteDailyEntity(
                        TsidUtils.nextId(),
                        statDate, srcName, destName,
                        totalCount != null ? totalCount.intValue() : 0,
                        0, 0, 0, 0, 0, 0, 0
                );
                entities.add(entity);
            }
        }
        transportRouteDailyJpaRepository.saveAll(entities);
    }

    @Override
    public void calculateAndSaveWorkOrderProcessed(String statDate) {
        com.querydsl.core.types.dsl.NumberPath<java.math.BigDecimal> totalProcessedQtyAlias =
                com.querydsl.core.types.dsl.Expressions.numberPath(java.math.BigDecimal.class, "totalProcessedQty");
        List<Tuple> results = queryFactory
                .select(
                        equipmentProductivityDailyEntity.totalProcessedCount.sum(),
                        equipmentProductivityDailyEntity.totalProcessedQuantity.sum().as(totalProcessedQtyAlias)
                )
                .from(equipmentProductivityDailyEntity)
                .where(equipmentProductivityDailyEntity.statDate.eq(statDate))
                .fetch();

        if (!results.isEmpty()) {
            Tuple tuple = results.get(0);
            Integer sumCount = tuple.get(equipmentProductivityDailyEntity.totalProcessedCount.sum());
            java.math.BigDecimal totalProcessedQty = tuple.get(totalProcessedQtyAlias);

            if (sumCount != null) {
                WorkOrderProcessedDailyEntity entity = new WorkOrderProcessedDailyEntity(
                        TsidUtils.nextId(),
                        statDate,
                        sumCount,
                        0,
                        totalProcessedQty != null ? totalProcessedQty : BigDecimal.ZERO
                );
                workOrderProcessedDailyJpaRepository.save(entity);
            }
        }
    }

    // == 1) EquipmentAvailabilityHourly 조회 ==
    @Override
    public Page<EquipmentAvailabilityHourly> findAvailabilityWithConditions(EquipmentAvailabilityHourlySearchCondition condition, Pageable pageable) {
        JPAQuery<EquipmentAvailabilityHourlyEntity> query = queryFactory
                .selectFrom(equipmentAvailabilityHourlyEntity)
                .where(
                        availabilityStatDateEq(condition.getStatDate()),
                        availabilityStatHourEq(condition.getStatHour()),
                        availabilityEquipmentNameContains(condition.getEquipmentName())
                );

        query.orderBy(getAvailabilityOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<EquipmentAvailabilityHourlyEntity> content = query.fetch();
        List<EquipmentAvailabilityHourly> converted = content.stream().map(equipmentAvailabilityHourlyMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(equipmentAvailabilityHourlyEntity.count())
                    .from(equipmentAvailabilityHourlyEntity)
                    .where(
                            availabilityStatDateEq(condition.getStatDate()),
                            availabilityStatHourEq(condition.getStatHour()),
                            availabilityEquipmentNameContains(condition.getEquipmentName())
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    // == 2) EquipmentProductivityDaily 조회 ==
    @Override
    public Page<EquipmentProductivityDaily> findProductivityWithConditions(EquipmentProductivityDailySearchCondition condition, Pageable pageable) {
        JPAQuery<EquipmentProductivityDailyEntity> query = queryFactory
                .selectFrom(equipmentProductivityDailyEntity)
                .where(
                        productivityStatDateEq(condition.getStatDate()),
                        productivityEquipmentNameContains(condition.getEquipmentName())
                );

        query.orderBy(getProductivityOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<EquipmentProductivityDailyEntity> content = query.fetch();
        List<EquipmentProductivityDaily> converted = content.stream().map(equipmentProductivityDailyMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(equipmentProductivityDailyEntity.count())
                    .from(equipmentProductivityDailyEntity)
                    .where(
                            productivityStatDateEq(condition.getStatDate()),
                            productivityEquipmentNameContains(condition.getEquipmentName())
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    // == 3) TransportRouteDaily 조회 ==
    @Override
    public Page<TransportRouteDaily> findTransportRouteWithConditions(TransportRouteDailySearchCondition condition, Pageable pageable) {
        JPAQuery<TransportRouteDailyEntity> query = queryFactory
                .selectFrom(transportRouteDailyEntity)
                .where(
                        transportRouteStatDateEq(condition.getStatDate()),
                        sourceEquipmentNameContains(condition.getSourceEquipmentName()),
                        destinationEquipmentNameContains(condition.getDestinationEquipmentName())
                );

        query.orderBy(getTransportRouteOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<TransportRouteDailyEntity> content = query.fetch();
        List<TransportRouteDaily> converted = content.stream().map(transportRouteDailyMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(transportRouteDailyEntity.count())
                    .from(transportRouteDailyEntity)
                    .where(
                            transportRouteStatDateEq(condition.getStatDate()),
                            sourceEquipmentNameContains(condition.getSourceEquipmentName()),
                            destinationEquipmentNameContains(condition.getDestinationEquipmentName())
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    // == 4) WorkOrderProcessedDaily 조회 ==
    @Override
    public Page<WorkOrderProcessedDaily> findWorkOrderProcessedWithConditions(WorkOrderProcessedDailySearchCondition condition, Pageable pageable) {
        JPAQuery<WorkOrderProcessedDailyEntity> query = queryFactory
                .selectFrom(workOrderProcessedDailyEntity)
                .where(
                        workOrderStatDateEq(condition.getStatDate())
                );

        query.orderBy(getWorkOrderOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WorkOrderProcessedDailyEntity> content = query.fetch();
        List<WorkOrderProcessedDaily> converted = content.stream().map(workOrderProcessedDailyMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(workOrderProcessedDailyEntity.count())
                    .from(workOrderProcessedDailyEntity)
                    .where(
                            workOrderStatDateEq(condition.getStatDate())
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    // == BooleanExpressions ==
    private BooleanExpression availabilityStatDateEq(String statDate) {
        return StringUtils.hasText(statDate) ? equipmentAvailabilityHourlyEntity.statDate.eq(statDate) : null;
    }

    private BooleanExpression availabilityStatHourEq(String statHour) {
        return StringUtils.hasText(statHour) ? equipmentAvailabilityHourlyEntity.statHour.eq(statHour) : null;
    }

    private BooleanExpression availabilityEquipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? equipmentAvailabilityHourlyEntity.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression productivityStatDateEq(String statDate) {
        return StringUtils.hasText(statDate) ? equipmentProductivityDailyEntity.statDate.eq(statDate) : null;
    }

    private BooleanExpression productivityEquipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? equipmentProductivityDailyEntity.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression transportRouteStatDateEq(String statDate) {
        return StringUtils.hasText(statDate) ? transportRouteDailyEntity.statDate.eq(statDate) : null;
    }

    private BooleanExpression sourceEquipmentNameContains(String sourceEquipmentName) {
        return StringUtils.hasText(sourceEquipmentName) ? transportRouteDailyEntity.sourceEquipmentName.contains(sourceEquipmentName) : null;
    }

    private BooleanExpression destinationEquipmentNameContains(String destinationEquipmentName) {
        return StringUtils.hasText(destinationEquipmentName) ? transportRouteDailyEntity.destinationEquipmentName.contains(destinationEquipmentName) : null;
    }

    private BooleanExpression workOrderStatDateEq(String statDate) {
        return StringUtils.hasText(statDate) ? workOrderProcessedDailyEntity.statDate.eq(statDate) : null;
    }

    // == OrderSpecifiers ==
    private OrderSpecifier<?>[] getAvailabilityOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    PathBuilder pathBuilder = new PathBuilder<>(equipmentAvailabilityHourlyEntity.getType(), equipmentAvailabilityHourlyEntity.getMetadata());
                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, equipmentAvailabilityHourlyEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getProductivityOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    PathBuilder pathBuilder = new PathBuilder<>(equipmentProductivityDailyEntity.getType(), equipmentProductivityDailyEntity.getMetadata());
                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, equipmentProductivityDailyEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getTransportRouteOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    PathBuilder pathBuilder = new PathBuilder<>(transportRouteDailyEntity.getType(), transportRouteDailyEntity.getMetadata());
                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, transportRouteDailyEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getWorkOrderOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    PathBuilder pathBuilder = new PathBuilder<>(workOrderProcessedDailyEntity.getType(), workOrderProcessedDailyEntity.getMetadata());
                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, workOrderProcessedDailyEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}