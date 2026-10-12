package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.InsertAutoTestSearchCondition;
import kr.co.aim.domain.model.InsertAutoTest;
import kr.co.aim.domain.repository.InsertAutoTestRepository;
import kr.co.aim.infra.persistence.entity.InsertAutoTestEntity;
import kr.co.aim.infra.persistence.mapper.InsertAutoTestMapper;
import kr.co.aim.infra.persistence.springdatajpa.InsertAutoTestJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QInsertAutoTestEntity.insertAutoTestEntity;

/**
 * InsertAutoTestRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 InsertAutoTestJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class InsertAutoTestRepositoryImpl implements InsertAutoTestRepository {

    private final InsertAutoTestJpaRepository insertAutoTestJpaRepository;
    private final InsertAutoTestMapper insertAutoTestMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<InsertAutoTest> findAll() {
        List<InsertAutoTestEntity> entities = insertAutoTestJpaRepository.findAll();
        List<InsertAutoTest> result = new ArrayList<>();
        for (InsertAutoTestEntity entity : entities) {
            result.add(insertAutoTestMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<InsertAutoTest> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<InsertAutoTestEntity> entityOptional = insertAutoTestJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(insertAutoTestMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<InsertAutoTest> findByWorkStationId(String workStationId) {
        List<InsertAutoTestEntity> entities = insertAutoTestJpaRepository.findByWorkStationId(workStationId);
        List<InsertAutoTest> result = new ArrayList<>();
        for (InsertAutoTestEntity entity : entities) {
            result.add(insertAutoTestMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public List<InsertAutoTest> findByCarrierName(String carrierName) {
        List<InsertAutoTestEntity> entities = insertAutoTestJpaRepository.findByCarrierName(carrierName);
        List<InsertAutoTest> result = new ArrayList<>();
        for (InsertAutoTestEntity entity : entities) {
            result.add(insertAutoTestMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public InsertAutoTest save(InsertAutoTest insertAutoTest) {
        InsertAutoTestEntity entity = insertAutoTestMapper.toEntity(insertAutoTest);
        InsertAutoTestEntity savedEntity = insertAutoTestJpaRepository.save(entity);
        return insertAutoTestMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        insertAutoTestJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        insertAutoTestJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<InsertAutoTest> findAutoTestsWithConditions(InsertAutoTestSearchCondition condition, Pageable pageable) {
        JPAQuery<InsertAutoTestEntity> query = queryFactory
                .select(insertAutoTestEntity)
                .from(insertAutoTestEntity)
                .where(
                        workStationIdContains(condition.getWorkStationId()),
                        carrierNameContains(condition.getCarrierName()),
                        zoneNameContains(condition.getZoneName()),
                        travelProfileContains(condition.getTravelProfile()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<InsertAutoTestEntity> content = query.fetch();
        List<InsertAutoTest> converted = new ArrayList<>();
        for (InsertAutoTestEntity entity : content) {
            if (entity != null) {
                InsertAutoTest domain = insertAutoTestMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(insertAutoTestEntity.count())
                    .from(insertAutoTestEntity)
                    .where(
                            workStationIdContains(condition.getWorkStationId()),
                            carrierNameContains(condition.getCarrierName()),
                            zoneNameContains(condition.getZoneName()),
                            travelProfileContains(condition.getTravelProfile()),
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
                PathBuilder pathBuilder = new PathBuilder<>(insertAutoTestEntity.getType(), insertAutoTestEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, insertAutoTestEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression workStationIdContains(String workStationId) {
        return StringUtils.hasText(workStationId) ? insertAutoTestEntity.workStationId.contains(workStationId) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? insertAutoTestEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? insertAutoTestEntity.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression travelProfileContains(String travelProfile) {
        return StringUtils.hasText(travelProfile) ? insertAutoTestEntity.travelProfile.contains(travelProfile) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? insertAutoTestEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? insertAutoTestEntity.eventUser.contains(eventUser) : null;
    }
}
