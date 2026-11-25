package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.MaterialLot;
import kr.co.aim.domain.repository.MaterialLotRepository;
import kr.co.aim.domain.repository.condition.MaterialLotSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialLotEntity;
import kr.co.aim.infra.persistence.mapper.MaterialLotMapper;
import kr.co.aim.infra.persistence.springdatajpa.MaterialLotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kr.co.aim.infra.persistence.entity.QMaterialLotEntity.materialLotEntity;

/**
 * MaterialLotRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 MaterialLotJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class MaterialLotRepositoryImpl implements MaterialLotRepository {

    private final MaterialLotJpaRepository materialLotJpaRepository;
    private final MaterialLotMapper materialLotMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MaterialLot> findAll() {
        List<MaterialLotEntity> entities = materialLotJpaRepository.findAll();
        List<MaterialLot> result = new ArrayList<>();
        for (MaterialLotEntity entity : entities) {
            result.add(materialLotMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<MaterialLot> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<MaterialLotEntity> entityOptional = materialLotJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialLotMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<MaterialLot> findByMaterialLotId(String materialLotId) {
        if (!StringUtils.hasText(materialLotId)) {
            return Optional.empty();
        }
        Optional<MaterialLotEntity> entityOptional = materialLotJpaRepository.findByMaterialLotId(materialLotId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialLotMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public MaterialLot save(MaterialLot materialLot) {
        MaterialLotEntity entity = materialLotMapper.toEntity(materialLot);
        MaterialLotEntity savedEntity = materialLotJpaRepository.save(entity);
        return materialLotMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        materialLotJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        materialLotJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<MaterialLot> findMaterialLotsWithConditions(MaterialLotSearchCondition condition, Pageable pageable) {
        JPAQuery<MaterialLotEntity> query = queryFactory
                .select(materialLotEntity)
                .from(materialLotEntity)
                .where(
                        materialLotIdContains(condition.getMaterialLotId()),
                        materialDefIdEq(condition.getMaterialDefId()),
                        parentMaterialLotIdEq(condition.getParentMaterialLotId()),
                        carrierNameContains(condition.getCarrierName()),
                        quantityEq(condition.getQuantity()),
                        stateContains(condition.getState()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<MaterialLotEntity> content = query.fetch();
        List<MaterialLot> converted = new ArrayList<>();
        for (MaterialLotEntity entity : content) {
            if (entity != null) {
                MaterialLot domain = materialLotMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(materialLotEntity.count())
                    .from(materialLotEntity)
                    .where(
                            materialLotIdContains(condition.getMaterialLotId()),
                            materialDefIdEq(condition.getMaterialDefId()),
                            parentMaterialLotIdEq(condition.getParentMaterialLotId()),
                            carrierNameContains(condition.getCarrierName()),
                            quantityEq(condition.getQuantity()),
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
                PathBuilder pathBuilder = new PathBuilder<>(materialLotEntity.getType(), materialLotEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, materialLotEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression materialLotIdContains(String materialLotId) {
        return StringUtils.hasText(materialLotId) ? materialLotEntity.materialLotId.contains(materialLotId) : null;
    }

    private BooleanExpression materialDefIdEq(Long materialDefId) {
        return materialDefId != null ? materialLotEntity.materialDefId.eq(materialDefId) : null;
    }

    private BooleanExpression parentMaterialLotIdEq(Long parentMaterialLotId) {
        return parentMaterialLotId != null ? materialLotEntity.parentMaterialLotId.eq(parentMaterialLotId) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? materialLotEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression quantityEq(BigDecimal quantity) {
        return quantity != null ? materialLotEntity.quantity.eq(quantity) : null;
    }

    private BooleanExpression stateContains(String state) {
        return StringUtils.hasText(state) ? materialLotEntity.state.contains(state) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? materialLotEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? materialLotEntity.eventUser.contains(eventUser) : null;
    }
}
