package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.MaterialClassDef;
import kr.co.aim.domain.repository.MaterialClassDefRepository;
import kr.co.aim.domain.repository.condition.MaterialClassDefSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialClassDefEntity;
import kr.co.aim.infra.persistence.mapper.MaterialClassDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.MaterialClassDefJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QMaterialClassDefEntity.materialClassDefEntity;

/**
 * MaterialClassDefRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 MaterialClassDefJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class MaterialClassDefRepositoryImpl implements MaterialClassDefRepository {

    private final MaterialClassDefJpaRepository materialClassDefJpaRepository;
    private final MaterialClassDefMapper materialClassDefMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MaterialClassDef> findAll() {
        List<MaterialClassDefEntity> entities = materialClassDefJpaRepository.findAll();
        List<MaterialClassDef> result = new ArrayList<>();
        for (MaterialClassDefEntity entity : entities) {
            result.add(materialClassDefMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<MaterialClassDef> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<MaterialClassDefEntity> entityOptional = materialClassDefJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialClassDefMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<MaterialClassDef> findByMaterialClassDefName(String materialClassDefName) {
        if (!StringUtils.hasText(materialClassDefName)) {
            return Optional.empty();
        }
        Optional<MaterialClassDefEntity> entityOptional = materialClassDefJpaRepository.findByMaterialClassDefName(materialClassDefName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialClassDefMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<MaterialClassDef> findByParentClassId(Long parentClassId) {
        List<MaterialClassDefEntity> entities = materialClassDefJpaRepository.findByParentClassId(parentClassId);
        List<MaterialClassDef> result = new ArrayList<>();
        for (MaterialClassDefEntity entity : entities) {
            result.add(materialClassDefMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public MaterialClassDef save(MaterialClassDef materialClassDef) {
        MaterialClassDefEntity entity = materialClassDefMapper.toEntity(materialClassDef);
        MaterialClassDefEntity savedEntity = materialClassDefJpaRepository.save(entity);
        return materialClassDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        materialClassDefJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        materialClassDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<MaterialClassDef> findMaterialClassDefsWithConditions(MaterialClassDefSearchCondition condition, Pageable pageable) {
        JPAQuery<MaterialClassDefEntity> query = queryFactory
                .select(materialClassDefEntity)
                .from(materialClassDefEntity)
                .where(
                        materialClassDefNameContains(condition.getMaterialClassDefName()),
                        materialClassTypeContains(condition.getMaterialClassType()),
                        parentClassIdEq(condition.getParentClassId()),
                        classLevelEq(condition.getClassLevel()),
                        descriptionContains(condition.getDescription()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<MaterialClassDefEntity> content = query.fetch();
        List<MaterialClassDef> converted = new ArrayList<>();
        for (MaterialClassDefEntity entity : content) {
            if (entity != null) {
                MaterialClassDef domain = materialClassDefMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(materialClassDefEntity.count())
                    .from(materialClassDefEntity)
                    .where(
                            materialClassDefNameContains(condition.getMaterialClassDefName()),
                            materialClassTypeContains(condition.getMaterialClassType()),
                            parentClassIdEq(condition.getParentClassId()),
                            classLevelEq(condition.getClassLevel()),
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
                PathBuilder pathBuilder = new PathBuilder<>(materialClassDefEntity.getType(), materialClassDefEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, materialClassDefEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression materialClassDefNameContains(String materialClassDefName) {
        return StringUtils.hasText(materialClassDefName) ? materialClassDefEntity.materialClassDefName.contains(materialClassDefName) : null;
    }

    private BooleanExpression materialClassTypeContains(String materialClassType) {
        return StringUtils.hasText(materialClassType) ? materialClassDefEntity.materialClassType.contains(materialClassType) : null;
    }

    private BooleanExpression parentClassIdEq(Long parentClassId) {
        return parentClassId != null ? materialClassDefEntity.parentClassId.eq(parentClassId) : null;
    }

    private BooleanExpression classLevelEq(Integer classLevel) {
        return classLevel != null ? materialClassDefEntity.classLevel.eq(classLevel) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? materialClassDefEntity.description.contains(description) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? materialClassDefEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? materialClassDefEntity.eventUser.contains(eventUser) : null;
    }
}
