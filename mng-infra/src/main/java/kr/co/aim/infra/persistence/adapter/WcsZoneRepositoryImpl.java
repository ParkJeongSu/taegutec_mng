package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsZoneSearchCondition;
import kr.co.aim.domain.model.WcsZone;
import kr.co.aim.domain.repository.WcsZoneRepository;
import kr.co.aim.infra.persistence.entity.QWcsZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsZoneId;
import kr.co.aim.infra.persistence.mapper.WcsZoneMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsZoneJpaRepository;
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
public class WcsZoneRepositoryImpl implements WcsZoneRepository {

    private final WcsZoneJpaRepository wcsZoneJpaRepository;
    private final WcsZoneMapper wcsZoneMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsZoneEntity qZone = QWcsZoneEntity.wcsZoneEntity;

    @Override
    public List<WcsZone> findAll() {
        List<WcsZoneEntity> entities = wcsZoneJpaRepository.findAll();
        List<WcsZone> result = new ArrayList<>();
        for (WcsZoneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsZoneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsZone> findById(String factoryName, String zoneName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(zoneName)) {
            return Optional.empty();
        }
        Optional<WcsZoneEntity> entityOpt = wcsZoneJpaRepository.findById(new WcsZoneId(factoryName, zoneName));
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsZoneMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsZone> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsZoneEntity> entities = wcsZoneJpaRepository.findByFactoryName(factoryName);
        List<WcsZone> result = new ArrayList<>();
        for (WcsZoneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsZoneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String zoneName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(zoneName)) {
            return false;
        }
        return wcsZoneJpaRepository.existsById(new WcsZoneId(factoryName, zoneName));
    }

    @Override
    public WcsZone save(WcsZone zone) {
        WcsZoneEntity entity = wcsZoneMapper.toEntity(zone);
        WcsZoneEntity savedEntity = wcsZoneJpaRepository.save(entity);
        return wcsZoneMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String zoneName) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(zoneName)) {
            wcsZoneJpaRepository.deleteById(new WcsZoneId(factoryName, zoneName));
        }
    }

    @Override
    public Page<WcsZone> findZones(WcsZoneSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String zoneName = (condition != null) ? condition.getZoneName() : null;
        String zoneType = (condition != null) ? condition.getZoneType() : null;
        String loadType = (condition != null) ? condition.getLoadType() : null;
        Boolean waitingAreaFlag = (condition != null) ? condition.getWaitingAreaFlag() : null;
        String shelfSelectMode = (condition != null) ? condition.getShelfSelectMode() : null;
        String zoneColor = (condition != null) ? condition.getZoneColor() : null;
        Boolean deepFirstFlag = (condition != null) ? condition.getDeepFirstFlag() : null;

        JPAQuery<WcsZoneEntity> query = queryFactory
                .selectFrom(qZone)
                .where(
                        factoryNameContains(factoryName),
                        zoneNameContains(zoneName),
                        zoneTypeContains(zoneType),
                        loadTypeContains(loadType),
                        waitingAreaFlagEq(waitingAreaFlag),
                        shelfSelectModeContains(shelfSelectMode),
                        zoneColorContains(zoneColor),
                        deepFirstFlagEq(deepFirstFlag)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsZoneEntity> content = query.fetch();
        List<WcsZone> resultList = new ArrayList<>();
        for (WcsZoneEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsZoneMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qZone.count())
                    .from(qZone)
                    .where(
                            factoryNameContains(factoryName),
                            zoneNameContains(zoneName),
                            zoneTypeContains(zoneType),
                            loadTypeContains(loadType),
                            waitingAreaFlagEq(waitingAreaFlag),
                            shelfSelectModeContains(shelfSelectMode),
                            zoneColorContains(zoneColor),
                            deepFirstFlagEq(deepFirstFlag)
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
                PathBuilder pathBuilder = new PathBuilder<>(qZone.getType(), qZone.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qZone.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qZone.zoneName));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qZone.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? qZone.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression zoneTypeContains(String zoneType) {
        return StringUtils.hasText(zoneType) ? qZone.zoneType.contains(zoneType) : null;
    }

    private BooleanExpression loadTypeContains(String loadType) {
        return StringUtils.hasText(loadType) ? qZone.loadType.contains(loadType) : null;
    }

    private BooleanExpression waitingAreaFlagEq(Boolean waitingAreaFlag) {
        return (waitingAreaFlag != null) ? qZone.waitingAreaFlag.eq(waitingAreaFlag) : null;
    }

    private BooleanExpression shelfSelectModeContains(String shelfSelectMode) {
        return StringUtils.hasText(shelfSelectMode) ? qZone.shelfSelectMode.contains(shelfSelectMode) : null;
    }

    private BooleanExpression zoneColorContains(String zoneColor) {
        return StringUtils.hasText(zoneColor) ? qZone.zoneColor.contains(zoneColor) : null;
    }

    private BooleanExpression deepFirstFlagEq(Boolean deepFirstFlag) {
        return (deepFirstFlag != null) ? qZone.deepFirstFlag.eq(deepFirstFlag) : null;
    }
}
