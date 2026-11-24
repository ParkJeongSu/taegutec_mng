package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.ZoneFallback;
import kr.co.aim.domain.repository.ZoneFallbackRepository;
import kr.co.aim.domain.repository.condition.ZoneFallbackSearchCondition;
import kr.co.aim.infra.persistence.entity.ZoneFallbackEntity;
import kr.co.aim.infra.persistence.mapper.ZoneFallbackMapper;
import kr.co.aim.infra.persistence.springdatajpa.ZoneFallbackJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QZoneFallbackEntity.zoneFallbackEntity;

/**
 * ZoneFallbackRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 ZoneFallbackJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class ZoneFallbackRepositoryImpl implements ZoneFallbackRepository {

    private final ZoneFallbackJpaRepository zoneFallbackJpaRepository;
    private final ZoneFallbackMapper zoneFallbackMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ZoneFallback> findAll() {
        List<ZoneFallbackEntity> entities = zoneFallbackJpaRepository.findAll();
        List<ZoneFallback> result = new ArrayList<>();
        for (ZoneFallbackEntity entity : entities) {
            result.add(zoneFallbackMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<ZoneFallback> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<ZoneFallbackEntity> entityOptional = zoneFallbackJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(zoneFallbackMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public ZoneFallback save(ZoneFallback zoneFallback) {
        ZoneFallbackEntity entity = zoneFallbackMapper.toEntity(zoneFallback);
        ZoneFallbackEntity savedEntity = zoneFallbackJpaRepository.save(entity);
        return zoneFallbackMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        zoneFallbackJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        zoneFallbackJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<ZoneFallback> findZoneFallbacksWithConditions(ZoneFallbackSearchCondition condition, Pageable pageable) {
        JPAQuery<ZoneFallbackEntity> query = queryFactory
                .select(zoneFallbackEntity)
                .from(zoneFallbackEntity)
                .where(
                        sourceZoneIdEq(condition.getSourceZoneId()),
                        targetZoneIdEq(condition.getTargetZoneId()),
                        priorityEq(condition.getPriority()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<ZoneFallbackEntity> content = query.fetch();
        List<ZoneFallback> converted = new ArrayList<>();
        for (ZoneFallbackEntity entity : content) {
            if (entity != null) {
                ZoneFallback domain = zoneFallbackMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(zoneFallbackEntity.count())
                    .from(zoneFallbackEntity)
                    .where(
                            sourceZoneIdEq(condition.getSourceZoneId()),
                            targetZoneIdEq(condition.getTargetZoneId()),
                            priorityEq(condition.getPriority()),
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
                PathBuilder pathBuilder = new PathBuilder<>(zoneFallbackEntity.getType(), zoneFallbackEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, zoneFallbackEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression sourceZoneIdEq(Long sourceZoneId) {
        return sourceZoneId != null ? zoneFallbackEntity.sourceZoneId.eq(sourceZoneId) : null;
    }

    private BooleanExpression targetZoneIdEq(Long targetZoneId) {
        return targetZoneId != null ? zoneFallbackEntity.targetZoneId.eq(targetZoneId) : null;
    }

    private BooleanExpression priorityEq(Integer priority) {
        return priority != null ? zoneFallbackEntity.priority.eq(priority) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? zoneFallbackEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? zoneFallbackEntity.eventUser.contains(eventUser) : null;
    }
}
