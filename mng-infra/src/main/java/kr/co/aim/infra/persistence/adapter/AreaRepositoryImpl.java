package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Area;
import kr.co.aim.domain.repository.AreaRepository;
import kr.co.aim.domain.repository.condition.AreaSearchCondition;
import kr.co.aim.infra.persistence.entity.AreaEntity;
import kr.co.aim.infra.persistence.mapper.AreaMapper;
import kr.co.aim.infra.persistence.springdatajpa.AreaJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QAreaEntity.areaEntity;

/**
 * AreaRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 AreaJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class AreaRepositoryImpl implements AreaRepository {

    private final AreaJpaRepository areaJpaRepository;
    private final AreaMapper areaMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Area> findAll() {
        List<AreaEntity> entities = areaJpaRepository.findAll();
        List<Area> result = new ArrayList<>();
        for (AreaEntity entity : entities) {
            result.add(areaMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Area> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<AreaEntity> entityOptional = areaJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(areaMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Area> findByAreaName(String areaName) {
        if (!StringUtils.hasText(areaName)) {
            return Optional.empty();
        }
        Optional<AreaEntity> entityOptional = areaJpaRepository.findByAreaName(areaName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(areaMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Area save(Area area) {
        AreaEntity entity = areaMapper.toEntity(area);
        AreaEntity savedEntity = areaJpaRepository.save(entity);
        return areaMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        areaJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        areaJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Area> findAreasWithConditions(AreaSearchCondition condition, Pageable pageable) {
        JPAQuery<AreaEntity> query = queryFactory
                .select(areaEntity)
                .from(areaEntity)
                .where(
                        areaNameContains(condition.getAreaName()),
                        descriptionContains(condition.getDescription()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<AreaEntity> content = query.fetch();
        List<Area> converted = new ArrayList<>();
        for (AreaEntity entity : content) {
            if (entity != null) {
                Area domain = areaMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(areaEntity.count())
                    .from(areaEntity)
                    .where(
                            areaNameContains(condition.getAreaName()),
                            descriptionContains(condition.getDescription()),
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
                PathBuilder pathBuilder = new PathBuilder<>(areaEntity.getType(), areaEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, areaEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression areaNameContains(String areaName) {
        return StringUtils.hasText(areaName) ? areaEntity.areaName.contains(areaName) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? areaEntity.description.contains(description) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? areaEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? areaEntity.eventUser.contains(eventUser) : null;
    }
}
