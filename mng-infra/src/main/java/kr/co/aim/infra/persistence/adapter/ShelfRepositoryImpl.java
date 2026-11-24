package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Shelf;
import kr.co.aim.domain.repository.ShelfRepository;
import kr.co.aim.domain.repository.condition.ShelfSearchCondition;
import kr.co.aim.infra.persistence.entity.ShelfEntity;
import kr.co.aim.infra.persistence.mapper.ShelfMapper;
import kr.co.aim.infra.persistence.springdatajpa.ShelfJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QShelfEntity.shelfEntity;

/**
 * ShelfRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 ShelfJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class ShelfRepositoryImpl implements ShelfRepository {

    private final ShelfJpaRepository shelfJpaRepository;
    private final ShelfMapper shelfMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Shelf> findAll() {
        List<ShelfEntity> entities = shelfJpaRepository.findAll();
        List<Shelf> result = new ArrayList<>();
        for (ShelfEntity entity : entities) {
            result.add(shelfMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Shelf> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<ShelfEntity> entityOptional = shelfJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(shelfMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Shelf> findByLocationId(String locationId) {
        if (!StringUtils.hasText(locationId)) {
            return Optional.empty();
        }
        Optional<ShelfEntity> entityOptional = shelfJpaRepository.findByLocationId(locationId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(shelfMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Shelf save(Shelf shelf) {
        ShelfEntity entity = shelfMapper.toEntity(shelf);
        ShelfEntity savedEntity = shelfJpaRepository.save(entity);
        return shelfMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        shelfJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        shelfJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Shelf> findShelfsWithConditions(ShelfSearchCondition condition, Pageable pageable) {
        JPAQuery<ShelfEntity> query = queryFactory
                .select(shelfEntity)
                .from(shelfEntity)
                .where(
                        locationIdContains(condition.getLocationId()),
                        equipmentNameContains(condition.getEquipmentName()),
                        zoneIdEq(condition.getZoneId()),
                        areaIdEq(condition.getAreaId()),
                        capacityEq(condition.getCapacity()),
                        shelfStateContains(condition.getShelfState()),
                        communicationStateContains(condition.getCommunicationState()),
                        holdStateContains(condition.getHoldState()),
                        lengthEq(condition.getLength()),
                        widthEq(condition.getWidth()),
                        heightEq(condition.getHeight()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<ShelfEntity> content = query.fetch();
        List<Shelf> converted = new ArrayList<>();
        for (ShelfEntity entity : content) {
            if (entity != null) {
                Shelf domain = shelfMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(shelfEntity.count())
                    .from(shelfEntity)
                    .where(
                            locationIdContains(condition.getLocationId()),
                            equipmentNameContains(condition.getEquipmentName()),
                            zoneIdEq(condition.getZoneId()),
                            areaIdEq(condition.getAreaId()),
                            capacityEq(condition.getCapacity()),
                            shelfStateContains(condition.getShelfState()),
                            communicationStateContains(condition.getCommunicationState()),
                            holdStateContains(condition.getHoldState()),
                            lengthEq(condition.getLength()),
                            widthEq(condition.getWidth()),
                            heightEq(condition.getHeight()),
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
                PathBuilder pathBuilder = new PathBuilder<>(shelfEntity.getType(), shelfEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, shelfEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression locationIdContains(String locationId) {
        return StringUtils.hasText(locationId) ? shelfEntity.locationId.contains(locationId) : null;
    }

    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? shelfEntity.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression zoneIdEq(Long zoneId) {
        return zoneId != null ? shelfEntity.zoneId.eq(zoneId) : null;
    }

    private BooleanExpression areaIdEq(Long areaId) {
        return areaId != null ? shelfEntity.areaId.eq(areaId) : null;
    }

    private BooleanExpression capacityEq(Integer capacity) {
        return capacity != null ? shelfEntity.capacity.eq(capacity) : null;
    }

    private BooleanExpression shelfStateContains(String shelfState) {
        return StringUtils.hasText(shelfState) ? shelfEntity.shelfState.contains(shelfState) : null;
    }

    private BooleanExpression communicationStateContains(String communicationState) {
        return StringUtils.hasText(communicationState) ? shelfEntity.communicationState.contains(communicationState) : null;
    }

    private BooleanExpression holdStateContains(String holdState) {
        return StringUtils.hasText(holdState) ? shelfEntity.holdState.contains(holdState) : null;
    }

    private BooleanExpression lengthEq(Integer length) {
        return length != null ? shelfEntity.length.eq(length) : null;
    }

    private BooleanExpression widthEq(Integer width) {
        return width != null ? shelfEntity.width.eq(width) : null;
    }

    private BooleanExpression heightEq(Integer height) {
        return height != null ? shelfEntity.height.eq(height) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? shelfEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? shelfEntity.eventUser.contains(eventUser) : null;
    }
}
