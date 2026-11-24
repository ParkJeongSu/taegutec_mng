package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Factory;
import kr.co.aim.domain.repository.FactoryRepository;
import kr.co.aim.domain.repository.condition.FactorySearchCondition;
import kr.co.aim.infra.persistence.entity.FactoryEntity;
import kr.co.aim.infra.persistence.mapper.FactoryMapper;
import kr.co.aim.infra.persistence.springdatajpa.FactoryJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QFactoryEntity.factoryEntity;

/**
 * FactoryRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 FactoryJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class FactoryRepositoryImpl implements FactoryRepository {

    private final FactoryJpaRepository factoryJpaRepository;
    private final FactoryMapper factoryMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Factory> findAll() {
        List<FactoryEntity> entities = factoryJpaRepository.findAll();
        List<Factory> result = new ArrayList<>();
        for (FactoryEntity entity : entities) {
            result.add(factoryMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Factory> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<FactoryEntity> entityOptional = factoryJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(factoryMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Factory> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return Optional.empty();
        }
        Optional<FactoryEntity> entityOptional = factoryJpaRepository.findByFactoryName(factoryName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(factoryMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Factory save(Factory factory) {
        FactoryEntity entity = factoryMapper.toEntity(factory);
        FactoryEntity savedEntity = factoryJpaRepository.save(entity);
        return factoryMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        factoryJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        factoryJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Factory> findFactorysWithConditions(FactorySearchCondition condition, Pageable pageable) {
        JPAQuery<FactoryEntity> query = queryFactory
                .select(factoryEntity)
                .from(factoryEntity)
                .where(
                        factoryNameContains(condition.getFactoryName()),
                        descriptionContains(condition.getDescription()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<FactoryEntity> content = query.fetch();
        List<Factory> converted = new ArrayList<>();
        for (FactoryEntity entity : content) {
            if (entity != null) {
                Factory domain = factoryMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(factoryEntity.count())
                    .from(factoryEntity)
                    .where(
                            factoryNameContains(condition.getFactoryName()),
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
                PathBuilder pathBuilder = new PathBuilder<>(factoryEntity.getType(), factoryEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, factoryEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? factoryEntity.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? factoryEntity.description.contains(description) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? factoryEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? factoryEntity.eventUser.contains(eventUser) : null;
    }
}
