package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsStockerSearchCondition;
import kr.co.aim.domain.model.WcsStocker;
import kr.co.aim.domain.repository.WcsStockerRepository;
import kr.co.aim.infra.persistence.entity.QWcsStockerEntity;
import kr.co.aim.infra.persistence.entity.WcsStockerEntity;
import kr.co.aim.infra.persistence.entity.WcsStockerId;
import kr.co.aim.infra.persistence.mapper.WcsStockerMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsStockerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WcsStockerRepositoryImpl implements WcsStockerRepository {

    private final WcsStockerJpaRepository wcsStockerJpaRepository;
    private final WcsStockerMapper wcsStockerMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsStockerEntity qStocker = QWcsStockerEntity.wcsStockerEntity;

    @Override
    public List<WcsStocker> findAll() {
        List<WcsStockerEntity> entities = wcsStockerJpaRepository.findAll();
        List<WcsStocker> result = new ArrayList<>();
        for (WcsStockerEntity entity : entities) {
            if (entity != null) {
                result.add(wcsStockerMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsStocker> findById(String factoryName, String stockerName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            return Optional.empty();
        }
        Optional<WcsStockerEntity> entityOpt = wcsStockerJpaRepository.findById(new WcsStockerId(factoryName, stockerName));
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsStockerMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsStocker> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsStockerEntity> entities = wcsStockerJpaRepository.findByFactoryName(factoryName);
        List<WcsStocker> result = new ArrayList<>();
        for (WcsStockerEntity entity : entities) {
            if (entity != null) {
                result.add(wcsStockerMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String stockerName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            return false;
        }
        return wcsStockerJpaRepository.existsById(new WcsStockerId(factoryName, stockerName));
    }

    @Override
    public WcsStocker save(WcsStocker stocker) {
        WcsStockerEntity entity = wcsStockerMapper.toEntity(stocker);
        WcsStockerEntity savedEntity = wcsStockerJpaRepository.save(entity);
        return wcsStockerMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String stockerName) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(stockerName)) {
            wcsStockerJpaRepository.deleteById(new WcsStockerId(factoryName, stockerName));
        }
    }

    @Override
    public Page<WcsStocker> findStockers(WcsStockerSearchCondition condition, Pageable pageable) {
        String factoryName = condition != null ? condition.getFactoryName() : null;
        String stockerName = condition != null ? condition.getStockerName() : null;
        String stockerType = condition != null ? condition.getStockerType() : null;
        String stockerStatus = condition != null ? condition.getStockerStatus() : null;
        String stockerMode = condition != null ? condition.getStockerMode() : null;
        String onlineControlStatus = condition != null ? condition.getOnlineControlStatus() : null;
        String operationMode = condition != null ? condition.getOperationMode() : null;
        String serverName = condition != null ? condition.getServerName() : null;
        String stockerConnectionStatus = condition != null ? condition.getStockerConnectionStatus() : null;
        String stockerNumber = condition != null ? condition.getStockerNumber() : null;
        String machineTypeName = condition != null ? condition.getMachineTypeName() : null;
        String eqRouteKey = condition != null ? condition.getEqRouteKey() : null;
        String areaName = condition != null ? condition.getAreaName() : null;
        Boolean stockerArrangeEnabled = condition != null ? condition.getStockerArrangeEnabled() : null;
        String stockerArrangeMode = condition != null ? condition.getStockerArrangeMode() : null;
        String stockerArrangeScheduleType = condition != null ? condition.getStockerArrangeScheduleType() : null;
        String stockerArrangeState = condition != null ? condition.getStockerArrangeState() : null;

        JPAQuery<WcsStockerEntity> query = queryFactory
                .selectFrom(qStocker)
                .where(
                        factoryNameContains(factoryName),
                        stockerNameContains(stockerName),
                        stockerTypeContains(stockerType),
                        stockerStatusEq(stockerStatus),
                        stockerModeEq(stockerMode),
                        onlineControlStatusEq(onlineControlStatus),
                        operationModeEq(operationMode),
                        serverNameContains(serverName),
                        stockerConnectionStatusEq(stockerConnectionStatus),
                        stockerNumberContains(stockerNumber),
                        machineTypeNameContains(machineTypeName),
                        eqRouteKeyContains(eqRouteKey),
                        areaNameContains(areaName),
                        stockerArrangeEnabledEq(stockerArrangeEnabled),
                        stockerArrangeModeEq(stockerArrangeMode),
                        stockerArrangeScheduleTypeEq(stockerArrangeScheduleType),
                        stockerArrangeStateEq(stockerArrangeState)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsStockerEntity> content = query.fetch();
        List<WcsStocker> resultList = new ArrayList<>();
        for (WcsStockerEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsStockerMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qStocker.count())
                    .from(qStocker)
                    .where(
                            factoryNameContains(factoryName),
                            stockerNameContains(stockerName),
                            stockerTypeContains(stockerType),
                            stockerStatusEq(stockerStatus),
                            stockerModeEq(stockerMode),
                            onlineControlStatusEq(onlineControlStatus),
                            operationModeEq(operationMode),
                            serverNameContains(serverName),
                            stockerConnectionStatusEq(stockerConnectionStatus),
                            stockerNumberContains(stockerNumber),
                            machineTypeNameContains(machineTypeName),
                            eqRouteKeyContains(eqRouteKey),
                            areaNameContains(areaName),
                            stockerArrangeEnabledEq(stockerArrangeEnabled),
                            stockerArrangeModeEq(stockerArrangeMode),
                            stockerArrangeScheduleTypeEq(stockerArrangeScheduleType),
                            stockerArrangeStateEq(stockerArrangeState)
                    )
                    .fetchOne();
            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = resultList.size();
        }

        return new PageImpl<>(resultList, pageable != null ? pageable : Pageable.unpaged(), total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(qStocker.getType(), qStocker.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qStocker.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qStocker.stockerName));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qStocker.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression stockerNameContains(String stockerName) {
        return StringUtils.hasText(stockerName) ? qStocker.stockerName.contains(stockerName) : null;
    }

    private BooleanExpression stockerTypeContains(String stockerType) {
        return StringUtils.hasText(stockerType) ? qStocker.stockerType.contains(stockerType) : null;
    }

    private BooleanExpression stockerStatusEq(String stockerStatus) {
        return StringUtils.hasText(stockerStatus) ? qStocker.stockerStatus.equalsIgnoreCase(stockerStatus) : null;
    }

    private BooleanExpression stockerModeEq(String stockerMode) {
        return StringUtils.hasText(stockerMode) ? qStocker.stockerMode.equalsIgnoreCase(stockerMode) : null;
    }

    private BooleanExpression onlineControlStatusEq(String onlineControlStatus) {
        return StringUtils.hasText(onlineControlStatus) ? qStocker.onlineControlStatus.equalsIgnoreCase(onlineControlStatus) : null;
    }

    private BooleanExpression operationModeEq(String operationMode) {
        return StringUtils.hasText(operationMode) ? qStocker.operationMode.equalsIgnoreCase(operationMode) : null;
    }

    private BooleanExpression serverNameContains(String serverName) {
        return StringUtils.hasText(serverName) ? qStocker.serverName.contains(serverName) : null;
    }

    private BooleanExpression stockerConnectionStatusEq(String stockerConnectionStatus) {
        return StringUtils.hasText(stockerConnectionStatus) ? qStocker.stockerConnectionStatus.equalsIgnoreCase(stockerConnectionStatus) : null;
    }

    private BooleanExpression stockerNumberContains(String stockerNumber) {
        return StringUtils.hasText(stockerNumber) ? qStocker.stockerNumber.contains(stockerNumber) : null;
    }

    private BooleanExpression machineTypeNameContains(String machineTypeName) {
        return StringUtils.hasText(machineTypeName) ? qStocker.machineTypeName.contains(machineTypeName) : null;
    }

    private BooleanExpression eqRouteKeyContains(String eqRouteKey) {
        return StringUtils.hasText(eqRouteKey) ? qStocker.eqRouteKey.contains(eqRouteKey) : null;
    }

    private BooleanExpression areaNameContains(String areaName) {
        return StringUtils.hasText(areaName) ? qStocker.areaName.contains(areaName) : null;
    }

    private BooleanExpression stockerArrangeEnabledEq(Boolean stockerArrangeEnabled) {
        return stockerArrangeEnabled != null ? qStocker.stockerArrangeEnabled.eq(stockerArrangeEnabled) : null;
    }

    private BooleanExpression stockerArrangeModeEq(String stockerArrangeMode) {
        return StringUtils.hasText(stockerArrangeMode) ? qStocker.stockerArrangeMode.equalsIgnoreCase(stockerArrangeMode) : null;
    }

    private BooleanExpression stockerArrangeScheduleTypeEq(String stockerArrangeScheduleType) {
        return StringUtils.hasText(stockerArrangeScheduleType) ? qStocker.stockerArrangeScheduleType.equalsIgnoreCase(stockerArrangeScheduleType) : null;
    }

    private BooleanExpression stockerArrangeStateEq(String stockerArrangeState) {
        return StringUtils.hasText(stockerArrangeState) ? qStocker.stockerArrangeState.equalsIgnoreCase(stockerArrangeState) : null;
    }
}
