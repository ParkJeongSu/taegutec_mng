package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.MaterialDef;
import kr.co.aim.domain.repository.MaterialDefRepository;
import kr.co.aim.domain.repository.condition.MaterialDefSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialDefEntity;
import kr.co.aim.infra.persistence.mapper.MaterialDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.MaterialDefJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QMaterialDefEntity.materialDefEntity;

/**
 * MaterialDefRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 MaterialDefJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class MaterialDefRepositoryImpl implements MaterialDefRepository {

    private final MaterialDefJpaRepository materialDefJpaRepository;
    private final MaterialDefMapper materialDefMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MaterialDef> findAll() {
        List<MaterialDefEntity> entities = materialDefJpaRepository.findAll();
        List<MaterialDef> result = new ArrayList<>();
        for (MaterialDefEntity entity : entities) {
            result.add(materialDefMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<MaterialDef> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<MaterialDefEntity> entityOptional = materialDefJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialDefMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<MaterialDef> findByMaterialDefName(String materialDefName) {
        if (!StringUtils.hasText(materialDefName)) {
            return Optional.empty();
        }
        Optional<MaterialDefEntity> entityOptional = materialDefJpaRepository.findByMaterialDefName(materialDefName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialDefMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public MaterialDef save(MaterialDef materialDef) {
        MaterialDefEntity entity = materialDefMapper.toEntity(materialDef);
        MaterialDefEntity savedEntity = materialDefJpaRepository.save(entity);
        return materialDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        materialDefJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        materialDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<MaterialDef> findMaterialDefsWithConditions(MaterialDefSearchCondition condition, Pageable pageable) {
        JPAQuery<MaterialDefEntity> query = queryFactory
                .select(materialDefEntity)
                .from(materialDefEntity)
                .where(
                        materialDefNameContains(condition.getMaterialDefName()),
                        descriptionContains(condition.getDescription()),
                        materialClassIdEq(condition.getMaterialClassId()),
                        materialGroupIdEq(condition.getMaterialGroupId()),
                        unitContains(condition.getUnit()),
                        isSerialTrackedContains(condition.getIsSerialTracked()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<MaterialDefEntity> content = query.fetch();
        List<MaterialDef> converted = new ArrayList<>();
        for (MaterialDefEntity entity : content) {
            if (entity != null) {
                MaterialDef domain = materialDefMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(materialDefEntity.count())
                    .from(materialDefEntity)
                    .where(
                            materialDefNameContains(condition.getMaterialDefName()),
                            descriptionContains(condition.getDescription()),
                            materialClassIdEq(condition.getMaterialClassId()),
                            materialGroupIdEq(condition.getMaterialGroupId()),
                            unitContains(condition.getUnit()),
                            isSerialTrackedContains(condition.getIsSerialTracked()),
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
                PathBuilder pathBuilder = new PathBuilder<>(materialDefEntity.getType(), materialDefEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, materialDefEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression materialDefNameContains(String materialDefName) {
        return StringUtils.hasText(materialDefName) ? materialDefEntity.materialDefName.contains(materialDefName) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? materialDefEntity.description.contains(description) : null;
    }

    private BooleanExpression materialClassIdEq(Long materialClassId) {
        return materialClassId != null ? materialDefEntity.materialClassId.eq(materialClassId) : null;
    }

    private BooleanExpression materialGroupIdEq(Long materialGroupId) {
        return materialGroupId != null ? materialDefEntity.materialGroupId.eq(materialGroupId) : null;
    }

    private BooleanExpression unitContains(String unit) {
        return StringUtils.hasText(unit) ? materialDefEntity.unit.contains(unit) : null;
    }

    private BooleanExpression isSerialTrackedContains(String isSerialTracked) {
        return StringUtils.hasText(isSerialTracked) ? materialDefEntity.isSerialTracked.contains(isSerialTracked) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? materialDefEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? materialDefEntity.eventUser.contains(eventUser) : null;
    }
}
