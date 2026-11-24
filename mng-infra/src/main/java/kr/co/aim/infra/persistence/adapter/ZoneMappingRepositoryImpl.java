package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.ZoneMapping;
import kr.co.aim.domain.repository.ZoneMappingRepository;
import kr.co.aim.domain.repository.condition.ZoneMappingSearchCondition;
import kr.co.aim.infra.persistence.entity.ZoneMappingEntity;
import kr.co.aim.infra.persistence.mapper.ZoneMappingMapper;
import kr.co.aim.infra.persistence.springdatajpa.ZoneMappingJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QZoneMappingEntity.zoneMappingEntity;

/**
 * ZoneMappingRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 ZoneMappingJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class ZoneMappingRepositoryImpl implements ZoneMappingRepository {

    private final ZoneMappingJpaRepository zoneMappingJpaRepository;
    private final ZoneMappingMapper zoneMappingMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ZoneMapping> findAll() {
        List<ZoneMappingEntity> entities = zoneMappingJpaRepository.findAll();
        List<ZoneMapping> result = new ArrayList<>();
        for (ZoneMappingEntity entity : entities) {
            result.add(zoneMappingMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<ZoneMapping> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<ZoneMappingEntity> entityOptional = zoneMappingJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(zoneMappingMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public ZoneMapping save(ZoneMapping zoneMapping) {
        ZoneMappingEntity entity = zoneMappingMapper.toEntity(zoneMapping);
        ZoneMappingEntity savedEntity = zoneMappingJpaRepository.save(entity);
        return zoneMappingMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        zoneMappingJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        zoneMappingJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<ZoneMapping> findZoneMappingsWithConditions(ZoneMappingSearchCondition condition, Pageable pageable) {
        JPAQuery<ZoneMappingEntity> query = queryFactory
                .select(zoneMappingEntity)
                .from(zoneMappingEntity)
                .where(
                        zoneIdEq(condition.getZoneId()),
                        orderLineNumberContains(condition.getOrderLineNumber()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<ZoneMappingEntity> content = query.fetch();
        List<ZoneMapping> converted = new ArrayList<>();
        for (ZoneMappingEntity entity : content) {
            if (entity != null) {
                ZoneMapping domain = zoneMappingMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(zoneMappingEntity.count())
                    .from(zoneMappingEntity)
                    .where(
                            zoneIdEq(condition.getZoneId()),
                            orderLineNumberContains(condition.getOrderLineNumber()),
                            eventNameContains(condition.getEventName()),
                            eventUserContains(condition.getEventUser())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = converted.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(zoneMappingEntity.getType(), zoneMappingEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, zoneMappingEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression zoneIdEq(Long zoneId) {
        return zoneId != null ? zoneMappingEntity.zoneId.eq(zoneId) : null;
    }

    private BooleanExpression orderLineNumberContains(String orderLineNumber) {
        return StringUtils.hasText(orderLineNumber) ? zoneMappingEntity.orderLineNumber.contains(orderLineNumber) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? zoneMappingEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? zoneMappingEntity.eventUser.contains(eventUser) : null;
    }
}
