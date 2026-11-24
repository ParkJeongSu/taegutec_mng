package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Zone;
import kr.co.aim.domain.repository.ZoneRepository;
import kr.co.aim.domain.repository.condition.ZoneSearchCondition;
import kr.co.aim.infra.persistence.entity.ZoneEntity;
import kr.co.aim.infra.persistence.mapper.ZoneMapper;
import kr.co.aim.infra.persistence.springdatajpa.ZoneJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QZoneEntity.zoneEntity;

/**
 * ZoneRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 ZoneJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class ZoneRepositoryImpl implements ZoneRepository {

    private final ZoneJpaRepository zoneJpaRepository;
    private final ZoneMapper zoneMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Zone> findAll() {
        List<ZoneEntity> entities = zoneJpaRepository.findAll();
        List<Zone> result = new ArrayList<>();
        for (ZoneEntity entity : entities) {
            result.add(zoneMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Zone> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<ZoneEntity> entityOptional = zoneJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(zoneMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Zone> findByZoneName(String zoneName) {
        if (!StringUtils.hasText(zoneName)) {
            return Optional.empty();
        }
        Optional<ZoneEntity> entityOptional = zoneJpaRepository.findByZoneName(zoneName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(zoneMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Zone save(Zone zone) {
        ZoneEntity entity = zoneMapper.toEntity(zone);
        ZoneEntity savedEntity = zoneJpaRepository.save(entity);
        return zoneMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        zoneJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        zoneJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Zone> findZonesWithConditions(ZoneSearchCondition condition, Pageable pageable) {
        JPAQuery<ZoneEntity> query = queryFactory
                .select(zoneEntity)
                .from(zoneEntity)
                .where(
                        zoneNameContains(condition.getZoneName()),
                        zoneTypeContains(condition.getZoneType()),
                        descriptionContains(condition.getDescription()),
                        zoneColorContains(condition.getZoneColor()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<ZoneEntity> content = query.fetch();
        List<Zone> converted = new ArrayList<>();
        for (ZoneEntity entity : content) {
            if (entity != null) {
                Zone domain = zoneMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(zoneEntity.count())
                    .from(zoneEntity)
                    .where(
                            zoneNameContains(condition.getZoneName()),
                            zoneTypeContains(condition.getZoneType()),
                            descriptionContains(condition.getDescription()),
                            zoneColorContains(condition.getZoneColor()),
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
                PathBuilder pathBuilder = new PathBuilder<>(zoneEntity.getType(), zoneEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, zoneEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? zoneEntity.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression zoneTypeContains(String zoneType) {
        return StringUtils.hasText(zoneType) ? zoneEntity.zoneType.contains(zoneType) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? zoneEntity.description.contains(description) : null;
    }

    private BooleanExpression zoneColorContains(String zoneColor) {
        return StringUtils.hasText(zoneColor) ? zoneEntity.zoneColor.contains(zoneColor) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? zoneEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? zoneEntity.eventUser.contains(eventUser) : null;
    }
}
