package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.InsertAutoRelocationTest;
import kr.co.aim.domain.repository.InsertAutoRelocationTestRepository;
import kr.co.aim.domain.repository.condition.InsertAutoRelocationTestSearchCondition;
import kr.co.aim.infra.persistence.entity.InsertAutoRelocationTestEntity;
import kr.co.aim.infra.persistence.mapper.InsertAutoRelocationTestMapper;
import kr.co.aim.infra.persistence.springdatajpa.InsertAutoRelocationTestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kr.co.aim.infra.persistence.entity.QInsertAutoRelocationTestEntity.insertAutoRelocationTestEntity;

/**
 * InsertAutoRelocationTestRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 InsertAutoRelocationTestJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class InsertAutoRelocationTestRepositoryImpl implements InsertAutoRelocationTestRepository {

    private final InsertAutoRelocationTestJpaRepository insertAutoRelocationTestJpaRepository;
    private final InsertAutoRelocationTestMapper insertAutoRelocationTestMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<InsertAutoRelocationTest> findAll() {
        List<InsertAutoRelocationTestEntity> entities = insertAutoRelocationTestJpaRepository.findAll();
        List<InsertAutoRelocationTest> result = new ArrayList<>();
        for (InsertAutoRelocationTestEntity entity : entities) {
            result.add(insertAutoRelocationTestMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<InsertAutoRelocationTest> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<InsertAutoRelocationTestEntity> entityOptional = insertAutoRelocationTestJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(insertAutoRelocationTestMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<InsertAutoRelocationTest> findByCarrierName(String carrierName) {
        List<InsertAutoRelocationTestEntity> entities = insertAutoRelocationTestJpaRepository.findByCarrierName(carrierName);
        List<InsertAutoRelocationTest> result = new ArrayList<>();
        for (InsertAutoRelocationTestEntity entity : entities) {
            result.add(insertAutoRelocationTestMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public InsertAutoRelocationTest save(InsertAutoRelocationTest insertAutoRelocationTest) {
        InsertAutoRelocationTestEntity entity = insertAutoRelocationTestMapper.toEntity(insertAutoRelocationTest);
        InsertAutoRelocationTestEntity savedEntity = insertAutoRelocationTestJpaRepository.save(entity);
        return insertAutoRelocationTestMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        insertAutoRelocationTestJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        insertAutoRelocationTestJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<InsertAutoRelocationTest> findAutoRelocationTestsWithConditions(InsertAutoRelocationTestSearchCondition condition, Pageable pageable) {
        JPAQuery<InsertAutoRelocationTestEntity> query = queryFactory
                .select(insertAutoRelocationTestEntity)
                .from(insertAutoRelocationTestEntity)
                .where(
                        wareHouseName1Contains(condition.getWareHouseName1()),
                        wareHouseName2Contains(condition.getWareHouseName2()),
                        zoneName1Contains(condition.getZoneName1()),
                        zoneName2Contains(condition.getZoneName2()),
                        carrierNameContains(condition.getCarrierName()),
                        stateContains(condition.getState()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<InsertAutoRelocationTestEntity> content = query.fetch();
        List<InsertAutoRelocationTest> converted = new ArrayList<>();
        for (InsertAutoRelocationTestEntity entity : content) {
            if (entity != null) {
                InsertAutoRelocationTest domain = insertAutoRelocationTestMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(insertAutoRelocationTestEntity.count())
                    .from(insertAutoRelocationTestEntity)
                    .where(
                            wareHouseName1Contains(condition.getWareHouseName1()),
                            wareHouseName2Contains(condition.getWareHouseName2()),
                            zoneName1Contains(condition.getZoneName1()),
                            zoneName2Contains(condition.getZoneName2()),
                            carrierNameContains(condition.getCarrierName()),
                            stateContains(condition.getState()),
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
                PathBuilder pathBuilder = new PathBuilder<>(insertAutoRelocationTestEntity.getType(), insertAutoRelocationTestEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, insertAutoRelocationTestEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression wareHouseName1Contains(String wareHouseName1) {
        return StringUtils.hasText(wareHouseName1) ? insertAutoRelocationTestEntity.wareHouseName1.contains(wareHouseName1) : null;
    }

    private BooleanExpression wareHouseName2Contains(String wareHouseName2) {
        return StringUtils.hasText(wareHouseName2) ? insertAutoRelocationTestEntity.wareHouseName2.contains(wareHouseName2) : null;
    }

    private BooleanExpression zoneName1Contains(String zoneName1) {
        return StringUtils.hasText(zoneName1) ? insertAutoRelocationTestEntity.zoneName1.contains(zoneName1) : null;
    }

    private BooleanExpression zoneName2Contains(String zoneName2) {
        return StringUtils.hasText(zoneName2) ? insertAutoRelocationTestEntity.zoneName2.contains(zoneName2) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? insertAutoRelocationTestEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression stateContains(String state) {
        return StringUtils.hasText(state) ? insertAutoRelocationTestEntity.state.contains(state) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? insertAutoRelocationTestEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? insertAutoRelocationTestEntity.eventUser.contains(eventUser) : null;
    }
}
