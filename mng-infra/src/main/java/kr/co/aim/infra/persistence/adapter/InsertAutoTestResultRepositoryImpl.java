package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.InsertAutoTestResultSearchCondition;
import kr.co.aim.domain.model.InsertAutoTestResult;
import kr.co.aim.domain.repository.InsertAutoTestResultRepository;
import kr.co.aim.infra.persistence.entity.InsertAutoTestResultEntity;
import kr.co.aim.infra.persistence.mapper.InsertAutoTestResultMapper;
import kr.co.aim.infra.persistence.springdatajpa.InsertAutoTestResultJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QInsertAutoTestResultEntity.insertAutoTestResultEntity;

/**
 * InsertAutoTestResultRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 InsertAutoTestResultJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class InsertAutoTestResultRepositoryImpl implements InsertAutoTestResultRepository {

    private final InsertAutoTestResultJpaRepository insertAutoTestResultJpaRepository;
    private final InsertAutoTestResultMapper insertAutoTestResultMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<InsertAutoTestResult> findAll() {
        List<InsertAutoTestResultEntity> entities = insertAutoTestResultJpaRepository.findAll();
        List<InsertAutoTestResult> result = new ArrayList<>();
        for (InsertAutoTestResultEntity entity : entities) {
            result.add(insertAutoTestResultMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<InsertAutoTestResult> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<InsertAutoTestResultEntity> entityOptional = insertAutoTestResultJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(insertAutoTestResultMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<InsertAutoTestResult> findByAutoTestId(Long autoTestId) {
        List<InsertAutoTestResultEntity> entities = insertAutoTestResultJpaRepository.findByAutoTestId(autoTestId);
        List<InsertAutoTestResult> result = new ArrayList<>();
        for (InsertAutoTestResultEntity entity : entities) {
            result.add(insertAutoTestResultMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public InsertAutoTestResult save(InsertAutoTestResult insertAutoTestResult) {
        InsertAutoTestResultEntity entity = insertAutoTestResultMapper.toEntity(insertAutoTestResult);
        InsertAutoTestResultEntity savedEntity = insertAutoTestResultJpaRepository.save(entity);
        return insertAutoTestResultMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        insertAutoTestResultJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        insertAutoTestResultJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteByAutoTestId(Long autoTestId) {
        insertAutoTestResultJpaRepository.deleteByAutoTestId(autoTestId);
    }

    @Override
    public Page<InsertAutoTestResult> findAutoTestResultsWithConditions(InsertAutoTestResultSearchCondition condition, Pageable pageable) {
        JPAQuery<InsertAutoTestResultEntity> query = queryFactory
                .select(insertAutoTestResultEntity)
                .from(insertAutoTestResultEntity)
                .where(
                        autoTestIdEq(condition.getAutoTestId()),
                        stateContains(condition.getState()),
                        resultContains(condition.getResult()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<InsertAutoTestResultEntity> content = query.fetch();
        List<InsertAutoTestResult> converted = new ArrayList<>();
        for (InsertAutoTestResultEntity entity : content) {
            if (entity != null) {
                InsertAutoTestResult domain = insertAutoTestResultMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(insertAutoTestResultEntity.count())
                    .from(insertAutoTestResultEntity)
                    .where(
                            autoTestIdEq(condition.getAutoTestId()),
                            stateContains(condition.getState()),
                            resultContains(condition.getResult()),
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
                PathBuilder pathBuilder = new PathBuilder<>(insertAutoTestResultEntity.getType(), insertAutoTestResultEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, insertAutoTestResultEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression autoTestIdEq(Long autoTestId) {
        return autoTestId != null ? insertAutoTestResultEntity.autoTestId.eq(autoTestId) : null;
    }

    private BooleanExpression stateContains(String state) {
        return StringUtils.hasText(state) ? insertAutoTestResultEntity.state.contains(state) : null;
    }

    private BooleanExpression resultContains(String result) {
        return StringUtils.hasText(result) ? insertAutoTestResultEntity.result.contains(result) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? insertAutoTestResultEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? insertAutoTestResultEntity.eventUser.contains(eventUser) : null;
    }
}
